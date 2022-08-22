# Spark Interview

## Hands On

- **Question** Implement Wordcount in Spark?
  - Using Dataframe

    ```scala
    val df = sqlContext.read.text("README.md")
    val wordsDF = df.select(split(df("value")," ").alias("words"))
    val wordDF = wordsDF.select(explode(wordsDF("words")).alias("word"))
    val wordCountDF = wordDF.groupBy("word").count
    wordCountDF.orderBy(desc("count")).show(truncate=false)
    ```

  - Using RDD

    ```scala
    val textFile = sc.textFile("hdfs://...")
    val counts = textFile.flatMap(line => line.split(" "))
                     .map(word => (word, 1))
                     .reduceByKey(_ + _)
    counts.saveAsTextFile("hdfs://...")
    ```

  - PySpark

    ```python
    text_file = sc.textFile("hdfs://...")
    counts = text_file.flatMap(lambda line: line.split(" ")) \
                    .map(lambda word: (word, 1)) \
                    .reduceByKey(lambda a, b: a + b)
    counts.saveAsTextFile("hdfs://...")
    ```

- **Question** Wordcount+, provide the nth most occurring word and also the files it's part of.

    ```scala
    val textFile = sc.textFile("hdfs://...")
    val counts = textFile.flatMap(line => line.split(" "))
    .map(word => (word, 1))
    .reduceByKey(_ + _)
    counts.saveAsTextFile("hdfs://...")
    ```

- **Question** Implement SCD Type 2 storage in Spark
- **Question** How to handle such delimited data with misplaced delimiters

  - |col1|,|col2|,"|co,l3|"

- Calculate # of sessions based upon following dataset. Assuming a single session is defined as consecutive activities by a user with difference < 3m b/w each.

    | SiteId | Userid | Time | Date | Activity Type |
    |---| --- | --- | --- | --- |
    | irctc | usr1 | 10:01 | 10-Mar | Click        |
    | irctc | usr1 | 10:03 | 10-Mar | Scroll       |
    | irctc | usr1 | 10:15 | 10-Mar | Scroll       |
    | irctc | usr2 | 10:01 | 10-Mar | Acknowledge  |
    | irctc | usr2 | 10:20 | 10-Mar | Click        |
    | irctc | usr3 | 10:15 | 10-Mar | Click        |
    | flpkrt | usr1 | 10:01 | 10-Mar | Click       |
    | flpkrt | usr1 | 10:33 | 10-Mar | Click       |
    | flpkrt | usr1 | 10:50 | 10-Mar | Acknowledge |
    | flpkrt | usr1 | 10:51 | 10-Mar | Acknowledge |
    | flpkrt | usr1 | 11:05 | 10-Mar | Acknowledge |
    | flpkrt | usr1 | 11:19 | 10-Mar | Acknowledge |
    | flpkrt | usr2 | 10:01 | 10-Mar | Scroll      |
    | flpkrt | usr2 | 10:10 | 10-Mar | Click       |
    | flpkrt | usr3 | 10:55 | 10-Mar | Scroll      |

    ****Answer****

    | SiteId | Date | Session Count |
    | irctc | 10-Mar | 5 |
    | flpkrt | 10-Mar | 8 |

- **Question** Get employee salary by manager

    Q) Give the list managers and his highest paid employee percentage
  - Tie breaker - If same salary then number skills
  - Tie Breaker - If same number of skills then Manager name

    Note:

    1) Employee Salary is sum of salA and salB
    2) Employee data of latest timestamp is the accurate data. previous data is invalid

    manager_json = [{
    "id" : "m1",
    "name": "Murali",
    "team_salary" : 500,
    "employees" : ["e1", "e2"]
    },

    {
    "id" : "m2",
    "name": "Aditya",
    "team_salary" : 400,
    "employees" : ["e1", "e2"]
    }

    ]

    employee_json = [{
    "id" : "e1",
    "salA" : 25,
    "salB" : 25,
    "timeStamp" : 1,
    "skills" : ["java", "python"]
    },

    {
    "id" : "e1",
    "salA" : 25,
    "salB" : 25,
    "timeStamp" : 3,
    "skills" : ["java", "python","spark"]
    },

    {
    "id" : "e2",
    "salA" : 25,
    "salB" : 25,
    "timeStamp" : 1,
    "skills" : ["java", "python"]
    }]

    Result should be in the exact same format as below

    manager_id  name    employee_id  salary skills
    m1          Murali      e1          90  "java,python,scala"
    m2          Aditya      e3          90  "java,python"

