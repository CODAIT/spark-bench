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

JAR="${DIR}/target/TerasortApp-1.0-jar-with-dependencies.jar"
CLASS="src.main.scala.terasortApp"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} "
Addition_jar="--jars ${DIR}/target/jars/guava-19.0-rc2.jar"

nexe=2
dmem=3g
emem=5g
ecore=3
YARN_OPT="--num-executors $nexe --driver-memory $dmem --executor-memory $emem --executor-cores $ecore"
res=$?;

for((i=0;i<${NUM_TRIALS};i++)); do
	
	RM ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
START_TS=`get_start_ts`;
	START_TIME=`timestamp`
	echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master yarn ${YARN_OPT} ${Addition_jar} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat"
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
