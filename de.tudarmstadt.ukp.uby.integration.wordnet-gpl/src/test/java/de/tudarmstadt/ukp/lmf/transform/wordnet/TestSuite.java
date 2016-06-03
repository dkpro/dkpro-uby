/**
 * Copyright 2016
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
