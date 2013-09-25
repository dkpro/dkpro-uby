/**
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
 */

package de.tudarmstadt.ukp.lmf.transform.wordnet.util;

import static org.junit.Assert.assertEquals;
import net.sf.extjwnl.data.POS;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;

/**
 * Tests methods of {@link WNConvUtil} class.<br>
 *
 * Tests are made for WordNet 3.0
 * data and UBY-LMF DTD version 0.2.0.
 *
 * @author Zijad Maksuti
 *
 * @since 0.2.0
 *
 */
@Ignore public class WNConvUtilTest {

	@Test
	public void testGetPos() {
		assertEquals(EPartOfSpeech.noun, WNConvUtil.getPOS(POS.NOUN));
		assertEquals(EPartOfSpeech.verb, WNConvUtil.getPOS(POS.VERB));
		assertEquals(EPartOfSpeech.adjective, WNConvUtil.getPOS(POS.ADJECTIVE));
		assertEquals(EPartOfSpeech.adverb, WNConvUtil.getPOS(POS.ADVERB));
	}

}
