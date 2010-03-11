package com.arktekk.jmxrestaccess

import javax.ws.rs.ext.{Provider, ExceptionMapper}
import javax.ws.rs.Produces
import javax.ws.rs.core.{MediaType, Response}
import org.apache.log4j.Logger
import javax.management.{InstanceNotFoundException, AttributeNotFoundException, JMException}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
object CustomExceptionMapper {
  val logger = Logger.getLogger(classOf[CustomExceptionMapper])
}

@Provider
@Produces(Array(MediaType.APPLICATION_XML))
class CustomExceptionMapper extends ExceptionMapper[JMException] {
  def toResponse(ex: JMException): Response = {
    val (status, message) =
    if (ex.isInstanceOf[AttributeNotFoundException])
      (Response.Status.NOT_FOUND, "Attribute not found")
    else if (ex.isInstanceOf[InstanceNotFoundException])
      (Response.Status.NOT_FOUND, "Instance not found")
    else
      (Response.Status.INTERNAL_SERVER_ERROR, null)
    CustomExceptionMapper.logger.error("Error from resources", ex)
    Response.status(status).entity(
      XHTML.createHead(status.getReasonPhrase,
        <span class="management error">
          {message}
          -
          {ex.getMessage}
        </span>)).build()
  }
}