- **Question** Calculate Last 30 days moving average price of a product

    | store | Product | Price | Date |
    | --- | --- | --- | --- |
    | 1 | Paint | 30 | 12-Jan-2021 |
    | 1 | Paint | 35 | 30-Jan-2021 |
    | 1 | Paint | 60 | 30-Apr-2021 |

    Solution : PySpark

    ```python
    **days = lambda i: i * 86400 # 86400 seconds in a day 
    
    w0 = Window.partitionBy('name') 
    
    df.withColumn('unix_time',F.col('date').cast('timestamp').cast('long'))\    
     .withColumn('moving_avg', \        
     F.avg('price')\            
     .over(w0.orderBy(F.col('unix_time')).rangeBetween(-days(30), 0)\        
     )\    
    )\    
     .withColumn('days_since_last', \        
     F.datediff('date', F.lag('date', 1)\            
     .over(w0.orderBy(F.col('date')))\        
    )).show()**
    ```

- **Question** Perform trend analysis on product prices

    | Product | Date | Price |
    | --- | --- | --- |
    | Paint | 01-Jan-2021 | 30 |
    | Paint | 02-Jan-2021 | 35 |
    | Paint | 03-Jan-2021 | 20 |

    **Expected Output**

    | Product | Date | Price | Trent |
    | --- | --- | --- | --- |
    | Paint | 01-Jan-2021 | 30 | NA |
    | Paint | 02-Jan-2021 | 35 | Up |
    | Paint | 03-Jan-2021 | 20 | Down |

    Solution : PySpark

    ```python
    from pyspark.sql.window import Window
    import pyspark.sql.functions as F
    
    wd = Window.partitionBy('empname').orderBy(F.col('saldt_parse').asc())
    
    csvdf2.withColumn(\
        'trend',  \
             F.when(\
                 (F.col('salary') - F.coalesce(F.lag('salary', 1).over(wd), F.lit(0))) > 0, 'UP')\
             .otherwise('DOWN')
    )\
    .withColumn(\
        'diff',  \
                (F.col('salary') - F.coalesce(F.lag('salary', 1).over(wd), F.lit(0)))\
    ).show()
    ```

## Theoretical

- ****Scenario**** : If we have lot of partitions with varying number of small files and varying data size. How will we write a compaction logic to repartition the data in proper sized chunks.
- ****Scenario**** : You have been given 2 tables in HIVE and asked to validate if the 2 datasets are equal or not e.g. doing a UAT against PROD data. Implement this request in Spark.
- **Question** B/w Spark and MR/HIVE, which will perform better for word-count use-case
- **Question** Explain lifecycle of a Spark job using word-count as an example. Different cluster components interacted with etc.
- **Question** Explain about some optimization scenarios you have worked upon in Spark
  - Dynamic Allocation of executors
  - Checkpointing to avoid recompute of lineage - cache() / persist()
- **Question** Narrow vs Wide Transformations
  - **Narrow transformation —** In *Narrow transformation*, all the elements that are required to compute the records in single
    partition live in the single partition of parent RDD. A limited subset
    of partition is used to calculate the result. *Narrow transformations* are the result of *map(), filter().*
  - **Wide transformation —** In wide transformation, all the elements that are required to compute the
    records in the single partition may live in many partitions of parent
    RDD. The partition may live in many partitions of parent RDD. *Wide transformations* are the result of *groupbyKey* and *reducebyKey*.
- **Question** What are the new features introduced in Spark 3.0
  - Adaptive Query Engine - Optimize queries during runtime based upon statistics collected from previous stages
    - Coalesce post shuffle partitions to optimal number
    - sort merge join to Broadcast Join
    - skew join optimization
  - Language version upgrades e.g. scala 2.12, python 3, JDK11
  - New UI for Structured Streaming
  - Support to read binary files such as images, pdf, zip etc.
    - Each file is read as single record in DF
  - Ability to read folders recursively using parameter `recursiveFileLookup`
  - Support for multi character delimiter e.g. `||`
  - New built in functions e.g. `sinh`, `cosh`, `make_date` etc.
  - Added `REPARTITION` hint support for SQL queries
