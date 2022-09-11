# Spark Installation

## Install using brew

- `brew install apache-spark`
  - As of time of writing this latest version available is v3.3.0
- Installation directory
  - `/opt/homebrew/Cellar/apache-spark/3.3.0`
- Scripts directory
  - `/opt/homebrew/Cellar/apache-spark/3.3.0/libexec/sbin`
- Binaries directory
  - `/opt/homebrew/Cellar/apache-spark/3.3.0/bin`
- Conf directory
  - `/opt/homebrew/Cellar/apache-spark/3.3.0/libexec/conf`

## Configure/Start Spark History Server

The Spark History Server is a User Interface that is used to monitor the metrics and performance of the completed Spark applications

- Update `spark-defaults.conf` file with following 3 properties

```conf
    spark.eventLog.enabled true
    spark.eventLog.dir file:///Users/saurabhmishra/_tmp/spark-events
    spark.history.fs.logDirectory file:///Users/saurabhmishra/_tmp/spark-events
```

- Start historu server
  - `bash /opt/homebrew/Cellar/apache-spark/3.3.0/libexec/sbin/start-history-server.sh`
  - Start on port `18080`

- In your code add following properties to sparkConf to generate execution events for browsing with HistoryServer

```scala
  lazy val sparkConf = new SparkConf()
    .set("spark.eventLog.enabled", "true")
    .set("spark.history.fs.logDirectory", sys.env("HOME")+"/_tmp/spark-events")
    .set("spark.eventLog.dir",sys.env("HOME")+"/_tmp/spark-events")
```
