---
layout: page
title: Data Generator - KMeans
---

#### Parameters

| Name    | Required | Default  | Description |
| ------- |---------------| ---------| ------------|
| name       | yes | -- | "data-generation-kmeans" |
| rows      | yes | -- | number of rows to generate |
| cols     | yes | -- | number of columns to generate |
| output   | yes | -- | output file |
| save-mode | no | errorifexists | Options are "errorifexists", "ignore" (no-op if exists), and "overwrite" |
| k      | no | 2 | number of clusters generated |
| scaling | no | 0.6 | scaling factor of the the dataset|
| partitions | no| 2 | number of partitions|

#### Examples

```hocon
{
  name = "data-generation-kmeans"
  rows = 100000000
  cols = 24
  output = "/tmp/kmeans-data.csv"
}
```

```hocon
{
  name = "data-generation-kmeans"
  rows = 100000000
  cols = 24
  output = "/tmp/kmeans-data.parquet"
  k = 4500
  scaling = 1.6
  parititions = 10
}
```
