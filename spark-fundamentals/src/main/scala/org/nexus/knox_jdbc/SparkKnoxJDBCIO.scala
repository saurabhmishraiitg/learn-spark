package org.nexus.knox_jdbc

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects}

object SparkKnoxJDBCIO {

  val logger: Logger = Logger("SparkJDBCIO")

  def loadDF(tableName: String) = {
    val HiveDialect = new JdbcDialect {
      override def canHandle(url: String): Boolean = url.startsWith("jdbc:hive2") || url.contains("hive2")

      override def quoteIdentifier(colName: String): String = s"$colName"
    }

    JdbcDialects.registerDialect(HiveDialect)

    val sparkConf = new SparkConf()
      .setAppName("Knox JDBC IO")
      .setMaster("local[*]")
      .set("spark.cores.max", "2")
      // Skip _SUCCESS files created when writing to disk
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

    val spark = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    val username = System.getenv("USER")
    val password = System.getenv("USER_PASSWORD")

    logger.info(s"username [${username}], password [${password}]")

    val jdbcDF = spark.read.format("jdbc")
      .option("url", "jdbc:hive2://knox-prod17.xxx-xx.com:8443/default;ssl=true;sslTrustStore=/Users/sxxx/tmp/cluster/p17/p17-gateway.jks;trustStorePassword=12345678;hive.security.authorization.enabled=false;transportMode=http;httpPath=gateway/PROD17/hive")
      .option("driver", "org.apache.hive.jdbc.HiveDriver")
      //      .option("dbtable", "sxxx.gg_reach_us_reg_cntct")
      .option("dbtable", tableName)
      .option("user", username)
      .option("fetchsize", "20")
      .option("password", password).load()

    //  jdbcDF.show(10, truncate=false)

    jdbcDF.select("`gg_reach_us_reg_cntct.submissionid`").show(3, truncate = false)

    print(jdbcDF.count())
  }
}
