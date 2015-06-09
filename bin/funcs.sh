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
function timestamp(){
    sec=`date +%s`
    nanosec=`date +%N`
    tmp=`expr $sec \* 1000 `
    msec=`expr $nanosec / 1000000 `
    echo `expr $tmp + $msec`
}

function gen_report() {
    local type=$1
    local start=$2
    local end=$3
    local size=$4
	local start_ts=$5
    which bc > /dev/null 2>&1
    if [ $? -eq 1 ]; then
        echo "\"bc\" utility missing. Please install it to generate proper report."
        return 1
    fi
    local size=`echo "scale=6;$size/1024/1024"|bc`
	local duration=`echo "scale=6;($end-$start)/1000"|bc`
    local tput=`echo "scale=6;$size/$duration"|bc`

	echo "$type ${start_ts} $duration $size $tput"
    #echo "$type `date +%F-%T` <$start,$end> $size $duration $tput"
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
function purge_data(){
	local mc_list="$1"	
	cmd="echo 3 >/proc/sys/vm/drop_caches"; 
	#echo ${mc_list}
	for nn in ${mc_list}; do 
	#echo $nn
	ssh  -t $nn "sudo sh -c \"$cmd\""; 
	done;
	echo "date purged on ${mc_list}"
}
function get_start_ts() {
  return `ssh ${masterhost} "date +%F-%T"`
}
function setup(){
  if [ "${MASTER}" = "spark" ]; then
    "${SPARK_HOME}/sbin/stop-all.sh"
    "${SPARK_HOME}/sbin/start-all.sh"
  fi
}
function teardown(){
  if [ "${MASTER}" = "spark" ]; then
    "${SPARK_HOME}/sbin/stop-all.sh"
  fi

}
