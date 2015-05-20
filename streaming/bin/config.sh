#!/bin/bash


APP=streaming
INPUT_HDFS=${DATA_HDFS}/LinearRegression/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output

if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 

nexe=60
dmem=1000m
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=1
memoryFraction=0.6
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

#SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}"
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} \
--conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
--conf spark.executor.memory=${emem} \
--conf spark.driver.memory=${dmem}"
#YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"

# for running
NUM_TRIALS=1
# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
# PageViewStream MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
subApp=PageViewStream
#subApp=TwitterPopularTags
optApp=80000000
#input benreport
function print_config(){
	local output=$1
	echo "${APP}_${subApp}_config memoryFraction ${memoryFraction}  " >> ${output}
}



if [ 1 -eq 0 ];then
	# for preparation 
	numV=4000000
	numPar=400
	mu=4.0
	sigma=1.3
	
	# for running
	MAX_ITERATION=3
	TOLERANCE=0.001
	RESET_PROB=0.15
	

fi
