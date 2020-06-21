package org.nexus.jdbc

import org.nexus.common.Context

object SparkJDBCIO extends App with Context {

  val url = "db-url"
  val username = "db-username"
  val password = "db-password"
  val database = "db?ssl=true"

  // parsing input parameters ...

  val df1 = spark.read.format("jdbc")
    .option("url", "jdbc:mysql://db-url:3306/db?autoReconnect=true" +
      "&verifyServerCertificate=false&useSSL=true&tinyInt1isBit=false&zeroDateTimeBehavior=convertToNull")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("dbtable", "(SELECT * FROM tasks limit 3) AS my_table")
    .option("user", username)
    .option("password", password).load()

  df1.show(3)
}
