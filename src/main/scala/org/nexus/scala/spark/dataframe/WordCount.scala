package org.nexus.dataframe

import org.nexus.general.Context

/**
 * Using RDDs
 */
object WordCount extends App with Context {

  val inputRDD = sparkSession.sparkContext.textFile("src/main/resources/wordcount.txt", 1)
  //inputRDD.take(3).foreach(x => println(x))

  val outRDD = inputRDD.flatMap(x => x.split(" ")).map(x => (x, 1)).reduceByKey(_ + _)
  outRDD.saveAsTextFile("src/main/resources/wordcount-out")
  // Alternate way to write out the RDD output
  //  sparkSession.sparkContext.textFile("src/main/resources/wordcount-out", 3)

  // Print result to console
  //outRDD.collect().foreach(x => println(x))
}
