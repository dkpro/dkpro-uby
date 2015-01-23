/**
 * Copyright 2015
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;

/**
 * Tests methods of {@link SenseGenerator} class.<br>
 *
 * Tests are made for <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * data and UBY-LMF DTD version 0.2.0.
 *
 * @author Zijad Maksuti
 *
 * @since 0.2.0
 *
 */
@Ignore public class SenseGeneratorTest {

	private static SenseGenerator senseGenerator;

	/**
	 * Creates a {@link SenseGenerator} instance.
	 * @throws JWNLException
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 *
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() throws FileNotFoundException, XMLStreamException, IOException, JWNLException{
		if(gnet == null) {
            TestSuite.setUpClass();
        }
		senseGenerator= new SenseGenerator(gnet,"GermaNet_7.0_deu");
	}

	/**
	 * Tests {@link SenseGenerator#generateSenses(java.util.Set)} method.
	 * @since 0.2.0
	 */
	@Test
	public void testGenerateSenses(){
		Set<LexUnit> luGroup = new HashSet<LexUnit>(4);
		luGroup.add(gnet.getLexUnitByID(84356));
		luGroup.add(gnet.getLexUnitByID(85423));
		luGroup.add(gnet.getLexUnitByID(79989));
		luGroup.add(gnet.getLexUnitByID(84884));

		List<Sense> senses = senseGenerator.generateSenses(luGroup);

		assertEquals(senses.size(), luGroup.size());
		for(Sense sense : senses){
			List<MonolingualExternalRef> monolingualExternalRefs = sense.getMonolingualExternalRefs();
			assertFalse(monolingualExternalRefs.isEmpty());
			for(MonolingualExternalRef monolingualExternalRef : monolingualExternalRefs){
				assertFalse(monolingualExternalRef == null);

				String externalSystem = monolingualExternalRef.getExternalSystem();
				assertNotNull(externalSystem);
				assertEquals(externalSystem, "GermaNet 7.0 LexicalUnit-ID");

				String externalReference = monolingualExternalRef.getExternalReference();
				assertNotNull(externalReference);
				assertFalse(externalReference.equals(""));
				assertFalse(externalReference.equals(" "));
			}
		// TODO test the rest of the Sense structure
		}
	}

	@AfterClass
	public static void tearDown(){
		senseGenerator = null;
	}

}
