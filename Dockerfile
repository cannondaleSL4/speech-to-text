# Start with the official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set environment for non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive

# Update and install required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends make git wget cmake g++ zlib1g-dev automake autoconf bzip2 unzip sox gfortran libtool subversion python2.7 python3 python3-pip patch && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    ln -s /usr/bin/python2.7 /usr/bin/python

# Clone Kaldi for Vosk
RUN git clone -b vosk --single-branch --depth=1 https://github.com/alphacep/kaldi /opt/kaldi

# Build tools and dependencies for Kaldi
WORKDIR /opt/kaldi/tools

# Set compiler flags
ENV CXXFLAGS="-O2"

# Build OpenFST and handle its failure gracefully
RUN make openfst || true && \
    rm -f openfst-*.tar.gz && \
    ls -la

# Configure and build OpenFST
RUN cd openfst-* && \
    autoreconf -i && \
    ./configure --prefix=`pwd` --enable-static --enable-shared --enable-far --enable-ngram-fsts --enable-lookahead-fsts --with-pic CXX="g++" \
    CXXFLAGS="-O2" \
    LDFLAGS="" LIBS="-ldl" && \
    make && \
    make install && \
    cd ..

# Build additional tools and dependencies
RUN make cub
RUN ./extras/install_openblas_clapack.sh

# Configure and compile Kaldi with OpenBLAS
WORKDIR /opt/kaldi/src
RUN ./configure --mathlib=OPENBLAS_CLAPACK --shared --fst-root=/opt/kaldi/tools/openfst-1.8.0 && make -j$(nproc) online2 lm rnnlm

# Clone vosk-api
WORKDIR /opt
RUN git clone https://github.com/alphacep/vosk-api --depth=1

# Build Vosk API
WORKDIR /opt/vosk-api/src
ENV CPLUS_INCLUDE_PATH=/opt/kaldi/tools/openfst-1.8.0/include
RUN ln -s /opt/kaldi/tools/openfst-1.8.0 /opt/kaldi/tools/openfst

RUN KALDI_ROOT=/opt/kaldi make

# Expose port for your application
EXPOSE 8080

# Set the JAR file path
ARG JAR_FILE=target/*.jar

# Copy JAR file into the image
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

## Uncomment the following lines if you're going to use this image to run a Java application
ENTRYPOINT ["java","-jar","/app.jar"]
