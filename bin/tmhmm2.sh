#!/bin/bash
cd $3
/bioinformatic/tmhmm-2.0c/bin/tmhmm $1 > $2
cd -