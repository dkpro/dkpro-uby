/*******************************************************************************
 * Copyright 2016
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

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class creates a DOM from LMF and writes it to XML
 * @deprecated THIS CLASS WILL BE REMOVED SOON. USE MODULE persistence.transform INSTEAD! 
 */
@Deprecated
class LMFXmlWriterDOM {
	
	/*
	 * Private members
	 */
	private DocumentBuilderFactory dbf; // factory API
	private DocumentBuilder docBuilder; // doc builder
	private Document doc; // xml-Document to be produced
	private String dtdPath; // path of the dtd File
	private Transformer transformer; // transformer	

	/**
	 * Constructs a LMFXmlWriter
	 * @param dtdPath Path of the dtd-File
	 * @return LMFXmlWriter
	 * @throws ParserConfigurationException 
	 */
	public LMFXmlWriterDOM(String dtdPath) throws ParserConfigurationException{
		this.dtdPath = dtdPath;
		this.dbf = DocumentBuilderFactory.newInstance();
		this.docBuilder = dbf.newDocumentBuilder();
		this.doc = this.docBuilder.newDocument();
		setUpTransformer();
	}
	
	/**
	 * This method consumes a LMF Object and appends it's
	 * XML-Representation to the consumed Element
	 * @param lmfObject An LMF Object for which an Element should be created and appended to root
	 * @param Root Element
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("rawtypes")
	public void transformToXml(Object lmfObject, Element root) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {	
		
		Class something = lmfObject.getClass();
		// Setting the Name of the new Element
		Element newElement = doc.createElement(something.getSimpleName());
		// Appending the new Element to the root
		root.appendChild(newElement);
		// Iterating over all fields
		for(Field field : something.getDeclaredFields()){
			String fieldName = field.getName().replace("_", "");
			VarType varType = field.getAnnotation(VarType.class);
			// No VarType-Annotation found for the field, then don't save to XML 
			if(varType == null)
				continue;
		
			EVarType type = varType.type();
			// VarType is NONE, don't save to XML
			if(type.equals(EVarType.NONE))
				continue;
			
			// Get-Method for the field
			String getFuncName = "get"+fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
			
			try{
				@SuppressWarnings("unchecked")
				Method getMethod = something.getDeclaredMethod(getFuncName);				
				Object retObj = getMethod.invoke(lmfObject); // Run the Get-Method
				if(retObj == null){
					continue;
				}
				else if(type.equals(EVarType.ATTRIBUTE)){ // Save Attribute to the new element
					newElement.setAttribute(fieldName, retObj.toString());					
				}else if(type.equals(EVarType.CHILD)){ // Transform children of the new element to XML
					transformToXml(retObj, newElement);
				}else if(type.equals(EVarType.CHILDREN)){					
					for(Object obj : (Iterable)retObj){
						transformToXml(obj, newElement);
					}
				}else if(type.equals(EVarType.IDREF)){ // Save IDREFs as attribute of the new element
					newElement.setAttribute(fieldName, ((IHasID)retObj).getId());
				}else if(type.equals(EVarType.IDREFS)){
					String attrValue = "";					
					for(Object obj : (Iterable)retObj){
						attrValue += ((IHasID)obj).getId() + " ";
					}
					if(!attrValue.isEmpty())
						newElement.setAttribute(fieldName, attrValue.substring(0, attrValue.length()-1));
				}
				
			}catch(NoSuchMethodException ex){
				
			}			
		}
	}
	
	/**
	 * Transforms lmfObject do XML-Document
	 * @param lmfObject
	 * @throws IllegalArgumentException
	 * @throws DOMException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void transformToXml(Object lmfObject) throws IllegalArgumentException, DOMException, IllegalAccessException, InvocationTargetException{
		System.out.print("Transforming to xml...");
		Element root = doc.createElement("root");
		doc.appendChild(root);
		transformToXml(lmfObject, root);
		System.out.println("done");
	}
	
	
	/**
	 * Sets up a transformer
	 * @return transformer
	 */
	private void setUpTransformer(){
		 //set up a transformer
	    TransformerFactory transfac = TransformerFactory.newInstance();
	    transformer = null;
		try {
			transformer = transfac.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.err.println("Transformer not well configured!");
			e.printStackTrace();
		}
	    // defining parameters
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
	    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdPath);
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	}

	/**
	 * Prints the xml-document
	 * @throws TransformerException 
	 */
	public void printXML() throws TransformerException{
		//create string from xml tree
	    StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw);
	    DOMSource source = new DOMSource(doc);
	    setUpTransformer();
	    transformer.transform(source, result);
	    String xmlString = sw.toString();
	    System.out.println(xmlString);
	}

	/**
	 * Saves the the xml-document into a File
	 * @param path path of the file
	 * @throws TransformerException 
	 */
	public void saveXML(String path) throws TransformerException{
		//create string from xml tree
	    File file = new File(path);
	    StreamResult result = new StreamResult(file);
	    DOMSource source = new DOMSource(doc);
	    System.out.println("Saving xml...");
	    setUpTransformer();
	    transformer.transform(source, result);
	    System.out.println("xml saved to: " + path);
	}

	/**
	 * Returns DOM-Document
	 * @return
	 */
	public Document getDocument(){
		return doc;
	}
}
