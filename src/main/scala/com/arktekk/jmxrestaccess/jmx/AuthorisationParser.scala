package com.arktekk.jmxrestaccess.jmx

import scala.util.parsing.combinator._
import com.sun.jersey.core.util.Base64

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object AuthorisationParser extends RegexParsers {
  def base64decode(encoded: String) = new String(Base64.base64Decode(encoded))

  val colonSplit = "([^:]+):(.*)".r

  def userNamePassword(encoded: String) = base64decode(encoded) match {
    case colonSplit(username, password) => Some(username, password)
    case _ => None
  }

  def authorisation = """([^ ]+)""".r

  def basic = "Basic " ~> authorisation ^^ userNamePassword

  def find(input: String) = parseAll(basic, input) match {
    case Success(Some(r), _) => r
    case _ => error("Illegal input")
  }

}