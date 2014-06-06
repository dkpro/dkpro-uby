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
package de.tudarmstadt.ukp.lmf.transform.verbnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
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
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcatFrameSetElement;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;

/**
 * This class extracts information from a preprocessed version of VerbNet and fills in the corresponding LMF classes
 * @author Eckle-Kohler
 *
 */
public class VerbNetExtractor {

	public static final String SENSE = "sense";
	public Lexicon lexicon = new Lexicon();
	
	private File verbNetInputFile; // The File containing the VerbNet Input
	private String resourceID; // name of the LMF lexicon, i.e. "VerbNet"

	// running numbers for IDs
	private static int lexicalEntryNumber = 0; 
	private static int senseNumber = 0; 
	private static int senseExampleNumber = 0; 
	private static int syntacticBehaviourNumber = 0; 
	private static int subcatFrameSetNumber = 0; 
	private static int subcatFrameNumber = 0; 
	private static int semanticPredNumber = 0; 
	private static int synSemCorrNumber = 0; 
	private static int syntacticArgumentNumber = 0; 
	private static int semanticArgumentNumber = 0;
	
	private final String resourceVersion;

	// Mapping between verb lemmas and their' corresponding sense definitions in VerbNet
	private static Map<String, Set<VerbNetSense>> LemmaVerbNetSenseMappings = new TreeMap<String, Set<VerbNetSense>>();
	// Mapping between LMF-Code of purely syntactic SC-Frame and SubcategorizationFrame
	private static Map<String, SubcategorizationFrame> synargsSubcatFrameMap  = new TreeMap<String, SubcategorizationFrame>();
	// Mapping between LMF-Code of syntactic/semantic (including thematic roles) SC-Frame and SynSemCorrespondence
	private static Map<List<String>, SynSemCorrespondence> predsynsemargsSynSemCorrMap  = new LinkedHashMap<List<String>, SynSemCorrespondence>();
	// Mapping between className and SubcategorizationFrameSet
	private static Map<String, SubcategorizationFrameSet> classSubcatFrameSetMap  = new TreeMap<String, SubcategorizationFrameSet>();
	// Mapping between className and set of SubcategorizationFrames
	private static Map<String, Set<SubcategorizationFrame>> classSCframeElementsMap  = new TreeMap<String, Set<SubcategorizationFrame>>();
	// Mapping between LMF-Code of semantic predicate and SemanticPredicate
	private static Map<String, SemanticPredicate> predSemPredicateMap  = new TreeMap<String, SemanticPredicate>();
	// Mapping between VerbNetSense and SubcategorizationFrameSet
	private static Map<VerbNetSense, SubcategorizationFrameSet> senseSubcatFrameSetMap  = new LinkedHashMap<VerbNetSense, SubcategorizationFrameSet>();

	private static List<VerbNetSense> listOfVerbNetSenses = new LinkedList <VerbNetSense>();

	/**
	 * Constructs a VerbNetExtractor
	 * @param verbNetInput path of the File containing the preprocessed version of VerbNet
	 * @param resourceID name of the LMF Lexicon instance
	 * @param resourceVersion Version of the resource
	 * @return VerbNetExtractor
	 * @throws IOException 
	 */
	public VerbNetExtractor(File verbNetInput, String resourceID, String resourceVersion) throws IOException {

		this.verbNetInputFile = verbNetInput;
		this.resourceID = resourceID;
		this.resourceVersion = resourceVersion;
		parseVerbNetInput();
		convertVerbNetInput();
	}
	
