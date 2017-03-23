#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running ${APP} benchmark =========="

DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



DU ${INPUT_HDFS} SIZE 
JAR="${DIR}/target/DecisionTreeApp-1.0.jar"
CLASS="DecisionTree.src.main.java.DecisionTreeApp"


setup
for((i=0;i<${NUM_TRIALS};i++)); do		
	# classification
	RM ${OUTPUT_HDFS_Classification}
	purge_data "${MC_LIST}"	
	OPTION=" ${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS_Classification} ${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}"
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/DecisionTree_run_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
teardown

exit 0


######################## unused ########################
if [ 1 -eq 0 ]; then
# Regression
	RM ${OUTPUT_HDFS_Regression}
	purge_data "${MC_LIST}"	
	OPTION=" ${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS_Regression} ${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR} "
	START_TIME=`timestamp`
START_TS=`get_start_ts`;
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/DecisionTree_run_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
fi

