package org.nexus.udemy

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

/**
 * Udemy Spark with Scala course Activity.
 */
object AverageFriendsByAge extends App {
  println("Practice problem to identify average number of friends by age from provided dataset!")

  // Suppress verbose logs
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create SparkContext
  val sc = new SparkContext("local[2]", "FriendsAverage")

  println("Load Data from local...")
  val localHome = sys.env("HOME")
  val localHomeRelativePath = "Desktop/tmp/data/udemy-spark/SparkScala/fakefriends.csv"

  val rawDataRDD = sc.textFile(localHome + "/" + localHomeRelativePath, 2)

  println("Split data in key-value pairs...")

  def parseLineFunction(line: String): (Int, Int) = {
    val splitLine = line.split(",")
    val age = splitLine(2).toInt
    val friendCount = splitLine(3).toInt
    (age, friendCount)
  }

  //val sampleLine = "0,Will,33,385"
  //println(parseLineFunction(sampleLine))

  println("Map the lineRDD into a key-value RDD, so that we can do group operation on key...")
  val keyValueRDD = rawDataRDD.map(parseLineFunction)

  println("Perform reduceByKey operation on the key-value RDD to get sum of all friends and total number of people " +
    "Both this info will be required to find the final average...")

  val reducedKeyValueRDD = keyValueRDD.mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))

  // reducedKeyValueRDD.take(4).foreach(println)

  println("Get average friends...")
  val averageFriends = reducedKeyValueRDD.mapValues(x => x._1 / x._2).sortByKey(true, 1)

  println("Print the final output...")
  averageFriends.foreach(println)

  //keyValueRDD.take(5).foreach(println)

  //Stop SparkContext
  sc.stop()
}
