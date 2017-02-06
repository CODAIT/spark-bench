#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# path check

DU ${INPUT_HDFS} SIZE 

#JAR="${DIR}/target/StreamingApp-1.0.jar"
JAR="$SPARK_HOME/examples/jars/spark-examples_2.11-2.0.1.jar"

if [[ -z "$JAR" ]]; then
  echo "Failed to find Spark examples assembly in  ${SPARK_HOME}/examples/target" 1>&2
  echo "You need to build Spark before running this program" 1>&2
  exit 1
fi



# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
# clickstream.PageViewStream MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
opt=$1
host=$2
port=$3

if [ ! -z "$opt" ]; then
  subApp=$opt
fi

if [ $subApp = "StreamingLogisticRegression" ];then
	OPTION="$trainingDir $testDir $batchDuration $NUM_OF_FEATURES"
	#CLASS="org.apache.spark.examples.mllib.${subApp}"
	CLASS="src.main.scala.${subApp}"
	echo "opt $OPTION"
	JAR="${DIR}/target/StreamingApp-1.0.jar"
elif [ $subApp = "NetworkWordCount" ];then
	OPTION="$host $port"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "StatefulNetworkWordCount" ];then	
	OPTION="$host $port"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "CustomReceiver" ];then	
	OPTION="$host $port"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "QueueStream"  ];then	
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "RawNetworkGrep" ];then	
       numStreams=$2
       host=$3
       port=$4
       batchMillis=$5
	OPTION="$numStreams $host $port $batchMillis"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "ActorWordCount" ];then	
	OPTION="$host $port"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "PageViewStream"  ];then	
	OPTION="errorRatePerZipCode $host $port"
	#CLASS="org.apache.spark.examples.streaming.${subApp}"
	CLASS="src.main.scala.PageViewStream"
	JAR="${DIR}/target/StreamingApp-1.0.jar"
elif [ $subApp = "MQTTWordCount"  ];then	
	OPTION="tcp://$host:1883 foo"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "ZeroMQWordCount"  ];then	
	OPTION="tcp://127.0.1.1:1234 foo"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "TwitterAlgebirdCMS" ];then	
	SPARK_OPT="${SPARK_OPT} --conf twitter4j.oauth.consumerKey=cD3wXjfmKbmMy43KP3f2lmcgK --conf twitter4j.oauth.consumerSecret=4oiAOoK6UA1q3IW24Mp2gyjhljbw5tPvbzVKvtOYLp --conf twitter4j.oauth.accessToken=18195366-xRzwB9QiTlX1z1av4LQ3QWGvsGIhTiQcLzEXUSMGb --conf twitter4j.oauth.accessTokenSecret=oopf1YVLvqOgTMn46i72go1Ok84KS3MiZ5QtS6zJybIf2 
	"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "TwitterAlgebirdHLL" ];then	
	SPARK_OPT="${SPARK_OPT} --conf twitter4j.oauth.consumerKey=cD3wXjfmKbmMy43KP3f2lmcgK --conf twitter4j.oauth.consumerSecret=4oiAOoK6UA1q3IW24Mp2gyjhljbw5tPvbzVKvtOYLp --conf twitter4j.oauth.accessToken=18195366-xRzwB9QiTlX1z1av4LQ3QWGvsGIhTiQcLzEXUSMGb --conf twitter4j.oauth.accessTokenSecret=oopf1YVLvqOgTMn46i72go1Ok84KS3MiZ5QtS6zJybIf2 
	"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
elif [ $subApp = "TwitterPopularTags" ];then
#   Usage: TwitterPopularTags <consumer key> <consumer secret> 
        #<access token> <access token secret> [<filters>]
	
	OPTION="cD3wXjfmKbmMy43KP3f2lmcgK 4oiAOoK6UA1q3IW24Mp2gyjhljbw5tPvbzVKvtOYLpeP4x8PlR 18195366-xRzwB9QiTlX1z1av4LQ3QWGvsGIhTiQcLzEXUSMGb oopf1YVLvqOgTMn46i72go1Ok84KS3MiZ5QtS6zJybIf2 
	"
	CLASS="org.apache.spark.examples.streaming.${subApp}"
else
	echo "$subApp not supported"
	exit 1
fi


#echo "subApp ${OPTION}"

echo "========== running ${APP}-${subApp} benchmark =========="


setup
for((i=0;i<${NUM_TRIALS};i++)); do
	
	RM ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_${subApp}_run_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
teardown
exit 0


#JAR="${DIR}/target/scala-2.10/pagerankapp_2.10-1.0.jar"
