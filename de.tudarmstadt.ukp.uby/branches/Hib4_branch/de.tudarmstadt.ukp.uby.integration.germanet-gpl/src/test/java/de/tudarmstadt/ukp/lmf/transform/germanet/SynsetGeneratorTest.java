/**
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package de.tudarmstadt.ukp.lmf.transform.germanet;

import static de.tudarmstadt.ukp.lmf.transform.germanet.TestSuite.gnet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * Tests methods of {@link SynsetGenerator} class.<br>
 * 
 * Tests are made for <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * data and UBY-LMF DTD version 0.2.0.
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
@Ignore public class SynsetGeneratorTest {
	
	
private static SynsetGenerator synsetGenerator;
	
	/**
	 * Creates a {@link SynsetGenerator} instance.
	 * @throws JWNLException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * 
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, XMLStreamException, IOException, JWNLException{
		if(gnet == null)
			TestSuite.setUpClass();
		synsetGenerator= new SynsetGenerator(gnet);
	}
	
	/**
	 * Tests {@link SynsetGenerator#initialize()} method.
	 * @since 0.2.0
	 */
	@Test
	public void testInitialize(){
		
		// test the creation of synsets
		synsetGenerator.initialize();
		List<Synset> synsets = synsetGenerator.getSynsets();
		assertFalse("No synsets created", synsets.isEmpty());
		for(Synset synset : synsets){
			assertNotNull("Synset should not be null", synset);
			
			// test the creation of MonolingualExternalRefs
			List<MonolingualExternalRef> monolingualExternalRefs = synset.getMonolingualExternalRefs();
			assertFalse(monolingualExternalRefs.isEmpty());
			for(MonolingualExternalRef monolingualExternalRef : monolingualExternalRefs){
				assertFalse(monolingualExternalRef == null);
				
				String externalSystem = monolingualExternalRef.getExternalSystem();
				assertNotNull(externalSystem);
				assertEquals(externalSystem, "GermaNet 7.0 Synset-ID");

				String externalReference = monolingualExternalRef.getExternalReference();
				assertNotNull(externalReference);
				assertFalse(externalReference.replaceAll(" ", "").isEmpty());
				}
		}
		// TODO test the rest of Synset structure
	}
	
	@AfterClass
	public static void tearDown(){
		synsetGenerator = null;
	}
	
}
