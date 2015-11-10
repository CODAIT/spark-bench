#!/bin/bash


# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"
echo "========== preparing MF data =========="


${RM} -r ${INPUT_HDFS}
#JAR="${MllibJar}"
#CLASS="org.apache.spark.mllib.util.MFDataGenerator"
JAR="${DIR}/target/MFApp-1.0.jar"
CLASS="src.main.scala.MFDataGenerator"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${m} ${n}  ${rank} ${trainSampFact} ${noise} ${sigma} ${test} ${testSampFact} ${NUM_OF_PARTITIONS}"

START_TS=`get_start_ts`;

setup
START_TIME=`timestamp`
#exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat
res=$?;
exec ${SPARK_HOME}/bin/spark-submit --jars ~/.m2/repository/org/jblas/jblas/1.2.4/jblas-1.2.4.jar --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat
res=$?;

END_TIME=`timestamp`
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gen ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
teardown

exit 0


# ===unused ==compress check 
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    COMPRESS_OPT="-compress true \
        -compressCodec $COMPRESS_CODEC \
        -compressType BLOCK "
else
    COMPRESS_OPT="-compress false"
fi
