FROM bioinfoufsc/proteomic:latest
# dependencies and clean apt cache
# wolfpsort - libfindbin-libs-perl
RUN apt-get update -y \
    && apt-get install --no-install-recommends -y \
    apt-utils \
    maven \
    nano \
    openjdk-17-jdk \
    unzip \
    zip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# python libraries
WORKDIR /
ARG CACHE_BUST=$(date +%s)
RUN git clone https://github.com/bioinformatics-ufsc/FastProtein.git
WORKDIR /FastProtein
RUN pip3 install --no-cache-dir -r requirements.txt

# Criando diretório bioinformatic e copiando arquivosRUN mkdir temp


# unzip third-party software
RUN ln -s /FastProtein/bin/interpro_insta/home/renato/FastProtein/third-party/InterProScanll.sh /usr/local/bin/interpro_install

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
