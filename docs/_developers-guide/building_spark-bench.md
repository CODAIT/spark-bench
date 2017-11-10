---
layout: page
title: Building Spark-Bench
permalink: /compilation/
---

## Preparing Your Environment
1. Install SBT according to the instructions for your system: <http://www.scala-sbt.org/0.13/docs/Setup.html>

2. Clone this repo.
```bash
git clone https://github.com/SparkTC/spark-bench.git
cd spark-bench/
```
Make sure you are on the master branch.
```bash
git checkout master
```

3. Change your SBT heap space. Building spark-bench takes more heap space than the default provided by SBT. There are several ways to set these options for SBT, 
this is just one. I recommend adding the following line to your bash_profile:
```bash
export SBT_OPTS="-Xmx1536M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xss2M"
```

If you are working directly on the Spark-Bench code, you are of course welcome to use any text editor or IDE you wish, though 
I would strongly encourage using the community edition of IntelliJ with the Scala, SBT, .gitignore, and Markdown plugins installed. 

## Using Spark-Bench from the Source Code Folder

You can use Spark-Bench from the source code repo without having to use the pre-compiled distribution. 
To do this, you will need to first follow the same steps as you would for installing from the distribution:

1. Make sure you have a local installation of Spark 2.x available to you.
2. Modify the example files to point to your spark-home and master OR set the environment variables.

For more info about the first two steps, see [the installation guide](../_users-guide/installation.md).

Finally, you will need to perform one more step to use Spark-Bench from the source code folder. 
While the distribution folder already contains the precompiled jars,
you will need to create the necessary jars in the source code repo yourself. 
Thankfully, this is really easy! Just run
```bash
sbt assembly
```
which will create the necessary jars in the `target` folder. 

spark-bench.sh looks first for jars in a local `lib/` folder, then in a local `target` folder. Once you have run `sbt assembly`,
You can use spark-bench.sh as usual and it will reference the jars in the target folder.
```bash
./bin/spark-bench.sh docs/_examples/minimal-config-file.md
```

## SBT Commands for Spark-Bench

### Useful Standard SBT Commands
- `sbt compile` compiles the code.
- `sbt refresh` will ensure the dependencies are pulled to your development environment and sync the project with any changes in the build.sbt.
- `sbt clean` cleans compiled class files and other fodder from project. Also has been slightly modified to call `rmDist` in addition to normal functions.
- `sbt test` runs all the unit tests in the project.

### Custom SBT Commands For Spark-Bench
- `sbt assembly` creates the two fat JARs of the project in the `target` folder. `assembly` is an SBT plugin.
- `sbt dist` creates the distribution folder for release and a tar of that same folder. TravisCI runs SBT dist to create the file that is uploaded to Github Releases. 
`dist` is a custom SBT action defined in this project's build.sbt.
- `sbt rmDist` cleans up any distribution files and associated tar files created by `sbt dist`.

## Docs Site
This documenation site that you're reading now is a Jekyll site generated from files in the `docs/` folder.
To see the Jekyll site locally:

1. Follow the instructions [from Github](https://help.github.com/articles/setting-up-your-github-pages-site-locally-with-jekyll/)
regarding installing Ruby, bundler, etc.

2. From the `docs/` folder, run `bundle exec jekyll serve` and navigate in your browser to `127.0.0.1:4000`
