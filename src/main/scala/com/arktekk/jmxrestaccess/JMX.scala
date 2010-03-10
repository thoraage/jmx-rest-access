package com.arktekk.jmxrestaccess

import javax.management.remote.{JMXServiceURL, JMXConnector, JMXConnectorFactory}
import java.util.{HashMap, Map => JMap}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object JMX {
  def getMBeanServerConnection = {
    val jmxEnv: JMap[String, Array[String]] = new HashMap
    val credentials = Array("admin", "adminadmin")
    val jmxGlassFishConnectorString = "service:jmx:rmi:///jndi/rmi://localhost:8686/jmxrmi"
    val jmxUrl = new JMXServiceURL(jmxGlassFishConnectorString)
    jmxEnv.put(JMXConnector.CREDENTIALS, credentials)
    val connector = JMXConnectorFactory.connect(jmxUrl, jmxEnv)
    connector.getMBeanServerConnection()
  }

}