	/**
	 * This method parses the document containing the verbNetInput
	 * verbNetInput has the form: <verb>#<wnSense>#<example>#((<Arg>),..,(<Arg>))#<predicateString># ...
	 * 
	 * @throws IOException
	 */
	private void parseVerbNetInput() throws IOException {
		System.out.print("Parsing VerbNet Input...");
		BufferedReader input = new BufferedReader(new FileReader(verbNetInputFile));
		try {
			String line;
			String[] parts;
			List<String> synSemArgs = new LinkedList <String>();
			Set<VerbNetSense> vnSenses = new LinkedHashSet<VerbNetSense>(); // Processed VerbNet senses

			while ((line = input.readLine()) != null) {
				parts = line.split("#");
				VerbNetSense verbNetSense = new VerbNetSense(parts[0],parts[1],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7]);
				synSemArgs = getSelRestr(verbNetSense.arguments,verbNetSense.roleSet);
				verbNetSense.synSemArgs = synSemArgs;
				if (LemmaVerbNetSenseMappings.containsKey(verbNetSense.lemma)) {
					vnSenses = LemmaVerbNetSenseMappings.get(verbNetSense.lemma);
					vnSenses.add(verbNetSense);
					LemmaVerbNetSenseMappings.put(verbNetSense.lemma,vnSenses);
				} else {
					Set<VerbNetSense> newSense = new LinkedHashSet<VerbNetSense>();
					newSense.add(verbNetSense);
					LemmaVerbNetSenseMappings.put(verbNetSense.lemma,newSense);
				}
				listOfVerbNetSenses.add(verbNetSense);
			}		
			System.out.println("done");
		} finally {
			input.close();
		}
	}
	
	/**
	 * This method extracts selectional restrictions from the syntactic-semantic arguments
	 * and from the set of thematic roles
	 * @param arguments syntactic-semantic arguments
	 * @param roleSet the set of thematic roles
	 * @return selectional restrictions
	 */
	private List<String> getSelRestr(String arguments, String roleSet) {
		List<String> newArgs = new LinkedList <String>();
		Map<String, String> RoleRestrMap  = new LinkedHashMap<String, String>();

		String [] roles = roleSet.split("%");
		for(String role : roles){
			String[] pair = role.split("\\[");
			String themRole = pair[0];
			RoleRestrMap.put(themRole,role);
		}
		String [] args = arguments.split(":");		
		for(String arg : args){	
			String newAtt = null;
			String [] attributes = arg.split(",");
			for(String att : attributes){
				String [] pair = att.split("=");
				if ((pair[0].equals("role")) && (RoleRestrMap.containsKey(pair[1]))) {
					String newRole = pair[0].concat("=").concat(RoleRestrMap.get(pair[1]));
					if (newAtt.equals(null)) {
						newAtt = newRole;
					} else {
						newAtt = newAtt.concat(",").concat(newRole);
					}
				} else {
					if (newAtt ==null) {
						newAtt = att;
					} else {
						newAtt = newAtt.concat(",").concat(att);
					}					
				}	
			}
			newArgs.add(newAtt);
		}
		return newArgs;
	}
	
	/**
	 * This method created LMF classes and
	 * stores the extracted VerbNet information in these LMF classes
	 * 
	 */
	private  void convertVerbNetInput() {
		lexicon.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
		lexicon.setId("VN_Lexicon_0");
		lexicon.setName(resourceID);
		
		// Create subclasses of Lexicon that are independent of particular lexemes
		List<SubcategorizationFrame> subcategorizationFrames = new LinkedList <SubcategorizationFrame>();
		List<SemanticPredicate> semanticPredicates = new LinkedList <SemanticPredicate>();		
		List<SubcategorizationFrameSet> subcategorizationFramesSets = new LinkedList <SubcategorizationFrameSet>();
		List<SynSemCorrespondence> synSemCorrespondences = new LinkedList<SynSemCorrespondence>();
		
		for (VerbNetSense vnSense : listOfVerbNetSenses) {
			// Create SemanticPredicates
			if (!predSemPredicateMap.containsKey(vnSense.predicate)) {
				SemanticPredicate semanticPredicate = new SemanticPredicate();
				semanticPredicate.setId("VN_SemanticPredicate_".concat(Integer.toString(semanticPredNumber)));
				semanticPredNumber++;
				List<Definition> definitions = new ArrayList<Definition>(); // Create Definitions
				Definition definition = new Definition();
				List<TextRepresentation> textReps = new ArrayList<TextRepresentation>();	// Create TextRepresentations
				TextRepresentation textRep = new TextRepresentation();
				textRep.setWrittenText(vnSense.predicate);
				textReps.add(textRep);
				definition.setTextRepresentations(textReps);			// Save TextRepresentations
				definitions.add(definition);
				semanticPredicate.setDefinitions(definitions);			// Save Definitions
				predSemPredicateMap.put(vnSense.predicate,semanticPredicate);
			}	
		}
		
		for (VerbNetSense vnSense : listOfVerbNetSenses) {
			// Create SubcatFrames
			if (!synargsSubcatFrameMap.containsKey(vnSense.synArgs)) { 
				SubcategorizationFrame subcategorizationFrame = new SubcategorizationFrame();
				subcategorizationFrame.setId("VN_SubcategorizationFrame_".concat(Integer.toString(subcatFrameNumber)));
				subcatFrameNumber++;	
				subcategorizationFrame = parseArguments(vnSense,subcategorizationFrame);
				synargsSubcatFrameMap.put(vnSense.synArgs,subcategorizationFrame);								
			}	 	
			// Create Semantic Arguments (if not already done), establish SynSemCorrespondence
			if (!predsynsemargsSynSemCorrMap.containsKey(vnSense.synSemArgs.add(vnSense.predicate))) { 
				SubcategorizationFrame subcategorizationFrame = synargsSubcatFrameMap.get(vnSense.synArgs);
				parseSemanticArguments(vnSense,subcategorizationFrame);
			}			
		}
		
		for (VerbNetSense vnSense : listOfVerbNetSenses) {
			// Create SubcatFrameSets
			String [] classInfo = vnSense.classInformation.split("\\(");
			String superClass = classInfo[1];
			superClass = superClass.replaceAll("\\)", "");							
			if (!classSubcatFrameSetMap.containsKey(classInfo[0])) {
				SubcategorizationFrameSet subcategorizationFrameSet = new SubcategorizationFrameSet();
				subcategorizationFrameSet.setId("VN_SubcategorizationFrameSet_".concat(Integer.toString(subcatFrameSetNumber)));
				subcategorizationFrameSet.setName(classInfo[0]);
				subcatFrameSetNumber++;	
				
				if (!superClass.equals("NULL")) { // inherits attribute needs to be set
					// super class might be empty, therefore create a new SubcatFrameSet NOW!
					// empty classes (without verbs) are the reason why the mapping is defined between className and
					// SubcatFrameSet, rather than between classInformation = class(superClass) and SubcatFrameSet
					if (!classSubcatFrameSetMap.containsKey(superClass)) {
						SubcategorizationFrameSet superFrameSet = new SubcategorizationFrameSet();
						superFrameSet.setId("VN_SubcategorizationFrameSet_".concat(Integer.toString(subcatFrameSetNumber)));
						superFrameSet.setName(superClass);
						subcatFrameSetNumber++;	
						
						classSubcatFrameSetMap.put(superClass,superFrameSet);
						subcategorizationFrameSet.setParentSubcatFrameSet(superFrameSet);
					} else {
						subcategorizationFrameSet.setParentSubcatFrameSet(classSubcatFrameSetMap.get(superClass));
					}
				} 					
				classSubcatFrameSetMap.put(classInfo[0],subcategorizationFrameSet);
				senseSubcatFrameSetMap.put(vnSense, subcategorizationFrameSet);
				
				if (classSCframeElementsMap.get(classInfo[0]) == null) {
					Set<SubcategorizationFrame> scFrames = new LinkedHashSet<SubcategorizationFrame>();
					scFrames.add(synargsSubcatFrameMap.get(vnSense.synArgs));
					
					classSCframeElementsMap.put(classInfo[0], scFrames);
				} else {
					Set<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(classInfo[0]);
					scFrames.add(synargsSubcatFrameMap.get(vnSense.synArgs));
					classSCframeElementsMap.put(classInfo[0], scFrames);
				}
			} else {
				senseSubcatFrameSetMap.put(vnSense, classSubcatFrameSetMap.get(classInfo[0]));
				if (classSCframeElementsMap.get(classInfo[0]) == null) {
					Set<SubcategorizationFrame> scFrames = new LinkedHashSet<SubcategorizationFrame>();
					scFrames.add(synargsSubcatFrameMap.get(vnSense.synArgs));
					
					classSCframeElementsMap.put(classInfo[0], scFrames);
				} else {
					Set<SubcategorizationFrame> scFrames = classSCframeElementsMap.get(classInfo[0]);
					scFrames.add(synargsSubcatFrameMap.get(vnSense.synArgs));
					classSCframeElementsMap.put(classInfo[0], scFrames);
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
		
		synSemCorrespondences.addAll(predsynsemargsSynSemCorrMap.values());
		lexicon.setSynSemCorrespondences(synSemCorrespondences);
		
		semanticPredicates.addAll(predSemPredicateMap.values());
		Collections.sort(semanticPredicates);
		lexicon.setSemanticPredicates(semanticPredicates);

		subcategorizationFrames.addAll(synargsSubcatFrameMap.values());
		Collections.sort(subcategorizationFrames);
		lexicon.setSubcategorizationFrames(subcategorizationFrames);
		
		subcategorizationFramesSets.addAll(classSubcatFrameSetMap.values());
		lexicon.setSubcategorizationFrameSets(subcategorizationFramesSets);

		List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
		Iterator<String> keyIterator = LemmaVerbNetSenseMappings.keySet().iterator();
		while (keyIterator.hasNext()) {
			String verbLemma = keyIterator.next();
			
			LexicalEntry lexicalEntry = new LexicalEntry();
			// Create ID
			lexicalEntry.setId("VN_LexicalEntry_".concat(Integer.toString(lexicalEntryNumber)));
			lexicalEntryNumber++;		
			// Create partOfSpeech
			lexicalEntry.setPartOfSpeech(EPartOfSpeech.verb);		
			// Creating Lemma 
			Lemma lemma = new Lemma();		
			// Create FormRepresentation
			List<FormRepresentation> formReps = new ArrayList<FormRepresentation>();
			FormRepresentation formRep = new FormRepresentation();			
			formRep.setWrittenForm(verbLemma.replaceAll("_", " "));	// Extract FormRepresentation																			
			formReps.add(formRep);				// Save FormRepresentation		
			lemma.setFormRepresentations(formReps);	// Save FormRepresentations		
			lexicalEntry.setLemma(lemma);			// Save Lemma

			// Creating Senses
			List <Sense> senses = new ArrayList<Sense>();
			// Creating SyntacticBehaviors 
			List<SyntacticBehaviour> syntacticBehaviours = new LinkedList <SyntacticBehaviour>();
			
			Iterator<VerbNetSense> senseIterator = LemmaVerbNetSenseMappings.get(verbLemma).iterator();
			while (senseIterator.hasNext()) {
				VerbNetSense vnSense = senseIterator.next();
				Sense sense = new Sense();

				sense.setId("VN_Sense_".concat(Integer.toString(senseNumber)));
				sense.setIndex(senseNumber);
				senseNumber++;
				String [] classInfo = vnSense.classInformation.split("\\(");
				
				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				monolingualExternalRef.setExternalSystem(resourceVersion + "_" + SENSE);
				monolingualExternalRef.setExternalReference(vnSense.lemma + "_" + classInfo[0]);
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				sense.setMonolingualExternalRefs(monolingualExternalRefs);

				List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
				SemanticLabel semanticLabel = new SemanticLabel();
				semanticLabel.setLabel(classInfo[0]);
				semanticLabel.setType(ELabelTypeSemantics.verbnetClass);
				semanticLabels.add(semanticLabel);
				sense.setSemanticLabels(semanticLabels);
				
						
				List<SenseExample> examples = new ArrayList<SenseExample>(); // Create SenseExamples
				SenseExample example = new SenseExample();				
				example.setId("VN_SenseExample_".concat(Integer.toString(senseExampleNumber)));
				senseExampleNumber++;		
				example.setExampleType(EExampleType.subcatFrame);						
				List<TextRepresentation> exTextReps = new ArrayList<TextRepresentation>();	// Create TextRepresentations
				TextRepresentation exTextRep = new TextRepresentation();
				exTextRep.setWrittenText(vnSense.example);				
				exTextReps.add(exTextRep);
				example.setTextRepresentations(exTextReps);			// Save TextRepresentations
				examples.add(example);
				sense.setSenseExamples(examples);			// Save SenseExamples
				
				// Creating SyntacticBehaviour (one for each VerbNet sense)
				SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
				// Generating an ID
				syntacticBehaviour.setId("VN_SyntacticBehaviour_".concat(Integer.toString(syntacticBehaviourNumber)));
				syntacticBehaviourNumber++;
				
				syntacticBehaviour.setSense(sense);
				syntacticBehaviour.setSubcategorizationFrame(synargsSubcatFrameMap.get(vnSense.synArgs));
				syntacticBehaviour.setSubcategorizationFrameSet(senseSubcatFrameSetMap.get(vnSense));
				syntacticBehaviours.add(syntacticBehaviour);
				
				// Creating Predicative Representation (one for each VerbNet sense)
				List<PredicativeRepresentation> predicativeRepresentations = new LinkedList <PredicativeRepresentation>();
				PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
				predicativeRepresentation.setPredicate(predSemPredicateMap.get(vnSense.predicate));
				
				predicativeRepresentations.add(predicativeRepresentation);				
				sense.setPredicativeRepresentations(predicativeRepresentations);		// Save PredicativeRepresentations
				
				senses.add(sense);		// Save Sense

			}
			//Save subclasses of lexicalEntry that require the complete VerbNet-Input to be processed
			lexicalEntry.setSenses(senses);			
			lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);	
			
			lexicalEntries.add(lexicalEntry);		// Save LexicalEntry			
		}
		lexicon.setLexicalEntries(lexicalEntries);
		System.out.println("Statistics");
		System.out.println(lexicalEntryNumber+" LexicalEntries");
		System.out.println(senseNumber+" Senses");
		System.out.println(subcatFrameSetNumber+" SubcatFrameSets");
		System.out.println(subcatFrameNumber+" SubcategorizationFrames");
		System.out.println(semanticPredNumber+" SemanticPredicates");
		System.out.println(synSemCorrNumber+" SynSemCorrespondences");
		System.out.println(syntacticArgumentNumber+" SyntacticArguments");
		System.out.println(semanticArgumentNumber+" SemanticArguments");
}

	/**
	 * This method creates semantic predicates and
	 * establishes a mapping between semantic arguments
	 * and syntactic arguments
	 * @param vnSense a VerbNet sense
	 * @param subcategorizationFrame
	 */
	private void parseSemanticArguments(VerbNetSense vnSense,SubcategorizationFrame subcategorizationFrame) {
		SemanticPredicate semanticPredicate = predSemPredicateMap.get(vnSense.predicate);
		// list of mappings between Syntactic and Semantic Arguments are to be created
		List<SynSemArgMap> synSemArgMaps = new LinkedList<SynSemArgMap>();	
		SynSemArgMap synSemArgMap = null;

		if (semanticPredicate.getSemanticArguments() == null) {
			List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
			int index = 0;
			// iterate over syntactic Arguments
			for (SyntacticArgument synArg: subcategorizationFrame.getSyntacticArguments()) {			
				String synsemArg = vnSense.synSemArgs.get(index);
				if (synsemArg.contains("syntacticProperty")) {
					index++;
					synsemArg = vnSense.synSemArgs.get(index);
				}
				// look at synsemArg: is thematic role defined? if yes: create corresponding semanticArg
				String[] atts = synsemArg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					
					if(attName.equals("role")){
						SemanticArgument semanticArgument = new SemanticArgument();
						semanticArgument.setId("VN_SemanticArgument_".concat(Integer.toString(semanticArgumentNumber)));
						semanticArgumentNumber++;
						
						String semArg = splits[1];
						if (semArg.matches(".*\\[.*\\]")) {
							String [] parts = semArg.split("\\[");
							String semRole = parts[0];
							String selRes = "[" +parts[1];
							semanticArgument.setSemanticRole(semRole);
							List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();
							SemanticLabel semanticLabel = new SemanticLabel();
							semanticLabel.setLabel(selRes);
							semanticLabel.setType(ELabelTypeSemantics.selectionalPreference);
							semanticLabels.add(semanticLabel);
							semanticArgument.setSemanticLabels(semanticLabels);
						} else {
							semanticArgument.setSemanticRole(splits[1]);
						}
						semanticArguments.add(semanticArgument);
						// Generate SynSemArgMapping
						synSemArgMap = new SynSemArgMap();
						synSemArgMap.setSyntacticArgument(synArg);
						synSemArgMap.setSemanticArgument(semanticArgument);
						synSemArgMaps.add(synSemArgMap);
					}																									
				}
				index++;				
			}	
			semanticPredicate.setSemanticArguments(semanticArguments);
			predSemPredicateMap.put(vnSense.predicate,semanticPredicate); // save extended predicate in Mapping
		} else { // Semantic Arguments have already been created for this predicate; SynSemCorr needs to be established
			List<SemanticArgument> semArgs = semanticPredicate.getSemanticArguments();
			
			int semIndex = 0;
			int max = semArgs.size();
			int synsemIndex = 0;
			// iterate over syntactic Arguments
			for (SyntacticArgument synArg: subcategorizationFrame.getSyntacticArguments()) {
				String synsemArg = vnSense.synSemArgs.get(synsemIndex);
				if (synsemArg.contains("syntacticProperty")) {
					synsemIndex++;
					synsemArg = vnSense.synSemArgs.get(synsemIndex);
				}
				// look at synsemArg: is thematic role defined? if yes: create SynSemArgMap
				String[] atts = synsemArg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					if (attName.equals("role")){
						if (semIndex >= max)  { // this subcatFrame contains roles that were not present in previous subcatFrames with the same predicate
							SemanticArgument semanticArgument = new SemanticArgument();
							semanticArgument.setId("VN_SemanticArgument_".concat(Integer.toString(semanticArgumentNumber)));
							semanticArgumentNumber++;
							String semArg = splits[1];
							if (semArg.matches("\\[")) {
								String [] parts = att.split("[");
								String semRole = parts[0];
								String selRes = "[" +parts[1];
								semanticArgument.setSemanticRole(semRole);
								List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();
								SemanticLabel semanticLabel = new SemanticLabel();
								semanticLabel.setLabel(selRes);
								semanticLabel.setType(ELabelTypeSemantics.selectionalPreference);
								semanticLabels.add(semanticLabel);
								semanticArgument.setSemanticLabels(semanticLabels);
							} else {
								semanticArgument.setSemanticRole(splits[1]);
							}

							semArgs.add(semanticArgument);
							// Generate SynSemArgMapping
							synSemArgMap = new SynSemArgMap();
							synSemArgMap.setSyntacticArgument(synArg);
							synSemArgMap.setSemanticArgument(semanticArgument);
							synSemArgMaps.add(synSemArgMap);
							semanticPredicate.setSemanticArguments(semArgs);
							predSemPredicateMap.put(vnSense.predicate,semanticPredicate); // save extended predicate in Mapping
						} else {
							SemanticArgument semArg = semArgs.get(semIndex);
							// Create SynSemArgMap
							synSemArgMap = new SynSemArgMap();
							synSemArgMap.setSyntacticArgument(synArg);
							synSemArgMap.setSemanticArgument(semArg);
							synSemArgMaps.add(synSemArgMap);
							semIndex++;
						}
					}																									
				}
				synsemIndex++;				
			}					
		}				
		SynSemCorrespondence synSemCorrespondence = new SynSemCorrespondence();
		synSemCorrespondence.setId("VN_SynSemCorrespondence_".concat(Integer.toString(synSemCorrNumber)));
		synSemCorrNumber++;
		synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
		
		List<String> predsynsemargs = vnSense.synSemArgs;
		vnSense.synSemArgs.add(vnSense.predicate);
		predsynsemargsSynSemCorrMap.put(predsynsemargs,synSemCorrespondence);
	}

	/**
	 * This method creates (purely syntactic) subcategorization frames
	 * @param vnSense a VerbNet sense
	 * @param subcatFrame a subcategorization frame
	 * @return the subcategorization frame
	 */
	private SubcategorizationFrame parseArguments(VerbNetSense vnSense, SubcategorizationFrame subcatFrame) {
		SubcategorizationFrame scFrame = subcatFrame;
		List<SyntacticArgument> synArgs = new LinkedList<SyntacticArgument>();
		
		for(String arg : vnSense.synSemArgs) {			
			if (!arg.contains("syntacticProperty")) {			
				SyntacticArgument syntacticArgument = new SyntacticArgument();
				syntacticArgument.setId("VN_SyntacticArgument_".concat(Integer.toString(syntacticArgumentNumber)));
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
}
