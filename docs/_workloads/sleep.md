---
layout: page
title: Sleep
---

Sleeps the thread. Can be configured to sleep for
a specified number of milliseconds,
or for a random-not-to-exceed number of milliseconds,
or for a random  number of milliseconds less than 3600000L (one hour).

#### Parameters

The Sleep workload can work in two different ways. 

1. Specify a specific number of milliseconds for the thread to sleep
2. Draw from a distribution. Available distributions are uniform random, gaussian, and Poisson

_To Specify A Specific Number Of Milliseconds_  

| Name        | Required | Default  | Description |
| ----------- |---------------| ---------| ------------|
| name       | yes | -- | "sleep" |
| sleepMS     | yes | -- | specific number of milliseconds the thread should sleep |

_To Draw From A Distribution_

| Name        | Required| Default  | Description |
| ----------- |---------------| ---------| ------------|
| name       | yes | -- | "sleep" |
| distribution     | yes | -- | "uniform", "gaussian", or "poisson" |
| min     | Optional for uniform | 0 | Minimum value of the uniform distribution |
| max     | Required for uniform | -- | Maximum number of milliseconds. Uniform distribution will choose a number \[(min or 0), max\] |
| mean    | Required for gaussian and poisson | -- | Mean of the gaussian or Poisson distribution |
| std     | Required for gaussian | -- | Standard deviation for the gaussian distribution |

#### Examples
```hocon
// Sleep the thread for exactly 60000 milliseconds
{
  name = "sleep"
  sleepMS = 60000
}
```
```hocon
// Sleep the thread for a number of milliseconds [0, max] chosen from a uniform distribution
{
  name = "sleep"
  distribution = "uniform"
  max = 60000
}
```
```hocon
// Sleep the thread for a number of milliseconds chosen from a Poisson distribution with a mean of 30000
{
  name = "sleep"
  distribution = "poisson"
  mean = 30000
}
```
```hocon
// Sleep the thread for a number of milliseconds chosen from a gaussian distribution with a mean of 30000 and standard deviation of 1000
{
  name = "sleep"
  distribution = "gaussian"
  mean = 30000
  std = 1000
}
```