#!/bin/bash


APP=PregelOperation
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

nexe=10
dmem=5000m
ecore=6
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
YARN_OPT=""

memoryFraction=0.5
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}"
YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"

# for preparation 
numV=5000 #0
numPar=100
mu=4.0
sigma=1.3
# for running

NUM_TRIALS=1

#input benreport
function print_config(){
	local output=$1
	echo "${APP}_config memoryFraction ${memoryFraction} numV ${numV} numPar ${numPar} mu ${mu} sigma ${sigma}" >> ${output}
}
