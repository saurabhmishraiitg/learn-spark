package org.nexus.io

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import java.io.{BufferedInputStream, BufferedReader, InputStream, InputStreamReader}
import java.util.stream.Collectors

/**
 * Read Bzip2 file formats
 */
object SparkBzip2IO extends StrictLogging {

  def readBzip2File(spark: SparkSession, bZip2FilePath: String): DataFrame = {
    logger.info(s"Started reading gzipped file [$bZip2FilePath]")

    spark.read.text(bZip2FilePath)
  }

  def readBzip2Stream(spark: SparkSession, is: InputStream): DataFrame = {
    logger.info("reading bzip2 input stream")

    val bzip2Is = new BZip2CompressorInputStream(new BufferedInputStream(is))
    val fileStr: String = new BufferedReader(new InputStreamReader(bzip2Is)).lines().parallel().collect(Collectors.joining("\n"))

    import spark.sqlContext.implicits._
    val ds: Dataset[String] = spark.createDataset(spark.sparkContext.parallelize(fileStr.stripMargin.lines.toList))

    spark.read.option("header", value = true).option("inferSchema", value = true).csv(ds)
  }
}
