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

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EContextType;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.EPerson;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.mrd.Context;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
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
	
	private static final ELanguageIdentifier lexicon_languageIdentifier = ELanguageIdentifier.en;
	private static final String lexicon_name = "lexicon_name";
	private static final String lexicon_id = "lexicon_id";
	
	private static final String lexicalEntry_id = "lexicalEntry_id";
	private static final EPartOfSpeech lexicalEntry_partOfSpeech = EPartOfSpeech.adjective;
	private static final String lexicalEntry_separableParticle = "lexicalEntry_separableParticle";
	
	private static final ELanguageIdentifier formRepresentation_languageIdentifier = ELanguageIdentifier.de;
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
	
	private static final ELanguageIdentifier textRepresentation_languageIdentifier = ELanguageIdentifier.sr;
	private static final String textRepresentation_orthographyName = "textRepresentation_orthographyName";
	private static final String textRepresentation_geographicalVariant = "textRepresentation_geographicalVariant";
	private static final String textRepresentation_writtenText = "textRepresentation_writtenText";
	
	private static final String monolingualExternalRef_externalSystem = "monolingualExternalRef_externalSystem";
	private static final String monolingualExternalRef_externalReference = "monolingualExternalRef_externalReference"; 

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
		// TODO check the parent of the lexicon
		
		NodeList nlLexicalEntry = lexicon.getElementsByTagName("LexicalEntry");
		assertEquals("LexicalResource should have one LexicalEntry instance", 1, nlLexicalEntry.getLength());
		checkLexicalEntry((Element) nlLexicalEntry.item(0));
		
		// TODO the rest
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
		
		// TODO the rest
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
		
		// TODO rest
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
		
//		FIXME
//		checkHasSingleMonolingualExternalRef(context, "Context");
		// TODO the rest
		
	}
	
	/**
	 * Test if the consumed {@link Element}, representing a UBY-LMF class,
	 * has one Element which represents a {@link MonolingualExternalRef} instance,
	 * attached. Subsequently, the method checks the content of the monolingual external
	 * reference.
	 * 
	 * @param  lmfClassIntance the element representing an UBY-lMF class instance which
	 * should have exactly one MonolingualExternalRef element attached
	 * @param className string used for generating a message on failure, represents
	 * the name of the UBY-LMF class instance which is being tested 
	 */
	private void checkHasSingleMonolingualExternalRef(Element lmfClassInstance, String className) {
		
		NodeList nlMonolingualExternalRef = lmfClassInstance.getElementsByTagName("MonolingualExternalRef");
		assertEquals(className + " should have one MonolingualExternalRef", 1, nlMonolingualExternalRef.getLength());
		checkMonolingualExternalRef((Element) nlMonolingualExternalRef.item(0));
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
		
		NodeList nlTextRepresentation = lmfClassInstance.getElementsByTagName("TextRepresentation");
		assertEquals(className + "should have one TextRepresentation", 1, nlTextRepresentation.getLength());
		checkTextRepresentation((Element) nlTextRepresentation.item(0));
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
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// TODO
	}

}
