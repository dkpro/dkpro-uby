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
package de.tudarmstadt.ukp.lmf.model.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests methods of {@link LexicalResource} class
 * 
 * @author Zijad Maksuti
 *
 */
public class LexicalResourceTest {
	
	private LexicalResource lexicalResource;

	@Before
	public void setUp(){
		lexicalResource = new LexicalResource();
	}
	
	/**
	 * Tests {@link LexicalResource#addLexicon(Lexicon)}
	 */
	@Test
	public void testAddLexicon() {
		Lexicon lexicon = new Lexicon();
		assertTrue(lexicalResource.addLexicon(lexicon));
		List<Lexicon> lexicons = lexicalResource.getLexicons();
		assertEquals(1, lexicons.size());
		assertEquals(lexicon, lexicons.get(0));
		assertFalse(lexicalResource.addLexicon(lexicon));
		assertEquals(1, lexicons.size());
		Lexicon lexicon2 = new Lexicon();
		assertTrue(lexicalResource.addLexicon(lexicon2));
		assertEquals(2, lexicons.size());
		assertTrue(lexicons.contains(lexicon));
		assertTrue(lexicons.contains(lexicon2));
	}

}
