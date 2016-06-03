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
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

/**
 * 
 * Writer for alignment xml format
 *
 */
public class AlignmentXmlWriter implements Closeable {

	private static final String RESOURCE_ALIGNMENT = "resourceAlignment";

	XMLEventWriter xmlEventWriter;
	XMLEventFactory xmlef; 
	OutputStream out;
	Marshaller marshaller;
	
	public AlignmentXmlWriter(OutputStream out) throws IOException {
		
		try {
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			xmlEventWriter = 
			        xmlOutputFactory.createXMLEventWriter(out);
			
			JAXBContext  context = JAXBContext.newInstance(XmlMeta.class, Alignments.class); //Source.class

			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE); // no document level events
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			xmlef = XMLEventFactory.newInstance();
			xmlEventWriter.add(xmlef.createStartDocument());
			xmlEventWriter.add(xmlef.createStartElement("", "", RESOURCE_ALIGNMENT));
			
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
        
	}

	public void writeMetaData(XmlMeta meta) throws IOException {
        try {
			marshaller.marshal(new JAXBElement<XmlMeta>(new QName("metadata"),
			        XmlMeta.class, meta), xmlEventWriter);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
		
	}

	public void writeAlignments(Alignments alignments) throws IOException {
        try {
			marshaller.marshal(new JAXBElement<Alignments>(new QName("alignments"),
			    Alignments.class, alignments), xmlEventWriter);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
		
	}
	
//	public void write(List<Source> sources) throws IOException, XMLStreamException {
//		for (Source source:sources){
//			write(source);
//		}
//	}
//	
//	private void write(Source source) throws IOException, XMLStreamException {
//        try {
//			marshaller.marshal(new JAXBElement<Source>(new QName("source"),
//			        Source.class, source), xmlEventWriter);
//		} catch (JAXBException e) {
//			throw new IOException(e);
//		}
//	}
	
	@Override
	public void close() throws IOException {
		try {
			xmlEventWriter.add(xmlef.createEndElement("", "", RESOURCE_ALIGNMENT));
			xmlEventWriter.add(xmlef.createEndDocument());
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		finally {
			closeQuietly(out);
		}
	}

	
	
}
