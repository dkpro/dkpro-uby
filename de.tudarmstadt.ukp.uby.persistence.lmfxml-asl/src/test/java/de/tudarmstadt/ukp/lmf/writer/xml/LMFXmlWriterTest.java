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
package de.tudarmstadt.ukp.lmf.writer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.Statement;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EAuxiliary;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.enums.ECoreType;
import de.tudarmstadt.ukp.lmf.model.enums.EDefinitionType;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.EPerson;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.morphology.Component;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.ListOfComponents;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxisRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.ArgumentRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicateRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcatFrameSetElement;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.model.syntax.SynArgMap;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * Tests methods of {@link LMFXmlWriter}.
 * 
 * Tests are made for UBY-LMF DTD version 0.2.0
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
public class LMFXmlWriterTest {
	
	private static LMFXmlWriter lmfXmlWriter;
	
	private static LexicalResource lexicalResource; 
	
	private static String dtdPath;
	private static String outputPath;
	
	private static final String lexicalResource_dtdVersion = "0.2.0";
	private static final String lexicalResource_name = "lexicalResource_name";
	
	private static final String globalInformation_label = "globalInformation_label";
	
	private static final String lexicon_languageIdentifier = ELanguageIdentifier.ENGLISH;
	private static final String lexicon_name = "lexicon_name";
	private static final String lexicon_id = "lexicon_id";
	
	private static final String lexicalEntry_id = "lexicalEntry_id";
	private static final EPartOfSpeech lexicalEntry_partOfSpeech = EPartOfSpeech.adjective;
	private static final String lexicalEntry_separableParticle = "lexicalEntry_separableParticle";
	
	private static final String formRepresentation_languageIdentifier = ELanguageIdentifier.GERMAN;
	private static final String formRepresentation_writtenForm = "formRepresentation_writtenForm";
	private static final String formRepresentation_phoneticForm = "formRepresentation_phoneticForm";
	private static final String formRepresentation_sound = "formRepresentation_sound";
	private static final String formRepresentation_geographicalVariant = "formRepresentation_geographicalVariant";
	private static final String formRepresentation_hyphenation = "formRepresenation_hyphenation";
	private static final String formRepresenataion_orthographyName = "formRepresenataion_orthographyName";
	
	private static final EGrammaticalNumber wordForm_grammaticalNumber = EGrammaticalNumber.plural;
	private static final EGrammaticalGender wordForm_grammaticalGender = EGrammaticalGender.feminine;
	private static final ECase wordForm_case = ECase.accusative;
	private static final EPerson wordForm_person = EPerson.third;
	private static final ETense wordForm_tense = ETense.present;
	private static final EVerbFormMood wordForm_verbFormMoode = EVerbFormMood.imperative;
	private static final EDegree wordForm_degree = EDegree.SUPERLATIVE;
	
	private static final String frequency_corpus = "frequency_corpus";
	private static final int frequency_frequancy = 12345;
	private static final String frequency_generator = "frequency_generator";
	
	private static final LexicalEntry targetLexicalEntry = new LexicalEntry("targetLexicalEntry_id");
	private static final Sense targetSense = new Sense("targetSense_id");
	
	private static final ERelTypeMorphology relatedForm_relType = ERelTypeMorphology.derivationBase;
	
	private static final String sense_id = "sense_id";
	private static final int sense_index = 54321;
	private static final Synset sense_synset = new Synset("sense_synset");
	private static final SemanticArgument sense_incorporatedSemArg = new SemanticArgument("sense_incorporatedSemArg_id");
	private static final boolean sense_transparentMeaning = true;
	private static final String sense_sense_id = "sense_sense_id"; // the id of the nested sense
	
	private static final EContextType context_contextType = EContextType.corpusEvidence;
	private static final String context_source = "context_source";
	
	private static final String textRepresentation_languageIdentifier = ELanguageIdentifier.ISO639_SRP;
	private static final String textRepresentation_orthographyName = "textRepresentation_orthographyName";
	private static final String textRepresentation_geographicalVariant = "textRepresentation_geographicalVariant";
	private static final String textRepresentation_writtenText = "textRepresentation_writtenText";
	
	private static final String monolingualExternalRef_externalSystem = "monolingualExternalRef_externalSystem";
	private static final String monolingualExternalRef_externalReference = "monolingualExternalRef_externalReference";
	
	private static final String semanticPredicate_id = "semanticPredicate_id";
	
	private static final String senseExample_id = "senseExample_id";
	private static final EExampleType senseExample_exampleType = EExampleType.senseInstance;
	
	private static final EDefinitionType definition_definitionType = EDefinitionType.intensionalDefinition;
	private static final EStatementType statement_statementType = EStatementType.encyclopedicInformation;
	
	private static final String senseRelation_relationName = "senseRelation_relationName";
	private static final ERelTypeSemantics senseRelation_relationType = ERelTypeSemantics.label;
	
	private static final String semanticLabel_label = "semanticLabel_label";
	private static final ELabelTypeSemantics semanticLabel_type = ELabelTypeSemantics.resourceSpecific;
	private static final String semanticLabel_quantification = "semanticLabel_quantification";
	
	private static final String syntacticBehaviour_id = "syntactibBehaviour_id";
	
	private static final String subcategorizationFrame_id = "subcategorizationFrame_id";
	private static final String subcategorizationFrame_subcatLabel = "subcategorizationFrame_subcatLabel";
	
	private static final String subcategorizationFrameSet_id = "subcategorizationFrameSet_id";
	private static final String subcategorizationFrameSet_name = "subcategorizationFrameSet_name";
	
	private static final boolean component_isHead = true;
	private static final int component_position = 1;
	private static final boolean component_isBreakBefore = false;
	
	private static final EAuxiliary lexemeProperty_auxiliary = EAuxiliary.sein;
	private static final ESyntacticProperty lexemePropert_syntacticProperty = ESyntacticProperty.objectRaising;
	
	private static final String syntacticArgument_id = "syntacticArgument_id";
	private static final boolean syntacticArgument_optional = true;
	private static final EGrammaticalFunction syntacticArgument_grammaticalFunction = EGrammaticalFunction.directObject;
	private static final ESyntacticCategory syntacticArgument_syntacticCategory = ESyntacticCategory.adverbPhrase_prepositionalPhrase;
	private static final ECase syntacticArgument_case = ECase.genitive;
	private static final EDeterminer syntacticArgument_determiner = EDeterminer.possessive;
	private static final String syntacticArgument_preposition = "syntacticArgument_preposition";
	private static final String syntacticArgument_prepositionType = "syntacticArgument_prepositionType";
	private static final EGrammaticalNumber syntacticArgument_number = EGrammaticalNumber.singular;
	private static final String syntacticArgument_lexeme = "syntacticArgument_lexeme";
	private static final EVerbForm syntacticArgument_verbForm = EVerbForm.ingForm;
	private static final ETense syntacticArgument_tense = ETense.present;
	private static final EComplementizer syntacticArgument_complementizer = EComplementizer.whType;
	
	private static final String semanticPredicate_label = "semanticPredicate_label";
	private static final boolean semanticPredicate_lexicalized = true;
	private static final boolean semanticPredicate_perspectivalized = false;
	
