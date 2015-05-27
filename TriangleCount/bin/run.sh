#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== running ${APP} benchmark =========="

# path check
SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
JAR="${DIR}/target/TriangleCountApp-1.0.jar"
CLASS="src.main.scala.triangleCountApp"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${numPar} ${STORAGE_LEVEL}"
echo "opt ${OPTION}"

setup
for((i=0;i<${NUM_TRIALS};i++)); do
	${RM} -r ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"	
	START_TS=`ssh ${master} "date +%F-%T"`
	START_TIME=`timestamp`
	exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_run_${START_TS}.dat
	END_TIME=`timestamp`
	gen_report "${APP}" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} >> ${BENCH_REPORT}
	print_config ${BENCH_REPORT}
done
teardown
exit 0


