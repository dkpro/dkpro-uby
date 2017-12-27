/*******************************************************************************
 * Copyright 2017
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
package de.tudarmstadt.ukp.lmf.transform;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.AccessType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.transform.UBYLMFClassMetadata.UBYLMFFieldMetadata;

/**
 * Abstract base class for all resource transformations involving the 
 * generation of UBY-XML. The class allows for creating XML tags from
 * UBY-LMF model objects.
 * @author Christian M. Meyer
 */
public abstract class UBYXMLTransformer extends UBYTransformer {

	protected TransformerHandler th;
	
	/** Creates XML TransformerHandler. */
	protected void writeStartDocument(final OutputStream outputStream,
			final String dtdPath) throws SAXException {
		try {
			SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
			th = tf.newTransformerHandler();
			Transformer serializer = th.getTransformer();
			serializer.setOutputProperty(OutputKeys.METHOD, "xml");
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			if (dtdPath != null)
				serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdPath);
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			th.setResult(new StreamResult(outputStream));
		} catch (TransformerConfigurationException e) {
			throw new SAXException(e);
		}
	}
	
	/** Writes lmfObject to XML without writing closing Tag for this Element.
	 *  @throws SAXException in case of any errors. */
	protected void writeStartElement(final Object lmfObject) throws SAXException {		
		doWriteElement(lmfObject, false);
	}

	/** Writes lmfObject to XML with closing tags. 
	 *  @throws SAXException in case of any errors. */
	protected void writeElement(Object lmfObject) throws SAXException {
		doWriteElement(lmfObject, true);
	}

	/** Appends an end tag for the given object.
	 *  @throws SAXException in case of any errors. */
	protected void writeEndElement(Object lmfObject) throws SAXException {
		String elementName = lmfObject.getClass().getSimpleName();
		th.endElement("", "", elementName);
	}

	/**
	 * This method consumes a LMF Object and transforms it to XML. The method
	 * iterates over all fields of a class and searches for the {@link AccessType} annotations.
	 * Depending on the value of the annotation, the method reads the values of the objects
	 * fields by invoking a getter or by directly accessing the field.
	 * 
	 * @param lmfObject An LMF Object for which an Element should be created
	 * @param closeTag If TRUE the closing Tag for the XML-Element will be created
	 * 
	 * @throws SAXException if writing to XML-file is for some reason not possible
	 */
	@SuppressWarnings("unchecked")
	protected void doWriteElement(Object lmfObject, boolean closeTag) 
			throws SAXException {
		Class<?> lmfClass = lmfObject.getClass();
		String elementName = lmfClass.getSimpleName();
		int hibernateSuffixIdx = elementName.indexOf("_$$");
		if (hibernateSuffixIdx > 0)
			elementName = elementName.substring(0, hibernateSuffixIdx);
		AttributesImpl atts = new AttributesImpl();
		List<Object> children = new ArrayList<Object>();
		
		UBYLMFClassMetadata classMeta = getClassMetadata(lmfClass);			
		for (UBYLMFFieldMetadata fieldMeta : classMeta.getFields()) {
			EVarType varType = fieldMeta.getVarType();
			if (varType == EVarType.NONE)
				continue;

			String xmlFieldName = fieldMeta.getName().replace("_", "");
			Method getter = fieldMeta.getGetter();
			Object retObj;
			try {
				retObj = getter.invoke(lmfObject);
			} catch (IllegalAccessException e) {
				throw new SAXException(e);
			} catch (InvocationTargetException e) {
				throw new SAXException(e);
			}
			
			if (retObj != null) {
				switch (fieldMeta.getVarType()) {
					case ATTRIBUTE:
					case ATTRIBUTE_OPTIONAL:
						atts.addAttribute("", "", xmlFieldName, "CDATA", retObj.toString());
						break;
					case CHILD:
						// Transform children of the new element to XML
						children.add(retObj);
						break;
					case CHILDREN:
						if (closeTag)
							for (Object obj : (Iterable<Object>) retObj)
								children.add(obj);
						break;
					case IDREF:
						// Save IDREFs as attribute of the new element
						atts.addAttribute("", "", xmlFieldName, "CDATA", ((IHasID) retObj).getId());
						break;
					case IDREFS:
						StringBuilder attrValue = new StringBuilder();					
						for (Object obj : (Iterable<Object>) retObj)
							attrValue.append(attrValue.length() > 0 ? " " : "")
									.append(((IHasID) obj).getId());
						if (attrValue.length() > 0)
							atts.addAttribute("", "", xmlFieldName, "CDATA", attrValue.toString());
						break;
					case NONE:
						break;
				}
			}						
		}
		
		// Save the current element and its children
		th.startElement("", "", elementName, atts);
		for (Object child : children)
			doWriteElement(child, true);
		if (closeTag)
			th.endElement("", "", elementName);
	}	

	/** Ends the Document. */
	protected void writeEndDocument() throws SAXException {
		th.endDocument();
	}

}
