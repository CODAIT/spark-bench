#!/bin/bash


bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== Running ${APP} Workload =========="

DU ${INPUT_HDFS} SIZE 
CLASS="SVM.src.main.java.SVMApp"
OPTION=" ${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${MAX_ITERATION} ${STORAGE_LEVEL}"
JAR="${DIR}/target/SVMApp-1.0.jar"


setup
for((i=0;i<${NUM_TRIALS};i++)); do
	# path check	
	RM ${OUTPUT_HDFS}
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
teardown
exit 0

