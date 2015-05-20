#!/bin/bash

echo "========== preparing ${APP} data =========="
# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# "Usage: SVMGenerator <master> <output_dir> [num_examples] [num_features] [num_partitions]"


#JAR="${MllibJarDir}/spark-mllib_2.10-1.1.0.jar"
#CLASS="org.apache.spark.mllib.util.SVMDataGenerator"
#OPTION=" ${APP_MASTER} ${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "

# paths check
#tmp_dir=${APP_DIR}/tmp
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}
${HADOOP_HOME}/bin/hdfs dfs -mkdir ${APP_DIR}
${HADOOP_HOME}/bin/hdfs dfs -mkdir ${INPUT_HDFS}
#${HADOOP_HOME}/bin/hdfs dfs -rm -r $tmp_dir

#${HADOOP_HOME}/bin/hdfs dfs -mkdir $tmp_dir
#srcf=/mnt/nfs_dir/sperf/data_set/tmp-10k
srcf=/mnt/nfs_dir/sperf/data_set/BigDataGeneratorSuite/Table_datagen/e-com/output

${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal $srcf/* ${INPUT_HDFS}

JAR="${DIR}/target/scala-2.10/svmapp_2.10-1.0.jar"
CLASS="src.main.scala.DocToTFIDF"
OPTION="${tmp_dir} ${INPUT_HDFS} ${num_task} "

START_TIME=`timestamp`
START_TS=`ssh ${master} "date +%F-%T"`

#exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat

END_TIME=`timestamp`

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "SVM-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
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
