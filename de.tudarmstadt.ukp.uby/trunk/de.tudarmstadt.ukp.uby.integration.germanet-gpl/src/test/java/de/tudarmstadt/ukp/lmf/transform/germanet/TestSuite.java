/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.junit.BeforeClass;
import org.junit.Ignore;
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
@Ignore
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
