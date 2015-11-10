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


function print_config(){
get_config_values $1 $2 $3 $4 $5 $6
}

function get_config_fields(){
local report_field=$(get_report_field_name)  
echo -n "#${report_field},AppType,nExe,driverMem,exeMem,exeCore,nPar,nIter,memoryFraction,NUM_OF_EXAMPLES,NUM_OF_FEATURES,NUM_OF_PARTITIONS,class_c,class-r, impurity-c,impurity-c, maxDepth-c,maxDepth-r, maxbin-c,maxbin-r, mode-c,modeR"
echo -en "\n"

}
function get_config_values(){
gen_report $1 $2 $3 $4 $5 $6
echo -n ",${APP}-MLlibConfig,$nexe,$dmem,$emem,$ecore,${NUM_OF_PARTITIONS},${MAX_ITERATION},${memoryFraction},${NUM_OF_EXAMPLES},${NUM_OF_FEATURES},${NUM_OF_PARTITIONS},${NUM_OF_CLASS_C},${NUM_OF_CLASS_R},${impurityC},${impurityR},${maxDepthC},${maxDepthR},${maxBinsC},${maxBinsR},${modeC},${modeR}"
echo -en "\n"
return 0
}
