package org.nexus

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
 * Create random local HIVE table for using in code
 */
object CreateRandomTable extends App with CommonSparkContext {

  // Ways to create a Sample DF
  // https://medium.com/@mrpowers/manually-creating-spark-dataframes-b14dae906393
  // Generate sample HIVE table for comparison

  val someData = Seq(
    Row(1, "bat", "delhi1"),
    Row(2, "mouse1", "ahmedabad"),
    Row(3, "horse", "chennai"),
    Row(5, "monkey", "guwahati")
  )

  val someSchema = List(
    StructField("key", IntegerType, true),
    StructField("val1", StringType, true),
    StructField("val2", StringType, true)
  )

  val someDF = spark.createDataFrame(spark.sparkContext.parallelize(someData),
    StructType(someSchema)
  )

  someDF.repartition(1).write.saveAsTable("sample_table2")
}
