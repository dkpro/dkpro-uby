/*******************************************************************************
 * Copyright 2015
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

/**
 * Tests methods of {@link SemanticArgument} class
 * 
 * @author Zijad Maksuti
 *
 * @since UBY 0.2.0
 */
public class SemanticArgumentTest {
	
	/**
	 * Tests the {@link SemanticArgument#SemanticArgument()} constructor.
	 */
	@Test
	public void testSemanticArgument(){
		SemanticArgument semanticArgument = new SemanticArgument();
		assertNull(semanticArgument.getId());
		// TODO check other fields
	}
	
	/**
	 * Tests the {@link SemanticArgument#SemanticArgument(String)} constructor.
	 */
	@Test
	public void testSemanticArgumentWithId(){
		String semArgId = "semArgId";
		SemanticArgument semArg = new SemanticArgument(semArgId);
		assertEquals(semArgId, semArg.getId());
		// TODO check other fields
	}

}
