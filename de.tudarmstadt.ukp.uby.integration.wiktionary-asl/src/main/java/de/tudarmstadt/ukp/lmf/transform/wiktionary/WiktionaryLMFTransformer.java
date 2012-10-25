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
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.Statement;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.mrd.Equivalent;
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
import de.tudarmstadt.ukp.lmf.transform.wiktionary.TemplateParser.EtymologyTemplateHandler;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabel;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelLMFMap;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelLoader;
import de.tudarmstadt.ukp.lmf.transform.wiktionary.labels.WiktionaryLabelType;
import de.tudarmstadt.ukp.wiktionary.api.IPronunciation;
import de.tudarmstadt.ukp.wiktionary.api.IPronunciation.PronunciationType;
import de.tudarmstadt.ukp.wiktionary.api.IWikiString;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryRelation;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionarySense;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryTranslation;
import de.tudarmstadt.ukp.wiktionary.api.Quotation;
import de.tudarmstadt.ukp.wiktionary.api.entry.WiktionaryIterator;
import de.tudarmstadt.ukp.wiktionary.api.util.ILanguage;

/**
 * Base class for converting Wiktionary to LMF
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public abstract class WiktionaryLMFTransformer extends LMFDBTransformer {

	// JWKTL Wiktionary object
	protected final IWiktionaryEdition wkt;		 						
	// Language of Wiktionary edition that should be transformed
	protected final ILanguage wktLang;									
	// JWKTL Entry iterator
	protected final WiktionaryIterator<IWiktionaryEntry> entryIterator;
	// Current entry number
	protected int currentEntryNr = 0;								

	// Loader of Wiktionary context labels
	protected WiktionaryLabelLoader labelLoader;					
	// Cache of unsaved word forms, that were extracted from Wiktionary FORM_OF context labels
	protected final HashMap<String, List<WordForm>> wordForms;			
	
	protected final String dtd_version;

	static int exampleIdx = 1;

	/**
	 * @param dbConfig - Database configuration of LMF database
	 * @param wkt - JWKTL Wiktionary Object
	 * @throws FileNotFoundException
	 */
	public WiktionaryLMFTransformer(final DBConfig dbConfig, 
			final IWiktionaryEdition wkt, final ILanguage wktLang, 
			final String dtd) throws FileNotFoundException{
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
		if (currentEntryNr % 1000 == 0) {
			System.out.println("PROCESSED " + currentEntryNr + " ENTRIES");
		}
		
		IWiktionaryEntry wktEntry = null;
		while (entryIterator.hasNext()){
			wktEntry = entryIterator.next();
			if (wktLang.equals(wktEntry.getWordLanguage())) {
				break;
			}
		}

		if (wktEntry == null/* || currentEntryNr > 1000*/) {
			return null;
		}

		// Lexical entry.
		LexicalEntry entry = new LexicalEntry();
		entry.setId(getLmfId(LexicalEntry.class, getEntryId(wktEntry)));		
		EPartOfSpeech pos = WiktionaryLMFMap.mapPos(wktEntry);
		entry.setPartOfSpeech(pos);
		
		// Lemma
		String word = convert(wktEntry.getWord(), 1000);
		Lemma lemma = new Lemma();
		lemma.setFormRepresentations(createFormRepresentationList(word, wktEntry.getWordLanguage()));
		entry.setLemma(lemma);

		// Senses.
		List<Sense> senses = new ArrayList<Sense>();
		for (IWiktionarySense wktSense : wktEntry.getSenses()) {
			if (considerSense(wktSense)) {
				Sense sense = wktSenseToLMFSense(wktSense, wktEntry, entry);
				senses.add(sense);
			}
		}
		entry.setSenses(senses);
		
		// Related forms.
		List<IWiktionaryRelation> relations = wktEntry.getRelations();
		if (relations != null) {
			List<RelatedForm> relatedForms = new ArrayList<RelatedForm>();
			for (IWiktionaryRelation relation : relations) {
				ERelTypeMorphology relType = WiktionaryLMFMap.mapMorphologicalRelation(relation.getRelationType());
				if (relType == null)
					continue;

				RelatedForm form = new RelatedForm();
				form.setRelType(relType);
				form.setFormRepresentations(createFormRepresentationList(relation.getTarget(), wktEntry.getWordLanguage()));
				relatedForms.add(form);
			}
			entry.setRelatedForms(relatedForms);
		}
		
		// Word forms: Pronunciations.
		List<IPronunciation> pronunciations = wktEntry.getPronunciations(); 
		if (pronunciations != null) {
			List<WordForm> wordForms = new ArrayList<WordForm>();
			for (IPronunciation pronunciation : pronunciations) {
				// Only save IPA pronunciations
				if (pronunciation.getType() != PronunciationType.IPA)
					continue;
					
				// Don't save empty pronunciations.
				String text = pronunciation.getText();
				if (text == null || "".equals(text)
						|| "...".equals(text) || "…".equals(text))
					continue;
					
				WordForm wordForm = new WordForm();
				List<FormRepresentation> formRep = createFormRepresentationList(
						wktEntry.getWord(), wktEntry.getWordLanguage()); //TODO: merge with inflection table result!
				formRep.get(0).setPhoneticForm(pronunciation.getText());
				wordForm.setFormRepresentations(formRep);
				if ("Pl.".equals(pronunciation.getNote())
						|| "Pl.1".equals(pronunciation.getNote())
						|| "Pl.2".equals(pronunciation.getNote()))
					wordForm.setGrammaticalNumber(EGrammaticalNumber.plural);
				if ("Gen.".equals(pronunciation.getNote()))
					wordForm.setCase(ECase.genitive);
				else
				if ("Dat.".equals(pronunciation.getNote()))
					wordForm.setCase(ECase.dative);
				else
				if ("Akk.".equals(pronunciation.getNote()))
					wordForm.setCase(ECase.accusative);
				if ("Prät.".equals(pronunciation.getNote()))
					wordForm.setTense(ETense.past);
				if ("Komp.".equals(pronunciation.getNote()))
					wordForm.setDegree(EDegree.COMPARATIVE);
				else
				if ("Sup.".equals(pronunciation.getNote()))
					wordForm.setDegree(EDegree.SUPERLATIVE);
				if ("Part.".equals(pronunciation.getNote()))
					wordForm.setVerbFormMood(EVerbFormMood.participle);					
				wordForms.add(wordForm);
			}
			entry.setWordForms(wordForms);
		}		
		
		currentEntryNr++;
		return entry;
	}

	/** Returns true if this sense should be used for the UBY database. */
	protected boolean considerSense(final IWiktionarySense wktSense) {
		return (wktSense.getGloss() != null);
	}

	/**
	 * Converts Wiktionary Sense to LMF Sense
	 * @param wktSense
	 * @param wktEntry
	 * @return
	 */
	private Sense wktSenseToLMFSense(IWiktionarySense wktSense, IWiktionaryEntry wktEntry, LexicalEntry entry){
		// Sense and identifier.
		Sense sense = new Sense();
		sense.setId(getLmfId(Sense.class, getSenseId(wktSense)));
		sense.setIndex(wktSense.getIndex());

		// Monolingual external reference.
		MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
		monolingualExternalRef.setExternalSystem("Wiktionary sense key");
		monolingualExternalRef.setExternalReference(wktSense.getKey());
		List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
		monolingualExternalRefs.add(monolingualExternalRef);
		sense.setMonolingualExternalRefs(monolingualExternalRefs);

		// Sense Definition (Definition class; type intensionalDefinition).
		List<Definition> definitions = new ArrayList<Definition>();
		Definition definition = new Definition();
		definition.setDefinitionType(EDefinitionType.intensionalDefinition);
		definition.setTextRepresentations(createTextRepresentationList(
				wktSense.getGloss().getPlainText(), wktEntry.getPage().getEntryLanguage()
		));
		definitions.add(definition);
		sense.setDefinitions(definitions);

		// Semantic Labels.
		saveLabels(sense, wktSense, entry, wktEntry);

		// Etymology (Statement class; type etymology).
		IWikiString etymology = null;
		if (wktEntry.getWordEtymology() != null)
			etymology = wktEntry.getWordEtymology();
		
		if (etymology != null && definition != null) {
			List<Statement> statements = new LinkedList<Statement>();
			Statement statement = new Statement();
			statement.setStatementType(EStatementType.etymology);
			statement.setTextRepresentations(createTextRepresentationList(
					convertEtymology(etymology), wktEntry.getPage().getEntryLanguage()));
			statements.add(statement);
			definition.setStatements(statements);
		}
	
		// Sense examples (SenseExample class; type senseInstance).
		List<SenseExample> examples = new ArrayList<SenseExample>();
		if (wktSense.getExamples() != null) {
			for (IWikiString example : wktSense.getExamples()) {
				SenseExample senseExample = new SenseExample();
				senseExample.setId(getResourceAlias() + "_SenseExample_" + (exampleIdx++));
				senseExample.setExampleType(EExampleType.senseInstance);
				senseExample.setTextRepresentations(createTextRepresentationList(
						example.getPlainText(), wktEntry.getWordLanguage()
				));
				examples.add(senseExample);
			}
		}
		sense.setSenseExamples(examples);

		// Quotations (Context class; type citation).
		List<Context> contexts = new ArrayList<Context>();
		if (wktSense.getQuotations() != null) {
			for (Quotation quotation : wktSense.getQuotations()) {
				Context context = new Context();
				context.setContextType(EContextType.citation);
				StringBuilder quotationText = new StringBuilder();
				for (IWikiString line : quotation.getLines()) {
					quotationText.append(quotationText.length() == 0 ? "" : " ")
							.append(line.getPlainText());
				}
				context.setTextRepresentations(createTextRepresentationList(
						quotationText.toString(), wktEntry.getWordLanguage()
				));
				context.setSource(quotation.getSource().getPlainText());
				contexts.add(context);
			}
		}
		sense.setContexts(contexts);

		// Sense relations (SenseRelation class).
		List<SenseRelation> senseRelations = new ArrayList<SenseRelation>();
		if (wktSense.getRelations() != null) {
			for (IWiktionaryRelation wktRelation : wktSense.getRelations()) {
				if (wktRelation.getRelationType() == null || wktRelation.getTarget().isEmpty()) {
					continue;
				}

				SenseRelation senseRelation = new SenseRelation();
				senseRelation.setRelType(WiktionaryLMFMap.mapRelationType(wktRelation.getRelationType()));
				senseRelation.setRelName(WiktionaryLMFMap.mapRelationName(wktRelation.getRelationType()));
				if (senseRelation.getRelType() == null) {
					continue;
				}

				// Save target word as targetFormRepresentation.
				FormRepresentation targetFormRepresentation = new FormRepresentation();
				targetFormRepresentation.setWrittenForm(convert(wktRelation.getTarget(),1000));
				targetFormRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				senseRelation.setFormRepresentation(targetFormRepresentation); 
				senseRelations.add(senseRelation);
			}
		}
		sense.setSenseRelations(senseRelations);

		// Translations (Equivalent class).
		if (wktSense.getTranslations() != null) {
			List<Equivalent> equivalents = new ArrayList<Equivalent>();
			for (IWiktionaryTranslation trans : wktSense.getTranslations()) {
				String targetForm = convert(trans.getTranslation(), 255);
				if (targetForm == null || targetForm.isEmpty()) {
					continue; // Do not save empty translations.
				}
				String language = WiktionaryLMFMap.mapLanguage(trans.getLanguage());
				if (language == null) {
					continue; // Do not save translations to unknown languages.
				}
				
				String transliteration = trans.getTransliteration();
				if (transliteration != null) {
					transliteration = convert(transliteration, 255);					
				}
				String additionalInformation = trans.getAdditionalInformation();
				if (additionalInformation != null) {
					additionalInformation = additionalInformation.replace("{{m}}", "masculine");
					additionalInformation = additionalInformation.replace("{{f}}", "feminine");
					additionalInformation = additionalInformation.replace("{{n}}", "neuter");
					additionalInformation = convert(additionalInformation, 255);
				}
				
				Equivalent equivalent = new Equivalent();
				equivalent.setWrittenForm(targetForm);
				equivalent.setLanguageIdentifier(language);
				equivalent.setTransliteration(transliteration);
				equivalent.setUsage(additionalInformation);
				equivalents.add(equivalent);
			}
			sense.setEquivalents(equivalents);
		}
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

	protected List<TextRepresentation> createTextRepresentationList(
			final String writtenText, final ILanguage language) {
		List<TextRepresentation> result = new ArrayList<TextRepresentation>();
		TextRepresentation textRepresentation = new TextRepresentation();
		textRepresentation.setWrittenText(convert(writtenText));
		textRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(language));
		result.add(textRepresentation);
		return result;
	}
	
	protected List<FormRepresentation> createFormRepresentationList(
			final String writtenForm, final ILanguage language) {
		List<FormRepresentation> result = new ArrayList<FormRepresentation>();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setWrittenForm(writtenForm);
		formRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(language));
		result.add(formRepresentation);
		return result;
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
		return "e" + entry.getKey();
	}

	/**
	 * Returns unique sense ID for a WiktionarySense
	 * @param sense
	 * @return
	 */
	private String getSenseId(IWiktionarySense sense){
		return "s" + sense.getKey();
	}
	
	private static String convert(final String text) {
		return StringUtils.replaceNonUtf8(
				StringUtils.replaceHtmlEntities(text));
	}
	
	private static String convert(final String text, int maxLength) {
		return StringUtils.replaceNonUtf8(
				StringUtils.replaceHtmlEntities(text), maxLength);
	}

	protected static final Pattern COMMENT_PATTERN = Pattern.compile("<!--.+?-->");
	protected static final Pattern QUOTES_PATTERN = Pattern.compile("'''?");
	protected static final Pattern WIKILINK_PATTERN = Pattern.compile("\\[\\[((?:[^|\\]]+?\\|)*)([^|\\]]+?)\\]\\]");
	protected static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{.+?\\}\\}");
	protected static final Pattern REFERENCES_PATTERN = Pattern.compile("<ref[^>]*>.+?</ref>");
	protected static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");
	protected static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s\\s+");
	
	@Deprecated
	public static String makePlainText(final String wikiText) {
		String result = wikiText;
		result = result.replace("\t", " ");
		result = COMMENT_PATTERN.matcher(result).replaceAll("");	
		result = QUOTES_PATTERN.matcher(result).replaceAll("");
		result = WIKILINK_PATTERN.matcher(result).replaceAll("$2");
		result = REFERENCES_PATTERN.matcher(result).replaceAll("");
		result = TEMPLATE_PATTERN.matcher(result).replaceAll("");
		result = HTML_PATTERN.matcher(result).replaceAll("");
		result = result.replace("’", "'");
		result = result.replace("�", "'");
		result = result.replace("°", "");
		result = WHITESPACE_PATTERN.matcher(result).replaceAll(" ");
		while (result.length() > 0 && "*: ".contains(result.substring(0, 1)))
			result = result.substring(1);
		return result.trim();
	}
	
	protected String convertEtymology(final IWikiString etymology) {
		String result = TemplateParser.parse(etymology.getText(), new EtymologyTemplateHandler());
		return makePlainText(result);
	}

}
