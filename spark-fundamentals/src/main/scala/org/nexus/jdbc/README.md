# Spark JDBC Connector  - SparkJDBCIO

- [Spark JDBC Connector  - SparkJDBCIO](#spark-jdbc-connector----sparkjdbcio)
  - [Changelog](#changelog)
  - [Objective](#objective)
  - [References](#references)

## Changelog

- 25/01/20 : Init

## Objective

Aim of this work is to

- Connect to REACH DB in Azure
- Pull data from templates table
- Store data into Alluxio for quick read and processing
- Pull out meaningful table metadata details for REACH
- Generate dynamic DDL query for each workflow

## References

- `LogSupport` use to enable colorful loggers
- Creating SparkSession using provided SparkConf

```scala
  def getSparkConf(): SparkConf = {
    new SparkConf()
      .set("spark.cores.max", "2")
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
  }

  def getSparkSession(sparkConf: SparkConf): SparkSession = {
    SparkSession.builder()
      .appName("mapr-flight")
      .master("local[*]")
      .config(sparkConf)
      .getOrCreate()
  }
```

- You can create a JDBC connection using SparkSession as follows

```scala
    val jdbcDF = spark.read
      .format("jdbc")
      //.option("driver","com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://prod.wxxx.com:3306?tinyInt1isBit=false&zeroDateTimeBehavior=convertToNull")
      //.option("dbtable", "reach.tasks") //Use either query or dbtable option
      .option("query", "select * from reach.tasks limit 10")
      .option("user", "xxx")
      .option("password", "xxx")
      .load()
```

- Spark JDBC Official User Guide
  - `https://spark.apache.org/docs/latest/sql-data-sources-jdbc.html`
