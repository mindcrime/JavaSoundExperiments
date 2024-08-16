#!/bin/bash

echo $1

if [ -z $1 ]
then

    java -cp target/javasound1-1.0.jar -Dloader.main=org.fogbeam.example.jsound.ListAppsApplication  org.springframework.boot.loader.PropertiesLauncher

else
    
    java -cp target/javasound1-1.0.jar -Dloader.main=org.fogbeam.example.jsound.$1Application  org.springframework.boot.loader.PropertiesLauncher

fi

