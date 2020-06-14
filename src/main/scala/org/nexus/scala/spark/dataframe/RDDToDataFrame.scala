package org.nexus.scala.spark.dataframe

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.nexus.scala.spark.general.Context

/**
  * This class demonstrates how to transform an RDD to a DataFrame.
  * https://medium.com/data-science-school/practical-apache-spark-in-10-minutes-part-3-dataframes-and-sql-ac36b26d28e5
  */
object RDDToDataFrame extends App with Context {

  // Load text file to RDD
  val txtFilePath = "src/main/resources/movielens.txt"
  val inputCsvRDD = sparkSession.sparkContext.textFile(txtFilePath)

  // Print elements of RDD
  for (elem <- inputCsvRDD.take(10)) {
    println(s"row : $elem")
  }

  // Define a function to map few splits of the row to Row object type
  def fnc(x: List[String]) = Row(x(0).toInt, x(1), x(2))

  val splitRDD = inputCsvRDD.map(x => x.split(";").toList).map(fnc)

  // Print elements of a Row type RDD
  for (elem <- splitRDD.take(10)) {
    println(s"row : $elem")
  }

  // Define schema for dataframe
  val id_ = StructType(
    Seq(
      StructField(name = "id", dataType = IntegerType, nullable = false),
      StructField(name = "title", dataType = StringType, nullable = false),
      StructField(name = "genre", dataType = StringType, nullable = false)
    )
  )

  // Create DataFrame from RDD
  val rddToDF = sparkSession.createDataFrame(splitRDD, id_)

  // Print elements of DataFrame
  rddToDF.show(10)
}
