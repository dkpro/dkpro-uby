/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.IndexSenseReader;


/**
 * Instance of this class offers methods for creating {@link LexicalEntry} out of WordNet's data
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class LexicalEntryGenerator {
	
	/*
	 * Mappings of lexemes with equal lemma and part of speech with associated LexicalEntries
	 */
	private Map<Set<Word>, LexicalEntry> lexemeGroupLexicalEntryMaping;
	
	//  mappings between part of speech, encoded in WordNet, part of speech specified by Uby-LMF
	private static final Map<String, EPartOfSpeech> posMappings = new HashMap<String, EPartOfSpeech>();
	
	private Map<POS,Map<String, Set<Word>>> posLemmaLexemeGroup = new HashMap<POS, Map<String, Set<Word>>>();
	
	private Set<Set<Word>> lexemeGroups = new HashSet<Set<Word>>();
	
	private Map<Word, Set<Word>> lexemeToGroupMappings = new HashMap<Word, Set<Word>> ();
	
	private Dictionary extWordnet;
	
	/*
	 * All generated LexicalEntries
	 */
	private List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
	
	private int lexicalEntryNumber; // used for creating IDs of LexicalEntries
	
	private int syntacticBehaviourNumber; // used for creating syntacticBehaviour IDs

	private boolean initialized = false; // true only when lexicalEntryGenerator is initialized
	
	private SenseGenerator senseGenerator; // instance of SenseGenerator used
	
	private SubcategorizationFrameExtractor subcategorizationFrameExtractor; // used for creating SyntacticBehaviours
	
	/*
	 * This map prevents creating identical SyntacticBehaviours with different IDs
	 * Key of the map is SyntacticBehaviour's string representation without ID
	 * value is the corresponding SyntacticBehavour
	 */
	private Map<String, SyntacticBehaviour> syntBeh = new HashMap<String, SyntacticBehaviour>();
	
	private Logger logger = Logger.getLogger(WNConverter.class.getName());
	
	/**
	 * Constructs a {@link LexicalEntryGenerator} used for generating LexicalEntries
	 * @param extWordnet an instance of initialized WordNet-{@link Dictionary} used for accessing WordNet's information
	 * @param synsetGenerator an instance of {@link SynsetGenerator} used for generating {@link Synset}-instances
	 * @param subcategorizationFrameExtractor an instance of {@link SubcategorizationFrameExtractor} used for generating {@link SubcategorizationFrame}-instances
	 * @see {@link LexicalEntry}
	 */
	public LexicalEntryGenerator(Dictionary extWordnet, SynsetGenerator synsetGenerator,
			SubcategorizationFrameExtractor subcategorizationFrameExtractor){
		this.subcategorizationFrameExtractor = subcategorizationFrameExtractor;
		if(!initialized){
			this.extWordnet = extWordnet;
			lexicalEntryNumber = 0;
			syntacticBehaviourNumber = 0;
			groupLexemes();
			
			// Put the POS mappings posKey <-> EPartOfSpeech
			posMappings.put("n", EPartOfSpeech.noun);
			posMappings.put("v", EPartOfSpeech.verb);
			posMappings.put("a", EPartOfSpeech.adjective);
			posMappings.put("r", EPartOfSpeech.adverb);
			IndexSenseReader isr = new IndexSenseReader();
			isr.initialize();
			senseGenerator = new SenseGenerator(synsetGenerator, isr);
			createLexicalEntries();
			initialized = true;
		}
	}

	/**
	 * This method groups all lexemes contained in WordNet 3.0 by lemma and part of speech
	 */
	private void groupLexemes() {
		byte percentage = 0;
		logger.log(Level.INFO, " grouping lexemes...");
		lexemeGroupLexicalEntryMaping= new HashMap<Set<Word>, LexicalEntry>();
		Iterator<Synset> synsetIter = null; // synset iterator
		for(POS pos : (List<POS>)POS.getAllPOS()){ // Iterate over all POSes
			logger.log(Level.INFO, percentage+"%");
			Map<String, Set<Word>>lemmaLexemeGroup = new HashMap<String, Set<Word>>();
			try {
				synsetIter = extWordnet.getSynsetIterator(pos);
			} catch (JWNLException e) {
				e.printStackTrace();
			} 

			while(synsetIter.hasNext()){ // Iterate over all Synsets (Lemmas)
				Synset synset = synsetIter.next();
				List<Word> lexemes = synset.getWords(); // lexemes of the Synset
				for(Word lexeme : lexemes){
					Set<Word> lexemeGroup; // group of lexemes with equal lemma
					String lemma = lexeme.getLemma(); // lemma's lexeme
					
					if((lexemeGroup = lemmaLexemeGroup.get(lemma)) == null){
						lexemeGroup = new HashSet<Word>();
						lemmaLexemeGroup.put(lemma, lexemeGroup);
					}
					lexemeGroup.add(lexeme);
					lexemeToGroupMappings.put(lexeme, lexemeGroup);
				}
			}
			posLemmaLexemeGroup.put(pos, lemmaLexemeGroup);
			lexemeGroups.addAll(lemmaLexemeGroup.values());
			percentage +=25;
		}
		logger.log(Level.INFO, "100%");
	}
	
	/**
	 * This method iterates over all {@link LexicalEntryGenerator#lexemeGroups} and 
	 * creates a list of LexicalEntries for every group
	 * @see LexicalEntry
	 */
	private void createLexicalEntries(){
		logger.log(Level.INFO, "transforming lexeme groups... 0% ");
		int size = lexemeGroups.size();
		int tenPercent = size/10;
		int percentageCounter=0;
		int percentage = 0;
		for(Set<Word> lexemeGroup : lexemeGroups){
			LexicalEntry lexicalEntry = createLexicalEntry(lexemeGroup);
			lexicalEntries.add(lexicalEntry);
			lexemeGroupLexicalEntryMaping.put(lexemeGroup, lexicalEntry);
			if(percentageCounter++ == tenPercent){
				percentage +=10;
				percentageCounter = 0;
				logger.log(Level.INFO, percentage+"%");
			}
		}
		logger.log(Level.INFO, "100%");
	}

	/**
	 * This method consumes a lexemeGroup and generates
	 * the corresponding {@link LexicalEntry}-instance
	 * @param lexemeGroup a group of lexemes with equal lemma and part of speech
	 * @return a LexicalEntry that corresponds to lexemeGroup
	 */
	private LexicalEntry createLexicalEntry(Set<Word> lexemeGroup) {
		
		// Create a new LexicalEntry for the consumed group
		LexicalEntry lexicalEntry = new LexicalEntry();
		
		// Create ID for this lexicalEntry
		lexicalEntry.setId(createID());
		
		// codes of subcat frames
		List<HashMap<String, Word>> subcatCodes = new LinkedList<HashMap<String,Word>>(); 

		boolean posSet = false; // True when POS is set to the LexicalEntry
		
		String lemmaString = null; // Lemmas Written form
		
		for(Word lexeme : lexemeGroup){
			if(!posSet){
				// Extract the POS of the first Lexeme in the group
				lexicalEntry.setPartOfSpeech(getPOS(lexeme.getPOS()));
				posSet = true;
				
				// Extract lemma
				lemmaString = lexeme.getLemma();
				
			}
			EPartOfSpeech lePOS = lexicalEntry.getPartOfSpeech();
			if(lePOS.equals(EPartOfSpeech.verb)){
				// Extracting the verb frame
				String[] frames = lexeme.getSynset().getVerbFrames();
				for(String frame : frames){
					HashMap<String, Word> codeLexeme = new HashMap<String, Word>();
					codeLexeme.put(frame, lexeme);
					subcatCodes.add(codeLexeme); // the codes will be processed later
				}
			}
			// extracting the subcat frame of an adjective
			String synMarker;
			if(lePOS.equals(EPartOfSpeech.adjective) && (synMarker = lexeme.getSenseKeyWithAdjClass()).contains("(")){
				int start = synMarker.indexOf("(");
				String adjFrameCode = synMarker.substring(start+1, synMarker.indexOf(")"));
				HashMap<String, Word> codeLexeme = new HashMap<String, Word>();
				codeLexeme.put(adjFrameCode, lexeme); // the codes will be processed later
				subcatCodes.add(codeLexeme);
			}
		}
		
		//*** Creating Lemma ***//
		Lemma lemma = new Lemma();
		List<FormRepresentation> formRepresentations = new LinkedList<FormRepresentation>();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(ELanguageIdentifier.en);
		formRepresentation.setWrittenForm(lemmaString);
		formRepresentations.add(formRepresentation);
		lemma.setFormRepresentations(formRepresentations);
		lexicalEntry.setLemma(lemma);
		
		//*** Creating Senses ***//
		lexicalEntry.setSenses(senseGenerator.generateSenses(lexemeGroup));
		
		//*** Creating SyntacticBehaviours***//
		if(!subcatCodes.isEmpty()){
			Set<SyntacticBehaviour> syntacticBehaviours = new HashSet<SyntacticBehaviour>();
			for(Map<String, Word> mapping : subcatCodes){
				// create a SyntacticBehaviour for every subcat code
				SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
				
				for(String frame : mapping.keySet()){
					Word lexeme = mapping.get(frame);
					Sense sense = senseGenerator.getSense(lexeme);
					syntacticBehaviour.setSense(sense);
					SubcategorizationFrame subcategorizationFrame = subcategorizationFrameExtractor.getSubcategorizationFrame(frame);
					syntacticBehaviour.setSubcategorizationFrame(subcategorizationFrame);
					
					// Updating PredicativeRepresentations of the sense
					SemanticPredicate semanticPredicate = subcategorizationFrameExtractor.getSemanticPredicate(frame);
					if(semanticPredicate != null){
						// PredicativeRepresentation will only be updated if sementicPredicate for this Sense exists
						List<PredicativeRepresentation> predicativeRepresentations = new LinkedList<PredicativeRepresentation>();
					
						PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
						predicativeRepresentation.setPredicate(semanticPredicate);
						predicativeRepresentations.add(predicativeRepresentation);
						
						if(sense.getPredicativeRepresentations() != null && !sense.getPredicativeRepresentations().isEmpty())
							sense.getPredicativeRepresentations().addAll(predicativeRepresentations);
						else
							sense.setPredicativeRepresentations(predicativeRepresentations);
					}
				}
				/*
				 * check in the mapping, if an equivalent SyntactiBehaviour was already created 
				 */
				String synBehString = createString(syntacticBehaviour);
				SyntacticBehaviour created = syntBeh.get(synBehString);
				if(created != null)
					syntacticBehaviours.add(created);
				else {
					// set the id of the new SyntacticBehaviour
					// and make a record 
					StringBuffer sb = new StringBuffer(64);
					sb.append("WN_SyntacticBehaviour_").append(syntacticBehaviourNumber++);
					syntacticBehaviour.setId(sb.toString());
					syntBeh.put(synBehString, syntacticBehaviour);
					syntacticBehaviours.add(syntacticBehaviour);
				}
			}
			lexicalEntry.setSyntacticBehaviours(new ArrayList<SyntacticBehaviour>(syntacticBehaviours));
		}
		
		return lexicalEntry;
	}

	/**
	 * This method creates a string-representation of a {@link SyntacticBehaviour}
	 * without SyntacticBehaviour's ID
	 * @param syntacticBehaviour
	 * @return syntacticBehaviour's string-representation without the it's id
	 */
	private String createString(SyntacticBehaviour syntacticBehaviour) {
		StringBuffer sb = new StringBuffer(64);
		sb.append(syntacticBehaviour.getSense()).append(syntacticBehaviour.getSubcategorizationFrame());
		sb.append(syntacticBehaviour.getSubcategorizationFrameSet());
		return sb.toString();
	}

	/**
	 * This method consumes a {@link POS}
	 * and returns corresponding {@link EPartOfSpeech}
	 * @param pos part of speech encoded in extJWNL-API
	 * @return associated part of speech defined in Uby-LMF or null if the associated part of speech does not exist
	 */
	private EPartOfSpeech getPOS(POS pos) {
		EPartOfSpeech result = posMappings.get(pos.getKey());
		return result;
	}

	/**
	 * This method creates an ID for a {@link LexicalEntry}. <br>
	 * The running number used for the creation of the id is incremented every time this method is called.
	 * @return ID of a lexicalEntry
	 */
	private String createID() {
		StringBuffer sb = new StringBuffer(32);
		sb.append("WN_LexicalEntry_").append(lexicalEntryNumber++);
		return sb.toString();
	}

	/**
	 * Returns all LexicalEntries generated by this generator 
	 * @return all LexicalEntries generated by this generator
	 * @see {@link LexicalEntry}
	 */
	public List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}
	
	/**
	 * Returns a LexicalEntry generated for the consumed lexemeGroup
	 * @param lexemeGroup a group of lexemes for which a LexicalEntry should be returned
	 * @return the LexicalEntry that corresponds to the consumed lexemeGroup
	 * @see LexicalEntry
	 */
	LexicalEntry getLexicalEntry(Set<Word> lexemeGroup){
		return lexemeGroupLexicalEntryMaping.get(lexemeGroup);
	}
	
	/**
	 * Returns a {@link LexicalEntry} that corresponds to the consumed lexeme
	 * @param lexeme an instance of {@link Word} for which a LexicalEntry should be returned
	 * @return the LexicalEntry generated for the consumed lexeme
	 */
	public LexicalEntry getLexicalEntry(Word lexeme){
		return this.getLexicalEntry(this.getGroup(lexeme));
	}

	/**
	 * Returns the mappings between lexeme groups and associated LexicalEntries of this generator
	 * @return this generator's {@link LexicalEntryGenerator#lexemeGroupLexicalEntryMaping}
	 * @see LexicalEntry
	 * @see Word
	 */
	Map<Set<Word>, LexicalEntry> getLexemeGroupLexicalEntryMaping() {
		return lexemeGroupLexicalEntryMaping;
	}

	/**
	 * Returns the {@link SenseGenerator} used by this generator
	 * @return the senseGenerator used by this generator
	 * @see Sense
	 */
	SenseGenerator getSenseGenerator() {
		return senseGenerator;
	}
	
	/**
	 * This method returns a group of lexemes which contains the consumed lexeme
	 * @param lexeme the lexeme which group should be returned
	 * @return the lexeme-group of the consumed lexeme
	 * @see Word
	 */
	Set<Word> getGroup(Word lexeme){
		return lexemeToGroupMappings.get(lexeme);
	}

}
