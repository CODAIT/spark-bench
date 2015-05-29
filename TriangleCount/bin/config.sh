#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

APP=TriangleCount
APP_DIR=${DATA_HDFS}/${APP}
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for preparation 
numV=10000 #100000
numPar=60
mu=4.0
sigma=1.3
# for running
NUM_TRIALS=1


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
nexe=10
dmem=1g
ecore=6
emem=6g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
memoryFraction=0.1
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

rdd_compression=true
spark_ser=KryoSerializer
rddcodec=lz4
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"


#input benreport
function print_config(){
	local output=$1
	echo "${APP}_config memoryFraction ${memoryFraction} core $executor_cores numV ${numV} numPar ${numPar} mu ${mu} sigma ${sigma} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}" >> ${output}
}
