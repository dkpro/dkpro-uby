/**
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.transform.germanet;

import static de.tudarmstadt.ukp.lmf.transform.germanet.TestSuite.gnet;
import static de.tudarmstadt.ukp.lmf.transform.germanet.TestSuite.wordNetLexicon;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

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
@Ignore public class GNConverterTest {

	/*
	 * The GNConverter
	 */
	private static GNConverter gnConverter;

	/**
	 * Invokes {@link GNConverter#toLMF()} method.
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws JWNLException
	 * @throws FileNotFoundException
	 *
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, XMLStreamException, IOException, JWNLException {
		if(gnet == null || wordNetLexicon == null) {
            TestSuite.setUpClass();
        }

		String dtd_version = "1_0";

		gnConverter = new GNConverter(gnet, new LexicalResource(),dtd_version);
		gnConverter.toLMF();

	}

	/**
	 * Tests the creation of {@link GlobalInformation}
	 *
	 * @since 0.2.0
	 */
	@Test
	public void testGlobalInformation(){
		GlobalInformation gi = gnConverter.getLexicalResource().getGlobalInformation();
		assertEquals("LMF representation of GermaNet 7.0", gi.getLabel());
	}

	/**
	 * Tests the creation of all {@link MonolingualExternalRef} instances.
	 * In particular, the return values of {@link MonolingualExternalRef#getExternalReference()}
	 * and {@link MonolingualExternalRef#getExternalSystem()} are tested.
	 *
	 * @since 0.2.0
	 */
	@Test
	public void testMonolingualExternalRefs(){
		LexicalResource lr = gnConverter.getLexicalResource();
		for(Lexicon lexicon : lr.getLexicons()){
			for(LexicalEntry le : lexicon.getLexicalEntries()) {
                for(Sense sense : le.getSenses()){
					// in Sense class
					checkMonolingualExternalRefs(sense.getMonolingualExternalRefs());

					for(Context context : sense.getContexts()) {
                        // in Context class
                        checkMonolingualExternalRefs(context.getMonolingualExternalRefs());
                    }

					for(SemanticLabel semanticLabel : sense.getSemanticLabels()) {
                        // in SemanticLabel class
						checkMonolingualExternalRefs(semanticLabel.getMonolingualExternalRefs());
                    }
				}
            }
			for(Synset synset : lexicon.getSynsets()) {
                // in Synset class
				checkMonolingualExternalRefs(synset.getMonolingualExternalRefs());
            }
			}
	}

	/**
	 * Consumes a {@link List} of {@link MonolingualExternalRef} instances and
	 * tests the value of their fields.<br>
	 * In particular, the return values of {@link MonolingualExternalRef#getExternalReference()}
	 * and {@link MonolingualExternalRef#getExternalSystem()} are tested.
	 *
	 * @param monolingualExternalRefs the list of monolingual external references to be tested
	 *
	 * @since 0.2.0
	 */
	private void checkMonolingualExternalRefs(List<MonolingualExternalRef> monolingualExternalRefs){

		for(MonolingualExternalRef mer : monolingualExternalRefs){
			assertNotNull("MonolingualExternalRef instnace should not be null", mer);
			assertTrue(mer.getExternalSystem().startsWith("GermaNet 7.0 "));
			assertNotNull(mer.getExternalReference());
			assertFalse(mer.getExternalReference().replaceAll(" " , "").isEmpty());
		}
	}

	/**
	 * Tests the creation of all {@link SemanticLabel} instances.
	 *
	 * @since 0.2.0
	 */
	@Test
	public void testSemanticLabels(){
		LexicalResource lr = gnConverter.getLexicalResource();
		for(Lexicon lexicon : lr.getLexicons()) {
            for(LexicalEntry le : lexicon.getLexicalEntries()) {
                for(Sense sense : le.getSenses()){
					List<SemanticLabel> semanticLabels = sense.getSemanticLabels();
					assertTrue("Sense does not have any semantic labels attached.", semanticLabels.size()>0);
					for(SemanticLabel semanticLabel : semanticLabels){
						assertNotNull(semanticLabel.getLabel());
					}
				}
            }
        }
	}

	/**
	 * Tests {@link GNConverter#toLMF(Lexicon)} method.
	 *
	 * @since UBY 0.2.0
	 */
	@Test
	public void testToLMFLexicon(){
		gnConverter.toLMF(wordNetLexicon);
		LexicalResource lexicalResource = gnConverter.getLexicalResource();
		List<SenseAxis> senseAxes = lexicalResource.getSenseAxes();

		assertFalse(senseAxes.isEmpty());
		for(SenseAxis senseAxis : senseAxes) {
            InterlingualIndexConverterTest.testNodesPresent(senseAxis);
        }
	}

}
