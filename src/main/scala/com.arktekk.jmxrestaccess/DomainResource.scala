package com.arktekk.jmxrestaccess

import javax.management.remote.{JMXServiceURL, JMXConnector, JMXConnectorFactory}
import java.util.{HashMap, Map => JMap}
import javax.ws.rs.{Produces, GET, Path}
import javax.ws.rs.core.{MediaType, UriInfo, Context}

@Path("domains")
@Produces(Array(MediaType.APPLICATION_XML))
class DomainResource {

  def getMBeanServerConnection = {
    val jmxEnv: JMap[String, Array[String]] = new HashMap
    val credentials = Array("admin", "adminadmin")
    val jmxGlassFishConnectorString = "service:jmx:rmi:///jndi/rmi://localhost:8686/jmxrmi"
    val jmxUrl = new JMXServiceURL(jmxGlassFishConnectorString)
    jmxEnv.put(JMXConnector.CREDENTIALS, credentials)
    val connector = JMXConnectorFactory.connect(jmxUrl, jmxEnv)
    connector.getMBeanServerConnection()
  }

  @GET
  def getAll(@Context uriInfo: UriInfo) = {
    val mbsc = getMBeanServerConnection
    val domains = mbsc.getDomains
    <domains> {
      domains.map({
        domain => <domain name={domain} uri={uriInfo.getBaseUriBuilder().path(domain).build().toString}/>
      })
    } </domains>
  }

}