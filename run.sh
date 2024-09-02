#Change your shared local folder
echo "Enter the input directory: "
read SHARED_FOLDER
docker run -it --name FastProtein -p 5000:5000 -v $SHARED_FOLDER:/fastprotein bioinfoufsc/fastprotein:latest
