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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBTransformer;
import de.tudarmstadt.ukp.lmf.transform.StringUtils;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabel;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelLMFMap;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelLoader;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelType;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryRelation;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionarySense;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryTranslation;
import de.tudarmstadt.ukp.wiktionary.api.Language;
import de.tudarmstadt.ukp.wiktionary.api.Quotation;
import de.tudarmstadt.ukp.wiktionary.api.WikiString;
import de.tudarmstadt.ukp.wiktionary.api.entry.Pronunciation;
import de.tudarmstadt.ukp.wiktionary.api.entry.WiktionaryIterator;
import de.tudarmstadt.ukp.wiktionary.api.entry.Pronunciation.PronunciationType;

/**
 * Base class for converting Wiktionary to LMF
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public abstract class WiktionaryLMFTransformer extends LMFDBTransformer {

	protected final IWiktionaryEdition wkt;		 						// JWKTL Wiktionary object
	protected final Language wktLang;									// Language of Wiktionary edition that should be transformed
	protected final WiktionaryIterator<IWiktionaryEntry> entryIterator;	// JWKTL Entry iterator
	protected int currentEntryNr = 0;								// Current entry number

	protected WiktionaryLabelLoader labelLoader;					// Loader of Wiktionary context labels
	protected final HashMap<String, List<WordForm>> wordForms;			// Cache of unsaved word forms, that were extracted
																// from Wiktionary FORM_OF context labels
	protected final String dtd_version;

	static int exampleIdx = 1;

	/**
	 * @param dbConfig - Database configuration of LMF database
	 * @param wkt - JWKTL Wiktionary Object
	 * @throws FileNotFoundException
	 */
	public WiktionaryLMFTransformer(DBConfig dbConfig, IWiktionaryEdition wkt, Language wktLang, String dtd) throws FileNotFoundException{
		super(dbConfig);
		this.wkt = wkt;
		this.wktLang = wktLang;
		this.entryIterator = wkt.getAllEntries();
		this.wordForms = new HashMap<String, List<WordForm>>();
		dtd_version = dtd;
	}

	/**
	 * @param labelLoader Loader of Wiktionary context labels
	 */
	public void setLabelLoader(WiktionaryLabelLoader labelLoader){
		this.labelLoader = labelLoader;
	}

	@Override
	protected LexicalEntry getNextLexicalEntry() {
		if(currentEntryNr % 1000 == 0) {
			System.out.println("PROCESSED "+currentEntryNr +" ENTRIES");
		}

		IWiktionaryEntry wktEntry = null;
		while(entryIterator.hasNext()){
			wktEntry = entryIterator.next();
			if(wktEntry.getWordLanguage().equals(wktLang)) {
				break;
			}
		}

		if(wktEntry == null/* || currentEntryNr > 1000*/) {
			return null;
		}

		LexicalEntry entry = new LexicalEntry();
		entry.setId(getLmfId(LexicalEntry.class, getEntryId(wktEntry))); // Create Lexical Entry
		String word = StringUtils.replaceNonUtf8(wktEntry.getWord(), 1000);
		Lemma lemma = new Lemma(); 					// Create Lemma

		List<FormRepresentation> formReps = new ArrayList<FormRepresentation>();

		if(wktEntry.getPronunciations() != null){  // Load pronunciation if there are any
			for(Pronunciation pronunciation : wktEntry.getPronunciations()){
				if(pronunciation.getType().equals(PronunciationType.IPA)){ // Only save IPA pronunciations
//					System.out.println("FOUND IPA");
					FormRepresentation formRep = new FormRepresentation();
					formRep.setWrittenForm(word);	// Create for each Pronunciation separate FormRepresentation
					formRep.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
					formRep.setPhoneticForm(pronunciation.getText());
					formReps.add(formRep);
				}
//				else {
//					System.out.println("FOUND "+pronunciation.getType());
//				}
			}
		}

		if(formReps.isEmpty()){		// No Pronunciations were found -> No FormRepresentations were created
			FormRepresentation formRep = new FormRepresentation();
			formRep.setWrittenForm(word);
			formRep.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
			formReps.add(formRep);
		}
		lemma.setFormRepresentations(formReps);	// Save FormRepresentations
		entry.setLemma(lemma);					// Save Lemma

		entry.setPartOfSpeech(WiktionaryLMFMap.mapPos(wktEntry.getPartOfSpeech())); // Set POS

		List<Sense> senses = new ArrayList<Sense>(); // Create Senses
		for(IWiktionarySense wktSense : wktEntry.getSenses()){
			if(wktSense.getGloss() == null) {
				continue;
			}
			Sense sense = wktSenseToLMFSense(wktSense, wktEntry, entry);
			senses.add(sense);		// Save Sense
		}
		entry.setSenses(senses);	 // Save Senses
		currentEntryNr ++;
		return entry;
	}

	/**
	 * Converts Wiktionary Sense to LMF Sense
	 * @param wktSense
	 * @param wktEntry
	 * @return
	 */
	private Sense wktSenseToLMFSense(IWiktionarySense wktSense, IWiktionaryEntry wktEntry, LexicalEntry entry){

		Sense sense = new Sense();				// Create Sense
		sense.setId(getLmfId(Sense.class, getSenseId(wktSense)));
		sense.setIndex(wktSense.getIndex());

		MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
		monolingualExternalRef.setExternalSystem("Wiktionary sense key");
		monolingualExternalRef.setExternalReference(wktSense.getKey());
		List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
		monolingualExternalRefs.add(monolingualExternalRef);
		sense.setMonolingualExternalRefs(monolingualExternalRefs);


		List<Definition> definitions = new ArrayList<Definition>(); // Create Definitions
		Definition definition = new Definition();
		definition.setDefinitionType(EDefinitionType.intensionalDefinition);

		List<TextRepresentation> textReps = new ArrayList<TextRepresentation>();	// Create TextRepresentations
		TextRepresentation textRep = new TextRepresentation();
		textRep.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getEntryLanguage()));
		textRep.setWrittenText(StringUtils.replaceNonUtf8(wktSense.getGloss().getPlainText()));

		saveLabels(sense, wktSense, entry, wktEntry);

		textReps.add(textRep);
		definition.setTextRepresentations(textReps);			// Save TextRepresentations
		definitions.add(definition);
		sense.setDefinitions(definitions);			// Save Definitions

		List<SenseExample> examples = new ArrayList<SenseExample>(); // Create Statements
		if(wktSense.getExamples() != null){
			for(WikiString example : wktSense.getExamples()){	// Save examples as statements of type EStatementType.example
				SenseExample senseExample = new SenseExample();
				senseExample.setId(getResourceAlias() + "_SenseExample_" + (exampleIdx++));
				senseExample.setExampleType(EExampleType.senseInstance);
				List<TextRepresentation> textRepsEx = new ArrayList<TextRepresentation>();
				TextRepresentation textRepEx = new TextRepresentation();
				textRepEx.setWrittenText(StringUtils.replaceNonUtf8(example.getPlainText()));
				textRepEx.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				textRepsEx.add(textRepEx);
				senseExample.setTextRepresentations(textRepsEx);
				examples.add(senseExample);
			}
		}
		sense.setSenseExamples(examples);

		List<Context> contexts = new ArrayList<Context>(); // Create Contexts
		if(wktSense.getQuotations() != null){
			for(Quotation quotation : wktSense.getQuotations()){	// Save quotations as contexts of type EContextType.citation
				Context context = new Context();
				context.setContextType(EContextType.citation);

				List<TextRepresentation> textRepsContext = new ArrayList<TextRepresentation>();
				TextRepresentation textRepContext = new TextRepresentation();
				StringBuilder writtenText = new StringBuilder();
				for (WikiString line : quotation.getLines()) {
					writtenText.append(writtenText.length() == 0 ? "" : " ")
							.append(line.getPlainText());
				}
				textRepContext.setWrittenText(StringUtils.replaceNonUtf8(writtenText.toString()));
				textRepContext.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				textRepsContext.add(textRepContext);
				context.setTextRepresentations(textRepsContext);
				context.setSource(quotation.getSource().getPlainText());
				contexts.add(context);
			}
		}
		sense.setContexts(contexts);	// Save Contexts

		List<SenseRelation> senseRelations = new ArrayList<SenseRelation>(); // Create SenseRelations
		if(wktSense.getRelations() != null){
			for(IWiktionaryRelation wktRelation : wktSense.getRelations()){
				if(wktRelation.getRelationType() == null || wktRelation.getTarget().isEmpty()) {
					continue;
				}

				SenseRelation senseRelation = new SenseRelation();
				senseRelation.setRelType(WiktionaryLMFMap.mapRelationType(wktRelation.getRelationType()));
				senseRelation.setRelName(wktRelation.getRelationType().toString());
				if(senseRelation.getRelType() == null) {
					continue;
				}

				FormRepresentation targetFormRepresentation = new FormRepresentation();
				targetFormRepresentation.setWrittenForm(StringUtils.replaceNonUtf8(wktRelation.getTarget(),1000));
				targetFormRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				senseRelation.setFormRepresentation(targetFormRepresentation); // Save target word as targetFormRepresentation
				senseRelations.add(senseRelation);
			}
		}

		if(wktSense.getTranslations() !=null ){			// Save translations as SenseRelations of type ERelTypeSemantics.translation
			for(IWiktionaryTranslation trans : wktSense.getTranslations()){
				String targetForm = StringUtils.replaceNonUtf8(trans.getTranslation(),1000);
				if (targetForm == null || targetForm.isEmpty()) {
					continue; // Do not save empty translations.
				}

				SenseRelation senseRelation = new SenseRelation();
				FormRepresentation targetFormRepresentation = new FormRepresentation();
				targetFormRepresentation.setWrittenForm(targetForm);
				targetFormRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(trans.getLanguage()));
				senseRelation.setRelName(WiktionaryLMFMap.mapLanguage(trans.getLanguage())+"Translation");
				senseRelation.setRelType(ERelTypeSemantics.translation);
				senseRelation.setFormRepresentation(targetFormRepresentation);
				senseRelations.add(senseRelation);
			}
		}
		sense.setSenseRelations(senseRelations); // Save SenseRelations
		return sense;
	}

	protected void saveLabels(Sense sense, IWiktionarySense wktSense,
			LexicalEntry entry, IWiktionaryEntry wktEntry) {}

	/**
	 * Extracts labels from the gloss and saves them to various LMF elements
	 * @param gloss
	 * @param sense
	 * @param entry
	 */
	protected void saveLabels(WikiString gloss, Sense sense, LexicalEntry entry, IWiktionaryEntry wktEntry){
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
						&& targetEntry.getEntryLanguage().equals(wktEntry.getEntryLanguage())){

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

			}else{	// Save all other context labels as SubjectFields
				//SubjectField subjectField = WiktionaryLabelLMFMap.labelToSubjectField(label);
				//subjectFields.add(subjectField);
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

	@Override
	protected Synset getNextSynset() { return null;}

	@Override
	protected ConstraintSet getNextConstraintSet() { return null;}

	@Override
	protected SemanticPredicate getNextSemanticPredicate() { return null;}

	@Override
	protected SenseAxis getNextSenseAxis() { return null;}

	@Override
	protected SubcategorizationFrame getNextSubcategorizationFrame() { return null;}

	@Override
	protected SubcategorizationFrameSet getNextSubcategorizationFrameSet() {return null;}

	@Override
	protected SynSemCorrespondence getNextSynSemCorrespondence() { return null;}


	@Override
	protected void finish(){
		int size = wordForms.size();
		System.out.println("Finishing WORD FORMS... "+size);
		for(String entryId : wordForms.keySet()){	// Save all unsaved word froms from the cache
			if(size % 1000 == 0) {
				System.out.println("SAVING WORD FORMS: "+size+" LEFT");
			}

			LexicalEntry lexEntry = (LexicalEntry)getLmfObjectById(LexicalEntry.class, entryId);
			if(lexEntry != null){
				if(lexEntry.getWordForms() == null) {
					lexEntry.setWordForms(wordForms.get(entryId));
				}
				else {
					lexEntry.getWordForms().addAll(wordForms.get(entryId));
				}
				saveList(lexEntry, lexEntry.getWordForms()); // Save word forms and update lexEntry
			}
			size--;
		}
	}
	/**
	 * Returns unique entry ID for a WiktionaryEntry
	 * @param entry
	 * @return
	 */
	private String getEntryId(IWiktionaryEntry entry){
		return "e"+entry.getPageId()+"_"+entry.getIndex();
	}

	/**
	 * Returns unique sense ID for a WiktionarySense
	 * @param sense
	 * @return
	 */
	private String getSenseId(IWiktionarySense sense){
		return "s"+sense.getPage().getId()+"_"
		   + sense.getEntry().getIndex()+"_"+sense.getIndex();
	}


}

