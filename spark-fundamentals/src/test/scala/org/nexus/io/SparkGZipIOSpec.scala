package org.nexus.io

import org.apache.hadoop.fs.{FileSystem, Path}
import org.nexus.BaseSpec
import org.nexus.common.ClassPathParser

import java.io.File
import java.net.URI

class SparkGZipIOSpec extends BaseSpec {

  "loading gzip file" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data.csv.gz"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    // check file type is gzip actually
    assert(SparkGZipIO.checkIsGzipType(absoluteFilePathStr))

    val df = SparkGZipIO.readGzipFile(spark, absoluteFilePathURI)
    df.show(10, truncate = false)

    assert(df.count() != 0)
  }

  "loading a gzip file from GCS" should "succeed" in {
    val gcsFilePathStr: String = "gs://xx/sxxx/loans_data.csv.gz"

    val gcsFilePath: Path = new Path(gcsFilePathStr)
    val fs: FileSystem = gcsFilePath.getFileSystem(spark.sparkContext.hadoopConfiguration)

    // Check if the file exists
    assert(fs.exists(gcsFilePath))

    val df = SparkGZipIO.readGzipFile(spark, gcsFilePathStr)
    df.show(10, truncate = false)

    assert(df.count() != 0)

  }
}