	private static final String semanticArgument_id = "semanticArgument_id";
	private static final String semanticArgument_semanticRole = "semanticArgument_semanticRole";
	private static final boolean semanticArgument_isIncorporated = false;
	private static final ECoreType semanticArgument_coreType = ECoreType.peripheral;
	
	private static final String argumentRelation_relType = "argumentRelation_relType";
	private static final String argumentRelation_relName = "argumentRelation_relName";
	
	private static final String predicateRelation_relType = "predicateRelation_relType";
	private static final String predicateRelation_relName = "predicateRelation_relName";
	
	private static final String synset_id = "synset_id";
	
	private static final String synsetRelation_relName = "synsetRelation_relName"; 
	private static final ERelTypeSemantics synsetRelation_relType = ERelTypeSemantics.predicative;
	
	private static final String synSemCorrespondences_id = "synSemCorrespondences_id";
	
	private static final String senseAxis_id = "senseAxis_id";
	private static final ESenseAxisType senseAxis_senseAxisType = ESenseAxisType.monolingualSenseAlignment;
	private static final String senseAxisRelation_type = "senseAxisRelation_type";
	private static final String senseAxisRelation_name = "senseAxisRelation_name";

	/**
	 * Creates a UBY-LMF structure by initializing every child and every field
	 * of all UBY-LMF classes.<br>
	 * All UBY-LMF classes containing a list of children have at least
	 * one fully initialized child instance.
	 * 
	 * @throws IOException if files needed for this {@link LMFXmlWriter} test to run
	 * could not be created
	 * @throws LMFWriterException if an error on writing the xml file needed for this
	 * test to rung occurred
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException, LMFWriterException {
		
		String outputDirectory = ("target/test-output/"+LMFXmlWriterTest.class.getSimpleName());
		
		
		
		// create folder structure
		File file = new File(outputDirectory);
		file.mkdirs();
		file.setReadable(true);
		file.setWritable(true);
		outputPath = outputDirectory+"/test.xml";
		
		// create a dummy dtd
		dtdPath = outputDirectory+"/test.dtd";
		File dtd = new File(dtdPath);
		dtd.createNewFile();
		
		
		
		
		lexicalResource = new LexicalResource();
		lexicalResource.setDtdVersion(lexicalResource_dtdVersion);
		lexicalResource.setName(lexicalResource_name);
		
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel(globalInformation_label);
		lexicalResource.setGlobalInformation(globalInformation);
		
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(lexicon_languageIdentifier);
		lexicon.setName(lexicon_name);
		lexicon.setId(lexicon_id);
		lexicalResource.addLexicon(lexicon);
		
		LexicalEntry lexicalEntry = new LexicalEntry(lexicalEntry_id);
		lexicalEntry.setPartOfSpeech(lexicalEntry_partOfSpeech);
		lexicalEntry.setSeparableParticle(lexicalEntry_separableParticle);
		lexicalEntry.setLexicon(lexicon);
		lexicon.addLexicalEntry(lexicalEntry);
		lexicon.setLexicalResource(lexicalResource);
		
		Lemma lemma = new Lemma();
		lexicalEntry.setLemma(lemma);
		
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(formRepresentation_languageIdentifier);
		formRepresentation.setWrittenForm(formRepresentation_writtenForm);
		formRepresentation.setPhoneticForm(formRepresentation_phoneticForm);
		formRepresentation.setSound(formRepresentation_sound);
		formRepresentation.setGeographicalVariant(formRepresentation_geographicalVariant);
		formRepresentation.setHyphenation(formRepresentation_hyphenation);
		formRepresentation.setOrthographyName(formRepresenataion_orthographyName);
		List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
		formRepresentations.add(formRepresentation);
		lemma.setFormRepresentations(formRepresentations);
		lemma.setLexicalEntry(lexicalEntry);
		
		WordForm wordForm = new WordForm();
		wordForm.setGrammaticalNumber(wordForm_grammaticalNumber);
		wordForm.setGrammaticalGender(wordForm_grammaticalGender);
		wordForm.setCase(wordForm_case);
		wordForm.setPerson(wordForm_person);
		wordForm.setTense(wordForm_tense);
		wordForm.setVerbFormMood(wordForm_verbFormMoode);
		wordForm.setDegree(wordForm_degree);
		wordForm.setFormRepresentations(formRepresentations);
		List<WordForm> wordForms = new ArrayList<WordForm>(1);
		wordForms.add(wordForm);
		lexicalEntry.setWordForms(wordForms);
		
		Frequency frequency = new Frequency();
		frequency.setCorpus(frequency_corpus);
		frequency.setFrequency(frequency_frequancy);
		frequency.setGenerator(frequency_generator);
		List<Frequency> frequencies = new ArrayList<Frequency>(1);
		frequencies.add(frequency);
		wordForm.setFrequencies(frequencies);
		
		RelatedForm relatedForm = new RelatedForm();
		relatedForm.setTargetLexicalEntry(targetLexicalEntry);
		relatedForm.setTargetSense(targetSense);
		relatedForm.setRelType(relatedForm_relType);
		relatedForm.setFormRepresentations(formRepresentations);
		List<RelatedForm> relatedForms = new ArrayList<RelatedForm>(1);
		relatedForms.add(relatedForm);
		lexicalEntry.setRelatedForms(relatedForms);
		
		Sense sense = new Sense(sense_id);
		sense.setIndex(sense_index);
		sense.setSynset(sense_synset);
		sense.setIncorporatedSemArg(sense_incorporatedSemArg);
		sense.setTransparentMeaning(sense_transparentMeaning);
		sense.setLexicalEntry(lexicalEntry);
		List<Sense> senses = new ArrayList<Sense>(1);
		senses.add(sense);
		lexicalEntry.setSenses(senses);
		
		Sense nestedSense = new Sense(sense_sense_id);
		nestedSense.setIndex(sense_index);
		nestedSense.setSynset(sense_synset);
		nestedSense.setIncorporatedSemArg(sense_incorporatedSemArg);
		nestedSense.setTransparentMeaning(sense_transparentMeaning);
		nestedSense.setLexicalEntry(lexicalEntry);
		List<Sense> nestedSenses = new ArrayList<Sense>(1);
		nestedSenses.add(nestedSense);
		sense.setSenses(nestedSenses);
		
		Context context = new Context();
		context.setContextType(context_contextType);
		context.setSource(context_source);
		List<Context> contexts = new ArrayList<Context>(1);
		contexts.add(context);
		sense.setContexts(contexts);
		nestedSense.setContexts(contexts);
		
		TextRepresentation textRepresentation = new TextRepresentation();
		textRepresentation.setLanguageIdentifier(textRepresentation_languageIdentifier);
		textRepresentation.setOrthographyName(textRepresentation_orthographyName);
		textRepresentation.setGeographicalVariant(textRepresentation_geographicalVariant);
		textRepresentation.setWrittenText(textRepresentation_writtenText);
		List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>(1);
		textRepresentations.add(textRepresentation);
		context.setTextRepresentations(textRepresentations);
		
		MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
		monolingualExternalRef.setExternalReference(monolingualExternalRef_externalReference);
		monolingualExternalRef.setExternalSystem(monolingualExternalRef_externalSystem);
		List<MonolingualExternalRef> monolingualExternalRefs = new ArrayList<MonolingualExternalRef>();
		monolingualExternalRefs.add(monolingualExternalRef);
		context.setMonolingualExternalRefs(monolingualExternalRefs);
		
		PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
		SemanticPredicate semanticPredicate = new SemanticPredicate();
		semanticPredicate.setId(semanticPredicate_id);
		predicativeRepresentation.setPredicate(semanticPredicate);
		List<PredicativeRepresentation> predicativeRepresentations = new ArrayList<PredicativeRepresentation>(1);
		predicativeRepresentations.add(predicativeRepresentation);
		sense.setPredicativeRepresentations(predicativeRepresentations);
		nestedSense.setPredicativeRepresentations(predicativeRepresentations);
		
		SenseExample senseExample = new SenseExample();
		senseExample.setId(senseExample_id);
		senseExample.setExampleType(senseExample_exampleType);
		senseExample.setTextRepresentations(textRepresentations);
		List<SenseExample> senseExamples = new ArrayList<SenseExample>(1);
		senseExamples.add(senseExample);
		sense.setSenseExamples(senseExamples);
		nestedSense.setSenseExamples(senseExamples);
		
		Definition definition = new Definition();
		definition.setDefinitionType(definition_definitionType);
		
		Statement statement = new Statement();
		statement.setStatementType(statement_statementType);
		statement.setTextRepresentations(textRepresentations);
		List<Statement> statements = new ArrayList<Statement>(1);
		statements.add(statement);
		definition.setStatements(statements);
		definition.setTextRepresentations(textRepresentations);
		List<Definition> definitions = new ArrayList<Definition>(1);
		definitions.add(definition);
		sense.setDefinitions(definitions);
		nestedSense.setDefinitions(definitions);
		
		SenseRelation senseRelation = new SenseRelation();
		senseRelation.setSource(sense);
		senseRelation.setTarget(sense);
		senseRelation.setRelName(senseRelation_relationName);
		senseRelation.setRelType(senseRelation_relationType);
		senseRelation.setFormRepresentation(formRepresentation);
		senseRelation.setFrequencies(frequencies);
		List<SenseRelation> senseRelations = new ArrayList<SenseRelation>(1);
		senseRelations.add(senseRelation);
		sense.setSenseRelations(senseRelations);
		nestedSense.setSenseRelations(senseRelations);
		
		sense.setMonolingualExternalRefs(monolingualExternalRefs);
		nestedSense.setMonolingualExternalRefs(monolingualExternalRefs);
		
		sense.setFrequencies(frequencies);
		nestedSense.setFrequencies(frequencies);
		
		SemanticLabel semanticLabel = new SemanticLabel();
		semanticLabel.setLabel(semanticLabel_label);
		semanticLabel.setQuantification(semanticLabel_quantification);
		semanticLabel.setType(semanticLabel_type);
		semanticLabel.setMonolingualExternalRefs(monolingualExternalRefs);
		semanticLabel.setParent(sense);
		List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
		semanticLabels.add(semanticLabel);
		sense.setSemanticLabels(semanticLabels);
		
		// semantic label of the nested sense
		SemanticLabel semanticLabel2 = new SemanticLabel();
		semanticLabel2.setLabel(semanticLabel_label);
		semanticLabel2.setQuantification(semanticLabel_quantification);
		semanticLabel2.setType(semanticLabel_type);
		semanticLabel2.setMonolingualExternalRefs(monolingualExternalRefs);
		semanticLabel2.setParent(nestedSense);
		List<SemanticLabel> semanticLabels2 = new ArrayList<SemanticLabel>();
		semanticLabels2.add(semanticLabel2);
		nestedSense.setSemanticLabels(semanticLabels2);
		
		SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
		syntacticBehaviour.setId(syntacticBehaviour_id);
		List<SyntacticBehaviour> syntacticBehaviours = new ArrayList<SyntacticBehaviour>(1);
		syntacticBehaviours.add(syntacticBehaviour);
		lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);
		syntacticBehaviour.setSense(sense);
		
		SubcategorizationFrame subcategorizationFrame = new SubcategorizationFrame();
		subcategorizationFrame.setId(subcategorizationFrame_id);
		syntacticBehaviour.setSubcategorizationFrame(subcategorizationFrame);
		
		SubcategorizationFrameSet subcategorizationFrameSet = new SubcategorizationFrameSet();
		subcategorizationFrameSet.setId(subcategorizationFrameSet_id);
		syntacticBehaviour.setSubcategorizationFrameSet(subcategorizationFrameSet);
		
		ListOfComponents listOfComponents = new ListOfComponents();
		lexicalEntry.setListOfComponents(listOfComponents);
		
		Component component = new Component();
		component.setTargetLexicalEntry(lexicalEntry);
		component.setHead(component_isHead);
		component.setPosition(component_position);
		component.setBreakBefore(component_isBreakBefore);
		List<Component> components = new ArrayList<Component>(1);
		components.add(component);
		listOfComponents.setComponents(components);
		
		lexicalEntry.setFrequencies(frequencies);
		
		subcategorizationFrame.setParentSubcatFrame(subcategorizationFrame);
		subcategorizationFrame.setSubcatLabel(subcategorizationFrame_subcatLabel);
		
		LexemeProperty lexemeProperty = new LexemeProperty();
		lexemeProperty.setAuxiliary(lexemeProperty_auxiliary);
		lexemeProperty.setSyntacticProperty(lexemePropert_syntacticProperty);
		subcategorizationFrame.setLexemeProperty(lexemeProperty);
		
		SyntacticArgument syntacticArgument = new SyntacticArgument();
		syntacticArgument.setId(syntacticArgument_id);
		syntacticArgument.setOptional(syntacticArgument_optional);
		syntacticArgument.setGrammaticalFunction(syntacticArgument_grammaticalFunction);
		syntacticArgument.setSyntacticCategory(syntacticArgument_syntacticCategory);
		syntacticArgument.setCase(syntacticArgument_case);
		syntacticArgument.setDeterminer(syntacticArgument_determiner);
		syntacticArgument.setPreposition(syntacticArgument_preposition);
		syntacticArgument.setPrepositionType(syntacticArgument_prepositionType);
		syntacticArgument.setNumber(syntacticArgument_number);
		syntacticArgument.setLexeme(syntacticArgument_lexeme);
		syntacticArgument.setVerbForm(syntacticArgument_verbForm);
		syntacticArgument.setTense(syntacticArgument_tense);
		syntacticArgument.setComplementizer(syntacticArgument_complementizer);
		syntacticArgument.setFrequencies(frequencies);
		List<SyntacticArgument> syntacticArguments = new ArrayList<SyntacticArgument>(1);
		syntacticArguments.add(syntacticArgument);
		subcategorizationFrame.setSyntacticArguments(syntacticArguments);
		subcategorizationFrame.setFrequencies(frequencies);
		List<SubcategorizationFrame> subcategorizationFrames = new ArrayList<SubcategorizationFrame>(1);
		subcategorizationFrames.add(subcategorizationFrame);
		lexicon.setSubcategorizationFrames(subcategorizationFrames);
		
		subcategorizationFrameSet.setName(subcategorizationFrameSet_name);
		subcategorizationFrameSet.setParentSubcatFrameSet(subcategorizationFrameSet);
		SubcatFrameSetElement subcatFrameSetElement = new SubcatFrameSetElement();
		subcatFrameSetElement.setElement(subcategorizationFrame);
		List<SubcatFrameSetElement> subcatFrameSetElements= new ArrayList<SubcatFrameSetElement>();
		subcatFrameSetElements.add(subcatFrameSetElement);
		subcategorizationFrameSet.setSubcatFrameSetElements(subcatFrameSetElements);
		SynArgMap synArgMap = new SynArgMap();
		synArgMap.setArg1(syntacticArgument);
		synArgMap.setArg2(syntacticArgument);
		List<SynArgMap> synArgMaps = new ArrayList<SynArgMap>();
		synArgMaps.add(synArgMap);
		subcategorizationFrameSet.setSynArgMaps(synArgMaps);
		List<SubcategorizationFrameSet> subcategorizationFrameSets = new ArrayList<SubcategorizationFrameSet>();
		subcategorizationFrameSets.add(subcategorizationFrameSet);
		lexicon.setSubcategorizationFrameSets(subcategorizationFrameSets);
		
		semanticPredicate.setLabel(semanticPredicate_label);
		semanticPredicate.setLexicalized(semanticPredicate_lexicalized);
		semanticPredicate.setPerspectivalized(semanticPredicate_perspectivalized);
		semanticPredicate.setDefinitions(definitions);
		
		List<SemanticPredicate> semanticPredicates = new ArrayList<SemanticPredicate>();
		semanticPredicates.add(semanticPredicate);
		lexicon.setSemanticPredicates(semanticPredicates);
		
		SemanticArgument semanticArgument = new SemanticArgument();
		semanticArgument.setId(semanticArgument_id);
		semanticArgument.setSemanticRole(semanticArgument_semanticRole);
		semanticArgument.setIncorporated(semanticArgument_isIncorporated);
		semanticArgument.setCoreType(semanticArgument_coreType);
		List<SemanticArgument> semanticArguments = new ArrayList<SemanticArgument>();
		semanticArguments.add(semanticArgument);
		semanticPredicate.setSemanticArguments(semanticArguments);
		
		ArgumentRelation argumentRelation = new ArgumentRelation();
		argumentRelation.setTarget(semanticArgument);
		argumentRelation.setRelType(argumentRelation_relType);
		argumentRelation.setRelName(argumentRelation_relName);
		List<ArgumentRelation> argumentRelations = new ArrayList<ArgumentRelation>();
		argumentRelations.add(argumentRelation);
		semanticArgument.setArgumentRelations(argumentRelations);
		semanticArgument.setFrequencies(frequencies);
		SemanticLabel semanticLabe_semanticArgument = new SemanticLabel();
		semanticLabe_semanticArgument.setLabel(semanticLabel_label);
		semanticLabe_semanticArgument.setQuantification(semanticLabel_quantification);
		semanticLabe_semanticArgument.setType(semanticLabel_type);
		semanticLabe_semanticArgument.setMonolingualExternalRefs(monolingualExternalRefs);
		semanticLabe_semanticArgument.setParent(semanticArgument);
		List<SemanticLabel> semanticLabe_semanticArguments = new ArrayList<SemanticLabel>();
		semanticLabe_semanticArguments.add(semanticLabe_semanticArgument);
		semanticArgument.setSemanticLabels(semanticLabe_semanticArguments);
		semanticArgument.setDefinitions(definitions);
		
		PredicateRelation predicateRelation = new PredicateRelation();
		predicateRelation.setTarget(semanticPredicate);
		predicateRelation.setRelevantSemanticPredicate(semanticPredicate);
		predicateRelation.setRelType(predicateRelation_relType);
		predicateRelation.setRelName(predicateRelation_relName);
		List<PredicateRelation> predicateRelations = new ArrayList<PredicateRelation>();
		predicateRelations.add(predicateRelation);
		semanticPredicate.setPredicateRelations(predicateRelations);
		semanticPredicate.setFrequencies(frequencies);
		semanticPredicate.setSemanticLabels(semanticLabels);
		
		Synset synset = new Synset(synset_id);
		synset.setDefinitions(definitions);
		List<Synset> synsets = new ArrayList<Synset>();
		synsets.add(synset);
		lexicon.setSynsets(synsets);
		
		SynsetRelation synsetRelation = new SynsetRelation();
		synsetRelation.setTarget(synset);
		synsetRelation.setRelName(synsetRelation_relName);
		synsetRelation.setRelType(synsetRelation_relType);
		synsetRelation.setFrequencies(frequencies);
		List<SynsetRelation> synsetRelations = new ArrayList<SynsetRelation>();
		synsetRelations.add(synsetRelation);
		synset.setSynsetRelations(synsetRelations);
		synset.setMonolingualExternalRefs(monolingualExternalRefs);
		
		SynSemCorrespondence synSemCorrespondence = new SynSemCorrespondence();
		synSemCorrespondence.setId(synSemCorrespondences_id);
		SynSemArgMap synSemArgMap = new SynSemArgMap();
		synSemArgMap.setSemanticArgument(semanticArgument);
		synSemArgMap.setSyntacticArgument(syntacticArgument);
		List<SynSemArgMap> synSemArgMaps = new ArrayList<SynSemArgMap>();
		synSemArgMaps.add(synSemArgMap);
		List<SynSemCorrespondence> synSemCorrespondences = new ArrayList<SynSemCorrespondence>();
		synSemCorrespondences.add(synSemCorrespondence);
		synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
		lexicon.setSynSemCorrespondences(synSemCorrespondences);
		
		ConstraintSet constraintSet = new ConstraintSet();
		List<ConstraintSet> constraintSets = new ArrayList<ConstraintSet>();
		constraintSets.add(constraintSet);
		lexicon.setConstraintSets(constraintSets);
		
		SenseAxis senseAxis = new SenseAxis();
		senseAxis.setId(senseAxis_id);
		senseAxis.setSenseOne(sense);
		senseAxis.setSenseTwo(sense);
		senseAxis.setSynsetOne(synset);
		senseAxis.setSynsetTwo(synset);
		senseAxis.setSenseAxisType(senseAxis_senseAxisType);
		List<SenseAxis> senseAxes = new ArrayList<SenseAxis>();
		senseAxes.add(senseAxis);
		
		SenseAxisRelation senseAxisRelation = new SenseAxisRelation();
		senseAxisRelation.setRelName(senseAxisRelation_name);
		senseAxisRelation.setRelType(senseAxisRelation_type);
		senseAxisRelation.setTarget(senseAxis);
		List<SenseAxisRelation> senseAxisRelations = new ArrayList<SenseAxisRelation>();
		senseAxisRelations.add(senseAxisRelation);
		senseAxis.setSenseAxisRelations(senseAxisRelations);
		lexicalResource.setSenseAxes(senseAxes);
		
		// write to XML
		lmfXmlWriter = new LMFXmlWriter(outputPath, dtd.getAbsolutePath());
		
		lmfXmlWriter.writeElement(lexicalResource);
		
		lmfXmlWriter.writeEndDocument();
	}
	
	/**
	 * Tests the content of the xml document
	 */
	@Test
	public void testXMLContent(){
		// Read the document
		File xmlFile = new File(outputPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			fail(e.toString());
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(xmlFile);
		} catch (SAXException e) {
			fail(e.toString());
		} catch (IOException e) {
			fail(e.toString());
		}
		
		checkLexicalResource(doc.getDocumentElement());
	}

