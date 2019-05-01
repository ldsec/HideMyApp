#!/bin/sh
#The script takes the apk as input

APK_FULL_PATH=$1.apk
NEW_PACKAGE_NAME=$2
UNZIPPED_APK_FOLDER=$1
BASE_CONTAINER=./base-container

if [ ! -f $APK_FULL_PATH ]; then
    echo "Error: The apk file does not exist!"
fi

#Get the name of the APK
IFS='/' read -r -a array <<< "$1"
APK_NAME=${array[${#array[@]}-1]}
echo $APK_NAME

#Unzip the APK file
echo 'Unzip the apk file'
apktool d $APK_FULL_PATH -o $UNZIPPED_APK_FOLDER

#Copy the BaseContainer app

START=$(date +%s)

if [ ! -d ./containers ]; then
    mkdir ./containers
fi

echo 'Create the folder for the container app'
CONTAINER_FOLDER=./containers/$APK_NAME
mkdir $CONTAINER_FOLDER
cp -r $BASE_CONTAINER/* $CONTAINER_FOLDER/ 

#Create local.properties file with the path to the SDK
#Infer SDK path from adb command first


SDK_PATH_ADB=$(which adb)
SDK_PATH_ADB=${SDK_PATH_ADB%"/platform-tools/adb"}
read -p "Please enter the path to your Android SDK [$SDK_PATH_ADB]: " SDK_PATH
SDK_PATH=${SDK_PATH:-$SDK_PATH_ADB}
touch $CONTAINER_FOLDER/local.properties
echo "sdk.dir=$SDK_PATH" > $CONTAINER_FOLDER/local.properties

#Replace the Manifest file in DroidPlugin lib
echo 'Create Manifest file for DroidPlugin'
CONTAINER_MANIFEST=$CONTAINER_FOLDER/DroidPlugin/AndroidManifest.xml
APK_MANIFEST=$UNZIPPED_APK_FOLDER/AndroidManifest.xml

if [ -f AndroidManifest.xml ]; then
    rm AndroidManifest.xml
fi

python2 ./manifest-stubs/create_new_manifest.py $APK_MANIFEST $CONTAINER_MANIFEST AndroidManifest.xml

cp -f AndroidManifest.xml $CONTAINER_MANIFEST
rm AndroidManifest.xml

python2 ./manifest-stubs/create_stubs_java.py $CONTAINER_MANIFEST
python2 ./manifest-stubs/create_service_stubs_java.py $CONTAINER_MANIFEST


#Copy the apk to the asset
if [ ! -d $CONTAINER_FOLDER/app/src/main/assets ]; then
	mkdir $CONTAINER_FOLDER/app/src/main/assets
fi
cp $APK_FULL_PATH $CONTAINER_FOLDER/app/src/main/assets/target.apk

if [ -d $UNZIPPED_APK_FOLDER/assets ]; then
    cp -r $UNZIPPED_APK_FOLDER/assets/* $CONTAINER_FOLDER/app/src/main/assets
fi

python2 ./manifest-stubs/handle_package_name.py $CONTAINER_FOLDER $NEW_PACKAGE_NAME

rm $CONTAINER_FOLDER/app/src/main/java/org/hma/hma_app1/*.java

echo 'Compiling the container app'
cd $CONTAINER_FOLDER
#./gradlew --stop
sh gradlew assembleDebug

cd ../../

if [ ! -f  $CONTAINER_FOLDER/app/build/outputs/apk/app-debug.apk ]; then
    echo "APK file does not exist!"
fi

cp $CONTAINER_FOLDER/app/build/outputs/apk/app-debug.apk HMA-apps/$NEW_PACKAGE_NAME.apk

END=$(date +%s);

echo 'Remove the unzipped apk folder'
rm -r $UNZIPPED_APK_FOLDER

