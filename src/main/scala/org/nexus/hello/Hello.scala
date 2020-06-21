package org.nexus.hello

import com.typesafe.scalalogging.LazyLogging

object Hello extends LazyLogging with App {
  println("Hello Scala Again")
  logger.info("Logging Done")
  //  logger.info("Logging Done Again")
}
