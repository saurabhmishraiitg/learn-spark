package org.nexus

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class BaseSpec extends FlatSpec with DataFrameSuiteBase with BeforeAndAfterAll with StrictLogging with Matchers
