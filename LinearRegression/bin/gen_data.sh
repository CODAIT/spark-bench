#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== preparing ${APP} data =========="

DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}

JAR="${DIR}/target/LinearRegression-project-1.0.jar"
CLASS="LinearRegression.src.main.java.LinearRegressionDataGen"
OPTION="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPTS} ${INPUT_HDFS}"

START_TS=`ssh ${master} "date +%F-%T"`

START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_gendata_${START_TS}.dat

END_TIME=`timestamp`
SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "${APP}-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
print_config ${BENCH_REPORT}

exit 0


# ===unused ==compress check 
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    COMPRESS_OPT="-compress true \
        -compressCodec $COMPRESS_CODEC \
        -compressType BLOCK "
else
    COMPRESS_OPT="-compress false"
fi
