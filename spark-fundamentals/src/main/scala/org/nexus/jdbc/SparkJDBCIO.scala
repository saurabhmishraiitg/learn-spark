package org.nexus.jdbc

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkJDBCIO {

  val logger: Logger = Logger("SparkJDBCIO")

  /**
   * Main method.
   *
   * @param args args
   */
  def main(args: Array[String]): Unit = {
    logger.info("SparkJDBCIO Program started")

    testLocalMySQLJDBC()
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

    val df1 = getSparkSession.read.format("jdbc")
      .option("url", s"jdbc:mysql://${hostname}:${port}/${database}?autoReconnect=true")
      //      .option("driver", "com.mysql.jdbc.Driver")
      .option("driver", "com.mysql.cj.jdbc.Driver") // Using mysql-connection v8.0.22
      .option("dbtable", "(SELECT * FROM tbl1 limit 3) AS my_table")
      .option("user", username)
      .option("password", password).load()

    df1.show()
  }


  /**
   * Get instance of spark session for usage
   *
   * @return SparkSession
   */
  def getSparkSession: SparkSession = {
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

    spark
  }
}
