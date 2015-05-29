#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

# paths
APP=KMeans
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for prepare
NUM_OF_POINTS=1000
NUM_OF_CLUSTERS=10
DIMENSIONS=20
SCALING=0.6
NUM_OF_PARTITION=10
#NUM_OF_SAMPLES=20000000
#SAMPLES_PER_INPUTFILE=4000000
#SAMPLES_PER_INPUTFILE=6000000
MAX_ITERATION=5
NUM_RUN=1
NUM_TRIALS=1


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster
numPar=${NUM_OF_PARTITION}
nexe=10
dmem=1g
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=6
memoryFraction=0.48
#0.001 rdd=0
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

rdd_compression=false
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"


YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"



function print_config(){
	local output=$1
	echo "${APP}_config memoryFraction ${memoryFraction} NUM_OF_POINTS ${NUM_OF_POINTS} NUM_OF_CLUSTERS ${NUM_OF_CLUSTERS} DIMENSIONS ${DIMENSIONS} SCALING \
	${SCALING} NUM_OF_PARTITION ${NUM_OF_PARTITION}  \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec} " >> ${output}
}
