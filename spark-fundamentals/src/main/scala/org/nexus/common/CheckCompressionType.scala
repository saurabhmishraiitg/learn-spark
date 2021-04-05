package org.nexus.common

import com.typesafe.scalalogging.StrictLogging

import java.io.RandomAccessFile

/**
 * Check given file's compression type.
 */
object CheckCompressionType extends StrictLogging {

  def checkCompressionType(filePath: String): String = {
    var compressionType: String = ""
    // TODO First check if the file is even compressed

    val magicNumber = getMagicNumber(filePath)
    // Check for Bzip2
    if (magicNumber == 0x425a6839) {
      logger.info(s"file [$filePath] is [bzip2] compressed.")
      compressionType = "bzip2"
    }
    // Check for bzip
    else if (magicNumber == 0x425a) {
      logger.info(s"file [$filePath] is [bzip] compressed.")
      compressionType = "bzip"
    }
    // Check for gzip
    else if (magicNumber == 0x1f8b0808) {
      logger.info(s"file [$filePath] is [gzip] compressed.")
      compressionType = "gzip"
    }
    // Check for zip
    else if (magicNumber == 0x504b0304) {
      logger.info(s"file [$filePath] is [zip] compressed.")
      compressionType = "zip"
    }
    else {
      logger.warn(s"File [$filePath] with magicNumber [$magicNumber] does not match any of the available compression type checks.")
    }

    compressionType
  }

  def getMagicNumber(zipFilePath: String): Long = {
    val raf: RandomAccessFile = new RandomAccessFile(zipFilePath, "r")
    val magicNumber: Long = raf.readInt()
    raf.close()

    logger.info(s"magicNumber [${magicNumber.toHexString}]")

    magicNumber
  }

}
