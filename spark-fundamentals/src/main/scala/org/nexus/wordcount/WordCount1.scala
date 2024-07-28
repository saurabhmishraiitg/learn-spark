package org.nexus.wordcount

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

object WordCount1 {

  def main(args: Array[String]): Unit = {

    if (args.length == 4) {
      println(s"Insufficient parameters passed. [${args.length}], [${args.toList.mkString}]")
      System.exit(1)
    }

    println(s"parameters passed [${args.toList.mkString}]")

    val sparkMaster = "local[*]" //args(0)
    val appName = "word-count" //args(1)
    val inputFile = "gs://7a6d4d3560ff58c28cef0096a7dbb16086aab77e53cab84d622cbd9794c90d/sect-shop/landing" //args(2)
    val outputPath = "gs://7a6d4d3560ff58c28cef0096a7dbb16086aab77e53cab84d622cbd9794c90d/sect-shop/landing1" //args(3)


    val sparkConf = new SparkConf()
      .setAppName(appName)
      .setMaster(sparkMaster)
      .set("spark.cores.max", "2")
      // Skip _SUCCESS files created when writing to disk
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
      .set("fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .set("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .set("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/sxxx/Desktop/tmp/gcloud/wmt-ww-gg-gec-dev/svc-gec-gcs-load-2020-12-14.json")
    //      .set("spark.sql.orc.enabled","true")


    val spark = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    // Word Count using RDD
    val textFile = spark.sparkContext.textFile(inputFile)
    val counts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    // Write out the DF to a file
    spark.createDataFrame(counts).write.mode("overwrite").csv(outputPath)

    // Print Words Sorted in Descending Order
    spark.createDataFrame(counts).orderBy(col("_2").desc).show(10, truncate = false)
  }
}
