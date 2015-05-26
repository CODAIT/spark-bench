#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# path check

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`


#JAR="${SPARK_HOME}/streaming/target/spark-streaming_2.10-1.2.0.jar"
#CLASS="org.apache.spark.streaming.util.RawTextSender"
#OPTION="9999 /tmp/config.sh 65536 1024"

JAR="${SPARK_HOME}/examples/target/scala-2.10/spark-examples-${SPARK_VERSION}-hadoop2.3.0.jar"
#CLASS="org.apache.spark.examples.streaming.FeederActor"
#OPTION="minli1 9999"


#opt=$subApp

# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
# PageViewStream MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
if [ $subApp = "NetworkWordCount" ];then
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

echo "========== running ${APP}-${subApp} gen data =========="


setup
for((i=0;i<${NUM_TRIALS};i++)); do
	
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
	START_TS=`ssh ${master} "date +%F-%T"`	
	START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_${subApp}_genData_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "${APP}" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
done
teardown
exit 0


#JAR="${DIR}/target/scala-2.10/pagerankapp_2.10-1.0.jar"
