# global settings

master="pts00450-vm16"
#A list of machines where the spark cluster is running
MC_LIST="pts00450-vm22 pts00450-vm23"


[ -z "$HADOOP_HOME" ] &&     export HADOOP_HOME=/YOUR/HADOOP
# base dir for DataSet
HDFS_URL="hdfs://${master}:9000"
SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE=536870912

# DATA_HDFS="hdfs://${master}:9000/SparkBench", "file:///home/`whoami`/SparkBench"
DATA_HDFS="hdfs://${master}:9000/SparkBench"

#Local dataset optional
DATASET_DIR=/home/`whoami`/SparkBench/dataset

SPARK_VERSION=2.0.1  #1.5.1
[ -z "$SPARK_HOME" ] &&     export SPARK_HOME=/YOUR/SPARK

#SPARK_MASTER=local
#SPARK_MASTER=local[K]
#SPARK_MASTER=local[*]
#SPARK_MASTER=spark://HOST:PORT
##SPARK_MASTER=mesos://HOST:PORT
##SPARK_MASTER=yarn-client
SPARK_MASTER=yarn
MASTER=yarn
YARN_DEPLOY_MODE=client # or cluster, this will go to spark submit as --deploy-mode
SPARK_RPC_ASKTIMEOUT=500
#SPARK_MASTER=spark://${master}:7077



# Spark config in environment variable or aruments of spark-submit 
# - SPARK_SERIALIZER, --conf spark.serializer
# - SPARK_RDD_COMPRESS, --conf spark.rdd.compress
# - SPARK_IO_COMPRESSION_CODEC, --conf spark.io.compression.codec
# - SPARK_DEFAULT_PARALLELISM, --conf spark.default.parallelism
SPARK_SERIALIZER=org.apache.spark.serializer.KryoSerializer
SPARK_RDD_COMPRESS=false
SPARK_IO_COMPRESSION_CODEC=lzf

# Spark options in system.property or arguments of spark-submit 
# - SPARK_EXECUTOR_MEMORY, --conf spark.executor.memory
# - SPARK_STORAGE_MEMORYFRACTION, --conf spark.storage.memoryfraction
#SPARK_STORAGE_MEMORYFRACTION=0.5
SPARK_EXECUTOR_MEMORY=1g
#export MEM_FRACTION_GLOBAL=0.005

# Spark options in YARN client mode
# - SPARK_DRIVER_MEMORY, --driver-memory
# - SPARK_EXECUTOR_INSTANCES, --num-executors
# - SPARK_EXECUTOR_CORES, --executor-cores
# - SPARK_DRIVER_MEMORY, --driver-memory
#export EXECUTOR_GLOBAL_MEM=2g
#export executor_cores=2
export SPARK_DRIVER_MEMORY=2g
export SPARK_EXECUTOR_INSTANCES=4
export SPARK_EXECUTOR_CORES=1

# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
# - STORAGE_LEVEL, set MEMORY_AND_DISK, MEMORY_AND_DISK_SER, MEMORY_ONLY, MEMORY_ONLY_SER, or DISK_ONLY
STORAGE_LEVEL=MEMORY_AND_DISK

# for data generation
NUM_OF_PARTITIONS=2
# for running
NUM_TRIALS=1
