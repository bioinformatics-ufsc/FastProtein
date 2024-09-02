./build.sh
#Change your shared local folder
echo "Enter the input directory: "
read SHARED_FOLDER
docker rm FastProtein
docker run -it --name FastProtein -p 5000:5000 -v $SHARED_FOLDER:/FastProtein/runs bioinfoufsc/fastprotein:latest
