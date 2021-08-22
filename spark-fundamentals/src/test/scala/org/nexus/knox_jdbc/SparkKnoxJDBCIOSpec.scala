package org.nexus.knox_jdbc

import com.typesafe.scalalogging.Logger
import org.scalatest.{BeforeAndAfter, FlatSpec}

class SparkKnoxJDBCIOSpec extends FlatSpec with BeforeAndAfter {

  // logger
  val logger: Logger = Logger("SparkKnoxJDBCIOSpec")

  before {
    logger.info("Before starting the SparkKnoxJDBCIOSpec")
  }

  "Loading data from Knox JDBC" should "successfully get the records" in {
    val tableName = "ww_glbl_gvrnce_dl_nonsecure_tables.gg_ra_contacts"
    SparkKnoxJDBCIO.loadDF(tableName)
  }
}
