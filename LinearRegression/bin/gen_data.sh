#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`


DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


echo "========== preparing ${APP} data =========="

${RM} -r ${INPUT_HDFS}

JAR="${DIR}/target/LinearRegressionApp-1.0.jar"
CLASS="LinearRegression.src.main.java.LinearRegressionDataGen"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${INTERCEPTS} ${NUM_OF_PARTITIONS}"

START_TS=get_start_ts

setup
START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_gendata_${START_TS}.dat

END_TIME=`timestamp`
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "${APP}-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
teardown

exit 0



