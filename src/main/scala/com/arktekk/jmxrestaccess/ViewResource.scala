package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{UriInfo, Context, MediaType}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs._
import FileHelper._
import java.io.{FileOutputStream, File}
import java.nio.channels.Channels
import xml.{XML, Elem}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@Path("rest/{host}/view")
@Produces(Array(MediaType.APPLICATION_XML))
class ViewResourceImpl extends ViewResource {
  def getJmxHelper = JMXHelperImpl

  def getRepositoryDirectory = new File(".") /! "repository"
}

trait ViewResource {
  def getJmxHelper: JMXHelper

  def getRepositoryDirectory: File

  @GET
  def getAll(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    val views = (getRepositoryDirectory /? host).doOrElse({_.listFiles.map(_.getName.replaceAll(".xml", ""))}, Array[String]())
    val viewLinks = views.map {
      view =>
        val url = UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[ViewResourceImpl]).path(classOf[ViewResourceImpl], "getView").build(host, view)
        (view, url)
    }
    JmxAccessXhtml.createHead("Views",
      <span>
        <li class="views">
          {viewLinks.map {pair => <a id={pair._1} href={pair._2.toString}/>}}
        </li>
        <span class="create-template">
          {UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[ViewResourceImpl]).path(classOf[ViewResourceImpl], "createItem").build().toString.replaceAll("%7[bB]", "{").replaceAll("%7[dD]", "}")}
        </span>
      </span>)
  }

  @Path("{view}")
  @GET
  def getView(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String) = {
    JmxAccessXhtml.createHead("Views",
        <ul/>
      )
  }

  @Path("{view}/items/{item}")
  @PUT
  def createItem(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String, @PathParam("item") item: String, document: Elem): Unit = {
    val file = getRepositoryDirectory /! host /! view /? (item + ".xml")
    using(Channels.newWriter(new FileOutputStream(file).getChannel, UTF8)) {
      writer => XML.write(writer, document, UTF8, true, null)
    }
  }

  @Path("{view}/item")
  @GET
  def getItem(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String, @PathParam("item") item: String) = {
    JmxAccessXhtml.createHead("Views",
        <ul/>
      )
  }

}