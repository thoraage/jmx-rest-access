package com.arktekk.jmxrestaccess

import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{GetRequest, Req}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object TestResource extends RestHelper {
  serve {
    case Req("api" :: doh, t, GetRequest) => <b>
      {doh}
      :
      {t}
    </b>
    case Req("rapi" :: doh, _, GetRequest) => <b>oh
      {doh}
    </b>
  }

}