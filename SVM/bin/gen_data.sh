#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

JAR="${MllibJar}"
CLASS="org.apache.spark.mllib.util.SVMDataGenerator"
OPTION=" ${APP_MASTER} ${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}

# paths check
if [ genOpt = "large" ];then
	tmp_dir=${APP_DIR}/tmp	
	${HADOOP_HOME}/bin/hdfs dfs -rm -r $tmp_dir
	${HADOOP_HOME}/bin/hdfs dfs -mkdir ${APP_DIR}
	${HADOOP_HOME}/bin/hdfs dfs -mkdir $tmp_dir
	#srcf=/mnt/nfs_dir/sperf/data_set/tmp-10k
	srcf=/home/limin/data_set/enwiki-doc
	${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal $srcf $tmp_dir

	JAR="${DIR}/target/scala-2.10/svmapp_2.10-1.0.jar"
	CLASS="src.main.scala.DocToTFIDF"
	OPTION="${tmp_dir} ${INPUT_HDFS} ${NUM_OF_PARTITIONS} "
fi

START_TS=`ssh ${master} "date +%F-%T"`

setup
START_TIME=`timestamp`
exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat

END_TIME=`timestamp`

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "SVM-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
teardown
exit 0



