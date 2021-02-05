package org.nexus.knox_jdbc

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkKnoxJDBCIO extends App{

  import org.apache.spark.sql.functions.col
  import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects}

  val HiveDialect = new JdbcDialect {
    override def canHandle(url: String): Boolean = url.startsWith("jdbc:hive2") || url.contains("hive2")

    override def quoteIdentifier(colName: String): String = s"$colName"

  }

  JdbcDialects.registerDialect(HiveDialect)

  val sparkConf = new SparkConf()
    .setAppName("Learn Spark")
    .setMaster("local[*]")
    .set("spark.cores.max", "2")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  val spark = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()

  val jdbcDF = spark.read.format("jdbc")
    .option("url", "jdbc:hive2://knox-prod17.wal-mart.com:8443/default;ssl=true;sslTrustStore=/Users/s0m0158/tmp/cluster/p17/p17-gateway.jks;trustStorePassword=12345678;hive.security.authorization.enabled=false;transportMode=http;httpPath=gateway/PROD17/hive")
    .option("driver", "org.apache.hive.jdbc.HiveDriver")
    .option("dbtable", "s0m0158.gg_reach_us_reg_cntct")
    .option("user", "s0m0158")
    .option("fetchsize", "20")
    .option("password", "mangaReader#2011").load()

//  jdbcDF.show(10, truncate=false)

  jdbcDF.select("`gg_reach_us_reg_cntct.submissionid`").show(3, truncate=false)

  print(jdbcDF.count())
}
