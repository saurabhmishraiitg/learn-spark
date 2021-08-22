package org.nexus.dataframe

import org.nexus.common.Context

/**
 * Working with Spark Dataframes and common operations.
 * https://medium.com/data-science-school/practical-apache-spark-in-10-minutes-part-3-dataframes-and-sql-ac36b26d28e5
 */
object SparkDataFramePractice extends App with Context {

  // Working with variable substitution in String in Scala
  // https://www.oreilly.com/library/view/scala-cookbook/9781449340292/ch01s05.html
  println(s"HOME dir : ${sys.env("HOME")}");

  val filePath = "src/main/resources/movielens.csv"


  // Getting an environment variable for use.
  //val filePath = sys.env("HOME") + "/github.com/spark-scala/src/main/resources/movielens.csv"


  // Check for existence of file path
  // import java.nio.file.{Paths, Files}
  // println(Files.exists(Paths.get(altFilePath)))


  // Read a file from resources directory of a project
  //import scala.io.Source
  //val readmeText : Iterator[String] = Source.fromResource(filePath).getLines
  //println(readmeText.take(1).mkString)

  val inputCsvDF = sparkSession.read.option("header",true).option("delimiter",";").csv(filePath)
  inputCsvDF.cache()

  inputCsvDF.show(3)

  inputCsvDF.filter("movieId < 3" ).show(5)

  inputCsvDF.groupBy("rating").count().orderBy("rating").show(10)

  inputCsvDF.describe("rating").show(100)

}
