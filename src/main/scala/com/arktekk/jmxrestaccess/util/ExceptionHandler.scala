package com.arktekk.jmxrestaccess.util

import com.arktekk.jmxrestaccess.jmx.ResponseException
import net.liftweb.http.{LiftResponse, ResponseWithReason}
import net.liftweb.common.{Full, Box}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object ExceptionHandler {
  def contain(f: () => Box[LiftResponse]): Box[LiftResponse] = {
    try {
      f()
    } catch {
      case ResponseException(response) => Full(ResponseWithReason(response, ""))
    }
  }
}