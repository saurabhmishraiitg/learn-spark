package org.nexus.io

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.RandomAccessFile
import java.util.zip.GZIPInputStream

/**
 * Read/Write Gzip format files with Spark.
 */
object SparkGZipIO extends StrictLogging {

  def readGzipFile(spark: SparkSession, gzipFilePath: String): DataFrame = {
    logger.info(s"Started reading gzipped file [$gzipFilePath]")

    spark.read.text(gzipFilePath)
  }

  /**
   * Check if the given file at the filePath is of type gzip or not.
   *
   * @param gzipFilePath gzipFilePath
   * @return Boolean
   */
  def checkIsGzipType(gzipFilePath: String): Boolean = {
    var magic: Int = 0

    try {
      val raf: RandomAccessFile = new RandomAccessFile(gzipFilePath, "r")
      magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00)
      raf.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
    magic == GZIPInputStream.GZIP_MAGIC;
  }
}
