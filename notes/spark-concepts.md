# Spark Theory

Concepts and Principles for Spark development

- [Spark Theory](#spark-theory)
  - [Concepts](#concepts)
    - [Dynamic Allocation](#dynamic-allocation)
    - [`groupByKey`, `reduceByKey`, `aggregateByKey`, `combineByKey`](#groupbykey-reducebykey-aggregatebykey-combinebykey)
    - [Predicate PushDown](#predicate-pushdown)
    - [Adaptive Query Execution](#adaptive-query-execution)

## Concepts

### Dynamic Allocation

`Dynamic allocation` allows Spark to dynamically scale the cluster resources allocated to your application based on the workload. When dynamic allocation is enabled and a Spark application has a backlog of pending tasks, it can request executors. When the application becomes idle, its executors are released and can be acquired by other applications.

- To enable this feature use the following configuration settings \
`spark.dynamicAllocation.enabled` = `true`

- To set the initial number of executors \
  - `spark.dynamicAllocation.initialExecutors`
  - The initial number of executors for a Spark application when dynamic allocation is enabled. If `spark.executor.instances` (or its equivalent command-line argument, `--num-executors`) is set to a higher number, that number is used instead.

- `spark.dynamicAllocation.minExecutors` : The lower bound for the number of executors
- `spark.dynamicAllocation.maxExecutors` : The upper bound for the number of executors

- If the spark job is running on YARN then following parameter is required to be set as well \
`spark.shuffle.service.enabled` = `true`

- Setting these properties in spark-submit command

    ```bash
    spark-submit --master yarn-cluster \
        --driver-cores 2 \
        --driver-memory 2G \
        --num-executors 10 \
        --executor-cores 5 \
        --executor-memory 2G \
        --conf spark.dynamicAllocation.minExecutors=5 \
        --conf spark.dynamicAllocation.maxExecutors=30 \
        --conf spark.dynamicAllocation.initialExecutors=10 \ # same as --num-executors 10
        --class com.spark.sql.jdbc.SparkDFtoOracle2 \
        Spark-hive-sql-Dataframe-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    ```

- Setting these properties in SparkContext

    ```scala
    val conf: SparkConf = new SparkConf()
    conf.set("spark.dynamicAllocation.minExecutors", "5");
    conf.set("spark.dynamicAllocation.maxExecutors", "30");
    conf.set("spark.dynamicAllocation.initialExecutors", "10");
    ```

### `groupByKey`, `reduceByKey`, `aggregateByKey`, `combineByKey`

- [Reference](https://stackoverflow.com/questions/43364432/spark-difference-between-reducebykey-vs-groupbykey-vs-aggregatebykey-vs-combineb)
- `groupByKey` can cause out of disk problems as data is sent over the network and collected on the reduce workers.
- `reduceByKey` data are combined at each partition, only one output for one key at each partition to send over the network.
  - Requires combining all your values into another value with the exact same type.
- `aggregateByKey` same as reduceByKey, which takes an initial value.
- `combineByKey` 3 parameters as input
  - Initial value: unlike `aggregateByKey`, need not pass constant always, we can pass a function that will return a new value.
  - `merging` function
  - `combine` function

### Predicate PushDown

`Predicate Pushdown` aims at pushing down the filtering to the `bare metal`, i.e. a data source engine. That is to increase the performance of queries since the filtering is performed at the very low level rather than dealing with the entire dataset after it has been loaded to Spark’s memory and perhaps causing memory issues.
InputFormat such as `parquet` also support `Predicate Pushdown`

- [Reference](https://jaceklaskowski.gitbooks.io/mastering-spark-sql/spark-sql-Optimizer-PushDownPredicate.html)

### Adaptive Query Execution

- Available from Spark 3.0 onwards
- It reoptimizes and adjusts query plans based on runtime statistics collected during the execution of the query. So basically during the running of the job, it will use the metrics from completed stages to optimize the next stages

-
