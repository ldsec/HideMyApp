# HideMyApp (HMA)

This file describes how to install and evaluate the HMA prototype. HMA is a solution for hiding the
presence of sensitive mobile apps, such as mHealth apps, from other apps installed in the same
Android smartphone. HMA does not requires changes to the Android OS or the apps.  With HMA, users
launch a sensitive app inside the context of a container app, without requiring the sensitive app to
be installed. A container app is a dynamically generated wrapper around the Android application
package (APK) of the sensitive app, and it is designed in such a way that the sensitive app cannot
be fingerprinted from static metadata. To launch the APK from the container app, HMA relies on
techniques such as the dynamic loading of compiled source code and app resources from the APKs and
user-level app virtualization techniques.

**Note**: This prototype shows how the HMA App manager and the container apps works, assuming that the
container APKs have been already created and downloaded in the phone (they are in a local directory
in the /sdcard). That is, this prototype does not show the interaction/communications with an online
HMA store, as such functionality cannot be made publicly available in a short notice (such
functionality is also not critical for the proof of concept). Still, this prototype shows how the
container apps can be created by the HMA App store. 

## Video
For a quick overview, you can find a short video showing the HMA prototype main features in the
video directory:

video/HMA-Demo.mp4

## Requirements
To install and evaluate the HMA prototype, you need the following:

- A Linux or MacOS computer (tested on MacOS v. 10.13.16)
- An Android smartphone (tested on Android 6.x, 7.x and, 8.x) and a USB cable to connect the
  smartphone to the computer.
- The Android SDK, particularly Android SDK tools (tested with v. 26.1.1) and Android platform tools
  (tested with v. 28.0.1)
- Java SDK/JRE 1.7, 1.8  or 1.9 (it will not work with Java 1.10 or newer).
- Apktool to decompile the APK files (tested with v2.3.3)
- Python 2 (tested with v. 2.7.15)

## Evaluation Steps

0. Download all the files in this Droxpbox directory to your computer. 

1. Connect your phone to the computer (USB cable) and check that the ADB command works. Then, copy
   the HMA-Apps directory into the sdcard directory of your phone:

    adb push HMA-Apps /sdcard/

    This directory contains the container apps of 5 mHealth apps. The original APKs of these apps
    can be found in the mHealth-apps directory. As stated above, in this prototype we assume that the
    container apps have been already created by the HMA App store and downloaded in the phone. 
    
    **Optional**: You can recreate the container apps using this script:

    sh create-all-containers.sh
    
    This script will delete the container files in the HMA-Apps directory and create new ones. You
    must satisfy all the dependency-requirements to perform this step.   

2. Install the HMA Manager app in the phone using its APK file (HMA-Manager.apk):

    adb install HMA-manager.apk

3. Run the HMA Manager. The first time it is launched, the HMA Manager should request permission to
   access external storage; please grant it.  Press the  "INSTALL APP" button, and select an app
   from the list (the apps in the /sdcard/HMA-Apps directory). After selecting the app, you should
   be prompted to install the container app of the selected app (e.g., HMA - app1). Select
   "Install".  The first time an app is installed using the HMA Manager, you will be prompted for a
   permission to allow the HMA Manager to install apps ("Allow installation form this source").
   Select "Allow". Select "Done" once the installation finishes.

4. To launch an installed app, run the HMA Manager and select the "LAUNCH APP" button. You should
   now see the container apps that have been installed using the manager. Select the app to launch
   it. The first time an app is launched via its container there is a small delay required to setup
   the app. You can now use the mHealth app in a similar ways as if it was installed directly (note
   that some functionality might still fail due to limitations of the virtualization techniques used
   by HMA or by specific details of your phone, this is work in progress).

5. You can check that there are not references in the OS APIs to the name of the mHealth app, only
   references to the generic name provided by HMA (org.hma.app1). For example, you can go to Android
   settings->apps and see the list of installed apps. Alternatively, you can execute the following
   command to list the installed apps:

   adb shell pm list packages

   You can use the package manager command (pm) to inquire more details about the installed apps
   (see the pm command documentation).

## Testing your own APKs

You can create a container for your own APKs using the following script:

    ./create-container-app.sh [PATH and NAME OF THE mHealth APK WITHOUT THE .APK EXTENSION] [GENERIC PACKAGE NAME FOR THE CONTAINER APP]

For example, if the APK filename is com.test.apk and it is located in the mHealth-apps directory:

    ./create-container-app.sh mHealth-apps/com.test org.hma.app1

The container app will be copied in the HMA-Apps directory.  You can install this app directly
(using "adb install")  or via the HMA Manager app. For the latter, you need to copy the new
container APK in the /sdcard/HMA-Apps folder in your phone. Your app will be shown as unknown by the
HMA manager app, as the it does not have access to the metadata (icon, name) of the original mHealth
app (in our prototype, the HMA-Manager app already has the metadata associated with the five
mHealth apps evaluated. In practice, the HMA App Store should send this information to the HMA
Manager app each time an app is installed). 

Note that there is a significant chance that the app you selected may not work immediately with HMA.
The more complex the app, the higher the chance that it will not be compatible with the current
version of HMA. HMA is designed to work with the functionalities required by mHealth apps, further
work is needed to support other type of apps and functionalities.


## Description of prototype files

- README.md: This file
- HMA-Apps: Directory with the container apps. This directory should be copied in the phone's sdcard
  so the HMA-Manager app can locate the container apps.
- HMA-Manager: Java source code of the HMA Manager app.
- HMA-Manager.apk: APK file for the HMA Manager app.
- base-container: template files for creating the container app.
- containers: temporal directory to store the container apps before building the corresponding APK
  (deleted once the APK is created).
- create-container-app.sh: script that takes as input an app APK file and outputs a container app
  with the chosen generic name. This represents the main operation in the HMA App store.  
- create-all-container-apps.sh: creates five container APKs based on the mHealth APKs used in this
  prototype (in the mHealth-apps directory)
- mHealth-apps: contains examples of mHealth APKs.
- manifest-stubs: contains Python 2 scripts to create the container APK.
- video/HMA-Demo.mp4: short video showing the HMA prototype in action. 

