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

import org.junit.BeforeClass;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;

/**
 * Tests methods of {@link GNConverter} and the correctness of the
 * UBY-LMF structure created by it.<br>
 * 
 * Tests are made for <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * data and UBY-LMF DTD version 0.2.0.
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
public class GNConverterTest {
	
	/*
	 * The GNConverter
	 */
	static GNConverter gnConverter;
	
	/**
	 * Invokes {@link GNConverter#toLMF()} method.
	 * 
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() {
		if(gnet == null)
			TestSuite.setUpClass();
		
		String dtd_version = "1_0";

		gnConverter = new GNConverter(gnet, new LexicalResource(),dtd_version);
		gnConverter.toLMF();
		
	}
	
	/**
	 * Tests the creation of {@link GlobalInformation}
	 */
	@Test
	public void testGlobalInformation(){
		GlobalInformation gi = gnConverter.getLexicalResource().getGlobalInformation();
		assertEquals("LMF representation of GermaNet 7.0", gi.getLabel());
	}
	
}
