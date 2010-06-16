package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{UriInfo, Context, MediaType}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs._

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@Path("rest/{host}/view")
@Produces(Array(MediaType.APPLICATION_XML))
class ViewResource {
  @GET
  def getAll(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    XHTML.createHead("Views",
        <ul/>
      );
  }

  @Path("{view}")
  @GET
  def get(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String) = {
    XHTML.createHead("Views",
        <ul/>
      );
  }

  @Path("{view}/item/{item}")
  @PUT
  def get(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String, @PathParam("item") item: String) = {
    XHTML.createHead("Views",
        <ul/>
      );
  }

  @Path("{view}/item")
  @GET
  def getItem(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String, @PathParam("view") view: String, @PathParam("item") item: String) = {
    XHTML.createHead("Views",
        <ul/>
      );
  }

}