package com.arktekk.jmxrestaccess

import collection.jcl.SetWrapper
import javax.ws.rs._
import core.{Context, UriInfo, MediaType}
import javax.management.{ObjectName}
import xml.Elem
import java.net.{URLDecoder, URLEncoder}
import javax.servlet.http.HttpServletRequest

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@Path("rest/{host}/mbeans")
@Produces(Array(MediaType.APPLICATION_XML))
class MBeanResource {
  @GET
  def getAll(@Context uriInfo: UriInfo, @QueryParam("domainName") domainName: String, @Context request: HttpServletRequest, @PathParam("host") host: String): Elem = {
    val objectName = if (domainName != null) new ObjectName(domainName + ":*") else null
    val objectNames = new SetWrapper[ObjectName] {
      def underlying = JMXHelper.getMBeanServerConnection(request, host).queryNames(objectName, null)
    }
    XHTML.createHead(if (domainName == null) "All Managed Beans" else "Managed Beans in Domain " + domainName,
      <ul>
        {objectNames.map({
        objectName =>
          <li>
            {createMBeanA(objectName, uriInfo, host, null)}
          </li>
      })}
      </ul>)
  }

  @GET
  @Path("{domainAndKeys}")
  def get(@Context uriInfo: UriInfo, @PathParam("domainAndKeys") domainAndKeys: String, @Context request: HttpServletRequest, @PathParam("host") host: String): Elem = {
    val objectName: ObjectName = new ObjectName(URLDecoder.decode(domainAndKeys, "UTF-8"))
    val info = JMXHelper.getMBeanServerConnection(request, host).getMBeanInfo(objectName)
    XHTML.createHead("Managed Bean " + domainAndKeys + " with attributes",
      createMBeanA(objectName, uriInfo, host,
        <ul>
          {info.getAttributes.map(attributeInfo =>
          <li>
            <a class="attribute" href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[AttributeResource]).path(attributeInfo.getName).build(host, URLEncoder.encode(domainAndKeys, "UTF-8")).toString}>
              {attributeInfo.getName}
            </a>
          </li>)}
        </ul>))
  }

  private def createMBeanA(mbean: ObjectName, uriInfo: UriInfo, host: String, content: Elem): Elem = {
    val id = mbean.getDomain + ":" + mbean.getKeyPropertyListString()
    <a class="mbean" href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[MBeanResource]).path(URLEncoder.encode(mbean.toString, "UTF-8")).build(host).toString}>
      {if (content != null) content else id}
    </a>
  }

}