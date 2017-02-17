# Benchmark Suite for Apache Spark #

- Current version: 2.0
- Release date: 2015-5-10

- Contents:

  1. Overview
  2. Getting Started
  3. Advanced Configuration
  4. Possible Issues

---
### OVERVIEW ###

**What's Benchmark Suite for Apache Spark ?**

Spark-Bench is a benchmarking suite specific for Apache Spark.
It comprises a representative and comprehensive set of workloads belonging to four different application types that currently supported by Apache Spark, including machine learning, graph processing, streaming and SQL queries.

The chosen workloads exhibit different workload characteristics and exercise different system bottlenecks; currently we cover CPU, memory, and shuffle and IO intensive workloads.

It also includes a data generator that allows users to generate arbitrary size of input data.

**Why the Benchmark Suite for Apache Spark ?**

While Apache Spark has been evolving rapidly, the community lacks a comprehensive benchmarking suite specifically tailored for Apache Spark. The purpose of such a suite is to help users to understand the trade-off between different system designs, guide the configuration optimization and cluster provisioning for Apache Spark deployments. In particular, there are four main use cases of Spark-Bench.
	
Usecase 1. It enables quantitative comparison for Apache Spark system optimizations such as caching policy and memory management optimization, scheduling policy optimization. Researchers and developers can use Spark-Bench to comprehensively evaluate and compare the performance of their optimization and the vanilla Apache Spark. 
	
Usecase 2. It provides quantitative comparison for different platforms and hardware cluster setups such as Google cloud and Amazon cloud. 
	
Usecase 3. It offers insights and guidance for cluster sizing and provision. It also helps to identify the bottleneck resources and minimize the impact of resource contention.
	
Usecase 4. It allows in-depth study of performance implication of Apache Spark system in various aspects including workload characterization, the study of parameter impact, scalability and fault tolerance behavior of Apache Spark system.
	
**Machine Learning Workloads:**

- Logistic Regression
- Support Vector Machine
- Matrix Factorization

**Graph Computation Workloads:**

- PageRank
- SVD++
- Triangle Count

**SQL Workloads:**

- Hive
- RDD Relation

**Streaming Workloads:**

- Twitter Tag
- Page View

**Other Workloads:**

- KMeans, LinearRegression, DecisionTree, ShortestPaths, LabelPropagation, ConnectedComponent, StronglyConnectedComponent, PregelOperation

**Supported Apache Spark releases:**
 
  - Spark 2.0.1, this code is branched for release 2.0.1, note that these versions need a later version of scala and as such there are changes to pom files. 
 
---
### Getting Started ###

1. System setup and compilation.

	Setup JDK, Apache Hadoop-YARN, Apache Spark runtime environment properly.
	
	Download  wikixmlj package:
	cd to a directory for download and type the next commands
	```
		git clone https://github.com/synhershko/wikixmlj.git
		cd wikixmlj
		mvn package install
	```
	Download/checkout Spark-Bench benchmark suite

	Run `<SPARK_BENCH_HOME>/bin/build-all.sh` to build Spark-Bench.
	
	Copy `<SparkBench_Root>/conf/env.sh.template` to `<SparkBench_Root>/conf/env.sh`, and set it according to your cluster.
	
2. Spark-Bench Configurations.
	
	Make sure below variables has been set:
	
	SPARK_HOME    The Spark installation location  
	HADOOP_HOME   The HADOOP installation location  
	SPARK_MASTER  Spark master  
	HDFS_MASTER	  HDFS master  

    Local mode:         
            `DATA_HDFS="file:///home/`whoami`/SparkBench"`
            `SPARK_MASTER=local[2]`
            `MC_List=""`


3. Execute.

    **Scala version:**
    
	`<SPARK_BENCH_HOME>/<Workload>/bin/gen_data.sh`  
	`<SPARK_BENCH_HOME>/<Workload>/bin/run.sh`
	
    **Java version:**
    
	`<SparkBench_Root>/<Workload>/bin/gen_data_java.sh`  
	`<SparkBench_Root>/<Workload>/bin/run_java.sh`	
	
	**Note for SQL applications**
	
	For SQL applications, by default it runs the RDDRelation workload.
	To run Hive workload, execute `<SPARK_BENCH_HOME>/SQL/bin/run.sh hive`;
	
	**Note for streaming applications**
	For Streaming applications such as TwitterTag,StreamingLogisticRegression
	First, execute `<SPARK_BENCH_HOME>/Streaming/bin/gen_data.sh` in one terminal;
	Second, execute `<SPARK_BENCH_HOME>/Streaming/bin/run.sh` in another terminal;

        In order run a particular streaming app (default: PageViewStream):
            You need to pass a subApp parameter to the gen_data.sh or run.sh like this:
                  <SPARK_BENCH_HOME>/Streaming/bin/run.sh TwitterPopularTags
            *Note: some subApps do not need the data_gen step. In those you will get a "no need" string in the output.

        You can make a certain subApp default by changing Streaming/conf/env.sh and changing the subApp= line with your choice of the streaming application.
	
    In addition, StreamingLogisticRegression requires the `gen_data.sh` and `run.sh` scripts which
	launches Apache Spark applications can run simultaneously.
4. View the result.

	Goto `<SPARK_BENCH_HOME>/report` to check for the final report.

---
### Advanced Configurations ###

1. Configuration for running workloads.

	The `<SPARK_BENCH_HOME>/bin/applications.lst` file defines the workloads to run when you execute the bin/run-all.sh script under the package folder. Each line in the list file specifies one workload. You can use # at the beginning of each line to skip the corresponding bench if necessary.

	You can also run each workload separately. In general, there are 3 different files under one workload folder.

	`<Workload>/bin/config.sh`      change the workload specific configurations  
	`<Workload>/bin/gen_data.sh`  
	`<Workload>/bin/run.sh`  

2. Apache Spark configuration.

	spark.executors.memory                Executor memory, standalone or YARN mode
    	spark.driver.memory                   Driver memory, standalone or YARN mode
	spark.rdd.cache

