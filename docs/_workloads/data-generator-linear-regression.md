---
layout: page
title: Data Generator - Linear Regression
---
#### Parameters

| Name    | Required | Default  | Description |
| ------- |---------------| ---------| ------------|
| name       | yes | -- | "data-generation-lr" |
| rows      | yes | -- | number of rows to generate |
| cols     | yes | -- | number of columns to generate |
| output   | yes | -- | output file |
| eps      | no | 2 | epsilon factor by which examples are scaled |
| intercepts | no | 0.1 | data intercept |
| partitions | no| 10 | number of partitions|

#### Examples

```hocon
{
  name = "data-generation-lr"
  rows = 100000100
  cols = 24
  output = "/tmp/kmeans-data.csv"
}
```

```hocon
{
  name = "data-generation-lr"
  rows = 100000000
  cols = 24
  output = "/tmp/kmeans-data.parquet"
  eps = 4500
  intercepts = 1.6
  parititions = 10
}
```
