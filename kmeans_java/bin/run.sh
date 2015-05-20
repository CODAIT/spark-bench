#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running kmeans bench =========="
# configure
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


# path check

# pre-running
SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
CLASS="kmeans_java.src.main.java.KmeansApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${NUM_RUN}"
JAR="${DIR}/target/kmeans-project-1.0.jar"



for((i=0;i<${NUM_TRIALS};i++)); do
	echo $i, ${BENCH_NUM};
	if [ 1 -eq 1 ]; then
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS}
	START_TIME=`timestamp`
	START_TS=`ssh ${master} "date +%F-%T"`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "KMEANS" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	fi
done
exit 0


#if [ $COMPRESS -eq 1 ]; then
#    COMPRESS_OPT="-Dmapred.output.compress=true
#    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
#else
#    COMPRESS_OPT="-Dmapred.output.compress=false"
#fi
