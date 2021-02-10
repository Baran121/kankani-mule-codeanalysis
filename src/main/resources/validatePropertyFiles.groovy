package main.resources;

import org.xml.sax.SAXException

import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlParser
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import main.resources.model.error


def String validatePropertyFileIsExists(def arg1) {


	def list = []
	def dir = new File(arg1 + "/src/main/resources")
	list= dir.list({d, f-> f ==~ /.*.yaml/ } as FilenameFilter)
	List<error> errors = []
	if(list.any {f-> f ==~ /.*dev*.yaml/  } == false)
		errors.add([foldername:arg1, fileName:"", lineNumber:"", errorDetails: "Dev property file is missing", priority: "Critical"])
	if(list.any {f-> f ==~ /.*uat*.yaml/  } == false)
		errors.add([foldername:arg1, fileName:"", lineNumber:"", errorDetails: "UAT property file is missing",priority: "Critical"])
	if(list.any {f-> f ==~ /.*prod*.yaml/  } == false)
		errors.add([foldername:arg1, fileName:"", lineNumber:"", errorDetails: "Prod property file is missing",priority: "Critical"])
		
		return new JsonBuilder(errors).toPrettyString();
}