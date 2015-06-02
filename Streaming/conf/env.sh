# for running
# NetworkWordCount StatefulNetworkWordCount CustomReceiver QueueStream RawNetworkGrep ActorWordCount
# PageViewStream MQTTWordCount ZeroMQWordCount TwitterAlgebirdCMS TwitterAlgebirdHLL TwitterPopularTags
subApp=PageViewStream
#subApp=TwitterPopularTags
optApp=80000000

if [ 1 -eq 0 ];then
	# for preparation 
	numV=4000000
	numPar=400
	mu=4.0
	sigma=1.3
	
	# for running
	MAX_ITERATION=3
	TOLERANCE=0.001
	RESET_PROB=0.15
fi

SPARK_STORAGE_MEMORYFRACTION=0.6
