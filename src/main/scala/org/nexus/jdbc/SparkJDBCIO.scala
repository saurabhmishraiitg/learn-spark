package org.nexus.jdbc

import org.nexus.general.Context

object SparkJDBCIO extends App with Context{

  val url="reach-mysql-prod.mysql.database.azure.com"
  val username="reachadmin@reach-mysql-prod"
  val password="2J<uDH}q[v[G*%2f"
  val database="reach-prod?ssl=true"



  // parsing input parameters ...


    val df1 = spark.read.format("jdbc")
    .option("url", "jdbc:mysql://reach-mysql-prod.mysql.database.azure.com:3306/reach-prod?autoReconnect=true&verifyServerCertificate=false&useSSL=true&tinyInt1isBit=false&zeroDateTimeBehavior=convertToNull")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("dbtable", "(SELECT * FROM tasks limit 3) AS my_table")
    .option("user", username)
    .option("password", password).load()

  df1.show(3)
}
