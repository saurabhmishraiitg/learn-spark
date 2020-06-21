package org.nexus.streaming

import org.nexus.general.Context

object StreamingWordCount extends App with Context{

  val lines = sparkSession.readStream.format("socket")
    .option("host", "localhost")
    .option("port", 9999)
    .load()

  import sparkSession.implicits._
  // Split the lines into words
  val words = lines.as[String].flatMap(_.split(" "))

  // Generate running word count
  val wordCounts = words.groupBy("value").count()

  // Start running the query that prints the running counts to the console
  val query = wordCounts.writeStream
    .outputMode("complete")
    .format("console")
    .start()

  query.awaitTermination()

  // To suppress spurious logs use a log4j.properties file
  //nc -lk 50050
}
