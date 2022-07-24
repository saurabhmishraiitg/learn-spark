# Spark Performance Tuning

- Following are few areas to explore for performance tuning your Spark application
  - <https://github.com/lskiranch/Spark-Performance-turning/blob/master/SparkTunings>

- <https://blog.cloudera.com/demystifying-spark-jobs-to-optimize-for-cost-and-performance/>
  - Identify which stage of the job takes most time and their corresponding key metrics e.g. memory, I/O etc.
  - Check if the jobs are spending more time on waiting for resources or otherwise
  - Check for number of active tasks vs running tasks or executor cores available. This will give indication of if we need to add more executors to speed up parallel execution of these waiting tasks
  - Check for memory allocation vs memory utilization metrics. This can indicate if we need to increase or decrease executor memory for more optimal resource utilization
  - Check for data skew if any within partitions. Data broadcast strategy may come in handy to resolve such scenarios
  - Check for time spent in compression/spilling data to disk. This could suggest, need to opt for another compression mechanism
  - Check for shuffle time
  - Check for memory related failures, could indicate need for increasing memory allocated to executor
  - Check for number of partitions at stage boundaries. This is similar to tuning the reducer count for optimal job performance
