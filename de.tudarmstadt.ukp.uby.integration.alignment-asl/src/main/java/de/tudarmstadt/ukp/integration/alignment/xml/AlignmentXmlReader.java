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
package de.tudarmstadt.ukp.integration.alignment.xml;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

/**
 * Read relevant information sources from alignment.xml file
 */
public class AlignmentXmlReader implements Closeable {

	XMLEventReader xmlEventReader;
	Unmarshaller unmarshaller;
	InputStream fs;
	
	public AlignmentXmlReader(File inputLocation) throws IOException {
		fs = null; 
		try {
			fs = new FileInputStream(inputLocation);
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			xmlEventReader = xmlInputFactory.createXMLEventReader(fs);
			
			JAXBContext context;
			context = JAXBContext.newInstance(XmlMeta.class, Alignments.class); 
			
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException | XMLStreamException e1) { 
			throw new IOException(e1);
		} 

	}

	 public XmlMeta readMetaData() throws IOException {
    	 XmlMeta meta = null;
		 try {
			XMLEvent e = xmlEventReader.peek();
			while (e!=null && !isStartElement(e,"metadata")){
				xmlEventReader.next();
				e = xmlEventReader.peek();
			}
			if (e!=null){
				meta = unmarshaller.unmarshal(xmlEventReader, XmlMeta.class).getValue();
			} 
		} catch (XMLStreamException e1) {
			throw new IOException(e1);
		} catch (JAXBException e1) {
			throw new IOException(e1);
		}
	return meta;
	 }
	 
     public Alignments readAlignments() throws IOException {
    	 Alignments alignments = null;
    		 try {
    			XMLEvent e = xmlEventReader.peek();
    			while (e!=null && !isStartElement(e,"alignments")){
    				xmlEventReader.next();
    				e = xmlEventReader.peek();
    			}
    			if (e!=null){
    				alignments = unmarshaller.unmarshal(xmlEventReader, Alignments.class).getValue();
    			} 
			} catch (XMLStreamException e1) {
				e1.printStackTrace();
				throw new IOException(e1);
			} catch (JAXBException e1) {
				e1.printStackTrace();
				throw new IOException(e1);
			}
    	return alignments;
     }	 
     
    public static boolean isStartElement(XMLEvent aEvent, String aElement)
    {
        return aEvent.isStartElement()
                && ((StartElement) aEvent).getName().getLocalPart().equals(aElement);
    }
    
	@Override
	public void close() throws IOException {
			closeQuietly(fs);
	}
}
