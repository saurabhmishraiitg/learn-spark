package org.nexus.udemy

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

/**
 * Process real movie ratings and figure of some insights
 */
object RatingsCounter extends App {

  // Set logging level
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create instance of SparkContext
  val sc = new SparkContext("local[2]", "RatingsCounter")

  // Load file from Local
  val localHome = sys.env("HOME")
  val localRelativePath = "Desktop/tmp/data/udemy-spark/ml-100k/u.data"

  val dataRDD = sc.textFile(localHome + "/" + localRelativePath, 1)

  // Print 4 records from the top of the input file
  //dataRDD.take(4).foreach(println)

  // (The file format is userID, movieID, rating, timestamp)
  // Split the row to get rating information from each column
  val splitRowRDD = dataRDD.map(_.split("\t")(2))

  // Count number of occurences for each rating
  val ratingCountMap = splitRowRDD.countByValue()

  ratingCountMap.toSeq.sortBy(_._1).foreach(println)

  //Thread.sleep(30000l)

  // Close Spark Context
  sc.stop()
}
