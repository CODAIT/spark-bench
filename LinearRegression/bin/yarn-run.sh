#!/bin/bash
#==Linear Regression== 
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running LinearRegression benchmark on YARN =========="
# configure
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"



# path check

DU ${INPUT_HDFS} SIZE 

#JAR="${DIR}/target/scala-2.10/LinearRegression-app_2.10-1.0.jar"
CLASS="LinearRegression.src.main.java.LinearRegressionApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${MAX_ITERATION} "

JAR="${DIR}/target/LinearRegression-project-1.0.jar"

nexe=3
dmem=4g
emem=2g
ecore=1
YARN_OPT="--num-executors $nexe --driver-memory $dmem --exectuor-memory $emem --executor-cores $ecore"
res=$?;

for((i=0;i<${NUM_TRIALS};i++)); do
	
	RM ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master yarn-cluster ${YARN_OPT} --conf spark.storage.memoryFraction=${memoryFraction} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_run_${START_TS}.dat"
res=$?;
	END_TIME=`timestamp`
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT};
done
exit 0


#if [ $COMPRESS -eq 1 ]; then
#    COMPRESS_OPT="-Dmapred.output.compress=true
#    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
#else
#    COMPRESS_OPT="-Dmapred.output.compress=false"
#fi
