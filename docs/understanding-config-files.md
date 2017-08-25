
# Running spark-bench from a Config File

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents


<!-- END doctoc generated TOC please keep comment here to allow auto update -->


You can do everything available in the CLI and more by running spark-bench through a configuration file.

A configuration file defines a Spark context with one or many suites, each with one or many workloads. Suites
are run serially, one after the other, but within a suite workloads can be run in parallel or serially. 

Data generation through a config file is not currently implemented but is in progress.

To use a configuration file, simply specify the path to the configuration file as the only argument. For example:
```bash
bin/spark-bench.sh /path/to/config/file.conf
```

Configuration files are written using Typesafe syntax, roughly a superset of JSON. For more on Typesafe, see their project
[here](https://github.com/typesafehub/config)

An example configuration file is included in the releases of spark-bench. You can also see the same file in the repo: <example-configuration-file.conf>