#!/bin/bash



# configure
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"
echo "========== running MF benchmark =========="




SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`


CLASS="MatrixFactorization.src.main.java.MFApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${rank} ${MAX_ITERATION} ${LAMBDA} ${STORAGE_LEVEL}"
JAR="${DIR}/target/MFApp-1.0.jar"

#CLASS="src.main.scala.MFMovieLens"
#OPTION=" ${INPUT_HDFS} /mnt/nfs_dir/sperf/data_set/ml-10M100K/personalRatings.txt"
#OPTION=" ${INPUT_HDFS} /mnt/nfs_dir/sperf/data_set/BigDataGeneratorSuite/Graph_datagen/personalRatings.txt $numPar"

#JAR="${DIR}/target/scala-2.10/mfapp_2.10-1.0.jar"


for((i=0;i<${NUM_TRIALS};i++)); do		
	# path check
	$HADOOP_HOME/bin/hadoop dfs -rm -r ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
	START_TS=`ssh ${master} "date +%F-%T"`
	
	START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat
	
	END_TIME=`timestamp`
	gen_report "MF" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
done
exit 0


#if [ $COMPRESS -eq 1 ]; then
#    COMPRESS_OPT="-Dmapred.output.compress=true
#    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
#else
#    COMPRESS_OPT="-Dmapred.output.compress=false"
#fi
