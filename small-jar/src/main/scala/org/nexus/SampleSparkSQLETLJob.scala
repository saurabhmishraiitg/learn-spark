package org.nexus

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.nexus.SampleDataGenerator.{args, exec_cnt, input_path, spark}

/**
 * Sample Job for Spark-SQL ETL
 * args - input, output
 * - decrypt age
 * - find count of male/female by age aggregate
 * - decrypt dob
 * - find count of male/female by year of birth
 */
object SampleSparkSQLETLJob extends App {

  //  val input_path = args(0)
  //  val output_path = args(1)
  val yarn_mode = args(0)
  val exec_mem = args(1)
  val exec_cnt = args(2)
  val exec_core_cnt = args(3)

  val source_table = "wmt_ww_gg_reach_highsecure_poc.sample_data_generator_03_encrypt"
  val output_table_1 = "wmt_ww_gg_reach_highsecure_poc.sample_data_age_distribution_01"
  val output_table_2 = "wmt_ww_gg_reach_highsecure_poc.sample_data_year_distribution_01"

  val sparkConf = new SparkConf()
    .setMaster(yarn_mode)
    .setAppName("sample-etl-job")
    // Skip _SUCCESS files created when writing to disk
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
    .set("spark.executor.memory", exec_mem)
    .set("spark.executor.instances", exec_cnt)
    .set("spark.executor.cores", exec_core_cnt)


  val spark = SparkSession
    .builder()
    .enableHiveSupport()
    .config(sparkConf)
    .getOrCreate()

  import spark.implicits._

  //create temporary function ptyUnprotectStr as 'com.protegrity.hive.udf.ptyUnprotectStr';
  //create temporary function ptyUnprotectInt AS 'com.protegrity.hive.udf.ptyUnprotectInt';
  spark.sql("create temporary function ptyUnprotectStr as 'com.protegrity.hive.udf.ptyUnprotectStr'")
  spark.sql("create temporary function ptyUnprotectInt as 'com.protegrity.hive.udf.ptyUnprotectInt'")

  val inputDF = spark.sql("select ssn_encrypt, age_encrypt, dob_encrypt, name_decrypt, m_f_decrypt " +
    //    "ptyUnprotectStr(ssn_encrypt, 'HIPAA_ASCII_PRIVATE') AS ssn_decrypt," +
    //    "ptyUnprotectInt(age_encrypt, 'HIPAA_INT') AS age_decrypt," +
    //    "ptyUnprotectStr(dob_encrypt, 'HIPAA_ASCII_CONFID') AS dob_decrypt," +
    //    "name_decrypt AS name_decrypt,m_f_decrypt AS m_f_decrypt " +
    "from " + source_table)

  inputDF.createOrReplaceTempView("src_encrypt_data")

  // Aggregate by Age
  spark.sql("drop table if exists " + output_table_1)
  spark.sql("SET spark.sql.shuffle.partitions = 2")
  spark.sql("create table " + output_table_1 + " as " +
    "select ptyUnprotectInt(age_encrypt, 'HIPAA_INT') AS age_decrypt, m_f_decrypt, " +
    "count(1) from src_encrypt_data group by ptyUnprotectInt(age_encrypt, 'HIPAA_INT'), m_f_decrypt DISTRIBUTE BY m_f_decrypt, age_decrypt")

  spark.sql("drop table if exists " + output_table_2)
  spark.sql("create table " + output_table_2 + " as " +
    "select year(ptyUnprotectStr(dob_encrypt, 'HIPAA_ASCII_CONFID')) AS dob_yr, m_f_decrypt, " +
    "count(1) from src_encrypt_data group by year(ptyUnprotectStr(dob_encrypt, 'HIPAA_ASCII_CONFID')), m_f_decrypt DISTRIBUTE BY m_f_decrypt, dob_yr")

}
