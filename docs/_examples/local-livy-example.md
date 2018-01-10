---
layout: page
title: Local Livy Example
---

While the other examples demonstrate different ways of launching through `bin/spark-submit`, this example
demonstrates how to launch through a local Livy server.

Instructions for installing and setting up Livy on your local developemnt machine can be found here: https://livy.incubator.apache.org/

It may be necessary to add your Spark-Bench jars to the whitelisted files in `livy.conf`. For example, to run the config file specified below
which uses a distribution of Spark-Bench that has been installed in /opt/spark-bench, the whitelist would need to be updated as such:

```text
# List of local directories from where files are allowed to be added to user sessions. By
# default it's empty, meaning users can only reference remote URIs when starting their
# sessions.
livy.file.local-dir-whitelist = /opt/spark-bench/
```

```hocon
spark-bench = {
  spark-submit-config = [{
    livy = {
      url = "localhost:8998" // Livy runs on port 8998 by default.
      poll-seconds = 1
    }
    spark-bench-jar = "/opt/spark-bench/lib/spark-bench-2.1.1_0.3.0-RELEASE.jar"
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