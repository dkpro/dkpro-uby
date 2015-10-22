/*******************************************************************************
 * Copyright 2015
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

package de.tudarmstadt.ukp.lmf.transform.gvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;

/**
 * This class extracts information from a preprocessed version of a subset of IMSlex and fills in the corresponding LMF classes
 * @author Eckle-Kohler
 *
 */
public class GermanVcExtractor {

	public static final String SENSE = "sense";
	public Lexicon lexicon = new Lexicon();

	private final List<SynSemCorrespondence> synSemCorrespondences = new LinkedList<SynSemCorrespondence>();
	private final List<SemanticPredicate> semanticPredicates = new LinkedList <SemanticPredicate>();

	private final File verbInputFile; // The File containing the Input lexicon
	private final String resourceName; // name of the LMF lexicon, i.e. "IMSlexSubset"
	private final String resourceVersion;

	private final Log logger = LogFactory.getLog(getClass());

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

	// Mapping between verb lemmas and their corresponding sense definitions in GVC
	private static HashMap<String, HashSet<GermanVcSense>> LemmaGermanVcSenseMappings = new HashMap<String, HashSet<GermanVcSense>>();
	// Mapping between syntactic/semantic arguments (containing sem. role information) and purely syntactic arguments
	private static HashMap<String, String> synSemArgSynArgMapping  = new HashMap<String, String>();
	// Mapping between LMF-Code of purely syntactic SC-Frame and SubcategorizationFrame
	private static HashMap<String, SubcategorizationFrame> synargsSubcatFrameMap  = new HashMap<String, SubcategorizationFrame>();
	// Mapping between className and SubcategorizationFrameSet
	private static HashMap<String, SubcategorizationFrameSet> classSubcatFrameSetMap  = new HashMap<String, SubcategorizationFrameSet>();
	// Mapping between className and set of SubcategorizationFrames
	private static HashMap<String, HashSet<SubcategorizationFrame>> classSCframeElementsMap  = new HashMap<String, HashSet<SubcategorizationFrame>>();
	// Mapping between GermanVcSense and SubcategorizationFrameSet
	private static HashMap<GermanVcSense, SubcategorizationFrameSet> senseSubcatFrameSetMap  = new HashMap<GermanVcSense, SubcategorizationFrameSet>();
	// Mapping between GermanVcSense and SemanticPredicate
	private static HashMap<GermanVcSense, SemanticPredicate> senseSemPredicateMap  = new HashMap<GermanVcSense, SemanticPredicate>();

	private static HashMap<String, String> verbClassMap  = new HashMap<String, String>();

	private static List<GermanVcSense> listOfGermanVcSenses = new LinkedList <GermanVcSense>();

	/**
	 * Constructs a GermanVcExtractor
	 * @param gvcInput path of the File containing the preprocessed version of the IMSlex subset
	 * @param resourceName name of the LMF Lexicon instance
	 * @return GermanVcExtractor
	 * @throws IOException
	 */
	public GermanVcExtractor(File gvcInput, String resourceName, String resourceVersion) throws IOException {

		getVerbClassMap();

		this.verbInputFile = gvcInput;
		this.resourceName = resourceName;
		this.resourceVersion = resourceVersion;
		parseGermanVcInput();
		convertGermanVcInput();
	}

