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
package de.tudarmstadt.ukp.lmf.transform.imslex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcatFrameSetElement;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
/**
 * This class extracts information from a preprocessed version of IMSLex - Subcategorization Frames and fills in the corresponding LMF classes
 * @author Eckle-Kohler
 *
 */
public class IMSlexExtractor {

	public Lexicon lexicon = new Lexicon();
	
	private List<SynSemCorrespondence> synSemCorrespondences = new LinkedList<SynSemCorrespondence>();
	private List<SemanticPredicate> semanticPredicates = new LinkedList <SemanticPredicate>();
	private List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
	
	private File lexiconInputFile; // The File containing the preprocessed Input lexicon
	private String resourceName; // name of the LMF lexicon, i.e. "IMSLexSubcat"

	// running numbers for IDs
	private static int lexicalEntryNumber = 0; 
	private static int senseNumber = 0; 
	private static int syntacticBehaviourNumber = 0; 
	private static int subcatFrameSetNumber = 0; 
	private static int subcatFrameNumber = 0; 
	private static int syntacticArgumentNumber = 0; 
	private static int semanticPredicateNumber = 0; 
	private static int semanticArgumentNumber = 0; 
	private static int synSemCorrespondenceNumber = 0; 
	
	// Mapping between lemmas and their corresponding IMSlex senses
	private static HashMap<String, HashSet<IMSlexSense>> verbLemmaIMSlexSenseMappings = new HashMap<String, HashSet<IMSlexSense>>();
	private static HashMap<String, HashSet<IMSlexSense>> adjLemmaIMSlexSenseMappings = new HashMap<String, HashSet<IMSlexSense>>();
	private static HashMap<String, HashSet<IMSlexSense>> nounLemmaIMSlexSenseMappings = new HashMap<String, HashSet<IMSlexSense>>();	
	// Mapping between syntactic/semantic arguments (containing sem. role information) and purely syntactic arguments
	private static HashMap<String, String> synSemArgSynArgMapping  = new HashMap<String, String>();	
	// Mapping between LMF-Code of purely syntactic SC-Frame and SubcategorizationFrame
	private static HashMap<String, SubcategorizationFrame> synargsSubcatFrameMap  = new HashMap<String, SubcategorizationFrame>();	
	// Mapping between className and SubcategorizationFrameSet
	private static HashMap<String, SubcategorizationFrameSet> classSubcatFrameSetMap  = new HashMap<String, SubcategorizationFrameSet>();
	// Mapping between className and set of SubcategorizationFrames
	private static HashMap<String, HashSet<SubcategorizationFrame>> classSCframeElementsMap  = new HashMap<String, HashSet<SubcategorizationFrame>>();	
	// Mapping between IMSlexSense and SubcategorizationFrameSet
	private static HashMap<IMSlexSense, SubcategorizationFrameSet> senseSubcatFrameSetMap  = new HashMap<IMSlexSense, SubcategorizationFrameSet>();
	// Mapping between IMSlexSense and SemanticPredicate
	private static HashMap<IMSlexSense, SemanticPredicate> senseSemPredicateMap  = new HashMap<IMSlexSense, SemanticPredicate>();

	// Mapping between IMSlex syntactic information and semantic class information
	private static HashMap<String, String> syntaxSemClassMap  = new HashMap<String, String>();
	
	private static List<IMSlexSense> listOfIMSlexSenses = new LinkedList <IMSlexSense>();

	/**
	 * Constructs a IMSlexExtractor
	 * @param preprocessedLexicon path of the File containing the preprocessed version of IMSlex
	 * @param resourceName name of the LMF Lexicon instance: "IMSLexSubcat"
	 * @return IMSlexExtractor
	 * @throws IOException 
	 */
	public IMSlexExtractor(File preprocessedLexicon, String resourceName) throws IOException {

		this.lexiconInputFile = preprocessedLexicon;
		this.resourceName = resourceName;
		parsePreprocessedIMSlex();
		convertIMSlex();
	}
	
