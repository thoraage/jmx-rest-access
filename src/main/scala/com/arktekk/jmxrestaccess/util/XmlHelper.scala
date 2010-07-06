package com.arktekk.jmxrestaccess.util

import xml.{Node, NodeSeq}

/**
 * @author Thor Åge Eldby (thoraageeldby@gmail.com)
 */

object XmlHelper {
  class AttributeWrapper(nodeSeq: NodeSeq) {
    def whereAt(attributeName: String, condition: Node => Boolean): NodeSeq = {
      nodeSeq filter {
        node: Node =>
          val attributeOption = node.attribute(attributeName)
          attributeOption != None && condition(attributeOption.get.first)
      }
    }
  }

  implicit def nodeseq2attributewrapper(nodeSeq: NodeSeq) = new AttributeWrapper(nodeSeq)

}