#!/bin/bash

# see https://github.com/Polidea/RoboSpock/issues/16

mkdir temp
cd temp

  git clone https://github.com/pjakubczyk/robospock-plugin.git
  cd robospock-plugin

    ./gradlew -x :robospock-plugin:signArchives :robospock-plugin:install

  cd ..
cd ..
