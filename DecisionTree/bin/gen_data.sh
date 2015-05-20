#!/bin/bash

echo "========== preparing DecisionTree data =========="
# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# paths check
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}


# "Usage: SVMGenerator <master> <output_dir> [num_examples] [num_features] [num_partitions]"


JAR="${MllibJarDir}/spark-mllib_2.10-1.1.0.jar"
CLASS="org.apache.spark.mllib.util.SVMDataGenerator"
OPTION=" ${SPARK_MASTER} ${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "

START_TIME=`timestamp`
START_TS=`ssh ${master} "date +%F-%T"`
exec ${SPARK_HOME}/bin/spark-submit --class $CLASS  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/DecisionTree_gendata_${START_TS}.dat
END_TIME=`timestamp`

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "DecisionTree-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
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
