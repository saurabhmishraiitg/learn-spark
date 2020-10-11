package org.nexus.df.partition

import org.nexus.common.Context

/**
 * Save Dataframe as a partitioned table.
 */
object SaveAsPartitionedTable extends App with Context {

  val inpDF = sparkSession.read.option("delimiter",";").csv("src/main/resources/movielens.csv")
  inpDF.show(5)
}
