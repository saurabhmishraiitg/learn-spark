package org.nexus.dataframe

import org.nexus.common.Context

object HelloDataFrame extends App with Context{

  // Create a DataFrame from reading a CSV file
  val dfTags = spark
    .read
    .option("header", "true")
    //Auto infer the schema of the dataframe based upon columns encountered
    .option("inferSchema", "true")
    .csv("src/main/resources/test.csv")
    .toDF("Id", "Tag")

  // Print top n records from the DataFrame
  dfTags.show(1)

  //Prints the schema of the Dataframe i.e. column type, nullable etc.
  dfTags.printSchema()

  dfTags.select("Id").show(3)

  //Filter operation on the Dataframe
  dfTags.filter("tag = 'php'").show(4)

  //Chain functions
  println(s"Number of 'php' tags is ${ dfTags.filter("Tag = 'php'").count()}")

  // DataFrame Query: SQL like query
  dfTags.filter("tag like 'ph%'").show(10)

  //Group by query
  dfTags.groupBy("Tag").count().show(10)
  dfTags.groupBy("Tag").count().filter("count > 5").orderBy("Tag").show(10)

  dfTags.describe().show()
}
