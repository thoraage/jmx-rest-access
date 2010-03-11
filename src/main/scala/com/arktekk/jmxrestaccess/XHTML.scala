package com.arktekk.jmxrestaccess

import xml.Elem

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object XHTML {
  def createHead(title: String, content: Elem): Elem = {
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
