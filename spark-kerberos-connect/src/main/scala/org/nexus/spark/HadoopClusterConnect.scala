package org.nexus.spark

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.sql.SparkSession
import org.nexus.loader.JSONConfLoader

/**
 * Connect to a hadoop cluster using provided conf and krb5 file.
 *
 * ***NOT WORKING***
 * Unable to connect to the cluster in SparkSession
 * ***NOT WORKING***
 */
object HadoopClusterConnect extends App {

  println("Hello Hadoop Cluster")

  val username = System.getenv("USER")

  val d17Principal = new JSONConfLoader().getConfig("spark-kerberos-connect", "d17-principal")
  val d17HadoopConfDir = new JSONConfLoader().getConfig("spark-kerberos-connect", "d17-hadoop-conf-dir")
  val d17KeytabPath = new JSONConfLoader().getConfig("spark-kerberos-connect", "d17-keytab-path")

  println("username : " + username)
  println("d17Principal : " + d17Principal)
  println("d17HadoopConfDir : " + d17HadoopConfDir)
  println("d17KeytabPath : " + d17KeytabPath)

  val conf = new Configuration
  conf.addResource(new Path("file://" + d17HadoopConfDir + "/hdfs-site.xml"))
  conf.addResource(new Path("file://" + d17HadoopConfDir + "/core-site.xml"))

  println("property check from hdfs-site.xml : " + conf.get("dfs.datanode.kerberos.principal"))
  println("property check from core-site.xml : " + conf.get("fs.defaultFS"))

  UserGroupInformation.setConfiguration(conf)
  UserGroupInformation.loginUserFromKeytab(d17Principal, d17KeytabPath)

  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Scala Spark Hive Example")
    .config("hive.metastore.uris", "thrift://xx.xx.sdc.xx-xx.com:9083,thrift://xx.xx-xx.com:9083")
    .enableHiveSupport()
    .getOrCreate();
  //    .config("spark.yarn.keytab", "user.keytab")
  //    .config("spark.yarn.principal", "principal@realm.com")

  // spark.catalog.listTables.filter($"name" === "t1").show
  // val t3Tid = spark.sessionState.sqlParser.parseTableIdentifier("s0m0158.gg_reach_fire_watch")
  // val t3Metadata = spark.sessionState.catalog.getTempViewOrPermanentTableMetadata(t3Tid)
  // spark.catalog.listTables("s0m0158")
  // import org.apache.spark.sql.catalyst.TableIdentifier
  // val v2 = TableIdentifier(table = "test01", database = Some("s0m0158"))
  // val metastore = spark.sharedState.externalCatalog
  // val db = spark.catalog.currentDatabase
  // import spark.sessionState.{catalog => c}
  // val tmeta = c.getTableMetadata(v2)

  spark.catalog.listTables(username).show(10)
}
