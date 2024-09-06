# docker build --no-cache --tag bioinfoufsc/fastprotein:latest .
# docker build --no-cache --build-arg INTERPRO_INSTALL=Y --tag bioinfoufsc/fastprotein-interpro:latest .
FROM debian:bullseye

# build arguments
ARG INTERPRO_INSTALL=N
ARG CACHEBUST=1

LABEL base_image="debian:bullseye" \
    version="1" \
    software="FastProtein" \
    software.version="1.1" \
    about.summary="FastProtein - An automated software for in silico proteomic analysis" \
    about.home="https://github.com/bioinformatics-ufsc/FastProtein/" \
    about.documentation="https://github.com/bioinformatics-ufsc/FastProtein/" \
    about.tags="Proteomics"

# dependencies and clean apt cache
# wolfpsort - libfindbin-libs-perl
RUN apt-get update && apt-get install --no-install-recommends -y \
    apt-utils \
    aria2 \
    diamond-aligner=2.0.7-1 \
    git \
    libfindbin-libs-perl \
    maven \
    nano \
    openjdk-17-jdk \
    pigz \
    procps=2:3.3.17-5 \
    python3 \
    python3-pip \
    unzip \
    wget \
    zip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# python libraries
COPY requirements.txt /tmp/requirements.txt
RUN pip3 install --no-cache-dir -r /tmp/requirements.txt

RUN git clone https://github.com/bioinformatics-ufsc/FastProtein.git

# Criando diretório bioinformatic e copiando arquivos
RUN mkdir -p /bioinformatic/ /FastProtein/temp

# unzip third-party software
RUN unzip -q "/FastProtein/third-party/*.zip" -d /bioinformatic/ \
    && ln -s /bioinformatic/ncbi-blast-2.10.0/bin/blastp /usr/local/bin/blastp \
    && ln -s /bioinformatic/ncbi-blast-2.10.0/bin/makeblastdb /usr/local/bin/makeblastdb \
    && ln -s /bioinformatic/signalp-5.0b/bin/signalp /usr/local/bin/signalp \
    && ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow.so /usr/local/lib/libtensorflow.so \
    && ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow_framework.so /usr/local/lib/libtensorflow_framework.so \
    && ln -s /bioinformatic/predgpi/predgpi.sh /usr/local/bin/predgpi \
    && ln -s /bioinformatic/phobius-1.01/phobius.pl /usr/local/bin/phobius \
    && ln -s /bioinformatic/tmhmm-2.0c/bin/tmhmm /usr/local/bin/tmhmm2 \
    && ln -s /bioinformatic/wolfpsort/bin/wolfpsort.sh /usr/local/bin/wolfpsort \
    && ln -s /FastProtein/bin/interpro_install.sh /usr/local/bin/interpro_install

# set environment variables
ENV PREDGPI_HOME="/bioinformatic/predgpi" \
    FASTPROTEIN_HOME="/FastProtein" \
    DATABASE_HOME="/FastProtein/db" \
    FLASK_RUN_HOST="0.0.0.0" \
    FLASK_DEBUG="False" \
    FLASK_HOME="/FastProtein/web" \
    FLASK_APP="/FastProtein/web/server.py" \
    FLASK_REMOVE_RESULT_DIR="Yes"

# allow script execution
RUN chmod 777 /bioinformatic/phobius-1.01/phobius.pl
RUN chmod +x /FastProtein/bin/*.sh /FastProtein/web/server.sh \
    && ln -s /FastProtein/bin/fastprotein.sh /usr/local/bin/fastprotein \
    && ln -s /FastProtein/web/server.sh /usr/local/bin/server \
    && echo "source server" >> ~/.bashrc

# build fastprotein
RUN mvn -f /FastProtein/pom.xml clean install

# install interproscan
RUN if [ "$INTERPRO_INSTALL" = "Y" ]; then /FastProtein/bin/interpro_install.sh; fi

# required directory for runs
RUN mkdir -p /FastProtein/runs

# expose the application port
EXPOSE 5000

# set volume for fastprotein runs and working directory
VOLUME /FastProtein/runs
WORKDIR /FastProtein

# success message
# RUN echo "The server is available at http://127.0.0.1:5000"

# interproscan
#   Docker image before InterProScan = 1.8GB
#   Docker image after InterProScan = 49GB
# make sure you machine supports this image size!
# docker exec -it FastProtein interpro_install