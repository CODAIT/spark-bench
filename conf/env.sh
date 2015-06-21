# global settings

SPARK_VERSION=1.4.0-SNAPSHOT

#SPARK_MASTER=local
#SPARK_MASTER=local[K]
#SPARK_MASTER=local[*]
#SPARK_MASTER=spark://HOST:PORT
##SPARK_MASTER=mesos://HOST:PORT
##SPARK_MASTER=yarn-client
##SPARK_MASTER=yarn-cluster
SPARK_MASTER=spark://`hostname`:7077
MC_LIST="minli3 minli4 minli5 minli6 minli7 minli8 minli12 minli13 minli14 minli15"
SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE=536870912

# base dir for DataSet
DATASET_DIR=/home/`whoami`/SparkBench/dataset

# base dir HDFS
#export DATA_HDFS=hdfs://SparkBench
#export DATA_HDFS="hdfs://`hostname`:9000/SparkBench"
DATA_HDFS=/home/`whoami`/SparkBench

################# Compress Options #################
# swith on/off compression: 0-off, 1-on
export COMPRESS_GLOBAL=0
export COMPRESS_CODEC_GLOBAL=org.apache.hadoop.io.compress.DefaultCodec
#export COMPRESS_CODEC_GLOBAL=com.hadoop.compression.lzo.LzoCodec
#export COMPRESS_CODEC_GLOBAL=org.apache.hadoop.io.compress.SnappyCodec

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

# Spark options in YARN client mode
# - SPARK_DRIVER_MEMORY, --driver-memory
# - SPARK_EXECUTOR_INSTANCES, --num-executors
# - SPARK_EXECUTOR_CORES, --executor-cores
# - SPARK_DRIVER_MEMORY, --driver-memory

# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
# - STORAGE_LEVEL, set MEMORY_AND_DISK or MEMORY_AND_DISK_SER
STORAGE_LEVEL=MEMORY_AND_DISK

# for preparation
NUM_OF_PARTITIONS=10
# for running
NUM_TRIALS=1
