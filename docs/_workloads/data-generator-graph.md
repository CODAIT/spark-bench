---
layout: page
title: Data Generator - Graph Generator
---
The graph data generator uses the GraphX logNormalGraph() generator.
This allows users to specify a certain number of vertices for the graph,
and the generator will generate edges between them according to the specified
distribution parameters.

From the [documentation](https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.graphx.util.GraphGenerators$)

```text
Generate a graph whose vertex out degree distribution is log normal.

The default values for mu and sigma are taken from the Pregel paper:

Grzegorz Malewicz, Matthew H. Austern, Aart J.C Bik, James C. Dehnert, Ilan Horn, Naty Leiser, and Grzegorz Czajkowski. 2010. Pregel: a system for large-scale graph processing. SIGMOD '10.

If the seed is -1 (default), a random seed is chosen. Otherwise, use the user-specified seed.
```

**NOTE:** 
The Spark GraphLoader can only accept input in a very specific format.
Due to this limitation, **the Graph Data Generator can only output to `.txt`.**
All other output file formats will cause an error.

#### Parameters

| Name    | Required (y/n)| Default  | Description |  
| ------- |---------------| ---------| ----------- |  
| name       | yes | -- | "graph-data-generator" |  
| output   | yes | -- | output file. MUST BE .TXT FORMAT |
| save-mode | no | errorifexists | Options are "errorifexists", "ignore" (no-op if exists), and "overwrite" |
| vertices      | yes | -- | Number of vertices in the graph |  
| mu | no | 4.0 | mean of out-degree distribution |  
| sigma | no| 1.3 | standard deviation of out-degree distribution |  
| seed | no | -1 | seed for random number generators, -1 causes a random seed to be chosen |  
| partitions | no | 0 | number of partitions |  

#### Examples

```hocon
// Generate a graph with 1,000,000 vertices using the default out degree parameters.
{
  name = "graph-data-generator"
  vertices = 1000
  output = "hdfs:///one-thousand-vertex-graph.txt"
}
```
```hocon
// Generate a graph with 1,000,000 vertices but specify a random seed.
{
  name = "graph-data-generator"
  vertices = 1000
  output = "hdfs:///one-thousand-vertex-graph.txt"
  seed = 104623
}
```
