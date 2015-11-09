# global settings

master="minli38.sl.cloud9.ibm.com"
#A list of machines where the spark cluster is running
MC_LIST="minli30 minli31 minli32 minli33 minli34  minli60 minli61 minli62 minli63 minli64" #10 nodes


[ -z "$HADOOP_HOME" ] &&     export HADOOP_HOME=/mnt/nfs_dir/hadoop-2.4.0/hadoop/hadoop-dist/target/hadoop-2.4.0/
# base dir for DataSet
HDFS_URL="hdfs://${master}:9000"
SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE=536870912
# DATA_HDFS=hdfs://SparkBench , "hdfs://${master}:9000/SparkBench", /home/`whoami`/SparkBench

DATA_HDFS="hdfs://${master}:9000/SparkBench"
DATASET_DIR=/home/`whoami`/SparkBench/dataset

SPARK_VERSION=1.5.1  #1.4.0
[ -z "$SPARK_HOME" ] &&     export SPARK_HOME=/mnt/nfs_dir/spark-1.5.1/spark 

#SPARK_MASTER=local
#SPARK_MASTER=local[K]
#SPARK_MASTER=local[*]
#SPARK_MASTER=spark://HOST:PORT
##SPARK_MASTER=mesos://HOST:PORT
##SPARK_MASTER=yarn-client
##SPARK_MASTER=yarn-cluster
SPARK_MASTER=spark://${master}:7077



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
SPARK_STORAGE_MEMORYFRACTION=0.5
#export MEM_FRACTION_GLOBAL=0.005

# Spark options in YARN client mode
# - SPARK_DRIVER_MEMORY, --driver-memory
# - SPARK_EXECUTOR_INSTANCES, --num-executors
# - SPARK_EXECUTOR_CORES, --executor-cores
# - SPARK_DRIVER_MEMORY, --driver-memory
export EXECUTOR_GLOBAL_MEM=6g
export executor_cores=6

# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
# - STORAGE_LEVEL, set MEMORY_AND_DISK or MEMORY_AND_DISK_SER
STORAGE_LEVEL=MEMORY_AND_DISK

# for data generation
NUM_OF_PARTITIONS=10
# for running
NUM_TRIALS=1
