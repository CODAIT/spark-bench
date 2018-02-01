---
layout: page
title: Logistic Regression
---

Runs LogisticRegression over the input datasets.

`input` in this case is the training dataset. The test dataset is specified by `testfile`.

#### Parameters

| Name        | Required (y/n)| Default  | Description |
| ----------- |---------------| ---------| ------------|
| name           | yes | --    | "lr-bml" |
| input          | yes | --    | path to the training dataset |
| testfile       | yes | --    | path to the test dataset |
| output         | no  | --    | If users wish to capture the actual results of the workload, they can specify an output file here. |
| save-mode | no | errorifexists | Options are "errorifexists", "ignore" (no-op if exists), and "overwrite" |
| numpartitions  | no  | 32    | number of partitions |
| cacheenabled   | no  | false | whether or not the datasets are cached after being read from disk |

#### Examples

```hocon
{
  name = "lr-bml"
  input = "/tmp/training-data.parquet"
  testfile = "/tmp/test-data.parquet"
  output = "/tmp/lr-results.csv"
}
```

```hocon
{
  name = "lr-bml"
  input = "/tmp/training-data.parquet"
  testfile = "/tmp/test-data.parquet"
  output = "/tmp/lr-results.csv"
  cacheenabled = true
}
```