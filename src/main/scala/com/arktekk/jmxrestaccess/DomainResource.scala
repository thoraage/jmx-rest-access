package com.arktekk.jmxrestaccess

import jmx.{JMXHelperImpl, JMXHelper, ResponseException}
import util.JmxAccessXhtml
import xml.Elem
import net.liftweb.http.rest.RestHelper
import net.liftweb.http._

object DomainResourceImpl extends DomainResource with RestHelper {
  def jmxHelper = JMXHelperImpl

  serve {
    case req@Req(DomainGet(host), _, GetRequest) =>
      try {
        getAll(req, host)
      } catch {
        case ResponseException(msg, response) => ResponseWithReason(response, msg)
      }
  }

}

object DomainGet {
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