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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EAccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.writer.LMFWriter;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * This class writes a LMF classes to XML Output Stream. 
 */
public class LMFXmlWriter extends LMFWriter{
	
	/*
	 * Private members
	 */
	private String dtdPath; // path of the dtd File	
	private TransformerHandler th; // Transform Handler
	
	private static Logger logger = Logger.getLogger(LMFXmlWriter.class.getName());

	/**
	 * Constructs a LMFXmlWriter, XML will be saved to file in outputPath
	 * @param outputPath
	 * @param dtdPath Path of the dtd-File
	 * @return LMFXmlWriter
	 * @throws FileNotFoundException if the writer can not to the specified outputPath
	 */
	public LMFXmlWriter(String outputPath, String dtdPath) throws FileNotFoundException {
		this(new FileOutputStream(outputPath), dtdPath);
	}
	
	/**
	 * Constructs a LMFXmlWriter, XML will be saved to OutputStream out
	 * @param out
	 * @param dtdPath
	 */
	public LMFXmlWriter(OutputStream out, String dtdPath) {
		this.dtdPath = dtdPath;
		th = getXMLTransformerHandler(out);
	}
	
	
	/**
	 * Writes lmfObject to XML without writing closing Tag for this Element
	 * @param lmfObject
	 * @throws LMFWriterException
	 */
	public void writeStartElement(Object lmfObject) throws LMFWriterException{		
		try{
			doTransform(lmfObject, false);
		}catch(Exception ex){
			throw new LMFWriterException(ex);
		}
	}
	
	/**
	 * Ends the element associated with lmfObject
	 * @param lmfObject
	 * @throws LMFWriterException
	 */
	public void writeEndElement(Object lmfObject) throws LMFWriterException{
		try{
			String elementName = lmfObject.getClass().getSimpleName();
			th.endElement("", "", elementName);
		}catch(Exception ex){
			throw new LMFWriterException(ex);
		}
	}
	/**
	 * Ends the Document
	 * @throws LMFWriterException
	 */
	public void writeEndDocument() throws LMFWriterException{
		try{
			th.endDocument();
		}catch(SAXException ex){
			throw new LMFWriterException(ex);
		}
	}
	
	/**
	 * Writes lmfObject to XML with closing tags
	 * @param lmfObject
	 * @throws LMFWriterException
	 */
	public void writeElement(Object lmfObject) throws LMFWriterException{
		//System.out.print("Transforimng to XML...");
		try{
			doTransform(lmfObject, true);
		}catch(Exception ex){
			throw new LMFWriterException(ex);
		}
		//System.out.println("done");
	}
	
	
	/**
	 * This method consumes a LMF Object and transforms it to XML. The method
	 * iterates over all fields of a class and searches for the {@link AccessType} annotations.
	 * Depending on the value of the annotation, the method reads the values of the objects
	 * fields by invoking a getter or by directly accessing the field.
	 * 
	 * @param lmfObject An LMF Object for which an Element should be created
	 * @param writeEndElement If TRUE the closing Tag for the XML-Element will be created
	 *  
	 * @throws IllegalAccessException when a direct access to a field of the class is for some reason not possible 
	 * @throws IllegalArgumentException when a direct access to a field of the class is for some reason not possible 
	 * @throws SAXException if writing to XML-file is for some reason not possible
	 */
	@SuppressWarnings("unchecked")
	private void doTransform(Object lmfObject, boolean writeEndElement) throws IllegalArgumentException, IllegalAccessException, SAXException{	
		
		Class<?> something = lmfObject.getClass();
		String elementName = something.getSimpleName();
				
		AttributesImpl atts = new AttributesImpl();
		List<Object> children = new ArrayList<Object>();
		
		// find all field, also the inherited ones 
		ArrayList<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(something.getDeclaredFields()));
		Class<?> superClass = something.getSuperclass();
		while(superClass != null){
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		
		// Iterating over all fields
		for(Field field : fields){
			String fieldName = field.getName().replace("_", "");
			VarType varType = field.getAnnotation(VarType.class);
			// No VarType-Annotation found for the field, then don't save to XML 
			if(varType == null)
				continue;
		
			EVarType type = varType.type();
			
			// VarType is NONE, don't save to XML
			if(type.equals(EVarType.NONE))
				continue;
			
			Object retObj = null;
			
			/*
			 * Determine how to access the variable
			 */
			AccessType accessType = field.getAnnotation(AccessType.class);
			if(accessType == null || accessType.equals(EAccessType.GETTER)){
				// access using a canonical getter
				
				// Get-Method for the field
				String getFuncName = "get"+fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				Method getMethod = null;
				try {
					getMethod = something.getDeclaredMethod(getFuncName);
					retObj = getMethod.invoke(lmfObject); // Run the Get-Method
				} catch (Exception e) {
					logger.log(Level.WARNING, "There was an error on accessing the method " + getFuncName + " . Falling back to field access");
					field.setAccessible(true);
					retObj = field.get(lmfObject);
				}
			}
			else{
				// Directly read the value of the field
				field.setAccessible(true);
				retObj = field.get(lmfObject);
			}
			
			if(retObj != null){
				if(type.equals(EVarType.ATTRIBUTE)){ // Save Attribute to the new element
					atts.addAttribute("", "", fieldName, "CDATA", retObj.toString());
				}else if(type.equals(EVarType.IDREF)){ // Save IDREFs as attribute of the new element
					atts.addAttribute("", "", fieldName, "CDATA", ((IHasID)retObj).getId());
				}else if(type.equals(EVarType.CHILD)){ // Transform children of the new element to XML
					children.add(retObj);
				}else if(type.equals(EVarType.CHILDREN) && writeEndElement){					
					for(Object obj : (Iterable<Object>)retObj){
						children.add(obj);
					}
				}else if(type.equals(EVarType.ATTRIBUTE_OPTIONAL)){
					atts.addAttribute("", "", fieldName, "CDATA", retObj.toString());
				}else  if(type.equals(EVarType.IDREFS)){
					String attrValue = "";					
					for(Object obj : (Iterable<Object>)retObj){
						attrValue += ((IHasID)obj).getId() + " ";
					}
					if(!attrValue.isEmpty())
						atts.addAttribute("", "", fieldName, "CDATA", attrValue.substring(0, attrValue.length()-1));
				}		
			}else { // Element is null, save only if it is a non-optional Attribute or IDREF
				if(type.equals(EVarType.ATTRIBUTE) || type.equals(EVarType.IDREF)){ // Save Attribute to the new element
					//atts.addAttribute("", "", fieldName, "CDATA", "NULL");
				}
			}						
		}
		
		
		// Save the current element and its children
		th.startElement("", "",elementName, atts);
		for(Object child : children){
			//System.out.println("CHILD: "+child.getClass().getSimpleName());
			doTransform(child, true);
		}
		if(writeEndElement)
			th.endElement("", "", elementName);
	}	
		
	/**
	 * Creates XML TransformerHandler
	 * @param xmlOutPath
	 * @param dtdPath
	 * @return
	 * @throws IOException
	 * @throws TransformerException
	 */
	public TransformerHandler getXMLTransformerHandler(OutputStream out) {
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
		TransformerHandler th = null;
		try {
			th = tf.newTransformerHandler();
		} catch (TransformerConfigurationException e) {
			logger.log(Level.SEVERE, "Error on initiating TransformerHandler");
			e.printStackTrace();
		}
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty(OutputKeys.METHOD, "xml");
		serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		if(dtdPath != null)
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdPath);
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		th.setResult(streamResult);	
		return th;
	}
}
