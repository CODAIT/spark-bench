#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

# paths check
RM ${INPUT_HDFS}

# generate data
START_TS=`get_start_ts`;

setup
genOpt="small"
if [ $genOpt = "small" ]; then
	JAR="${DIR}/target/PageRankApp-1.0.jar"
	CLASS="src.main.scala.pageRankDataGen"
	OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${numV} ${NUM_OF_PARTITIONS} ${mu} ${sigma}"
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat"
res=$?;
elif [ $genOpt = "large" ]; then
	START_TIME=`timestamp`
	MKDIR ${APP_DIR}
	MKDIR ${INPUT_HDFS}
	srcf=${DATASET_DIR}/web-Google.txt	#srcf=${DATASET_DIR}/BigDataGeneratorSuite/Graph_datagen/AMR_gen_edge_24.txt
	CPFROM $srcf ${INPUT_HDFS}
else
	echo "error"
	exit 1
fi

END_TIME=`timestamp`
DU ${INPUT_HDFS} SIZE 
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gen ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
teardown
exit 0



