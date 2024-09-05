#!/bin/bash
echo 'Starting InterProScan installation'
START_TIME=$(date +%s)

command_path="/usr/local/bin/interproscan"

# Verificar se o comando existe
if [ -x "$command_path" ]; then
    echo "InterProScan is installed."
    exit 0
else
    echo "InterProScan is not installed."
fi

if [ -d "/bioinformatic/interproscan-5.61-93.0" ]; then
    echo "We are trying to configure"
    echo "The directory already exists. Try to execute command 'interproscan --help'"
    exit 1
fi

if [ ! -f "/bioinformatic/interproscan-5.61-93.0-64-bit.tar.gz" ]; then
    echo 'File interproscan-5.61-93.0-64-bit.tar.gz not found, downloading'
    aria2c --continue=true --max-connection-per-server=8 --min-split-size=2M -d /bioinformatic https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/5.61-93.0/interproscan-5.61-93.0-64-bit.tar.gz
    echo 'interproscan-5.61-93.0-64-bit.tar.gz downloaded'
fi

if [ -f "/bioinformatic/interproscan-5.61-93.0-64-bit.tar.gz" ]; then
    echo 'File interproscan-5.61-93.0-64-bit.tar.gz found. Unpacking. This process my take minutes (20~30min).'
    tar --use-compress-program=pigz -xvf /bioinformatic/interproscan-5.61-93.0-64-bit.tar.gz -C /bioinformatic

    echo "export INTERPRO_HOME=/bioinformatic/interproscan-5.61-93.0" >> ~/.bashrc
    source  ~/.bashrc

    ln -sf $INTERPRO_HOME/interproscan.sh  /usr/local/bin/interproscan

    cd $INTERPRO_HOME
    echo "Executing InterProScan setup. This process my take some minutes (10~15)"
    python3 setup.py -f interproscan.properties

    echo "InterProScan installed successfuly at $INTERPRO_HOME"

    END_TIME=$(date +%s)
    ELAPSED_TIME=$(expr $END_TIME - $START_TIME)
    ELAPSED_TIME=$(date -u -d @${ELAPSED_TIME} +"%H:%M:%S")

    echo "Total execution time: $ELAPSED_TIME."
fi