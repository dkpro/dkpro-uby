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
package de.tudarmstadt.ukp.lmf.model.semantics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * Tests methods of {@link Synset} class
 * 
 * @author Zijad Maksuti
 *
 * @since UBY 0.2.0
 */
public class SynsetTest {
	
	/**
	 * Tests the {@link Synset#Synset()} constructor.
	 */
	@Test
	public void testSynset(){
		Synset synset = new Synset();
		assertNull(synset.getId());
		// TODO check other fields
	}
	
	/**
	 * Tests the {@link Synset#Synset(String)} constructor.
	 */
	@Test
	public void testSynsetWithId(){
		String synsetId = "synsetId";
		Synset synset = new Synset(synsetId);
		assertEquals(synsetId, synset.getId());
		// TODO check other fields
	}

}
