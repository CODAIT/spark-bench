#!/bin/bash
#==kmeans==
# paths
APP=KMeans
#INPUT_HDFS=${DATA_HDFS}/KMeans/Input-40g
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# either stand alone or yarn cluster
# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}
#APP_MASTER=yarn-cluster 
nexe=10
dmem=1g
emem=1g
[ -n "$EXECUTOR_GLOBAL_MEM"  ] && emem=$EXECUTOR_GLOBAL_MEM
ecore=6
memoryFraction=0.48
#0.001 rdd=0
[ -n "$MEM_FRACTION_GLOBAL"  ] && memoryFraction=${MEM_FRACTION_GLOBAL}

rdd_compression=false
spark_ser=KryoSerializer
rddcodec=lzf
SPARK_OPT="--conf spark.storage.memoryFraction=${memoryFraction} --conf spark.executor.memory=${emem}  --conf spark.serializer=org.apache.spark.serializer.${spark_ser} --conf spark.rdd.compress=${rdd_compression} --conf spark.io.compression.codec=${rddcodec} --conf spark.default.parallelism=${numPar}"


YARN_OPT=""
#YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"

# for prepare #1million points=200m data set size
NUM_OF_POINTS=200000 #000
NUM_OF_CLUSTERS=10
DIMENSIONS=10
SCALING=0.6
NUM_OF_PARTITIONS=460



#NUM_OF_SAMPLES=20000000
#SAMPLES_PER_INPUTFILE=4000000
#SAMPLES_PER_INPUTFILE=6000000


# for running
MAX_ITERATION=3
NUM_RUN=1
NUM_TRIALS=1
CONF_OPT="--conf spark.shuffle.spill=false --conf spark.default.parallelism=10"
#input benreport
function print_config(){
	local output=$1
         echo "kmeans_config npoint ${NUM_OF_POINTS} nCluster ${NUM_OF_CLUSTERS} dim ${DIMENSIONS} npar ${NUM_OF_PARTITIONS} niter ${MAX_ITERATION}" >> ${output}
}
