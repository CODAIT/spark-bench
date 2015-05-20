#!/bin/bash
# paths
#INPUT_HDFS=${DATA_HDFS}/LogisticRegression/Input-x10

APP=LogisticRegression
#APP_DIR=${DATA_HDFS}/${APP}
#INPUT_HDFS=${DATA_HDFS}/SVM/Input
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

# for prepare #32G date size=400 million examples; 1G= 12.5
NUM_OF_EXAMPLES=300000000
NUM_OF_FEATURES=4
NUM_OF_PARTITIONS=720
ProbOne=0.2
EPS=0.5

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
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${NUM_OF_PARTITIONS}"

#kryo_app=LogisticRegressionApp
  #--conf spark.kryo.classesToRegister=${kryo_app}
#SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem} --conf spark.default.parallelism=${num_task}  --conf spark.kryo.classesToRegister=${kryo_app}"
YARN_OPT=""





# for running
MAX_ITERATION=3
NUM_TRIALS=1

#input benreport
function print_config(){
	local output=$1
	echo "LogisticRegressionConfig nexample ${NUM_OF_EXAMPLES} nCluster ${NUM_OF_FEATURES} EPS ${EPS} \
	npar ${NUM_OF_PARTITIONS} ProbOne ${ProbOne} numiter ${MAX_ITERATION} memoryFraction ${memoryFraction} \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}">> ${output}
}
