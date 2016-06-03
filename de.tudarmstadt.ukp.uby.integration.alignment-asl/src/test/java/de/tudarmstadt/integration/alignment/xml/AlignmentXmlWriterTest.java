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
package de.tudarmstadt.integration.alignment.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlReader;
import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlWriter;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

public class AlignmentXmlWriterTest {

	@Test
	@Ignore
	public void readerWriterTest() throws Exception{
		//System.err.println("testing: " + JAXBContext.newInstance(AlignmentXmlReader.class).getClass().getName());

		// read xml information
		AlignmentXmlReader reader = new AlignmentXmlReader(new File("src/test/resources/ResourceAlignmentDraft_v1.2nn.xml"));

		XmlMeta m = reader.readMetaData();
		Alignments alignments = reader.readAlignments();				
		reader.close(); 
		
		// check reading 
		assertEquals(m.date.trim(),"2014-06-12"); //trim formatting
		assertEquals(m.rights.trim(),"Released into the public domain by the creator.");
		assertEquals(m.sourceResource.id,"WordNet_2.1");
		assertEquals(m.targetResource.id,"GermaNet_1.0");
		assertEquals(m.targetResource.identifiertype.trim(),"lexical unit ID");
		assertEquals(m.scoretypes.iterator().next().type, "manual");
		assertEquals(alignments.source.size(), 1);
		Source s = alignments.source.iterator().next();
		assertEquals(s.targets.size(),2);
		assertEquals(s.targets.get(1).ref,"12443");
		assertEquals(s.targets.get(1).scores.get(0).src,"headferret");
		assertEquals(s.targets.get(0).decision.src,"ferretdecision");
		assertEquals(s.targets.get(0).decision.value,true);
		
		// write output
		AlignmentXmlWriter writer = new AlignmentXmlWriter(new FileOutputStream("target/testRes_v1.2nn.xml"));
		writer.writeMetaData(m);
		writer.writeAlignments(alignments);;
		writer.close();
		
		// compare input and output 

	}
	
	@Test
	@Ignore
	public void readerWriterTestSub() throws Exception{
		//System.err.println("testing: " + JAXBContext.newInstance(AlignmentXmlReader.class).getClass().getName());

		// read xml information
		AlignmentXmlReader reader = new AlignmentXmlReader(new File("src/test/resources/ResourceAlignmentDraft_v1.2nn_predicate.xml"));

		XmlMeta m = reader.readMetaData();
		Alignments alignments = reader.readAlignments();				
		reader.close(); 
		
		// check reading 
		System.err.println(m.date);
		assertEquals(m.date.trim(),"2014-06-12"); //trim formatting
		assertEquals(m.rights.trim(),"Released into the public domain by the creator.");
		assertEquals(m.sourceResource.id,"WordNet_2.1");
		assertEquals(m.targetResource.id,"GermaNet_1.0");
		assertEquals(m.targetResource.identifiertype.trim(),"lexical unit ID");
		assertEquals(m.subSource.identifiertype,"semantic role");
		assertEquals(m.subTarget.identifiertype,"semantic role");

		assertEquals(m.scoretypes.iterator().next().type, "manual");
		assertEquals(alignments.source.size(), 1);
		Source s = alignments.source.iterator().next();
		assertEquals(s.targets.size(),2);
		assertEquals(s.targets.get(1).ref,"12443");
		assertEquals(s.targets.get(1).scores.get(0).src,"headferret");
		assertEquals(s.targets.get(0).decision.src,"ferretdecision");
		assertEquals(s.targets.get(0).decision.value,true);
		assertEquals(s.targets.get(1).subsources.get(0).ref, "role1");
		assertEquals(s.targets.get(1).subsources.get(0).subtargets.get(1).ref, "roleb");
		
		// write output
		AlignmentXmlWriter writer = new AlignmentXmlWriter(new FileOutputStream("target/testRes_v1.2nn_predicate.xml"));
		writer.writeMetaData(m);
		writer.writeAlignments(alignments);;
		writer.close();
		
		// compare input and output 

	}
}
