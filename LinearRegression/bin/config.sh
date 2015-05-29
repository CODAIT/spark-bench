#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

#paths
APP=LinearRegression
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# Application parameters #1million points have 200M data size 
NUM_OF_EXAMPLES=40000 #0000
NUM_OF_PARTITIONS=720
NUM_OF_FEATURES=4
EPS=0.5
INTERCEPTS=0.1
MAX_ITERATION=3
NUM_TRIALS=1

# Either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
num_task=20
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
	echo "LinearRegressionConfig nexe $nexe drivermem $dmem exe_mem $emem exe_core $ecore nexample ${NUM_OF_EXAMPLES} nCluster ${NUM_OF_FEATURES} EPS ${EPS} npar ${NUM_OF_PARTITIONS} Intercepts ${INTERCEPTS} niter ${MAX_ITERATION} memoryFraction ${memoryFraction}" >> ${output}
}
