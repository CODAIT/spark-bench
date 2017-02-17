# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

############### common functions ################
function timestamp() {
    sec=`date +%s`
    nanosec=`date +%N`
    
    # %N is not supported in OSX. Use gdate from `coreutils` suite instead.
    unamestr=`uname`
    if [ "${unamestr}" = "Darwin" ]; then
        nanosec=`gdate  +%N`
    fi
    
    tmp=`expr $sec \* 1000 `
    msec=`expr $nanosec / 1000000 `
    echo `expr $tmp + $msec`
}

function get_report_field_name() {
        echo -n "Apptype,start_ts,duration,size,throughput,resStatus"
}

function gen_report() {
    if [ $# -lt 6 ];then echo "error: gen_report input variables less than 6"; return 1; fi
    local type=$1
    local start=$2
    local end=$3
    local size=$4
	local start_ts=$5
    local res=$6
    which bc > /dev/null 2>&1
    if [ $? -eq 1 ]; then
        echo "\"bc\" utility missing. Please install it to generate proper report."
        return 1
    fi
    local size=`echo "scale=6;$size/1024/1024"|bc`
	local duration=`echo "scale=6;($end-$start)/1000"|bc`
    local tput=`echo "scale=6;$size/$duration"|bc`
    echo -n "$type,${start_ts},$duration,$size,$tput,$res"

}

function check_dir() {
    local dir=$1
    if [ -z "$dir" ];then
        echo "WARN: payload missing."
        return 1
    fi
    if [ ! -d "$dir" ];then
        echo "ERROR: directory $dir does not exist."
        exit 1
    fi
}

#usage purge_date "${MC_LIST}"
function purge_data() {
	local mc_list="$1"	
    if [ -z "${mc_list}" ];then
        return 1
    fi
	cmd="echo 3 >/proc/sys/vm/drop_caches"; 
	#echo ${mc_list}
	for nn in ${mc_list}; do 
	#echo $nn
	ssh  -t $nn "sudo sh -c \"$cmd\""; 
	done;
	echo "data purged on ${mc_list}"
}

function get_start_ts() {
   ts=`ssh ${master} "date +%F-%T"`
   echo $ts
}

function setup() {
  echo "Master:$MASTER"
  if [ "${MASTER}" = "spark" ] && [ "${RESTART}" = "TRUE" ] ; then
    "${SPARK_HOME}/sbin/stop-all.sh"
    "${SPARK_HOME}/sbin/start-all.sh"
  fi
}

function teardown() {
  if [ "${MASTER}" = "spark" ] && [ "${RESTART}" = "TRUE" ] ; then
    "${SPARK_HOME}/sbin/stop-all.sh"
  fi
}

function set_gendata_opt() {
  SPARK_OPT=
  if [ ! -z "$SPARK_RPC_ASKTIMEOUT" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.rpc.askTimeout=${SPARK_RPC_ASKTIMEOUT}"
  fi
#memory fraction is deprecated in spark 2.0.1
#  if [ ! -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
#    SPARK_OPT="${SPARK_OPT} --conf spark.storage.memoryFraction=${SPARK_STORAGE_MEMORYFRACTION}"
#  fi
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

    if [ ! -z "$YARN_DEPLOY_MODE" ]; then
      YARN_OPT="${YARN_OPT} --deploy-mode ${YARN_DEPLOY_MODE}"
    fi
#echo "YARN_OPTS $YARN_OPT"
  fi
}

function set_run_opt() {
  if [ ! -z "$SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE" ] && [ "$FILESYSTEM" != "hdfs" ]; then
    export SPARK_SUBMIT_OPTS="${SPARK_SUBMIT_OPTS} -Dspark.hadoop.fs.local.block.size=${SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE}"
  fi
}

function echo_and_run() { echo "$@" ; "$@" ; }



function RM() { 
    tmpdir=$1;
    if [ $# -lt 1 ] || [ -z "$tmpdir" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       if [ ! -d "${tmpdir:7}" ]; then return 1;    fi
       /bin/rm -r ${tmpdir:7}; 
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $tmpdir;
       if [ $? == 1 ]; then  return 1; fi
      ${HADOOP_HOME}/bin/hdfs dfs -rmr $tmpdir
    fi
}
function MKDIR() {  
    tmpdir=$1;
    if [ $# -lt 1 ] || [ -z "$tmpdir" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       /bin/mkdir -p ${tmpdir:7};
    else
      ${HADOOP_HOME}/bin/hdfs dfs -mkdir -p $tmpdir
    fi
}
function DU() { 
    local tmpdir=$1;
    if [ -z "$tmpdir" ] || [ $# -lt 2 ]; then
        return 1;
    fi
    local  __resultvar=$2
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       if [ ! -d "${tmpdir:7}" ]; then return 1;    fi
       local myresult=`/usr/bin/du -b "${tmpdir:7}"|awk '{print $1}'`;
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $tmpdir;
       if [ $? == 1 ]; then  return 1; fi
       local myresult=`${HADOOP_HOME}/bin/hdfs dfs -du -s $tmpdir|awk '{print $1}'`;
    fi
    eval $__resultvar="'$myresult'"

}
 
function CPFROM() { 
    if [ $# -lt 2 ]; then return 1; fi
    src=$1; dst=$2;
    if [ -z "$src" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
        if [ ! -d "${src:7}" ]; then echo "src dir should start with file:///";return 1;    fi
        /bin/cp  ${src:7}/* ${dst:7}
    else
       if [ ! -d "${src:8}" ]; then return 1;    fi
      ${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal  ${src:8}/* $dst
    fi
}
function  CPTO() { 
    if [ $# -lt 2 ]; then return 1; fi
    src=$1; dst=$2;
    if [ -z "$src" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
        /bin/cp -r $src $dst
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $src;
       if [ $? == 1 ]; then  return 1; fi
      ${HADOOP_HOME}/bin/hdfs dfs -copyToLocal  $src $dst
    fi
}
