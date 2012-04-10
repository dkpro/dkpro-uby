/**
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package de.tudarmstadt.ukp.lmf.transform.framenet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.saar.coli.salsa.reiter.framenet.Lexeme;
import de.saar.coli.salsa.reiter.framenet.LexicalUnit;
import de.saar.coli.salsa.reiter.framenet.PartOfSpeech;
import de.saar.coli.salsa.reiter.framenet.SemanticType;
import de.saar.coli.salsa.reiter.framenet.SemanticTypeNotFoundException;
import de.saar.coli.salsa.reiter.framenet.fncorpus.AnnotatedLexicalUnit;
import de.saar.coli.salsa.reiter.framenet.fncorpus.AnnotationCorpus;
import de.saar.coli.salsa.reiter.framenet.fncorpus.AnnotationCorpus15;
import de.saar.coli.salsa.reiter.framenet.fncorpus.Sentence;
import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.Component;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.ListOfComponents;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;

/**
 * Instance of this class offers methods for creating {@link LexicalEntry} out of FrameNet's data
 * @author Zijad Maksuti, Silvana Hartmann
 *
 */
public class LexicalEntryGenerator {
	private SemanticPredicateGenerator semanticPredicateGenerator;
	private int lexicalEntryNumber; // Running number used for creating IDs of LexicalEntries
	private int senseNumber; // Running number used for creating IDs of Senses
	private int senseExampleNumber; // Running number used for creating IDs of SenseExamples
	private FrameNet fn; // FrameNet object, used for obtaining needed informations out of FrameNet's files
	
	
	// all LexicalEntries produced by this LexicalEntryGenerator
	private List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
	
	/*
	 * Groups of LexicalUnits with equal lemma,
	 * divided by PartOfSpeech
	 */
	private Map<PartOfSpeech, HashMap<String, HashSet<LexicalUnit>>> mappings;
	
	// Mapping between luGroups and corresponding LexicalEntries
	private Map<Set<LexicalUnit>, LexicalEntry> groupLEMappings = new HashMap<Set<LexicalUnit>, LexicalEntry>();
	
	// Mappings used for creating targetLexicalEntry attribute in Component class
	private Map<PartOfSpeech, Map<String, List<Component>>> components = new HashMap<PartOfSpeech, Map<String,List<Component>>>(); // all Components
	
	/*
	 *  This mapping contains all "dummy" LexicalEntries,
	 *  that are created in order to set the targetLexicalEntry attribute in Component class
	 */
	private Map<PartOfSpeech, Map<String, LexicalEntry>> dummyLEs = new HashMap<PartOfSpeech, Map<String, LexicalEntry>>();
	
	// used for extracting annotations
	private AnnotationCorpus ac;
	
	// directory where FrameNet's files are located 
	private String fnhome;
	
	private Logger logger = Logger.getLogger(FNConverter.class.getName());
	
	/**
	 * Constructs an instance of LexicalEntryGenerator, which provides methods for creating <br>
	 * LexicalEntries out of FrameNet's files
	 * @param fn instance of {@link FrameNet} class, used for obtaining needed informations for generating LexicalEntries
	 * @param semanticPredicateGenerator instance of {@link SemanticPredicateGenerator} used for creating SemanticPredicates
	 * @see {@link LexicalEntry}
	 * @see {@link SemanticPredicate}
	 */
	public LexicalEntryGenerator(FrameNet fn, SemanticPredicateGenerator semanticPredicateGenerator){
		this.fn = fn;
		this.semanticPredicateGenerator = semanticPredicateGenerator;
		ac = new AnnotationCorpus15(fn, logger);
		fnhome = System.getenv("UBY_HOME")+"/FrameNet/fndata-1.5"; 
		groupLUs();
		
		// Initialize help-mappings
		for(PartOfSpeech pos : PartOfSpeech.values()){
			dummyLEs.put(pos, new HashMap<String, LexicalEntry>());
			components.put(pos, new HashMap<String, List<Component>>());
		}
		
		createLexicalEntries();
		updateComponents();
	}
	
