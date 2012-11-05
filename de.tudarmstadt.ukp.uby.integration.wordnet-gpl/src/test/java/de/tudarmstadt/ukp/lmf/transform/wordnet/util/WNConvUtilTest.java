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
package de.tudarmstadt.ukp.lmf.transform.wordnet.util;

import static org.junit.Assert.*;

import net.sf.extjwnl.data.POS;

import org.junit.Test;

import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.WNConvUtil;

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
public class WNConvUtilTest {

	@Test
	public void testGetPos() {
		assertEquals(EPartOfSpeech.noun, WNConvUtil.getPOS(POS.NOUN));
		assertEquals(EPartOfSpeech.verb, WNConvUtil.getPOS(POS.VERB));
		assertEquals(EPartOfSpeech.adjective, WNConvUtil.getPOS(POS.ADJECTIVE));
		assertEquals(EPartOfSpeech.adverb, WNConvUtil.getPOS(POS.ADVERB));
	}

}
