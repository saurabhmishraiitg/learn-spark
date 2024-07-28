package org.nexus.io

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.stream.Collectors
import scala.collection.JavaConverters.asScalaIteratorConverter

/**
 * Reading Input Stream in spark.
 */
object SparkStreamIO extends StrictLogging {

  def readFromStream(spark: SparkSession, is: InputStream): DataFrame = {
    logger.info("reading input stream")
    val fileStr: String = new BufferedReader(new InputStreamReader(is)).lines().parallel().collect(Collectors.joining("\n"))

    import spark.sqlContext.implicits._
    val ds: Dataset[String] = spark.createDataset(spark.sparkContext.parallelize(fileStr.stripMargin.lines.iterator().asScala.toList))

    spark.read.option("header", value = true).option("inferSchema", value = true).csv(ds)
  }

}