	/**
	 * This method parses the document containing the lexicon Input
	 * Input has the form: <verb>%<Arg>:...:<Arg>%classInformation
	 *
	 * @throws IOException
	 */
	private void parseGermanVcInput() throws IOException {
		System.out.print("Parsing GVC Input...");
		Reader r = new InputStreamReader(new FileInputStream(verbInputFile), "UTF8");
		BufferedReader input = new BufferedReader(r);
		try {
			String line;
			String[] parts;
			HashSet<GermanVcSense> gvcSenses = new HashSet<GermanVcSense>(); // Processed verb senses

			while ((line = input.readLine()) != null) {
				parts = line.split("%");
				GermanVcSense gvcSense = new GermanVcSense(parts[0],parts[1],parts[2]);
				if (LemmaGermanVcSenseMappings.containsKey(gvcSense.lemma)) {
					gvcSenses = LemmaGermanVcSenseMappings.get(gvcSense.lemma);
					gvcSenses.add(gvcSense);
					LemmaGermanVcSenseMappings.put(gvcSense.lemma,gvcSenses);
				} else {
					HashSet<GermanVcSense> newSense = new HashSet<GermanVcSense>();
					newSense.add(gvcSense);
					LemmaGermanVcSenseMappings.put(gvcSense.lemma,newSense);
				}
				listOfGermanVcSenses.add(gvcSense);
				if (gvcSense.synArgs.contains("role")) {
					String pureSynArgs = gvcSense.synArgs.replaceFirst(",role=[a-z]+", "");
					synSemArgSynArgMapping.put(gvcSense.synArgs, pureSynArgs);
				} else {
					synSemArgSynArgMapping.put(gvcSense.synArgs, gvcSense.synArgs);
				}
			}
			System.out.println("done");
		} finally {
		    input.close();
			r.close();
		}
	}

