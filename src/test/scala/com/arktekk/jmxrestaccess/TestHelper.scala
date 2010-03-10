package com.arktekk.jmxrestaccess

import java.io.File

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object TestHelper {
  def getBaseDir(aClass: Class[_]): File = {
    val basedir = System.getProperty("basedir")
    if (basedir != null) {
      new File(basedir)
    } else {
      var file = new File(aClass.getProtectionDomain().getCodeSource().getLocation().getPath())
      if (!file.exists()) {
        throw new RuntimeException("Unable to find basedir")
      }
      while (!new File(file, "pom.xml").exists()) {
        file = file.getParentFile()
        if (file == null) {
          throw new RuntimeException("Unable to find basedir")
        }
      }
      file
    }
  }

  def getWebAppDir(aClass: Class[_]): String = {
    val dir = new File(getBaseDir(aClass), "src/main/webapp")
    if (!dir.exists()) {
      throw new RuntimeException("Unable to find web application directory")
    }
    dir.toString()
  }

}