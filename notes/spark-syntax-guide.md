# `Spark` Syntax Guide

Commonly used syntax examples/snippets in both Python/Scala

- [`Spark` Syntax Guide](#spark-syntax-guide)
  - [Python](#python)
  - [Scala](#scala)
  - [General](#general)

## Python

## Scala

## General

- Provide `log4j.properties` file at runtime for `spark-shell`/`spark-sql`
  
  ```bash
  spark-sql --files <file_path>/log4j.properties --conf "spark.executor.extraJavaOptions='-Dlog4j.configuration=log4j.properties'" --driver-java-options "-Dlog4j.configuration=file:<file_path>/log4j.properties"
  ```

- Provide external jars to `spark-shell` context
  `export SPARK_CLASSPATH=$SPARK_CLASSPATH:/u/users/s0m0158/mysql-connector-java-5.1.40-bin.jar`
- Increase driver memory for `spark-shell`, `spark-sql` to avoid failure in case big joins
  - `spark-shell --driver-memory 4g`
  - `spark-sql --driver-memory 4g`
  - You can check that the properties have reflected in the session by going to the GUI `Configurations` tab
- Sample spark-examples program to test spark on a cluster
  - SparkPi : Calculates the value of `pi` to given degree of precision. Requires no input / output path
    - `spark-submit --class org.apache.spark.examples.SparkPi --master yarn --deploy-mode client /usr/hdp/2.6.5.0-292/spark2/examples/jars/spark-examples_2.11-2.3.0.2.6.5.0-292.jar 10`
  - WordCount : Takes input path and prints wordcount on console
    - `spark-submit --class org.apache.spark.examples.JavaWordCount --master yarn --deploy-mode client /usr/hdp/2.6.5.0-292/spark2/examples/jars/spark-examples_2.11-2.3.0.2.6.5.0-292.jar <input_path>`
  