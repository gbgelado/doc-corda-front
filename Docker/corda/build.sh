#!/bin/bash

docker stop corda

rm -rf mnt/corda/CorDapps/*

cp -Rp ../../CordaSample/build/* mnt/corda/CorDapps/

docker start corda