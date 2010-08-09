package com.arktekk.jmxrestaccess.util

import com.arktekk.jmxrestaccess.jmx.ResponseException
import xml.Node
import net.liftweb.common.{Full, Box}
import net.liftweb.http.{NoContentResponse, LiftResponse, XmlResponse, ResponseWithReason}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object ExceptionHandler {
  def contain(f: => Option[Node]): Box[LiftResponse] = {
    try {
      f match {
        case Some(node) => Full(XmlResponse(node))
        case None => Full(NoContentResponse())
      }
    } catch {
      case ResponseException(response) => Full(ResponseWithReason(response, ""))
    }
  }
}