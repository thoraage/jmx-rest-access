package com.arktekk.jmxrestaccess

import java.io.File
import java.lang.String

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object FileHelper {
  val UTF8 = "UTF-8"

  def using[T <: {def close()}](resource: T)(block: T => Unit) {
    try {
      block(resource)
    } finally {
      if (resource != null) resource.close()
    }
  }

  class FileWrapper(file: File) {
    def /?(fileName: String) = new File(file, fileName)

    def /(fileName: String) = {
      val newFile = /?(fileName)
      if (!newFile.exists) error("File " + newFile.toString + " not found")
      newFile
    }

    def /!(fileName: String) = {
      val newDir = /?(fileName)
      newDir.mkdirs
      newDir
    }

    def doOrElse[I, O](f: (File => O), other: (Unit => O)): O = if (file.exists) f(file) else other()

    def deleteAll: Unit = if (file.exists) FileHelper.deleteAll(file)

    def getNameWithoutExtension: String = file.getName.replaceFirst("\\.[^\\.]*", "")
  }

  def deleteAll(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles.foreach {deleteAll(_)}
    } else if (!file.isFile)
      error("Not directory or file; what can it be!")
    file.delete || error("Unable to delete file " + file)
  }

  implicit def file2filewrapper(file: File) = new FileWrapper(file)

}