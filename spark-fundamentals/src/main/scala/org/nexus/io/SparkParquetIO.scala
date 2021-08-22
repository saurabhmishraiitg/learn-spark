package org.nexus.io

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Read a parquet file.
 */
class SparkParquetIO {

  val logger: Logger = Logger("SparkParquetIO")

  def readParquetFile(absoluteFilePath: String): Unit = {

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

    val df: DataFrame = spark.read.format("parquet").load(absoluteFilePath)

    df.show(10)
    logger.info(s"df-count [${df.count()}]")
    df.printSchema()
  }

}
