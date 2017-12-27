/**
 * Copyright 2017
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * Tests methods of {@link SynsetGenerator} class.<br>
 *
 * Tests are made for <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * data and UBY-LMF DTD version 0.2.0.
 *
 * @author Zijad Maksuti
 *
 * @since 0.2.0
 *
 */
@Ignore public class SynsetGeneratorTest {


private static SynsetGenerator synsetGenerator;

	/**
	 * Creates a {@link SynsetGenerator} instance.
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 *
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, XMLStreamException, IOException {
		if(gnet == null) {
            TestSuite.setUpClass();
        }
		synsetGenerator= new SynsetGenerator(gnet,"GermaNet_7.0_deu");
	}

	/**
	 * Tests {@link SynsetGenerator#initialize()} method.
	 * @since 0.2.0
	 */
	@Test
	public void testInitialize(){

		// test the creation of synsets
		synsetGenerator.initialize();
		List<Synset> synsets = synsetGenerator.getSynsets();
		assertFalse("No synsets created", synsets.isEmpty());
		for(Synset synset : synsets){
			assertNotNull("Synset should not be null", synset);

			// test the creation of MonolingualExternalRefs
			List<MonolingualExternalRef> monolingualExternalRefs = synset.getMonolingualExternalRefs();
			assertFalse(monolingualExternalRefs.isEmpty());
			for(MonolingualExternalRef monolingualExternalRef : monolingualExternalRefs){
				assertFalse(monolingualExternalRef == null);

				String externalSystem = monolingualExternalRef.getExternalSystem();
				assertNotNull(externalSystem);
				assertEquals(externalSystem, "GermaNet 7.0 Synset-ID");

				String externalReference = monolingualExternalRef.getExternalReference();
				assertNotNull(externalReference);
				assertFalse(externalReference.replaceAll(" ", "").isEmpty());
				}
		}
		// TODO test the rest of Synset structure
	}

	@AfterClass
	public static void tearDown(){
		synsetGenerator = null;
	}

}
