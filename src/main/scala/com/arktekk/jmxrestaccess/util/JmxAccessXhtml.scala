package com.arktekk.jmxrestaccess.util

import xml.{NodeSeq, Elem}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object JmxAccessXhtml {
  def createHead(title: String, content: NodeSeq): Elem = {
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
      <head>
        <title>
          {title}
        </title>
      </head>
      <body>
        <h1>
          {title}
        </h1>
        <span class="management">
          {content}
        </span>
      </body>
    </html>
  }
}