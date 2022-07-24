package org.nexus.spark

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * Sample class to validate concept for Delta Lake
 */
object HelloDeltaLake extends App with LazyLogging {
  logger.info("Hello Delta Lake")

  lazy val sparkConf = new SparkConf()
    .setAppName("Learn Spark Delta Lake")
    .setMaster("local[*]")
    .set("spark.cores.max", "2")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  lazy val spark = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()

  //    lazy val sparkSession = SparkSession
  //      .builder()
  //      .config(sparkConf)
  //      .getOrCreate()

  // Create a delta lake table
  val data = spark.range(0, 5)
  data.write.format("delta").save("delta-table")

}
