package org.nexus

/**
 * Read a Given HIVE Table in DF
 */
object ReadHiveTable extends App with CommonSparkContext {

  //  spark.sql("create table temp2 (col1 string)").show(100)

  //  spark.sql("show tables in default").show(100)
  //  spark.sql("select * from sample_table").show(100)
  spark.table("sample_table1").show(100)
  spark.table("sample_table2").show(100)
  //  spark.sql("drop table if exists temp1")
  //  spark.sql("drop table if exists temp2")
}

