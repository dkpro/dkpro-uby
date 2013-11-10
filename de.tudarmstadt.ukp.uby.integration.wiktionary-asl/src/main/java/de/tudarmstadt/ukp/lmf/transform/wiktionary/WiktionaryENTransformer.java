/*******************************************************************************
 * Copyright 2013
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

import java.io.IOException;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.wiktionary.api.util.ILanguage;

/**
 * Converts the English Wiktionary to LMF
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 * @author Zijad Maksuti
 */
public class WiktionaryENTransformer extends WiktionaryLMFTransformer {

	public WiktionaryENTransformer(final DBConfig dbConfig,
			final IWiktionaryEdition wkt, final ILanguage wktLang,
			final String dtd) throws IOException {
		super(dbConfig, wkt, wktLang, dtd);
	}

	@Override
	protected String getResourceAlias() {
		return "WktEN";
	}

	@Override
	protected LexicalResource createLexicalResource() {
		LexicalResource resource = new LexicalResource();
		GlobalInformation glInformation = new GlobalInformation();
		glInformation.setLabel("Wiktionary English edition, dump of 20100403, JWKTL 0.16.1");
		resource.setGlobalInformation(glInformation);
		resource.setName("WiktionaryEN");
		resource.setDtdVersion(dtd_version);
		return resource;
	}

	@Override
	protected Lexicon createNextLexicon() {
		if(!entryIterator.hasNext()/* || currentEntryNr > 1000*/) {
			return null;
		}
		lexicon = new Lexicon();
		String lmfLang = WiktionaryLMFMap.mapLanguage(wktLang);
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWkt" + lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("Wiktionary_eng");
		return lexicon;
	}

}
