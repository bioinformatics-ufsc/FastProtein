<!-- MARKDOWN LINKS & IMAGES ----------------------------------------------- -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[![License][apache-shield]][apache-url]
[![BioLib][biolib-shield]][biolib-url]

[apache-url]: https://opensource.org/licenses/Apache-2.0
[apache-shield]: https://img.shields.io/badge/License-Apache_2.0-blue.svg
[biolib-url]: https://biolib.com/ufsc/FastProtein
[biolib-shield]: https://img.shields.io/badge/Online%20Server-BioLib-brightgreen

<!-- PROJECT LOGO ---------------------------------------------------------- -->

<br />
<div align="center">
  <!-- <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a> -->

  <h3 align="center">FastProtein</h3>

  <p align="center">
    A fast and easy way to know more about your proteins :)
    <!-- <br />
    <a href="https://github.com/othneildrew/Best-README-Template"><strong>Explore the docs »</strong></a>
    <br /> -->
  </p>
</div>

<!-- TABLE OF CONTENTS ----------------------------------------------------- -->

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-fastprotein">About FastProtein</a>
      <ul>
        <li><a href="#expected-results">Expected Results</a></li>
      </ul>
    </li>
    <li><a href="#installation">Installation</a></li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#Docker">Docker</a></li>
        <li><a href="#biolib">BioLib</a></li>
      </ul>
    </li>
    <li><a href="#contact-info">Contact Info</a></li>
    <li><a href="#citation-and-acknowledgments">Citation and Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT ----------------------------------------------------- -->

## **About FastProtein**

> _Developed by Renato Simões, PhD - <renato.simoes@ifsc.edu.br>_

# TODO terminar descrição do Fast (incluir workflow)
FastProtein is a integrated pipeline...

FastProtein was tested in Unix-based systems and supports Docker and BioLib execution.

If you have questions, suggestions or difficulties regarding the pipeline, please do not hesitate to contact our team here on GitHub or by [email](labioinfo.genome@gmail.com).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## **Expected Results**

 <!-- TODO mudar arquivo de resultados -->
 
