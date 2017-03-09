#!/bin/bash


${SPARK_HOME}/bin/spark-submit \
        --class com.ibm.sparktc.sparkbench.cli.CLIKickoff \
        --master ${SPARK_MASTER_HOST} \
        --driver-memory 1g \
        --total-executor-cores 2 \
        --executor-memory 4g \
        ${JAR} \
        "$@"