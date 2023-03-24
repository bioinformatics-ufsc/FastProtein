<p>

FastProtein

FastProtein is a integrated Pipeline ...

FastProtein was tested in Unix-based systems, supporting docker and biolib execution

If you have questions, suggestions or difficulties regarding the pipeline, please do not hesitate to contact our team here on GitHub or by email: <labioinfo.genome@gmail.com>.

---
- [**Installation**](#1---installation)
- [**Workflow**](#Workflow)
- [**Outputs**](#Workflow)
- [**Citation**](#Workflow)

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


Check the [source code](https://github.com/simoesrenato/bioinfo/tree/master/fast-protein) 
Developed by PhD. Renato Simoes - renato.simoes@ifsc.edu.br

## License

MIT


---
# 1 - Installation

## 1.1 - With Docker locally 

```bash
git clone https://github.com/bioinformatics-ufsc/FastProtein
cd FastProtein/docker
docker build -t fastprotein:latest .

# run this command to access the volume shared
docker run -it --name FastProtein -p 5000:5000 -v /Users/renato/Documents/GitHub/bioinfo/fast-protein/example:/fastprotein FastProtein:latest /bin/bash;

#Starting docker for the first time

#<local dir> - local directory shared between user's machine and FastProtein Docker (Don't change docker path)

docker run -it --name FastProtein -p 5000:5000 -v <local dir>:/fastprotein fastprotein:latest /bin/bash;

```
### Starting docker 

```bash
docker exec -it FastProtein /bin/bash;
```

### Execution
```bash
fastprotein -h
```

### Simplest execution
```bash
fastprotein -i input.fasta -o /fastprotein/test
```

#### Running remote blast, local blast with custom db and InterPro
```bash
fastprotein -i input.fasta -s animal --interpro --remote-blast --local-blast db.fasta
```

---
## 1.1.2 - Starting a local web server
Inside FastProtein container, execute
```bash
./server.sh
```
Pre-configurated ip is: 127.0.0.1, and exposed port is 5000.
Just open the following link in a browser and FastProtein local service is up and running

http://127.0.0.1:5000/

Results will be redirect to directory `/fastproteins/runs`

### 1.2 Biolib

FastProtein has an online service for small datasets or even example of generated results
This service is available at:

https://biolib.com/UFSC/FastProtein/

### 1.2.1 Running Biolib locally by terminal

```bash
biolib run UFSC/FastProtein -h
```

Due to Biolib sintax, flags (--interpro / --remote-blast) needed a true/false value in command line as in 
following example
```bash
biolib run UFSC/FastProtein -i input.fasta --interpro true --remote-blast true
```

# 2 - Workflow

# 3 - Output

# 4 - Citation

This software was developed in Java 17 (please cite [BioJava](https://biojava.org/)) and Python 3.

FastProtein use a suite of softwares, please cite them:

- WoLF PSORT [Horton et al., 2007](https://doi.org/10.1093/nar/gkm259)

- TMHMM-2.0 [Krogh et al., 2001](https://doi.org/10.1006/jmbi.2000.4315) 

- Phobius [Käll et al., 2004](http://dx.doi.org/10.1016/j.jmb.2004.03.016)

- SignalP-5 [Armenteros et al., 2019](https://doi.org/10.1038/s41587-019-0036-z)

- PredGPI [Pierleoni, A; Marteli P.L. and Casadio R., 2008](https://doi.org/10.1186/1471-2105-9-392)

- PROSITE suite [Sigrist et al., 2012](https://doi.org/10.1093/nar/gks1067)

- InterProScan5 [Blum et al., 2020](https://doi.org/10.1093/nar/gkaa977)

### **Technologies**

[docker-url]: https://www.docker.com/
[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[java-url]: https://www.java.com
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[python-url]: https://www.python.org
[python-shield]: https://img.shields.io/badge/Python-14354C?style=for-the-badge&logo=python&logoColor=white

- [![Docker][docker-shield]][docker-url]
- [![Java][java-shield]][java-url]
- [![Python][python-shield]][python-url]


</p>

[![MIT License][license-shield]][license-url]
[![BioLib][biolib-shield]][biolib-url]

[biolib-url]: https://biolib.com/ufsc/FastProtein
[biolib-shield]: https://img.shields.io/badge/Online%20Server-BioLib-brightgreen

<!-- PROJECT LOGO -->
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

- [FastProtein 1.0](#fastprotein-10)
    - [_A fast and easy way to know more about your proteins :)_](#a-fast-and-easy-way-to-know-more-about-your-proteins-)
    - [Information that you will find here:](#information-that-you-will-find-here)
  - [Output example](#output-example)
  - [License](#license)
- [1 - Installation](#1---installation)
  - [1.1 - With Docker locally](#11---with-docker-locally)
    - [Starting docker](#starting-docker)
    - [Execution](#execution)
    - [Simplest execution](#simplest-execution)
      - [Running remote blast, local blast with custom db and InterPro](#running-remote-blast-local-blast-with-custom-db-and-interpro)
  - [1.1.2 - Starting a local web server](#112---starting-a-local-web-server)
    - [1.2 Biolib](#12-biolib)
    - [1.2.1 Running Biolib locally by terminal](#121-running-biolib-locally-by-terminal)
- [2 - Workflow](#2---workflow)
- [3 - Output](#3---output)
- [4 - Citation](#4---citation)
    - [**Technologies**](#technologies)
  - [**About FastProtein**](#about-fastprotein)
  - [**Expected Results**](#expected-results)
  - [**Technologies**](#technologies-1)
  - [**Prerequisites**](#prerequisites)
  - [**Installation**](#installation)
    - [**With Docker (Local)**](#with-docker-local)
  - [**Usage**](#usage)
    - [**Starting Docker**](#starting-docker-1)
      - [**Execution**](#execution-1)
      - [**Simplest Execution**](#simplest-execution-1)
      - [**Running remote BLAST, local BLASST with custom database and InterPro**](#running-remote-blast-local-blasst-with-custom-database-and-interpro)
    - [**Starting Local Web Server**](#starting-local-web-server)
    - [**BioLib**](#biolib)
      - [**Running BioLib locally using the command line**](#running-biolib-locally-using-the-command-line)
  - [**License**](#license-1)
  - [**Contact Info**](#contact-info)
  - [**Citation and Acknowledgments**](#citation-and-acknowledgments)


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-fastprotein">About FastProtein</a>
      <ul>
        <li><a href="#expected-results">Output</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## **About FastProtein**
> *Developed by Renato Simões, PhD - <renato.simoes@ifsc.edu.br>*

FastProtein is a integrated pipeline...

FastProtein was tested in Unix-based systems and supports Docker and BioLib execution.

If you have questions, suggestions or difficulties regarding the pipeline, please do not hesitate to contact our team here on GitHub or by [email](labioinfo.genome@gmail.com).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## **Expected Results**
> *Example: [fastprotein_results.zip](https://github.com/simoesrenato/bioinfo/tree/master/fast-protein/example/fastprotein_results.zip)*

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

[linux-url]: https://www.debian.org
[linux-shield]: https://img.shields.io/badge/Linux-E34F26?style=for-the-badge&logo=linux&logoColor=black
[docker-url]: https://www.docker.com/
[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[java-url]: https://www.java.com
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[python-url]: https://www.python.org
[python-shield]: https://img.shields.io/badge/Python-14354C?style=for-the-badge&logo=python&logoColor=white

- [![Linux][linux-shield]][linux-url]
- [![Docker][docker-shield]][docker-url]
- [![Java][java-shield]][java-url]
- [![Python][python-shield]][python-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## **Prerequisites**

- Docker

## **Installation**

### **With Docker (Local)**

1. Clone the repository
    ```bash
    git clone https://github.com/bioinformatics-ufsc/FastProtein
    ```

2. Change directory and build container
    ```bash
    cd FastProtein/docker
    docker build -t fastprotein:latest .
    ```

3. Using Docker
    ```bash
    # run this command to access the volume shared
    docker run -it --name FastProtein -p 5000:5000 -v /Users/renato/Documents/GitHub/bioinfo/fast-protein/example:/fastprotein FastProtein:latest /bin/bash;

    # starting docker for the first time
    # <local dir>
    #   local directory shared between user's machine and FastProtein Docker
    #   *don't* change docker path
    docker run -it --name FastProtein -p 5000:5000 -v <local dir>:/fastprotein fastprotein:latest /bin/bash;
    ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## **Usage**

### **Starting Docker** 

```bash
docker exec -it FastProtein /bin/bash;
```

#### **Execution**

```bash
fastprotein -h
```

#### **Simplest Execution**

```bash
fastprotein -i input.fasta -o /fastprotein/test
```

#### **Running remote BLAST, local BLASST with custom database and InterPro**

```bash
fastprotein -i input.fasta -s animal --interpro --remote-blast --local-blast db.fasta
```

---
### **Starting Local Web Server**

Inside the FastProtein container, execute:

```bash
./server.sh
```

Default IP is 127.0.0.1 and exposed port is 5000.

Just open the following link in a browser and FastProtein local service will be up and running: <http://127.0.0.1:5000>

Results will be redirect to directory `/fastproteins/runs`.

### **BioLib**

FastProtein has an online service for small datasets or even example of generated results

This service is available at: <https://biolib.com/ufsc/FastProtein>

#### **Running BioLib locally using the command line**

```bash
biolib run UFSC/FastProtein -h
```

BioLib has a specific syntax and the flags `--interpro` and `--remote-blast` needed a `true` or `false` value in the command line.

Example:

```bash
biolib run UFSC/FastProtein -i input.fasta --interpro true --remote-blast true
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

## **License**

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## **Contact Info**

Lab. Bioinformática
- [@labioinfoufsc](https://www.instagram.com/labioinfoufsc)
- <labioinfo.genome@gmail.com>

Project Links:
- [GitHub](https://github.com/bioinformatics-ufsc/FastProtein)
- [BioLib](https://biolib.com/ufsc/FastProtein)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->

## **Citation and Acknowledgments**

This software was developed using Java 17 (please cite [BioJava](https://biojava.org)) and Python 3.

FastProtein also uses a suite of softwares, please cite them:
- [WoLF PSORT](https://wolfpsort.hgc.jp)
- TMHMM-2.0 - [Krogh et al., 2001](https://doi.org/10.1006/jmbi.2000.4315) 
- Phobius - [Käll et al., 2004](http://dx.doi.org/10.1016/j.jmb.2004.03.016)
- SignalP-5 - [Armenteros et al., 2019](https://doi.org/10.1038/s41587-019-0036-z)
- PredGPI - [Pierleoni, A; Marteli P.L. and Casadio R., 2008](https://doi.org/10.1186/1471-2105-9-392)
- PROSITE - [Sigrist et al., 2012](https://doi.org/10.1093/nar/gks1067)
- InterProScan5 - [Blum et al., 2020](https://doi.org/10.1093/nar/gkaa977)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->