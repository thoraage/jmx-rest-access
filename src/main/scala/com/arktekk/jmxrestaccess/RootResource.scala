package com.arktekk.jmxrestaccess

import javax.ws.rs.{PathParam, GET, Produces, Path}
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.{UriInfo, Context, MediaType}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@Path("rest/{host}")
@Produces(Array(MediaType.APPLICATION_XML))
class RootResource {
  @GET
  def get(@Context uriInfo: UriInfo, @Context request: HttpServletRequest, @PathParam("host") host: String) = {
    JmxAccessXhtml.createHead("Root",
      <ul>
        <li class="domains">
          {linkTo(uriInfo, host, classOf[DomainResource], "Domains")}
        </li>
        <li class="mbeans">
          {linkTo(uriInfo, host, classOf[MBeanResource], "MBeans")}
        </li>
        <li class="views">
          {linkTo(uriInfo, host, classOf[ViewResource], "Views")}
        </li>
      </ul>)
  }

  def linkTo(uriInfo: UriInfo, host: String, clazz: Class[_], name: String) =
    <a href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(clazz).build(host).toString}>
      {name}
    </a>

}