	/**
	 * Test the values of attributes and children of a {@link LexicalResource} node in the
	 * consumed lexical resource {@link Element}.
	 * 
	 * @param lexicalResource the node of the lexical resource
	 */
	private void checkLexicalResource(Element lexicalResource) {
		
		assertEquals(lexicalResource_dtdVersion, lexicalResource.getAttribute("dtdVersion"));
		assertEquals(lexicalResource_name, lexicalResource.getAttribute("name"));
		
		NodeList nlGlobalInformation = lexicalResource.getElementsByTagName("GlobalInformation");
		assertEquals("lexical resource should have one GlobalInformation instance", 1, nlGlobalInformation.getLength());
		checkGlobalInformation((Element)nlGlobalInformation.item(0));
		
		NodeList nlLexicon = lexicalResource.getElementsByTagName("Lexicon");
		assertEquals("LexicalResource should have one Lexicon instance", 1, nlLexicon.getLength());
		checkLexicon((Element) nlLexicon.item(0));
		
		Element senseAxis = checkHasSingleChild(lexicalResource, SenseAxis.class);
		assertEquals(senseAxis_id, senseAxis.getAttribute("id"));
		assertEquals(sense_id, senseAxis.getAttribute("senseOne"));
		assertEquals(sense_id, senseAxis.getAttribute("senseTwo"));
		assertEquals(synset_id, senseAxis.getAttribute("synsetOne"));
		assertEquals(synset_id, senseAxis.getAttribute("synsetTwo"));
		assertEquals(senseAxis_senseAxisType.toString(), senseAxis.getAttribute("senseAxisType"));
		
		Element senseAxisRelation = checkHasSingleChild(senseAxis, SenseAxisRelation.class);
		assertEquals(senseAxis_id, senseAxisRelation.getAttribute("target"));
		assertEquals(senseAxisRelation_type, senseAxisRelation.getAttribute("relType"));
		assertEquals(senseAxisRelation_name, senseAxisRelation.getAttribute("relName"));
		
		
	}

