package org.nexus.io

import com.typesafe.scalalogging.Logger
import org.scalatest.FlatSpec

class SparkParquetIOSpec extends FlatSpec {

  val logger: Logger = Logger("SparkParquetIOSpec")

  "reading of parquet file" should "return schema and records" in {
    val filePath = "/Users/sxxx/Desktop/tmp/sample-dataset/gg_reach_us_reg_cntct"
    (new SparkParquetIO).readParquetFile(filePath)
  }
}
