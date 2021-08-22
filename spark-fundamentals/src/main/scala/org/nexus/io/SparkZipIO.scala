package org.nexus.io

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.input.PortableDataStream
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.{BufferedReader, InputStreamReader}
import java.util.zip.ZipInputStream

/**
 * Read Zip File formats.
 */
object SparkZipIO extends StrictLogging {

  def readZipFile(spark: SparkSession, zipFilePath: String): DataFrame = {
    logger.info(s"Started reading zipped file [$zipFilePath]")

    val rdd: RDD[String] = spark.sparkContext.binaryFiles(zipFilePath, 1)
      .flatMap { case (name: String, content: PortableDataStream) =>
        val zis = new ZipInputStream(content.open)
        Stream.continually(zis.getNextEntry)
          .takeWhile(_ != null)
          .flatMap { _ =>
            val br = new BufferedReader(new InputStreamReader(zis))
            Stream.continually(br.readLine()).takeWhile(_ != null)
          }
      }

    import spark.sqlContext.implicits._
    spark.createDataset(rdd).toDF("col1")
  }

}
