#!/bin/bash
#===overall config===
this="${BASH_SOURCE-$0}"
bin=$(cd -P -- "$(dirname -- "$this")" && pwd -P)
script="$(basename -- "$this")"
this="$bin/$script"

SPARK_VERSION=1.4.0-SNAPSHOT
master=`hostname`

#A list of machines where the spark cluster is running
MC_LIST="minli3 minli4 minli5 minli6 minli7 minli8 minli12 minli13 minli14 minli15"

#SPARK_MASTER=local
#SPARK_MASTER=local[K]
#SPARK_MASTER=local[*]
#SPARK_MASTER=spark://HOST:PORT
##SPARK_MASTER=mesos://HOST:PORT
##SPARK_MASTER=yarn-client
##SPARK_MASTER=yarn-cluster
SPARK_MASTER=spark://${master}:7077
if [ ! -z `echo $SPARK_MASTER | grep "^spark://"` ]; then
  MASTER=spark
  MC_LIST="$master"
elif [ ! -z `echo $SPARK_MASTER | grep "^mesos://"` ]; then
  MASTER=mesos
elif [ ! -z `echo $SPARK_MASTER | grep "^yarn"` ]; then
  MASTER=yarn
else
  MASTER=local
  MC_LIST="$master"
fi

# base dir for DataSet
export DATASET_DIR=/home/`whoami`/SparkBench/dataset

# base dir HDFS
#export DATA_HDFS=hdfs://SparkBench
#export DATA_HDFS="hdfs://${master}:9000/SparkBench"
export DATA_HDFS=/home/`whoami`/SparkBench

if [ ! -z `echo $DATA_HDFS | grep "^hdfs://"` ]; then
  FILESYSTEM=hdfs
  HDFS_URL="hdfs://${master}:9000"
else
  FILESYSTEM=local
fi
if [ "$MASTER" = "spark" ] && [ "$FILESYSTEM" = "local" ]; then
  INOUT_SCHEME="file://"
fi

export BENCH_VERSION="1.0"

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
export EXECUTOR_GLOBAL_MEM=6g
#export STORAGE_LEVEL=MEMORY_AND_DISK_SER
export STORAGE_LEVEL=MEMORY_AND_DISK
export executor_cores=6
#export MEM_FRACTION_GLOBAL=0.6
#export MEM_FRACTION_GLOBAL=0.005

################# Compress Options #################
# swith on/off compression: 0-off, 1-on
export COMPRESS_GLOBAL=0
export COMPRESS_CODEC_GLOBAL=org.apache.hadoop.io.compress.DefaultCodec
#export COMPRESS_CODEC_GLOBAL=com.hadoop.compression.lzo.LzoCodec
#export COMPRESS_CODEC_GLOBAL=org.apache.hadoop.io.compress.SnappyCodec


#if [ -z "$MAHOUT_HOME" ]; then
#    export MAHOUT_HOME=${BENCH_HOME}/common/mahout-distribution-0.7
#fi
