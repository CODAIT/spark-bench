#!/bin/bash

this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
if [ -f "${bin}/../conf/env.sh" ]; then
    set -a
    . "${bin}/../conf/env.sh"
    set +a
fi

# paths
APP=KMeans
INPUT_HDFS=${DATA_HDFS}/${APP}/Input
OUTPUT_HDFS=${DATA_HDFS}/${APP}/Output


# Either stand alone or yarn cluster
APP_MASTER=${SPARK_MASTER}

set_gendata_opt
set_run_opt

function print_config(){
   get_config_values $1 $2 $3 $4 $5 $6
}

function get_config_fields(){
  local report_field=$(get_report_field_name)  
  echo -n "#${report_field},AppType,nExe,driverMem,exeMem,exeCore,nPoints,nClusters,nPar,nIter,memoryFraction" 
  echo -en "\n"

}
function get_config_values(){
    gen_report $1 $2 $3 $4 $5 $6
    echo -n ",${APP}-MLlibConfig,$nexe,$dmem,$emem,$ecore,${NUM_OF_POINTS},${NUM_OF_CLUSTERS},${NUM_OF_PARTITIONS},${MAX_ITERATION},${memoryFraction}"
    echo -en "\n"
    return 0
}
