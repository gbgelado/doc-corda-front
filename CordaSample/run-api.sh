#!/bin/bash

# Esse script executa a api de um determinado nó do corda

cd build/nodes/$1

java -jar corda-webserver.jar

cd ../../../

clear

