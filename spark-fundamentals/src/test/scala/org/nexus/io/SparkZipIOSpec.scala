package org.nexus.io

import org.nexus.BaseSpec
import org.nexus.common.{CheckCompressionType, ClassPathParser}

import java.io.File
import java.net.URI

class SparkZipIOSpec extends BaseSpec {

  "loading zip file with multiple files" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data_multi.zip"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    // check file type is gzip actually
    assert(CheckCompressionType.checkCompressionType(absoluteFilePathStr) == "zip")

    val df = SparkZipIO.readZipFile(spark, absoluteFilePathURI)
    df.show(10, truncate = false)
    assert(df.count() != 0)
  }

  "loading zip file with single file" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data.zip"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    // check file type is gzip actually
    assert(CheckCompressionType.checkCompressionType(absoluteFilePathStr) == "zip")

    val df = SparkZipIO.readZipFile(spark, absoluteFilePathURI)
    df.show(10, truncate = false)
    assert(df.count() != 0)
  }

}
