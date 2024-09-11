docker build -t bioinfoufsc/proteomic-interpro:latest -f Dockerfile-proteomic-interpro .
docker build -t bioinfoufsc/fastprotein-interpro:latest -f Dockerfile-interpro .
docker tag bioinfoufsc/fastprotein-interpro:latest bioinfoufsc/fastprotein:interpro
