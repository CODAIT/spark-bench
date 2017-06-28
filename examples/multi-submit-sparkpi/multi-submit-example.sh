#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname ${WHEREILIVE})

bin/spark-bench.sh ${BASEDIR}/multi-submit-sparkpi.conf