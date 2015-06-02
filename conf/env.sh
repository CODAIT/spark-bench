
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

# Spark config
# - SPARK_STORAGE_MEMORYFRACTION --conf spark.storage.memoryFraction
# - SPARK_SERIALIZER, --conf spark.serializer
# - SPARK_RDD_COMPRESS, --conf spark.rdd.compress
# - SPARK_IO_COMPRESSION_CODEC, --conf spark.io.compression.codec
# - SPARK_DEFAULT_PARALLELISM, --conf spark.default.parallelism
SPARK_SERIALIZER=org.apache.spark.serializer.KryoSerializer
SPARK_RDD_COMPRESS=false
SPARK_IO_COMPRESSION_CODEC=lzf

# Spark options in 
# - SPARK_EXECUTOR_MEMORY, --conf spark.executor.memory
# - SPARK_STORAGE_MEMORYFRACTION, --conf spark.storage.memoryfraction

# Spark options in YARN client mode
# - SPARK_DRIVER_MEMORY
# - SPARK_EXECUTOR_INSTANCES=60, --num-executors
# - SPARK_EXECUTOR_CORES, --executor-cores
# - SPARK_DRIVER_MEMORY, --driver-memory

# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
export STORAGE_LEVEL=MEMORY_AND_DISK


# for running
NUM_TRIALS=1


SPARK_OPT=
if [ -z "$SPARK_STORAGE_MEMORYFRACTION" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.storage.memoryFraction=${SPARK_STORAGE_MEMORYFRACTION}"
fi
if [ -z "$SPARK_EXECUTOR_MEMORY" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.executor.memory=${SPARK_EXECUTOR_MEMORY}"
fi
if [ -z "$SPARK_SERIALIZER" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.serializer=${SPARK_SERIALIZER}"
fi
if [ -z "$SPARK_RDD_COMPRESS" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.rdd.compress=${SPARK_RDD_COMPRESS}"
fi
if [ -z "$SPARK_IO_COMPRESSION_CODEC" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.io.compression.codec=${SPARK_IO_COMPRESSION_CODEC}"
fi
if [ -z "$SPARK_DEFAULT_PARALLELISM" ]; then
  SPARK_OPT="${SPARK_OPT} --conf spark.default.parallelism=${SPARK_DEFAULT_PARALLELISM}"
fi

YARN_OPT=
if [ "$MASTER" = "yarn" ]; then
  if [ -z "$SPARK_EXECUTOR_INSTANCES" ]; then
    YARN_OPT="${YARN_OPT} --num-executors =${SPARK_EXECUTOR_INSTANCES}"
  fi
  if [ -z "$SPARK_EXECUTOR_CORES" ]; then
    YARN_OPT="${YARN_OPT} --executor-cores =${SPARK_EXECUTOR_CORES}"
  fi
  if [ -z "$SPARK_DRIVER_MEMORY" ]; then
    YARN_OPT="${YARN_OPT} --driver-memory ${SPARK_DRIVER_MEMORY}"
  fi
fi
