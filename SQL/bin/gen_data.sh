#!/bin/bash

echo "========== preparing ${APP} data =========="
# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# "Usage: SVMGenerator <master> <output_dir> [num_examples] [num_features] [num_partitions]"


#JAR="${MllibJar}"
#CLASS="org.apache.spark.mllib.util.SVMDataGenerator"
#OPTION=" ${APP_MASTER} ${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "

# paths check
#tmp_dir=${APP_DIR}/tmp
${RM} -r ${INPUT_HDFS}
${MKDIR} ${APP_DIR}
${MKDIR} ${INPUT_HDFS}
#${RM} -r $tmp_dir

#${MKDIR} $tmp_dir
#srcf=${DATASET_DIR}/tmp-10k
srcf=${DATASET_DIR}/BigDataGeneratorSuite/Table_datagen/e-com/output

${CPFROM} $srcf/* ${INPUT_HDFS}

JAR="${DIR}/target/scala-2.10/svmapp_2.10-1.0.jar"
CLASS="src.main.scala.DocToTFIDF"
OPTION="${tmp_dir} ${INPUT_HDFS} ${num_task} "

START_TS=`get_start_ts`;

setup
START_TIME=`timestamp`
#exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat

END_TIME=`timestamp`

SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "SVM-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
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
