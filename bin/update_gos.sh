#!/bin/bash
echo "Updating GO-OBO from http://geneontology.org/docs/download-ontology/"
aria2c --continue=true --max-connection-per-server=8 --min-split-size=2M -d $FASTPROTEIN_HOME/data/ http://purl.obolibrary.org/obo/go.obo
echo "Success!"