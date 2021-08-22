package org.nexus

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait CommonSparkContext {

  lazy val sparkConf = new SparkConf()
    .setAppName("Learn Spark")
    .setMaster("local[*]")
    .set("spark.cores.max", "2")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  lazy val spark = SparkSession
    .builder()
    .enableHiveSupport()
    .config(sparkConf)
    .getOrCreate()

  lazy val sc = spark.sparkContext
}
