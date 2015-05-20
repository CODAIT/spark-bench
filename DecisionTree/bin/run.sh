#!/bin/bash
#==DecisionTree===
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running DecisionTree benchmark =========="
# configure
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"





SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`


JAR="${DIR}/target/DecisionTree-project-1.0.jar"
CLASS="DecisionTree.src.main.java.DecisionTreeApp"



#System.out.println("usage: <input> <output> <numClass>"
                    #+ " <impurity> <maxDepth> <maxBins> <mode:Regression/Classification>");

for((i=0;i<${NUM_TRIALS};i++)); do
		
	# classification
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS_Classification}
	purge_data "${MC_LIST}"	
	OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS_Classification} ${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}"
	START_TIME=`timestamp`
	START_TS=`ssh ${master} "date +%F-%T"`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} --conf spark.storage.memoryFraction=${memoryFraction} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/DecisionTree_run_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "DecisionTree-classification" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
	
	
done
exit 0

if [ 1 -eq 0 ]; then
# Regression
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS_Regression}
	purge_data "${MC_LIST}"	
	OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS_Regression} ${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR} "
	START_TIME=`timestamp`
	START_TS=`ssh ${master} "date +%F-%T"`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/DecisionTree_run_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "DecisionTree-regression" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
fi

#if [ $COMPRESS -eq 1 ]; then
#    COMPRESS_OPT="-Dmapred.output.compress=true
#    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
#else
#    COMPRESS_OPT="-Dmapred.output.compress=false"
#fi