#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

echo "========== preparing ${APP} data =========="

#JAR="${MllibJar}"
JAR="${DIR}/target/SVMApp-1.0.jar"
#CLASS="org.apache.spark.mllib.util.SVMDataGenerator"
CLASS="src.main.scala.SVMDataGen"
OPTION=" ${APP_MASTER} ${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES}  ${NUM_OF_PARTITIONS} "
${RM} -r ${INPUT_HDFS}

# paths check
if [ "$genOpt" = "large" ]; then
	tmp_dir=${APP_DIR}/tmp	
	${RM} -r $tmp_dir
	${MKDIR} ${APP_DIR}
	${MKDIR} $tmp_dir
	#srcf=${DATASET_DIR}/tmp-10k
	srcf=${DATASET_DIR}/enwiki-doc
	${CPFROM} $srcf $tmp_dir

	JAR="${DIR}/target/scala-2.10/svmapp_2.10-1.0.jar"
	CLASS="src.main.scala.DocToTFIDF"
	OPTION="${tmp_dir} ${INPUT_HDFS} ${NUM_OF_PARTITIONS} "
fi

START_TS=`get_start_ts`;

setup
START_TIME=`timestamp`
echo "${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat"
exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/SVM_gendata_${START_TS}.dat

END_TIME=`timestamp`

SIZE=`${DU} -s ${INPUT_HDFS} | awk '{ print $1 }'`
gen_report "SVM-gendata" ${START_TIME} ${END_TIME} ${SIZE} ${START_TS}>> ${BENCH_REPORT}
print_config ${BENCH_REPORT}
teardown
exit 0



