#docker build -t bioinfoufsc/fastprotein:latest .
FROM debian

RUN apt-get update
RUN apt-get install -y unzip
RUN apt-get install -y zip
RUN apt-get install -y nano
RUN apt-get install -y wget
#Dependency for wolfpsort
RUN apt-get install -y libfindbin-libs-perl

RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN apt-get install -y openjdk-17-jdk

#Dependency for predgpi
RUN pip3 install numpy==1.23.1

#Dependences for interpro online
RUN pip3 install requests
RUN pip3 install xmltramp2


# Criando diretório bioinformatic e copiando arquivos
RUN mkdir -p /bioinformatic/fastprotein/
RUN mkdir /fasta/

COPY softwares/predgpi.zip /bioinformatic/
COPY softwares/tmhmm-2.0c.zip /bioinformatic/
COPY softwares/signalp-5.0b.zip /bioinformatic/
COPY softwares/wolfpsort.zip /bioinformatic/
COPY softwares/phobius-1.01.zip /bioinformatic/
COPY softwares/ncbi-blast-2.10.0.zip /bioinformatic/
COPY fastprotein/ /bioinformatic/fastprotein/

RUN mkdir /bioinformatic/fastprotein/temp

# Dezipando arquivos
RUN unzip -q /bioinformatic/predgpi.zip -d /bioinformatic/ \
    && unzip -q /bioinformatic/tmhmm-2.0c.zip -d /bioinformatic/ \
    && unzip -q /bioinformatic/signalp-5.0b.zip -d /bioinformatic/ \
    && unzip -q /bioinformatic/wolfpsort.zip -d /bioinformatic/ \
    && unzip -q /bioinformatic/phobius-1.01.zip -d /bioinformatic/ \
    && unzip -q /bioinformatic/ncbi-blast-2.10.0.zip -d /bioinformatic/


RUN ln -s /bioinformatic/ncbi-blast-2.10.0/bin/blastp /usr/local/bin/blastp
RUN ln -s /bioinformatic/ncbi-blast-2.10.0/bin/makeblastdb /usr/local/bin/makeblastdb
RUN ln -s /bioinformatic/signalp-5.0b/bin/signalp /usr/local/bin/signalp
RUN ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow.so /usr/local/lib/libtensorflow.so
RUN ln -s /bioinformatic/signalp-5.0b/lib/libtensorflow_framework.so /usr/local/lib/libtensorflow_framework.so
RUN ln -s /bioinformatic/predgpi/predgpi.sh /usr/local/bin/predgpi
RUN ln -s /bioinformatic/phobius-1.01/phobius.pl /usr/local/bin/phobius
RUN ln -s /bioinformatic/tmhmm-2.0c/bin/tmhmm /usr/local/bin/tmhmm2
RUN ln -s /bioinformatic/wolfpsort/bin/wolfpsort.sh /usr/local/bin/wolfpsort
RUN ln -s /bioinformatic/fastprotein/input.fasta /fasta/input.fasta
RUN ln -s /bioinformatic/fastprotein/db.fasta /fasta/db.fasta
RUN ln -s /bioinformatic/fastprotein/bin/interpro_install.sh /usr/local/bin/interpro_install

ENV PREDGPI_HOME='/bioinformatic/predgpi'
ENV FASTPROTEIN_HOME='/bioinformatic/fastprotein'

RUN rm /bioinformatic/predgpi.zip
RUN rm /bioinformatic/tmhmm-2.0c.zip
RUN rm /bioinformatic/signalp-5.0b.zip 
RUN rm /bioinformatic/wolfpsort.zip
RUN rm /bioinformatic/phobius-1.01.zip
RUN rm /bioinformatic/ncbi-blast-2.10.0.zip

# Givin permission
RUN chmod +x /bioinformatic/fastprotein/bin/signalp5.sh
RUN chmod +x /bioinformatic/fastprotein/bin/predgpi.sh
RUN chmod +x /bioinformatic/fastprotein/bin/tmhmm2.sh
RUN chmod +x /bioinformatic/fastprotein/bin/wolfpsort.sh
RUN chmod +x /bioinformatic/fastprotein/bin/phobius.sh
RUN chmod +x /bioinformatic/fastprotein/bin/blast.sh
RUN chmod +x /bioinformatic/fastprotein/bin/fastprotein.sh
RUN chmod +x /bioinformatic/fastprotein/bin/interproscan.sh
RUN chmod +x /bioinformatic/fastprotein/bin/interpro_install.sh
RUN chmod +x /bioinformatic/fastprotein/web/server.sh






RUN mkdir -p /fastprotein/
RUN mkdir /example
COPY fastprotein/input.fasta /example/
COPY fastprotein/db.fasta /example/

RUN ln -s /bioinformatic/fastprotein/bin/fastprotein.sh /usr/local/bin/fastprotein

#Optional if you want to run a Flask server. Access it via browser at localhost:5000
RUN pip3 install flask
RUN pip3 install biopython==1.81
RUN mkdir /fastprotein/runs
EXPOSE 5000
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_DEBUG=True
ENV FLASK_HOME='/bioinformatic/fastprotein/web'
ENV FLASK_APP=$FLASK_HOME""'/server.py'
ENV FLASK_REMOVE_RESULT_DIR=Yes
ENV FASTPROTEIN_WEB_OUTPUT='/fastprotein/runs'
RUN ln -s /bioinformatic/fastprotein/web/server.sh /usr/local/bin/server
RUN echo 'source server' >> ~/.bashrc

VOLUME /fastprotein
WORKDIR /fastprotein


#INSTALLATION INTERPROSCAN
#Docker size before InterProScan = 1.8GB
#Docker size after InterProScan = 49GB
#Confirm before you continue if your docker supports this size
#docker exec -it FastProtein interpro_install

# Exibindo mensagem de sucesso
RUN echo "Try it online at https://biolib.com/UFSC/FastProtein/"
RUN echo "The server is available at http://127.0.0.1:5000"

