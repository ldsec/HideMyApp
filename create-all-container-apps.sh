#!/bin/sh

cmd="sh create-container-app.sh" 
pkgname="org.hma.app"

# List of mHealth apps to be included in container apps. 
apps=(
"com.fueled.cancernet"
"com.jacksontempra.apps.whatsup"
"com.sanovation.catchmypain.phone"
"com.EAGINsoftware.dejaloYa"
"com.mobilebreeze.AsthmaMD"
)

# Delete any container apps in the directory
rm HMA-Apps/* 

# Create new container apps 
i=1
for app in "${apps[@]}"; do
    echo "\n **************************************************************\n"
    echo " Creating container for $app..."
    echo "\n **************************************************************\n"
    
    $cmd mhealth-Apps/$app $pkgname$i
    
    ((i++))
done

