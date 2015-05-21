#!/bin/bash

APP=LogisticRegression
APP_DIR=${DATA_HDFS}/${APP}
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi



# Application parameters #32G date size=400 million examples; 1G= 12.5
NUM_OF_EXAMPLES=25000000
NUM_OF_FEATURES=20
NUM_OF_PARTITIONS=720
ProbOne=0.2
EPS=0.5
MAX_ITERATION=3
NUM_TRIALS=1

# Either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
nexe=60
dmem=1g
ecore=1
emem=6g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
memoryFraction=0.5
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
rdd_compression=false
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} \
--conf spark.executor.memory=${emem}  \
--conf spark.serializer=org.apache.spark.serializer.${spark_ser} \
--conf spark.rdd.compress=${rdd_compression} \
--conf spark.io.compression.codec=${rddcodec} \
--conf spark.default.parallelism=${num_task} "
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"


#input benreport
function print_config(){
	local output=$1
	echo "${APP}-Config nexample ${NUM_OF_EXAMPLES} nCluster ${NUM_OF_FEATURES} EPS ${EPS} \
	npar ${NUM_OF_PARTITIONS} ProbOne ${ProbOne} numiter ${MAX_ITERATION} memoryFraction ${memoryFraction} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}">> ${output}
}

