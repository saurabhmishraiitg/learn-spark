package org.nexus.dataframe

import java.util.UUID

import org.nexus.common.Context

/**
 * Working with a JSON file in Spark
 * https://medium.com/data-science-school/practical-apache-spark-in-10-minutes-part-3-dataframes-and-sql-ac36b26d28e5
 */
object IOJsonFile extends App with Context {

  // Load a JSON file to DataFrame
  val jsonFilePath = "src/main/resources/movielens.json"
  val outputPath = "src/main/resources/" + UUID.randomUUID()
  val jsonDF = sparkSession.read.json(jsonFilePath)

  // Filter dataframe on ratings
  println(s" original count : ${jsonDF.count()} , filtered count : ${jsonDF.filter("rating > 4").count()}")
  val filteredDF = jsonDF.filter("rating > 4")

  // Save output as JSON
  //filteredDF.write.json(outputPath)

  // Save output as Parquet file
  filteredDF.write.parquet(outputPath)
}
