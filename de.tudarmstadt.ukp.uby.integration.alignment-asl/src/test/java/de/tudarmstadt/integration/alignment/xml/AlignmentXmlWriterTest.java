package de.tudarmstadt.integration.alignment.xml;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlReader;
import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlWriter;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

public class AlignmentXmlWriterTest {

	@Ignore
	@Test
	public void readerWriterTest() throws Exception{
		//System.err.println("testing: " + JAXBContext.newInstance(AlignmentXmlReader.class).getClass().getName());

		// read xml information
		AlignmentXmlReader reader = new AlignmentXmlReader("src/test/resources/ResourceAlignmentDraft_v1.2nn.xml");

		XmlMeta m = reader.readMetaData();
		Alignments alignments = reader.readAlignments();				
		reader.close(); 
		
		// check reading 
		assertEquals(m.date.trim(),"2014-06-12"); //trim formatting
		assertEquals(m.rights.trim(),"Released into the public domain by the creator.");
		assertEquals(m.sourceResource.id,"WordNet_2.1");
		assertEquals(m.targetResource.id,"GermaNet_1.0");
		assertEquals(m.targetResource.type.trim(),"lexical unit ID");
		assertEquals(m.scoretypes.iterator().next().type, "manual");
		assertEquals(alignments.source.size(), 1);
		Source s = alignments.source.iterator().next();
		assertEquals(s.targets.size(),2);
		assertEquals(s.targets.get(1).ref,"12443");
		assertEquals(s.targets.get(1).scores.get(0).src,"headferret");
		assertEquals(s.targets.get(0).decision.src,"ferretdecision");
		assertEquals(s.targets.get(0).decision.value,"true");
		
		// write output
		AlignmentXmlWriter writer = new AlignmentXmlWriter(new FileOutputStream("target/testRes_v1.2nn.xml"));
		writer.writeMetaData(m);
		writer.writeAlignments(alignments);;
		writer.close();
		
		// compare input and output 

	}
}