> _Example: [fastprotein_results.zip](https://github.com/simoesrenato/bioinfo/tree/master/fast-protein/example/fastprotein_results.zip)_

- **ID:** Protein ID from your FASTA file
- **Length:** The length of the protein sequence
- **kDa:** Molecular mass in kilodaltons
- **Isoelectric Point:** Isoelectric point of the full protein sequence
- **Hydropathy:** Hydropathy index of the full protein sequence
- **Aromaticity:** Aromaticity index of the full protein sequence
- **Membrane Evidence:** We provide in silico evidence of proteins related to the membrane
- **Subcellular Localization Prediction:** Prediction of the protein's subcellular localization using [WoLF PSORT](https://wolfpsort.hgc.jp)
- **Prediction of Transmembrane Helices in Proteins:** Prediction of transmembrane helices in proteins using [TMHMM-2.0c](https://services.healthtech.dtu.dk/service.php?TMHMM-2.0) and [Phobius](https://phobius.sbc.su.se)
- **Prediction of Signal Peptides:** Prediction of signal peptides using [SignalP-5](https://services.healthtech.dtu.dk/service.php?SignalP-5.0) and [Phobius](https://phobius.sbc.su.se)
- **GPI-Anchored Proteins:** Prediction of GPI-anchored proteins using [PredGPI](https://github.com/BolognaBiocomp/predgpi)
- **Endoplasmic Reticulum Retention Total:** Total number of domains found with an endoplasmic reticulum retention signal - [E.R Retention Domain](https://prosite.expasy.org/PDOC00014)
- **Endoplasmic Reticulum Retention Domains:** Endoplasmic reticulum retention domains found with peptide and position
- **N-Glycosylation Total:** Total number of N-glycosylation domains found - [N-Glyc Domain](https://prosite.expasy.org/PDOC00001)
- **N-Glycosylation Domains:** N-glycosylation domains found with peptide and position
- **Header:** Protein header
- **Gene Ontology, Panther, and Pfam:** Protein function and annotation using [InterProScan5](https://www.ebi.ac.uk/interpro)
- **Sequence:** Protein sequence

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## **Technologies**

[debian-url]: https://www.debian.org
[debian-shield]: https://img.shields.io/badge/Debian-A81D33?style=for-the-badge&logo=debian&logoColor=white
[docker-url]: https://www.docker.com/
[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[java-url]: https://www.java.com
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[python-url]: https://www.python.org
[python-shield]: https://img.shields.io/badge/Python-14354C?style=for-the-badge&logo=python&logoColor=white

[![Debian][debian-shield]][debian-url]

[![Docker][docker-shield]][docker-url]

[![Java][java-shield]][java-url]

[![Python][python-shield]][python-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED ------------------------------------------------------- -->

## **Prerequisites**
 <!-- TODO ver pre requisitos -->
- Docker

## **Installation**

### **To create a local image Docker (optional)**

1. Clone the repository

   ```bash
   git clone https://github.com/bioinformatics-ufsc/FastProtein
   ```

2. Change directory and build container

   ```bash
   cd FastProtein/docker
   docker build -t fastprotein:latest .
   ```

### **Controlling Docker container**
   ```bash
   # Step 1 - Create a local directory that will be used to exchange files with Docker (example fastprotein/ inside user home)
   #          ~/fastprotein is the work directory
   #          ~/fastprotein/runs the directory that stores the FastProtein web server requests
   #          
   mkdir -p ~/fastprotein/runs
   # Step 2 - Create a container named FastProtein that will have the volume associated with the locally created directory. 
   #          Port 5000 is used to access the FastProtein web server.
   #          PS 1: if you mounted local image, remove the user 'bioinfoufsc/'
   #          PS 2: this command is executed only one time
   docker run -d -it --name FastProtein -p 5000:5000 -v ~/fastprotein:/fastprotein bioinfoufsc/fastprotein:latest
   #
   # The 'docker run' command starts the container for the first time. 
   # If everything runs without errors, open a browser and go to 127.0.0.1:5000 to access the FastProtein web.
   #
   # If you want to STOP the container, the command is:
   docker stop FastProtein
   # If you want to START the container, the command is:
   docker stop FastProtein
   # To check if your container is running, the command is:
   docker ps | grep FastProtein
   # If you want to enter inside the container, the command is:
   docker exec -it FastProtein /bin/bash 
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE ----------------------------------------------------------------- -->

## **Usage**

### **Starting Local Web Server**
<p align="right">Access the address (<a href="http://127.0.0.1" target='_blank'>127.0.0.1</a>) using your preferred browser. </p>

<p>Default IP is 127.0.0.1 and exposed port is 5000.

Just open the following link in a browser and FastProtein local service will be up and running: <http://127.0.0.1:5000>

Results will be redirect to directory `/fastproteins/runs` linked with the local folder `~/fastproteins/runs`.
</p>
### **FastProtein web**



### **Using via docker container (local)**

```bash
## To learn about the execution parameters, type:
docker exec -it FastProtein fastprotein -h
## Example of execution:
##        input.fasta - proteins to analyze
##        db.fasta - database for blast search (protein FASTA)
##        result_test - local inside the container with the results (/fastprotein/result_test is linked with the local folder ~/fastprotein/result_test)
docker exec -it FastProtein fastprotein -i /example/input.fasta --local-blast /example/db.fasta -o result_test
```

#### **Using inside the docker container**

```bash
## Enter into container
docker exec -it FastProtein /bin/bash 
##
## Execute the command:
fastprotein -h
##
## Simplest execution (the default output is fastprotein_results)
fastprotein -i /example/input.fasta 
##
## Complete execution (with InterproScan and Remote Blast using uniprotkb_swissprot as database)
## PS: The Remote Blast executes protein by protein, which makes the process slower (2-4min per protein). 
       It is recommended to use the --local-blast instead. However, both can be executed, and the result is displayed in separate columns.
fastprotein -i /example/input.fasta --local-blast /example/db.fasta --interpro --remote-blast -o result_test --zip
```

#### **Running remote BLAST, local BLAST with custom database and InterPro**
```bash
fastprotein -i /example/input.fasta -s animal --interpro --remote-blast --local-blast /example/db.fasta
```

---


### **BioLib**

FastProtein has an online service for small datasets or even example of generated results

This service is available at: (<a href="https://biolib.com/UFSC/FastProtein" target='_blank'>https://biolib.com/UFSC/FastProtein</a>) <>

#### **Running BioLib locally using the command line**

Alternatively, you can run FastProtein via biolib through the command line. To do this, install biolib and execute the command.
Required python 3 and pip3.

```bash
pip3 install -U pybiolib
biolib run UFSC/FastProtein --help
```

BioLib has a specific syntax and the flags `--interpro` and `--remote-blast` needed a `true` or `false` value in the command line. 
Check the Example:

```bash
biolib run UFSC/FastProtein -i input.fasta --interpro true --remote-blast true
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## **Contact Info**

[Lab. Bioinformática](https://bioinformatica.ufsc.br) (UFSC)

[gmail-url]: mailto:labioinfo.genome@gmail.com
[gmail-shield]: https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white
[ig-url]: https://www.instagram.com/labioinfoufsc
[ig-shield]: https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white

[![Instagram][ig-shield]][ig-url]
[![Gmail][gmail-shield]][gmail-url]

Project Links:

- [GitHub](https://github.com/bioinformatics-ufsc/FastProtein)
- [BioLib](https://biolib.com/ufsc/FastProtein)
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS ------------------------------------------------------- -->

## **Citation and Acknowledgments**

This software was developed using Java 17 (please cite [BioJava](https://biojava.org)) and Python 3.

Please cite us: 

FastProtein also uses a suite of softwares, please cite them too:

- WoLF PSORT - [Horton et al., 2007](https://doi.org/10.1093/nar/gkm259)
- TMHMM-2.0 - [Krogh et al., 2001](https://doi.org/10.1006/jmbi.2000.4315)
- Phobius - [Käll et al., 2004](http://dx.doi.org/10.1016/j.jmb.2004.03.016)
- SignalP-5 - [Armenteros et al., 2019](https://doi.org/10.1038/s41587-019-0036-z)
- PredGPI - [Pierleoni, A; Marteli P.L. and Casadio R., 2008](https://doi.org/10.1186/1471-2105-9-392)
- PROSITE - [Sigrist et al., 2012](https://doi.org/10.1093/nar/gks1067)
- InterProScan5 - [Blum et al., 2020](https://doi.org/10.1093/nar/gkaa977)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
