#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== running ${APP} workload =========="


# path check

DU ${INPUT_HDFS} SIZE 

JAR="${DIR}/target/LinearRegressionApp-1.0.jar"
CLASS="LinearRegression.src.main.java.LinearRegressionApp"
OPTION=" ${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${MAX_ITERATION} "

setup
for((i=0;i<${NUM_TRIALS};i++)); do
	
	RM  ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
	START_TS=`get_start_ts`
	START_TIME=`timestamp`
	echo_and_run sh -c "${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_run_${START_TS}.dat"
    res=$?
	END_TIME=`timestamp`
    get_config_fields >> ${BENCH_REPORT}
	print_config  ${APP} ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT}
done
teardown

exit 0


#if [ $COMPRESS -eq 1 ]; then
#    COMPRESS_OPT="-Dmapred.output.compress=true
#    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
#else
#    COMPRESS_OPT="-Dmapred.output.compress=false"
#fi
