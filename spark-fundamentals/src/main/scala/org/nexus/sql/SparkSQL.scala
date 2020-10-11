package org.nexus.sql

import org.nexus.common.Context

object SparkSQL extends App with Context {

  val dfTags = sparkSession.read.option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/question_tags_10K.csv")
    .toDF("Id", "Tag")

  dfTags.show(10)

  //Register a table in SPARK catalog
  dfTags.createOrReplaceTempView("so_tags")

  //Access SparkSession catalog specific methods
  sparkSession.catalog.listTables().show(10)

  //Use SQL to access list of tables
  sparkSession.sql("show tables").show(10)

  //Select columns like general SQL queries
  sparkSession.sql("select id, tag as no_tag from so_tags limit 5")
    .show(10)

  // Filter by column value
  sparkSession
    .sql("select * from so_tags where tag = 'php'")
    .show(10)

  // Count number of rows
  // Check otu the approach of writing a multi-line query
  sparkSession
    .sql(
      """select
        |count(*) as php_count
        |from so_tags where tag='php'""".stripMargin)
    .show(10)

  // SQL like
  sparkSession
    .sql(
      """select *
        |from so_tags
        |where tag like 's%'""".stripMargin)
    .show(10)

  //Register and use an UDF
  def prefixSO(s: String): String = s"so_$s"

  sparkSession.udf.register("so_udf", prefixSO _)

  sparkSession.sql("select tag, so_udf(tag) from so_tags limit 10").show(12)

}
