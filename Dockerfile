# Start from the official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set environment for non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive

# Update and install required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends make git wget automake g++ zlib1g-dev automake autoconf bzip2 unzip sox gfortran libtool subversion python2.7 python3 python3-pip patch && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    ln -s /usr/bin/python2.7 /usr/bin/python

# Clone Kaldi
RUN git clone https://github.com/kaldi-asr/kaldi.git /opt/kaldi --origin upstream

# Download and compile OpenFST 1.8.2
RUN wget http://www.openfst.org/twiki/pub/FST/FstDownload/openfst-1.8.2.tar.gz && \
    tar xvf openfst-1.8.2.tar.gz && \
    cd openfst-1.8.2/ && \
    ./configure --prefix=/opt/kaldi/tools/openfst --enable-shared --enable-static && \
    make && \
    make install


## Manually download and extract OpenBLAS
WORKDIR /opt/kaldi/tools/extras

# Download OpenBLAS
RUN wget -O OpenBLAS-0.3.20.tar.gz https://codeload.github.com/OpenMathLib/OpenBLAS/legacy.tar.gz/refs/tags/v0.3.20

# Extract the archive
RUN tar xzf OpenBLAS-0.3.20.tar.gz && mv OpenMathLib-OpenBLAS-* OpenBLAS

# Build and Install OpenBLAS
RUN cd OpenBLAS \
    && make PREFIX=$(pwd)/install USE_LOCKING=1 USE_THREAD=0 all \
    && make PREFIX=$(pwd)/install install

# Check Kaldi dependencies
WORKDIR /opt/kaldi/tools

# Delete the openfst directory before compiling Kaldi tools
RUN rm -rf openfst/

RUN extras/check_dependencies.sh

# Compile Kaldi tools
RUN make -j$(nproc)

# Set environment variables for compilation
ENV CXXFLAGS="-I/opt/kaldi/tools/openfst/include"
ENV LDFLAGS="-L/opt/kaldi/tools/openfst/lib"
ENV LD_LIBRARY_PATH="/opt/kaldi/tools/openfst/lib:$LD_LIBRARY_PATH"

# Configure Kaldi with updated FST version
WORKDIR /opt/kaldi/src
RUN ./configure --shared --mathlib=OPENBLAS --openblas-root=/opt/kaldi/tools/extras/OpenBLAS/install --fst-root=/opt/kaldi/tools/openfst --fst-version=1.8.2

#Compile Kaldi
RUN make -j clean depend && make -j$(nproc)

# Expose port for your application
EXPOSE 8080

# Set the JAR file path
ARG JAR_FILE=target/*.jar

# Copy JAR file into the image
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

# Uncomment the following lines if you're going to use this image to run a Java application
#ENTRYPOINT ["java","-jar","/app.jar"]