<!-- 
 (C) Copyright IBM Corp. 2015 - 2017

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# spark-bench
## Benchmark Suite for Apache Spark

[![GitHub issues](https://img.shields.io/github/release/SparkTC/spark-bench.svg)](https://github.com/SparkTC/spark-bench/releases/latest)
[![Build Status](https://travis-ci.org/SparkTC/spark-bench.svg?branch=master)](https://travis-ci.org/SparkTC/spark-bench)
[![codecov](https://codecov.io/gh/SparkTC/spark-bench/branch/master/graph/badge.svg)](https://codecov.io/gh/SparkTC/spark-bench)
<a href="https://github.com/SparkTC/spark-bench#boards?repos=40686427"><img src="https://raw.githubusercontent.com/ZenHubIO/support/master/zenhub-badge.png"></a>

Visit the docs website: <https://sparktc.github.io/spark-bench/>

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents

- [Current VS. Legacy Version](#current-vs-legacy-version)
- [Current Spark version supported by spark-bench: 2.1.1](#current-spark-version-supported-by-spark-bench-211)
- [Documentation](#documentation)
- [Installation](#installation)
- [Building It Yourself](#building-it-yourself)
- [Running the Examples From The Distribution](#running-the-examples-from-the-distribution)
  - [Creating the Distribution Folder](#creating-the-distribution-folder)
  - [Setting Environment Variables](#setting-environment-variables)
  - [Running the Examples](#running-the-examples)
- [Previewing the Github Pages Site Locally](#previewing-the-github-pages-site-locally)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->
 
## Current VS. Legacy Version

spark-bench has recently gone through an extensive rewrite.
While we think you'll like the new capabilities, it is not quite feature complete with the previous version of spark-bench.
Many of the workloads that were available in the legacy have not yet been ported over, but they will be!

In the meantime, if you would like to see the old version of spark-bench, it's preserved in [the legacy branch](https://github.com/SparkTC/spark-bench/tree/legacy).

You can also grab the last official release of the legacy version [from here](https://github.com/SparkTC/spark-bench/releases/tag/SparkBench_spark-v1.6).

## Current Spark version supported by spark-bench: 2.1.1


## Documentation
Visit the docs website: <https://sparktc.github.io/spark-bench/>
 
## Installation 

1. Grab the latest release from here: <https://github.com/ecurtin/spark-bench/releases/latest>.
2. Unpack the tarball using `tar -xvzf`.
3. `cd` into the newly created folder.
4. Modify `SPARK_HOME` and `SPARK_MASTER_HOST` in `bin/spark-bench-env.sh` to reflect your environment. 
5. Start using spark-bench!


## Building It Yourself

Alternatively, you can also clone this repo and build it yourself. 

First, install SBT according to the instructions for your system: <http://www.scala-sbt.org/0.13/docs/Setup.html>

Clone this repo.
```bash
git clone https://github.com/ecurtin/spark-bench.git
cd spark-bench/
```
The latest changes will always be on develop, the stable version is master. Optionally check out develop here, or skip this step to stay on master.
```bash
git checkout develop
```
Building spark-bench takes more heap space than the default provided by SBT. There are several ways to set these options for SBT, 
this is just one. I recommend adding the following line to your bash_profile:
```bash
export SBT_OPTS="-Xmx1536M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M"
```
Now you're ready to test spark-bench, if you so desire.
```bash
sbt test
```
And finally to build the distribution folder and associated tar file.
```bash
sbt dist
```


## Running the Examples From The Distribution

The spark-bench distribution comes bundled with example scripts and configuration files that should run out out the box
with only very limited setup.

### Creating the Distribution Folder
If you installed spark-bench by unpacking the tar file, you're ready to go. If you cloned the repo, first run
`sbt dist` and then change into that generated folder.

### Setting Environment Variables
Inside the `bin` folder is a file called `spark-bench-env.sh`. In this folder are two environment variables
that you will be required to set. The first is `SPARK_HOME` which is simply the full path to the top level of your
Spark installation on your laptop or cluster. The second is SPARK_MASTER_HOST which is the same as what you
would enter as `--master` in a spark submit script for this environment. This might be `local[2]` on your laptop,
`yarn` on a Yarn cluster, an IP address and port if you're running in standalone mode, you get the idea!

You can set those environment variables in your bash profile or by uncommenting the lines in `spark-bench-env.sh`
and filling them out in place.

### Running the Examples
From the spark-bench distribution file, simply run:

```bash
./examples/multi-submit-sparkpi/multi-submit-example.sh
```

The example scripts and associated configuration files are a great starting point for learning spark-bench by example.
The kmeans example shows some examples of using the spark-bench CLI while the multi-submit example shows more
thorough usage of a configuration file.


## Previewing the Github Pages Site Locally

The spark-bench documentation at <https://sparktc.github.io/spark-bench/> is generated from files in the `docs/` folder.
To see the Jekyll site locally:

1. Follow the instructions [from Github](https://help.github.com/articles/setting-up-your-github-pages-site-locally-with-jekyll/)
regarding installing Ruby, bundler, etc.

2. From the `docs/` folder, run `bundle exec jekyll serve` and navigate in your browser to `127.0.0.1:4000`
