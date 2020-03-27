---
layout: page
title: Installation
---

_This guide assumes you have a working installation of Spark 2.x available and that you have access
to the system where it is installed._

## Installation Summary

1. Grab the latest release from here: <https://github.com/SparkTC/spark-bench/releases/latest>.
2. Unpack the tarball using `tar -xvzf`.
3. `cd` into the newly created folder.
4. Set your environment variables
  - Option 1: modify `SPARK_HOME` and `SPARK_MASTER_HOST` in `bin/spark-bench-env.sh` to reflect your environment. 
  - **Option 2: Recommended!** Modify the config files in the examples and set `spark-home` and `spark-args = { master }` 
  to reflect your environment. (_See below for more info!_)
5. Start using spark-bench!
```bash
./bin/spark-bench.sh /path/to/your/config/file.conf
```

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Installation Summary](#installation-summary)
  - [Setting Environment Variables](#setting-environment-variables)
    - [Option 1: Setting Bash Environment Variables](#option-1-setting-bash-environment-variables)
    - [Option 2: RECOMMENDED! Modifying Example Config Files To Include Environment Info](#option-2-recommended-modifying-example-config-files-to-include-environment-info)
  - [Running the Examples](#running-the-examples)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

### Setting Environment Variables
There are two ways to set the Spark home and master variables necessary to run the examples. 

#### Option 1: Setting Bash Environment Variables
Inside the `bin` folder is a file called `spark-bench-env.sh`. In this folder are two environment variables
that you will be required to set. The first is `SPARK_HOME` which is simply the full path to the top level of your
Spark installation on your laptop or cluster. The second is SPARK_MASTER_HOST which is the same as what you
would enter as `--master` in a spark submit script for this environment. This might be `local[2]` on your laptop,
`yarn` on a Yarn cluster, an IP address and port if you're running in standalone mode, you get the idea!

You can set those environment variables in your bash profile or by uncommenting the lines in `spark-bench-env.sh`
and filling them out in place.

#### Option 2: RECOMMENDED! Modifying Example Config Files To Include Environment Info
For example, in the minimal-example.conf, which looks like this:
```hocon
spark-bench = {
  spark-submit-config = [{
    workload-suites = [
      {
        descr = "One run of SparkPi and that's it!"
        benchmark-output = "console"
        workloads = [
          {
            name = "sparkpi"
            slices = 10
          }
        ]
      }
    ]
  }]
}
```

Add the spark-home and master keys.
```hocon
spark-bench = {
  spark-submit-config = [{
    spark-home = "/path/to/your/spark/install/" 
    spark-args = {
      master = "local[*]" // or whatever the correct master is for your environment
    }
    workload-suites = [
      {
        descr = "One run of SparkPi and that's it!"
        benchmark-output = "console"
        workloads = [
          {
            name = "sparkpi"
            slices = 10
          }
        ]
      }
    ]
  }]
}
```

### Running the Examples
From the spark-bench distribution file, simply run:

```bash
./bin/spark-bench.sh ./examples/minimal-example.conf
```

The example scripts and associated configuration files are a great starting point for learning spark-bench by example.
You can also read more about spark-bench at our [documentation site](https://sparktc.github.io/spark-bench/)

