package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{MediaType, UriInfo, Context}
import javax.ws.rs.{Produces, GET, Path}

@Path("domains")
@Produces(Array(MediaType.APPLICATION_XML))
class DomainResource {
  @GET
  def getAll(@Context uriInfo: UriInfo) = {
    val domains = JMX.getMBeanServerConnection.getDomains
    <domains>
      {domains.map({
      domain => <domain name={domain} mbeansUri={uriInfo.getBaseUriBuilder.path(classOf[MBeanResource]).queryParam("domainName", domain).build().toString}/>
    })}
    </domains>
  }
}