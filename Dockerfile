# Start with the official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set environment for non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive

# Update and install required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends make git wget cmake g++ zlib1g-dev automake autoconf bzip2 unzip sox gfortran libtool subversion python2.7 python3 python3-pip patch && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    ln -s /usr/bin/python2.7 /usr/bin/python && \
    git clone -b vosk --single-branch --depth=1 https://github.com/alphacep/kaldi /opt/kaldi && \
    cd /opt/kaldi/tools && \
    make openfst || true && \
    rm -f openfst-*.tar.gz && \
    cd openfst-* && \
    autoreconf -i && \
    ./configure --prefix=`pwd` --enable-static --enable-shared --enable-far --enable-ngram-fsts --enable-lookahead-fsts --with-pic CXX="g++" CXXFLAGS="-O2" LDFLAGS="" LIBS="-ldl" && \
    make && \
    make install && \
    cd .. && \
    make cub && \
    ./extras/install_openblas_clapack.sh && \
    cd /opt/kaldi/src && \
    ./configure --mathlib=OPENBLAS_CLAPACK --shared --fst-root=/opt/kaldi/tools/openfst-1.8.0 && \
    make -j$(nproc) online2 lm rnnlm && \
    git clone https://github.com/alphacep/vosk-api /opt/vosk-api --depth=1 && \
    cd /opt/vosk-api/src && \
    ln -s /opt/kaldi/tools/openfst-1.8.0 /opt/kaldi/tools/openfst && \
    KALDI_ROOT=/opt/kaldi make

ENV LD_LIBRARY_PATH /opt/vosk-api/src/:$LD_LIBRARY_PATH

# Expose port for your application
EXPOSE 8080

# Set the working directory
WORKDIR /app

# Set the JAR file path
ARG JAR_FILE=target/*.jar

# Copy JAR file into the image
COPY ${JAR_FILE} /app/app.jar

ENV JAVA_OPTS=""

# Set the ENTRYPOINT
ENTRYPOINT ["java","-jar","/app/app.jar"]
