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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBTransformer;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.PageIterator;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * Converts Wikipedia to the LMF model and saves it to the database
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
*/
public abstract class WikipediaLMFTransformer extends LMFDBTransformer {

	protected final Wikipedia wiki;			     // JWPL Wikipedia object
	protected final Iterator<Page> pageIterator; // Page iterator
	protected int currentEntryNr;			 // Number of the entries(pages) that were already transformed
	protected final Set<Integer> categoryBlackList; // List of categories that should not be saved as SubjectFields
	protected final MediaWikiParser mediaWikiParser; // Parser needed for parsing of Wikipedia pages
	protected final String dtd_version;

	/**
	 * @param dbConfig
	 * @param wiki
	 * @throws WikiApiException
	 * @throws FileNotFoundException
	 */
	public WikipediaLMFTransformer(DBConfig dbConfig, Wikipedia wiki, String dtd) throws WikiApiException, FileNotFoundException {
		super(dbConfig);
		this.wiki = wiki;
		this.pageIterator = new PageIterator(wiki, true, 7000);
		this.categoryBlackList = wiki.getCategory(getHiddenCategoryName()).getChildrenIDs();
		this.currentEntryNr = 0;
		MediaWikiParserFactory pf = new MediaWikiParserFactory();		// Parse with MediaWikiParser
		pf.setCalculateSrcSpans(false);
		pf.setTemplateParserClass(FlushTemplates.class);
		mediaWikiParser = pf.createParser();
		dtd_version = dtd;
	}

	protected abstract String getHiddenCategoryName();

	@Override
	protected abstract String getResourceAlias();

	@Override
	protected abstract LexicalResource createLexicalResource();

	@Override
	protected Lexicon createNextLexicon() {
		if(!pageIterator.hasNext() /*|| currentEntryNr > 100*/) {
			return null;
		}

		Lexicon lexicon = new Lexicon();
		ELanguageIdentifier lmfLang = WikipediaLMFMap.mapLanguage(wiki.getLanguage());
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWiki"+lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("Wikipedia");
		return lexicon;
	}

