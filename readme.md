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
	setup: master address


3. Execute.
	<SparkBench_Root>/<Workload>/bin/gen_data.sh
	<SparkBench_Root>/<Workload>/bin/run.sh
	
4. View the result.
	Goto `<SparkBench_Root>/report` to check for the final report.

---
### Advanced Configurations ###
