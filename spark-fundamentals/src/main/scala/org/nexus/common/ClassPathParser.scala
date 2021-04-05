package org.nexus.common

object ClassPathParser {

  def returnAbsolutePath(relativePathStr: String): String = {

    var finalValueStr = ""
    if (relativePathStr.startsWith("classpath://")) {
      val relativePath = cleanupDuplicateSlashes(relativePathStr.split("classpath://")(1))

      val absoluteClassPath = cleanupFileURI(getClass.getClassLoader.getResource("").toString)
      finalValueStr = absoluteClassPath.concat(relativePath)
    }

    finalValueStr
  }

  // TODO merge and create a common cleanupPathURI method
  def cleanupDuplicateSlashes(path: String): String = {
    path.replaceAll("/+", "/")
  }

  def cleanupFileURI(fileURI: String): String = {
    // Handle number of slashes after the file string.
    val regexExpr = "(?<=file:/)(/*)(?=\\w+)"
    fileURI.replaceAll(regexExpr, "//")
  }
}
