#!/bin/bash
# paths
APP=MF
#INPUT_HDFS=${DATA_HDFS}/MF/Input-ml/ml-10M100K
#INPUT_HDFS=${DATA_HDFS}/MF/Input-amazon
INPUT_HDFS=${DATA_HDFS}/MF/Input
OUTPUT_HDFS=${DATA_HDFS}/MF/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# for prepare #200M=1million points
m=500 
n=200
rank=10
trainSampFact=0.9
noise=false
sigma=0.1
test=false
testSampFact=0.1
numPar=400
##### for running ####
MAX_ITERATION=3 #90 # 3
LAMBDA=0.01
NUM_RUN=1
NUM_TRIALS=1

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

nexe=10
dmem=1g
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=6
memoryFraction=0.015
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

rdd_compression=true
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"


#YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"

# println("Usage: MFDataGenerator " +
#        "<master> <outputDir> [m] [n] [rank] [trainSampFact] [noise] [sigma] [test] [testSampFact]")


#input benreport
function print_config(){
	local output=$1
	echo "MFConfig m $m n $n memoryfraction $memoryFraction core $ecore rank $rank \
	trainSampFact $trainSampFact noise $noise sigma $sigma test $test testSampFact $testSampFact \
	RDDcomp ${rdd_compression} ${spark_ser} ${rddcodec}" >> ${output}
}
