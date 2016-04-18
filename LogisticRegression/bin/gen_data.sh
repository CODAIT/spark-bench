#!/bin/bash


# DIR path
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


echo "========== preparing LogisticRegression data =========="
# paths check
RM ${INPUT_HDFS}

# generate data
JAR="${DIR}/target/LogisticRegressionApp-1.0.jar"
CLASS="LogisticRegression.src.main.java.LogisticRegressionDataGen"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${ProbOne} ${NUM_OF_PARTITIONS}"

START_TS=`get_start_ts`;
setup
START_TIME=`timestamp`
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LogisticRegression_gendata_${START_TS}.dat"
res=$?
END_TIME=`timestamp`

DU ${INPUT_HDFS} SIZE 

get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gendata ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT}
teardown

exit 0


# compress check
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    COMPRESS_OPT="-compress true \
        -compressCodec $COMPRESS_CODEC \
        -compressType BLOCK "
else
    COMPRESS_OPT="-compress false"
fi
