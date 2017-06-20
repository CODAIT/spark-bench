#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname ${WHEREILIVE})

bin/spark-launch.sh ${BASEDIR}/multi-submit-sleep.conf