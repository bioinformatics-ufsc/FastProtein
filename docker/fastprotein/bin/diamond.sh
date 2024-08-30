#!/bin/bash

#arg1 = input.fasta
#arg2 = proteome to perform search
#arg3 = temporary folder

TEMP_DIR=$3

DIAMOND_RESULTS="$TEMP_DIR/diamond_local"
echo "Diamond results $DIAMOND_RESULTS"
DIAMOND_DB="$DIAMOND_RESULTS/dbfasta"
OUTPUT_FILE="$DIAMOND_RESULTS/results.txt"
FIRST_HIT="$DIAMOND_RESULTS/diamond-firsthit.txt"

mkdir -p $DIAMOND_RESULTS

#Create database with the given proteome
if [[ $2 == *.fasta ]]; then
    # Create database with the given proteome
    echo '[Diamond] Creating database'
    diamond makedb --in $2 -d $DIAMOND_DB
else
    DIAMOND_DB="$2"
    echo '[Diamond] Using database '$DIAMOND_DB
fi

echo "[Diamond] Performing query"
echo "FASTA: $1"
echo "DB: $DIAMOND_DB"
echo "OUTPUT: $OUTPUT_FILE"

diamond blastp --query $1 --db $DIAMOND_DB --out $OUTPUT_FILE --outfmt 6 qseqid sseqid pident qcovhsp positive evalue bitscore sseqid stitle

sed -i '1i qseqid\tsseqid\tpident\tqcovhsp\tpositive\tevalue\tbitscore\tsseqid\tstitle' $OUTPUT_FILE

awk '!seen[$1]++' $OUTPUT_FILE > $FIRST_HIT