	/**
	 * Test the values of attributes and children of a {@link Lexicon} in the
	 * consumed lexicon {@link Element}.
	 * 
	 * @param lexicon the node of the lexicon
	 */
	private void checkLexicon(Element lexicon) {
		assertEquals(lexicon_languageIdentifier.toString(), lexicon.getAttribute("languageIdentifier"));
		assertEquals(lexicon_name, lexicon.getAttribute("name"));
		assertEquals(lexicon_id, lexicon.getAttribute("id"));
		
		NodeList nlLexicalEntry = lexicon.getElementsByTagName("LexicalEntry");
		assertEquals("LexicalResource should have one LexicalEntry instance", 1, nlLexicalEntry.getLength());
		checkLexicalEntry((Element) nlLexicalEntry.item(0));
		
		Element subcategorizationFrame = checkHasSingleChild(lexicon, SubcategorizationFrame.class);
		assertEquals(subcategorizationFrame_id, subcategorizationFrame.getAttribute("id"));
		assertEquals(subcategorizationFrame_id, subcategorizationFrame.getAttribute("parentSubcatFrame"));
		assertEquals(subcategorizationFrame_subcatLabel, subcategorizationFrame.getAttribute("subcatLabel"));
		
		Element lexemeProperty = checkHasSingleChild(subcategorizationFrame, LexemeProperty.class);
		assertEquals(lexemeProperty_auxiliary.toString(), lexemeProperty.getAttribute("auxiliary"));
		assertEquals(lexemePropert_syntacticProperty.toString(), lexemeProperty.getAttribute("syntacticProperty"));
		
		Element syntacticArgument = checkHasSingleChild(subcategorizationFrame, SyntacticArgument.class);
		assertEquals(syntacticArgument_id, syntacticArgument.getAttribute("id"));
		if(syntacticArgument_optional)
			assertEquals(EYesNo.yes.toString(), syntacticArgument.getAttribute("optional"));
		else
			assertEquals(EYesNo.no.toString(), syntacticArgument.getAttribute("optional"));
		assertEquals(syntacticArgument_grammaticalFunction.toString(), syntacticArgument.getAttribute("grammaticalFunction"));
		assertEquals(syntacticArgument_syntacticCategory.toString(), syntacticArgument.getAttribute("syntacticCategory"));
		assertEquals(syntacticArgument_case.toString(), syntacticArgument.getAttribute("case"));
		assertEquals(syntacticArgument_determiner.toString(), syntacticArgument.getAttribute("determiner"));
		assertEquals(syntacticArgument_preposition, syntacticArgument.getAttribute("preposition"));
		assertEquals(syntacticArgument_prepositionType.toString(), syntacticArgument.getAttribute("prepositionType"));
		assertEquals(syntacticArgument_number.toString(), syntacticArgument.getAttribute("number"));
		assertEquals(syntacticArgument_lexeme, syntacticArgument.getAttribute("lexeme"));
		assertEquals(syntacticArgument_verbForm.toString(), syntacticArgument.getAttribute("verbForm"));
		assertEquals(syntacticArgument_tense.toString(), syntacticArgument.getAttribute("tense"));
		assertEquals(syntacticArgument_complementizer.toString(), syntacticArgument.getAttribute("complementizer"));
		checkHasSingleFrequency(syntacticArgument);
			
		checkHasSingleFrequency(subcategorizationFrame);
		
		Element subcategorizationFrameSet = checkHasSingleChild(lexicon, SubcategorizationFrameSet.class);
		assertEquals(subcategorizationFrameSet_id, subcategorizationFrameSet.getAttribute("id"));
		assertEquals(subcategorizationFrameSet_name, subcategorizationFrameSet.getAttribute("name"));
		assertEquals(subcategorizationFrameSet_id, subcategorizationFrameSet.getAttribute("parentSubcatFrameSet"));
		
		Element subcatFrameSetElement = checkHasSingleChild(subcategorizationFrameSet, SubcatFrameSetElement.class);
		assertEquals(subcategorizationFrame_id, subcatFrameSetElement.getAttribute("element"));
		
		Element synArgMap = checkHasSingleChild(subcategorizationFrameSet, SynArgMap.class);
		assertEquals(syntacticArgument_id, synArgMap.getAttribute("arg1"));
		assertEquals(syntacticArgument_id, synArgMap.getAttribute("arg2"));
		
		Element semanticPredicate = checkHasSingleChild(lexicon, SemanticPredicate.class);
		assertEquals(semanticPredicate_id, semanticPredicate.getAttribute("id"));
		assertEquals(semanticPredicate_label, semanticPredicate.getAttribute("label"));
		if(semanticPredicate_lexicalized)
			assertEquals(EYesNo.yes.toString(), semanticPredicate.getAttribute("lexicalized"));
		else
			assertEquals(EYesNo.no.toString(), semanticPredicate.getAttribute("lexicalized"));
		if(semanticPredicate_perspectivalized)
			assertEquals(EYesNo.yes.toString(), semanticPredicate.getAttribute("perspectivalized"));
		else
			assertEquals(EYesNo.no.toString(), semanticPredicate.getAttribute("perspectivalized"));
		checkHasSingleDefinition(semanticPredicate, SemanticPredicate.class.getCanonicalName());
		
		Element semanticArgument = checkHasSingleChild(semanticPredicate, SemanticArgument.class);
		assertEquals(semanticArgument_id, semanticArgument.getAttribute("id"));
		assertEquals(semanticArgument_semanticRole.toString(), semanticArgument.getAttribute("semanticRole"));
		if(semanticArgument_isIncorporated)
			assertEquals(EYesNo.yes.toString(), semanticArgument.getAttribute("isIncorporated"));
		else
			assertEquals(EYesNo.no.toString(), semanticArgument.getAttribute("isIncorporated"));
		assertEquals(semanticArgument_coreType.toString(), semanticArgument.getAttribute("coreType"));
		
		Element argumentRelation = checkHasSingleChild(semanticArgument, ArgumentRelation.class);
		assertEquals(semanticArgument_id, argumentRelation.getAttribute("target"));
		assertEquals(argumentRelation_relType, argumentRelation.getAttribute("relType"));
		assertEquals(argumentRelation_relName, argumentRelation.getAttribute("relName"));
		
		checkHasSingleFrequency(semanticArgument);
		checkHasSingleSemanticLabel(semanticArgument);
		checkHasSingleDefinition(semanticArgument, SemanticArgument.class.getCanonicalName());
		
		Element predicateRelation = checkHasSingleChild(semanticPredicate, PredicateRelation.class);
		assertEquals(semanticPredicate_id, predicateRelation.getAttribute("target"));
		assertEquals(semanticPredicate_id, predicateRelation.getAttribute("relevantSemanticPredicate"));
		assertEquals(predicateRelation_relType, predicateRelation.getAttribute("relType"));
		assertEquals(predicateRelation_relName, predicateRelation.getAttribute("relName"));
		
		checkHasSingleFrequency(semanticArgument);
		checkHasSingleSemanticLabel(semanticArgument);
		
		Element synset = checkHasSingleChild(lexicon, Synset.class);
		assertEquals(synset_id, synset.getAttribute("id"));
		checkHasSingleDefinition(synset, Synset.class.getCanonicalName());
		
		Element synsetRelation = checkHasSingleChild(synset, SynsetRelation.class);
		assertEquals(synset_id, synsetRelation.getAttribute("target"));
		assertEquals(synsetRelation_relName, synsetRelation.getAttribute("relName"));
		assertEquals(synsetRelation_relType.toString(), synsetRelation.getAttribute("relType"));
		checkHasSingleFrequency(synsetRelation);
		checkHasSingleMonolingualExternalRef(synset);
		
		Element synSemCorrespondence = checkHasSingleChild(lexicon, SynSemCorrespondence.class);
		assertEquals(synSemCorrespondences_id, synSemCorrespondence.getAttribute("id"));
		
		Element synSemArgMap = checkHasSingleChild(synSemCorrespondence, SynSemArgMap.class);
		assertEquals(syntacticArgument_id, synSemArgMap.getAttribute("syntacticArgument"));
		assertEquals(semanticArgument_id, synSemArgMap.getAttribute("semanticArgument"));
		
		checkHasSingleChild(lexicon, ConstraintSet.class);
	}

