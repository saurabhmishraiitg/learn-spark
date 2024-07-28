package org.nexus.dataframe

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


object CSVFileRead {

  def readCSVFile: Unit = {
    lazy val sparkConf = new SparkConf()
      .setAppName("Read CSV")
      .setMaster("local[*]")
      .set("spark.cores.max", "2")
      // Skip _SUCCESS files created when writing to disk
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
      .set("spark.driver.bindAddress", "127.0.0.1")

    lazy val spark = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()

    // Create a DataFrame from reading a CSV file
    val dfCSV = spark
      .read
      .option("header", "true")
      .option("multiLine", "true")
      //Auto infer the schema of the dataframe based upon columns encountered
      .option("inferSchema", "true")
      .option("escape", "\"")
      .csv("/Users/sxxx/Desktop/contacts.csv")

    // Print top n records from the DataFrame
    dfCSV.show(10, truncate = true)

    println(dfCSV.count)

//    dfCSV.select("Identified Issue Status").distinct.show(10)
    dfCSV.select("Contact Status").distinct.show(10)
  }

  def main(args: Array[String]): Unit = {
    readCSVFile
  }
}
