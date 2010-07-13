package com.arktekk.jmxrestaccess.util

import com.arktekk.jmxrestaccess.jmx.ResponseException
import net.liftweb.common.{Full, Box}
import xml.Node
import net.liftweb.http.{LiftResponse, XmlResponse, ResponseWithReason}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object ExceptionHandler {
  def contain(f: => Node): Box[LiftResponse] = {
    try {
      Full(XmlResponse(f))
    } catch {
      case ResponseException(response) => Full(ResponseWithReason(response, ""))
    }
  }
}