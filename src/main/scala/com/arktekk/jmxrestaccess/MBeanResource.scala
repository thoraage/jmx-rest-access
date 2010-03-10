package com.arktekk.jmxrestaccess

import collection.jcl.SetWrapper
import javax.ws.rs.core.{Context, UriInfo, MediaType}
import javax.ws.rs._
import javax.management.{ObjectName}
import xml.Elem

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@Path("mbeans")
@Produces(Array(MediaType.APPLICATION_XML))
class MBeanResource {
  @GET
  def getAll(@Context uriInfo: UriInfo, @QueryParam("domainName") domainName: String): Elem = {
    val objectName = if (domainName != null) new ObjectName(domainName + ":*") else null
    val objectNames = new SetWrapper[ObjectName] {
      def underlying = JMX.getMBeanServerConnection.queryNames(objectName, null)
    }
    <mbeans>
      {objectNames.map({objectName => getMBean(objectName, uriInfo, null)})}
    </mbeans>
  }

  @GET
  @Path("{domainAndKeys}")
  def get(@Context uriInfo: UriInfo, @PathParam("domainAndKeys") domainAndKeys: String): Elem = {
    val objectName: ObjectName = new ObjectName(decodeUrl(domainAndKeys))
    val info = JMX.getMBeanServerConnection.getMBeanInfo(objectName)
    getMBean(objectName, uriInfo,
      <attributes>
        {info.getAttributes.map(attributeInfo => <attribute type={attributeInfo.getType}
                                                            name={attributeInfo.getName}/>)}
      </attributes>)
  }

  private def getMBean(mbean: ObjectName, uriInfo: UriInfo, content: Elem): Elem =
    <mbean domainName={mbean.getDomain} keyProperties={mbean.getKeyPropertyListString()} uri={uriInfo.getBaseUriBuilder.path(classOf[MBeanResource]).path(encodeUrl(mbean.toString)).build().toString}>
      {if (content != null) content else ""}
    </mbean>

  private def encodeUrl(s: String): String = s.replaceAll("\\/", "%2F").replaceAll(":", "%3A").replaceAll("=", "%3D")

  private def decodeUrl(s: String): String = s.replaceAll("%2F", "\\/").replaceAll("%3A", ":").replaceAll("%3D", "=")

}