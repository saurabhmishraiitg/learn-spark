package org.nexus.spark.kerberos

import com.typesafe.scalalogging.Logger
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.hadoop.security.UserGroupInformation
import org.nexus.loader.JSONConfLoader

/**
 * POC example class to demonstrate connection to HDFS
 */
object SparkHDFSConnect {

  val logger: Logger = Logger("SparkHDFSConnect")

  def main(args: Array[String]): Unit = {

    //The location of krb5.conf file needs to be provided in the VM arguments for the JVM
    //-Djava.security.krb5.conf=/Users/xxx/Desktop/utils/cluster/xx/krb5.conf
    val username: String = System.getenv("USER")
    val userHome: String = System.getenv("HOME")

    val d17Principal: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "d17-principal")
    val d17HadoopConfDir: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "d17-hadoop-conf-dir")
    val d17KeytabPath: String = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
      .getConfig("spark-kerberos-connect", "d17-keytab-path")

    logger.info("username : " + username)
    logger.info("userHome : " + userHome)
    logger.info("d17Principal : " + d17Principal)
    logger.info("d17HadoopConfDir : " + d17HadoopConfDir)
    logger.info("d17KeytabPath : " + d17KeytabPath)

    val conf = new Configuration
    conf.addResource(new Path("file://" + d17HadoopConfDir + "/hdfs-site.xml"))
    conf.addResource(new Path("file://" + d17HadoopConfDir + "/core-site.xml"))

    println("property check from hdfs-site.xml : " + conf.get("dfs.datanode.kerberos.principal"))
    println("property check from core-site.xml : " + conf.get("fs.defaultFS"))

    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab(d17Principal, d17KeytabPath)

    //    val spark = SparkSession
    //      .builder()
    //      .master("local[*]")
    //      .appName("Scala Spark Hive Example")
    //      .config("hive.metastore.uris", "thrift://xx.xx.sdc.xx-xx.com:9083,thrift://xx.xx-xx.com:9083")
    //      .enableHiveSupport()
    //      .getOrCreate();

    try {
      val fs: FileSystem = FileSystem.get(conf)
      val fileStatuses: Array[FileStatus] = fs.listStatus(new Path("/user/" + username));
      for (fileStatus <- fileStatuses) {
        logger.info(fileStatus.getPath.getName)
      }
      fs.close()
    }
    catch {
      case _: Exception =>
    }

  }
}
