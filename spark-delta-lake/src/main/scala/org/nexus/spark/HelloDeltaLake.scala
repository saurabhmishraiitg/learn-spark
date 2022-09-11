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
    .set("dfs.client.read.shortcircuit.skip.checksum", "true")
    .set("spark.sql.parquet.compression.codec", "uncompressed")
    .set("spark.eventLog.enabled", "true")
    .set("spark.history.fs.logDirectory", sys.env("HOME")+"/_tmp/spark-events")
    .set("spark.eventLog.dir",sys.env("HOME")+"/_tmp/spark-events")

  lazy val spark = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()

  //    lazy val sparkSession = SparkSession
  //      .builder()
  //      .config(sparkConf)
  //      .getOrCreate()

  // Dataset
  // JSON
  //  val srcFile = sys.env("HOME") + "/_scratch/movielens/movie_dataset_public_final/raw/metadata.json"

//  logger.info(sparkConf.get("spark.eventLog.dir"))

  // CSV
  val srcFilePath = sys.env("HOME") + "/_scratch/movielens/ml-25m/movies.csv"

  // Create a delta lake table
  val data = spark.read
    .format("csv")
    .load(srcFilePath)

  data.show(5)
  //  data.write.format("delta").save("output/delta-table")

//  Thread.sleep(10000)

}