	/**
	 * This method parses the document containing the lexicon Input
	 * Input has the form: <lemma>%<pos>%<Arg>:...:<Arg>%classInformation
	 * 
	 * @throws IOException
	 */
	private void parsePreprocessedIMSlex() throws IOException {
		System.out.print("Parsing preprocessed IMSlex ...");
		Reader r = new InputStreamReader(new FileInputStream(lexiconInputFile), "UTF8");
		BufferedReader input = new BufferedReader(r);
		try {
			String line;
			String[] parts;
			HashSet<IMSlexSense> imsLexSenses = new HashSet<IMSlexSense>(); // senses that have already been processed for one lemma

			while ((line = input.readLine()) != null) {
				parts = line.split("%");
				IMSlexSense imsLexSense = new IMSlexSense(parts[0],parts[1],null,parts[2],parts[3]);
				if (imsLexSense.synArgs.contains("semanticLabel")) {
					imsLexSense.synArgs = "null";
					imsLexSense.classInformation = parts[2];
				}

				if (!imsLexSense.synArgs.equals("null") || !imsLexSense.classInformation.equals("null")) {
					// skip adjectives without SCF and semantic class information
					if (syntaxSemClassMap.containsKey(imsLexSense.lemma+"%"+imsLexSense.pos+"%"+imsLexSense.synArgs)) { // there is already an IMSlexSense with the same syntax
						if (!imsLexSense.classInformation.equals("null") && syntaxSemClassMap.get(imsLexSense.lemma+"%"+imsLexSense.pos+"%"+imsLexSense.synArgs).equals("null")) {
							//if the class of the existing entry is null, but the class of the new IMSlexSense is not null:
							//replace the null with the class information
							syntaxSemClassMap.put(imsLexSense.lemma+"%"+imsLexSense.pos+"%"+imsLexSense.synArgs, imsLexSense.classInformation);
						}
					} else {
						syntaxSemClassMap.put(imsLexSense.lemma+"%"+imsLexSense.pos+"%"+imsLexSense.synArgs, imsLexSense.classInformation);
					}

					if (imsLexSense.synArgs.contains("role")) {
						String pureSynArgs = imsLexSense.synArgs.replaceFirst(",role=[a-z]+", "");
						synSemArgSynArgMapping.put(imsLexSense.synArgs, pureSynArgs);
					} else {
						synSemArgSynArgMapping.put(imsLexSense.synArgs, imsLexSense.synArgs);	
					}
				}
			}

			Iterator<String> syntaxIterator = syntaxSemClassMap.keySet().iterator();
			while (syntaxIterator.hasNext()) {
				String syntax = syntaxIterator.next();
				parts = syntax.split("%");
				IMSlexSense sense = new IMSlexSense(parts[0],parts[1],null,parts[2],syntaxSemClassMap.get(syntax));
				listOfIMSlexSenses.add(sense);
			}

			for	(IMSlexSense imsLexSense : listOfIMSlexSenses) {

				if (imsLexSense.pos.equals("verb")) {
					if (verbLemmaIMSlexSenseMappings.containsKey(imsLexSense.lemma)) {
						imsLexSenses = verbLemmaIMSlexSenseMappings.get(imsLexSense.lemma);
						imsLexSenses.add(imsLexSense);
						verbLemmaIMSlexSenseMappings.put(imsLexSense.lemma,imsLexSenses);
					} else {
						HashSet<IMSlexSense> newSense = new HashSet<IMSlexSense>();
						newSense.add(imsLexSense);
						verbLemmaIMSlexSenseMappings.put(imsLexSense.lemma,newSense);
					}
				} else if (imsLexSense.pos.equals("adj")) {
					if (adjLemmaIMSlexSenseMappings.containsKey(imsLexSense.lemma)) {
						imsLexSenses = adjLemmaIMSlexSenseMappings.get(imsLexSense.lemma);
						imsLexSenses.add(imsLexSense);
						adjLemmaIMSlexSenseMappings.put(imsLexSense.lemma,imsLexSenses);
					} else {
						HashSet<IMSlexSense> newSense = new HashSet<IMSlexSense>();
						newSense.add(imsLexSense);
						adjLemmaIMSlexSenseMappings.put(imsLexSense.lemma,newSense);
					}
				} else { //noun
					if (nounLemmaIMSlexSenseMappings.containsKey(imsLexSense.lemma)) {
						imsLexSenses = nounLemmaIMSlexSenseMappings.get(imsLexSense.lemma);
						imsLexSenses.add(imsLexSense);
						nounLemmaIMSlexSenseMappings.put(imsLexSense.lemma,imsLexSenses);
					} else {
						HashSet<IMSlexSense> newSense = new HashSet<IMSlexSense>();
						newSense.add(imsLexSense);
						nounLemmaIMSlexSenseMappings.put(imsLexSense.lemma,newSense);
					}
				}
			}


			System.out.println("done");
		} finally {
			r.close();
		}
	}
	
