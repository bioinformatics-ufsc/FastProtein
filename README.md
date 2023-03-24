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

#TODO FIX BACK TO TOP
<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

## **Output Results**

 <!-- TODO mudar arquivo de resultados -->

### FastProtein can evaluate multiple protein parameters in a single run. As output, user obtain a table with:
<p>&ensp;</p>

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

---
## **Technologies**

<!--
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
-->
<p align="center">
  <a href="https://www.debian.org">
    <img src="https://img.shields.io/badge/Debian-A81D33?style=for-the-badge&logo=debian&logoColor=white" width="100" height="30"/>
  </a><a>&ensp;</a>
  <a href="https://www.docker.com/">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" width="100" height="30"/>
  </a><a>&ensp;</a>
    <a href="https://www.python.org">
    <img src="https://img.shields.io/badge/Python-14354C?style=for-the-badge&logo=python&logoColor=white" width="100" height="30"/>
</p>
<p align="center">  
  </a><a>&ensp;</a>
    <a href="https://www.java.com">
    <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" width="100" height="30"/>
  </a><a>&ensp;</a>
    <a href="https://biojava.org/">
    <img src="https://biojava.org/images/logo/logo_800.png" width="100" height="30"/>
  </a><a>&ensp;</a>
    <a href="https://biolib.com/">
    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDx2Oto5TiJ0WjidYTHUJ819-cnPaZZiX8yVsa8dXxSaMlsv7D" width="100" height="30"/>
  </a><a>&ensp;</a>
</p>
<!--blank line-->
<p>&ensp;</p>



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
#
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
   docker start FastProtein
   # To check if your container is running, the command is:
   docker ps | grep FastProtein
   # If you want to enter inside the container, the command is:
   docker exec -it FastProtein /bin/bash 
  # To exchange files between the host and the container without using a volume, use the command:
  # docker cp <local_file> <container_id>:<container_file>
  # Eg: copy a fasta file test/human.fasta to /fastprotein (or other directory if you need)
  docker cp test/human.fasta FastProtein:/fastprotein
  # Eg: copy the folder runs from container to local runs folder
  docker cp FastProtein:/fastprotein/runs ./runs
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE ----------------------------------------------------------------- -->
---
## **Usage**
### **FastProtein Web Server**

<p>Default IP is 127.0.0.1 and exposed port is 5000.

Just open the following link in a browser and FastProtein local service will be up and running: (<a href="http://127.0.0.1:5000" target='_blank'>127.0.0.1:5000</a>)

Results will be redirect to directory `/fastproteins/runs` linked with the local folder `~/fastproteins/runs`.
A list of zip files is showed in the web page.
</p>

### **Server Screen**
<p align="center">
  <a>
    <img src=".img/screen.png" width="800" height="500"/>
  </a>
</p>

#
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
#
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
##       It is recommended to use the --local-blast instead. However, both can be executed, and the result is displayed in separate columns.
fastprotein -i /example/input.fasta --local-blast /example/db.fasta --interpro --remote-blast -o result_test --zip
##
## Running remote BLAST, local BLAST with custom database and InterPro**
##
fastprotein -i /example/input.fasta -s animal --interpro --remote-blast --local-blast /example/db.fasta
```
---

### **BioLib**

FastProtein has an online service for small datasets or even example of generated results

This service is available at: (<a href="https://biolib.com/UFSC/FastProtein" target='_blank'>https://biolib.com/UFSC/FastProtein</a>)

#### **Running BioLib locally using the command line**

Alternatively, you can run FastProtein via biolib through the command line. To do this, install biolib and execute the command.
Required python 3 and pip3.

```bash
##Installing BioLib
pip3 install -U pybiolib
##Running help
biolib run UFSC/FastProtein --help
```

<p>&ensp;</p>

BioLib has a specific syntax and the flags `--interpro` and `--remote-blast` needed a `true` or `false` value in the command line. 
The default BioLib output is `biolib_results/` in the current folder
Check the Example:

```bash
biolib run UFSC/FastProtein -i input.fasta --interpro true --remote-blast true
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
---
## **Contact Info**


[ufsc-url]: https://ufsc.br/
[ufsc-shield]: https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Flogodownload.org%2Fwp-content%2Fuploads%2F2015%2F02%2Fufsc-logo-1-140x140.png&f=1&nofb=1&ipt=211156b56538b57afb3fbfab6e4c96c77572f6d1ff3daa8a0240ff714352c390&ipo=images
[ifsc-url]: https://www.ifsc.edu.br/
[ifsc-shield]: https://www.ifsc.edu.br/image/layout_set_logo?img_id=902745&t=1677950696306
[bioinfo-url]: https://bioinformatica.ufsc.br
[bioinfo-shield]: https://bioinformatica.ufsc.br/files/2020/02/cropped-Logo-V-C.png
[ig-url]: https://www.instagram.com/labioinfoufsc
[ig-shield]: https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white
[gmail-url]: mailto:labioinfo.genome@gmail.com
[gmail-shield]: https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white
[ig-url]: https://www.instagram.com/labioinfoufsc
[ig-shield]: https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white

<!---
[![ufsc][ufsc-shield]][ufsc-url] 
[![ifsc][ifsc-shield]][ifsc-url] 
[![bioinfo][bioinfo-shield]][bioinfo-url] 
-->

<p align="center">
  <a href="https://bioinformatica.ufsc.br">
    <img src="https://bioinformatica.paginas.ufsc.br/files/2012/03/Logo-H-C-e1582133841295.png" width="300" height="100"/>
  </a><a>&ensp;</a>
  <a href="https://www.ifsc.edu.br/">
    <img src="https://www.colegioweb.com.br/wp-content/uploads/2016/10/Instituto-Federal-de-Santa-Catarina-abre-inscri%C3%A7%C3%B5es-para-o-vestibular-2-1024x342.png" width="300" height="100"/>
  </a><a>&ensp;</a>
  <a href="https://ufsc.br/">
    <img src="https://bioinformatica.ufsc.br/wp-content/themes/brasilGovInterno/img/brasao_site_ufsc.svg?ver=1679517588" width="100" height="120"/>
  </a><a>&ensp;</a>
</p>

<!--blank line-->
<p>&ensp;</p>

<p align="center">
  <a href="mailto:labioinfo.genome@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" width="90" height="30"/>
  </a>
  <a href="https://www.instagram.com/labioinfoufsc">
    <img src="https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white" width="100" height="30"/>
  </a>
</p>


<!--
[github-url]: https://github.com/bioinformatics-ufsc/FastProtein
[github-shield]:https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white

[![github][github-shield]][github-url]

-->

<p>&ensp;</p>

### **Project Links:**
<p align="center">
  <a href="https://biolib.com/ufsc/FastProtein">
    <img src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F51168%3Fs%3D200%26v%3D4&f=1&nofb=1&ipt=4dce28babba31bf34b7b7e72629ef9169d8bb49487f030e6978adb1df8b5facb&ipo=images" width="80" height="80"/>
  </a>
  <a href="https://github.com/bioinformatics-ufsc/FastProtein">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" width="100" height="30"/>
  </a>
</p>


<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS ------------------------------------------------------- -->
---
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