	/**
	 * Test the values of attributes and children of a {@link LexicalEntry} in the
	 * consumed lexicalEntry {@link Element}.
	 * 
	 * @param lexical entry the node of the lexical entry
	 */
	private void checkLexicalEntry(Element lexicalEntry) {
		assertEquals(lexicalEntry_id, lexicalEntry.getAttribute("id"));
		assertEquals(lexicalEntry_partOfSpeech.toString(), lexicalEntry.getAttribute("partOfSpeech"));
		assertEquals(lexicalEntry_separableParticle, lexicalEntry.getAttribute("separableParticle"));
		assertEquals(lexicon_id, lexicalEntry.getAttribute("lexicon"));
		
		NodeList nlLemma = lexicalEntry.getElementsByTagName("Lemma");
		assertEquals("LexicalEntry should have one Lemma instance", 1, nlLemma.getLength());
		checkLemma((Element) nlLemma.item(0));
		
		NodeList nlWordForm = lexicalEntry.getElementsByTagName("WordForm");
		assertEquals("LexicalEntry should have one WordForm instance", 1, nlWordForm.getLength());
		checkWordForm((Element) nlWordForm.item(0));
		
		NodeList nlRelatedForm = lexicalEntry.getElementsByTagName("RelatedForm");
		assertEquals("LexicalEntry should have one RelatedForm instance", 1, nlRelatedForm.getLength());
		checkRelatedForm((Element) nlRelatedForm.item(0));
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		NodeList nlSense = null;
		try {
			nlSense = (NodeList) xpath.evaluate("Sense", lexicalEntry,
			    XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			fail(e.toString());
		}
		
		assertEquals("LexicalEntry should have one RelatedForm instance", 1, nlSense.getLength());
		checkSense((Element) nlSense.item(0), true);
		
		Element syntacticBehaviour = checkHasSingleChild(lexicalEntry, SyntacticBehaviour.class);
		assertEquals(syntacticBehaviour_id, syntacticBehaviour.getAttribute("id"));
		assertEquals(sense_id, syntacticBehaviour.getAttribute("sense"));
		assertEquals(subcategorizationFrame_id, syntacticBehaviour.getAttribute("subcategorizationFrame"));
		assertEquals(subcategorizationFrameSet_id, syntacticBehaviour.getAttribute("subcategorizationFrameSet"));
		
		Element listOfComponents = checkHasSingleChild(lexicalEntry, ListOfComponents.class);
		Element component = checkHasSingleChild(listOfComponents, Component.class);
		assertEquals(lexicalEntry_id, component.getAttribute("targetLexicalEntry"));
		
		if(component_isHead)
			assertEquals(EYesNo.yes.toString(), component.getAttribute("isHead"));
		else
			assertEquals(EYesNo.no.toString(), component.getAttribute("isHead"));
		
		assertEquals(Integer.toString(component_position), component.getAttribute("position"));
		
		if(component_isBreakBefore)
			assertEquals(EYesNo.yes.toString(), component.getAttribute("isBreakBefore"));
		else
			assertEquals(EYesNo.no.toString(), component.getAttribute("isBreakBefore"));
		
		checkHasSingleFrequency(lexicalEntry);
	}

	/**
	 * Test the values of attributes and children of a {@link Sense} in the
	 * consumed sense {@link Element}.
	 * 
	 * @param sense the node of the sense
	 * @param hasNestedSense set to true if the method should recursively check the
	 * correctness of the nested sense, which can be retrived by invoking
	 * {@link Sense#getSenses()}
	 */
	private void checkSense(Element sense, boolean hasNestedSense) {
		
		if(hasNestedSense)
			assertEquals(sense_id, sense.getAttribute("id"));
		else
			assertEquals(sense_sense_id, sense.getAttribute("id"));
		
		assertEquals(Integer.toString(sense_index), sense.getAttribute("index"));
		assertEquals(sense_synset.getId(), sense.getAttribute("synset"));
		assertEquals(sense_incorporatedSemArg.getId(), sense.getAttribute("incorporatedSemArg"));
		
		if(sense_transparentMeaning)
			assertEquals(EYesNo.yes.toString(), sense.getAttribute("transparentMeaning"));
		else
			assertEquals(EYesNo.no.toString(), sense.getAttribute("transparentMeaning"));
		
		assertEquals(lexicalEntry_id, sense.getAttribute("lexicalEntry"));
		
		if(hasNestedSense){
			NodeList nlNestedSense = sense.getElementsByTagName("Sense");
			assertEquals("Sense should have one nested Sense instance", 1, nlNestedSense.getLength());
			checkSense((Element) nlNestedSense.item(0), false);
		}
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		NodeList nlContext = null;
		try {
			nlContext = (NodeList) xpath.evaluate("Context", sense,
			    XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			fail(e.toString());
		}
		assertEquals("Sense should have one Context instance", 1, nlContext.getLength());
		checkContext((Element) nlContext.item(0));
		
		Element predicativeRepresentation = checkHasSingleChild(sense, PredicativeRepresentation.class);
		assertEquals(semanticPredicate_id, predicativeRepresentation.getAttribute("predicate"));
		
		Element senseExample = checkHasSingleChild(sense, SenseExample.class);
		assertEquals(senseExample_id, senseExample.getAttribute("id"));
		assertEquals(senseExample_exampleType.toString(), senseExample.getAttribute("exampleType"));
		checkHasSingleTextRepresentation(senseExample, SenseExample.class.toString());
		
		checkHasSingleDefinition(sense, Sense.class.toString());
		
		Element senseRelation = checkHasSingleChild(sense, SenseRelation.class);
		assertEquals(sense_id, senseRelation.getAttribute("source"));
		assertEquals(sense_id, senseRelation.getAttribute("target"));
		assertEquals(senseRelation_relationName, senseRelation.getAttribute("relName"));
		assertEquals(senseRelation_relationType.toString(), senseRelation.getAttribute("relType"));
		checkHasSingleFormRepresentation(senseRelation, SenseRelation.class.toString());
		checkHasSingleFrequency(senseRelation);
		
		checkHasSingleMonolingualExternalRef(sense);
		
		checkHasSingleFrequency(sense);
		
		checkHasSingleSemanticLabel(sense);
		
	}
	
	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link SemanticLabel} instance,
	 * attached. Subsequently, the method checks the content of the semantic label instance.
	 * 
	 * @param  lmfClassInstance the element representing an UBY-lMF class instance which
	 * should have exactly one semantic label element attached
	 */
	private void checkHasSingleSemanticLabel(Element lmfClassInstance) {
		Element semanticLabel = checkHasSingleChild(lmfClassInstance, SemanticLabel.class);
		assertEquals(semanticLabel_label, semanticLabel.getAttribute("label"));
		assertEquals(semanticLabel_quantification, semanticLabel.getAttribute("quantification"));
		assertEquals(semanticLabel_type.toString(), semanticLabel.getAttribute("type"));
		assertEquals(lmfClassInstance.getAttribute("id"), semanticLabel.getAttribute("parent"));
		checkHasSingleMonolingualExternalRef(semanticLabel);
	}

	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link Frequency} instance,
	 * attached. Subsequently, the method checks the content of the frequency instance.
	 * 
	 * @param  lmfClassInstance the element representing an UBY-lMF class instance which
	 * should have exactly one Frequency element attached
	 */
	private void checkHasSingleFrequency(Element lmfClassInstance) {
		Element frequency = checkHasSingleChild(lmfClassInstance, Frequency.class);
		checkFrequency(frequency);
	}

	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link Definition} instance,
	 * attached. Subsequently, the method checks the content of the definition instance.
	 * 
	 * @param  lmfClassInstance the element representing an UBY-lMF class instance which
	 * should have exactly one Definition element attached
	 * @param className string used for generating a message on failure, represents
	 * the name of the UBY-LMF class instance which is being tested 
	 */
	private void checkHasSingleDefinition(Element lmfClassInstance, String className) {
		Element definition = checkHasSingleChild(lmfClassInstance, Definition.class);
		assertEquals(definition_definitionType.toString(), definition.getAttribute("definitionType"));
		
		Element statement = checkHasSingleChild(definition, Statement.class);
		assertEquals(statement_statementType.toString(), statement.getAttribute("statementType"));
		checkHasSingleTextRepresentation(statement, Statement.class.toString());
		
		checkHasSingleTextRepresentation(definition, Definition.class.toString());
	}

	/**
	 * Consumes a parent {@link Element} and the {@link Class} of the child.
	 * Checks if the parent element contains exactly one child element of the
	 * of the consumed class.
	 * 
	 * @param parent the parent which should contain one child
	 * @param child the class describing the child
	 * @return child element or null if the parent does not contain the specified child
	 * 
	 */
	private Element checkHasSingleChild(Element parent, Class<?> child){
		String parentName = parent.getNodeName();
		String childName = child.getSimpleName();
		NodeList nodeList = null;
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			nodeList = (NodeList) xpath.evaluate(childName, parent,
			    XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			fail(e.toString());
		}
		
		assertEquals(parentName + " should have one " + childName, 1, nodeList.getLength());
		return (Element) nodeList.item(0);
	}

	/**
	 * Test the values of attributes and children of a {@link Context} in the
	 * consumed context {@link Element}.
	 * 
	 * @param context the node of the context
	 */
	private void checkContext(Element context) {
		assertEquals(context_contextType.toString(), context.getAttribute("contextType"));
		assertEquals(context_source, context.getAttribute("source"));
		
		checkHasSingleTextRepresentation(context, "Context");
		
		checkHasSingleMonolingualExternalRef(context);
	}
	
	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link MonolingualExternalRef} instance,
	 * attached. Subsequently, the method checks the content of the monolingual external
	 * reference.
	 * 
	 * @param  lmfClassIntance the element representing an UBY-lMF class instance which
	 * should have exactly one MonolingualExternalRef element attached
	 */
	private void checkHasSingleMonolingualExternalRef(Element lmfClassInstance) {
		
		Element monolingualExternalRef = checkHasSingleChild(lmfClassInstance, MonolingualExternalRef.class);
		checkMonolingualExternalRef(monolingualExternalRef);
	}

	/**
	 * Test the values of attributes and children of a {@link MonolingualExternalRef} in the
	 * consumed monolingual external reference {@link Element}.
	 * 
	 * @param monolingualExternalRef the node of the monolingual external reference
	 */
	private void checkMonolingualExternalRef(Element monolingualExternalRef) {
		assertEquals(monolingualExternalRef_externalSystem, monolingualExternalRef.getAttribute("externalSystem"));
		assertEquals(monolingualExternalRef_externalReference, monolingualExternalRef.getAttribute("externalReference"));
	}

	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link TextRepresentation} instance,
	 * attached. Subsequently, the method checks the content of the text representation.
	 * 
	 * @param  lmfClassIntance the element representing an UBY-lMF class instance which
	 * should have exactly one TextRepresentation element attached
	 * @param className string used for generating a message on failure, represents
	 * the name of the UBY-LMF class instance which is being tested 
	 */
	private void checkHasSingleTextRepresentation(Element lmfClassInstance, String className){
		
		Element textRepresentation = checkHasSingleChild(lmfClassInstance, TextRepresentation.class);
		checkTextRepresentation(textRepresentation);
		
	}

	/**
	 * Test the values of attributes and children of a {@link TextRepresentation} in the
	 * consumed text representation {@link Element}.
	 * 
	 * @param textRepresentation the node of the text representation
	 */
	private void checkTextRepresentation(Element textRepresentation) {
		
		assertEquals(textRepresentation_orthographyName, textRepresentation.getAttribute("orthographyName"));
		assertEquals(textRepresentation_geographicalVariant, textRepresentation.getAttribute("geographicalVariant"));
		assertEquals(textRepresentation_languageIdentifier.toString(), textRepresentation.getAttribute("languageIdentifier"));
		assertEquals(textRepresentation_writtenText, textRepresentation.getAttribute("writtenText"));
		
	}

	/**
	 * Test the values of attributes and children of a {@link RelatedForm} in the
	 * consumed related form {@link Element}.
	 * 
	 * @param relatedForm the node of the related form
	 */
	private void checkRelatedForm(Element relatedForm) {
		assertEquals(targetLexicalEntry.getId(), relatedForm.getAttribute("targetLexicalEntry"));
		assertEquals(targetSense.getId(), relatedForm.getAttribute("targetSense"));
		assertEquals(relatedForm_relType.toString(), relatedForm.getAttribute("relType"));
		
		checkHasSingleFormRepresentation(relatedForm, "RelatedForm");
	}

	/**
	 * Test the values of attributes and children of a {@link WordForm} in the
	 * consumed word form {@link Element}.
	 * 
	 * @param wordForm entry the node of the word form
	 */
	private void checkWordForm(Element wordForm) {
		
		assertEquals(wordForm_grammaticalNumber.toString(), wordForm.getAttribute("grammaticalNumber"));
		assertEquals(wordForm_grammaticalGender.toString(), wordForm.getAttribute("grammaticalGender"));
		assertEquals(wordForm_case.toString(), wordForm.getAttribute("case"));
		assertEquals(wordForm_person.toString(), wordForm.getAttribute("person"));
		assertEquals(wordForm_tense.toString(), wordForm.getAttribute("tense"));
		assertEquals(wordForm_verbFormMoode.toString(), wordForm.getAttribute("verbFormMood"));
		assertEquals(wordForm_degree.toString(), wordForm.getAttribute("degree"));
		
		NodeList nFormRepresentation = wordForm.getElementsByTagName("FormRepresentation");
		assertEquals("WordForm should have one FormRepresentation", 1, nFormRepresentation.getLength());
		checkFormRepresentation((Element) nFormRepresentation.item(0));
		
		NodeList nFrequency = wordForm.getElementsByTagName("Frequency");
		assertEquals("WordForm should have one Frequency", 1, nFrequency.getLength());
		checkFrequency((Element) nFrequency.item(0));
		
	}
	
	/**
	 * Test the values of attributes and children of a {@link Frequency} in the
	 * consumed frequency {@link Element}.
	 * 
	 * @param frequency the node of the frequency
	 */
	private void checkFrequency(Element frequency) {
		assertEquals(frequency_corpus, frequency.getAttribute("corpus"));
		assertEquals(Integer.toString(frequency_frequancy), frequency.getAttribute("frequency"));
		assertEquals(frequency_generator, frequency.getAttribute("generator"));
	}

	/**
	 * Test the values of attributes and children of a {@link Lemma} in the
	 * consumed lemma {@link Element}.
	 * 
	 * @param lemma the node of the lemma
	 */
	private void checkLemma(Element lemma) {
		assertEquals(lexicalEntry_id, lemma.getAttribute("lexicalEntry"));
		
		NodeList nFormRepresentation = lemma.getElementsByTagName("FormRepresentation");
		assertEquals("Lemma should have one FormRepresentation", 1, nFormRepresentation.getLength());
		checkFormRepresentation((Element) nFormRepresentation.item(0));
	}
	
	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link FormRepresentation} instance,
	 * attached. Subsequently, the method checks the content of the form representation.
	 * 
	 * @param  lmfClassIntance the element representing an UBY-lMF class instnace which
	 * should have exactly one FormRepresentation element attached
	 * @param className string used for generating a message on failure, represents
	 * the name of the UBY-LMF class instance which is being tested 
	 */
	private void checkHasSingleFormRepresentation(Element lmfClassInstance, String className){
		
		NodeList nFormRepresentation = lmfClassInstance.getElementsByTagName("FormRepresentation");
		assertEquals(className + "should have one FormRepresentation", 1, nFormRepresentation.getLength());
		checkFormRepresentation((Element) nFormRepresentation.item(0));
	}

	/**
	 * Test the values of attributes and children of a {@link FormRepresentation} in the
	 * consumed formRepresentation {@link Element}.
	 * 
	 * @param formRepresentation the node of the form representation
	 */
	private void checkFormRepresentation(Element formRepresentation) {
		assertEquals(formRepresenataion_orthographyName, formRepresentation.getAttribute("orthographyName"));
		assertEquals(formRepresentation_geographicalVariant, formRepresentation.getAttribute("geographicalVariant"));
		assertEquals(formRepresentation_hyphenation, formRepresentation.getAttribute("hyphenation"));
		assertEquals(formRepresentation_languageIdentifier.toString(), formRepresentation.getAttribute("languageIdentifier"));
		assertEquals(formRepresentation_phoneticForm, formRepresentation.getAttribute("phoneticForm"));
		assertEquals(formRepresentation_sound, formRepresentation.getAttribute("sound"));
		assertEquals(formRepresentation_writtenForm, formRepresentation.getAttribute("writtenForm"));
	}

	/**
	 * Test the values of attributes and children of a {@link GlobalInformation} in the
	 * consumed global information {@link Element}.
	 * 
	 * @param globalInformation the element of the global information
	 */
	private void checkGlobalInformation(Element globalInformation) {
		assertEquals(globalInformation_label, globalInformation.getAttribute("label"));
	}

	/**
	 * Sets the memory consuming private fields of this class to <code>null</code>.
	 */
	@AfterClass
	public static void tearDownAfterClass(){
		lexicalResource = null;
	}

}
