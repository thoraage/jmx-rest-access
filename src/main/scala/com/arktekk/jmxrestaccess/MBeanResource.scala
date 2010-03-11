package com.arktekk.jmxrestaccess

import collection.jcl.SetWrapper
import javax.ws.rs.core.{Context, UriInfo, MediaType}
import javax.ws.rs._
import javax.management.{ObjectName}
import xml.Elem

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@Path("rest/mbeans")
@Produces(Array(MediaType.APPLICATION_XML))
class MBeanResource {
  @GET
  def getAll(@Context uriInfo: UriInfo, @QueryParam("domainName") domainName: String): Elem = {
    val objectName = if (domainName != null) new ObjectName(domainName + ":*") else null
    val objectNames = new SetWrapper[ObjectName] {
      def underlying = JMX.getMBeanServerConnection.queryNames(objectName, null)
    }
    XHTML.createHead(if (domainName == null) "All Managed Beans" else "Managed Beans in Domain " + domainName,
      <span class="mbeans">
        <ul>
          {objectNames.map({
          objectName =>
            <li>
              {createMBeanA(objectName, uriInfo, null)}
            </li>
        })}
        </ul>
      </span>)
  }

  @GET
  @Path("{domainAndKeys}")
  def get(@Context uriInfo: UriInfo, @PathParam("domainAndKeys") domainAndKeys: String): Elem = {
    val objectName: ObjectName = new ObjectName(decodeUrl(domainAndKeys))
    val info = JMX.getMBeanServerConnection.getMBeanInfo(objectName)
    XHTML.createHead("Managed Bean " + domainAndKeys,
      createMBeanA(objectName, uriInfo,
        <span class="mbean">
          <ul>
            {info.getAttributes.map(attributeInfo =>
            <li>
              <a class="attribute" href="nowhere">
                {attributeInfo.getName}
              </a>
            </li>)}
          </ul>
        </span>))
  }

  private def createMBeanA(mbean: ObjectName, uriInfo: UriInfo, content: Elem): Elem = {
    val id = mbean.getDomain + ":" + mbean.getKeyPropertyListString()
    <a class="mbean" id={id} href={uriInfo.getBaseUriBuilder.path(classOf[MBeanResource]).path(encodeUrl(mbean.toString)).build().toString}>
      {if (content != null) content else id}
    </a>
  }

  private def encodeUrl(s: String): String = s.replaceAll("\\/", "%2F").replaceAll(":", "%3A").replaceAll("=", "%3D")

  private def decodeUrl(s: String): String = s.replaceAll("%2F", "\\/").replaceAll("%3A", ":").replaceAll("%3D", "=")

}