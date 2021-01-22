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
- Increase executor memory and cores
  - `spark-shell --executor-memory 15G --executor-cores 8`
- Sample spark-examples program to test spark on a cluster
  - SparkPi : Calculates the value of `pi` to given degree of precision. Requires no input / output path
    - `spark-submit --class org.apache.spark.examples.SparkPi --master yarn --deploy-mode client /usr/hdp/2.6.5.0-292/spark2/examples/jars/spark-examples_2.11-2.3.0.2.6.5.0-292.jar 10`
  - WordCount : Takes input path and prints wordcount on console
    - `spark-submit --class org.apache.spark.examples.JavaWordCount --master yarn --deploy-mode client /usr/hdp/2.6.5.0-292/spark2/examples/jars/spark-examples_2.11-2.3.0.2.6.5.0-292.jar <input_path>`
- Start a `spark-shell` or `spark-sql` client in CLIENT mode
  - Setting the queue
  - Setting the job name in YARN
  - Setting the driver memory
  - Setting the deploy-mode
  - Setting the log file to control the log verbosity
  - `spark-shell --files /u/users/$USER/log4j.properties --master yarn --deploy-mode client --driver-java-options \"-Dlog4j.configuration=file:/u/users/$USER/log4j.properties\" --driver-memory 4g --name \"spark-shell-debug\" --queue ggdataload`
- `CONNECT` to DB2

  ```scala
  user="username"
  url="jdbc:db2://XXXX.wal-mart.com:59884/XXX:securityMechanism=18;sslConnection=true;sslKeyStoreLocation=<path-to-file>/ssl/DB2SNKUS.sp01.wal-mart.com.pfx;sslKeyStorePassword=<<password>>;sslKeyStoreType=PKCS12;sslTrustStoreLocation=<path-to-file>/ssl/DB2SNKUS.sp01.wal-mart.com.jks;"
  prop = {"user": user, "driver": "com.ibm.db2.jcc.DB2Driver", "ssl": "true", "sslmode" : "require"}
  table = "DPSTTHRT.TABLE_NAME"
  df = spark.read.jdbc(url=url, table=table, properties=prop)
  ```