	/**
	 * This method iterates over all groups of LexicalEntries and <br>
	 * creates a {@link LexicalEntry} for every group of Lexemes
	 * @see {@link Lexeme}
	 */
	private void createLexicalEntries(){
		for(PartOfSpeech pos : mappings.keySet())
			for(HashSet<LexicalUnit> luGroup : mappings.get(pos).values()){
				LexicalEntry lexicalEntry = createLexicaltEntry(luGroup);
				groupLEMappings.put(luGroup, lexicalEntry);
				lexicalEntries.add(lexicalEntry);
			}
	}
	
	/**
	 * This method creates a {@link LexicalEntry} based on the
	 * consumed group of LexicalUnits
	 * @param luGroup a group LexicalUnits with equal lemma and part of speech
	 * @return generated LexicalEntry based on consumed luGroup
	 * @see {@link LexicalUnit}
	 */
	public LexicalEntry createLexicaltEntry(HashSet<LexicalUnit> luGroup){
		LexicalEntry lexicalEntry = new LexicalEntry();
		lexicalEntry.setId(createID());
		PartOfSpeech pos=null;
		String lemmaString = null;
		ListOfComponents listOfComponents = null;
		List<Sense> senses = new ArrayList<Sense>();
		for(LexicalUnit lu : luGroup){
			if(pos == null)
				pos = lu.getPartOfSpeech();
			if(lemmaString == null)
				lemmaString = lu.getLexemeString();
			
			// CREATING SENSE FOR THE LU
			Sense sense = new Sense();
			// setting id
			StringBuffer sb = new StringBuffer(32);
			sb.append("FN_Sense_").append(senseNumber++);
			sense.setId(sb.toString());
			// setting Definition
			Definition definition = new Definition();
			TextRepresentation textRepresentation = new TextRepresentation();
			textRepresentation.setLanguageIdentifier(ELanguageIdentifier.en);
			textRepresentation.setWrittenText(FNUtils.filterTags(lu.getDefinition()));
			List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();
			textRepresentations.add(textRepresentation);
			definition.setTextRepresentations(textRepresentations);
			List<Definition> definitions = new ArrayList<Definition>();
			definitions.add(definition);
			sense.setDefinitions(definitions);
			
			// setting MonolingualExternalRef
			MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
			monolingualExternalRef.setExternalSystem("FrameNet 1.5 lexical unit ID");
			monolingualExternalRef.setExternalReference(lu.getId());
			List<MonolingualExternalRef> monolingualExternalRefs = new ArrayList<MonolingualExternalRef>();
			monolingualExternalRefs.add(monolingualExternalRef);
			sense.setMonolingualExternalRefs(monolingualExternalRefs);
			
			// setting PredicativeRepresentation
			SemanticPredicate semanticPredicate = semanticPredicateGenerator.getSemanticPredicate(lu.getFrame());
			if(semanticPredicate == null){
				StringBuffer sbErr = new StringBuffer(64);
				sbErr.append("LexicalEntryGenerator: SemanticPredicateGenerator did not provide SemanticPredicate of Frame: ");
				sbErr.append(lu.getFrame());
				sbErr.append('\n');
				sbErr.append("Aborting all operations!");
				logger.log(Level.SEVERE, sbErr.toString());
				System.exit(1);
			}
			PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
			predicativeRepresentation.setPredicate(semanticPredicate);
			List<PredicativeRepresentation> predicativeRepresentations = new ArrayList<PredicativeRepresentation>();
			predicativeRepresentations.add(predicativeRepresentation);
			sense.setPredicativeRepresentations(predicativeRepresentations);
			
			// SETTING incorporatedSemArg
			AnnotatedLexicalUnit alu = ac.getAnnotation(lu);
			if(alu != null){
				// incorporatedSemArg can only be set for annotated lus
				String incorporatedFEName = alu.getIncorporatedFE();
				if(incorporatedFEName != null){
					// Create a SemanticArgument for the incorporated FrameElement
					SemanticArgument semanticArgument = semanticPredicateGenerator.createIncorporatedSemanticArgument(incorporatedFEName);
					List<SemanticArgument> semanticArguments = semanticPredicate.getSemanticArguments();
					if(semanticArgument == null)
						semanticArguments = new ArrayList<SemanticArgument>();
					semanticArguments.add(semanticArgument);
					semanticPredicate.setSemanticArguments(semanticArguments);
					sense.setIncorporatedSemArg(semanticArgument);
				}
				// MAPPING LU'S SEMTYPE TO SENSE
				HashSet<String> semTypeIDs = alu.getSemTypes();
				
				for(String semTypeID : semTypeIDs){
					if(semTypeID.equals("9")) //lexicalized
						sense.setTransparentMeaning(EYesNo.yes);
					else
						if(semTypeID.equals("223"))// perspectivalized
							sense.setBoundLexeme(EYesNo.yes);
						else{
							// the underlying checks for semTypeIDs 68 and 182 are because of a bug in FN-API
							SemanticType semanticType = null;
							if(semTypeID.equals("68"))
								try {
									semanticType = fn.getSemanticType("Physical_object");
								} catch (SemanticTypeNotFoundException e) {
									e.printStackTrace();
								}
							else if(semTypeID.equals("182"))
								try {
									semanticType = fn.getSemanticType("Locative_relation");
								} catch (SemanticTypeNotFoundException e) {
									e.printStackTrace();
								}
							else
								try {
									semanticType = fn.getSemanticType(semTypeID);
								} catch (SemanticTypeNotFoundException e) {
									e.printStackTrace();
								}
								// Checking if the root of this semanticType != "Lexical_type"
								SemanticType rootSemanticType = null;
								for(SemanticType temp : semanticType.getSuperTypes())
									rootSemanticType = temp;
								
								// if the root is still == null, semanticType has no parents
								if(rootSemanticType == null)
									rootSemanticType = semanticType;

								// if the root of semanticType != "Lexical_type", then we have an ontological type
								if(!rootSemanticType.equals("Lexical_type")){
									// Creating SemanticLabels for FN-"Ontological types"
									SemanticLabel semanticLabel = new SemanticLabel();
									semanticLabel.setLabel(semanticType.getName());
									semanticLabel.setType("FrameNet semantic sentiment");
									
									// crating MonolingualExternalRef
									List<MonolingualExternalRef> merefs = new LinkedList<MonolingualExternalRef>();
									MonolingualExternalRef meref = new MonolingualExternalRef();
									meref.setExternalReference(semTypeID);
									meref.setExternalSystem("FrameNet 1.5 semantic type ID");
									merefs.add(meref);
									semanticLabel.setMonolingualExternalRefs(monolingualExternalRefs);
									List<SemanticLabel> semanticLabels = sense.getSemanticLabels();
									if(semanticLabels == null)
										semanticLabels = new ArrayList<SemanticLabel>();
									semanticLabels.add(semanticLabel);
									sense.setSemanticLabels(semanticLabels);
								}
						}
				}
				
				List<Lexeme> lexemes = lu.getLexemes();
				// Creating a list of components for multiword LexicalUnits
				if(lexemes.size() > 1)
					listOfComponents = createListOfComponents(lexemes);
				
				
				List<SenseExample> senseExamples = sense.getSenseExamples();
				if(senseExamples == null)
					senseExamples = new ArrayList<SenseExample>();
				sense.setSenseExamples(senseExamples);
				
				// GETTING ANNOTATION SENTENCES
				for(Sentence sentence : alu.getSentences()){
					// Creating a SenseExample for every Sentence
					SenseExample senseExample = new SenseExample();
					StringBuffer sexID = new StringBuffer(32);
					sexID.append("FN_SenseExample_").append(senseExampleNumber++);
					senseExample.setId(sexID.toString());
					TextRepresentation sexTR = new TextRepresentation();
					sexTR.setLanguageIdentifier(ELanguageIdentifier.en);
					sexTR.setWrittenText(FNUtils.filterTags(sentence.getText()));
					List<TextRepresentation> sexTRs = new ArrayList<TextRepresentation>();
					sexTRs.add(sexTR);
					senseExample.setTextRepresentations(sexTRs);
					senseExample.setExampleType(EExampleType.senseInstance);
					senseExamples.add(senseExample);
				}
			}
			
			// ##### CREATING FREQUENCY
			List<Frequency> frequencies = sense.getFrequencies();
			if(frequencies == null)
				frequencies = new ArrayList<Frequency>();
			
			// for annotated instances
			Frequency frequency = new Frequency();
			frequency.setFrequency(lu.getSentCountAnnotated());
			frequency.setGenerator("annotated_instances");
			frequencies.add(frequency);
			
			// total
			Frequency freqTotal = new Frequency();
			freqTotal.setFrequency(lu.getSentCountTotal());
			freqTotal.setGenerator("all_instances");
			frequencies.add(freqTotal);
			
			sense.setFrequencies(frequencies);
			senses.add(sense);
		}
		lexicalEntry.setSenses(senses);
		//Setting POS
		EPartOfSpeech epos = FNUtils.getPOS(pos);
		
		if(epos == null)
			posNotFound(pos);
			
		lexicalEntry.setPartOfSpeech(epos);
		
		// Creting a lemma
		Lemma lemma = new Lemma();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(ELanguageIdentifier.en);
		formRepresentation.setWrittenForm(lemmaString);
		List<FormRepresentation> formRepresentations = new LinkedList<FormRepresentation>();
		formRepresentations.add(formRepresentation);
		lemma.setFormRepresentations(formRepresentations);
		lexicalEntry.setLemma(lemma);
		
		LexicalEntry control = groupLEMappings.put(luGroup, lexicalEntry);
		if(control != null){
			StringBuffer sb = new StringBuffer(128);
			sb.append("LexicalEntryGenerator: Mapping for luGroup: ");
			sb.append(luGroup);
			sb.append(" already exists!");
			sb.append("\n");
			sb.append("Aborting all operations!");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}
		
		// Setting listOfComponents
		if(listOfComponents != null)
			lexicalEntry.setListOfComponents(listOfComponents);
		
		return lexicalEntry;
	}
	
