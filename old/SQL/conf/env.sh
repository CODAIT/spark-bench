# for preparation 
numV=1000
numB=14
batch=500
NUM_OF_PARTITIONS=2

# for running
MAX_ITERATION=2
TOLERANCE=0.001
RESET_PROB=0.15

#50%rdd for 40g data 0.152 7g
SPARK_STORAGE_MEMORYFRACTION=0.44

SPARK_SERIALIZER=org.apache.spark.serializer.JavaSerializer
SPARK_IO_COMPRESSION_CODEC=snappy
