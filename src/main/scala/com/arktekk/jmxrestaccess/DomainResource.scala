package com.arktekk.jmxrestaccess

import javax.ws.rs.{PathParam, Produces, GET, Path}
import javax.ws.rs.core._
import javax.servlet.http.HttpServletRequest

@Path("rest/{host}/domains")
@Produces(Array(MediaType.APPLICATION_XML))
class DomainResourceImpl extends DomainResource {
  def jmxHelper = JMXHelper

}

trait DomainResource {
  def jmxHelper: JMXHelperI

  @GET
  def getAll(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    val domains = jmxHelper.getMBeanServerConnection(request, host).getDomains
    XHTML.createHead("Domains",
      <ul>
        {domains.map({
        domain => <li>
          <a class="domain" href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[MBeanResource]).queryParam("domainName", domain).build(host).toString}>
            {domain}
          </a>
        </li>
      })}
      </ul>)
  }

}