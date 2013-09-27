/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.model.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests methods of {@link Lexicon} class
 * 
 * @author Zijad Maksuti
 * 
 * @since UBY 0.2.0
 *
 */
public class LexiconTest {
	
	private Lexicon lexicon;

	@Before
	public void setUp(){
		lexicon = new Lexicon();
	}
	
	/**
	 * Tests {@link Lexicon#addLexicalEntry(LexicalEntry)}
	 */
	@Test
	public void testAddLexicalEntry() {
		LexicalEntry lexicalEntry = new LexicalEntry("1");
		assertTrue(lexicon.addLexicalEntry(lexicalEntry));
		List<LexicalEntry> lexicalEntries = lexicon.getLexicalEntries();
		
		assertEquals(1, lexicalEntries.size());
		assertEquals(lexicalEntry, lexicalEntries.get(0));
		assertFalse(lexicon.addLexicalEntry(lexicalEntry));
		assertEquals(1, lexicalEntries.size());
		LexicalEntry lexicalEntry2 = new LexicalEntry("2");
		assertTrue(lexicon.addLexicalEntry(lexicalEntry2));
		assertEquals(2, lexicalEntries.size());
		assertTrue(lexicalEntries.contains(lexicalEntry));
		assertTrue(lexicalEntries.contains(lexicalEntry2));
	}

}
