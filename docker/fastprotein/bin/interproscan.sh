#!/bin/bash

$INTERPRO_HOME/interproscan.sh -i $1 -o $2 -f tsv --goterms -T $3
