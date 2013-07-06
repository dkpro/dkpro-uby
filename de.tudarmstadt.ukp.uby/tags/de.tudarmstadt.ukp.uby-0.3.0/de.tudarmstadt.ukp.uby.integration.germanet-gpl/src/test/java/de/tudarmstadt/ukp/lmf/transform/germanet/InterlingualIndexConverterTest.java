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
import static de.tudarmstadt.ukp.lmf.transform.germanet.TestSuite.wordNetLexicon;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import static org.junit.Assert.fail;

/**
 * Tests methods of {@link InterlingualIndexConverter} class.<br>
 * 
 * Tests are made for <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * data and UBY-LMF DTD version 0.2.0.
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
@Ignore public class InterlingualIndexConverterTest {
	
	private static InterlingualIndexConverter iliConverter;
	private GNConverter gnConverter;
	private static Class<? extends InterlingualIndexConverter> iliClass;
	private static Field fieldSynsetMappings;
	
	@BeforeClass
	public static void setUpClass() throws JWNLException, NoSuchFieldException, SecurityException, XMLStreamException, IOException{
		if(gnet == null || wordNetLexicon == null)
			TestSuite.setUpClass();
		
		// reveal private members
		iliClass = InterlingualIndexConverter.class;
		
		fieldSynsetMappings = iliClass.getDeclaredField("synsetMappings");
		fieldSynsetMappings.setAccessible(true);
		
	}
	
	@Before
	public void initialize(){
		gnConverter = new GNConverter(gnet, new LexicalResource(), "test");
		iliConverter = new InterlingualIndexConverter(gnConverter, gnet, wordNetLexicon);
	}

	/**
	 * Test method for {@link de.tudarmstadt.ukp.lmf.transform.germanet.InterlingualIndexConverter#InterlingualIndexConverter(de.tudarmstadt.ukp.lmf.transform.germanet.GNConverter, de.tuebingen.uni.sfs.germanet.api.GermaNet, de.tudarmstadt.ukp.lmf.model.core.Lexicon)}.
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testInterlingualIndexConverter() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		Field fieldGnConverter = iliClass.getDeclaredField("gnConverter");
		fieldGnConverter.setAccessible(true);
		assertEquals(gnConverter, fieldGnConverter.get(iliConverter));
		
		Field fieldGnet = iliClass.getDeclaredField("gnet");
		fieldGnet.setAccessible(true);
		assertEquals(gnet, fieldGnet.get(iliConverter));
		
		Field fieldWordNetLexicon = iliClass.getDeclaredField("wordNetLexicon");
		fieldWordNetLexicon.setAccessible(true);
		assertEquals(wordNetLexicon, fieldWordNetLexicon.get(iliConverter));
		
		Field fieldSenseAxes = iliClass.getDeclaredField("senseAxes");
		fieldSenseAxes.setAccessible(true);
		assertEquals(0, ((List<SenseAxis>)fieldSenseAxes.get(iliConverter)).size());

		assertEquals(0, ((Map<EPartOfSpeech, Map<Long, Synset>>)fieldSynsetMappings.get(iliConverter)).size());
		
	}

	/**
	 * Test method for {@link de.tudarmstadt.ukp.lmf.transform.germanet.InterlingualIndexConverter#convert()}.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public final void testConvert() throws IllegalArgumentException, IllegalAccessException {
		
		iliConverter.convert();
		/*
		 * synset mappings should not be empty
		 */
		@SuppressWarnings("unchecked")
		Map<EPartOfSpeech, Map<Long, Synset>> synsetMappings = (Map<EPartOfSpeech, Map<Long, Synset>>) fieldSynsetMappings.get(iliConverter);
		assertFalse(synsetMappings.isEmpty());
		List<EPartOfSpeech> poses = Arrays.asList(EPartOfSpeech.noun, EPartOfSpeech.verb, EPartOfSpeech.adjective, EPartOfSpeech.adverb);
		assertTrue(synsetMappings.keySet().containsAll(poses));
		
		List<Synset> synsets = wordNetLexicon.getSynsets();
		int numSynsets = 0;
		for(EPartOfSpeech pos : poses){
			Collection<Synset> temp = synsetMappings.get(pos).values();
			assertTrue(synsets.containsAll(temp));
			numSynsets += temp.size();
		}
		assertEquals(synsets.size(), numSynsets);
		
		/*
		 * Check the creation of SenseAxes
		 */
		List<SenseAxis> senseAxes = iliConverter.getSenseAxes();
		for(SenseAxis senseAxis : senseAxes){
			testNodesPresent(senseAxis);
		}
	}

	/**
	 * Tests the contents of the consumed {@link SenseAxis} instance. In particular,
	 * every sense axis should either link two senses or two synsets. Also,
	 * every sense axis should have an ID set.
	 * 
	 * @param senseAxis sense axis to be tested
	 */
	static void testNodesPresent(SenseAxis senseAxis) {
		String id = senseAxis.getId();
		ESenseAxisType type = senseAxis.getSenseAxisType();
		
		assertNotNull(id);
		assertNotNull("SenseAxis should have the type set", type);
		
		Synset synsetOne = senseAxis.getSynsetOne();
		Synset synsetTwo = senseAxis.getSynsetTwo();
		Sense senseOne = senseAxis.getSenseOne();
		Sense senseTwo = senseAxis.getSenseTwo();
		
		boolean synsetsLinked = synsetOne != null && synsetTwo != null;
		boolean sensesLinked = senseOne != null && senseTwo != null;
		boolean linkFound = synsetsLinked || sensesLinked;
		
		if(!linkFound){
			StringBuffer sb = new StringBuffer(512);
			sb.append("SenseAxis should link two synsets or two senses. ").append("\n");
			sb.append("SenseAxis:").append("\n");
			sb.append("id: ").append(id).append("\n");
			sb.append("senseOne: ").append(senseOne).append("\n");
			sb.append("senseTwo: ").append(senseTwo).append("\n");
			sb.append("synsetOne: ").append(synsetOne).append("\n");
			sb.append("synsetTwo: ").append(synsetTwo).append("\n");
			sb.append("type: ").append(type).append("\n");
			fail(sb.toString());
		}
	}

	/**
	 * Test method for {@link de.tudarmstadt.ukp.lmf.transform.germanet.InterlingualIndexConverter#getSenseAxes()}.
	 */
	@Test
	public final void testGetSenseAxes() {
		assertEquals(0, iliConverter.getSenseAxes().size());
	}
	

}
