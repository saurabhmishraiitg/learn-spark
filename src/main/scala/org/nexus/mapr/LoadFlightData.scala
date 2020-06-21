package org.nexus.mapr

import com.typesafe.scalalogging.LazyLogging
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Random

/**
 * Trying to read and load a CSV file in DF
 */
object LoadFlightData extends LazyLogging {

  def getSchema(): StructType = {
    new StructType()
      .add("Year", StringType, true)
    //      StructField("dofW", IntegerType, true),
    //      StructField("carrier", StringType, true),
    //      StructField("origin", StringType, true),
    //      StructField("dest", StringType, true),
    //      StructField("crsdephour", IntegerType, true),
    //      StructField("crsdeptime", DoubleType, true),
    //      StructField("crsarrtime", DoubleType, true),
    //      StructField("crselapsedtime", DoubleType, true),
    //      StructField("label", DoubleType, true),
    //      StructField("pred_dtree", DoubleType, true)
    //    )
  }


  def getSparkConf(): SparkConf = {
    new SparkConf()
      .set("spark.cores.max", "2")
      .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")
      .set("fs.alluxio.impl", "alluxio.hadoop.FileSystem")
    //      .setJars(Seq("/Users/xxx/Desktop/utility/alluxio/latest/client/alluxio-2.1.0-client.jar"))
  }

  def getSparkSession(sparkConf: SparkConf): SparkSession = {
    SparkSession.builder()
      .appName("mapr-flight")
      .master("local[*]")
      .config(sparkConf)
      //      .config("spark.jars", "/Users/xxx/Desktop/utility/alluxio/latest/client/alluxio-2.1.0-client.jar")
      .getOrCreate()
  }

  def sampleDFIO(spark: SparkSession): Unit = {
    val s = getSparkContext(spark).textFile("alluxio://localhost:19998/Input")
    val double = s.map(line => line + line)
    double.saveAsTextFile("alluxio://localhost:19998/Output")
  }

  def getRawDF(spark: SparkSession, schema: StructType): DataFrame = {
    spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      //      .option("multiLine", true)
      .option("mode", "PERMISSIVE")
      //      .option("inferSchema", "false")
      //      .schema(schema)
      .load("/Users/xxx/Desktop/tmp/data/us-flight-data-jan-2019/reporting.csv")
    //      .cache()
  }

  def getSparkContext(spark: SparkSession): SparkContext = {
    spark.sparkContext
  }

  def saveRawDFToAlluxio(rawDF: DataFrame): Unit = {
    rawDF
      .repartition(5)
      .write
      .option("compression", "gzip") //none, snappy, gzip, lzo
      .mode(SaveMode.Overwrite)
      .json("alluxio://localhost:19998/mapr/flight-data-02")
    //      .parquet("alluxio://localhost:19998/mapr/flight-data-02")
  }

  def readRawDFFromAlluxio(spark: SparkSession): DataFrame = {
    spark.read
      .json("alluxio://localhost:19998/mapr/flight-data-02")
    //      .parquet("alluxio://localhost:19998/mapr/flight-data-01")
  }

  def generateLargeDataset(spark: SparkSession): Unit = {
    //    val schema = getSchema()

    //https://docs.databricks.com/data/data-sources/read-csv.html
    //    val rawDF = getRawDF(spark, null)
    //    val unionRawDF = rawDF.union(rawDF).union(rawDF); //unionAll and union both retain duplicates. unionAll transformation from old SPARK version is just renamed to union, hence the deprecation
    //    saveRawDFToAlluxio(unionRawDF)
  }

  /**
   * Do a UNION of tables with different set of columns and dataTypes
   * https://stackoverflow.com/questions/39758045/how-to-perform-union-on-two-dataframes-with-different-amounts-of-columns-in-spar
   *
   * @param alluxioRawDF
   */
  def unionDiffTables(alluxioRawDF: DataFrame): Unit = {
    val tbl1 = alluxioRawDF.select("Flight_Number_Reporting_Airline", "ActualElapsedTime").limit(3)
    val tbl2 = alluxioRawDF.select("Flight_Number_Reporting_Airline", "ArrDel15").limit(3)

    val allcols = tbl1.columns.toSet ++ tbl2.columns.toSet

    import org.apache.spark.sql.functions._
    def expr(myCols: Set[String], allCols: Set[String]) = {
      allCols.toList.map(x => x match {
        case x if myCols.contains(x) => col(x)
        case _ => lit(null).as(x)
      })
    }

    tbl1.select(expr(tbl1.columns.toSet, allcols): _*).union(tbl2.select(expr(tbl2.columns.toSet, allcols): _*)).show()
  }

