#!/bin/bash
#==SVM==
# paths
APP=SVM
APP_DIR=${DATA_HDFS}/${APP}
INPUT_HDFS=${DATA_HDFS}/LogisticRegression/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for prepare #600M example=40G
NUM_OF_EXAMPLES=300000 #300000000
NUM_OF_FEATURES=2
NUM_OF_PARTITIONS=720
# for running
MAX_ITERATION=3
NUM_TRIALS=1


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
nexe=60
dmem=1024m
ecore=1
emem=1024m
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
memoryFraction=0.79
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
rdd_compression=false
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${num_task} "
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"



#input benreport
function print_config(){
	local output=$1
	echo "SVMConfig nexample ${NUM_OF_EXAMPLES} nCluster ${NUM_OF_FEATURES} numPartition ${NUM_OF_PARTITIONS} niter ${MAX_ITERATION} memoryFraction ${memoryFraction} RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}" >> ${output}
	
}
