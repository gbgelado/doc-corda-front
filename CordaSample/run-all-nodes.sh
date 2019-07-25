#!/bin/bash

# Executa todos os nós e todas as apis

nodes=( "Notary" "NodeA" "NodeB" )
apis=( "NodeA" "NodeB" )

for i in "${nodes[@]}"
do
   echo "Node: $i"
   cd build/nodes/$i
   java -jar corda.jar &
   cd ../../../
done

for i in "${apis[@]}"
do
   echo "Api: $i"
   cd build/nodes/$i
   java -jar corda-webserver.jar &
   cd ../../../
done