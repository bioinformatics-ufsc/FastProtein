FROM bioinfoufsc/proteomic:latest

RUN apt-get update -y \
    && apt-get install --no-install-recommends -y \
    apt-utils \
    maven \
    nano \
    openjdk-17-jdk \
    unzip \
    zip \
    aria2 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# python libraries
WORKDIR /
ARG CACHE_BUST=5
RUN git clone https://github.com/bioinformatics-ufsc/FastProtein.git
WORKDIR /FastProtein
RUN pip3 install --no-cache-dir -r requirements.txt

# unzip third-party software
RUN ln -s /FastProtein/bin/interpro_install.sh /usr/local/bin/interpro_install
# required directory for runs, dbs and executions
RUN mkdir -p /FastProtein/runs
RUN mkdir -p /FastProtein/temp
RUN mkdir -p /FastProtein/db

# set environment variables
ENV FASTPROTEIN_HOME="/FastProtein" \
    DATABASE_HOME="/FastProtein/db" \
    FLASK_RUN_HOST="0.0.0.0" \
    FLASK_DEBUG="False" \
    FLASK_HOME="/FastProtein/web" \
    FLASK_APP="/FastProtein/web/server.py" \
    FLASK_REMOVE_RESULT_DIR="Yes"

RUN chmod +x /FastProtein/bin/*.sh /FastProtein/web/server.sh \
    && ln -s /FastProtein/bin/fastprotein.sh /usr/local/bin/fastprotein \
    && ln -s /FastProtein/web/server.sh /usr/local/bin/server \
    && echo "source server" >> ~/.bashrc

# build fastprotein
RUN mvn -f /FastProtein/pom.xml clean install

#Creating a database - uniprot_sprot
RUN aria2c --continue=true --max-connection-per-server=8 --min-split-size=2M -d $DATABASE_HOME https://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/uniprot_sprot.fasta.gz
RUN gzip -d $DATABASE_HOME/uniprot_sprot.fasta.gz
RUN diamond makedb --in $DATABASE_HOME/uniprot_sprot.fasta -d $DATABASE_HOME/uniprot_sprot
RUN rm $DATABASE_HOME/uniprot_sprot.fasta

# expose the application port
EXPOSE 5000

# set volume for fastprotein runs and working directory
VOLUME /FastProtein/runs
WORKDIR /FastProtein

# success message
# RUN echo "The server is available at http://127.0.0.1:5000"

# interproscan
#   Docker image without InterProScan = 2.5GB
#   Docker image with InterProScan = 57GB
# make sure you machine supports this image size!
# docker exec -it FastProtein interpro_install
#or try the image to use the image -> bioinfoufsc/fastprotein-interpro:latest

