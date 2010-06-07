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
    XHTML.createHead("Root",
      <ul>
        <li>
          <a class="domains" href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[DomainResource]).build(host).toString}>
            Domains
          </a>
        </li>
        <li>
          <a class="mbeans" href={UriBuilderHelper.cloneBaseUriBuilder(uriInfo).path(classOf[MBeanResource]).build(host).toString}>
            MBeans
          </a>
        </li>
      </ul>)
  }
}