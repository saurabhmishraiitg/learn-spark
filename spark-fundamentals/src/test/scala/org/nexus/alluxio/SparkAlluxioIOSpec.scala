package org.nexus.alluxio

import com.typesafe.scalalogging.Logger
import org.scalatest.{BeforeAndAfter, FlatSpec}

class SparkAlluxioIOSpec extends FlatSpec with BeforeAndAfter {

  // logger
  val logger: Logger = Logger("SparkAlluxioIOSpec")

  before {
    logger.info("Before starting the SparkAlluxioIOSpec")
  }

  "Reading a file in alluxio" should " return the file content" in {
    assertResult((), "Method Invocation should return no output i.e. Unit") {
      SparkAlluxioIO.readFromAlluxio()
    }
  }
}