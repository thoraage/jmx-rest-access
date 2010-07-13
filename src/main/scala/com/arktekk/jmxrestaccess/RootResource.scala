package com.arktekk.jmxrestaccess

import net.liftweb.http.rest.RestHelper
import util.{JmxAccessXhtml, UriBuilder}
import net.liftweb.http.{GetRequest, Req}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object RootResource extends RestHelper {
  serve {
    case req@Req(host :: Nil, _, GetRequest) => get(req, host)
  }

  def get(req: Req, host: String) = {
    JmxAccessXhtml.createHead("Root",
      <ul>
        <li class="domains">
          {linkTo(new UriBuilder(req, DomainsUri(host, Nil)), "Domains")}
        </li>
        <li class="mbeans">
          {linkTo(new UriBuilder(req, MBeansUri(host, Nil)), "MBeans")}
        </li>
        <li class="views">
          {linkTo(new UriBuilder(req, ViewsUri(host, Nil)), "Views")}
        </li>
      </ul>)
  }

  def linkTo(uriBuilder: UriBuilder, name: String) =
    <a href={uriBuilder.uri}>
      {name}
    </a>

}