  def setLogger(): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("alluxio").setLevel(Level.ERROR)
    //    wvlet.log.Logger.setDefaultFormatter(wvlet.log.LogFormatter.IntelliJLogFormatter)
  }

  /**
   * Simple Word Count using RDD
   *
   * @param spark
   */
  def simpleWCRDD(spark: SparkSession): Unit = {
    val rawDF = spark.read.textFile("/Users/xxx/Desktop/utility/alluxio/alluxio-2.1.0/LICENSE")
    //    println(s"${rawDF.show(6, truncate = false)}")

    rawDF.toJavaRDD.rdd
      .flatMap(x => x.trim.split(" "))
      .map(x => (x, 1))
      .reduceByKey((x, y) => x + y)
      .sortBy(_._2, false)
      .take(5)
      .foreach(println)
  }

  /**
   * Simple Word Count example using Dataframes
   *
   * @param spark
   */
  def simpleWCDF(spark: SparkSession): Unit = {
    val rawDF = spark.read.textFile("/Users/xxx/Desktop/utility/alluxio/alluxio-2.1.0/LICENSE")
    import org.apache.spark.sql.functions._

    rawDF.explode("value", "word")((x: String) => x.trim.split(" "))
      .groupBy("word")
      .count()
      .orderBy(desc("count"))
      .take(5)
      .foreach(println)
    //    rawDF.withColumn("split", col("value")).explode("value","")


    //Alternate without using 'explode'
    import org.apache.spark.sql.functions._
    import spark.implicits._
    rawDF.select("value").flatMap((row: Row) => row.getString(0).trim.split(" ")).withColumnRenamed("value", "word").groupBy("word").count().orderBy(desc("count")).take(5).foreach(println)
  }

  /**
   * Write the output of word count to table in local spark-warehouse. partitioned and bucketed.
   *
   * @param spark
   */
  def saveDFToTable(spark: SparkSession): Unit = {
    val rawDF = spark.read.textFile("/Users/xxx/Desktop/utility/alluxio/alluxio-2.1.0/LICENSE")

    import spark.implicits._

    val rand = Random
    val newCols = Seq("word", "word_count", "code")
    val tblDS = rawDF.select("value")
      .flatMap(row => row.getString(0).trim.split(""))
      .withColumnRenamed("value", "word")
      .groupBy("word")
      .count()
      .map((row: Row) => (row.getString(0), row.getLong(1), rand.nextInt(4)))

    val tblDF = tblDS.toDF(newCols: _*)
      .repartition(4, $"code")

    tblDF.write
      .partitionBy("code")
      .bucketBy(4, "word") // This parameter is not honoured in current Spark version i.e. 2.4
      .mode(SaveMode.Append)
      .saveAsTable("word_count_tbl")
  }

  /**
   * The table we saved in the previous step, we are trying to read it to the DF and checking if the
   * partition and bucket columns are honoured or not.
   *
   * @param spark
   */
  def readTableToDF(spark: SparkSession): Unit = {
    val tblDF = spark.read.load("spark-warehouse/word_count_tbl")
    tblDF.printSchema()
  }

  /**
   * Reading raw taxi trip data.
   * e.g. record >>> 2,2016-01-01 00:00:00,2016-01-01 00:00:00,2,1.10,-73.990371704101563,40.734695434570313,1,N,-73.981842041015625,40.732406616210937,2,7.5,0.5,0.5,0,0,0.3,8.8
   *
   * @param spark
   */
  def readYellowTaxiRawData(spark: SparkSession): DataFrame = {
    val taxiTripSchema = new StructType()
      .add("vendorId", IntegerType)
      .add("pickupDate", DateType)
      .add("dropTime", TimestampType)
      .add("passengerCount", IntegerType)
      .add("tripDistance", FloatType)
    spark.read.option("header", true).schema(taxiTripSchema).csv("alluxio://localhost:19998/sample-dataset/yellow-taxi-small")
  }

  def getCountOfPartitionsForDF(spark: SparkSession, rawDF: DataFrame): Unit = {
    println(s"number of partitions : [${rawDF.rdd.getNumPartitions}]")
    //    rawDF.rdd.mapPartitionsWithIndex((index, iterator) => (Array(s"Partition #$index").iterator)).foreach(x => println(x))
  }

  /**
   * Using mapPartitions to get the starting date record for eachPartition
   *
   * @param spark
   * @param rawDF
   */
  def exampleMapPartition(spark: SparkSession, rawDF: DataFrame): Unit = {
    import spark.implicits._
    rawDF.mapPartitions(iterator => {
      if (!iterator.isEmpty)
        Array(iterator.next().getDate(2).toString).iterator
      else
        Array("NA").iterator
    }).foreach(println(_))
  }

  def getDFDetails(rawDF: DataFrame): Unit = {
    logger.warn(s"Count of records in the DF : [${rawDF.count()}]")
    //    rawDF.printSchema()
    //    rawDF.show(10)
  }

  /**
   * Explore performance of spark - reduceByKey, groupByKey, aggregateByKey, combineByKey functions
   * Get count by different grouping paradigms
   *
   * @param rawDF
   */
  def testRDDReduceByGroupBY(rawDF: DataFrame): Unit = {
    val rawRDD = rawDF.rdd.cache()
    logger.warn(s"RDD count : ${rawRDD.count}")

    //Get countBy date using different grouping paradigms
    logger.warn("groupByKey")
    rawRDD.map(x => (x.getDate(1).toString, 1))
      .groupByKey().map((a) => (a._1, a._2.sum))
      .sortBy(_._1, true, 1)
      .foreach(println)

    logger.warn("reduceByKey")
    rawRDD.map(x => (x.getDate(1).toString, 1))
      .reduceByKey((x, y) => (x + y))
      .sortBy(_._1, true, 1)
      .foreach(println)

    logger.warn("aggregateByKey")
    val initCnt = 0
    val sumCounts = (p1: Int, p2: Int) => p1 + p2
    rawRDD.map(x => (x.getDate(1).toString, 1))
      .aggregateByKey(initCnt)(sumCounts, sumCounts)
      .sortBy(_._1, true, 1)
      .foreach(println)

    logger.warn("combineByKey")
    rawRDD.map(x => (x.getDate(1).toString, 1))
      .combineByKey((x => 1)
        , ((x: Int, y) => (x + y))
        , ((x: Int, y: Int) => (x + y)))
      .sortBy(_._1, true, 1)
      .foreach(println)
  }

  def joinDF(rawDF: DataFrame): Unit = {
    val childDF1 = rawDF.select("vendorId", "pickupDate").limit(10)
    val childDF2 = rawDF.select("vendorId", "passengerCount").limit(2)
    val joinDF = childDF1.as("cf1").join(childDF2.as("cf2"), Seq("vendorId"), "inner")

    joinDF /*.select($"cf1.vendorId", $"cf2.pickupDate")*/ .show(10)
  }

  /**
   * Compare between join and cogroup operations on an RDD.
   *
   * @param rawDF
   */
  def joinVsCogroupRDD(rawDF: DataFrame): Unit = {
    val childRDD1 = rawDF.select("vendorId", "pickupDate").limit(10).rdd.map(x => (x.getInt(0), x.getDate(1)))
    val childRDD2 = rawDF.select("vendorId", "passengerCount").limit(2).rdd.map(x => (x.getInt(0), x.getInt(1)))

    childRDD1.collect().foreach(println)
    childRDD2.collect().foreach(println)

    //Join
    childRDD1.join(childRDD2).collect().foreach(println)

    //Cogroup
    childRDD1.cogroup(childRDD2).collect().foreach(println)
  }

  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {
    setLogger()

    val startTime = System.currentTimeMillis()
    logger.info("Hello Scala! ")

    val sparkConf = getSparkConf()
    val spark = getSparkSession(sparkConf)
    //    generateLargeDataset(spark)

    //    wvlet.log.Logger.clearAllHandlers
    //    wvlet.log.Logger.setDefaultFormatter(wvlet.log.LogFormatter.SourceCodeLogFormatter)

    val rawDF = readYellowTaxiRawData(spark)
    //    getDFDetails(rawDF)

    // Usage of broadcast variable
    val childDF1 = rawDF.select("vendorId", "pickupDate", "dropTime").limit(10)
    val childDF2 = rawDF.select("vendorId", "passengerCount", "tripDistance").limit(2)

    import org.apache.spark.sql.functions._
    //    spark.conf.set("","")
    val joinDF = childDF1.join(broadcast(childDF2), Seq("vendorId"))
    //    info(joinDF.queryExecution.analyzed.numberedTreeString)
    //    warn(joinDF.queryExecution.logical.numberedTreeString)
    logger.info(joinDF.queryExecution.optimizedPlan.numberedTreeString)
    //    warn(joinDF.queryExecution.withCachedData.numberedTreeString)
    logger.warn(joinDF.queryExecution.executedPlan.numberedTreeString)



    val endTime = System.currentTimeMillis()
    error(s"Bye Scala! runtTime : ${(endTime - startTime) / 1000}")
    //    Thread.sleep(100000)
  }
}