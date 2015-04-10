/*******************************************************************************
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.mrd.Equivalent;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * An instance of this class updates the SenseRelations of already created Senses
 * @author matuschek
 *
 */
public class EquivalentGenerator {

	private final LexicalEntryGenerator lexicalEntryGenerator;

	private final int GlobalLanguage;

	public EquivalentGenerator(LexicalEntryGenerator lexicalEntryGenerator, SynsetGenerator synsetGenerator, int globalLanguage) {
		this.GlobalLanguage = globalLanguage;
		this.lexicalEntryGenerator = lexicalEntryGenerator;
	}

	/**
	 * This method updates the SenseRelations
	 * of alredy created Senses
	 * @throws OmegaWikiException
	 */
	public void updateEquivalents() throws OmegaWikiException {

		// Iterate over all LexemeGroups and update
		for(SynTrans lexeme : lexicalEntryGenerator.getSenseGenerator().getProcessedLexemes()){
			updateEquivalents(lexeme);
		}
	}

	// Skip supplementary planes (multi-byte UTF16 characters), as they cause problems with the XML reader.
	protected static Pattern SUPPLEMENTARY_PLANES = Pattern.compile("[\\uD7FF-\\uE000]");
	
	/**
	 * This method updates the Equvalents
	 * of lexeme's Sense
	 * @param lexeme
	 * @throws OmegaWikiException
	 */
	private void updateEquivalents(SynTrans lexeme) throws OmegaWikiException {

		SenseGenerator senseGenerator = lexicalEntryGenerator.getSenseGenerator();
		Sense sense = senseGenerator.getSense(lexeme);
		List<Equivalent> equivalents = new LinkedList<Equivalent>();
		DefinedMeaning dm = lexeme.getDefinedMeaning();

		try {
			for(SynTrans st : dm.getSynTranses())
			{
				if (st.getSyntrans().getLanguageId() != GlobalLanguage && st.getSyntrans().getSpelling()!=null && st.getSyntrans().getSpelling().length()>0)
				{
					String writtenForm = st.getSyntrans().getSpelling();
					if (SUPPLEMENTARY_PLANES.matcher(writtenForm).find())
						continue;
					if (writtenForm.length() > 255)
						writtenForm = writtenForm.substring(0, 255);
					Equivalent eq = new  Equivalent();
					eq.setLanguageIdentifier(OmegaWikiLMFMap.mapLanguage(st.getSyntrans().getLanguageId()));
					eq.setWrittenForm(writtenForm);
					equivalents.add(eq);
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sense.setEquivalents(equivalents);
	}

}
