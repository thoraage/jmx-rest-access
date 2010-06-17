package com.arktekk.jmxrestaccess

import java.io.File
import com.sun.jersey.core.util.MultivaluedMapImpl
import javax.ws.rs.core.{UriBuilder, PathSegment, UriInfo}
import java.util.ArrayList
import java.net.URI
import xml.Elem

import XmlHelper._

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

  def getUriInfo = {
    val arktekk = new URI("http://arktekk.no/")
    new UriInfo() {
      override def getPath() = arktekk.getPath()

      override def getPath(decode: Boolean) = getPath()

      override def getPathSegments() = new ArrayList[PathSegment]()

      override def getPathSegments(decode: Boolean) = getPathSegments()

      override def getRequestUri = arktekk

      override def getRequestUriBuilder = UriBuilder.fromUri(arktekk)

      override def getAbsolutePath() = arktekk

      override def getAbsolutePathBuilder() = UriBuilder.fromUri(arktekk)

      override def getBaseUri = arktekk

      override def getBaseUriBuilder = UriBuilder.fromUri(arktekk)

      override def getPathParameters = new MultivaluedMapImpl()

      override def getPathParameters(decode: Boolean) = getPathParameters()

      override def getQueryParameters = getPathParameters()

      override def getQueryParameters(decode: Boolean) = getPathParameters()

      override def getMatchedURIs() = new ArrayList[String]() { {add(arktekk.getPath())}}

      override def getMatchedURIs(decode: Boolean) = getMatchedURIs()

      override def getMatchedResources() = new ArrayList[Object]()
    }
  }

  def getManagementElement(document: Elem) = {
    document \\ "html" \ "body" \ "span" whereAt ("class", _ == "management")
  }

}