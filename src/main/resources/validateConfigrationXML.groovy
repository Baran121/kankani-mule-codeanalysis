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
	def dir = new File(arg1 + "/src/main/mule")
	fileList = dir.list()
	 
	List<error> errors = []
	for(item in fileList){
		def parser = new XmlParser()
		def project = parser.parse(arg1 + "/src/main/mule/" + item)
		project.'sub-flow'.each {
			thing->
			def name= "${thing.@name}"
		
			if(name.endsWithAny("-sub-flow")) {
			return ""
			}
			else {
				String filePath= arg1 + "/src/main/mule/" + item
				File myfile = new File(filePath)
				myfile.eachLine { line, lineNo->
				if(line.contains(name))
					errors.add([foldername:arg1, fileName:i, lineNumber:"$lineNo", errorDetails: "Incorrect "+ name +" name, Sub flow name format should end with '-sub-flow'", "priority": "Improvement"])
				}
			}
		}
	
	}
	return new JsonBuilder(errors).toPrettyString();
}