	@Override
	protected LexicalEntry getNextLexicalEntry() {

		if(currentEntryNr % 1000 == 0) {
			System.out.println("PROCESSED "+currentEntryNr +" ENTRIES");
		}

	/*	if(currentEntryNr > 100)
			return null;*/
		while(pageIterator.hasNext()){	// Iterate over pages, skip pages that should not be saved
			String pageTitle = "";
			Page page = null;
			try{
				page = pageIterator.next();
				if(page.isDisambiguation() || page.isRedirect()	// Skip redirect, disambiguation and discussion pages
						|| isDiscussionPage(page.getTitle().getPlainTitle())) {
					continue;
				}

				boolean alreadyExists = true;	// If true there was already an entry created for this page
				ELanguageIdentifier wikiLmfLang = WikipediaLMFMap.mapLanguage(wiki.getLanguage());
				pageTitle = page.getTitle().getPlainTitle();
				String name = page.getTitle().getEntity();
				String id = "Art"+name;
				LexicalEntry lexEntry = null;
				String entryId;
				if(idMapping.containsKey(id)){ // If the entry(page without the disambiguation tag)
											   // for this page has been already created,
					entryId = getLmfId(LexicalEntry.class, id);	 // get it from the database
					lexEntry = (LexicalEntry)getLmfObjectById(LexicalEntry.class, entryId);
				}
				else {
					entryId = getLmfId(LexicalEntry.class, id);
				}

				if(lexEntry == null){		// No entry was created-->create new one
					alreadyExists = false;
					lexEntry = new LexicalEntry();
					lexEntry.setId(entryId);
					Lemma lemma = new Lemma();
					lemma.setFormRepresentations(new ArrayList<FormRepresentation>());
					FormRepresentation formRep = new FormRepresentation();
					formRep.setLanguageIdentifier(wikiLmfLang);
					formRep.setWrittenForm(name);
					lemma.getFormRepresentations().add(formRep);
					lexEntry.setLemma(lemma);
					lexEntry.setSenses(new ArrayList<Sense>());
					lexEntry.setPartOfSpeech(EPartOfSpeech.noun);
				}

				Sense sense = new Sense();
				sense.setId(getLmfId(Sense.class, String.valueOf(page.getPageId())));

				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				monolingualExternalRef.setExternalSystem("Wikipedia article title");
				monolingualExternalRef.setExternalReference(pageTitle);
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				sense.setMonolingualExternalRefs(monolingualExternalRefs);


				sense.setDefinitions(new ArrayList<Definition>());
				String pageText = page.getText(); // Parse the page, take only first 10000 characters to improve performance
				ParsedPage ppage = mediaWikiParser.parse(pageText.substring(0, Math.min(10000, pageText.length())));

				if(ppage.getFirstParagraph() == null) {
					continue;
				}

				String text = ppage.getFirstParagraph().getText();

				Definition definition = new Definition();	// Save first paragraph text as definition text
				definition.setDefinitionType(EDefinitionType.intensionalDefinition);
				definition.setTextRepresentations(new ArrayList<TextRepresentation>());
				TextRepresentation textRep = new TextRepresentation();
				textRep.setLanguageIdentifier(wikiLmfLang);
				textRep.setWrittenText(text);
				definition.getTextRepresentations().add(textRep);
				sense.getDefinitions().add(definition);

				sense.setSenseRelations(new ArrayList<SenseRelation>());
				for(String redirect : page.getRedirects()){ // Save redirects as SenseRelations, relType=association
					if (isDiscussionPage(redirect)) {
						continue;
					}
					SenseRelation senseRelation = new SenseRelation();
					FormRepresentation targetForm = new FormRepresentation();
					targetForm.setLanguageIdentifier(wikiLmfLang);
					targetForm.setWrittenForm(redirect);
					senseRelation.setTargetFormRepresentation(targetForm);
					senseRelation.setRelName("redirect");
					senseRelation.setRelType(ERelTypeSemantics.association);
					sense.getSenseRelations().add(senseRelation);
				}

				sense.setSemanticLabels(new ArrayList<SemanticLabel>()); // Save categories as SubjectFields
				for(Category category : page.getCategories()){
					if(categoryBlackList.contains(category.getPageId())) {
						continue;
					}

					SemanticLabel semanticLabel = new SemanticLabel();
					semanticLabel.setType("WikipediaCategory");
					//subjectField.setRegisterType(ERegisterType.usage);
					//subjectField.setSubjectField(category.getTitle().getPlainTitle());
					semanticLabel.setLabel(category.getTitle().getPlainTitle());
					sense.getSemanticLabels().add(semanticLabel);
				}

				lexEntry.getSenses().add(sense);
				currentEntryNr++;
				if(alreadyExists){		// If the entry already exists, then only add sense to it, and continue to the next one
					saveList(lexEntry, lexEntry.getSenses());
				}
				else {
					return lexEntry;  // If the entry does not exist, return it
				}

			}catch (Exception ex){
				System.err.println("Error while transforming '"+pageTitle+"': "
						+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		return null;
	}

	protected abstract boolean isDiscussionPage(final String pageTitle);

	@Override
	protected void finish() {}

	@Override
	protected ConstraintSet getNextConstraintSet() {return null;}
	@Override
	protected SemanticPredicate getNextSemanticPredicate() {return null;}
	@Override
	protected SenseAxis getNextSenseAxis() {return null;}
	@Override
	protected SubcategorizationFrame getNextSubcategorizationFrame() {return null;}
	@Override
	protected SubcategorizationFrameSet getNextSubcategorizationFrameSet() {return null;}
	@Override
	protected SynSemCorrespondence getNextSynSemCorrespondence() {return null;}
	@Override
	protected Synset getNextSynset() {return null;}


}
