package com.arktekk.jmxrestaccess

import org.junit.runner.RunWith
import org.specs.runner.{JUnit4, JUnitSuiteRunner}
import org.specs.Specification
import javax.management.MBeanServerConnection
import org.specs.mock.Mockito
import xml.NodeSeq

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */
@RunWith(classOf[JUnitSuiteRunner])
class DomainResourceRunAsTest extends JUnit4(DomainResourceSpec)
object DomainResourceSpec extends Specification with Mockito {
  val host = "anyhost:8080"

  val domainResource = new DomainResource {
    def jmxHelper = {
      val helper = mock[JMXHelper]
      val connection = mock[MBeanServerConnection]
      doReturn(Array("Domain1").asInstanceOf[scala.runtime.BoxedAnyArray].unbox(classOf[String])).when(connection).getDomains()
      doReturn(connection).when(helper).getMBeanServerConnection(null, host)
      helper
    }
  }

  "root resource" should {

    "return list of resources" in {
      val listElements: NodeSeq = domainResource.getAll(TestHelper.getUriInfo, null, host) \\ "html" \\ "body" \\ "ul" \\ "li"
      listElements(0).text.trim must_== "Domain1"
      (listElements(0) \\ "@href").text must_== "http://arktekk.no/rest/anyhost:8080/mbeans?domainName=Domain1"
    }

  }

}
