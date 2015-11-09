#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

APP=DecisionTree
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS_Classification=${DATA_HDFS}/${APP}/Output-Classification
OUTPUT_HDFS_Regression=${DATA_HDFS}/${APP}/Output-Classification

# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}

set_gendata_opt
set_run_opt

#input benreport
function print_config(){
	local output=$1

	CONFIG=
	if [ ! -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
	  CONFIG="${CONFIG} memoryFraction ${SPARK_STORAGE_MEMORYFRACTION}"
	fi

	echo "DecisionTreeC-Config \
	NUM_OF_EXAMPLES ${NUM_OF_EXAMPLES} \
	NUM_OF_FEATURES ${NUM_OF_FEATURES} \
	NUM_OF_PARTITIONS ${NUM_OF_PARTITIONS} \
	class ${NUM_OF_CLASS_C} ${NUM_OF_CLASS_R} impurity ${impurityC}  ${impurityR} maxDepth ${maxDepthC} ${maxDepthR} maxbin ${maxBinsC} ${maxBinsR} mode ${modeC} ${modeR} \
        ${CONFIG}" >> ${output}
}

#### unused ####
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    INPUT_HDFS=${INPUT_HDFS}-comp
    OUTPUT_HDFS=${OUTPUT_HDFS}-comp
fi
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
