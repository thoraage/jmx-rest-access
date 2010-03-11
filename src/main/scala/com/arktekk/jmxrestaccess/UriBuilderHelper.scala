package com.arktekk.jmxrestaccess

import _root_.com.sun.jersey.api.uri.UriBuilderImpl
import javax.ws.rs.core.UriInfo

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object UriBuilderHelper {
  def cloneBaseUriBuilder(uriInfo: UriInfo) = uriInfo.getBaseUriBuilder.asInstanceOf[UriBuilderImpl].clone
}
