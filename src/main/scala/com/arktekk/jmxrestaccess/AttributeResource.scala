package com.arktekk.jmxrestaccess

import javax.ws.rs.core.MediaType
import javax.ws.rs.{PathParam, GET, Produces, Path}
import java.net.URLDecoder
import javax.management.ObjectName

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@Path("rest/mbeans/{domainAndKeys}/attributes")
@Produces(Array(MediaType.APPLICATION_XML))
class AttributeResource {
  @GET
  @Path("{attributeName}")
  def get(@PathParam("domainAndKeys") domainAndKeys: String, @PathParam("attributeName") attributeName: String) = {
    val value = JMXHelper.getMBeanServerConnection.getAttribute(new ObjectName(URLDecoder.decode(domainAndKeys, "UTF-8")), attributeName)
    XHTML.createHead("Attribute " + attributeName,
      <div class="value">
        {value.toString}
      </div>)
  }
}
