#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

RM ${INPUT_HDFS}


JAR="${DIR}/target/KMeansApp-1.0.jar"
CLASS="KmeansDataGenJava"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_POINTS} ${NUM_OF_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"


setup
START_TS=`get_start_ts`;
START_TIME=`timestamp`
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/TEMP_gendata_${START_TS}.dat"
res=$?;
END_TIME=`timestamp`
DU ${INPUT_HDFS} SIZE 

get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
teardown

exit 0


