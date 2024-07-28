package org.nexus

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
//import org.nexus.utils.TinkEncryptDecrypt

/**
 * Read from Hive Knox connection using Spark JDBC
 */
object SparkJDBCHiveKnox extends App {

  val yarn_mode = "local[*]"
  val exec_mem = "1g"
  val exec_cnt = "1"
  val exec_core_cnt = "1"

  val sparkConf = new SparkConf()
    .setMaster(yarn_mode)
    .setAppName("sample-knox-jdbc")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
    .set("spark.executor.memory", exec_mem)
    .set("spark.executor.instances", exec_cnt)
    .set("spark.executor.cores", exec_core_cnt)

  val encryptedJDBCPassword = "xxx"
//  val jdbcPassword = TinkEncryptDecrypt.deEncryptData(encryptedJDBCPassword)
  val jdbcPassword = "xxx"

  val spark = SparkSession
    .builder()
    //    .enableHiveSupport()
    .config(sparkConf)
    .getOrCreate()

  // Creating a JKS file from the KNOX server's pem file
  // openssl s_client -showcerts -connect knox-prod17.xx-xx.com:8443 2>&1 < /dev/null | sed -n '/-----BEGIN/,/-----END/p' > knox-prod17.xx-xx.com.pem
  // keytool -import -keystore p17-gateway.jks -file knox-prod17.xx-xx.com.pem


  //  Class.forName("org.apache.hive.jdbc.HiveDriver") // Databricks Runtime 3.4 and above

  val jdbcHostname = "knox-prod17.xxx-xx.com"
  val jdbcPort = 8443
  val jdbcDatabase = "default;ssl=true;sslTrustStore=/tmp/p17-gateway.jks;trustStorePassword=xxx;" +
    "hive.security.authorization.enabled=false;transportMode=http;httpPath=gateway/PROD17/hive"

  // Create the JDBC URL without passing in the user and password parameters.
  val jdbcUrl = s"jdbc:hive2://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"

  val jdbcUsername = "sxxx"

  //  val connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
  //  val connectionProperties = new Properties()
  //  connectionProperties.setProperty("user", jdbcUsername)
  //  connectionProperties.setProperty("password", jdbcPassword)

  //  val tbl = spark.read.jdbc(jdbcUrl, "ww_gg_reach_dl_secure.gg_reach_entity", connectionProperties).take(10)

  // Note: JDBC loading and saving can be achieved via either the load/save or jdbc methods
  // Loading data from a JDBC source
  val jdbcDF = spark.read
    .format("jdbc")
    .option("oracle.jdbc.mapDateToTimestamp", "false")
    .option("driver", "org.apache.hive.jdbc.HiveDriver")
    .option("url", jdbcUrl)
    .option("dbtable", "ww_gg_reach_dl_secure.gg_reach_entity")
    .option("user", jdbcUsername)
    .option("fetchsize", "20")
    .option("password", jdbcPassword).load()

  //  val jdbcDF = spark.read.format("jdbc").
  //    option("url", "jdbc:hive2://xxx.homeoffice.xxx-xx.com:10000/;principal=hive/_HOST@HADOOP_DEV17.xxx-xx.COM")
  //    .option("dbtable", "xx.hr_assoc_meal_excpt").option("fetchsize", "10000").load

  jdbcDF.printSchema()
  jdbcDF.createTempView("entity")
  print(jdbcDF.count())

  // To fix the issue with casting of Timestamp and other fields.

  import org.apache.spark.sql.jdbc.{JdbcDialects, JdbcType, JdbcDialect}

  val HiveDialect = new JdbcDialect {
    override def canHandle(url: String): Boolean = url.startsWith("jdbc:hive2") || url.contains("hive2")

    override def quoteIdentifier(colName: String): String = {
      s"$colName"
    }
  }

  JdbcDialects.registerDialect(HiveDialect)

  //  jdbcDF.show(10)

  //  spark.sql("select workflow_id from entity limit 10").show(10)

  //  import org.apache.spark.sql.functions.{col, count, isnull, lit, lower, trim, coalesce}

  //  print(jdbcDF.columns(1))
  //  jdbcDF.select(col(jdbcDF.columns(1))).show(10)
}
