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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.transform.wordnet.WNConverter;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;

/**
 * Runs all tests for germanet-gpl module:
 * {@link SenseGeneratorTest},
 * {@link SynsetGeneratorTest},
 * {@link InterlingualIndexConverterTest} and
 * {@link GNConverterTest}.
 * 
 * @author Zijad Maksuti
 * 
 * @since UBY 0.2.0
 *
 */
@RunWith(Suite.class)
@SuiteClasses({SynsetGeneratorTest.class, SenseGeneratorTest.class, InterlingualIndexConverterTest.class, GNConverterTest.class})
public class TestSuite {
	
	/*
	 * GermaNet object
	 */
	static GermaNet gnet;
	
	/*
	 * WordNet Lexicon
	 */
	static Lexicon wordNetLexicon;
	
	/**
	 * Creates a {@link GermaNet} instance and initializes it.<br>
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws JWNLException 
	 * 
	 * @since UBY 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, XMLStreamException, IOException, JWNLException {
		String UBY_HOME = System.getenv("UBY_HOME");
		File gnetDir = new File(UBY_HOME+"/GermaNet/GN_V70/GN_V70/GN_V70_XML");
		
		gnet = new GermaNet(gnetDir);
		
		/*
		 * Prepare wordNetLexicon
		 */
		String extJWNL_configuration = UBY_HOME+"/WordNet/extJWNL/file_properties.xml";
		Dictionary extWordnet = Dictionary.getInstance(new FileInputStream(extJWNL_configuration));
		String dtd_version = "dtd_version_test";
		WNConverter converterWN = new WNConverter(extWordnet, new LexicalResource(), dtd_version, UBY_HOME+"/WordNet/cache/ExampleSentenceLexemeMapping.xml");
		converterWN.toLMF();
		wordNetLexicon = converterWN.getLexicalResource().getLexicons().get(0);	
		
	}
	
}
