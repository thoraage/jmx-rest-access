package com.arktekk.jmxrestaccess

import java.io.{File, FileOutputStream}
import java.nio.channels.Channels
import jmx.{ResponseException, JMXHelper, JMXHelperImpl}
import util.JmxAccessXhtml
import xml.parsing.ConstructingParser
import io.Source
import javax.management.ObjectName
import util.FileHelper._
import util.UriBuilder
import util.ExceptionHandler._
import net.liftweb.http.rest.RestHelper
import xml.{XML, Elem}
import net.liftweb.http.{PutRequest, NotFoundResponse, GetRequest, Req}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object ViewResourceImpl extends ViewResource with RestHelper {
  def getJmxHelper = JMXHelperImpl

  def getRepositoryDirectory = new File(".") /! "repository"

  serve {
    case req@Req(ViewsUri(host, Nil), _, GetRequest) => contain {() => getAll(req, host)}
    case req@Req(StatesUri(host, view), _, GetRequest) => contain {() => getViewState(req, host, view)}
    case req@Req(ItemUri(host, view, item), _, PutRequest) => contain {() => <b/>}
  }
}

object ViewsUri {
  def apply(host: String, rest: List[String]): List[String] = host :: "views" :: rest

  def unapply(path: List[String]): Option[(String, List[String])] =
    path match {
      case host :: "views" :: rest => Some(host, rest)
      case _ => None
    }
}

object ViewUri {
  def apply(host: String, view: String, rest: List[String]) = ViewsUri(host, view :: rest)

  def unapply(path: List[String]): Option[(String, String, List[String])] =
    path match {
      case ViewsUri(host, view :: rest) => Some(host, view, rest)
      case _ => None
    }
}

object ItemUri {
  def apply(host: String, view: String, item: String): List[String] = ViewUri(host, view, "items" :: item :: Nil)

  def unapply(path: List[String]): Option[(String, String, String)] =
    path match {
      case ViewUri(host, view, "items" :: item :: Nil) => Some(host, view, item)
      case _ => None
    }
}

object StatesUri {
  def apply(host: String, view: String): List[String] = ViewUri(host, view, "states" :: Nil)

  def unapply(path: List[String]): Option[(String, String)] =
    path match {
      case ViewUri(host, view, "states" :: Nil) => Some(host, view)
      case _ => None
    }
}

trait ViewResource {
  def getJmxHelper: JMXHelper

  def getRepositoryDirectory: File

  def getAll(req: Req, host: String) = {
    val views = (getRepositoryDirectory /? host).doOrElse({_.listFiles.map(_.getName.replaceAll(".xml", ""))}, {_ => Array[String]()})
    val viewLinks = views.map {view => (view, ViewUri(host, view, Nil))}
    JmxAccessXhtml.createHead("Views",
      <span>
        <li class="views">
          {viewLinks.map {pair => <a id={pair._1} href={pair._2.toString}/>}}
        </li>
        <span class="create-template">
          <a href={new UriBuilder(req, ViewsUri(host, Nil)).uri}>Create view</a>
        </span>
      </span>)
  }

  def getViewState(req: Req, host: String, view: String) = {
    val itemFiles = (getRepositoryDirectory /? host /? view).doOrElse({_.listFiles}, {_ => throw new ResponseException(new NotFoundResponse("View " + view + " not found at " + host))})
    val connection = getJmxHelper.getMBeanServerConnection(req, host)
    val items = itemFiles.map {
      itemFile =>
        val document = ConstructingParser.fromSource(Source.fromFile(itemFile), false).document.docElem(0)
        val href = (document \ "a" \ "@href").text.trim
        val parser = ".*/mbeans/(.*)/attributes/(.*)".r
        href match {
          case parser(mbeanName, attributeName) =>
            val value = connection.getAttribute(new ObjectName(mbeanName), attributeName);
            <span id={itemFile.getNameWithoutExtension}>
              {value}
            </span>
        }
    }
    JmxAccessXhtml.createHead("Views", items)
  }

  def createItem(req: Req, host: String, view: String, item: String, document: Elem): Unit = {
    val file = getRepositoryDirectory /! host /! view /? (item + ".xml")
    using(Channels.newWriter(new FileOutputStream(file).getChannel, UTF8)) {
      writer => XML.write(writer, document, UTF8, true, null)
    }
  }

}