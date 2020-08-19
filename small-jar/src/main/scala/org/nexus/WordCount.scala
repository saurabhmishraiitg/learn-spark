package org.nexus

import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object WordCount extends App with CommonSparkContext {
  println(args.toList)
  // Check file exists
  println(Files.exists(Paths.get("data/wordcount-input.txt")))
  //  Files.deleteIfExists(Paths.get("data/wordcount-output/*"))
  //  Files.deleteIfExists(Paths.get("data/wordcount-output1"))
  FileUtils.deleteDirectory(FileUtils.getFile("data/wordcount-output1"))
  FileUtils.deleteDirectory(FileUtils.getFile("data/wordcount-output2"))

  // Word Count using RDD
  val textFile = sc.textFile("data/wordcount-input.txt")
  val counts = textFile.flatMap(line => line.split(" "))
    .map(word => (word, 1))
    .reduceByKey(_ + _)
  counts.saveAsTextFile("data/wordcount-output1")

  // Word Count using Dataframe
  val textFileDF = spark.read.text("data/wordcount-input.txt")
  textFileDF.write.mode("overwrite").text("data/wordcount-output2")
}
