#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

APP=DecisionTree
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS_Classification=${DATA_HDFS}/${APP}/Output-Classification
OUTPUT_HDFS_Regression=${DATA_HDFS}/${APP}/Output-Classification



# for gen_data.sh;  200M data size = 1 million points
NUM_OF_EXAMPLES=5000 #00000
NUM_OF_PARTITIONS=120 #0
memoryFraction=0.01
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}
NUM_OF_FEATURES=6

# for run.sh
NUM_OF_CLASS_C=10
impurityC="gini"
maxDepthC=5
maxBinsC=100
modeC="Classification"
#${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}

NUM_OF_CLASS_R=10
impurityR="variance"
maxDepthR=5
maxBinsR=100
modeR="Regression"
#${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR}

MAX_ITERATION=3
NUM_TRIALS=1


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
num_task=20
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
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} \
--conf spark.executor.memory=${emem} \
 --conf spark.serializer=org.apache.spark.serializer.${spark_ser} \
 --conf spark.rdd.compress=${rdd_compression} \
 --conf spark.io.compression.codec=${rddcodec} \
 --conf spark.default.parallelism=${num_task} "
#YARN_OPT="--num-executors $nexe --driver-memory $dmem \
#--executor-memory $emem --executor-cores $ecore"


#input benreport
function print_config(){
	local output=$1
	echo "DecisionTreeC-Config memoryFraction ${memoryFraction} \
	NUM_OF_EXAMPLES ${NUM_OF_EXAMPLES} \
	NUM_OF_FEATURES ${NUM_OF_FEATURES} \
	NUM_OF_PARTITIONS ${NUM_OF_PARTITIONS} \
	class ${NUM_OF_CLASS_C} ${NUM_OF_CLASS_R} impurity ${impurityC}  ${impurityR} maxDepth ${maxDepthC} ${maxDepthR} maxbin ${maxBinsC} ${maxBinsR} mode ${modeC} ${modeR}" >> ${output}		
}

#### unused ####
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi
