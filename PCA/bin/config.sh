#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

# paths
APP=PCA
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi

# Either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}

set_gendata_opt
set_run_opt

function print_config(){
	local output=$1

	CONFIG=
	if [ ! -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
	  CONFIG="${CONFIG} memoryFraction ${SPARK_STORAGE_MEMORYFRACTION}"
	fi
	if [ ! -z "$SPARK_SERIALIZER" ]; then
	  CONFIG="${CONFIG} ${SPARK_SERIALIZER}"
	fi
	if [ ! -z "$SPARK_RDD_COMPRESS" ]; then
	  CONFIG="${CONFIG} RDDcomp ${SPARK_RDD_COMPRESS}"
	fi
	if [ ! -z "$SPARK_IO_COMPRESSION_CODEC" ]; then
	  CONFIG="${CONFIG} ${SPARK_IO_COMPRESSION_CODEC}"
	fi
	if [ ! -z "$SPARK_DEFAULT_PARALLELISM" ]; then
	  CONFIG="${CONFIG} ${SPARK_DEFAULT_PARALLELISM}"
	fi

	echo "${APP}_config \
        NUM_OF_SAMPLES ${NUM_OF_SAMPLES} MIN_SUPPORT ${MIN_SUPPORT} \
	NUM_OF_PARTITIONS ${NUM_OF_PARTITIONS}  \
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