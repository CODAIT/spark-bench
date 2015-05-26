#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running ${APP} bench =========="
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


# path check
$HADOOP_HOME/bin/hadoop dfs -rm-r ${OUTPUT_HDFS}

# pre-running
SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $2 }'`

JAR="${DIR}/target/scala-2.10/kmeans-app_2.10-1.0.jar"
CLASS="KmeansApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${NUM_RUN}"

#JAR="${DIR}/target/kmeans-project-1.0.jar"
#CLASS="kmeans_java.src.main.java.KmeansApp"
#OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${NUM_RUN}"

setup
for((i=0;i<${NUM_TRIALS};i++)); do
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
	START_TS=`ssh ${master} "date +%F-%T"`
	START_TIME=`timestamp`

		exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat
		
	END_TIME=`timestamp`
	gen_report "${APP}" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
done
teardown

exit 0


if [ $COMPRESS -eq 1 ]; then
    COMPRESS_OPT="-Dmapred.output.compress=true
    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
else
    COMPRESS_OPT="-Dmapred.output.compress=false"
fi
