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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * This class writes a LMF class to XML Output Stream
 * @deprecated THIS CLASS WILL BE REMOVED SOON. USE MODULE persistence.transform INSTEAD!
 */
@Deprecated
class LMFXmlWriterImmediate {
	
	/*
	 * Private members
	 */
	private String dtdPath; // path of the dtd File	
	private TransformerHandler th; // Transform Handler

	/**
	 * Constructs a LMFXmlWriter
	 * @param dtdPath Path of the dtd-File
	 * @return LMFXmlWriter
	 * @throws ParserConfigurationException 
	 */
	public LMFXmlWriterImmediate(String dtdPath) throws ParserConfigurationException{
		this.dtdPath = dtdPath;
	}
	
	/**
	 * This method consumes a LMF Object and transforms it to XML
	 * @param lmfObject An LMF Object for which an Element should be created and appended to rooot
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SAXException 
	 */
	@SuppressWarnings("rawtypes")
	private void doTransform(Object lmfObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SAXException {	
		
		Class something = lmfObject.getClass();
		String elementName = something.getSimpleName();
		AttributesImpl atts = new AttributesImpl();
		List<Object> children = new ArrayList<Object>();
		boolean isLeaf =  true;
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
					atts.addAttribute("", "", fieldName, "CDATA", retObj.toString());							
				}else if(type.equals(EVarType.CHILD)){ // Transform children of the new element to XML
					children.add(retObj);
					isLeaf = false;
				}else if(type.equals(EVarType.CHILDREN)){					
					for(Object obj : (Iterable)retObj){
						children.add(obj);
					}
					isLeaf = false;
				}else if(type.equals(EVarType.IDREF)){ // Save IDREFs as attribute of the new element
					atts.addAttribute("", "", fieldName, "CDATA", ((IHasID)retObj).getId());
					isLeaf = false;
				}else if(type.equals(EVarType.IDREFS)){
					String attrValue = "";					
					for(Object obj : (Iterable)retObj){
						attrValue += ((IHasID)obj).getId() + " ";
					}
					if(!attrValue.isEmpty())
						atts.addAttribute("", "", fieldName, "CDATA", attrValue.substring(0, attrValue.length()-1));
					isLeaf = false;
				}
				
			}catch(NoSuchMethodException ex){	
			}			
		}
		if(isLeaf)
			System.out.println(elementName);
		// Save the current element and its children
		th.startElement("", "",elementName, atts);
		for(Object child : children){
			doTransform(child);
		}
		th.endElement("", "", elementName);
	}
	
	/**
	 * Transforms LMF-Hierarchy beginning with lmfObject ot XML and saves it to xmlOutPath
	 * @param lmfObject
	 * @param xmlOutPath
	 * @throws IllegalArgumentException
	 * @throws DOMException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	public void transformToXml(Object lmfObject, String xmlOutPath) throws IllegalArgumentException, DOMException, IllegalAccessException, InvocationTargetException, IOException, TransformerException, SAXException{
		FileOutputStream outStream = new FileOutputStream(new File(xmlOutPath));
		transformToXml(lmfObject, outStream);
	}
	
	/**
	 * Transforms LMF-Hierarchy beginning with lmfObject ot XML and saves it to outStream
	 * @param lmfObject
	 * @param outStream
	 * @throws IllegalArgumentException
	 * @throws DOMException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	public void transformToXml(Object lmfObject, OutputStream outStream) throws IllegalArgumentException, DOMException, IllegalAccessException, InvocationTargetException, IOException, TransformerException, SAXException{		
		th = getXMLTransformerHandler(outStream);
		th.startDocument();
		doTransform(lmfObject);
		th.endDocument();
	}	
	
	
	/**
	 * Creates XML TransformerHandler
	 * @param xmlOutPath
	 * @param dtdPath
	 * @return
	 * @throws IOException
	 * @throws TransformerException
	 */
	public TransformerHandler getXMLTransformerHandler(OutputStream out) throws IOException, TransformerException{
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
		TransformerHandler th = tf.newTransformerHandler();
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty(OutputKeys.METHOD, "xml");
		serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdPath);
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		th.setResult(streamResult);	
		return th;
	}
}
