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
package de.tudarmstadt.ukp.lmf.model.meta;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.core.Sense;

/**
 * Test the methods of {@link Frequency} class.
 * 
 * @author Zijad Maksuti
 *
 */
public class FrequencyTest {
	
	private static Frequency frequency;
	
	/**
	 * Initializes the fields needed for this test to run.
	 */
	@Before
	public void setUp(){
		frequency = new Frequency();
	}
	
	/**
	 * Tests the {@link Frequency#getParent()} method.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testGetParent() {
		assertNull(frequency.getParent());
		assertNull(frequency.getParentId());
	}
	
	/**
	 * Tests the {@link Frequency#setParent(de.tudarmstadt.ukp.lmf.model.interfaces.IHasID)}
	 * method.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSetParent(){
		String id = "test_id";
		Sense sense = new Sense(id);
		frequency.setParent(sense);
		assertEquals(sense, frequency.getParent());
		assertEquals(sense.getId(), frequency.getParentId());
	}
	
	/**
	 * Sets the fields of this class to <code>null</code>.
	 */
	@AfterClass
	public static void tearDown(){
		frequency = null;
	}

}
