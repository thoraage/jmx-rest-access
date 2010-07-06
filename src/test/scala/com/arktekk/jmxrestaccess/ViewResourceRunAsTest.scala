package com.arktekk.jmxrestaccess

import org.specs.Specification
import org.junit.runner.RunWith
import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import javax.ws.rs.core.UriInfo
import java.lang.String
import util.XmlHelper._
import xml.Elem
import util.FileHelper._
import org.specs.mock.Mockito
import javax.management.{ObjectName, MBeanServerConnection}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@RunWith(classOf[JUnitSuiteRunner])
class ViewResourceRunAsTest extends JUnit4(ViewResourceSpec)
object ViewResourceSpec extends Specification with Mockito {
  val repositoryDirectory = TestHelper.getTargetDir(this.getClass) /! "junittestrepository"
  val host: String = "anyhost:8080"
  val uriInfo: UriInfo = TestHelper.getUriInfo

  val viewResource = new ViewResource {
    override def getJmxHelper = {
      val jmxHelper = mock[JMXHelper]
      val connection = mock[MBeanServerConnection]
      //doReturn(connection).when(jmxHelper).getMBeanServerConnection(null, host)
      doReturn("value1").when(connection).getAttribute(new ObjectName("d:k=v"), "attrName1")
      doReturn("value2").when(connection).getAttribute(new ObjectName("d:k=v"), "attrName2")
      jmxHelper
    }

    override def getRepositoryDirectory = repositoryDirectory
  }

  def views = {
    val document: Elem = viewResource.getAll(uriInfo, null, host)
    val elements = TestHelper.getManagementElement(document)
    val template = (elements \\ "span" whereAt ("class", {_.text == "create-template"})).text.trim
    template must_== "http://arktekk.no/rest/{host}/views/{view}/items/{item}"
    val views = (elements \\ "li" whereAt ("class", {_.text == "views"})) \ "_"
    views
  }

  try {
    "before any views are created we get only a empty list with a template" in {
      views.size must_== 0

      "create new view and view items" in {
        val elem1 = <span>
            <a href="/rest/anyhost:8080/rest/host/mbeans/d:k=v/attributes/attrName1"/>
        </span>
        val viewName = "MyView"
        viewResource.createItem(uriInfo, null, host, viewName, "Name1", elem1)
        val elem2 = <span>
            <a href="/rest/anyhost:8080/rest/host/mbeans/d:k=v/attributes/attrName2"/>
        </span>
        viewResource.createItem(uriInfo, null, host, viewName, "Name2", elem2)

        "views can be listed" in {
          views.size must_== 1
          (views \ "@href").text must_== "http://arktekk.no/rest/anyhost:8080/views/MyView/state"
        }

        "view item states can be retrieved" in {
          val items = TestHelper.getManagementElement(viewResource.getView(uriInfo, null, host, viewName))
          (items \ "span").whereAt("id", _ == "Name1").text.trim must_== "value1"
          (items \ "span").whereAt("id", _ == "Name2").text.trim must_== "value2"
        }
      }
    }
  } finally {
    repositoryDirectory.deleteAll
  }

}