	/**
	 * This method creates LMF classes and
	 * fills in the extracted lexical information
	 * 
	 */
	private  void convertIMSlex() {
		lexicon.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
		lexicon.setId("IMSLexSubcat_Lexicon_0");
		lexicon.setName(resourceName);
		
		// Create subclasses of Lexicon that are independent of particular lexemes
		List<SubcategorizationFrame> subcategorizationFrames = new LinkedList <SubcategorizationFrame>();
		List<SubcategorizationFrameSet> subcategorizationFramesSets = new LinkedList <SubcategorizationFrameSet>();
		
		for	(IMSlexSense imsLexSense : listOfIMSlexSenses) {
			if (!imsLexSense.synArgs.equals("null")) {
				// Create SubcatFrames and SemanticPredicates
				String synArgs = synSemArgSynArgMapping.get(imsLexSense.synArgs);
	
				if (!synargsSubcatFrameMap.containsKey(synArgs)) {
					SubcategorizationFrame subcategorizationFrame = new SubcategorizationFrame();
					subcategorizationFrame.setId("IMSLexSubcat_SubcategorizationFrame_".concat(Integer.toString(subcatFrameNumber)));
					subcatFrameNumber++;	
					subcategorizationFrame = parseArguments(imsLexSense,subcategorizationFrame);
					
					synargsSubcatFrameMap.put(synArgs,subcategorizationFrame);	
					if (imsLexSense.synArgs.contains("role")) { //only few IMSLexSubcat-frames specify a semantic role
						SemanticPredicate semanticPredicate = new SemanticPredicate();
						semanticPredicate = parseSemanticArguments(imsLexSense,subcategorizationFrame);
						semanticPredicates.add(semanticPredicate);		
						
						senseSemPredicateMap.put(imsLexSense, semanticPredicate);
					} 
				} else {
					SubcategorizationFrame subcategorizationFrame = synargsSubcatFrameMap.get(synArgs);
					if (imsLexSense.synArgs.contains("role")) { //only few IMSlexSubcat-frames specify a semantic role
						SemanticPredicate semanticPredicate = new SemanticPredicate();
						semanticPredicate = parseSemanticArguments(imsLexSense,subcategorizationFrame);
						semanticPredicates.add(semanticPredicate);	
						
						senseSemPredicateMap.put(imsLexSense, semanticPredicate);
					} 				
				}
			}
		}

		for (IMSlexSense imsLexSense : listOfIMSlexSenses) {
			if (!imsLexSense.synArgs.equals("null") && !imsLexSense.classInformation.equals("null")) {
				// Create SubcatFrameSets
				if (!classSubcatFrameSetMap.containsKey(imsLexSense.classInformation)) {
					SubcategorizationFrameSet subcategorizationFrameSet = new SubcategorizationFrameSet();
					subcategorizationFrameSet.setId("IMSLexSubcat_SubcategorizationFrameSet_".concat(Integer.toString(subcatFrameSetNumber)));
					subcategorizationFrameSet.setName(imsLexSense.classInformation);
					subcatFrameSetNumber++;	
	
					classSubcatFrameSetMap.put(imsLexSense.classInformation,subcategorizationFrameSet);
					senseSubcatFrameSetMap.put(imsLexSense, subcategorizationFrameSet);
					
					if (classSCframeElementsMap.get(imsLexSense.classInformation) == null) {
						HashSet<SubcategorizationFrame> scFrames = new HashSet<SubcategorizationFrame>();
						scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(imsLexSense.synArgs)));
						
						classSCframeElementsMap.put(imsLexSense.classInformation, scFrames);
					} else {
						HashSet<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(imsLexSense.classInformation);
						scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(imsLexSense.synArgs)));
						classSCframeElementsMap.put(imsLexSense.classInformation, scFrames);
					}
				} else {
					senseSubcatFrameSetMap.put(imsLexSense, classSubcatFrameSetMap.get(imsLexSense.classInformation));
					if (classSCframeElementsMap.get(imsLexSense.classInformation) == null) {
						HashSet<SubcategorizationFrame> scFrames = new HashSet<SubcategorizationFrame>();
						scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(imsLexSense.synArgs)));
						
						classSCframeElementsMap.put(imsLexSense.classInformation, scFrames);
					} else {
						HashSet<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(imsLexSense.classInformation);
						scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(imsLexSense.synArgs)));
						classSCframeElementsMap.put(imsLexSense.classInformation, scFrames);
					}
				}
			}
		}
		
		if(classSubcatFrameSetMap != null ) {
			// Add SubcatFrameElements to SubcategorizationFrameSet	
			Iterator<String> classIterator = classSubcatFrameSetMap.keySet().iterator();
			while (classIterator.hasNext()) {
				String classKey = classIterator.next();
				SubcategorizationFrameSet subcatFrameSet = classSubcatFrameSetMap.get(classKey);
				if (classSCframeElementsMap.get(classKey) != null) {
					List<SubcatFrameSetElement> subcatFrameSetElements = new LinkedList<SubcatFrameSetElement>();
	
					Iterator<SubcategorizationFrame> frameIterator = classSCframeElementsMap.get(classKey).iterator();
					while (frameIterator.hasNext()) {
						SubcategorizationFrame scFrame = frameIterator.next();
						SubcatFrameSetElement subcatFrameSetElement = new SubcatFrameSetElement();
						subcatFrameSetElement.setElement(scFrame);
						subcatFrameSetElements.add(subcatFrameSetElement);
					}		
					subcatFrameSet.setSubcatFrameSetElements(subcatFrameSetElements);
					classSubcatFrameSetMap.put(classKey, subcatFrameSet);
				}
			}
		}
		
				
		subcategorizationFrames.addAll(synargsSubcatFrameMap.values());
		Collections.sort(subcategorizationFrames);
		lexicon.setSubcategorizationFrames(subcategorizationFrames);
		
		subcategorizationFramesSets.addAll(classSubcatFrameSetMap.values());
		lexicon.setSubcategorizationFrameSets(subcategorizationFramesSets); // might be null
		
		lexicon.setSemanticPredicates(semanticPredicates); // might be null
		
		lexicon.setSynSemCorrespondences(synSemCorrespondences); // might be null

		createLexicalEntries(verbLemmaIMSlexSenseMappings.keySet().iterator(),"verb");
		createLexicalEntries(adjLemmaIMSlexSenseMappings.keySet().iterator(),"adj");
		createLexicalEntries(nounLemmaIMSlexSenseMappings.keySet().iterator(),"noun");
		
		lexicon.setLexicalEntries(lexicalEntries);
		
		System.out.println("Statistics");
		System.out.println(lexicalEntryNumber+" LexicalEntries");
		System.out.println(senseNumber+" Senses");
		System.out.println(subcatFrameSetNumber+" SubcatFrameSets");
		System.out.println(subcatFrameNumber+" SubcategorizationFrames");
		System.out.println(syntacticArgumentNumber+" SyntacticArguments");
		System.out.println(semanticPredicateNumber+" SemanticPredicates");
		System.out.println(synSemCorrespondenceNumber+" SynSemCorrespondences");
		System.out.println(semanticArgumentNumber+" SemanticArguments");
}
	
	
	
	
	/**
	 * This method creates LexicalEntries and
	 * fills in lexical information
	 * 
	 */	
	private void createLexicalEntries(Iterator<String> iterator, String pos)
	{
		Iterator<IMSlexSense> senseIterator;

		while (iterator.hasNext()) {
			String sourceLemma = iterator.next();
			
			LexicalEntry lexicalEntry = new LexicalEntry();
			// Create ID
			lexicalEntry.setId("IMSLexSubcat_LexicalEntry_".concat(Integer.toString(lexicalEntryNumber)));
			lexicalEntryNumber++;		
			// Set partOfSpeech
			lexicalEntry.setPartOfSpeech(mapPOS(pos));	
			// Create Lemma 
			Lemma lemma = new Lemma();		
			// Create FormRepresentation
			List<FormRepresentation> formReps = new ArrayList<FormRepresentation>();
			FormRepresentation formRep = new FormRepresentation();
			if (sourceLemma.contains("#")) { // then it is a verb with separable prefix, the prefix marked by #
				String[] parts = sourceLemma.split("#");
				String prefix = parts[0];
				lexicalEntry.setSeparableParticle(prefix);
				String newVerbLemma = prefix.concat(parts[1]);
				formRep.setWrittenForm(newVerbLemma);
			} else {
				formRep.setWrittenForm(sourceLemma);	
			}
																							
			formReps.add(formRep);				// Save FormRepresentation		
			lemma.setFormRepresentations(formReps);	// Save FormRepresentations		
			lexicalEntry.setLemma(lemma);			// Save Lemma

			// Create Senses
			List <Sense> senses = new ArrayList<Sense>();
			// Create SyntacticBehavior 
			List<SyntacticBehaviour> syntacticBehaviours = new LinkedList <SyntacticBehaviour>();
			
			if (pos.equals("verb")) {
				senseIterator = verbLemmaIMSlexSenseMappings.get(sourceLemma).iterator();
			} else if (pos.equals("adj")) {
				senseIterator = adjLemmaIMSlexSenseMappings.get(sourceLemma).iterator();				
			} else {
				senseIterator = nounLemmaIMSlexSenseMappings.get(sourceLemma).iterator();								
			}
			while (senseIterator.hasNext()) {
				IMSlexSense imsLexSense = senseIterator.next();
				Sense sense = new Sense();

				sense.setId("IMSLexSubcat_Sense_".concat(Integer.toString(senseNumber)));
				sense.setIndex(senseNumber);
				senseNumber++;
				
				if (!imsLexSense.classInformation.equals("null")) {
					List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
					SemanticLabel semanticLabel = new SemanticLabel();
					semanticLabel.setLabel(imsLexSense.classInformation);
					semanticLabel.setType(getTypeOfSemanticLabel(imsLexSense.classInformation));
					semanticLabels.add(semanticLabel);
					sense.setSemanticLabels(semanticLabels);
				}
					
				if (!imsLexSense.synArgs.equals("null")) {
					// Creating SyntacticBehaviour (one for each sense)
					SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
					// Generating an ID
					syntacticBehaviour.setId("IMSLexSubcat_SyntacticBehaviour_".concat(Integer.toString(syntacticBehaviourNumber)));
					syntacticBehaviourNumber++;
					
					syntacticBehaviour.setSense(sense);
					syntacticBehaviour.setSubcategorizationFrame(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(imsLexSense.synArgs)));
					syntacticBehaviour.setSubcategorizationFrameSet(senseSubcatFrameSetMap.get(imsLexSense));
					syntacticBehaviours.add(syntacticBehaviour);
					
					if (senseSemPredicateMap.containsKey(imsLexSense)) {
						// Creating Predicative Representation 
						List<PredicativeRepresentation> predicativeRepresentations = new LinkedList <PredicativeRepresentation>();
						PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
						predicativeRepresentation.setPredicate(senseSemPredicateMap.get(imsLexSense));
					
						predicativeRepresentations.add(predicativeRepresentation);				
						sense.setPredicativeRepresentations(predicativeRepresentations);// Save PredicativeRepresentations
					}
				}
				
				senses.add(sense);// Save Sense

			}
			lexicalEntry.setSenses(senses);	
			if (syntacticBehaviours != null) {
				lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);	
			}
			
			lexicalEntries.add(lexicalEntry);// Save LexicalEntry			
		}
		
	}

	
	private ELabelTypeSemantics getTypeOfSemanticLabel(String classInformation)
	{
		if (classInformation.contains("Noun")) {
			return ELabelTypeSemantics.semanticNounClass;
		} else {
			return ELabelTypeSemantics.syntacticAlternationClass;
		}
	}

	/**
	 * This method maps POS information to EPartOfSpeech
	 * 
	 */	
	private EPartOfSpeech mapPOS(String pos)
	{
		EPartOfSpeech result = null;
		if (pos.equals("verb")) {
			result = EPartOfSpeech.verbMain;
		} else if (pos.equals("adj")) {
			result = EPartOfSpeech.adjective;
		} else if (pos.equals("noun")) {
			result = EPartOfSpeech.nounCommon;
		}
		return result;
	}

	/**
	 * This method creates (purely syntactic) subcategorization frames
	 * @param IMSlexSubcatSense a IMSlexSubcat sense
	 * @param subcatFrame a subcategorization frame
	 * @return the subcategorization frame
	 */
	private SubcategorizationFrame parseArguments(IMSlexSense IMSlexSubcatSense, SubcategorizationFrame subcatFrame) {
		SubcategorizationFrame scFrame = subcatFrame;
		List<SyntacticArgument> synArgs = new LinkedList<SyntacticArgument>();
		String[] args = IMSlexSubcatSense.synArgs.split(":");
		for(String arg : args) {			
			if (!arg.contains("syntacticProperty")) {			
				SyntacticArgument syntacticArgument = new SyntacticArgument();
				syntacticArgument.setId("IMSLexSubcat_SyntacticArgument_".concat(Integer.toString(syntacticArgumentNumber)));
				syntacticArgumentNumber++;	
				String[] atts = arg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					if (attName.equals("grammaticalFunction")){
						String gf = splits[1];
						if (gf.equals("object")) {
							gf = gf.replaceAll("object", "directObject");							
						}
						syntacticArgument.setGrammaticalFunction(EGrammaticalFunction.valueOf(gf));
					}
					if(attName.equals("syntacticCategory")) {
						syntacticArgument.setSyntacticCategory(ESyntacticCategory.valueOf(splits[1]));
					}
					if(attName.equals("case")) {
						syntacticArgument.setCase(ECase.valueOf(splits[1]));
					}
					if(attName.equals("determiner")) {
						syntacticArgument.setDeterminer(EDeterminer.valueOf(splits[1]));
					}
					if(attName.equals("preposition")) {
						syntacticArgument.setPreposition(splits[1]);
					}
					if(attName.equals("prepositionType")) {
						syntacticArgument.setPrepositionType(splits[1]);
					}
					if(attName.equals("number")) {
						syntacticArgument.setNumber(EGrammaticalNumber.valueOf(splits[1]));
					}
					if(attName.equals("lexeme")) {
						syntacticArgument.setLexeme(splits[1]);
					}
					if(attName.equals("verbForm")) {						
						syntacticArgument.setVerbForm(EVerbForm.valueOf(splits[1]));
					}
					if(attName.equals("tense")) {
						syntacticArgument.setTense(ETense.valueOf(splits[1]));
					}
					if(attName.equals("complementizer")) {
						syntacticArgument.setComplementizer(EComplementizer.valueOf(splits[1]));
					}																	
				}
				synArgs.add(syntacticArgument);								
			} else {
				String [] splits = arg.split("=");
				String sp = splits[1];
				if (sp.equals("raising")) {
					sp = sp.replaceAll("raising", "subjectRaising");							
				}
				LexemeProperty lexemeProperty = new LexemeProperty();
				lexemeProperty.setSyntacticProperty(ESyntacticProperty.valueOf(sp));
				scFrame.setLexemeProperty(lexemeProperty);
			} 	
		}
		scFrame.setSyntacticArguments(synArgs);		
		return scFrame;
	}	
	
	/**
	 * This method creates semantic predicates and
	 * establishes a mapping between semantic arguments
	 * and syntactic arguments
	 * @param IMSlexSubcatSense a IMSlexSubcat sense
	 * @param subcategorizationFrame a subcategorization frame
	 * @returns the semantic predicate
	 */
	private SemanticPredicate parseSemanticArguments(IMSlexSense IMSlexSubcatSense,SubcategorizationFrame subcategorizationFrame) {
		// list of mappings between Syntactic and Semantic Arguments are to be created
		SemanticPredicate semanticPredicate = new SemanticPredicate();
		semanticPredicate.setId("IMSLexSubcat_SemanticPredicate_".concat(Integer.toString(semanticPredicateNumber)));
		semanticPredicateNumber++;
		List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
		List<SynSemArgMap> synSemArgMaps = new LinkedList<SynSemArgMap>();	
		SynSemArgMap synSemArgMap = new SynSemArgMap();
		
		String[] args = IMSlexSubcatSense.synArgs.split(":");
		int index = 0;
		// iterate over syntactic Arguments
		for (SyntacticArgument synArg: subcategorizationFrame.getSyntacticArguments()) {			
			String synsemArg = args[index];
			if (synsemArg.contains("syntacticProperty")) {
				index++;
				synsemArg = args[index];
			}
			// look at synsemArg: is semantic role defined? if yes: create corresponding semanticArg
			String[] atts = synsemArg.split(",");
			for(String att : atts){
				String [] splits = att.split("=");
				String attName = splits[0];
				if(attName.equals("role")){
					SemanticArgument semanticArgument = new SemanticArgument();
					semanticArgument.setId("IMSLexSubcat_SemanticArgument_".concat(Integer.toString(semanticArgumentNumber)));
					semanticArgumentNumber++;
					semanticArgument.setSemanticRole(splits[1]);
					semanticArguments.add(semanticArgument);
					// Generate SynSemArgMapping	
					synSemArgMap.setSyntacticArgument(synArg);
					synSemArgMap.setSemanticArgument(semanticArgument);
					synSemArgMaps.add(synSemArgMap);
				}																									
			}
			index++;									
		}	
		semanticPredicate.setSemanticArguments(semanticArguments);
		
		SynSemCorrespondence synSemCorrespondence = new SynSemCorrespondence();
		synSemCorrespondence.setId("IMSLexSubcat_SynSemCorrespondence_".concat(Integer.toString(synSemCorrespondenceNumber)));
		synSemCorrespondenceNumber++;
		synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
		synSemCorrespondences.add(synSemCorrespondence);
		return semanticPredicate;
	}

}