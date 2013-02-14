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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabel;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelLMFMap;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelType;
import de.tudarmstadt.ukp.wiktionary.api.IWikiString;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionarySense;
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
			final String dtd) throws FileNotFoundException {
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
		glInformation.setLabel("Wiktionary English edition, dump of 20100403, JWKTL 0.15.2");
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
		Lexicon lexicon = new Lexicon();
		String lmfLang = WiktionaryLMFMap.mapLanguage(wktLang);
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWkt" + lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("WiktionaryEN");
		return lexicon;
	}
	
	/*@Override
	protected boolean considerSense(final IWiktionarySense wktSense) {
		// TODO: JWKTL 0.15.4 still associates those three senses with a 
		//   wrong language code. Should be fixed in the next release.
		String entryId = wktSense.getEntry().getKey();
		if ("290974:1".equals(entryId) 
				|| "482476:1".equals(entryId)
				|| "97982:2".equals(entryId))
			return false;
		else
			return super.considerSense(wktSense);
	}*/

	@Override
	protected void saveLabels(Sense sense, IWiktionarySense wktSense,
			LexicalEntry entry, IWiktionaryEntry wktEntry) {
		IWikiString gloss = wktSense.getGloss();
		saveLabels(gloss, sense, entry, wktEntry);						// Convert context labels and update LMF entry
	}

	/**
	 * Extracts labels from the gloss and saves them to various LMF elements
	 * @param gloss
	 * @param sense
	 * @param entry
	 */
	@Deprecated
	protected void saveLabels(IWikiString gloss, Sense sense, LexicalEntry entry, IWiktionaryEntry wktEntry){
		if(labelLoader == null) {
			return;
		}

		List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
		for(WiktionaryLabel label : labelLoader.getLabels(gloss)){
			WiktionaryLabelType labelType = label.getLabelType();
			if(labelType.equals(WiktionaryLabelType.FORM_OF)){			// FORM_OF labels --> Word forms

				WordForm wordForm = WiktionaryLabelLMFMap.formOfToWordForm(label, wktEntry.getWord());
				String targetWord = label.getParameterByName("1");		// Target word is in the first parameter of the label template

				for(IWiktionaryEntry targetEntry: wkt.getEntriesForWord(targetWord)){
					if(targetEntry.getPartOfSpeech().equals(wktEntry.getPartOfSpeech()) // Only entries with the same POS and language
						&& targetEntry.getPage().getEntryLanguage().equals(wktEntry.getPage().getEntryLanguage())){

						String entryId = getLmfId(LexicalEntry.class, getEntryId(targetEntry));
						LexicalEntry lexEntry = (LexicalEntry)getLmfObjectById(LexicalEntry.class, entryId);

						if(lexEntry != null){ // If entry already exists then save directly to it

							if(lexEntry.getWordForms() != null){	// If the entry already has other word forms,
								lexEntry.getWordForms().add(wordForm); // then add this word form to them
							}else{
								List<WordForm> wordForms = new ArrayList<WordForm>();
								wordForms.add(wordForm);
								lexEntry.setWordForms(wordForms);
							}
							saveList(lexEntry, lexEntry.getWordForms()); // Save word forms and update lexEntry

						}else{				// If lexical entry does not yet exist then save wordForms temporarily
							if(wordForms.containsKey(entryId)){
								wordForms.get(entryId).add(wordForm);
							}else{
								List<WordForm> temp = new ArrayList<WordForm>();
								temp.add(wordForm);
								wordForms.put(entryId, temp);
							}
						}
					}
				}
			//}else if(labelType.equals(WiktionaryLabelType.GRAMMATICAL)){

			} else {
				// Save all other context labels as SemanticLabel.
				SemanticLabel semanticLabel = WiktionaryLabelLMFMap.labelToSemanticLabel(label);
				semanticLabels.add(semanticLabel);
			}
		}
		sense.setSemanticLabels(semanticLabels);

		if(wordForms.containsKey(entry.getId())){ // We are currently in an entry,
												  // for which some unsaved word forms exist
												  // --> save them and delete from cache
			entry.setWordForms(wordForms.get(entry.getId()));
			wordForms.remove(entry.getId());
		}
	}
	
}
