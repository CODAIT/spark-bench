#!/bin/bash

APP=PageRank
APP_DIR=${DATA_HDFS}/${APP}
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for preparation 
numV=1920000
numPar=400
mu=4.0
sigma=1.3
# for running
MAX_ITERATION=12
TOLERANCE=0.001
RESET_PROB=0.15
NUM_TRIALS=1

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
nexe=10
dmem=1g
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=6
memoryFraction=0.48
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
rdd_compression=false
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"



#input benreport
function print_config(){
	local output=$1
	echo "${APP}_config memoryFraction ${memoryFraction} numV ${numV} numPar ${numPar} mu ${mu} sigma \
	${sigma} numiter ${MAX_ITERATION} tol ${TOLERANCE} reset_prob ${RESET_PROB} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec} " >> ${output}
}