	/**
	 * This method created LMF classes and
	 * stores the extracted lexical information in these LMF classes
	 *
	 */
	private  void convertGermanVcInput() {
		lexicon.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
		lexicon.setId("GVC_Lexicon_0");
		lexicon.setName(resourceName);

		// Create subclasses of Lexicon that are independent of particular lexemes
		List<SubcategorizationFrame> subcategorizationFrames = new LinkedList <SubcategorizationFrame>();
		List<SubcategorizationFrameSet> subcategorizationFramesSets = new LinkedList <SubcategorizationFrameSet>();

		for (GermanVcSense gvcSense : listOfGermanVcSenses) {
			// Create SubcatFrames and SemanticPredicates
			String synArgs = synSemArgSynArgMapping.get(gvcSense.synArgs);

			if (!synargsSubcatFrameMap.containsKey(synArgs)) {
				SubcategorizationFrame subcategorizationFrame = new SubcategorizationFrame();
				subcategorizationFrame.setId("GVC_SubcategorizationFrame_".concat(Integer.toString(subcatFrameNumber)));
				subcatFrameNumber++;
				subcategorizationFrame = parseArguments(gvcSense,subcategorizationFrame);

				synargsSubcatFrameMap.put(synArgs,subcategorizationFrame);
				if (gvcSense.synArgs.contains("role")) { //only few GVC-frames specify a semantic role
					SemanticPredicate semanticPredicate = new SemanticPredicate();
					semanticPredicate = parseSemanticArguments(gvcSense,subcategorizationFrame);
					semanticPredicates.add(semanticPredicate);

					senseSemPredicateMap.put(gvcSense, semanticPredicate);
				}
			} else {
				SubcategorizationFrame subcategorizationFrame = synargsSubcatFrameMap.get(synArgs);
				if (gvcSense.synArgs.contains("role")) { //only few GVC-frames specify a semantic role
					SemanticPredicate semanticPredicate = new SemanticPredicate();
					semanticPredicate = parseSemanticArguments(gvcSense,subcategorizationFrame);
					semanticPredicates.add(semanticPredicate);

					senseSemPredicateMap.put(gvcSense, semanticPredicate);
					}
			}
		}

		/*
		for (GermanVcSense gvcSense : listOfGermanVcSenses) {
			// Create SubcatFrameSets
			if (!classSubcatFrameSetMap.containsKey(gvcSense.classInformation)) {
				SubcategorizationFrameSet subcategorizationFrameSet = new SubcategorizationFrameSet();
				subcategorizationFrameSet.setId("GVC_SubcategorizationFrameSet_".concat(Integer.toString(subcatFrameSetNumber)));
				subcategorizationFrameSet.setName(gvcSense.classInformation);
				subcatFrameSetNumber++;

				classSubcatFrameSetMap.put(gvcSense.classInformation,subcategorizationFrameSet);
				senseSubcatFrameSetMap.put(gvcSense, subcategorizationFrameSet);

				if (classSCframeElementsMap.get(gvcSense.classInformation) == null) {
					HashSet<SubcategorizationFrame> scFrames = new HashSet<SubcategorizationFrame>();
					scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(gvcSense.synArgs)));

					classSCframeElementsMap.put(gvcSense.classInformation, scFrames);
				} else {
					HashSet<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(gvcSense.classInformation);
					scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(gvcSense.synArgs)));
					classSCframeElementsMap.put(gvcSense.classInformation, scFrames);
				}
			} else {
				senseSubcatFrameSetMap.put(gvcSense, classSubcatFrameSetMap.get(gvcSense.classInformation));
				if (classSCframeElementsMap.get(gvcSense.classInformation) == null) {
					HashSet<SubcategorizationFrame> scFrames = new HashSet<SubcategorizationFrame>();
					scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(gvcSense.synArgs)));

					classSCframeElementsMap.put(gvcSense.classInformation, scFrames);
				} else {
					HashSet<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(gvcSense.classInformation);
					scFrames.add(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(gvcSense.synArgs)));
					classSCframeElementsMap.put(gvcSense.classInformation, scFrames);
				}
			}
		}

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
		*/

		subcategorizationFrames.addAll(synargsSubcatFrameMap.values());
		Collections.sort(subcategorizationFrames);
		lexicon.setSubcategorizationFrames(subcategorizationFrames);

		//subcategorizationFramesSets.addAll(classSubcatFrameSetMap.values());
		//lexicon.setSubcategorizationFrameSets(subcategorizationFramesSets);

		lexicon.setSemanticPredicates(semanticPredicates);

		lexicon.setSynSemCorrespondences(synSemCorrespondences);

		List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
		Iterator<String> keyIterator = LemmaGermanVcSenseMappings.keySet().iterator();
		while (keyIterator.hasNext()) {
			String verbLemma = keyIterator.next();

			LexicalEntry lexicalEntry = new LexicalEntry();
			// Create ID
			lexicalEntry.setId("GVC_LexicalEntry_".concat(Integer.toString(lexicalEntryNumber)));
			lexicalEntryNumber++;
			// Create partOfSpeech
			lexicalEntry.setPartOfSpeech(EPartOfSpeech.verbMain);
			// Create Lemma
			Lemma lemma = new Lemma();
			// Create FormRepresentation
			List<FormRepresentation> formReps = new ArrayList<FormRepresentation>();
			FormRepresentation formRep = new FormRepresentation();
			formRep.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
			if (verbLemma.contains("#")) {
				String[] parts = verbLemma.split("#");
				String prefix = parts[0];
				lexicalEntry.setSeparableParticle(prefix);
				String newVerbLemma = prefix.concat(parts[1]);
				formRep.setWrittenForm(newVerbLemma);
			} else {
				formRep.setWrittenForm(verbLemma);
			}

			formReps.add(formRep);				// Save FormRepresentation
			lemma.setFormRepresentations(formReps);	// Save FormRepresentations
			lexicalEntry.setLemma(lemma);			// Save Lemma

			// Create Senses
			List <Sense> senses = new ArrayList<Sense>();
			// Create SyntacticBehavior
			List<SyntacticBehaviour> syntacticBehaviours = new LinkedList <SyntacticBehaviour>();

			Iterator<GermanVcSense> senseIterator = LemmaGermanVcSenseMappings.get(verbLemma).iterator();
			while (senseIterator.hasNext()) {
				GermanVcSense gvcSense = senseIterator.next();
				Sense sense = new Sense();

				sense.setId("GVC_Sense_".concat(Integer.toString(senseNumber)));
				sense.setIndex(senseNumber);
				senseNumber++;

				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				monolingualExternalRef.setExternalSystem(resourceVersion + "_" + SENSE);
				monolingualExternalRef.setExternalReference(gvcSense.lemma);
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				sense.setMonolingualExternalRefs(monolingualExternalRefs);

				List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
				SemanticLabel semanticLabel = new SemanticLabel();
				semanticLabel.setLabel(verbClassMap.get(gvcSense.classInformation));
				semanticLabel.setType(ELabelTypeSemantics.syntacticAlternationClass);
				semanticLabels.add(semanticLabel);
				sense.setSemanticLabels(semanticLabels);

				// Creating SyntacticBehaviour (one for each sense)
				SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
				// Generating an ID
				syntacticBehaviour.setId("GVC_SyntacticBehaviour_".concat(Integer.toString(syntacticBehaviourNumber)));
				syntacticBehaviourNumber++;

				syntacticBehaviour.setSense(sense);
				syntacticBehaviour.setSubcategorizationFrame(synargsSubcatFrameMap.get(synSemArgSynArgMapping.get(gvcSense.synArgs)));
				syntacticBehaviour.setSubcategorizationFrameSet(senseSubcatFrameSetMap.get(gvcSense));
				syntacticBehaviours.add(syntacticBehaviour);

				if (senseSemPredicateMap.containsKey(gvcSense)) {
					// Creating Predicative Representation
					List<PredicativeRepresentation> predicativeRepresentations = new LinkedList <PredicativeRepresentation>();
					PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
					predicativeRepresentation.setPredicate(senseSemPredicateMap.get(gvcSense));

					predicativeRepresentations.add(predicativeRepresentation);
					sense.setPredicativeRepresentations(predicativeRepresentations);// Save PredicativeRepresentations
				}

				senses.add(sense);// Save Sense

			}
			//Save subclasses of lexicalEntry that require the complete lexicon Input to be processed
			lexicalEntry.setSenses(senses);
			lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);

			lexicalEntries.add(lexicalEntry);// Save LexicalEntry
		}
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

