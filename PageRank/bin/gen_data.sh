#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

# paths check
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}

# generate data
START_TIME=`timestamp`
START_TS=`ssh ${master} "date +%F-%T"`

genOpt="small"
if [ $genOpt = "small" ];then
	JAR="${DIR}/target/PageRankApp-1.0.jar"
	CLASS="src.main.scala.pageRankDataGen"
	OPTION="${INPUT_HDFS} ${numV} ${numPar} ${mu} ${sigma}"
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat
elif [ $genOpt = "large" ];then
	${HADOOP_HOME}/bin/hdfs dfs -mkdir ${APP_DIR}
	${HADOOP_HOME}/bin/hdfs dfs -mkdir ${INPUT_HDFS}
	srcf=/mnt/nfs_dir/sperf/data_set/web-Google.txt	#srcf=/mnt/nfs_dir/sperf/data_set/BigDataGeneratorSuite/Graph_datagen/AMR_gen_edge_24.txt
	${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal $srcf ${INPUT_HDFS}
else
	echo "error"
	exit 1
fi

END_TIME=`timestamp`
SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "${APP}_gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
exit 0



