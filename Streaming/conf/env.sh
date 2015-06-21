# for running
# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
#  MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
# PageViewStream StreamingLogisticRegression
subApp=PageViewStream
optApp=80000000


if [ 1 -eq 0 ];then
	
	
	# for preparation 
	numV=4000000
	#NUM_OF_PARTITIONS=400
	mu=4.0
	sigma=1.3
	
	# for running
	MAX_ITERATION=3
	TOLERANCE=0.001
	RESET_PROB=0.15
	
	# The parameter of StreamingLogisticRegression
	trainingDir=${INPUT_HDFS}/trainingDir 
	testDir=${INPUT_HDFS}/testDir 
	batchDuration=30 
	NUM_OF_EXAMPLES=25000
	NUM_OF_FEATURES=20
	NUM_OF_PARTITIONS=120
	ProbOne=0.2
	EPS=0.5
fi

SPARK_STORAGE_MEMORYFRACTION=0.6
