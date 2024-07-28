package org.nexus.knox

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects}
import org.nexus.loader.JSONConfLoader

/**
 * Read data from a remote kerberized cluster using Spark JDBC over Knox server connection
 * 1. Issue with no data getting pulled
 * https://issues.apache.org/jira/browse/SPARK-21063
 *
 * Time Spent : 2hr
 */
object SparkHIVEJDBC {

  val logger: Logger = Logger("SparkHIVEJDBC")

  /**
   * Main method.
   *
   * @param args args
   */
  def main(args: Array[String]): Unit = {
    logger.info("SparkKnoxJDBC Program started")

    val username: String = System.getenv("USER")
    val userHome: String = System.getenv("HOME")

    val password: String = new JSONConfLoader().getConfig("spark-kerberos-connect", "password")
//    (userHome + "/nexus.conf", "org.nexus")

    val hostname : String = new JSONConfLoader().getConfig("spark-kerberos-connect", "hostname")
//    (userHome + "/nexus.conf", "org.nexus")


    val port = "8443"
    val database = "default;ssl=true;sslTrustStore=/tmp/d17-gateway.jks;" +
      "trustStorePassword=12345678;hive.security.authorization.enabled=false;transportMode=http;httpPath=gateway/DEV17/hive"

    JdbcDialects.registerDialect(HiveDialect) // Needed to fix empty dataframe issue

    val df1 = getSparkSession(getSparkConf).read.format("jdbc")
      .option("url", s"jdbc:hive2://${hostname}:${port}/${database}")
      //      .option("driver", "com.mysql.jdbc.Driver")
      .option("driver", "org.apache.hive.jdbc.HiveDriver")
      //            .option("dbtable", "(select col1, col2 from sxxx.test01) test") // Working
      .option("dbtable", s"$username.test01") // Working
      .option("fetchsize", "10") // Needed to fix empty dataframe issue
      .option("user", username)
      .option("password", password)
      .load()

    df1.show()
  }

  /**
   * Adding the HIVE Dialect Explicitly helps parse the data correctly instead of just empty table schema.
   */
  object HiveDialect extends JdbcDialect {

    override def canHandle(url: String): Boolean = url.startsWith("jdbc:hive2")

    override def quoteIdentifier(colName: String): String = s"$colName"
  }

  /**
   * Test against a local mySQL instance the JDBC connect as POC.
   */
  def testLocalMySQLJDBC(): Unit = {
    // Start a docker container for mysql instance
    // docker run -v ~/Desktop/tmp/docker/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8

    val hostname = "localhost"
    val port = "3306"
    val username = "root"
    val password = "root"
    val database = "mysql_docker"

    // parsing input parameters ...

    val df1 = getSparkSession(getSparkConf).read.format("jdbc")
      .option("url", s"jdbc:mysql://${hostname}:${port}/${database}?autoReconnect=true")
      //      .option("driver", "com.mysql.jdbc.Driver")
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("dbtable", "(SELECT * FROM tbl1 limit 3) AS my_table")
      .option("user", username)
      .option("password", password).load()

    df1.show()
  }

  /**
   * Get sparkConf instance.
   *
   * @return SparkConf
   */
  def getSparkConf: SparkConf =
    new SparkConf()
      .setAppName("Learn Spark")
      .setMaster("local[*]")
      .set("spark.cores.max", "2")
      // Skip _SUCCESS files created when writing to disk
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  /**
   * Get instance of spark session for usage
   *
   * @return SparkSession
   */
  def getSparkSession(sparkConf: SparkConf): SparkSession = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()
}
