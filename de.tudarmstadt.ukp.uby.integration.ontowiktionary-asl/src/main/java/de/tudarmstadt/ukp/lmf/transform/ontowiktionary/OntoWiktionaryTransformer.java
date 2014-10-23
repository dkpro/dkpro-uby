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
package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.tudarmstadt.ukp.jwktl.api.IPronunciation;
import de.tudarmstadt.ukp.jwktl.api.IPronunciation.PronunciationType;
import de.tudarmstadt.ukp.jwktl.api.IQuotation;
import de.tudarmstadt.ukp.jwktl.api.IWikiString;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryRelation;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryTranslation;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryWordForm;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.IWiktionaryIterator;
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser;
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser.EtymologyTemplateHandler;
import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.Statement;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
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
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBTransformer;
import de.tudarmstadt.ukp.lmf.transform.StringUtils;
import de.tudarmstadt.ukp.lmf.transform.ontowiktionary.WiktionaryLabelManager.PragmaticLabel;

/**
 * Base class for converting Wiktionary to LMF
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class OntoWiktionaryTransformer extends LMFDBTransformer {

	// The embracing lexicon instance.
	protected Lexicon lexicon;
	// JWKTL Wiktionary object
	protected final IWiktionaryEdition wkt;		 						
	// Language of Wiktionary edition that should be transformed
	protected final ILanguage wktLang;									
	// A string representation (YYYY-MM-DD) of the dump date.
	protected final String wktDate;
	
	// JWKTL Entry iterator
	protected final IWiktionaryIterator<IWiktionaryEntry> entryIterator;
	// Current entry number
	protected int currentEntryNr = 0;								
	// Handler for Wiktionary's pragmatic labels.
	protected WiktionaryLabelManager labelManager;
	// Cache of unsaved word forms defined by Wiktionary word form labels.
	protected final Map<String, List<WordForm>> wordForms;			
	// Cache of unsaved subcategorization frames.
	protected final List<SubcategorizationFrame> subcatFrames;			

	protected OntoWiktionary ontoWiktionary;
	protected Iterator<OntoWiktionaryConcept> synsetIter;

	protected final String jwktlVersion;
	protected final String dtd_version;
	

	static int exampleIdx = 1;
	static int subcatFrameIdx = 1;
	static int syntacticBehaviourIdx = 1;

	/**
	 * @param dbConfig - Database configuration of LMF database
	 * @param wkt - JWKTL Wiktionary Object
	 * @throws FileNotFoundException
	 */
	public OntoWiktionaryTransformer(final DBConfig dbConfig,
			final OntoWiktionary ontoWiktionary,
			final IWiktionaryEdition wkt, final ILanguage wktLang, 
			final String wktDate, final String dtd) throws IOException {
		super(dbConfig);
		this.ontoWiktionary = ontoWiktionary;
		this.wkt = wkt;
		this.wktLang = wktLang;
		this.wktDate = wktDate;
		this.entryIterator = wkt.getAllEntries();
		this.wordForms = new TreeMap<String, List<WordForm>>();
		this.subcatFrames = new LinkedList<SubcategorizationFrame>();
		this.labelManager = WiktionaryLMFMap.createLabelManager();
		jwktlVersion = /*JWKTL.getVersion() - version clash!*/ "1.0.0";
		dtd_version = dtd;
	}

	@Override
	protected String getResourceAlias() {
		return "OntoWkt" + wktLang.getISO639_1().toUpperCase();
	};

	@Override
	protected LexicalResource createLexicalResource() {
		LexicalResource resource = new LexicalResource();
		GlobalInformation glInformation = new GlobalInformation();
		glInformation.setLabel("OntoWiktionary " + wktLang.getName() 
				+ " edition, dump of 2013/02, JWKTL " 
				+ jwktlVersion);
		resource.setGlobalInformation(glInformation);
		resource.setName("OntoWiktionary" + wktLang.getISO639_1().toUpperCase());
		resource.setDtdVersion(dtd_version);
		return resource;
	}

	@Override
	protected Lexicon createNextLexicon() {
		if (lexicon != null)
			return null;

		lexicon = new Lexicon();
		String lmfLang = WiktionaryLMFMap.mapLanguage(wktLang);
		lexicon.setId(getLmfId(Lexicon.class, "lexiconWkt" + lmfLang));
		lexicon.setLanguageIdentifier(lmfLang);
		lexicon.setName("OntoWiktionary" + wktLang.getISO639_1().toUpperCase());
		return lexicon;
	}
	
	@Override
	protected LexicalEntry getNextLexicalEntry() {		
		// If we're finished, convert the semantic relations and free resources.
		//if (entryIterator != null){tx.rollback();session.close();session = sessionFactory.openSession();tx = session.beginTransaction();return null;}		
		//if (entryIterator != null){tx.rollback();session.close();session = sessionFactory.openSession();tx = session.beginTransaction();
		if (!entryIterator.hasNext()/* || currentEntryNr > 1000*/) {
			System.out.println("PROCESS SENSE RELATIONS");
			convertSemanticRelations();
			return null;
		}

		if (currentEntryNr % 1000 == 0) {
			System.out.println("PROCESSED " + currentEntryNr + " ENTRIES");
		}
		
		IWiktionaryEntry wktEntry = null;
		while (entryIterator.hasNext()){
			wktEntry = entryIterator.next();
			if (wktLang.equals(wktEntry.getWordLanguage()))
				break;
		}


		// Lexical entry.
		LexicalEntry entry = new LexicalEntry();
		entry.setId(getLmfId(LexicalEntry.class, getEntryId(wktEntry)));
		EPartOfSpeech pos = WiktionaryLMFMap.mapPos(wktEntry);
		entry.setPartOfSpeech(pos);
		
		// Lemma
		String word = wktEntry.getWord();
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
			wktSense = null;
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
		
		// Word forms.
		List<WordForm> wordForms = new ArrayList<WordForm>();
		
		
		// Word forms: Inflected forms.
		List<IWiktionaryWordForm> wktWordForms = wktEntry.getWordForms();
		if (wktWordForms != null) {
			for (IWiktionaryWordForm wktWordForm : wktWordForms) {
				WordForm wordForm = new WordForm();
				List<FormRepresentation> formRep = createFormRepresentationList(
						wktWordForm.getWordForm(), wktEntry.getWordLanguage());
				wordForm.setFormRepresentations(formRep);
				wordForm.setCase(WiktionaryLMFMap.mapCase(wktWordForm));
				wordForm.setDegree(WiktionaryLMFMap.mapDegree(wktWordForm));
				wordForm.setPerson(WiktionaryLMFMap.mapPerson(wktWordForm));
				wordForm.setGrammaticalNumber(WiktionaryLMFMap.mapGrammaticalNumber(wktWordForm));
				wordForm.setVerbFormMood(WiktionaryLMFMap.mapVerbFormMood(wktWordForm));
				wordForm.setTense(WiktionaryLMFMap.mapTense(wktWordForm));
				wordForms.add(wordForm);
			}
		}
		
		// Word forms: Pronunciations.
		List<IPronunciation> pronunciations = wktEntry.getPronunciations(); 
		if (pronunciations != null) {
			for (IPronunciation pronunciation : pronunciations) {
				// Only save IPA pronunciations
				if (pronunciation.getType() != PronunciationType.IPA)
					continue;
					
				// Don't save empty pronunciations.
				String text = pronunciation.getText();
				if (text == null || "".equals(text)
						|| "...".equals(text) || "…".equals(text))
					continue;
				
				String writtenForm = null; //TODO: merge with inflection table result!
				if (pronunciation.getNote() == null || pronunciation.getNote().isEmpty())
					writtenForm = wktEntry.getWord();
				WordForm wordForm = new WordForm();
				List<FormRepresentation> formRep = createFormRepresentationList(
						writtenForm, wktEntry.getWordLanguage());
				if (pronunciation.getText() != null && !pronunciation.getText().isEmpty())
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
				if ("Part.".equals(pronunciation.getNote())) {
					wordForm.setVerbFormMood(EVerbFormMood.participle);
					wordForm.setTense(ETense.past); // Partizip II
				}
				wordForms.add(wordForm);
			}
		}		

		if (!wordForms.isEmpty())
			entry.setWordForms(wordForms);
		
		wktEntry = null;
		
		currentEntryNr++;
		return entry;
	}

	protected void convertSemanticRelations() {
		int senseCount = 0;
		for (IWiktionaryEntry wktEntry : wkt.getAllEntries()) {
			if (!wktLang.equals(wktEntry.getWordLanguage()))
				continue;
			
			for (IWiktionarySense wktSense : wktEntry.getSenses()) {
				if (!considerSense(wktSense))
					continue;
				
				String sourceId = getLmfId(Sense.class, getSenseId(wktSense.getKey()));
				Sense source = (Sense) getLmfObjectById(Sense.class, sourceId);
				if (source != null)
					convertSemanticRelations(source, wktSense, wktEntry);
				source = null;
				wktSense = null;
				
				if (++senseCount % 500 == 0) {
					System.out.println("SAVING RELATIONS / PROCESSED " + senseCount + " SENSES");
					tx.commit();
					session.close();
					session = sessionFactory.openSession();
					tx = session.beginTransaction();
				}
			}
			wktEntry = null;
		}
		
		ontoWiktionary.freeSemanticRelations();
	}
	
	protected void convertSemanticRelations(final Sense source, 
			final IWiktionarySense wktSense, 
			final IWiktionaryEntry wktEntry) {		
		// Sense relations (SenseRelation class).
		List<OntoWiktionarySemanticRelation> owktRelations;
		try {
			owktRelations = ontoWiktionary.getSemanticRelations(wktSense.getKey());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
				
				// Find a suitable relation in OntoWiktionary.
				if (owktRelations != null) {
					Iterator<OntoWiktionarySemanticRelation> owktRelationIter = owktRelations.iterator();
					while (owktRelationIter.hasNext()) {
						OntoWiktionarySemanticRelation owktRelation = owktRelationIter.next();
						if (!wktRelation.getRelationType().equals(owktRelation.getRelationType())
								|| !wktRelation.getTarget().equals(owktRelation.getTargetWordForm()))
							continue;
						
						
						owktRelationIter.remove();
						String targetId = getLmfId(Sense.class, getSenseId(owktRelation.getTargetSenseId()));
						if (!"???".equals(targetId)) {
							Sense target = (Sense) getLmfObjectById(Sense.class, targetId);
							if (target != null)
								senseRelation.setTarget(target);
//							else
//								System.err.println("SenseRelation.Target not found: " + owktRelation.getTargetSenseId());
							target = null;
						}
						break;
					}
				}
				
				// Save target word as targetFormRepresentation.
				FormRepresentation targetFormRepresentation = new FormRepresentation();
				targetFormRepresentation.setWrittenForm(convert(wktRelation.getTarget(), 255));
				targetFormRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				senseRelation.setFormRepresentation(targetFormRepresentation); 
				senseRelations.add(senseRelation);
			}
		}
		
		// Save inferred relations.
		if (owktRelations != null) {
			for (OntoWiktionarySemanticRelation owktRelation : owktRelations) {
				SenseRelation senseRelation = new SenseRelation();
				senseRelation.setRelType(WiktionaryLMFMap.mapRelationType(owktRelation.getRelationType()));
				senseRelation.setRelName(WiktionaryLMFMap.mapRelationName(owktRelation.getRelationType()) + "-AUTO");
				if (senseRelation.getRelType() == null)
					continue;

				String targetId = getLmfId(Sense.class, getSenseId(owktRelation.getTargetSenseId()));
				if (!"???".equals(targetId)) {
					Sense target = (Sense) getLmfObjectById(Sense.class, targetId);
					if (target != null)
						senseRelation.setTarget(target);
//					else
//						System.err.println("SenseRelation.Target not found: " + owktRelation.getTargetSenseId());
					target = null;
				}

				// Save target word as targetFormRepresentation.
				FormRepresentation targetFormRepresentation = new FormRepresentation();
				targetFormRepresentation.setWrittenForm(convert(owktRelation.getTargetWordForm(), 255));
				targetFormRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(wktEntry.getWordLanguage()));
				senseRelation.setFormRepresentation(targetFormRepresentation); 
				senseRelations.add(senseRelation);
			}
		}		
		source.setSenseRelations(senseRelations);
		saveList(source, senseRelations);
	}

	/** Returns true if this sense should be used for the UBY database. */
	protected boolean considerSense(final IWiktionarySense wktSense) {
		return (wktSense.getGloss() != null);
	}

	/** Converts Wiktionary Sense to LMF Sense. */
	protected Sense wktSenseToLMFSense(IWiktionarySense wktSense, IWiktionaryEntry wktEntry, LexicalEntry entry){
		// Sense and identifier.
		Sense sense = new Sense();
		sense.setId(getLmfId(Sense.class, getSenseId(wktSense)));
		sense.setIndex(wktSense.getIndex());

		// Monolingual external reference.
		MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
		monolingualExternalRef.setExternalSystem("Wiktionary_" 
				+ jwktlVersion + "_" + wktDate + "_" 
				+ wktLang.getISO639_2T() + "_sense");
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
		List<SemanticLabel> semanticLabels = createSemanticLabels(entry, sense, wktSense);
		if (semanticLabels != null && semanticLabels.size() > 0)
			sense.setSemanticLabels(semanticLabels);
		
		// Etymology (Statement class; type etymology).
		IWikiString etymology = null;
		if (wktEntry.getWordEtymology() != null)
			etymology = wktEntry.getWordEtymology();
		
		if (etymology != null) {
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
			for (IQuotation quotation : wktSense.getQuotations()) {
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
				if (quotation.getSource() != null) {
					String source = quotation.getSource().getPlainText();
					if (source.length() > 255)
						source = source.substring(0, 255);
					context.setSource(source);
				}
				contexts.add(context);
			}
		}
		sense.setContexts(contexts);

		// Sense relations (SenseRelation class) 
		// -- skip (will be done in a separate step!
		
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

	protected List<SemanticLabel> createSemanticLabels(
			final LexicalEntry entry, final Sense sense, 
			final IWiktionarySense wktSense) {
		List<SemanticLabel> result = new ArrayList<SemanticLabel>();
		
		// Create semantic labels from part of speech tags.
		for (PartOfSpeech p : wktSense.getEntry().getPartsOfSpeech()) {
			if (p == null)
				continue;
			ELabelTypeSemantics semanticLabelType;
			String semanticLabelName;
			switch (p) {
				case TOPONYM:
					semanticLabelType = ELabelTypeSemantics.semanticNounClass;
					semanticLabelName = ELabelNameSemantics.SEMANTIC_NOUN_CLASS_TOPONYM;
					break;
				case SINGULARE_TANTUM:
					semanticLabelType = ELabelTypeSemantics.semanticNounClass;
					semanticLabelName = ELabelNameSemantics.SEMANTIC_NOUN_CLASS_ONLY_SINGULAR;
					break;
				case PLURALE_TANTUM:
					semanticLabelType = ELabelTypeSemantics.semanticNounClass;
					semanticLabelName = ELabelNameSemantics.SEMANTIC_NOUN_CLASS_ONLY_PLURAL;
					break;
					
				case SALUTATION:
					semanticLabelType = ELabelTypeSemantics.interjectionClass;
					semanticLabelName = ELabelNameSemantics.INTERJECTION_SALUTATION;
					break;
				case ONOMATOPOEIA:
					semanticLabelType = ELabelTypeSemantics.interjectionClass;
					semanticLabelName = ELabelNameSemantics.INTERJECTION_ONOMATOPOEIA;
					break;
					
				case IDIOM:
					semanticLabelType = ELabelTypeSemantics.phrasemeClass;
					semanticLabelName = ELabelNameSemantics.PHRASEME_CLASS_IDIOM;
					break;
				case COLLOCATION:
					semanticLabelType = ELabelTypeSemantics.phrasemeClass;
					semanticLabelName = ELabelNameSemantics.PHRASEME_CLASS_COLLOCATION;
					break;
				case PROVERB:
					semanticLabelType = ELabelTypeSemantics.phrasemeClass;
					semanticLabelName = ELabelNameSemantics.PHRASEME_CLASS_PROVERB;
					break;
				case MNEMONIC:
					semanticLabelType = ELabelTypeSemantics.phrasemeClass;
					semanticLabelName = ELabelNameSemantics.PHRASEME_CLASS_MNEMONIC;
					break;
					
				case MODAL_PARTICLE:
					semanticLabelType = ELabelTypeSemantics.discourseFunction;
					semanticLabelName = ELabelNameSemantics.DISCOURSE_FUNCTION_MODAL_PARTICLE;
					break;
				case FOCUS_PARTICLE:
					semanticLabelType = ELabelTypeSemantics.discourseFunction;
					semanticLabelName = ELabelNameSemantics.DISCOURSE_FUNCTION_FOCUS_PARTICLE;
					break;
				case INTENSIFYING_PARTICLE:
					semanticLabelType = ELabelTypeSemantics.discourseFunction;
					semanticLabelName = ELabelNameSemantics.DISCOURSE_FUNCTION_INTENSIFYING_PARTICLE;
					break;
				
				default:
					continue;
			}
			
			SemanticLabel semanticLabel = new SemanticLabel();
			semanticLabel.setType(semanticLabelType);
			semanticLabel.setLabel(semanticLabelName);
			result.add(semanticLabel);
		}
		
		// Create semantic labels from the sense definition.
		IWikiString senseDefinition = wktSense.getGloss();
		List<PragmaticLabel> labels = labelManager.parseLabels(
				senseDefinition.getText(), wktSense.getEntry().getWord());
		if (labels != null) {			
			for (PragmaticLabel label : labels) {
				String labelGroup = label.getLabelGroup();
				if (labelGroup == null || labelGroup.length() == 0)
					continue;

				if (labelManager.isWordFormLabel(label)) {
					// Morphological labels.
					String targetWord = labelManager.extractTargetWordForm(senseDefinition.getText()); 
					IWiktionaryEntry wktEntry = wktSense.getEntry();
					WordForm wordForm = new WordForm();
					List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
					FormRepresentation formRepresentation = new FormRepresentation();
					formRepresentation.setWrittenForm(convert(wktEntry.getWord(), 255));						
					formRepresentations.add(formRepresentation);	
					wordForm.setFormRepresentations(formRepresentations);

					for (IWiktionaryEntry targetEntry : wkt.getEntriesForWord(targetWord)) {
						// Ignore entries of a different language.
						if (targetEntry.getWordLanguage() == null 
								|| !targetEntry.getWordLanguage().equals(wktEntry.getWordLanguage()))
							continue;
						// Ignore entries of a different part of speech.
						if (targetEntry.getPartOfSpeech() == null 
								|| !targetEntry.getPartOfSpeech().equals(wktEntry.getPartOfSpeech()))
							continue;

						String entryId = getLmfId(LexicalEntry.class, getEntryId(targetEntry));
						LexicalEntry lexEntry = (LexicalEntry) getLmfObjectById(LexicalEntry.class, entryId);
						if (lexEntry != null) { 
							// If the entry already exists then save directly to it
							List<WordForm> wordForms = lexEntry.getWordForms(); 
							if (wordForms == null) {
								wordForms = new ArrayList<WordForm>();
								lexEntry.setWordForms(wordForms);
							}
							wordForms.add(wordForm);
							saveList(lexEntry, lexEntry.getWordForms());
						} else {
							// If the lexical entry does not yet exist, then 
							// save the wordForms temporarily.
							if (wordForms.containsKey(entryId)) {
								wordForms.get(entryId).add(wordForm);
							} else {
								List<WordForm> temp = new ArrayList<WordForm>();
								temp.add(wordForm);
								wordForms.put(entryId, temp);
							}
						}
					}

				} else				
					if (labelGroup.startsWith("syntax:gram")) {
						// Grammatical labels.
						SubcategorizationFrame subcatFrame = new SubcategorizationFrame();
						subcatFrame.setSubcatLabel(label.getLabel());
						subcatFrame.setId(getResourceAlias() + "_SubcatFrame_" + (subcatFrameIdx++));
						subcatFrames.add(subcatFrame);

						SyntacticBehaviour sb = new SyntacticBehaviour();
						sb.setSubcategorizationFrame(subcatFrame);
						sb.setId(getResourceAlias() + "_SyntacticBehaviour_" + (syntacticBehaviourIdx++));
						sb.setSense(sense);
						if (entry.getSyntacticBehaviours() == null) {
							entry.setSyntacticBehaviours(new LinkedList<SyntacticBehaviour>());
						}
						entry.getSyntacticBehaviours().add(sb);
						
					} else {
						// Semantic labels.
						SemanticLabel semanticLabel = new SemanticLabel();
						semanticLabel.setLabel(StringUtils.replaceNonUtf8(label.getLabel()));
						if (labelGroup.startsWith("dom"))
							semanticLabel.setType(ELabelTypeSemantics.domain);
						else
						if (labelGroup.startsWith("reg") || labelGroup.startsWith("dia"))
							semanticLabel.setType(ELabelTypeSemantics.regionOfUsage);
						else
						if (labelGroup.startsWith("phas") || labelGroup.startsWith("strat") || labelGroup.startsWith("eval"))
							semanticLabel.setType(ELabelTypeSemantics.register);
						else
						if (labelGroup.startsWith("temp"))
							semanticLabel.setType(ELabelTypeSemantics.timePeriodOfUsage);
						else
						if (labelGroup.startsWith("freq") || labelGroup.startsWith("norm"))
							semanticLabel.setType(ELabelTypeSemantics.usage);
						else
							continue;

						result.add(semanticLabel);
					}
				// TODO: Additional labels: etym;request;syntax:form;syntax:pos
			}
		}
		return result;
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
		formRepresentation.setWrittenForm(convert(writtenForm, 255));
		formRepresentation.setLanguageIdentifier(WiktionaryLMFMap.mapLanguage(language));
		result.add(formRepresentation);
		return result;
	}

	@Override
	protected SubcategorizationFrame getNextSubcategorizationFrame() {
		return (subcatFrames.isEmpty() ? null : subcatFrames.remove(0));
	}

	@Override
	protected Synset getNextSynset() {
		// If we haven't started yet, initialize the iterator.
		if (synsetIter == null)
			try {
//tx.rollback();session.close();session = sessionFactory.openSession();tx = session.beginTransaction();
				//synsetIter = ontoWiktionary.getConcepts().iterator(); //This is probelmatic!
				synsetIter = ontoWiktionary.getStreamedConcepts().iterator();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		
		// If we're finished, convert the synset relations and free resources.
		if (!synsetIter.hasNext()) {
			synsetIter = null;
			convertSynsetRelations();
			ontoWiktionary.freeConcepts();
			return null;
		}
		
		// Check if at least one sense exists.
		OntoWiktionaryConcept owktSynset = synsetIter.next();
		List<Sense> senses = new LinkedList<Sense>();
		for (String lexicalization : owktSynset.getLexicalizations()) {
			String senseId = getLmfId(Sense.class, getSenseId(lexicalization));
			Sense sense = (Sense) getLmfObjectById(Sense.class, senseId);
			if (sense == null) {
				// Caused by different sense selection (e.g., inflected forms) 
//				System.err.println("Sense not found: " + lexicalization);
				continue;
			}
			
			if (sense.getSynset() != null)
				System.err.println("Inconsistent synset structure for " + lexicalization);
			senses.add(sense);
		}
		if (senses.size() == 0)
			return getNextSynset();
		
		// Synset.
		Synset synset = new Synset();
		synset.setId(getLmfId(Synset.class, getSynsetId(owktSynset.getConceptId())));
		
		// MonolingualExternalRef.
		List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
		MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
		monolingualExternalRef.setExternalSystem("OntoWiktionary" + wktLang.getISO639_1().toUpperCase() + "_ConceptID");
		monolingualExternalRef.setExternalReference(owktSynset.getConceptId());
		monolingualExternalRefs.add(monolingualExternalRef);
		synset.setMonolingualExternalRefs(monolingualExternalRefs);
		
		// Senses.
		for (Sense sense : senses)
			sense.setSynset(synset);
		synset.setSenses(senses);
		
		return synset;
	}
	
	protected void convertSynsetRelations() {
		try {
			synsetIter = ontoWiktionary.getConcepts().iterator();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int conceptCount = 0;
		while (synsetIter.hasNext()) {
			OntoWiktionaryConcept owktSynset = synsetIter.next();
			Synset source = (Synset) getLmfObjectById(Synset.class, 
					getLmfId(Synset.class, getSynsetId(owktSynset.getConceptId())));
			if (source == null) {
//				System.err.println("Source concept not found: " + owktSynset.getConceptId());	
				continue;
			}
			
			// SynsetRelation.
			List<SynsetRelation> synsetRelations = new LinkedList<SynsetRelation>();
			addSynsetRelations(synsetRelations, owktSynset.getSubsumesRelations(), 
					ERelTypeSemantics.taxonomic, "subsumes", source);
			addSynsetRelations(synsetRelations, owktSynset.getSubsumedByRelations(), 
					ERelTypeSemantics.taxonomic, "subsumedBy", source);
			addSynsetRelations(synsetRelations, owktSynset.getRelatedConcepts(), 
					ERelTypeSemantics.association, "related", source);
			source.setSynsetRelations(synsetRelations);
			saveCascade(source);
			
			if (++conceptCount % 1000 == 0) {
				System.out.println("SAVING RELATIONS / PROCESSED " + conceptCount + " SYNSETS");
				tx.commit();
				session.close();
				session = sessionFactory.openSession();
				tx = session.beginTransaction();
			}
		}
		
		synsetIter = null;
	}

	protected void addSynsetRelations(final List<SynsetRelation> synsetRelations,
			final Iterable<String> relationTargets,
			final ERelTypeSemantics relType, final String relName,
			final Synset source) {
		for (String targetID : relationTargets) {
			Synset target = (Synset) getLmfObjectById(Synset.class, 
					getLmfId(Synset.class, getSynsetId(targetID)));
			if (target == null) {
//				System.err.println("Target concept not found: " + targetID);			
				continue;
			}
			
			SynsetRelation synsetRelation = new SynsetRelation();
			synsetRelation.setRelType(relType);
			synsetRelation.setRelName(relName);
			synsetRelation.setSource(source);		
			synsetRelation.setTarget(target);
			synsetRelations.add(synsetRelation);
		}
	}
	
	@Override
	protected ConstraintSet getNextConstraintSet() { return null;}

	@Override
	protected SemanticPredicate getNextSemanticPredicate() { return null;}

	@Override
	protected SenseAxis getNextSenseAxis() { return null;}

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

	/** Returns unique entry ID for a WiktionaryEntry. */
	protected String getEntryId(IWiktionaryEntry entry){
		return "e" + entry.getKey();
	}

	/** Returns unique sense ID for a WiktionarySense. */
	protected String getSenseId(IWiktionarySense sense){
		return getSenseId(sense.getKey());
	}
	
	protected String getSenseId(final String senseKey){
		return "s" + senseKey;
	}

	protected String getSynsetId(final String conceptId) {
		return "c" + conceptId;
	}
	
	private static String convert(final String text) {
		return StringUtils.replaceNonUtf8(
				StringUtils.replaceHtmlEntities(text));
	}
	
	private static String convert(final String text, int maxLength) {
		if (text == null)
			return "";
		else
			return StringUtils.replaceNonUtf8(
					StringUtils.replaceHtmlEntities(text), maxLength);
	}

	protected String convertEtymology(final IWikiString etymology) {
		try {
			String result = TemplateParser.parse(etymology.getText(), new EtymologyTemplateHandler());
			return WikiString.makePlainText(result);
		} catch (Exception e) {
			return WikiString.makePlainText(etymology.getText());
		}
	}

}
