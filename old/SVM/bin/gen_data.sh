#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

JAR="${DIR}/target/SVMApp-1.0.jar"
CLASS="SVM.src.main.scala.SVMDataGen"
OPTION=" ${APP_MASTER} ${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "
RM ${INPUT_HDFS}

# paths check

setup
START_TS=`get_start_ts`;
START_TIME=`timestamp`
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat"
res=$?;
END_TIME=`timestamp`
DU ${INPUT_HDFS} SIZE 
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gen ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
teardown
exit 0



