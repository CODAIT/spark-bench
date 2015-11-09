#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

APP=StronglyConnectedComponent
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# Either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}

SPARK_OPT=
if [ ! -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.storage.memoryFraction=${SPARK_STORAGE_MEMORYFRACTION}"
fi
if [ ! -z "$SPARK_EXECUTOR_MEMORY" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.executor.memory=${SPARK_EXECUTOR_MEMORY}"
fi
if [ ! -z "$SPARK_SERIALIZER" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.serializer=${SPARK_SERIALIZER}"
fi
if [ ! -z "$SPARK_RDD_COMPRESS" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.rdd.compress=${SPARK_RDD_COMPRESS}"
fi
if [ ! -z "$SPARK_IO_COMPRESSION_CODEC" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.io.compression.codec=${SPARK_IO_COMPRESSION_CODEC}"
fi
if [ ! -z "$SPARK_DEFAULT_PARALLELISM" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.default.parallelism=${SPARK_DEFAULT_PARALLELISM}"
fi

YARN_OPT=
if [ "$MASTER" = "yarn" ]; then
  if [ ! -z "$SPARK_EXECUTOR_INSTANCES" ]; then
    YARN_OPT="${YARN_OPT} --num-executors ${SPARK_EXECUTOR_INSTANCES}"
  fi
  if [ ! -z "$SPARK_EXECUTOR_CORES" ]; then
    YARN_OPT="${YARN_OPT} --executor-cores ${SPARK_EXECUTOR_CORES}"
  fi
  if [ ! -z "$SPARK_DRIVER_MEMORY" ]; then
    YARN_OPT="${YARN_OPT} --driver-memory ${SPARK_DRIVER_MEMORY}"
  fi
fi


#input benreport
function print_config(){
	local output=$1

	CONFIG=
	if [ ! -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
	  CONFIG="${CONFIG} memoryFraction ${SPARK_STORAGE_MEMORYFRACTION}"
	fi

	echo "${APP}_config \
	numV ${numV} numPar ${NUM_OF_PARTITIONS} mu ${mu} sigma ${sigma} \
	${CONFIG} " >> ${output}
}
function print_config_t(){
                   get_config_values $1 $2 $3 $4 $5 $6
}

function get_config_fields(){
       local report_field=$(get_report_field_name)  
           echo -n "#${report_field},AppType,nExe,driverMem,exeMem,exeCore,nExample,nFeature,EPS,nPar,Intercepts,nIter,memoryFraction,STEP_SIZE,noise,lambda,miniBatch,sparseness,convergenceTol" 
                echo -en "\n"
                                    
}
function get_config_values(){
          gen_report $1 $2 $3 $4 $5 $6
                echo -n ",${APP}-MLlibConfig,$nexe,$dmem,$emem,$ecore,${NUM_OF_EXAMPLES},${NUM_OF_FEATURES},${EPS},${NUM_OF_PARTITIONS},${INTERCEPTS},${MAX_ITERATION},${memoryFraction},${STEP_SIZE},$noise,${lambda},${miniBatch},${sparseness},${convergenceTol}" 
                       echo -en "\n"
                              return 0
}
