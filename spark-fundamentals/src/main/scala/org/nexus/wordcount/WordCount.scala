package org.nexus.wordcount

import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.nexus.common.Context

/**
 * Word Count
 */
case object WordCount extends App with Context {

  println(args.toList)
  // Check file exists
  println(Files.exists(Paths.get("data/wordcount-input.txt")))
  //  Files.deleteIfExists(Paths.get("data/wordcount-output/*"))
  //  Files.deleteIfExists(Paths.get("data/wordcount-output1"))
  FileUtils.deleteDirectory(FileUtils.getFile("data/wordcount-output1"))
  FileUtils.deleteDirectory(FileUtils.getFile("data/wordcount-output2"))

  // Word Count using RDD
  val textFile = spark.sparkContext.textFile("data/wordcount-input.txt")
  val counts = textFile.flatMap(line => line.split(" "))
    .map(word => (word, 1))
    .reduceByKey(_ + _)
  counts.saveAsTextFile("data/wordcount-output1")

  // Word Count using Dataframe
  val textFileDF = spark.read.text("data/wordcount-input.txt")
  textFileDF.write.mode("overwrite").text("data/wordcount-output2")
}
