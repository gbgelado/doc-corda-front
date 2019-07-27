#!/bin/bash

cd /tmp

rm -rf /tmp/DemoCordaNodes

tar -xjvf DemoCordaNodes.tar.bz2

#cp -R /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodesBKP

docker stop corda

cp /tmp/DemoCordaNodes/nodes/OrgA/cordapps/*.jar /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes/nodes/OrgA/cordapps/
cp /tmp/DemoCordaNodes/nodes/OrgB/cordapps/*.jar /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes/nodes/OrgB/cordapps/
cp /tmp/DemoCordaNodes/nodes/SYS/cordapps/*.jar /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes/nodes/SYS/cordapps/
cp /tmp/DemoCordaNodes/nodes/Notary/cordapps/*.jar /bluchain/democorda/docker/corda/mnt/corda/DemoCordaNodes/nodes/Notary/cordapps/

docker restart corda

rm -rf /tmp/DemoCordaNodes*

