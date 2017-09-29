---
layout: page
title: Quickstart
permalink: /quickstart/
---

1. Grab the latest release from the [releases page on Github](https://github.com/SparkTC/spark-bench/releases/latest)

2. Unzip the zip file into whatever directory you like and `cd` into the newly created folder. 

3. Set the environment variable for $SPARK_HOME. Example:
```bash
export SPARK_HOME=/path/to/my/local/install/of/spark/
```

4. Run the examples!
```
# pwd is /path/to/where/you/unzipped/spark-bench_.../
./bin/spark-bench.sh examples/from-docs/minimal-example.conf
```