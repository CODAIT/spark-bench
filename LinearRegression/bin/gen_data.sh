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

START_TS=`get_start_ts`

setup
START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_gendata_${START_TS}.dat
res=$?
END_TIME=`timestamp`
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
    get_config_fields >> ${BENCH_REPORT}
    echo " s ${START_TIME} et ${END_TIME} s ${SIZE} sts ${START_TS} res ${res}"
	print_config  ${APP}-gendata ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT}
teardown

exit 0



