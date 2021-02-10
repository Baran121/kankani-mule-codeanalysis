package main.resources;

import org.xml.sax.SAXException

import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlParser
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource



 

public class groovyHelpers
{
	def boolean whichLayer(def arg1)
	{
		if(arg1.toString().contains("exp"))
			return "exp";
		else if(arg1.toString().contains("proc"))
			return "exp";
		else if(arg1.toString().contains("sys"))
			return "sys";
		else return "unknown"
				
	}
	
	def boolean isAPI(def arg1)	
	{
		return false;
	}
	
	
	def String addAttributeToAllElements(String filePath)
	{
		 
		// create transformer
		def transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader('<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><xsl:template match="*"><xsl:copy><xsl:copy-of select="@*"/><xsl:attribute name="lineNumber"> <xsl:value-of select = "position()" /></xsl:attribute><xsl:apply-templates/></xsl:copy></xsl:template></xsl:stylesheet>')))
		 
		// Load xml
		def xml = new File(filePath).getText()
		def output = new StringWriter()
		
		 
		// perform transformation
		transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(output))
		
		return output.toString()
		
	}
}