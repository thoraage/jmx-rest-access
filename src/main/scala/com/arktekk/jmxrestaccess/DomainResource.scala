package com.arktekk.jmxrestaccess

import jmx.{JMXHelperImpl, JMXHelper}
import util.JmxAccessXhtml
import util.ExceptionHandler._
import xml.Elem
import net.liftweb.http.rest.RestHelper
import net.liftweb.http._

object DomainResourceImpl extends DomainResource with RestHelper {
  def jmxHelper = JMXHelperImpl

  serve {
    case req@Req(DomainUri(host), _, GetRequest) => contain {getAll(req, host)}
  }

}

object DomainUri {
  def apply(host: String) = host :: "domains" :: Nil

  def unapply(path: List[String]): Option[String] =
    path match {
      case host :: "domains" :: Nil => Some(host)
      case _ => None
    }
}

trait DomainResource {
  def jmxHelper: JMXHelper

  def getAll(req: Req, host: String): Elem = {
    val domains = jmxHelper.getMBeanServerConnection(req, host).getDomains
    JmxAccessXhtml.createHead("Domains",
      <ul>
        {domains.map({
        domain => <li>
          <a class="domain" href={"tull"}>
            {domain}
          </a>
        </li>
      })}
      </ul>)
  }

}