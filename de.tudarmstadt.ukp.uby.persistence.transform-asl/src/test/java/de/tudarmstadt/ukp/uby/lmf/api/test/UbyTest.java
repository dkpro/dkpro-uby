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
package de.tudarmstadt.ukp.uby.lmf.api.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.exceptions.UbyInvalidArgumentException;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBUtils;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;


/**
 * @author Eckle-Kohler
 * @author Yevgen Chebotar
 * Unit tests for UBY-API methods on in-memory test database
 *
 */
public class UbyTest extends TestCase
{
	
	private Uby uby;
	public UbyTest(){
		try {
			this.uby = createDB();
		} catch (Exception e) {
			fail(e.getMessage());			
		}
	}

	private Uby createDB() throws FileNotFoundException, DocumentException, UbyInvalidArgumentException {
		String uby_user = "root";
		String uby_pass = "pass";

		DBConfig dbConfig = 
			new DBConfig("not_important","org.h2.Driver","h2",uby_user,uby_pass,true);
		LMFDBUtils.createTables(dbConfig);
		XMLToDBTransformer trans = new XMLToDBTransformer(dbConfig);
		File ubyTestXmlFile = new File("src/test/resources/UbyTestLexicon.xml");
		trans.transform(ubyTestXmlFile, "UbyTest");
		Uby uby = new Uby(dbConfig);
		return uby;
		
	}
	
	@Test
	public void testLexicons() throws UbyInvalidArgumentException{		
		List<String> lexiconNames = uby.getLexiconNames();
		assertTrue(lexiconNames.contains("FrameNet"));
		assertTrue(lexiconNames.contains("VerbNet"));
		assertTrue(lexiconNames.contains("WordNet"));
		assertTrue(lexiconNames.contains("Wiktionary"));
		assertTrue(lexiconNames.contains("Wikipedia"));
		assertTrue(lexiconNames.contains("OmegaWikideu"));
		
		List<Lexicon> lexicons = uby.getLexicons();
		assertEquals(lexiconNames.size(), lexicons.size());
		
		for(Lexicon lexicon : lexicons){
			String name = lexicon.getName();
			assertTrue(lexiconNames.contains(name));
			assertEquals(uby.getLexiconByName(name).getId(), lexicon.getId());
		}
		
		lexicons = uby.getLexiconsByLanguage(ELanguageIdentifier.ENGLISH);
		assertEquals(lexicons.size(), 5);
		assertEquals(lexicons.get(0).getLexicalEntries().get(0)
			.getLemma().getFormRepresentations().get(0).getLanguageIdentifier(), ELanguageIdentifier.ENGLISH);
	}
	
	@Test
	public void testLexicalEntries() throws UbyInvalidArgumentException{
		Lexicon lexicon = uby.getLexiconByName("FrameNet");
		List<LexicalEntry> lexEntries = uby.getLexicalEntries("run", lexicon);
		assertEquals(lexEntries.size(), 1);
		
		LexicalEntry lexEntry = lexEntries.get(0);		
		lexEntry = uby.getLexicalEntryById(lexEntry.getId());		
		lexEntry = uby.getLexicalEntries(lexEntry.getLemmaForm(), 
					   lexEntry.getPartOfSpeech(), lexicon).get(0);
		
		assertEquals(lexEntry.getId(), "FN_LexicalEntry_4417");
		assertEquals(lexEntry.getPartOfSpeech(), EPartOfSpeech.verb);
		
		
		List<Lexicon> lexicons = uby.getLexicons();
		for(Lexicon l : lexicons){
			Iterator<LexicalEntry> iter = uby.getLexicalEntryIterator(l);
			int i = 0;			
			while(iter.hasNext()){
				iter.next();
				i++;				
			}
			assertEquals(i, l.getLexicalEntries().size());
		}
		
		lexEntries = uby.getLexicalEntries("question", null);
		
		Set<String> lexNames = new HashSet<String>();		
		for(LexicalEntry le : lexEntries){
			assertEquals(le.getLemmaForm(), "question");
			lexNames.add(le.getLexicon().getName());			
		}		
		assertTrue(lexNames.contains("VerbNet"));		
		assertTrue(lexNames.contains("Wiktionary"));		
		
		lexEntries = uby.getLexicalEntriesByLemmaPrefix("ques", EPartOfSpeech.noun, null);
		assertEquals(lexEntries.size(), 2);
	}
	
	@Test
	public void testSenses() throws UbyInvalidArgumentException{
		
		List<Lexicon> lexicons = uby.getLexicons();
		for(Lexicon l : lexicons){
			Iterator<Sense> iter = uby.getSenseIterator(l);
			int i = 0;		
			String prefix = "";
			while(iter.hasNext()){
				Sense sense = iter.next();
				if(prefix.isEmpty()){
					prefix = sense.getId().split("_")[0];
				}
				i++;				
			}			
			int j = 0;
			for(LexicalEntry le : l.getLexicalEntries()){
				j += le.getSenses().size();
			}			
			assertEquals(i, j);
			assertEquals(uby.getSensesbyIdPattern(prefix).size(), i);
		}
		
		Sense sense = uby.getSenseById("VN_Sense_2");
		assertEquals(sense.getIndex(), 2);
		assertEquals(sense.getSenseExamples().get(0).getTextRepresentations()
				.get(0).getWrittenText(), "I interrogated him as a suspect.");
		assertEquals(sense.getSemanticLabels().get(0).getType(), ELabelTypeSemantics.verbnetClass);
		
		List<Sense> extSenses = uby.getSensesByOriginalReference("FrameNet", "10035");
		assertEquals(extSenses.size(), 1);
		for(Sense s : extSenses){
			assertEquals(s.getLexicalEntry().getLexicon().getName(), "FrameNet");
		}
		
		extSenses = uby.getSensesByOriginalReference("VerbNet", "question_interrogate-37.1.3");
		assertEquals(extSenses.size(), 3);
		int i = 0;
		for(Sense s : extSenses){
			assertEquals(s.getLexicalEntry().getLexicon().getName(), "VerbNet");
			assertEquals(i, s.getIndex());
			i++;
		}
		extSenses = uby.getSensesByOriginalReference("WordNet", "[POS: verb] question%2:32:03::");
		assertEquals(extSenses.size(), 1);
		assertEquals(extSenses.get(0).getIndex(), 1);
		assertEquals(extSenses.get(0).getLexicalEntry().getLexicon().getName(), "WordNet");
		
		extSenses = uby.getSensesByOriginalReference("Wiktionary", "49890:1:2");
		assertEquals(extSenses.get(0).getId(), "WktEn_sense_2");

		extSenses = uby.getSensesByOriginalReference("OW", "209021");
		assertEquals(extSenses.get(0).getId(), "OW_deu_Sense_4143");

		
		
		
	}
	
	
	@Test
	public void testSynsets() throws UbyInvalidArgumentException{
		Lexicon lexicon = uby.getLexiconByName("WordNet");
		Iterator<Synset> synsetIterator = uby.getSynsetIterator(lexicon);
		
		int i = 0;
		while(synsetIterator.hasNext()){
			Synset synset = synsetIterator.next();
			Synset synset2 = uby.getSynsetById(synset.getId());
			assertEquals(synset.getDefinitionText(), synset2.getDefinitionText());
			assertEquals(synset.getId(), synset2.getId());
			i++;
		}
		assertEquals(i,6);
	}
}
