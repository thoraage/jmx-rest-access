package com.arktekk.jmxrestaccess

import collection.jcl.SetWrapper
import javax.management.{ObjectName}
import jmx.JMXHelperImpl
import util.JmxAccessXhtml
import util.ExceptionHandler._
import util.UriBuilder
import xml.Elem
import java.net.{URLDecoder, URLEncoder}
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{GetRequest, Req}

object MBeansUri {
  def apply(host: String, rest: List[String]): List[String] = host :: "mbeans" :: rest

  def unapply(path: List[String]): Option[(String, List[String])] =
    path match {
      case host :: "mbeans" :: rest => Some(host, rest)
      case _ => None
    }
}

object DomainMBeansUri {
  def apply(host: String, domainName: String) = DomainsUri(host, domainName :: "mbeans" :: Nil)

  def unapply(path: List[String]): Option[(String, String)] =
    path match {
      case DomainsUri(host, domainName :: "mbeans" :: Nil) => Some(host, domainName)
      case _ => None
    }
}

object MBeanUri {
  def apply(host: String, domainAndKeys: String, rest: List[String]): List[String] = MBeansUri(host, domainAndKeys :: rest)

  def unapply(path: List[String]): Option[(String, String, List[String])] =
    path match {
      case MBeansUri(host, domainAndKeys :: rest) => Some(host, domainAndKeys, rest)
      case _ => None
    }
}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object MBeanResource extends RestHelper {
  serve {
    case req@Req(MBeansUri(host, Nil), _, GetRequest) => contain {getAll(req, host, null)}
    case req@Req(DomainMBeansUri(host, domainName), _, GetRequest) => contain {getAll(req, host, domainName)}
    case req@Req(MBeanUri(host, domainAndKeys, Nil), _, GetRequest) => contain {get(req, host, domainAndKeys)}
  }

  def getAll(req: Req, host: String, domainName: String): Elem = {
    val objectName = if (domainName != null) new ObjectName(domainName + ":*") else null
    val objectNames = new SetWrapper[ObjectName] {
      def underlying = JMXHelperImpl.getMBeanServerConnection(req, host).queryNames(objectName, null)
    }
    JmxAccessXhtml.createHead(if (domainName == null) "All Managed Beans" else "Managed Beans in Domain " + domainName,
      <ul>
        {objectNames.map({
        objectName =>
          <li>
            {createMBeanA(objectName, req, host, null)}
          </li>
      })}
      </ul>)
  }

  def get(req: Req, host: String, domainAndKeys: String): Elem = {
    val objectName: ObjectName = new ObjectName(URLDecoder.decode(domainAndKeys, "UTF-8"))
    val info = JMXHelperImpl.getMBeanServerConnection(req, host).getMBeanInfo(objectName)
    JmxAccessXhtml.createHead("Managed Bean " + domainAndKeys + " with attributes",
      createMBeanA(objectName, req, host,
        <ul>
          {info.getAttributes.map(attributeInfo =>
          <li>
            <a class="attribute" href={new UriBuilder(req, AttributeUri(host, URLEncoder.encode(domainAndKeys, "UTF-8").toString, attributeInfo.getName)).uri}>
              {attributeInfo.getName}
            </a>
          </li>)}
        </ul>))
  }

  private def createMBeanA(mbean: ObjectName, req: Req, host: String, content: Elem): Elem = {
    val id = mbean.getDomain + ":" + mbean.getKeyPropertyListString()
    <a class="mbean" href={new UriBuilder(req, MBeanUri(host, URLEncoder.encode(mbean.toString, "UTF-8"), Nil)).uri}>
      {if (content != null) content else id}
    </a>
  }

}