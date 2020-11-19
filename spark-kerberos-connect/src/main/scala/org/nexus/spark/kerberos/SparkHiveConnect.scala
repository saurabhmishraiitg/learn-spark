package org.nexus.spark.kerberos

import com.typesafe.scalalogging.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.nexus.loader.JSONConfLoader

/**
 * ***NOT WORKING***
 * ***NOT WORKING***
 * ***NOT WORKING***
 *
 * Validate connecting Spark with remote HIVE Cluster
 * 1. https://stackoverflow.com/questions/31980584/how-to-connect-spark-sql-to-remote-hive-metastore-via-thrift-protocol-with-no
 * 2. https://stackoverflow.com/questions/32586793/howto-add-hive-properties-at-runtime-in-spark-shell
 * 3. https://github.com/apache/spark/blob/v2.3.1/core/src/main/scala/org/apache/spark/deploy/SparkHadoopUtil.scala#L501
 * 4. https://stackoverflow.com/questions/21375372/accessing-hive-metastore-using-jdbc-with-kerberos-keytab?rq=1
 *
 * ***NOT WORKING***
 * ***NOT WORKING***
 * ***NOT WORKING***
 */
object SparkHiveConnect {
  val logger: Logger = Logger("SparkHiveConnect")

  def main(args: Array[String]): Unit = {
    val username: String = System.getenv("USER")
    val userHome: String = System.getenv("HOME")

    val password: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "password")
    val hostname: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "hostname")
    val krb5Conf: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "krb5-conf")
    val hivePrincipal: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "hive-principal")
    val hiveMetastoreThrift: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "hive-metastore-thrift")
    val keytabFile: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "d17-keytab-path")
    val userPrincipal: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "d17-principal")


    System.setProperty("java.security.auth.login.config", s"$userHome/github/learn-spark/spark-kerberos-connect/src/main/resources/jaas.conf")
    System.setProperty("sun.security.jgss.debug", "true")
    System.setProperty("java.security.debug", "gssloginconfig,configfile,configparser,logincontext")
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
    System.setProperty("java.security.krb5.conf", krb5Conf)

    System.setProperty("hive.metastore.sasl.enabled", "true")
    System.setProperty("hive.security.authorization.enabled", "false")
    System.setProperty("hive.metastore.kerberos.principal", hivePrincipal)
    System.setProperty("hive.metastore.execute.setugi", "true")
    System.setProperty("hadoop.security.authentication", "kerberos")
    //    System.setProperty("hadoop.home.dir", "/Users/user/Desktop/utility/hadoop-2.7.3")
    //    System.setProperty("spark.home", "/Users/user/Desktop/utility/spark-2.4.5-bin-hadoop2.7")

    val conf: SparkConf = new SparkConf()

    conf.setMaster("local[*]")
    conf.setAppName("SparkHiveConnect")
    conf.set("spark.hadoop.hive.metastore.uris", hiveMetastoreThrift)
    conf.set("spark.hadoop.spark.sql.warehouse.dir", "/tmp/warehouse")
    conf.set("hadoop.security.authentication", "kerberos")
    conf.set("spark.eventLog.enabled", "false")

    //    val sc: SparkContext = new SparkContext(conf)
    //    val hiveContext: HiveContext = new HiveContext(sc);
    //
    //    hiveContext.sql("SHOW DATABASES");

    val spark = SparkSession
      .builder()
      .master("local[1]")
      .appName("Scala Spark Hive Example")
      .config("spark.hadoop.hive.metastore.uris", hiveMetastoreThrift)
      .config("spark.yarn.keytab", keytabFile)
      .config("spark.yarn.principal", userPrincipal)
      .config("spark.hadoop.spark.sql.warehouse.dir", "/tmp/warehouse")
      .enableHiveSupport()
      .getOrCreate();

    // spark.catalog.listTables.filter($"name" === "t1").show
    // val t3Tid = spark.sessionState.sqlParser.parseTableIdentifier("user.gg_reach_fire_watch")
    // val t3Metadata = spark.sessionState.catalog.getTempViewOrPermanentTableMetadata(t3Tid)
    // spark.catalog.listTables("s0m0158")
    // import org.apache.spark.sql.catalyst.TableIdentifier
    // val v2 = TableIdentifier(table = "test01", database = Some("user"))
    // val metastore = spark.sharedState.externalCatalog
    // val db = spark.catalog.currentDatabase
    // import spark.sessionState.{catalog => c}
    // val tmeta = c.getTableMetadata(v2)

    spark.sql("select * from s0m0158.test01").show()
  }
}
