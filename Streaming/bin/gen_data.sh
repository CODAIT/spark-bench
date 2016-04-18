#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# path check

DU ${INPUT_HDFS} SIZE 


#JAR="${SPARK_HOME}/streaming/target/spark-streaming_2.10-1.2.0.jar"
#CLASS="org.apache.spark.streaming.util.RawTextSender"
#OPTION="9999 /tmp/config.sh 65536 1024"

JAR="${SPARK_HOME}/examples/target/scala-2.10/spark-examples-${SPARK_VERSION}-hadoop2.3.0.jar"
#CLASS="org.apache.spark.examples.streaming.FeederActor"
#OPTION="minli1 9999"


#opt=$subApp

# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
# PageViewStream MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
if [ $subApp = "StreamingLogisticRegression" ];then

	${HADOOP_HOME}/bin/hdfs dfs -rm -r  ${INPUT_HDFS}
	${HADOOP_HOME}/bin/hdfs dfs -mkdir  ${INPUT_HDFS}
	${HADOOP_HOME}/bin/hdfs dfs -mkdir  ${trainingDir}
	${HADOOP_HOME}/bin/hdfs dfs -mkdir  ${testDir}

	JAR="${DIR}/../LogisticRegression/target/LogisticRegressionApp-1.0.jar"
	CLASS="LogisticRegression.src.main.java.LogisticRegressionDataGen"
	NUM_TRIALS=3
	purge_data "${MC_LIST}"
	for((i=0;i<${NUM_TRIALS};i++)); do
		echo "========== generating data of ${APP}-${subApp} iteration ${i}  =========="


		START_TIME=`timestamp`
		START_TS=`ssh ${master} "date +%F-%T"`

		${HADOOP_HOME}/bin/hdfs dfs -rm -r  ${trainingDir}/*
		tmpdir=${INPUT_HDFS}/tmp-train
		${HADOOP_HOME}/bin/hdfs dfs -rm -r  ${tmpdir}



		OPTION="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${ProbOne} ${tmpdir}"
		echo "gen train data opion $OPTION"
		echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_${subApp}_genData_${START_TS}.dat;"
res=$?;
		${HADOOP_HOME}/bin/hdfs dfs -mv ${tmpdir}/* ${trainingDir}/;


		${HADOOP_HOME}/bin/hdfs dfs -rm -r  ${testDir}/*
		tmpdir=${INPUT_HDFS}/tmp-test
		${HADOOP_HOME}/bin/hdfs dfs -rm -r  ${tmpdir}

		OPTION="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${ProbOne} ${tmpdir}"
		echo "gen test data op $OPTION"
		echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_${subApp}_genData_${START_TS}.dat;"
res=$?;
		${HADOOP_HOME}/bin/hdfs dfs -mv ${tmpdir}/* ${testDir}/

		END_TIME=`timestamp`
		sleep 5
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
	done

	exit 0
elif [ $subApp = "NetworkWordCount" ];then
	echo "no need"
	exit 0
	#subapp="nc -lk 9999"
	#OPTION="nc -lk 9999"
elif [ $subApp = "StatefulNetworkWordCount" ];then
	echo "no need"
	exit 0
	#OPTION="minli1 9999"
elif [ $subApp = "CustomReceiver" ];then
	echo "no need"
	exit 0
#	OPTION="minli1 9999"
elif [ $subApp = "QueueStream" ];then
	echo "no need"
	exit 0
elif [ $subApp = "RawNetworkGrep" ];then
	echo "no need"
	exit 0
	OPTION="10 minli1 9999 3000"
elif [ $subApp = "ActorWordCount" ];then
	echo "no need"
	exit 0
	OPTION="minli1 9999"
elif [ $subApp = "PageViewStream" ];then	
	#OPTION="errorRatePerZipCode minli1 44444"
	#CLASS="org.apache.spark.examples.streaming.clickstream.PageViewGenerator"
	CLASS="src.main.scala.PageViewGenerator"
	OPTION=" 44444 ${optApp}"
	JAR="${DIR}/target/scala-2.10/streamingapp_2.10-1.0.jar"
elif [ $subApp = "MQTTWordCount" ];then
	#subapp=MQTTWordCount
	#OPTION="tcp://minli1:1883 foo"
	CLASS="org.apache.spark.examples.streaming.MQTTPublisher"
    OPTION=" tcp://minli1:1883 foo"
elif [ $subApp = "ZeroMQWordCount" ];then
	#subapp=ZeroMQWordCount 
	#OPTION="tcp://127.0.1.1:1234 foo"
	CLASS="org.apache.spark.examples.streaming.SimpleZeroMQPublisher "
    OPTION=" tcp://127.0.1.1:1234 foo.bar"
elif [ $subApp = "TwitterAlgebirdCMS" ];then
	echo "no need"
	exit 0
elif [ $subApp = "TwitterAlgebirdHLL" ];then
	echo "no need"
	exit 0
elif [ $subApp = "TwitterPopularTags" ];then
	echo "no need"
	exit 0
else
	echo "$subApp not supported"
	exit 1;		
fi

 

#echo "opt ${OPTION}"




setup
for((i=0;i<${NUM_TRIALS};i++)); do
	echo "========== Generating data for ${APP}-${subApp}  =========="	
	RM ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_${subApp}_genData_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
teardown
exit 0


#JAR="${DIR}/target/scala-2.10/pagerankapp_2.10-1.0.jar"
