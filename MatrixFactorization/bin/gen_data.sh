#!/bin/bash

echo "========== preparing MF data =========="
# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# paths check
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}
#${HADOOP_HOME}/bin/hadoop fs -mkdir ${INPUT_HDFS}

# println("Usage: MFDataGenerator " +
#        "<master> <outputDir> [m] [n] [rank] [trainSampFact] [noise] [sigma] [test] [testSampFact]")

#JAR="${MllibJarDir}/spark-mllib_2.10-1.1.0.jar"
#CLASS="org.apache.spark.mllib.util.MFDataGenerator"
JAR="${DIR}/target/scala-2.10/mfapp_2.10-1.0.jar"
CLASS="MatrixFactorization.src.main.scala.MFDataGenerator"

OPTION="${SPARK_MASTER} ${INPUT_HDFS} ${m} ${n}  ${rank} ${trainSampFact} ${noise} ${sigma} ${test} ${testSampFact}"

echo ${OPTION}
START_TIME=`timestamp`
START_TS=`ssh ${master} "date +%F-%T"`

exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat

END_TIME=`timestamp`


SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "MF-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
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
