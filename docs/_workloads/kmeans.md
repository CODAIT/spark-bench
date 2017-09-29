---
layout: page
title: KMeans
---

Runs the KMeans algorithm over the input dataset. 

**`output` from the workload vs. benchmark output**

The KMeans workload allows users to optionally capture the results of running KMeans over 
their input dataset by specifying a file for the workload output.

Whether or not users specify an output file for the workload, the _benchmark_ output
will be passed upwards and outputted with the workload suite.


#### Parameters

| Name        | Required | Default  | Description |
| ----------- |---------------| ---------| ------------|
| name       | yes | -- | "kmeans" |
| input | yes | -- | the input dataset |
| output | no | -- | If users wish to capture the actual results of the kmeans algorithm, they can specify an output file here. |
| k     | no | 2 | number of clusters |
| seed  | no | 127L | initial values |
| maxiterations  | no | 2 | maximum number of times the algorithm should iterate |  


#### Examples

```hocon
  {
    name = "kmeans"
    input = "/tmp/kmeans-data.parquet"
    k = 10
  }
```

```hocon
  {
    name = "kmeans"
    input = "/tmp/kmeans-data.parquet"
    k = 10
  }
```