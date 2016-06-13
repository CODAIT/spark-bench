# SparkBench Suite #
## The Spark-specific benchmark suite ##


- Current version: 2.0
- Release date: 2015-5-10

- Contents:

  1. Overview
  2. Getting Started
  3. Advanced Configuration
  4. Possible Issues

---
### OVERVIEW ###

**What's SparkBench?**

SparkBench is a Spark specific benchmarking suite.
It comprises a representative and comprehensive set of workloads belonging to four different application types that currently supported by Spark, including machine learning, graph processing, streaming and SQL queries.

The chosen workloads exhibit different workload characteristics and exercise different system bottlenecks; currently we cover CPU, memory, and shuffle and IO intensive workloads.

It also includes a data generator that allows users to generate arbitrary size of input data.

**Why SparkBench?**

While Spark has been evolving rapidly, the community lacks a comprehensive benchmarking suite specifically tailored for Spark. The purpose of such a suite is to help users to understand the trade-off between different system designs, guide the configuration optimization and cluster provisioning for Spark deployments. In particular, there are four main use cases of SparkBench.
	
Usecase 1. It enables quantitative comparison for Spark system optimizations such as caching policy and memory management optimization, scheduling policy optimization. Researchers and developers can use SparkBench to comprehensively evaluate and compare the performance of their optimization and the vanilla Spark. 
	
Usecase 2. It provides quantitative comparison for different platforms and hardware cluster setups such as Google cloud and Amazon cloud. 
	
Usecase 3. It offers insights and guidance for cluster sizing and provision. It also helps to identify the bottleneck resources and minimize the impact of resource contention.
	
Usecase 4. It allows in-depth study of performance implication of Spark system in various aspects including workload characterization, the study of parameter impact, scalability and fault tolerance behavior of Spark system.
	
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

**Supported Spark releases:**
 
  - Spark 1.2, 1.3, 1.4, 1.5,1.6
 
---
### Getting Started ###

1. System setup and compilation.

	Setup JDK, Hadoop-YARN, Spark runtime environment properly.
	
	Download  wikixmlj package:
	cd to a directory for download and type the next commands
	```
		git clone https://github.com/synhershko/wikixmlj.git
		cd wikixmlj
		mvn package install
	```
	Download/checkout SparkBench benchmark suite

	Run `<SparkBench_Root>/bin/build-all.sh` to build SparkBench.
	
	Rename `<SparkBench_Root>/conf/enc.sh.template` to `env.sh` and set it according to your cluster.
	
2. SparkBench Configurations.
	
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

	`<SparkBench_Root>/<Workload>/bin/gen_data.sh`  
	`<SparkBench_Root>/<Workload>/bin/run.sh`
	
	**Note**
	
	For SQL applications, by default it runs the RDDRelation workload.
	To run Hive workload, execute `<SparkBench_Root>/SQL/bin/run.sh hive`;
	
	For Streaming applications such as TwitterTag,StreamingLogisticRegression
	First, execute `<SparkBench_Root>/SQL/bin/gen_data.sh` in one terminal;
	Second, execute `<SparkBench_Root>/SQL/bin/run.sh` in another terminal;
	
	**Note**
	StreamingLogisticRegression requires the gen_data.sh and run.sh scripts which
	launches Spark applications can run simutaneously.
4. View the result.

	Goto `<SparkBench_Root>/report` to check for the final report.

---
### Advanced Configurations ###

1. Configures for running workloads.

	The `<SparkBench_Root>/bin/applications.lst` file defines the workloads to run when you execute the bin/run-all.sh script under the package folder. Each line in the list file specifies one workload. You can use # at the beginning of each line to skip the corresponding bench if necessary.

	You can also run each workload separately. In general, there are 3 different files under one workload folder.

	`<Workload>/bin/config.sh`      change the workload specific configurations  
	`<Workload>/bin/gen_data.sh`  
	`<Workload>/bin/run.sh`  

2. Spark configuration.

	spark.executors.memory                Executor memory, standalone or YARN mode
    spark.driver.memory                   Driver memory, standalone or YARN mode
	spark.rdd.cache

