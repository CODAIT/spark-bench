#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

# =============== path check ===============

DU ${INPUT_HDFS} SIZE 


APP=sql_rddRelation
JAR="${DIR}/target/SQLApp-1.0.jar"
CLASS="src.main.scala.RDDRelation"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${NUM_OF_PARTITIONS}  "
if  [ $# -ge 1 ] && [ $1 = "hive" ]; then	
	
	APP=sql_hive
    JAR="${DIR}/target/SQLApp-1.0.jar"
	CLASS="src.main.scala.HiveFromSpark"
	OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${NUM_OF_PARTITIONS} "
fi



echo "========== running ${APP} benchmark =========="

setup
for((i=0;i<${NUM_TRIALS};i++)); do

	RM ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
START_TS=`get_start_ts`;
	export logf=${BENCH_NUM}/${APP}_run_${START_TS}.dat
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} $JAR ${OPTION} 2>&1|tee $logf"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
teardown
exit 0

if [[ -z "$JAR" ]]; then
  echo "Failed to find Spark examples assembly in  ${SPARK_HOME}/examples/target" 1>&2
  echo "You need to build Spark before running this program" 1>&2
  exit 1
fi
