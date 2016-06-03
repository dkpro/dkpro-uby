/**
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.WNConvUtil;


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

	private final Map<POS,Map<String, Set<Word>>> posLemmaLexemeGroup = new LinkedHashMap<POS, Map<String, Set<Word>>>();

	private final Set<Set<Word>> lexemeGroups = new LinkedHashSet<Set<Word>>();

	private final Map<Word, Set<Word>> lexemeToGroupMappings;

	private Dictionary extWordnet;

	/*
	 * All generated LexicalEntries
	 */
	private final List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();

	private int lexicalEntryNumber; // used for creating IDs of LexicalEntries

	private int syntacticBehaviourNumber; // used for creating syntacticBehaviour IDs

	private boolean initialized = false; // true only when lexicalEntryGenerator is initialized

	private SenseGenerator senseGenerator; // instance of SenseGenerator used

	private final SubcategorizationFrameExtractor subcategorizationFrameExtractor; // used for creating SyntacticBehaviours

	/*
	 * This map prevents creating identical SyntacticBehaviours with different IDs
	 * Key of the map is SyntacticBehaviour's string representation without ID
	 * value is the corresponding SyntacticBehavour
	 */
	private final Map<String, SyntacticBehaviour> syntBeh = new TreeMap<String, SyntacticBehaviour>();

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Constructs a {@link LexicalEntryGenerator} used for generating LexicalEntries
	 * @param dictionaryPath the path of the WordNet dictionary files
	 * @param extWordnet an instance of initialized WordNet-{@link Dictionary} used for accessing WordNet's information
	 * @param synsetGenerator an instance of {@link SynsetGenerator} used for generating {@link Synset}-instances
	 * @param subcategorizationFrameExtractor an instance of {@link SubcategorizationFrameExtractor} used for generating {@link SubcategorizationFrame}-instances
	 * @param Version of the resource
	 * @see {@link LexicalEntry}
	 */
	public LexicalEntryGenerator(File dictionaryPath, Dictionary extWordnet, SynsetGenerator synsetGenerator,
			SubcategorizationFrameExtractor subcategorizationFrameExtractor, String resourceVersion){
		this.subcategorizationFrameExtractor = subcategorizationFrameExtractor;
		lexemeToGroupMappings = new TreeMap<Word, Set<Word>>(new Comparator<Word>() {
			@Override
			public int compare(Word o1, Word o2) {
				try {
                    return o1.getSenseKey().compareTo(o2.getSenseKey());
                }
                catch (JWNLException e) {
                    throw new IllegalArgumentException(e);
                }
			}
		});

		if(!initialized){
			this.extWordnet = extWordnet;
			lexicalEntryNumber = 0;
			syntacticBehaviourNumber = 0;
			groupLexemes();

			IndexSenseReader isr = new IndexSenseReader();
			isr.initialize(new File(dictionaryPath, "index.sense"));
			senseGenerator = new SenseGenerator(synsetGenerator, isr, resourceVersion);
			createLexicalEntries();
			initialized = true;
		}
	}

	/**
	 * This method groups all lexemes contained in WordNet 3.0 by lemma and part of speech
	 */
	private void groupLexemes() {
		byte percentage = 0;
		logger.info(" grouping lexemes...");
		lexemeGroupLexicalEntryMaping= new LinkedHashMap<Set<Word>, LexicalEntry>();
		Iterator<Synset> synsetIter = null; // synset iterator
		for(POS pos : POS.getAllPOS()){ // Iterate over all POSes
			logger.info(percentage+"%");
			Map<String, Set<Word>>lemmaLexemeGroup = new TreeMap<String, Set<Word>>();
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
						lexemeGroup = new TreeSet<Word>(new Comparator<Word>() {
							@Override
							public int compare(Word o1, Word o2) {
								try {
                                    return o1.getSenseKey().compareTo(o2.getSenseKey());
                                }
                                catch (JWNLException e) {
                                    throw new IllegalArgumentException(e);
                                }
							}
						});
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
		logger.info("100%");
	}

	/**
	 * This method iterates over all {@link LexicalEntryGenerator#lexemeGroups} and
	 * creates a list of LexicalEntries for every group
	 * @see LexicalEntry
	 */
	private void createLexicalEntries(){
		logger.info("transforming lexeme groups... 0% ");
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
				logger.info(percentage+"%");
			}
		}
		logger.info("100%");
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
		List<Map<String, Word>> subcatCodes = new LinkedList<Map<String, Word>>();

		boolean posSet = false; // True when POS is set to the LexicalEntry

		String lemmaString = null; // Lemmas Written form

		for(Word lexeme : lexemeGroup){
			if(!posSet){
				// Extract the POS of the first Lexeme in the group
				lexicalEntry.setPartOfSpeech(WNConvUtil.getPOS(lexeme.getPOS()));
				posSet = true;

				// Extract lemma
				lemmaString = lexeme.getLemma();

			}
			EPartOfSpeech lePOS = lexicalEntry.getPartOfSpeech();
			if(lePOS.equals(EPartOfSpeech.verb)){
				// Extracting the verb frame
				String[] frames = lexeme.getSynset().getVerbFrames();
				for(String frame : frames){
					Map<String, Word> codeLexeme = new TreeMap<String, Word>();
					codeLexeme.put(frame, lexeme);
					subcatCodes.add(codeLexeme); // the codes will be processed later
				}
			}
			// extracting the subcat frame of an adjective
			String synMarker;
			try {
                if(lePOS.equals(EPartOfSpeech.adjective) && (synMarker = lexeme.getSenseKeyWithAdjClass()).contains("(")){
                	int start = synMarker.indexOf("(");
                	String adjFrameCode = synMarker.substring(start+1, synMarker.indexOf(")"));
                	Map<String, Word> codeLexeme = new TreeMap<String, Word>();
                	codeLexeme.put(adjFrameCode, lexeme); // the codes will be processed later
                	subcatCodes.add(codeLexeme);
                }
            }
            catch (JWNLException e) {
                throw new IllegalArgumentException(e);
            }
		}

		//*** Creating Lemma ***//
		Lemma lemma = new Lemma();
		List<FormRepresentation> formRepresentations = new LinkedList<FormRepresentation>();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
		formRepresentation.setWrittenForm(lemmaString);
		formRepresentations.add(formRepresentation);
		lemma.setFormRepresentations(formRepresentations);
		lexicalEntry.setLemma(lemma);

		//*** Creating Senses ***//
		lexicalEntry.setSenses(senseGenerator.generateSenses(lexemeGroup, lexicalEntry));

		//*** Creating SyntacticBehaviours***//
		if(!subcatCodes.isEmpty()){
			Set<SyntacticBehaviour> syntacticBehaviours = new TreeSet<SyntacticBehaviour>();
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

						if(sense.getPredicativeRepresentations() != null && !sense.getPredicativeRepresentations().isEmpty()) {
                            sense.getPredicativeRepresentations().addAll(predicativeRepresentations);
                        }
                        else {
                            sense.setPredicativeRepresentations(predicativeRepresentations);
                        }
					}
				}
				/*
				 * check in the mapping, if an equivalent SyntactiBehaviour was already created
				 */
				String synBehString = createString(syntacticBehaviour);
				SyntacticBehaviour created = syntBeh.get(synBehString);
				if(created != null) {
                    syntacticBehaviours.add(created);
                }
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
