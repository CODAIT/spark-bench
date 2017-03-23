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


JAR="${DIR}/../common/target/Common-1.0.jar"
CLASS="DataGen.src.main.scala.GraphDataGen"

OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${numV} ${NUM_OF_PARTITIONS} ${mu} ${sigma}"

START_TS=`get_start_ts`;

setup
START_TIME=`timestamp`
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat"
res=$?;

END_TIME=`timestamp`

DU ${INPUT_HDFS} SIZE 

get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gen ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
teardown
exit 0


