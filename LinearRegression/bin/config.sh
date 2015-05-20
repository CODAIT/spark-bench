#!/bin/bash
#==Linear Regression== 
#paths
INPUT_HDFS=${DATA_HDFS}/LinearRegression/Input
OUTPUT_HDFS=${DATA_HDFS}/LinearRegression/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 


nexe=60
dmem=1088m
ecore=8
emem=1088m
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
memoryFraction=0.5
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

#YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"

# for prepare #1million points have 200M data size 
NUM_OF_EXAMPLES=40000 #0000
NUM_OF_PARTITIONS=720
NUM_OF_FEATURES=4
EPS=0.5
INTERCEPTS=0.1

# for running
MAX_ITERATION=3
NUM_TRIALS=1

#input benreport
function print_config(){
	local output=$1
	echo "LinearRegressionConfig nexe $nexe drivermem $dmem exe_mem $emem exe_core $ecore nexample ${NUM_OF_EXAMPLES} nCluster ${NUM_OF_FEATURES} EPS ${EPS} npar ${NUM_OF_PARTITIONS} Intercepts ${INTERCEPTS} niter ${MAX_ITERATION} memoryFraction ${memoryFraction}" >> ${output}
}
