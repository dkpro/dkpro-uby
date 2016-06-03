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
package de.tudarmstadt.ukp.lmf.transform.framenet;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
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
	public static final String LEXICAL_UNIT = "lexicalUnit";
	public static final String SEMANTIC_TYPE = "semanticType";

	private final String resourceVersion;
	private final SemanticPredicateGenerator semanticPredicateGenerator;
	private int lexicalEntryNumber; // Running number used for creating IDs of LexicalEntries
	private int senseNumber; // Running number used for creating IDs of Senses
	private int senseExampleNumber; // Running number used for creating IDs of SenseExamples
	private final FrameNet fn; // FrameNet object, used for obtaining needed informations out of FrameNet's files


	// all LexicalEntries produced by this LexicalEntryGenerator
	private final List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();

	/*
	 * Groups of LexicalUnits with equal lemma,
	 * divided by PartOfSpeech
	 */
	private Map<PartOfSpeech, Map<String, Set<LexicalUnit>>> mappings;

	// Mapping between luGroups and corresponding LexicalEntries
	private final Map<Set<LexicalUnit>, LexicalEntry> groupLEMappings = new LinkedHashMap<Set<LexicalUnit>, LexicalEntry>();

	// Mappings used for creating targetLexicalEntry attribute in Component class
	private final Map<PartOfSpeech, Map<String, List<Component>>> components = new LinkedHashMap<PartOfSpeech, Map<String,List<Component>>>(); // all Components

	/*
	 *  This mapping contains all "dummy" LexicalEntries,
	 *  that are created in order to set the targetLexicalEntry attribute in Component class
	 */
	private final Map<PartOfSpeech, Map<String, LexicalEntry>> dummyLEs = new LinkedHashMap<PartOfSpeech, Map<String, LexicalEntry>>();

	// used for extracting annotations
	private AnnotationCorpus ac;

	// directory where FrameNet's files are located
	private final String fnhome;

	private final Log logger = LogFactory.getLog(getClass());

    /**
     * Constructs an instance of LexicalEntryGenerator, which provides methods for creating
     * {@link LexicalEntry LexicalEntries} out of FrameNet's files
     *
     * @param fn
     *            instance of {@link FrameNet} class, used for obtaining needed informations for
     *            generating LexicalEntries
     * @param semanticPredicateGenerator
     *            instance of {@link SemanticPredicateGenerator} used for creating
     *            {@link SemanticPredicate SemanticPredicates}
     * @param resourceVersion
     *            Version of the resource
     */
	public LexicalEntryGenerator(FrameNet fn, SemanticPredicateGenerator semanticPredicateGenerator,
			String resourceVersion){
		this.fn = fn;
		this.semanticPredicateGenerator = semanticPredicateGenerator;
		this.resourceVersion = resourceVersion;
//		ac = new AnnotationCorpus15(fn, logger);
		fnhome = System.getenv("UBY_HOME")+"/FrameNet/fndata-1.5";
		System.err.println("LUS group to");
		groupLUs();
		System.err.println("LUS grouped");
		// Initialize help-mappings
		for(PartOfSpeech pos : PartOfSpeech.values()){
			dummyLEs.put(pos, new TreeMap<String, LexicalEntry>());
			components.put(pos, new TreeMap<String, List<Component>>());
		}
		System.err.println("help mappings initialized");
		createLexicalEntries();
		System.err.println("LEs created");
		updateComponents();
		System.err.println("Compontents updated");
	}

    /**
     * This method iterates over all groups of LexicalEntries and creates a {@link LexicalEntry} for
     * every group of {@link Lexeme Lexemes}.
     */
	private void createLexicalEntries(){
		for(PartOfSpeech pos : mappings.keySet()) {
            for (Set<LexicalUnit> luGroup : mappings.get(pos).values()){
				LexicalEntry lexicalEntry = createLexicaltEntry(luGroup);
				groupLEMappings.put(luGroup, lexicalEntry);
				lexicalEntries.add(lexicalEntry);
			}
        }
	}

    /**
     * This method creates a {@link LexicalEntry} based on the consumed group of {@link LexicalUnit
     * LexicalUnits}
     *
     * @param luGroup
     *            a group LexicalUnits with equal lemma and part of speech
     * @return generated LexicalEntry based on consumed luGroup
     */
	public LexicalEntry createLexicaltEntry(Set<LexicalUnit> luGroup){
		LexicalEntry lexicalEntry = new LexicalEntry();
		lexicalEntry.setId(createID());
		PartOfSpeech pos=null;
		String lemmaString = null;
		ListOfComponents listOfComponents = null;
		List<Sense> senses = new ArrayList<Sense>();
		for(LexicalUnit lu : luGroup){
			if(pos == null) {
                pos = lu.getPartOfSpeech();
            }
//			if(lemmaString == null) {
//                ;
//            }
//				lemmaString = lu.getLexemeString(); //wrong order for some mwes from API
				// workaround:
				List<Lexeme> lexemeList = lu.getLexemes(); // get all units
			    int lexemeCount = lexemeList.size();
				if (lexemeCount>1){ 	//if multiword lemma
					String[] ordered = new String[lexemeCount];
					for (Lexeme lex:lexemeList){
						ordered[lex.getOrder()-1] = lex.getValue();
					}
					lemmaString = StringUtils.join(ordered, " ");
				} else {				// unigram lemma
					lemmaString = lu.getLexemeString();
				}

			// CREATING SENSE FOR THE LU
			Sense sense = new Sense();
			// setting id
			StringBuffer sb = new StringBuffer(32);
			sb.append("FN_Sense_").append(senseNumber++);
			sense.setId(sb.toString());
			sense.setIndex(senses.size() + 1);
			// setting Definition
			Definition definition = new Definition();
			TextRepresentation textRepresentation = new TextRepresentation();
			textRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
			textRepresentation.setWrittenText(FNUtils.filterTags(lu.getDefinition()));
			List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();
			textRepresentations.add(textRepresentation);
			definition.setTextRepresentations(textRepresentations);
			List<Definition> definitions = new ArrayList<Definition>();
			definitions.add(definition);
			sense.setDefinitions(definitions);

			// setting MonolingualExternalRef
			MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
			monolingualExternalRef.setExternalSystem(resourceVersion + "_" + LEXICAL_UNIT);
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
				logger.error(sbErr.toString());
				System.exit(1);
			}
			PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
			predicativeRepresentation.setPredicate(semanticPredicate);
			List<PredicativeRepresentation> predicativeRepresentations = new ArrayList<PredicativeRepresentation>();
			predicativeRepresentations.add(predicativeRepresentation);
			sense.setPredicativeRepresentations(predicativeRepresentations);

			// SETTING incorporatedSemArg
			// Parse the corpus in order to get more information about the lu
			ac = new AnnotationCorpus15(fn, Logger.getLogger(getClass().getName()));//works
			ac.parse(new File(fnhome+File.separator+"lu"), "lu"+lu.getId()+".xml");
			AnnotatedLexicalUnit alu = ac.getAnnotation(lu);
			if(alu != null){
				// incorporatedSemArg can only be set for annotated lus
				String incorporatedFEName = alu.getIncorporatedFE();
				if(incorporatedFEName != null){
					// Add inCorporated=true information to SemanticArguments
					List<SemanticArgument> semanticArguments = semanticPredicate.getSemanticArguments();
					SemanticArgument incorporatedArgument = null;
					if(semanticArguments == null){ // no args => create a new one //not needed
						semanticArguments = new ArrayList<SemanticArgument>();
					}
					if (semanticArguments.size()==0){
						incorporatedArgument = semanticPredicateGenerator.createIncorporatedSemanticArgument(incorporatedFEName);
						semanticArguments.add(incorporatedArgument);
					} else { // change the isIncorporated Flag of the corresponding argument
						for (SemanticArgument semanticArgument: semanticArguments){
							if (semanticArgument.getSemanticRole().equals(incorporatedFEName)){
								incorporatedArgument = semanticArgument;
								incorporatedArgument.setIncorporated(true);
							}
						}
					}
					semanticPredicate.setSemanticArguments(semanticArguments);
					if (incorporatedArgument != null){
						sense.setIncorporatedSemArg(incorporatedArgument);
					}
 				}
				// MAPPING LU'S SEMTYPE TO SENSE NEW
				Set<String> semTypes = alu.getSemTypes();
				for (String s: semTypes){
					SemanticType t = null;
					try {
						t = fn.getSemanticType(s);
					} catch (SemanticTypeNotFoundException e) {
						logger.warn("Did not find semantic type in FN: " + s);
					}
					if (s.matches("^[0-9]+")){// ID
						System.err.println("ID: " + s);
					} else { // filter different types
						if (s.equalsIgnoreCase("Transparent Noun")|| s.equalsIgnoreCase("9")){ // no semantic label
							sense.setTransparentMeaning(true);
						} else {// semantic label format
							sense.setTransparentMeaning(false);
							SemanticLabel semanticLabel = new SemanticLabel();
							if (s.equalsIgnoreCase("Negative_judgment")||s.equalsIgnoreCase("Positive_judgment")){
								//type
								semanticLabel.setType(ELabelTypeSemantics.sentiment);
								semanticLabel.setLabel(t.getName());
							} else if (s.equalsIgnoreCase("Bound_LU") || s.equalsIgnoreCase("Bound_dependent_LU") || s.equalsIgnoreCase("Support")||s.equals("223")){
								semanticLabel.setType(ELabelTypeSemantics.collocate);
								semanticLabel.setLabel(t.getName());
							} else if (s.equalsIgnoreCase("Biframal_LU")){
								//Does not occur
								semanticLabel.setType(ELabelTypeSemantics.resourceSpecific);
								semanticLabel.setLabel(t.getName());
							} else if (s.equalsIgnoreCase("Tendency_Grading_LU")){
								semanticLabel.setType(ELabelTypeSemantics.resourceSpecific);
								semanticLabel.setLabel(t.getName());
							} else {
								// this should be ontological types
								semanticLabel.setType(ELabelTypeSemantics.semanticCategory);
								semanticLabel.setLabel(t.getName());
							}
							// for all semantic labels
							// creating MonolingualExternalRef for SemanticLabel
							List<MonolingualExternalRef> merefs = new LinkedList<MonolingualExternalRef>();
							MonolingualExternalRef meref = new MonolingualExternalRef();
							meref.setExternalReference(s);
							meref.setExternalSystem(resourceVersion + "_" + SEMANTIC_TYPE);
							merefs.add(meref);
							semanticLabel.setMonolingualExternalRefs(monolingualExternalRefs);
							List<SemanticLabel> semanticLabels = sense.getSemanticLabels();
							if(semanticLabels == null) {
                                semanticLabels = new ArrayList<SemanticLabel>();
                            }
							semanticLabels.add(semanticLabel);
							sense.setSemanticLabels(semanticLabels);
						}
					}
				}

				List<Lexeme> lexemes = lu.getLexemes();
				// Creating a list of components for multiword LexicalUnits
				if(lexemes.size() > 1) {
                    listOfComponents = createListOfComponents(lexemes);
                }


				List<SenseExample> senseExamples = sense.getSenseExamples();
				if(senseExamples == null) {
                    senseExamples = new ArrayList<SenseExample>();
                }
				sense.setSenseExamples(senseExamples);

				// GETTING ANNOTATION SENTENCES
				for(Sentence sentence : alu.getSentences()){
					// Creating a SenseExample for every Sentence
					SenseExample senseExample = new SenseExample();
					StringBuffer sexID = new StringBuffer(32);
					sexID.append("FN_SenseExample_").append(senseExampleNumber++);
					senseExample.setId(sexID.toString());
					TextRepresentation sexTR = new TextRepresentation();
					sexTR.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
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
			if(frequencies == null) {
                frequencies = new ArrayList<Frequency>();
            }

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

		if(epos == null) {
            posNotFound(pos);
        }

		lexicalEntry.setPartOfSpeech(epos);

		// Creting a lemma
		Lemma lemma = new Lemma();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
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
			logger.error(sb.toString());
			System.exit(1);
		}

		// Setting listOfComponents
		if(listOfComponents != null) {
            lexicalEntry.setListOfComponents(listOfComponents);
        }

		return lexicalEntry;
	}

    /**
     * This method consumes a list of {@link Lexeme Lexemes} and creates a {@link ListOfComponents}.
     * The {@link Component Components} do NOT have the targetLexicalEntry attribute set!
     *
     * @param lexemes
     *            the list of Lexemes from which a ListOfComponents should be generated
     * @return ListOfComponents based on consumed lexemes
     */
	private ListOfComponents createListOfComponents(List<Lexeme> lexemes) {
		ListOfComponents listOfComponents = new ListOfComponents();
		List<Component> components = new LinkedList<Component>();
		for(Lexeme lexeme : lexemes){
			// Create a Component for every lexeme
			Component component = new Component();
			component.setHead(lexeme.isHeadword());
			component.setBreakBefore(lexeme.isBreakBefore());
			component.setPosition(lexeme.getOrder());
			PartOfSpeech pos = lexeme.getPartOfSpeech();
			components.add(component);
			String name = lexeme.getValue();

			// Record for creation of targetLexicalEntry attribute later
			Map<String, List<Component>> mapping =  this.components.get(pos);

			List<Component> cmps = mapping.get(name);
			if(cmps == null) {
                cmps = new LinkedList<Component>();
            }
			cmps.add(component);
			mapping.put(name, cmps);
		}
		listOfComponents.setComponents(components);
		return listOfComponents;
	}


    /**
     * This method creates an ID for a {@link LexicalEntry}
     *
     * @return ID for an instance of LexicalEntry
     */
	private String createID() {
		StringBuffer sb = new StringBuffer(32);
		sb.append("FN_LexicalEntry_").append(lexicalEntryNumber++);
		return sb.toString();
	}

    /**
     * This method iterates over all created {@link Component Components} and updates their
     * {@link Component#getTargetLexicalEntry() targetLexicalEntry} attribute
     */
	private void updateComponents() {
		for(PartOfSpeech pos : components.keySet()) {
            for(String lemma : components.get(pos).keySet()) {
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
							logger.error(sb.toString());
							System.exit(1);
						}
					}
                    else {
                        // when no luGroup with this lemma has been found
						// check if a dummy LexicalEntry exists
						lexicalEntry = dummyLEs.get(pos).get(lemma);
                    }

					if(lexicalEntry != null){
						// component has a corresponding LexicalEntry
						component.setTargetLexicalEntry(lexicalEntry);
					}
					else{
						// component does not have a corresponding LexicalEntry
						// a new LexicalEntry will be created
						lexicalEntry = new LexicalEntry();
						EPartOfSpeech epos = FNUtils.getPOS(pos);
						if(epos == null) {
                            posNotFound(pos);
                        }
						lexicalEntry.setPartOfSpeech(epos);
						lexicalEntry.setId(createID());
						List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
						FormRepresentation formRepresentation = new FormRepresentation();
						formRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
						formRepresentation.setWrittenForm(lemma);
						formRepresentations.add(formRepresentation);
						Lemma lemmaObj = new Lemma();
						lemmaObj.setLexicalEntry(lexicalEntry);
						lemmaObj.setFormRepresentations(formRepresentations);
						component.setTargetLexicalEntry(lexicalEntry);
						lexicalEntry.setLemma(lemmaObj);

						// Add a record for future
						dummyLEs.get(pos).put(lemma, lexicalEntry);
						lexicalEntries.add(lexicalEntry);
					}
				}
            }
        }
	}

	/**
	 * This method groups all LexicalUnits by lemma and part of speech
	 */
	private void groupLUs() {
		mappings = new LinkedHashMap<PartOfSpeech, Map<String, Set<LexicalUnit>>>();
		PartOfSpeech[] poses = PartOfSpeech.values();

		for(PartOfSpeech pos : poses) {
            mappings.put(pos, new TreeMap<String, Set<LexicalUnit>>());
        }

		for(LexicalUnit lu : fn.getLexicalUnits()){
			Map<String, Set<LexicalUnit>> lemmaLUMappings = mappings.get(lu.getPartOfSpeech());
			String lemma = lu.getLexemeString(); // lu's lemma



			// Appending partOfSpeech of components for multiword expressions
			List<Lexeme> lexemes = lu.getLexemes();
			if(lexemes.size() > 1) {
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
            }

			Set<LexicalUnit> luGroup = lemmaLUMappings.get(lemma);
			if(luGroup == null) {
                luGroup = new LinkedHashSet<LexicalUnit>();
            }
			luGroup.add(lu);
			lemmaLUMappings.put(lemma, luGroup);
		}
		}

    /**
     * This method is called when an associated part of speech, defined in {@link EPartOfSpeech},
     * could not be found for part of speech defined in {@link PartOfSpeech}.
     * It informs the user about the situation and terminates the running process
     *
     * @param pos
     *            part of speech defined in {@link PartOfSpeech}, for which an associated part of
     *            speech in {@link EPartOfSpeech} could not be found
     */
	private void posNotFound(PartOfSpeech pos){
		StringBuffer sb = new StringBuffer(128);
        sb.append("LexicalEntryGenerator: FNUtils returned null for PartOfSpeech: ").append(pos)
                .append(" Aborting all operations.");
		logger.error(sb.toString());
		System.exit(1);
	}

    /**
     * Returns all {@link LexicalEntry LexicalEntries} generated by this
     * {@link LexicalEntryGenerator}.
     *
     * @return the lexicalEntries
     */
	public List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}
}
