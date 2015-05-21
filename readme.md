# SparkBench Suite #
## The Spark-specific benchmark suite ##


- Current version: 2.0
- Release date: 2015-5-10
- Contact: [Min Li](mailto:minli@us.ibm.com)
- Homepage: https://bitbucket.org/lm0926/sparkbench

- Contents:
  1. Overview
  2. Getting Started
  3. Advanced Configuration
  4. Possible Issues

---
### OVERVIEW ###

**Supported Spark releases:**
 
  - Spark1.2
  - Spark1.3
 
---
### Getting Started ###

1. System setup and compilation.
	Setup JDK, Hadoop-YARN, Spark runtime environment properly.
	
	Download/checkout SparkBench benchmark suite
	
	Run `<SparkBench_Root>/bin/build-all.sh` to build SparkBench.
	
2. SparkBench Configurations.
	
	Make sure below variables has been set:
	
	SPARK_HOME    The Spark installation location
	HADOOP_HOME   The HADOOP installation location
	SPARK_MASTER  Spark master
	HDFS_MASTER	  HDFS master


3. Execute.

	`<SparkBench_Root>/<Workload>/bin/gen_data.sh`
	`<SparkBench_Root>/<Workload>/bin/run.sh`
	
	**note**
	For SQL applications, by default it runs the RDDRelation workload.
	To run Hive workload, execute `<SparkBench_Root>/SQL/bin/run.sh hive`;
	
	For Streaming applications such as TwitterTag,
	First, execute `<SparkBench_Root>/SQL/bin/gen_data.sh` in one terminal;
	Second, execute `<SparkBench_Root>/SQL/bin/run.sh` in another terminal;
	
4. View the result.
	Goto `<SparkBench_Root>/report` to check for the final report.

---
### Advanced Configurations ###

1. Configures for running workloads.

The conf/benchmarks.lst file under the package folder defines the workloads to run when you execute the bin/run-all.sh script under the package folder. Each line in the list file specifies one workload. You can use # at the beginning of each line to skip the corresponding bench if necessary.

You can also run each workload separately. In general, there are 3 different files under one workload folder.

<Workload>/bin/config.sh      change the configurations specific 
<Workload>/bin/gen_data.sh
<Workload>/bin/run.sh
