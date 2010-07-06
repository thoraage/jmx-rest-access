package com.arktekk.jmxrestaccess

import javax.management.remote.{JMXServiceURL, JMXConnector, JMXConnectorFactory}
import java.util.{HashMap, Map => JMap}
import javax.management.MBeanServerConnection
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.{Response}
import scala.util.parsing.combinator._
import com.sun.jersey.core.util.Base64
import net.liftweb.common.{Full, Box}
import net.liftweb.http.{LiftResponse, UnauthorizedResponse, SessionVar, Req}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
trait JMXHelper {
  def getMBeanServerConnection(request: HttpServletRequest, host: String): MBeanServerConnection

  def getMBeanServerConnection(req: Req, host: String): MBeanServerConnection
}

case class ResponseException(msg: String, response: LiftResponse) extends RuntimeException(msg)

object JMXHelperImpl extends JMXHelper {
  val realm = "JMX multi-host domain"

  object connectionMapVar extends SessionVar[Map[String, MBeanServerConnection]](Map())

  def getMBeanServerConnection(req: Req, host: String): MBeanServerConnection = {
    def unauthorisedException: Exception = {
      new ResponseException("", UnauthorizedResponse(realm))
    }
    connectionMapVar.get.get(host) match {
      case None =>
        req.header("Authorization") match {
          case Full(authorisation) =>
            try {
              val credentials: (String, String) = AuthorisationParser.find(authorisation)
              val connection = getMBeanServerConnection(credentials._1, credentials._2, host)
              connectionMapVar(connectionMapVar.get + (host -> connection))
              connection
            } catch {
              case e: Exception => throw unauthorisedException
            }
          case _ =>
            throw unauthorisedException
        }
      case Some(connection) =>
        connection
    }
  }

  def getMBeanServerConnection(request: HttpServletRequest, host: String): MBeanServerConnection = {
    val connection = request.getSession.getAttribute(host).asInstanceOf[MBeanServerConnection]
    def throwUnauthorisedException: Unit = {
      val response = Response.status(Response.Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"" + realm + "\"").build
      throw new WebApplicationException(response)
    }
    if (connection == null) {
      val authorisation = request.getHeader("Authorization")
      if (authorisation != null) {
        try {
          val credentials: (String, String) = AuthorisationParser.find(authorisation)
          val connection = getMBeanServerConnection(credentials._1, credentials._2, host)
          request.getSession.setAttribute(host, connection)
          return connection
        } catch {
          case e: Exception => throwUnauthorisedException
        }
      } else {
        throwUnauthorisedException
      }
    }
    return connection
  }

  def getMBeanServerConnection(userName: String, password: String, host: String): MBeanServerConnection = {
    val jmxEnv: JMap[String, Array[String]] = new HashMap
    val credentials = Array(userName, password)
    val jmxGlassFishConnectorString = "service:jmx:rmi:///jndi/rmi://" + host + "/jmxrmi"
    val jmxUrl = new JMXServiceURL(jmxGlassFishConnectorString)
    jmxEnv.put(JMXConnector.CREDENTIALS, credentials)
    val connector = JMXConnectorFactory.connect(jmxUrl, jmxEnv)
    connector.getMBeanServerConnection()
  }

}

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