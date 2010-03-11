package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{MediaType, UriInfo, Context}
import javax.ws.rs.{Produces, GET, Path}

@Path("rest/domains")
@Produces(Array(MediaType.APPLICATION_XML))
class DomainResource {
  @GET
  def getAll(@Context uriInfo: UriInfo) = {
    val domains = JMX.getMBeanServerConnection.getDomains
    XHTML.createHead("Domains",
      <span class="management">
        <ul>
          {domains.map({
          domain => <li>
            <a class="domain" href={uriInfo.getBaseUriBuilder.path(classOf[MBeanResource]).queryParam("domainName", domain).build().toString}>
              {domain}
            </a>
          </li>
        })}
        </ul>
      </span>)
  }
}