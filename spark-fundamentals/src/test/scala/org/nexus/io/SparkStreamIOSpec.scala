package org.nexus.io

import org.nexus.BaseSpec
import org.nexus.common.ClassPathParser

import java.io.{File, FileInputStream}
import java.net.URI

/**
 * Spec class for spark read input stream.
 */
class SparkStreamIOSpec extends BaseSpec {
  "loading csv file as stream" should "succeed" in {
    val relativeFilePath: String = "classpath://loans_data.csv"

    val absoluteFilePathURI: String = ClassPathParser.returnAbsolutePath(relativeFilePath)
    val absoluteFilePathStr: String = new URI(absoluteFilePathURI).getPath

    logger.info(s"absoluteFilePathURI : [$absoluteFilePathURI], absoluteFilePathStr : [$absoluteFilePathStr]")

    // Check if the file exists
    assert(new File(absoluteFilePathStr).exists())

    val fis: FileInputStream = new FileInputStream(absoluteFilePathStr)

    val df = SparkStreamIO.readFromStream(spark, fis)
    df.show(10, truncate = false)
    assert(df.count() != 0)
  }
}