	private void getVerbClassMap() {
		InputStream classMapping;
		try {
			classMapping = getClass().getClassLoader().getResource("ClassMappings/verbClassMapping").openStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(classMapping));
			String line;
			while((line = input.readLine())!=null){
				String temp[] = line.split("\t");
				String oldClass = temp[1];
				String newClass = temp[4];
				System.out.println(oldClass +"\t" +newClass);
				verbClassMap.put(oldClass, newClass);
			}
			input.close();
		} catch (IOException e) {
			logger.error("GermanVcExtractor: unable to load class mapping file. Aborting all operations");
			System.exit(1);
		}
	}

	/**
	 * This method creates (purely syntactic) subcategorization frames
	 * @param gvcSense a GVC sense
	 * @param subcatFrame a subcategorization frame
	 * @return the subcategorization frame
	 */
	private SubcategorizationFrame parseArguments(GermanVcSense gvcSense, SubcategorizationFrame subcatFrame) {
		SubcategorizationFrame scFrame = subcatFrame;
		List<SyntacticArgument> synArgs = new LinkedList<SyntacticArgument>();
		String[] args = gvcSense.synArgs.split(":");
		for(String arg : args) {
			if (!arg.contains("syntacticProperty")) {
				SyntacticArgument syntacticArgument = new SyntacticArgument();
				syntacticArgument.setId("GVC_SyntacticArgument_".concat(Integer.toString(syntacticArgumentNumber)));
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
	 * @param gvcSense a GVC sense
	 * @param subcategorizationFrame a subcategorization frame
	 * @returns the semantic predicate
	 */
	private SemanticPredicate parseSemanticArguments(GermanVcSense gvcSense,SubcategorizationFrame subcategorizationFrame) {
		// list of mappings between Syntactic and Semantic Arguments are to be created
		SemanticPredicate semanticPredicate = new SemanticPredicate();
		semanticPredicate.setId("GVC_SemanticPredicate_".concat(Integer.toString(semanticPredicateNumber)));
		semanticPredicateNumber++;
		List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
		List<SynSemArgMap> synSemArgMaps = new LinkedList<SynSemArgMap>();
		SynSemArgMap synSemArgMap = new SynSemArgMap();

		String[] args = gvcSense.synArgs.split(":");
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
					semanticArgument.setId("GVC_SemanticArgument_".concat(Integer.toString(semanticArgumentNumber)));
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
		synSemCorrespondence.setId("GVC_SynSemCorrespondence_".concat(Integer.toString(synSemCorrespondenceNumber)));
		synSemCorrespondenceNumber++;
		synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
		synSemCorrespondences.add(synSemCorrespondence);
		return semanticPredicate;
	}

}