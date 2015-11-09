#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

${RM} -r ${INPUT_HDFS}


JAR="${DIR}/target/PCAApp-1.0.jar"
CLASS="PCA.src.main.scala.PCADataGen"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_SAMPLES} ${NUM_OF_FEATURES} ${NUM_OF_PARTITIONS}"


START_TS=`get_start_ts`;
setup
START_TIME=`timestamp`
echo "${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/TEMP_gendata_${START_TS}.dat"
exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/TEMP_gendata_${START_TS}.dat

END_TIME=`timestamp`
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`

#gen_report "${APP}-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
#print_config ${BENCH_REPORT}
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
