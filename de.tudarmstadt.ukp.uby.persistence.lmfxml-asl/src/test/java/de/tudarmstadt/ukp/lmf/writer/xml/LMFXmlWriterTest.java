/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.lmf.writer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * Tests methods of {@link LMFXmlWriter}.
 * 
 * Tests are made for UBY-LMF DTD version 0.2.0
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
public class LMFXmlWriterTest {
	
	private static LMFXmlWriter lmfXmlWriter;
	
	private static LexicalResource lexicalResource; 
	
	private static String dtdPath;
	private static String outputPath;
	
	private static final String lexicalResource_dtdVersion = "0.2.0";
	private static final String lexicalResource_name = "lexicalResource_name";
	
	private static final String globalInformation_label = "globalInformation_label";

	/**
	 * Creates a UBY-LMF structure by initializing every child and every field
	 * of all UBY-LMF classes.<br>
	 * All UBY-LMF classes containing a list of children have at least
	 * one fully initialized child instance.
	 * 
	 * @throws IOException if files needed for this {@link LMFXmlWriter} test to run
	 * could not be created
	 * @throws LMFWriterException if an error on writing the xml file needed for this
	 * test to rung occured
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException, LMFWriterException {
		
		String outputDirectory = ("target/test-output/"+LMFXmlWriterTest.class.getSimpleName());
		
		
		
		// create folder structure
		File file = new File(outputDirectory);
		file.mkdirs();
		file.setReadable(true);
		file.setWritable(true);
		outputPath = outputDirectory+"/test.xml";
		
		// create a dummy dtd
		dtdPath = outputDirectory+"/test.dtd";
		File dtd = new File(dtdPath);
		dtd.createNewFile();
		
		
		
		
		lexicalResource = new LexicalResource();
		lexicalResource.setDtdVersion(lexicalResource_dtdVersion);
		lexicalResource.setName(lexicalResource_name);
		
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel(globalInformation_label);
		lexicalResource.setGlobalInformation(globalInformation);
		
		lmfXmlWriter = new LMFXmlWriter(outputPath, dtd.getAbsolutePath());
		
		lmfXmlWriter.writeElement(lexicalResource);
		
		lmfXmlWriter.writeEndDocument();
	}
	
	/**
	 * Tests the content of the xml document
	 */
	@Test
	public void testXMLContent(){
		// Read the document
		File xmlFile = new File(outputPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			fail(e.toString());
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(xmlFile);
		} catch (SAXException e) {
			fail(e.toString());
		} catch (IOException e) {
			fail(e.toString());
		}
		
		checkLexicalResource(doc.getDocumentElement());
	}

	/**
	 * Test the values of attributes and children of a {@link LexicalResource} node in the
	 * consumed lexical resource {@link Element}.
	 * 
	 * @param lexicalResource the node of the lexical resource
	 */
	private void checkLexicalResource(Element lexicalResource) {
		
		assertEquals(lexicalResource_dtdVersion, lexicalResource.getAttribute("dtdVersion"));
		assertEquals(lexicalResource_name, lexicalResource.getAttribute("name"));
		
		NodeList nl = lexicalResource.getElementsByTagName("GlobalInformation");
		assertEquals("lexical resource should only have one GlobalInformation instance", 1, nl.getLength());
		checkGlobalInformation((Element)nl.item(0));
		
	}

	/**
	 * Test the values of attributes and children of a {@link GlobalInformation} node in the
	 * consumed global information {@link Node}.
	 * 
	 * @param lexicalResource the node of the lexical resource
	 */
	private void checkGlobalInformation(Element globalInformation) {
		assertEquals(globalInformation_label, globalInformation.getAttribute("label"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// TODO
	}

}
