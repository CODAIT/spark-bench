#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

APP=sql
APP_DIR=${DATA_HDFS}/${APP}
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for preparation 
#numV=10000000
numB=14
batch=500000
numPar=1600
# for running
MAX_ITERATION=3
TOLERANCE=0.001
RESET_PROB=0.15
NUM_TRIALS=1

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

nexe=60
dmem=1g
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=1
#50%rdd for 40g data 0.152 7g
memoryFraction=0.004
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}


rdd_compression=false
spark_ser=JavaSerializer
rddcodec=snappy
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} \
--conf spark.executor.memory=${emem} \
--conf spark.serializer=org.apache.spark.serializer.${spark_ser} \
--conf spark.rdd.compress=${rdd_compression} \
--conf spark.io.compression.codec=${rddcodec} \
--conf spark.default.parallelism=${numPar}"

#YARN_OPT=" "
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"




#input benreport
function print_config(){
	local output=$1
	echo "${APP}_config memoryFraction ${memoryFraction} vcores ${executor_cores} \
	numV ${numV} numPar ${numPar} mu ${mu} sigma ${sigma} \
	iter ${MAX_ITERATION} tol ${TOLERANCE} reset_prob ${RESET_PROB} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}" >> ${output}
}
