package com.arktekk.jmxrestaccess

import java.net.URLDecoder
import javax.management.ObjectName
import jmx.JMXHelperImpl
import util.JmxAccessXhtml
import util.ExceptionHandler._
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{GetRequest, Req}

object AttributeUri {
  def apply(host: String, domainAndKeys: String, attributeName: String): List[String] = MBeanUri(host, domainAndKeys, attributeName :: Nil)

  def unapply(path: List[String]): Option[(String, String, String)] =
    path match {
      case MBeanUri(host, domainAndKeys, attributeName :: Nil) => Some(host, domainAndKeys, attributeName)
      case _ => None
    }
}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object AttributeResource extends RestHelper {
  serve {
    case req@Req(AttributeUri(host, domainAndKeys, attributeName), _, GetRequest) => contain {get(req, domainAndKeys, attributeName, host)}
  }

  def get(req: Req, urlEncodedDomainAndKeys: String, attributeName: String, host: String) = {
    val connection = JMXHelperImpl.getMBeanServerConnection(req, host)
    val domainAndKeys = URLDecoder.decode(urlEncodedDomainAndKeys, "UTF-8")
    val value = connection.getAttribute(new ObjectName(domainAndKeys), attributeName)
    JmxAccessXhtml.createHead("Attribute " + attributeName,
      <div class="value">
        {value.toString}
      </div>)
  }
}
