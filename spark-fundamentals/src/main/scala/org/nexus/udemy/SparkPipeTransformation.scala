package org.nexus.udemy

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

/**
 * In this code we demonstrate how to use 'pipe' transformation.
 * Pipe operator in Spark, allows developer to process RDD data using external applications.
 * Sometimes in data analysis, we need to use an external library which may not be written using Java/Scala.
 * Ex: Fortran math libraries. In that case, spark's pipe operator allows us to
 * send the RDD data to the external application.
 * ref: http://blog.madhukaraphatak.com/pipe-in-spark/
 */
object SparkPipeTransformation extends App {

  println("Spark pipe transformation example...")

  // Suppress logs
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create spark context
  val sc = new SparkContext("local[2]", "PipeTransformation")
  val homeLocation = ""

  val localScript = homeLocation + "/spark-scala/src/main/resources/spark-pipe-transform.sh"

  val sampleInput = Seq("Bangalore", "Patna", "Delhi", "Kolkata", "Mumbai", "Chennai")

  // Create input RDD
  val inputRDD = sc.parallelize(sampleInput)

  inputRDD.foreach(println)

  // Perform pipe operations
  val pipedRDD = inputRDD.pipe(localScript)

  pipedRDD.foreach(println)
  // Stop Spark Context
  sc.stop()
}
