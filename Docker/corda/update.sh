#!/bin/bash

cd /tmp

rm -rf /tmp/DemoCordaNodes

tar -xjvf DemoCordaNodes.tar.bz2

#cp -R /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodesBKP

docker stop corda

rm -rf /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes*

cp -R /tmp/DemoCordaNodes /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes
cp -R /tmp/DemoCordaNodes /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodesClean

docker restart corda

rm -rf /tmp/DemoCordaNodes*

