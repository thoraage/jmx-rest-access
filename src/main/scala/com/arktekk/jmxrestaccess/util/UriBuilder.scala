package com.arktekk.jmxrestaccess.util

import net.liftweb.http.Req

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

class UriBuilder(req: Req, path: List[String]) {
  lazy val uri = "/" + path.mkString("/")
}