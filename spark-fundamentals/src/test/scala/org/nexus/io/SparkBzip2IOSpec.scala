package org.nexus.io

import org.apache.commons.compress.compressors.bzip2.BZip2Utils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.nexus.BaseSpec
import org.nexus.common.ClassPathParser

import java.io.{File, FileInputStream}
import java.net.URI

class SparkBzip2IOSpec extends BaseSpec {

  "loading bzip2 file" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data.csv.bz2"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    // check file type is gzip actually
    assert(BZip2Utils.isCompressedFilename(absoluteFilePathStr))

    val df = SparkBzip2IO.readBzip2File(spark, absoluteFilePathStr)
    df.show(10, truncate = false)

    assert(df.count() != 0)
  }

  "loading a bzip2 file from GCS" should "succeed" in {
    val gcsFilePathStr: String = "gs://xx/sxxx/loans_data.csv.gz"

    val gcsFilePath: Path = new Path(gcsFilePathStr)
    val fs: FileSystem = gcsFilePath.getFileSystem(spark.sparkContext.hadoopConfiguration)

    // Check if the file exists
    assert(fs.exists(gcsFilePath))

    val df = SparkBzip2IO.readBzip2File(spark, gcsFilePathStr)
    df.show(10, truncate = false)

    assert(df.count() != 0)

  }

  "loading a bzip2 file as a stream" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data.csv.bz2"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    // check file type is gzip actually
    assert(BZip2Utils.isCompressedFilename(absoluteFilePathStr))

    val bzip2Is: FileInputStream = new FileInputStream(absoluteFilePathStr)

    val df = SparkBzip2IO.readBzip2Stream(spark, bzip2Is)
    df.show(10, truncate = false)

    assert(df.count() != 0)
  }
}
