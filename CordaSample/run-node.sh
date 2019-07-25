#!/bin/bash

# Esse script executa o nó do corda
# Importante executar todos os nós configurados em deployNodes (Notary, NodeA, NodeB, etc)

cd build/nodes/$1

java -jar corda.jar

cd ../../../

clear

