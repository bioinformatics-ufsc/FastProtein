#docker build -t bioinfoufsc/fastprotein:latest .
#docker build --build-arg INTERPRO_INSTALL=Y -t bioinfoufsc/fastprotein-ipr:latest .
FROM debian:bullseye
ARG INTERPRO_INSTALL=N
###################################
LABEL base_image="debian:bullseye"
LABEL version="1"
LABEL software="FastProtein"
LABEL software.version="1.1"
LABEL about.summary="FastProtein – An automated software for in silico proteomic analysis."
LABEL about.home="https://github.com/bioinformatics-ufsc/FastProtein/"
LABEL about.documentation="https://github.com/bioinformatics-ufsc/FastProtein/"
LABEL about.tags="Proteomics"
###################################

RUN apt-get update
RUN apt-get install -y apt-utils
RUN apt-get install -y git
RUN apt-get install -y unzip
RUN apt-get install -y zip
RUN apt-get install -y nano
RUN apt-get install -y wget
RUN apt-get install -y maven
RUN apt-get install -y procps=2:3.3.17-5
RUN apt-get install -y aria2
RUN apt-get install -y pigz

#Dependency for wolfpsort
RUN apt-get install -y libfindbin-libs-perl

RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN apt-get install -y openjdk-17-jdk

RUN apt-get install -y diamond-aligner=2.0.7-1

#Dependency for predgpi
RUN pip3 install numpy==1.23.1

#Dependencies for interpro online
RUN pip3 install requests==2.32.3
RUN pip3 install xmltramp2==3.1.1

#Dependencies for charts
RUN pip3 install pandas==2.2.2
RUN pip3 install matplotlib==3.9.2
RUN pip3 install seaborn==0.13.2

ARG CACHEBUST=1
RUN git clone https://github.com/bioinformatics-ufsc/FastProtein.git

# Criando diretório bioinformatic e copiando arquivos
RUN mkdir /bioinformatic/
RUN mkdir /FastProtein/temp

# Dezipando arquivos
RUN unzip -q /FastProtein/third-party/predgpi.zip -d /bioinformatic/ \
    && unzip -q /FastProtein/third-party/tmhmm-2.0c.zip -d /bioinformatic/ \
    && unzip -q /FastProtein/third-party/signalp-5.0b.zip -d /bioinformatic/ \
    && unzip -q /FastProtein/third-party/wolfpsort.zip -d /bioinformatic/ \
    && unzip -q /FastProtein/third-party/phobius-1.01.zip -d /bioinformatic/ \
    && unzip -q /FastProtein/third-party/ncbi-blast-2.10.0.zip -d /bioinformatic/

RUN ln -s /bioinformatic/ncbi-blast-2.10.0/bin/blastp /usr/local/bin/blastp
RUN ln -s /bioinformatic/ncbi-blast-2.10.0/bin/makeblastdb /usr/local/bin/makeblastdb
RUN ln -s /bioinformatic/signalp-5.0b/bin/signalp /usr/local/bin/signalp
RUN ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow.so /usr/local/lib/libtensorflow.so
RUN ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow_framework.so /usr/local/lib/libtensorflow_framework.so
RUN ln -s /bioinformatic/predgpi/predgpi.sh /usr/local/bin/predgpi
RUN ln -s /bioinformatic/phobius-1.01/phobius.pl /usr/local/bin/phobius
RUN ln -s /bioinformatic/tmhmm-2.0c/bin/tmhmm /usr/local/bin/tmhmm2
RUN ln -s /bioinformatic/wolfpsort/bin/wolfpsort.sh /usr/local/bin/wolfpsort
RUN ln -s /FastProtein/bin/interpro_install.sh /usr/local/bin/interpro_install

ENV PREDGPI_HOME='/bioinformatic/predgpi'
ENV FASTPROTEIN_HOME='/FastProtein'
ENV DATABASE_HOME=$FASTPROTEIN_HOME""'/db'

# Givin permission
RUN chmod +x /FastProtein/bin/signalp5.sh
RUN chmod +x /FastProtein/bin/predgpi.sh
RUN chmod +x /FastProtein/bin/tmhmm2.sh
RUN chmod +x /FastProtein/bin/wolfpsort.sh
RUN chmod +x /FastProtein/bin/phobius.sh
RUN chmod +x /FastProtein/bin/blastp.sh
RUN chmod +x /FastProtein/bin/diamond.sh
RUN chmod +x /FastProtein/bin/fastprotein.sh
RUN chmod +x /FastProtein/bin/interproscan.sh
RUN chmod +x /FastProtein/bin/interpro_install.sh
RUN chmod +x /FastProtein/web/server.sh

RUN ln -s /FastProtein/bin/fastprotein.sh /usr/local/bin/fastprotein

ARG CACHEBUST=1
RUN mvn -f /FastProtein/pom.xml clean install

#Install interproscan
RUN if [ "$INTERPRO_INSTALL" = "Y" ]; then /FastProtein/bin/interpro_install.sh; fi

#Optional if you want to run a Flask server. Access it via browser at localhost:5000
RUN pip3 install Flask==3.0.3
RUN pip3 install biopython==1.84
RUN pip3 install Werkzeug==3.0.4

RUN mkdir -p /FastProtein/runs

EXPOSE 5000

ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_DEBUG=False
ENV FLASK_HOME='/FastProtein/web'
ENV FLASK_APP=$FLASK_HOME""'/server.py'
ENV FLASK_REMOVE_RESULT_DIR=Yes
RUN ln -s /FastProtein/web/server.sh /usr/local/bin/server
RUN echo 'source server' >> ~/.bashrc

VOLUME /FastProtein/runs
WORKDIR /FastProtein


#INSTALLATION INTERPROSCAN
#Docker size before InterProScan = 1.8GB
#Docker size after InterProScan = 49GB
#Confirm before you continue if your docker supports this size
#docker exec -it FastProtein interpro_install

# Exibindo mensagem de sucessoRUN echo "The server is available at http://127.0.0.1:5000"

