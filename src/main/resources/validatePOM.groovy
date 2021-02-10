package main.resources;

import org.xml.sax.SAXException

import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlParser
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import main.resources.model.error
 

def String validateSubflowNaming(def arg1) {
	
	def fileList = []
	List<error> errors=[] 
	def isLoggginExists = false
	def isExceptionExists = false
	
	def project = new XmlParser().parse(arg1 + "/pom.xml")
	project.'dependencies'.'dependency'.each {
		thing->
		def name= "${thing.artifactId.text()}"
		if(name.endsWithAny("common-logging-framework")) {
			isLoggginExists = true;
		}
		if(name.endsWithAny("Common-Exception-framework")) {
			isExceptionExists = true;
		}
	
	}
	if(isLoggginExists)
	{
		errors.add([foldername:arg1, fileName:"pom.xml", lineNumber:"", errorDetails: "Missing - Common logging framework", priority: "Critical"])
	}
	
	if(isExceptionExists)
	{
		errors.add([foldername:arg1, fileName:"pom.xml", lineNumber:"", errorDetails: "Missing - Common Exception framework", priority: "Critical"])
	}
	return new JsonBuilder(errors).toPrettyString();
}

def String validateMuleRuntime(def arg1) {
	List<error> errors=[]
def filePath= arg1 + "/pom.xml"
def parser = new XmlParser()
def project = parser.parse(filePath)
def errorLine=[]
def version = project.properties.'app.runtime'.text()
	if(version>= "4.3.0") {
		return ""
	}
	else {
		File myfile = new File(filePath)
		myfile.eachLine { line, lineNo->
		if(line.contains("app.runtime"))  {
		errorLine.add("$lineNo")
			}
		}
	}

	errors.add([foldername:arg1, fileName:"pom.xml", lineNumber:errorLine[0], errorDetails: "Mule runtime is not 4.3.0 or above",priority: "Warning"])
	return new JsonBuilder(errors).toPrettyString();
}

def String validateAutoDiscovery(def arg1) {
	List<error> errors=[]
	if(arg1.find("-api")) {
	def lines = new File(arg1 + "/pom.xml").readLines()
	def results = lines.findAll( { it.contains('raml') })
	if(results.isEmpty()==false) {
	def list = []
	def dir = new File(arg1 + "/src/main/mule")
	list= dir.list()
	if(list.contains("global.xml")) {
	def autoLines = new File(arg1 + "/src/main/mule/global.xml").readLines()
	def autoResults = autoLines.findAll( { it.contains('api-gateway:autodiscovery') })
	if(autoResults.isEmpty()==false)
		return ""
	else
		errors.add([foldername:arg1, fileName:"global.xml",lineNumber:"",errorDetails: "Autodiscovery not found",priority: "Critical"])
		return new JsonBuilder(errors).toPrettyString();
	
	}else {
		errors.add([foldername:arg1, fileName:"global.xml",lineNumber:"",errorDetails: "global.xml not found", priority: "Critical"])
		return new JsonBuilder(errors).toPrettyString();
	}
	
	
	}else {
		errors.add([foldername:arg1, fileName:"global.xml",lineNumber:"",errorDetails: "Raml not found", priority: "Critical"])
		return new JsonBuilder(errors).toPrettyString();
		 
	}
	}else if(arg1.find("-app")) {
		return ""
	}else {
		
	}
	errors.add([foldername:arg1, fileName:"global.xml",lineNumber:"",errorDetails: "Project is neither API nor Application",priority: "Warning"])
	return new JsonBuilder(errors).toPrettyString();
	 
	
	}