#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

# paths check
${RM} -r ${INPUT_HDFS}

# generate data
START_TS=`get_start_ts`;

setup


JAR="${DIR}/target/PageRankApp-1.0.jar"
CLASS="src.main.scala.pageRankDataGen"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${numV} ${NUMBER_OF_PARTITIONS} ${mu} ${sigma}"
START_TIME=`timestamp`

exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat

END_TIME=`timestamp`
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "${APP}_gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
teardown
exit 0



