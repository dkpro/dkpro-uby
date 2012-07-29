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
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EDegree;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.EPerson;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbFormMood;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
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
		lexicon.addLexicalEntry(lexicalEntry);
		
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
		assertEquals("lexical resource should only have one GlobalInformation instance", 1, nlGlobalInformation.getLength());
		checkGlobalInformation((Element)nlGlobalInformation.item(0));
		
		NodeList nlLexicon = lexicalResource.getElementsByTagName("Lexicon");
		assertEquals("LexicalResource should only have one Lexicon instance", 1, nlLexicon.getLength());
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
		
		NodeList nlLexicalEntry = lexicon.getElementsByTagName("LexicalEntry");
		assertEquals("LexicalResource should only have one LexicalEntry instance", 1, nlLexicalEntry.getLength());
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
		
		NodeList nlLemma = lexicalEntry.getElementsByTagName("Lemma");
		assertEquals("LexicalEntry should only have one Lemma instance", 1, nlLemma.getLength());
		checkLemma((Element) nlLemma.item(0));
		
		NodeList nlWordForm = lexicalEntry.getElementsByTagName("WordForm");
		assertEquals("LexicalEntry should only have one WordForm instance", 1, nlWordForm.getLength());
		checkWordForm((Element) nlWordForm.item(0));
		// TODO the rest
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
		assertEquals("WordForm should only have one FormRepresentation", 1, nFormRepresentation.getLength());
		checkFormRepresentation((Element) nFormRepresentation.item(0));
		
		// TODO check frequencies
		
	}

	/**
	 * Test the values of attributes and children of a {@link Lemma} in the
	 * consumed lemma {@link Element}.
	 * 
	 * @param lemma the node of the lemma
	 */
	private void checkLemma(Element lemma) {
		NodeList nFormRepresentation = lemma.getElementsByTagName("FormRepresentation");
		assertEquals("Lemma should only have one FormRepresentation", 1, nFormRepresentation.getLength());
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