	/**
	 * This method consumes a list of Lexemes and creates a {@link ListOfComponents}. <br>
	 * The Components do NOT have the  targetLexicalEntry attribute set!
	 * @param lexemes the list of Lexemes from which a ListOfComponents should be generated
	 * @return ListOfComponents based on consumed lexemes  
	 * @see {@link Component}
	 * @see {@link Lexeme}
	 */
	private ListOfComponents createListOfComponents(List<Lexeme> lexemes) {
		ListOfComponents listOfComponents = new ListOfComponents();
		List<Component> components = new LinkedList<Component>();
		for(Lexeme lexeme : lexemes){
			// Create a Component for every lexeme
			Component component = new Component();
			component.setIsHead(FNUtils.booleanForHumans(lexeme.isHeadword()));
			component.setIsBreakBefore(FNUtils.booleanForHumans(lexeme.isBreakBefore()));
			component.setPosition(lexeme.getOrder());
			PartOfSpeech pos = lexeme.getPartOfSpeech();
			components.add(component);
			String name = lexeme.getValue();
			
			// Record for creation of targetLexicalEntry attribute later
			Map<String, List<Component>> mapping =  this.components.get(pos);
			
			List<Component> cmps = mapping.get(name);
			if(cmps == null)
				cmps = new LinkedList<Component>();
			cmps.add(component);
			mapping.put(name, cmps);
		}
		listOfComponents.setComponents(components);
		return listOfComponents;
	}

	
	/**
	 * This method creates an ID for a {@link LexicalEntry}
	 * @return ID for an instance of LexicalEntry
	 */
	private String createID() {
		StringBuffer sb = new StringBuffer(32);
		sb.append("FN_LexicalEntry_").append(lexicalEntryNumber++);
		return sb.toString();
	}

