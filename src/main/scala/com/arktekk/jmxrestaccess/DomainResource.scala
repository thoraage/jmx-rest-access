package com.arktekk.jmxrestaccess

import jmx.{JMXHelperImpl, JMXHelper}
import util.JmxAccessXhtml
import util.ExceptionHandler._
import util.UriBuilder
import xml.Elem
import net.liftweb.http.rest.RestHelper
import net.liftweb.http._

object DomainResourceImpl extends DomainResource with RestHelper {
  def jmxHelper = JMXHelperImpl

  serve {
    case req@Req(DomainsUri(host, Nil), _, GetRequest) => contain {getAll(req, host)}
  }

}

object DomainsUri {
  def apply(host: String, rest: List[String]) = host :: "domains" :: rest

  def unapply(path: List[String]): Option[(String, List[String])] =
    path match {
      case host :: "domains" :: rest => Some(host, rest)
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
          <a class="domain" href={new UriBuilder(req, DomainMBeansUri(host, domain)).uri}>
            {domain}
          </a>
        </li>
      })}
      </ul>)
  }

}