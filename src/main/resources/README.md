# FastProtein 1.0

### _A fast and easy way to know more about your proteins :)_

### Information that you will find here:

- ID: Protein ID from your FASTA file.
- Length: The length of the protein sequence.
- kDa: Molecular mass in kilodaltons.
- Isoelectric Point: Isoelectric point of the full protein sequence.
- Hydropathy: Hydropathy index of the full protein sequence.
- Aromaticity: Aromaticity index of the full protein sequence.
- Membrane Evidence: We provide in silico evidence of proteins related to the membrane.
- Subcellular Localization Prediction: Prediction of the protein's subcellular localization using [WoLF PSORT](https://wolfpsort.hgc.jp/).
- Prediction of Transmembrane Helices in Proteins: Prediction of transmembrane helices in proteins using [TMHMM-2.0c](https://services.healthtech.dtu.dk/service.php?TMHMM-2.0) and [Phobius](https://phobius.sbc.su.se/).
- Prediction of Signal Peptides: Prediction of signal peptides using [SignalP-5](https://services.healthtech.dtu.dk/service.php?SignalP-5.0) and [Phobius](https://phobius.sbc.su.se/).
- GPI-Anchored Proteins: Prediction of GPI-anchored proteins using [PredGPI](https://github.com/BolognaBiocomp/predgpi/).
- Endoplasmic Reticulum Retention Total: Total number of domains found with an endoplasmic reticulum retention signal - [E.R Retention Domain](https://prosite.expasy.org/PDOC00014).
- Endoplasmic Reticulum Retention Domains: Endoplasmic reticulum retention domains found with peptide and position.
- N-Glycosylation Total: Total number of N-glycosylation domains found - [N-Glyc Domain](https://prosite.expasy.org/PDOC00001).
- N-Glycosylation Domains: N-glycosylation domains found with peptide and position.
- Header: Protein header.
- Gene Ontology, Panther, and Pfam: Analysis of protein function and annotation using [InterproScan5](https://www.ebi.ac.uk/interpro/).
- Sequence: Protein sequence.

## Output example
[fastprotein_results.zip](https://github.com/simoesrenato/bioinfo/tree/master/fast-protein/example/fastprotein_results.zip)


This software was developed in Java 17 (please cite [BioJava](https://biojava.org/)) and Python 3.

Check the [source code](https://github.com/simoesrenato/bioinfo/tree/master/fast-protein) 
Developed by PhD. Renato Simoes - renato.simoes@ifsc.edu.br

## License

MIT
