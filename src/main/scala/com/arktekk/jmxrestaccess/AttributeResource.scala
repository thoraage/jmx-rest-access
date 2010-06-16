package com.arktekk.jmxrestaccess

import javax.ws.rs.{PathParam, GET, Produces, Path}
import java.net.URLDecoder
import javax.management.ObjectName
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.{Context, MediaType}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@Path("rest/{host}/mbeans/{domainAndKeys}/attributes")
@Produces(Array(MediaType.APPLICATION_XML))
class AttributeResource {
  @GET
  @Path("{attributeName}")
  def get(@PathParam("domainAndKeys") domainAndKeys: String, @PathParam("attributeName") attributeName: String, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    val value = JMXHelperImpl.getMBeanServerConnection(request, host).getAttribute(new ObjectName(URLDecoder.decode(domainAndKeys, "UTF-8")), attributeName)
    JmxAccessXhtml.createHead("Attribute " + attributeName,
      <div class="value">
        {value.toString}
      </div>)
  }
}
