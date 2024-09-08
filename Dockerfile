FROM bioinfoufsc/proteomic:latest

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
RUN mkdir -p /FastProtein/temp

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

