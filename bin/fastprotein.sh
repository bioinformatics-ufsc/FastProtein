#!/bin/bash
ln -sf /bioinformatic/ncbi-blast-2.10.0/bin/blastp /usr/local/bin/blastp
ln -sf /bioinformatic/ncbi-blast-2.10.0/bin/makeblastdb /usr/local/bin/makeblastdb
ln -sf /bioinformatic/signalp-5.0b/bin/signalp /usr/local/bin/signalp
ln -sf /bioinformatic/signalp-5.0b/lib/libtensorflow.so /usr/local/lib/libtensorflow.so
ln -sf /bioinformatic/signalp-5.0b/lib/libtensorflow_framework.so /usr/local/lib/libtensorflow_framework.so
ln -sf /bioinformatic/predgpi/predgpi.sh /usr/local/bin/predgpi
ln -sf /bioinformatic/phobius-1.01/phobius.pl /usr/local/bin/phobius
ln -sf /bioinformatic/tmhmm-2.0c/bin/tmhmm /usr/local/bin/tmhmm2
ln -sf /bioinformatic/wolfpsort/bin/wolfpsort.sh /usr/local/bin/wolfpsort
#ln -sf $INTERPRO_HOME/interproscan.sh  /usr/local/bin/interproscan
#export INTERPRO_HOME='/bioinformatic/interproscan-5.61-93.0'



java -jar $FASTPROTEIN_HOME/bin/FastProtein.jar $@