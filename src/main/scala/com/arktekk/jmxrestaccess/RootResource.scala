package com.arktekk.jmxrestaccess

import net.liftweb.http.rest.RestHelper
import net.liftweb.http.Req
import util.{JmxAccessXhtml, UriBuilder}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object RootResource extends RestHelper {
  object Tull {
    def unapply(r: Req): Option[(List[String], Req)] = Some(r.path.partPath -> r)
  }

  serve {
    case req@Tull(host :: Nil, _) => get(req, host)
  }

  def get(req: Req, host: String) = {
    JmxAccessXhtml.createHead("Root",
      <ul>
        <li class="domains">
          {linkTo(new UriBuilder(req, DomainGet(host)), "Domains")}
        </li>
        <li class="mbeans">
          { //linkTo(req, host, classOf[MBeanResource], "MBeans")
          }
        </li>
        <li class="views">
          { //linkTo(req, host, classOf[ViewResource], "Views")
          }
        </li>
      </ul>)
  }

  def linkTo(uriBuilder: UriBuilder, name: String) =
    <a href={uriBuilder.uri}>
      {name}
    </a>

}