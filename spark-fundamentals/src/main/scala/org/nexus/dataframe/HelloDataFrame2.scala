package org.nexus.dataframe

import org.nexus.common.Context

object HelloDataFrame2 extends App with Context {
  val dfTags = sparkSession.read.option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/question_tags_10K.csv")
    .toDF("Id", "Tag")

  val dfQuestionsCSV = sparkSession.read.option("header", "true")
    //Auto infer the schema of the dataframe based upon columns encountered
    .option("inferSchema", "true")
    .option("dateFormat", "yyyy-MM-dd HH:mm:ss")
    .csv("src/main/resources/questions_10K.csv")
    .toDF("Id", "CreationDate", "ClosedDate", "DeletionDate", "Score", "OwnerUserId", "AnswerCount")

  dfQuestionsCSV.show(10)

  dfQuestionsCSV.printSchema()

  //Explicit casting of columns
  val dfQuestions = dfQuestionsCSV.select(dfQuestionsCSV.col("Id").cast("integer"),
    dfQuestionsCSV.col("CreationDate").cast("timestamp"),
    dfQuestionsCSV.col("ClosedDate").cast("timestamp"),
    dfQuestionsCSV.col("DeletionDate").cast("date"),
    dfQuestionsCSV.col("Score").cast("integer"),
    dfQuestionsCSV.col("OwnerUserId").cast("integer"),
    dfQuestionsCSV.col("AnswerCount").cast("integer"))

  dfQuestions.show(10)

  dfQuestions.printSchema()

  val dfQuestionsSubset = dfQuestions.filter("score > 400 and score < 410")
  println(s"dfQuestionsSubset class ${dfQuestionsSubset.getClass}")
  println(s"dfQuestionsSubset class ${dfQuestionsSubset.toDF().getClass}")

  dfQuestionsSubset.show(10)


  //Join the Question subset with Question tags
  val joinDF = dfQuestionsSubset.join(dfTags, "Id")
  joinDF.show(10)

  dfTags.filter("id = '888'").show(10)

  //DF join with explicit column names
  val joinDF2 = dfQuestionsSubset.join(dfTags, dfTags("Id") === dfQuestionsSubset("Id"))
  joinDF2.show(10)

  //Multiple join types
  //inner, cross, outer, full, full_outer, left, left_outer, right, right_outer, left_semi, left_anti
  dfQuestionsSubset
    .join(dfTags, Seq("id"), "inner")
    .show(10)

  //Distinct values
  dfTags.select("Tag").distinct().show(10)

  //Count of values
  println(s"Count of records in Questions Subset ${dfQuestionsSubset.count()}")
  println(s"Inner join count ${ dfQuestionsSubset.join(dfTags, Seq("Id"), "inner").count()}")
  println(s"Cross join count ${ dfQuestionsSubset.crossJoin(dfTags).count()}")
}
