package com.arktekk.jmxrestaccess

import jmx.AuthorisationParser
import org.specs.Specification
import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import org.junit.runner.RunWith

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

@RunWith(classOf[JUnitSuiteRunner])
class AuthorisationParserRunAsTest extends JUnit4(AuthorisationParserSpec)
object AuthorisationParserSpec extends Specification {
  "parser" should {
    "separate authorisation in valid Basic" in {
      AuthorisationParser.find("Basic dHVsbDp0dWxs") must_== ("tull", "tull")
    }

    "fail on non valid autorizations" in {
      AuthorisationParser.find("MoraDi dHVsbDp0dWxs") must throwA[RuntimeException]
    }
  }
}
