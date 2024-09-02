./build.sh
#Change your shared local folder
docker rm FastProtein
docker run -it --name FastProtein -p 5000:5000 -v $(pwd)/web:/FastProtein/web bioinfoufsc/fastprotein:latest
