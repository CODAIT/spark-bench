---
layout: page
title: Quickstart
permalink: /quickstart/
---

_This guide assumes you have a working installation of Spark 2.x available and that you have access
to the system where it is installed._

1. Grab the `.tgz` of the latest release from the [releases page on Github](https://github.com/SparkTC/spark-bench/releases/latest).
Although Github also packages tars and zips of the source code, you only need the file whose name begins with `spark-bench`.
For example, `spark-bench_2.1.1_0.2.2-RELEASE_52.tgz`


2. Unpack the file into whatever directory you like and `cd` into the newly created folder. 
```bash
tar -xvzf spark-bench_2.1.1_0.2.2-RELEASE_52.tgz
cd spark-bench/
```

3. Set the environment variable for $SPARK_HOME. Example:
```bash
export SPARK_HOME=/path/to/my/local/install/of/spark/
```
4. Set the environment variable for your Spark master. Example:
```bash
export SPARK_MASTER_HOST=local[*]
```
or 
```bash
export SPARK_MASTER_HOST=yarn
```
or whatever is appropriate for your Standalone or Mesos environment.

_NOTE: You can also modify the config files in the examples folder to set these variables,
but this is the QUICKstart guide so we're not covering that here 🙂
To read more about that, see our [Installation Guide](../users-guide/installation/)_

4. Run the examples!
```bash
./bin/spark-bench.sh examples/minimal-example.conf
```