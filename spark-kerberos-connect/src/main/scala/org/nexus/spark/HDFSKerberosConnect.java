package org.nexus.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.nexus.loader.JSONConfLoader;

import java.io.IOException;

/**
 * Before moving on to Spark cluster, testing the setup using HDFS cluster connect.
 */
public class HDFSKerberosConnect {

    public static void main(String[] args) throws IOException {
        //The location of krb5.conf file needs to be provided in the VM arguments for the JVM
        //-Djava.security.krb5.conf=/Users/xxx/Desktop/utils/cluster/xx/krb5.conf
        String username = System.getenv("USER");
        String userHome = System.getenv("HOME");

        String d17Principal = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
                .getConfig("spark-kerberos-connect", "d17-principal");
        String d17HadoopConfDir = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
                .getConfig("spark-kerberos-connect", "d17-hadoop-conf-dir");
        String d17KeytabPath = new JSONConfLoader(userHome + "/nexus.conf", "org.nexus")
                .getConfig("spark-kerberos-connect", "d17-keytab-path");

        System.out.println("username : " + username);
        System.out.println("userHome : " + userHome);
        System.out.println("d17Principal : " + d17Principal);
        System.out.println("d17HadoopConfDir : " + d17HadoopConfDir);
        System.out.println("d17KeytabPath : " + d17KeytabPath);

        Configuration conf = new Configuration();
        conf.addResource(new Path("file://" + d17HadoopConfDir + "/hdfs-site.xml"));
        conf.addResource(new Path("file://" + d17HadoopConfDir + "/core-site.xml"));

        System.out.println("property check from hdfs-site.xml : " + conf.get("dfs.datanode.kerberos.principal"));
        System.out.println("property check from core-site.xml : " + conf.get("fs.defaultFS"));

        //The following property is enough for a non-kerberized setup
        //		conf.set("fs.defaultFS", "localhost:9000");

        //need following set of properties to access a kerberized cluster
//        conf.set("fs.defaultFS", "hdfs://dev17ha");
//        conf.set("dfs.nameservices", "dev17ha");
//        conf.set("dfs.ha.namenodes.dev17ha", "nn1,nn2");
//        conf.set("dfs.namenode.rpc-address.dev17ha.nn1", "xx.xx-xx.com:8020");
//        conf.set("dfs.namenode.rpc-address.dev17ha.nn2", "xx.xx-xx.com:8020");
//        conf.set("dfs.client.failover.proxy.provider.dev17ha",
//                "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
//        conf.set("hadoop.security.authentication", "kerberos");

        //If you don't want to bother with HA namenodes and want to hardcode the namenode service to hit, the use the single liner below
        //		conf.set("fs.defaultFS", "hdfs://xxxx.xx-xx.com:8020");

        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(d17Principal, d17KeytabPath);

        try (FileSystem fs = FileSystem.get(conf);) {
            FileStatus[] fileStatuses = fs.listStatus(new Path("/user/" + username));
            for (FileStatus fileStatus : fileStatuses) {
                System.out.println(fileStatus.getPath().getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
