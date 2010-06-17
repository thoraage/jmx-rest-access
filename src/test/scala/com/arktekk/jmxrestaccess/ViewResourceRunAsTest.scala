package com.arktekk.jmxrestaccess

import org.specs.Specification
import org.junit.runner.RunWith
import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import javax.ws.rs.core.UriInfo
import java.lang.String
import xml.Elem

import XmlHelper._

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@RunWith(classOf[JUnitSuiteRunner])
class ViewResourceRunAsTest extends JUnit4(ViewResourceSpec)
object ViewResourceSpec extends Specification {
  "before any views are created we get only a empty list with a template" in {
    val viewResource = new ViewResource {
      override def getJmxHelper = error("Not implemented")
    }
    val uriInfo: UriInfo = TestHelper.getUriInfo
    val host: String = "anyhost:8080"
    val document: Elem = viewResource.getAll(uriInfo, null, host)
    val template = (TestHelper.getManagementElement(document) \\ "span" whereAt ("class", {_.text == "create-template"})).text
    template must_== "http://arktekk.no/rest/{host}/view"
  }

  /*"create new view and view items" in {
    val elem = JmxAccessXhtml.createHead("View", <a class="item" href="/rest/anyhost:8080/mbean/....1">Name1</a>)
    viewResource.create(uriInfo, null, host, "MyView", "Name1", elem)
    val elem2 = JmxAccessXhtml.createHead("View", <a class="item" href="/rest/anyhost:8080/mbean/....2">Name2</a>)
    viewResource.create(uriInfo, null, host, "MyView", "Name2", elem2)

  }
  
  "views can be listed" in {
  }*/

}
