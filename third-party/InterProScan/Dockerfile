FROM debian:bullseye

LABEL base_image="debian:bullseye" \
    version="1" \
    software="InterProScan" \
    software.version="5.69-101.0" \
    about.summary="InterProScan" \
    about.home="https://www.ebi.ac.uk/interpro/download/InterProScan/" \
    about.documentation="https://www.ebi.ac.uk/interpro/download/InterProScan/" \
    about.tags="Proteomics"

RUN apt-get update && apt-get install -y \
    aria2 \
    pigz \
    python3 \
    openjdk-17-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN aria2c --continue=true --max-connection-per-server=8 --min-split-size=2M -d /bioinformatic https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/5.69-101.0/interproscan-5.69-101.0-64-bit.tar.gz
RUN tar --use-compress-program=pigz -xvf /bioinformatic/interproscan-5.69-101.0-64-bit.tar.gz -C /bioinformatic

ENV INTERPRO_HOME="/bioinformatic/interproscan-5.69-101.0"
RUN ln -sf $INTERPRO_HOME/interproscan.sh  /usr/local/bin/interproscan
RUN python3 $INTERPRO_HOME/setup.py -f $INTERPRO_HOME/interproscan.properties
RUN echo "InterProScan installed successfuly at $INTERPRO_HOME"



