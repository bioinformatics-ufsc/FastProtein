#!/bin/bash

#arg1 = input.fasta
#arg2 = proteome to perform search
#arg3 = temporary folder

TEMP_DIR=$3

BLAST_RESULTS="$TEMP_DIR/blast_local"
echo "Blast results $BLAST_RESULTS"
BLAST_DB="$BLAST_RESULTS/dbfasta"
OUTPUT_FILE="$BLAST_RESULTS/results.txt"
FIRST_HIT="$BLAST_RESULTS/blast-firsthit.txt"

mkdir -p $BLAST_RESULTS

#Create database with the given proteome
echo '[Blast] Creating database'
makeblastdb -dbtype prot -in $2 -out $BLAST_DB

echo '[Blast] Performing query'
blastp -query $1 -db $BLAST_DB -outfmt '6 qacc sacc pident qcovs ppos evalue bitscore sseqid stitle' -out $OUTPUT_FILE -max_target_seqs 5

sed -i '1i qacc\tsacc\tpident\tqcovs\tppos\tevalue\tbitscore\tsseqid\tstitle' $OUTPUT_FILE

awk '!seen[$1]++' $OUTPUT_FILE > $FIRST_HIT