package org.nexus

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import scala.util.Random

/**
 * Sample Data Generator
 * Params : input_path, output_path, generation_ratio, output_format, yarn_mode
 */
object SampleDataGenerator extends App {

  println("Input Parameters : " + args.toList)
  // TODO : Check if 2 input parameters submitted or not
  // TODO : Check if Input Path exists or not
  // TODO : Check if output path exists or not
  // TODO : Make no. of columns and their datatype as dynamic

  // Sample Data Schema
  //  ID(hashString) | Age (Int) | DOB (Date) | Name (String) | M/F (String)
  val schema = StructType(
    List(
      StructField("ssn", StringType, nullable = true),
      StructField("age", IntegerType, nullable = true),
      StructField("dob", StringType, nullable = true),
      StructField("name", StringType, nullable = true),
      StructField("m_f", StringType, nullable = true)
    )
  )

  val input_path = args(0)
  val output_path = args(1)
  val generation_ratio = args(2).toInt
  val output_format = args(3)
  val yarn_mode = args(4)

  val sparkConf = new SparkConf()
    //    .setMaster("local[*]")
    .setMaster(yarn_mode)
    .setAppName("sample-data-generator")
    .set("spark.cores.max", "2")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  val spark = SparkSession
    .builder()
    .config(sparkConf)
    .getOrCreate()

  //  lazy val sc = spark.sparkContext

  // import org.apache.commons.io.FileUtils
  //  FileUtils.deleteDirectory(FileUtils.getFile(output_path))

  def examples() {
    //[new cls_Employee("", "", 10)]
    // Ref: https://medium.com/@mrpowers/manually-creating-spark-dataframes-b14dae906393
    import spark.implicits._
    val sample_records = Seq(
      (8, "bat"),
      (64, "mouse"),
      (-27, "horse"))

    sample_records.toDF
  }

  // In Python Equivalent
  // sample_record = [['unknown', '1']]

  def generate_hash(rndm: Random): String = {
    rndm.setSeed(rndm.nextInt(Int.MaxValue))
    rndm.alphanumeric.filter(_.isLetterOrDigit).take(4).mkString.toLowerCase() + "-" + rndm.alphanumeric.filter(_.isLetterOrDigit).take(5).mkString.toLowerCase()
  }

  def generate_date(rndm: Random): String = {
    val year = 1900 + rndm.nextInt(120)
    val month = "%02d".format(1 + rndm.nextInt(11))
    val date = "%02d".format(1 + rndm.nextInt(27)) // Skipping dates beyond 28 to avoid risk of Feb month
    year + "-" + month + "-" + date
  }

  def generate_name(rndm: Random): String = {
    rndm.setSeed(rndm.nextInt(Int.MaxValue))

    rndm.alphanumeric.filter(_.isLetter).take(7).mkString.toUpperCase()
  }

  def generate_age(rndm: Random): Int = {
    rndm.nextInt(100)
  }

  def generate_m_f(rndm: Random): String = {
    if (rndm.nextBoolean()) "M" else "F"
  }

  def generate_rows(gen_ratio: Int): Seq[(String, Int, String, String, String)] = {
    val rndm = scala.util.Random

    // Initial Value
    var row_seq = Seq((generate_hash(rndm), generate_age(rndm), generate_date(rndm), generate_name(rndm), generate_m_f(rndm)))

    //    1 to generationRatio foreach (x => println(rndm.alphanumeric.filter(_.isLetterOrDigit).take(10).mkString.toLowerCase()))
    //  Random.alphanumeric.filter(_.isDigit).take(3).mkString

    1 to gen_ratio foreach { _ => row_seq = row_seq :+ (generate_hash(rndm), generate_age(rndm), generate_date(rndm), generate_name(rndm), generate_m_f(rndm)) }
    row_seq
  }

  //  val broadcast_gen_ratio = spark.sparkContext.broadcast(generation_ratio)
  import spark.implicits._

  val inputDF = spark.read.text(input_path)

  val outputDF = inputDF.flatMap(_ => generate_rows(generation_ratio))

  val outputDFRenamed = outputDF.withColumnRenamed("_1", "ssn")
    .withColumnRenamed("_2", "age")
    .withColumnRenamed("_3", "dob")
    .withColumnRenamed("_4", "name")
    .withColumnRenamed("_5", "m_f")

  output_format match {
    case "csv" => outputDFRenamed.write.mode(SaveMode.Overwrite).csv(output_path)
    case "parquet" => outputDFRenamed.write.mode(SaveMode.Overwrite).parquet(output_path)
    case "orc" => outputDFRenamed.write.mode(SaveMode.Overwrite).orc(output_path)
  }
}