	/**
	 * This Method iterates over all created Components
	 * and updates their targetLexicalEntry attribute
	 * @see {@link Component}
	 */
	private void updateComponents() {
		for(PartOfSpeech pos : components.keySet())
			for(String lemma : components.get(pos).keySet())
				for(Component component : components.get(pos).get(lemma)){
					Set<LexicalUnit> luGroup = mappings.get(pos).get(lemma);
					LexicalEntry lexicalEntry = null;
					if(luGroup != null){
						lexicalEntry = groupLEMappings.get(luGroup);
						if(lexicalEntry == null){
							StringBuffer sb = new StringBuffer(256);
							sb.append("LexicalEntryGenerator: Error on updating Components!");
							sb.append("No lexical entry for luGroup: ").append(luGroup).append(" found");
							sb.append('\n').append("Aborting all operations!");
							logger.log(Level.SEVERE, sb.toString());
							System.exit(1);
						}
					}
					else
						// when no luGroup with this lemma has been found
						// check if a dummy LexicalEntry exists
						lexicalEntry = dummyLEs.get(pos).get(lemma);
					
					if(lexicalEntry != null){
						// component has a corresponding LexicalEntry
						component.setTargetLexicalEntry(lexicalEntry);
					}
					else{
						// component does not have a corresponding LexicalEntry
						// a new LexicalEntry will be created
						lexicalEntry = new LexicalEntry();
						EPartOfSpeech epos = FNUtils.getPOS(pos);
						if(epos == null)
							posNotFound(pos);
						lexicalEntry.setPartOfSpeech(epos);
						lexicalEntry.setId(createID());
						List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
						FormRepresentation formRepresentation = new FormRepresentation();
						formRepresentation.setLanguageIdentifier(ELanguageIdentifier.en);
						formRepresentation.setWrittenForm(lemma);
						formRepresentations.add(formRepresentation);
						Lemma lemmaObj = new Lemma();
						lemmaObj.setLexicalEntryId(lexicalEntry.getId());
						lemmaObj.setFormRepresentations(formRepresentations);
						component.setTargetLexicalEntry(lexicalEntry);
						lexicalEntry.setLemma(lemmaObj);
						
						// Add a record for future 
						dummyLEs.get(pos).put(lemma, lexicalEntry);
						lexicalEntries.add(lexicalEntry);
					}
				}
	}
	
