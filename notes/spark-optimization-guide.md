# Spark Optimization Considerations

This page attempts to list Spark Optimization considerations in Production environment. Will try to provide references where-ever possible for statements being made

- [Spark Optimization Considerations](#spark-optimization-considerations)
  - [Pre-Checks to Memory Tuning](#pre-checks-to-memory-tuning)
  - [Memory Tuning Parameters/Configuration](#memory-tuning-parametersconfiguration)
  - [Programming Tips](#programming-tips)
  - [General Tips](#general-tips)

## Pre-Checks to Memory Tuning

- Check if your dataset is able to fit in memory and not getting spilled to disk during processing
  - **`How to check this?`**
- How frequently garbage collection is getting triggered and how much time consuming it is?
  - **`How to check this?`**

## Memory Tuning Parameters/Configuration

- Control the split size for each task
  - `spark.hadoop.mapreduce.input.fileinputformat.split.maxsize`
  - `spark.hadoop.mapreduce.input.fileinputformat.split.minsize`

## Programming Tips

- Use `reduceByKey`, `aggregateByKey`, `combineByKey` **INSTEAD OF** `groupByKey`
  - `groupByKey` can cause out of disk problems as data is sent over the network and collected on the reduce workers
  - [Details](spark-concepts.md#groupbykey-reducebykey-aggregatebykey-combinebykey)
- Use `coalesce` instead of `repartition` when you want to reduce the number of files, to avoid shuffle of data across executors.

## General Tips

- Enable dynamic memory allocation for your job >> [Dynamic Execution](spark-concepts.md#dynamic-allocation)
- Broadcast large variables to reduce the serialized task size
- Do not copy ALL elements of a large RDD to the driver
- `UDF`'s are Blackbox — Don't Use Them Unless You've Got No Choice
  - When using UDFs `Catalyst` is unable to implement optimizations such as Predicate Pushdown, Constant Folding etc. on the data -> [Reference](https://jaceklaskowski.gitbooks.io/mastering-spark-sql/spark-sql-udfs-blackbox.html)
  - [Comparison](https://stackoverflow.com/a/49103325/5679728)
- Check that your queries are leveraging `Predicate Pushdown` >> [Predicate Pushdown](spark-concepts.md#predicate-pushdown)
