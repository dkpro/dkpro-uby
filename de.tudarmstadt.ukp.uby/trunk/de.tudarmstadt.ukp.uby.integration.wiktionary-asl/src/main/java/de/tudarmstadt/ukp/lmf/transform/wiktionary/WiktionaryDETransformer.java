/*******************************************************************************
 * Copyright 2012
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
package de.tudarmstadt.ukp.lmf.transform.wiktionary;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.wiktionary.api.Language;

/**
 * Converts the German Wiktionary to LMF
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WiktionaryDETransformer extends WiktionaryLMFTransformer {

	public WiktionaryDETransformer(final DBConfig dbConfig, 
			final IWiktionaryEdition wkt, final Language wktLang, 
			final String dtd) {
		super(dbConfig, wkt, wktLang, dtd);
	}

	@Override
	protected String getResourceAlias() {
		return "WktDE";
	};

	@Override
	protected LexicalResource createLexicalResource() {
		LexicalResource resource = new LexicalResource();
		GlobalInformation glInformation = new GlobalInformation();
		glInformation.setLabel("Wiktionary German edition, dump of 20110406, JWKTL 0.15.2");
		resource.setGlobalInformation(glInformation);
		resource.setName("WiktionaryDE");
		resource.setDtdVersion(dtd_version);

		return resource;
	}

	@Override
	protected Lexicon createNextLexicon() {
		if(!entryIterator.hasNext()/* || currentEntryNr > 1000*/) {
			return null;
		}
		Lexicon lexicon = new Lexicon();
		ELanguageIdentifier lmfLang = WiktionaryLMFMap.mapLanguage(wktLang);
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWkt"+lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("WiktionaryDE");
		return lexicon;
	}

}
