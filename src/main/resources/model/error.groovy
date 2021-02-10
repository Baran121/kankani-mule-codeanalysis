package main.resources.model

import org.xml.sax.SAXException

import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlParser
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class error {
	def foldername
	def fileName
	def errorDetails
	def lineNumber
	def priority
}