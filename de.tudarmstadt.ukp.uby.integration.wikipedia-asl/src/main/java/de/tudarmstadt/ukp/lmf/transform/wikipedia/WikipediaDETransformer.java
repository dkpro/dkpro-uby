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
package de.tudarmstadt.ukp.lmf.transform.wikipedia;

import java.io.FileNotFoundException;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * Converts the German Wikipedia edition to LMF.
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
*/
public class WikipediaDETransformer extends WikipediaLMFTransformer {

	public WikipediaDETransformer(final DBConfig dbConfig,
			final Wikipedia wiki, final String dtd) throws WikiApiException, FileNotFoundException {
		super(dbConfig, wiki, dtd);
	}

	@Override
	protected String getHiddenCategoryName() {
		return "Versteckte Kategorie";
	}

	@Override
	protected String getResourceAlias() {
		return "WikiDE";
	}

	@Override
	protected LexicalResource createLexicalResource() {
		LexicalResource resource = new LexicalResource();
		GlobalInformation glInformation = new GlobalInformation();
		glInformation.setLabel("LMF Representation of Wikipedia, wikiapi_de_20090618");
		resource.setGlobalInformation(glInformation);
		resource.setName("WikipediaDE");
		resource.setDtdVersion(dtd_version);
		return resource;
	}

	@Override
	protected Lexicon createNextLexicon() {
		if(!pageIterator.hasNext() /*|| currentEntryNr > 100*/) {
			return null;
		}
		Lexicon lexicon = new Lexicon();
		ELanguageIdentifier lmfLang = WikipediaLMFMap.mapLanguage(wiki.getLanguage());
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWiki"+lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("WikipediaDE");
		return lexicon;
	}

	@Override
	protected boolean isDiscussionPage(final String pageTitle) {
		if (pageTitle.startsWith("Discussion:")) {
			return true;
		}
		else {
			return pageTitle.startsWith("Diskussion:");
		}
	}

}
