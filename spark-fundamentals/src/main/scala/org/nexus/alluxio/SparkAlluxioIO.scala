package org.nexus.alluxio

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Read data from alluxio
 */
object SparkAlluxioIO {

  // logger
  val logger: Logger = Logger("SparkAlluxioIO")

  def createSparkSession: SparkSession = {
    val sparkConf = new SparkConf()
      .setAppName("Learn Spark")
      .setMaster("local[*]")
      .set("spark.cores.max", "2")
      .set("spark.driver.bindAddress", "127.0.0.1")
      .set("fs.alluxio.impl", "alluxio.hadoop.FileSystem")
      // Skip _SUCCESS files created when writing to disk
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

    SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

  }

  def readFromAlluxio() = {
    val spark = createSparkSession

    val df: DataFrame = spark.read.csv("alluxio://localhost:19998/gcs/sect-shop/landing/BARS_DAILY_WALMART.txt")

    df.show(10)

  }

}
