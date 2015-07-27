#!/bin/bash
#===overall config===
this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
script="$(basename -- "$this")"
this="$bin/$script"

if [ -f "${bin}/../conf/env.sh" ]; then
  set -a
  . "${bin}/../conf/env.sh"
  set +a
fi

export BENCH_VERSION="1.0"

masterhost=`hostname`
#A list of machines where the spark cluster is running

if [ ! -z `echo $SPARK_MASTER | grep "^spark://"` ]; then
  MC_LIST="$masterhost"
  MASTER=spark
elif [ ! -z `echo $SPARK_MASTER | grep "^mesos://"` ]; then
  MASTER=mesos
elif [ ! -z `echo $SPARK_MASTER | grep "^yarn"` ]; then
  MASTER=yarn
else
  MC_LIST="$masterhost"
  MASTER=local
fi

# whether restart spark cluster at every workload run; value: "TRUE" or "FALSE"
RESTART="FALSE"
# base dir for DataSet
if [ -z "$DATASET_DIR" ]; then
  export DATASET_DIR=/home/`whoami`/SparkBench/dataset
fi

# base dir for HDFS
if [ -z "$DATA_HDFS" ]; then
  export DATA_HDFS=/home/`whoami`/SparkBench
fi

if [ ! -z `echo $DATA_HDFS | grep "^hdfs://"` ]; then
  FILESYSTEM=hdfs
  HDFS_URL="hdfs://${masterhost}:9000"
else
  FILESYSTEM=local
fi
if [ "$MASTER" = "spark" ] && [ "$FILESYSTEM" = "local" ]; then
  INOUT_SCHEME="file://"
fi

###################### Global Paths ##################
if [ -z "$BENCH_HOME" ]; then
    export BENCH_HOME=`dirname "$this"`/..
fi

if [ -z "$BENCH_CONF" ]; then
    export BENCH_CONF=${BENCH_HOME}/bin
fi

if [ -f "${BENCH_CONF}/funcs.sh" ]; then
    . "${BENCH_CONF}/funcs.sh"
fi

if [ -z "$SPARK_HOME" ]; then
    export SPARK_HOME=/mnt/nfs_dir/spark-latest/spark
fi

if [ -z "$HADOOP_HOME" ]; then
    export HADOOP_HOME=/mnt/nfs_dir/hadoop-common/hadoop-dist/target/hadoop-2.4.0/
fi

if [ -z "$HIVE_HOME" ]; then
    export HIVE_HOME=${BENCH_HOME}/common/hive-0.9.0-bin
fi

if [ -z "$MllibJar" ]; then
    export MllibJar=~/.m2/repository/org/apache/spark/spark-mllib_2.10/${SPARK_VERSION}/spark-mllib_2.10-${SPARK_VERSION}.jar
    if [ ! -f "$MllibJar" ]; then
        export MllibJar=${SPARK_HOME}/lib/spark-assembly-*.jar
    fi
fi




if [ $# -gt 1 ]
then
    if [ "--hadoop_config" = "$1" ]
          then
              shift
              confdir=$1
              shift
              HADOOP_CONF_DIR=$confdir
    fi
fi

HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/conf}"

if [ "${FILESYSTEM}" = "local" ]; then
  MKDIR="/bin/mkdir -p"
  RM="/bin/rm"
  CPFROM="/bin/cp -r"
  CPTO="/bin/cp -r"
  DU="/usr/bin/du -b"
else
  MKDIR="${HADOOP_HOME}/bin/hadoop fs -mkdir -p"
  RM="${HADOOP_HOME}/bin/hadoop fs -rm"
  CPFROM="${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal"
  CPTO="${HADOOP_HOME}/bin/hdfs dfs -copyToLocal"
  DU="${HADOOP_HOME}/bin/hadoop fs -du"
fi

export BENCH_NUM=${BENCH_HOME}/num;
if [ ! -d ${BENCH_NUM} ]; then
	mkdir -p ${BENCH_NUM};
	mkdir -p ${BENCH_NUM}/old;
fi 

# local report
export BENCH_REPORT=${BENCH_HOME}/bench-model-validation-march26-15.dat

#if [ -z "$MAHOUT_HOME" ]; then
#    export MAHOUT_HOME=${BENCH_HOME}/common/mahout-distribution-0.7
#fi
