package com.arktekk.jmxrestaccess

import javax.ws.rs.core.{UriInfo, Context, MediaType}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs._

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@Path("rest/{host}/view")
@Produces(Array(MediaType.APPLICATION_XML))
class ViewResourceImpl extends ViewResource {
  def getJmxHelper = JMXHelperImpl
}

trait ViewResource {
  def getJmxHelper: JMXHelper

  @GET
  def getAll(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    JmxAccessXhtml.createHead("Views",
      <span>
        <li class="views"></li>
        <span class="create-template">
          {UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[ViewResourceImpl]).build()}
        </span>
      </span>
  )
}

@Path ("{view}")
@GET
def get (@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam ("host") host: String, @PathParam ("view") view: String) = {
JmxAccessXhtml.createHead ("Views",
<ul/>
)
}

@Path ("{view}/item/{item}")
@PUT
def create (@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam ("host") host: String, @PathParam ("view") view: String, @PathParam ("item") item: String, document: Elem): Unit = {
JmxAccessXhtml.createHead ("Views",
<ul/>
)
}

@Path ("{view}/item")
@GET
def getItem (@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam ("host") host: String, @PathParam ("view") view: String, @PathParam ("item") item: String) = {
JmxAccessXhtml.createHead ("Views",
<ul/>
);
}

}