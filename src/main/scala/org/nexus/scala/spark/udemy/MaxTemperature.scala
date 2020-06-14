package org.nexus.scala.spark.udemy

import org.nexus.scala.general.ConfigurationManager

/**
  * This example takes weather data for a location and identifies the MAX temperature after some
  * filter operations.
  */
object MaxTemperature extends App {

  println("Demonstration of filter transformation in Spark...")

  val key = "nexus"
  println(s"Key [${key}] , Value [${ConfigurationManager.getConfiguration(key)}]")
}
