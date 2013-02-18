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
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tudarmstadt.ukp.lmf.transform.wordnet.util.WNConvUtilTest;


/**
 * Runs all tests for wordnet-gpl module:
 * {@link WNConvUtilTest} and {@link SenseGeneratorTest}.<p>
 * 
 * @author Zijad Maksuti
 * 
 * @since UBY 0.2.0
 *
 */
@Ignore
@RunWith(Suite.class)
@SuiteClasses({WNConvUtilTest.class, SenseGeneratorTest.class})
public class TestSuite {
	
	/*
	 * WordNet dictionary
	 */
	static Dictionary wordNet;
	static final String UBY_HOME = System.getenv("UBY_HOME");;
	
	/**
	 * Creates a {@link Dictionary} instance of WordNet and initializes it.<br>
	 * @throws JWNLException 
	 * @throws FileNotFoundException 
	 * 
	 * @since UBY 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, JWNLException {
		
		String extJWNL_configuration = UBY_HOME+"/WordNet/extJWNL/file_properties.xml";
		wordNet = Dictionary.getInstance(new FileInputStream(extJWNL_configuration));
		
	}
	
}
