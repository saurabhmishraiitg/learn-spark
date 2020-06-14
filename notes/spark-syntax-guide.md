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
  