#!/bin/bash

/bioinformatic/interproscan-5.61-93.0/interproscan.sh -i $1 -o $2 -f tsv --goterms -T $3

