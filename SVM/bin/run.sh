#!/bin/bash


bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== Running ${APP} Workload =========="

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
CLASS="SVM.src.main.java.SVMApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${MAX_ITERATION} ${STORAGE_LEVEL}"
JAR="${DIR}/target/SVMApp-1.0.jar"


for((i=0;i<${NUM_TRIALS};i++)); do
	# path check	
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS}
	START_TS=`ssh ${master} "date +%F-%T"`	
	START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "SVM" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
done
exit 0

