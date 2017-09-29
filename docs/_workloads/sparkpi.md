---
layout: page
title: SparkPi
---

Borrowing from the classic Spark example, this workload computes an approximation of pi.
From the Spark examples page: <https://spark.apache.org/examples.html>
```
Spark can also be used for compute-intensive tasks. This code estimates π
by "throwing darts" at a circle. We pick random points in the unit square
((0, 0) to (1,1)) and see how many fall in the unit circle. The fraction
should be π / 4, so we use this to get our estimate.
```

SparkPi is particularly useful for exercising the computing power of Spark 
without the consideration of heavy I/O from data-reliant workloads.

The timing result of SparkPi will include the estimate of Pi that was generated.

#### Parameters

| Name        | Required | Default  | Description |
| ----------- |---------------| ---------| ------------|
| name       | yes | -- | "sparkpi" |
| slices      | no | 2 | Number of partitions that will be spawned |

#### Examples

```hocon
{
  name = "sparkpi"
  slices = 500000000
}
```

```hocon
{
  name = "sparkpi"
}
```