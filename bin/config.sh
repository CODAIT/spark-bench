#!/bin/bash

this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )

if [ -f "${this}/../conf/env.sh" ] ; then
    . ${this}/../conf/env.sh
else
    echo "env.sh is missing"
    exit 1
fi

export BENCH_VERSION="2.0"
[ -z "$BENCH_HOME" ] &&    export BENCH_HOME="${this}/../"
[ -z "$BENCH_CONF" ]  &&    export BENCH_CONF="${this}/../conf/"
[ -f "${BENCH_HOME}/bin/funcs.sh" ] &&    . "${BENCH_HOME}/bin/funcs.sh"


export BENCH_NUM=${BENCH_HOME}/num;
if [ ! -d ${BENCH_NUM} ]; then
	mkdir -p ${BENCH_NUM};
	mkdir -p ${BENCH_NUM}/old;
fi 

# local report
export BENCH_REPORT=${BENCH_NUM}/bench-report.dat



if [ -z "$MllibJar" ]; then
	export MllibJar=~/.m2/repository/org/apache/spark/spark-mllib_2.10/${SPARK_VERSION}/spark-mllib_2.10-${SPARK_VERSION}.jar
fi

HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/conf}"

  MKDIR="${HADOOP_HOME}/bin/hdfs dfs -mkdir -p"
  RM="${HADOOP_HOME}/bin/hdfs dfs -rm"
  CPFROM="${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal"
  CPTO="${HADOOP_HOME}/bin/hdfs dfs -copyToLocal"
  DU="${HADOOP_HOME}/bin/hdfs dfs -du"

function  RM() { 
    tmpdir=$1;
    if [ -z "$tmpdir" ] || [ ! -d "$tmpdir" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       /bin/rm ${tmpdir:7}; 
    else
      ${HADOOP_HOME}/bin/hdfs dfs -rm $tmpdir
    fi
}
function MKDIR() { tmpdir=$1; /bin/mkdir -p ${tmpdir:7}; }
function DU() { 
   tmpdir=$1; /usr/bin/du -b "${tmpdir:7}";
}
  CPFROM="/bin/cp -r"
  CPTO="/bin/cp -r"
