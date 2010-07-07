package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{UriInfo, Context}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs._
import java.io.{File, FileOutputStream}
import java.nio.channels.Channels
import jmx.{ResponseException, JMXHelper, JMXHelperImpl}
import net.liftweb.http.{ResponseWithReason, GetRequest, Req}
import util.JmxAccessXhtml
import com.sun.jersey.api.NotFoundException
import xml.parsing.ConstructingParser
import io.Source
import javax.management.ObjectName
import util.FileHelper._
import util.UriBuilder
import net.liftweb.http.rest.RestHelper
import xml.{XML, Elem}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object ViewResourceImpl extends ViewResource with RestHelper {
  def getJmxHelper = JMXHelperImpl

  def getRepositoryDirectory = new File(".") /! "repository"

  serve {
    case req@Req(ViewGet(host, None), _, GetRequest) =>
      try {
        getAll(req, host)
      } catch {
        case ResponseException(msg, response) => ResponseWithReason(response, msg)
      }
  }
}

object ViewGet {
  def apply(host: String, view: Option[String]): List[String] = host :: "views" :: (if (view == None) Nil else view.get :: Nil)

  def unapply(path: List[String]): Option[(String, Option[String])] =
    path match {
      case host :: "views" :: Nil => Some(host, None)
      case host :: "views" :: view :: Nil => Some(host, Some(view))
      case _ => None
    }
}

trait ViewResource {
  def getJmxHelper: JMXHelper

  def getRepositoryDirectory: File

  def getAll(req: Req, host: String) = {
    val views = (getRepositoryDirectory /? host).doOrElse({_.listFiles.map(_.getName.replaceAll(".xml", ""))}, {_ => Array[String]()})
    val viewLinks = views.map {view => (view, ViewGet(host, Some(view)))}
    JmxAccessXhtml.createHead("Views",
      <span>
        <li class="views">
          {viewLinks.map {pair => <a id={pair._1} href={pair._2.toString}/>}}
        </li>
        <span class="create-template">
          <a href={new UriBuilder(req, ViewGet(host, None)).uri}>Create view</a>
        </span>
      </span>)
  }

  @Path("{view}/state")
  @GET
  def getView(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String) = {
    val itemFiles = (getRepositoryDirectory /? host /? view).doOrElse({_.listFiles}, {_ => throw new NotFoundException})
    val connection = getJmxHelper.getMBeanServerConnection(request, host)
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

  @Path("{view}/items/{item}")
  @PUT
  def createItem(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String, @PathParam("item") item: String, document: Elem): Unit = {
    val file = getRepositoryDirectory /! host /! view /? (item + ".xml")
    using(Channels.newWriter(new FileOutputStream(file).getChannel, UTF8)) {
      writer => XML.write(writer, document, UTF8, true, null)
    }
  }

}