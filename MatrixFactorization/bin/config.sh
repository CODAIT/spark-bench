#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
    set -a
    . "${bin}/../conf/env.sh"
    set +a
fi

# paths
APP=MF
#INPUT_HDFS=${DATA_HDFS}/MF/Input-ml/ml-10M100K
#INPUT_HDFS=${DATA_HDFS}/MF/Input-amazon
INPUT_HDFS=${DATA_HDFS}/MF/Input
OUTPUT_HDFS=${DATA_HDFS}/MF/Output


# either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}

set_gendata_opt
set_run_opt

function print_config(){
get_config_values $1 $2 $3 $4 $5 $6
}

function get_config_fields(){
local report_field=$(get_report_field_name) 
echo -n "#${report_field},AppType,nExe,driverMem,exeMem,exeCore,nPar,nIter,memoryFraction,m,n,trainSampFact,noise,sigma,test,testSampFact"
echo -en "\n"

}
function get_config_values(){
gen_report $1 $2 $3 $4 $5 $6
echo -n ",${APP}-MLlibConfig,$nexe,$dmem,$emem,$ecore,${NUM_OF_PARTITIONS},${MAX_ITERATION},${memoryFraction}, ${trainSampFact},${noise},${sigma},${test},${testSampFact}"
echo -en "\n"
return 0
}