	/**
	 * This method groups all LexicalUnits by lemma and part of speech
	 */
	private void groupLUs() {
		mappings = new HashMap<PartOfSpeech, HashMap<String, HashSet<LexicalUnit>>>();
		PartOfSpeech[] poses = PartOfSpeech.values();
		
		for(PartOfSpeech pos : poses)
			mappings.put(pos, new HashMap<String, HashSet<LexicalUnit>>());
		
		for(LexicalUnit lu : fn.getLexicalUnits()){
			HashMap<String, HashSet<LexicalUnit>> lemmaLUMappings = mappings.get(lu.getPartOfSpeech());
			String lemma = lu.getLexemeString(); // lu's lemma
			
			// Parse the corpus in order to get more information about the lu 
			ac.parse(new File(fnhome+File.separator+"lu"), "lu"+lu.getId()+".xml");
			
			// Appending partOfSpeech of components for multiword expressions
			List<Lexeme> lexemes = lu.getLexemes();
			if(lexemes.size() > 1)
				for(Lexeme lexeme : lexemes){
					/*
					 * POS, isBreakBefor, isHeadWord and Order are relevant when grouping
					 * multiword LexicalUnits
					 */
					lemma = lemma.concat(lexeme.getPos());
					lemma = lemma.concat("isBreakBefore:").concat(Boolean.toString(lexeme.isBreakBefore()));
					lemma = lemma.concat("isHeadWord:").concat(Boolean.toString(lexeme.isHeadword()));
					lemma = lemma.concat("order:").concat(Integer.toString(lexeme.getOrder()));
				}
			
			HashSet<LexicalUnit> luGroup = lemmaLUMappings.get(lemma);
			if(luGroup == null)
				luGroup = new HashSet<LexicalUnit>();
			luGroup.add(lu);
			lemmaLUMappings.put(lemma, luGroup);
		}
		}
	
	/**
	 * This method is called when an associated part of speech, defined in {@link EPartOfSpeech}, <br>
	 * could not be found for part of speech defined in {@link PartOfSpeech}. <br>
	 * It informs the user about the situation and terminates the running process
	 * @param pos part of speech defined in {@link PartOfSpeech}, for which an associated part of speech in {@link EPartOfSpeech} could not be found
	 * 
	 */
	private void posNotFound(PartOfSpeech pos){
		StringBuffer sb = new StringBuffer(128);
		sb.append("LexicalEntryGenerator: FNUtils returned null for PartOfSpeech: ").append(pos).append(" Aborting all operations.");
		logger.log(Level.SEVERE, sb.toString());
		System.exit(1);
	}

	/**
	 * Returns all LexicalEntries generated by this {@link LexicalEntryGenerator}
	 * @return the lexicalEntries
	 * @see {@link LexicalEntry}
	 */
	public List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}
}
