#!/bin/bash

cd /corda
  
#nodes=( "NodeA" )
#apis=( "NodeA" )

nodes=( "NodeA" "NodeB" )
apis=( "NodeA" "NodeB" )

for i in "${nodes[@]}"
do
   echo "Node: $i"
   cd CorDapps/nodes/$i
   java -jar corda.jar &
   cd ../../../
done

for i in "${apis[@]}"
do
   echo "Api: $i"
   cd CorDapps/nodes/$i
   java -jar corda-webserver.jar &
   cd ../../../
done

cd CorDapps/nodes/Notary
java -jar corda.jar

