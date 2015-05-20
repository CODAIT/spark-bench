#!/bin/bash


APP=SVDPlusPlus
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi


# for preparation 
numV=50000
numPar=800
mu=4.0
sigma=1.3
# for running
NUM_ITERATION=1
RANK=50
MINVAL=0.0
MAXVAL=5.0
GAMMA1=0.007
GAMMA2=0.007
GAMMA6=0.005
GAMMA7=0.015
NUM_TRIALS=1


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

nexe=10
dmem=1g
ecore=1
emem=1024m
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
memoryFraction=0.5
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}



rdd_compression=true
spark_ser=KryoSerializer
rddcodec=lz4
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"


#input benreport
function print_config(){
	local output=$1
	echo "${APP}_config core ${ecore} memoryFraction ${memoryFraction} numV ${numV} \
	numPar ${numPar} mu ${mu} sigma ${sigma} iter ${NUM_ITERATION} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}" >> ${output}
}
