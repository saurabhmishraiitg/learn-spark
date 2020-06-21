package org.nexus.scala.spark.rdd

import java.nio.file.{Files, Path, Paths}

import org.apache.commons.io.FileUtils
import org.apache.hadoop.io.compress.{DefaultCodec, GzipCodec}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import wvlet.log.{LogLevel, LogSupport}

object SparkRDD extends LogSupport {

  def getSparkConf(): SparkConf = {
    new SparkConf()
      .set("spark.cores.max", "2")
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
    //      .set("spark.rdd.compress", "false")
    //      .set("spark.hadoop.mapred.output.compress", "false")
    //      .set("spark.io.compression.codec", "org.apache.spark.io.LZ4CompressionCodec")
    //      .set("mapred.output.compress", "false")
    //      .set("fs.alluxio.impl", "alluxio.hadoop.FileSystem")
    //      .setJars(Seq("/Users/xxx/Desktop/utility/alluxio/latest/client/alluxio-2.1.0-client.jar"))
  }

  def getSparkSession(sparkConf: SparkConf): SparkSession = {
    SparkSession.builder()
      .appName("rdd-sample")
      .master("local[*]")
      .config(sparkConf)
      .config("mapred.output.compress", "true")
      //      .config("spark.jars", "/Users/xxx/Desktop/utility/alluxio/latest/client/alluxio-2.1.0-client.jar")
      .getOrCreate()
  }

  def setLogger(): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    wvlet.log.Logger.clearAllHandlers
    wvlet.log.Logger.setDefaultLogLevel(LogLevel.INFO)
    wvlet.log.Logger.setDefaultFormatter(wvlet.log.LogFormatter.SourceCodeLogFormatter)
    //    wvlet.log.Logger.setDefaultFormatter(wvlet.log.LogFormatter.IntelliJLogFormatter)
  }

  def main(args: Array[String]): Unit = {
    setLogger()

    FileUtils.deleteDirectory(new java.io.File("sample"))
    info("Getting the Spark Session")
    val spark = getSparkSession(getSparkConf())

    //TODO 1. Parallelize the DB import in multiple threads based upon splitByKey
    //TODO 2. Filter import on workflowID to generate specific DF
    //TODO 3. Generate table DDL based upon workflowID specific metadata
    val range100 = spark.range(100)
    range100.rdd.coalesce(1).saveAsTextFile("sample", classOf[GzipCodec])

  }
}
