package main.resources;

import org.xml.sax.SAXException

import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlParser
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import main.resources.model.error
 


def String validateIsGlobalExists(def arg1) {
	List<error> errors=[]
	def dir = new File(arg1 + "/src/main/mule")
	list= dir.list
	if(list.contains("global.xml")) {
		return ""
	}else {
		errors.add([foldername:arg1, fileName:"global.xml",lineNumber:"",errorDetails: "global.xml not found", priority: "Critical"])
		return new JsonBuilder(errors).toPrettyString();
		}
	
}
	

def String validateUnexportedValues(def arg1) {
	def list=[]
	def hardCodeLine=[]
	List<error> errors=[]

	String filePath= arg1 + "/src/main/mule/global.xml"
	File myfile = new File(filePath)
	myfile.eachLine { line, lineNo->
		if(line.contains("-connection")) {
		list.add(line)
		}
		if(line.contains(":connection")) {
			list.add(line)
			}
	}
	
	for(item in list) {
		def equ= item.count("=")
		def sym= item.count('="${')
			if(equ!=sym)
			hardCodeLine.add(item)		
	}
	for(item in hardCodeLine) {
		File file = new File(filePath)
		file.eachLine { line, lineNo->
			if(line.contains(item)) {
				errors.add([foldername:arg1, fileName:"global.xml", lineNumber:"$lineNo", errorDetails: "Found hardcoded value", priority: "Critical"])
			}
		}
	}
	return new JsonBuilder(errors).toPrettyString();
}