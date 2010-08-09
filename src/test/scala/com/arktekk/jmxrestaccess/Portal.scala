package com.arktekk.jmxrestaccess

import org.mortbay.resource.ResourceCollection
import org.mortbay.jetty.Server
import org.mortbay.jetty.nio.SelectChannelConnector
import org.mortbay.jetty.webapp.WebAppContext

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object Portal {
  def main(args: Array[String]): Unit = new PortalImpl
}

class PortalImpl {
  val server = new Server();
  val connector = new SelectChannelConnector();
  connector.setPort(8888);
  server.setConnectors(Array(connector));
  val context = new WebAppContext();
  context.setContextPath("/");
  val paths = new ResourceCollection(Array(TestHelper.getWebAppDir(this.getClass).toString));
  context.setBaseResource(paths);
  server.addHandler(context);
  server.start();
}
