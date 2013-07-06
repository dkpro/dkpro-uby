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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saar.coli.salsa.reiter.framenet.Frame;
import de.saar.coli.salsa.reiter.framenet.FrameElement;
import de.saar.coli.salsa.reiter.framenet.FrameElementNotFoundException;
import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.saar.coli.salsa.reiter.framenet.FrameNetRelationDirection;
import de.saar.coli.salsa.reiter.framenet.FrameNotFoundException;
import de.saar.coli.salsa.reiter.framenet.ParsingException;
import de.saar.coli.salsa.reiter.framenet.SemanticType;
import de.saar.coli.salsa.reiter.framenet.SemanticTypeNotFoundException;
import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECoreType;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.ArgumentRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicateRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;

/**
 * This class offers methods for generating SemanticPredicates
 * out of Frames
 * @author Zijad Maksuti, Silvana Hartmann
 * @see {@link SemanticPredicate}
 * @see {@link Frame}
 */
public class SemanticPredicateGenerator {
	
	private FrameNet fn;
	
	//Mappings between SemanticPredicates and corresponding Frames
	private Map<Frame, SemanticPredicate> frameSemanticPredicateMappings = new HashMap<Frame, SemanticPredicate>();
	
	private int semanticPredicateNumber = 0; // Used for creating IDs
	private int semanticArgumentNumber = 0; // Used for creating IDs
	
	// Mappings between FrameElement-names and SemanticArguments in order to prevent duplication of SemanticArgumens
	private Map<String, SemanticArgument> feSemArgMapping = new HashMap<String, SemanticArgument>();
	
	// list of incorporated SemanticSArguments
//	private List<SemanticArgument> incorporatedSemArgs = new ArrayList<SemanticArgument>();
	
	// all frame relations in FrameNet, used for creating PredicateRelations
	private Set<String> frameRelations;
	
	private Logger logger = Logger.getLogger(FNConverter.class.getName());
	
	/**
	 * Constructs an instance of {@link SemanticPredicateGenerator} used for generating SemanticPredicates
	 * out of Frames
	 * @param fn initialized {@link FrameNet} object used for accessing Frames
	 * @see {@link Frame}
	 * @see {@link SemanticPredicate}
	 */
	public SemanticPredicateGenerator(FrameNet fn){
		this.fn = fn;
		this.init();
	}
	
	/**
	 * Initializes {@link SemanticPredicateGenerator}
	 */
	private void init(){
		for(Frame frame : fn.getFrames())
			frameSemanticPredicateMappings.put(frame, createSemanticPredicate(frame));
		updateArgumentRelations();
		initializeFrameRelations();
		updatePredicateRelations();
	}

	/**
	 * Consumes a {@link Frame} and generates a {@link SemanticPredicate} that corresponds to the consumed Frame
	 * @param frame Frame for which a SemanticPredicate should be generated
	 * @return SemanticPredicate that corresponds to the consumed frame
	 */
	private SemanticPredicate createSemanticPredicate(Frame frame) {
		SemanticPredicate semanticPredicate = new SemanticPredicate();
		semanticPredicate.setLabel(frame.getName());
		
		// Creating Definition
		List<Definition> definitions = new LinkedList<Definition>();
		Definition definition = new Definition();
		List<TextRepresentation> textRepresentations = new LinkedList<TextRepresentation>();
		TextRepresentation textRepresentation = new TextRepresentation();
		textRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
		textRepresentation.setWrittenText(FNUtils.filterTags(frame.getDefinition()));
		textRepresentations.add(textRepresentation);
		definition.setTextRepresentations(textRepresentations);
		definitions.add(definition);
		semanticPredicate.setDefinitions(definitions);
		
		//setting id
		StringBuffer sb = new StringBuffer(32);
		sb.append("FN_SemanticPredicate_").append(semanticPredicateNumber++);
		semanticPredicate.setId(sb.toString());
		
		List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();
		
		for(String semTypeID : frame.getSemTypeIDs()){
			if(semTypeID.equals("16")) //lexicalized
				semanticPredicate.setLexicalized(false);
			else
				if(semTypeID.equals("52"))// perspectivalized
					semanticPredicate.setPerspectivalized(false);
				else{
					// semTypeIDs 68 and 182 need to be processed manually because of a bug in FN-API
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
						for(SemanticType temp : semanticType.getSuperTypes()){
							rootSemanticType = temp;
						}
						
						// if the root is still == null, semanticType has no parents
						if(rootSemanticType == null)
							rootSemanticType = semanticType;
						
						// if the root of semanticType != "Lexical_type", then we have an ontological type
						if(!rootSemanticType.getName().equals("Lexical_type")){
							// Creating SemanticLabels for FN-"Ontological types"
							SemanticLabel semanticLabel = new SemanticLabel();
							semanticLabel.setLabel(semanticType.getName());
							semanticLabel.setType(ELabelTypeSemantics.semanticCategory);
							
							// creating MonolingualExternalRef
							List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
							MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
							monolingualExternalRef.setExternalReference(semTypeID);
							monolingualExternalRef.setExternalSystem("FrameNet 1.5 semantic type ID");
							monolingualExternalRefs.add(monolingualExternalRef);
							semanticLabel.setMonolingualExternalRefs(monolingualExternalRefs);
							semanticLabels.add(semanticLabel);
						}
				}
		}
		semanticPredicate.setSemanticLabels(semanticLabels);
		
		// generate semanticArguments
		semanticPredicate.setSemanticArguments(generateSemanticArguments(frame));
		
		return semanticPredicate;
	}
	
	/**
	 * This method consumes a {@link Frame} and returns a list of SemanticArguments
	 * associated to the consumed Frame's FrameElements
	 * @param frame a Frame for which associated SemanticArguments should be generated
	 * @return a list of SemanticArguments associated with the consumed frame's FrameElements
	 * @see {@link SemanticArgument}
	 * @see {@link FrameElement}
	 */
	private List<SemanticArgument> generateSemanticArguments(Frame frame) {
		List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
		// Creating SemanticArgument
		for (FrameElement fe : frame.frameElements()) {
			// feName = frameName.feName
			String feName = frame.getName().concat(".").concat(fe.getName());
			
			
			SemanticArgument semanticArgument = new SemanticArgument();

			// Setting id
			StringBuffer semArgID = new StringBuffer(32);
			semArgID.append("FN_SemanticArgument_").append(semanticArgumentNumber++);
			semanticArgument.setId(semArgID.toString());

			// Setting semanticRole
			semanticArgument.setSemanticRole(fe.getName());

			// Setting definition
			List<Definition> definitions = new ArrayList<Definition>();
			Definition definition = new Definition();
			List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();
			TextRepresentation textRepresentation = new TextRepresentation();
			textRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
			textRepresentation.setWrittenText(FNUtils.filterTags(fe.getDefinition()));
			textRepresentations.add(textRepresentation);
			definition.setTextRepresentations(textRepresentations);
			definitions.add(definition);
			semanticArgument.setDefinitions(definitions);

			// setting coreType
			ECoreType coreType = FNUtils.getCoreType(fe.getCoreType());
			if(coreType == null){
				StringBuffer sb = new StringBuffer(256);
				sb.append("SemanticPredicate generator could not find ECoreType for FrameElement: ");
				sb.append(fe);
				sb.append('\n');
				sb.append("Aborting all operations!");
				logger.log(Level.SEVERE, sb.toString());
				System.exit(1);
			}
			semanticArgument.setCoreType(coreType);
			
			// not incorporated
			semanticArgument.setIncorporated(false);

			List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();

			for (SemanticType semanticType : fe.getSemanticTypes()) {
				// Checking if the root of this semanticType != "Lexical_type"
				SemanticType rootSemanticType = null;
				for (SemanticType temp : semanticType.getSuperTypes()) {
					rootSemanticType = temp;
				}

				// if the root is still == null, semanticType has no parents
				if (rootSemanticType == null)
					rootSemanticType = semanticType;

				// if the root of semanticType != "Lexical_type", then we have
				// an ontological type
				if (!rootSemanticType.getName().equals("Lexical_type")) {
					// Creating SemanticLabels for FN-"Ontological types"
					SemanticLabel semanticLabel = new SemanticLabel();
					semanticLabel.setLabel(semanticType.getName());
					semanticLabel.setType(ELabelTypeSemantics.selectionalPreference);

					// creating MonolingualExternalRef
					List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
					MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
					monolingualExternalRef.setExternalReference(semanticType.getId());
					monolingualExternalRef.setExternalSystem("FrameNet 1.5 semantic type ID");
					monolingualExternalRefs.add(monolingualExternalRef);
					semanticLabel.setMonolingualExternalRefs(monolingualExternalRefs);
					semanticLabels.add(semanticLabel);
				}
			}
			semanticArgument.setSemanticLabels(semanticLabels);
			semanticArguments.add(semanticArgument);
			
			// record the Mapping
			feSemArgMapping.put(feName, semanticArgument);
		}
		return semanticArguments;
	}
	
	/**
	 * This method adds ArgumentRelations to all previously generated SemanticArguments
	 * @see {@link ArgumentRelation}
	 * @see {@link SemanticArgument}
	 */
	private void updateArgumentRelations(){
		for(String feName :feSemArgMapping.keySet()){
			FrameElement fe=null;
			try {
				fe = fn.getFrameElement(feName);
			} catch (ParsingException e1) {
				
				e1.printStackTrace();
			} catch (FrameElementNotFoundException e1) {
				
				e1.printStackTrace();
			} catch (FrameNotFoundException e1) {

				e1.printStackTrace();
			}
			
			SemanticArgument semArg = feSemArgMapping.get(feName);
			List<ArgumentRelation> relationsFrom = semArg.getArgumentRelations();
			if(relationsFrom == null)
				relationsFrom = new LinkedList<ArgumentRelation>();
			// setting relations FROM semArg
			semArg.setArgumentRelations(relationsFrom);
			//###### MAPPING REQUIRED AND EXCLUDED RELATION
			Set<String> excludedFENames = fe.getExcludedFEs();
			for(String excludedFEName : excludedFENames){
				try {
					FrameElement excludedFE = fn.getFrameElement(excludedFEName);
					SemanticArgument excluded = feSemArgMapping.get(excludedFE.getFrame().getName().concat(".").concat(excludedFE.getName()));
					
					// Creating ArgumentRelation from semArg
					ArgumentRelation argumentRelation = new ArgumentRelation();
					argumentRelation.setTarget(excluded);
					argumentRelation.setRelType("fn_same_frame");
					argumentRelation.setRelName("excludes");
					relationsFrom.add(argumentRelation);
					
					// Creating ArgumentRelation in opposite direction
					ArgumentRelation opposite = new ArgumentRelation();
					opposite.setTarget(semArg);
					opposite.setRelType("fn_same_frame");
					opposite.setRelName("excludes");
					List<ArgumentRelation> fromExcluded = excluded.getArgumentRelations();
					if(fromExcluded == null)
						fromExcluded = new LinkedList<ArgumentRelation>();
					fromExcluded.add(opposite);
				} catch (ParsingException e) {
					e.printStackTrace();
				} catch (FrameElementNotFoundException e) {
					e.printStackTrace();
				} catch (FrameNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			//###### MAPPING REQUIRED RELATION
			Set<String> requiredFENames = fe.getRequiredFEs();
			for(String requiredFEName : requiredFENames){
				FrameElement requiredFE = null;
				try {
					requiredFE = fn.getFrameElement(requiredFEName);
				} catch (ParsingException e) {
					e.printStackTrace();
				} catch (FrameElementNotFoundException e) {
					e.printStackTrace();
				} catch (FrameNotFoundException e) {
					e.printStackTrace();
				}
				SemanticArgument required = feSemArgMapping.get(requiredFE.getFrame().getName().concat(".").concat(requiredFE.getName()));
				
				// Creating ArgumentRelation from semArg
				ArgumentRelation argumentRelation = new ArgumentRelation();
				argumentRelation.setTarget(required);
				argumentRelation.setRelType("fn_same_frame");
				argumentRelation.setRelName("requires");
				relationsFrom.add(argumentRelation);
			}
		}
		//###### MAPPING CORE_SET RELATION
		// it is easier to do this by iterating over frames :)
		for(Frame frame : fn.getFrames()){
			Map<Integer, FrameElement[]> coreSets = frame.getFeCoreSets();
			for(int setNumber : coreSets.keySet()){
				String label = "core_set_".concat(Integer.toString(setNumber));
				FrameElement[] coreSet = coreSets.get(setNumber);
				for(FrameElement frameElement : coreSet){
					SemanticArgument source = feSemArgMapping.get(frameElement.getFrame().getName()+"."+frameElement.getName());
					if(source == null){
						semanticArgumentNotFound(frameElement);
					}
					
					List<ArgumentRelation> argumentRelations = source.getArgumentRelations();
					if(argumentRelations == null)
						argumentRelations = new ArrayList<ArgumentRelation>();
					for(FrameElement target : coreSet){
						if(frameElement.equals(target))
							continue;
						ArgumentRelation argumentRelation = new ArgumentRelation();
						SemanticArgument targetSemanticArgument = feSemArgMapping.get(target.getFrame().getName()+"."+target.getName());
						if(targetSemanticArgument == null){
							semanticArgumentNotFound(target);
						}
						argumentRelation.setTarget(targetSemanticArgument);
						argumentRelation.setRelType("fn_core_set");
						argumentRelation.setRelName(label);
						argumentRelations.add(argumentRelation);
					}
					source.setArgumentRelations(argumentRelations);
				}
			}
		}
	}
	
	/**
	 * This method adds PredicateRelations to all previously generated SemanticPredicates
	 * @see {@link PredicateRelation}
	 * @see {@link SemanticPredicate}
	 */
	private void updatePredicateRelations(){
		for(Frame frame : fn.getFrames()){
			SemanticPredicate semanticPredicate = frameSemanticPredicateMappings.get(frame);
			List<PredicateRelation> relations = semanticPredicate.getPredicateRelations();
			if(relations == null)
				relations = new ArrayList<PredicateRelation>();
			for(String relationName : frameRelations){
				relations.addAll(getPredicateRelations(frame, relationName));
			}
			semanticPredicate.setPredicateRelations(relations);
		}
	}

	/**
	 * Returns all SemanticPredicates generated by this instance of {@link SemanticPredicateGenerator}
	 * @return SemanticPredicates generated by this SemanticPredicateGenerator
	 * @see {@link SemanticPredicate}
	 */
	public Collection<SemanticPredicate> getSemanticPredicates() {
		return frameSemanticPredicateMappings.values();
	}
	
	/**
	 * Returns {@link SemanticPredicate}, generated by this {@link SemanticPredicateGenerator}, associated with the consumed {@link Frame}
	 * @param frame a Frame for which generated SemanticPredicate should be returned 
	 * @return SemanticPredicate associated with the consumed frame
	 */
	public SemanticPredicate getSemanticPredicate(Frame frame){
		return frameSemanticPredicateMappings.get(frame);
	}
	
	/**
	 * Returns {@link SemanticArgument}, generated by this {@link SemanticPredicateGenerator}, associated with the consumed {@link FrameElement}
	 * @param frameElement a FrameElement for which generated SemanticArgument should be returned 
	 * @return SemanticArgument associated with the consumed frameElement
	 */
	public SemanticArgument getSemanticArgument(FrameElement frameElement){
		return feSemArgMapping.get(frameElement.getFrame().getName()+"."+frameElement.getName());
	}
	
	/**
	 * This method initializes the names of frame-relations, as described in FrameNet's files
	 */
	private void initializeFrameRelations(){
		frameRelations = new HashSet<String>();
		frameRelations.add("Inheritance");
		frameRelations.add("Subframe");
		frameRelations.add("Using");
		frameRelations.add("See_also");
		frameRelations.add("ReFraming_Mapping");
		frameRelations.add("Inchoative_of");
		frameRelations.add("Causative_of");
		frameRelations.add("Precedes");
		frameRelations.add("Perspective_on");
	}
	
	/**
	 * This method returns a list of PredicateRelations for the given {@link Frame} and  the name of the frame relation
	 * @param frame the Frame from which PredicateRelations should be generated
	 * @param relationName the name of the relation that should be taken into account when generating PredicateRelations
	 * @return list of PredicateRelations generated from consumed frame and relationName
	 * @see {@link PredicateRelation}
	 */
	private List<PredicateRelation> getPredicateRelations(Frame frame, String relationName){
		
		List<PredicateRelation> result = new ArrayList<PredicateRelation>();
		
		/*
		 * Used for determining the relevantSemanticPredicate by "precedes"
		 * relation
		 */
		Collection<Frame> subframeOf = frame.subframeOf();
		Frame superFrame = null;
		for(Frame fr : subframeOf)
			if(superFrame == null)
				superFrame = fr;
		
		Collection<Frame> parents = frame.relatedFrames(relationName, FrameNetRelationDirection.UP);
		String relType = "fn_frame_relation";
		String relNameUp = null;
		if(parents != null && !parents.isEmpty()){
			relNameUp = FNUtils.getRelName(relationName, FrameNetRelationDirection.UP);
			for(Frame parent : parents ){
				PredicateRelation predicateRelation = new PredicateRelation();
				predicateRelation.setRelType(relType);
				predicateRelation.setRelName(relNameUp);
				SemanticPredicate target = frameSemanticPredicateMappings.get(parent);
				
				// relevantSemanticPredicate for "precedes"
				if((relNameUp.equals("is_preceded_by") || relNameUp.equals("precedes")) && !subframeOf.isEmpty()){
					predicateRelation.setRelevantSemanticPredicate(frameSemanticPredicateMappings.get(superFrame));
				}
				predicateRelation.setTarget(target);
				result.add(predicateRelation);
			}
		}
		
		Collection<Frame> children = frame.relatedFrames(relationName, FrameNetRelationDirection.DOWN);
		String relNameDown = null;
		if(children != null && !children.isEmpty()){
			relNameDown = FNUtils.getRelName(relationName, FrameNetRelationDirection.DOWN);
			for(Frame child : children){
				PredicateRelation predicateRelation = new PredicateRelation();
				predicateRelation.setRelType(relType);
				predicateRelation.setRelName(relNameDown);
				SemanticPredicate target = frameSemanticPredicateMappings.get(child);
				if(target == null){
					StringBuffer sb = new StringBuffer(128);
					sb.append("SemanticPredicateGenerator could not find SemanticPredicate for Frame: ");
					sb.append(child);
					sb.append('\n');
					sb.append("Aborting all operations!");
					logger.log(Level.SEVERE, sb.toString());
					System.exit(1);
				}
				// relevantSemanticPredicate for "precedes"
				if((relNameDown.equals("is_preceded_by") || relNameDown.equals("precedes")) && !subframeOf.isEmpty()){
					predicateRelation.setRelevantSemanticPredicate(frameSemanticPredicateMappings.get(superFrame));
				}
				predicateRelation.setTarget(frameSemanticPredicateMappings.get(child));
				result.add(predicateRelation);
			}
		}
		return result;
	}

	/**
	 * This method consumes the name of the incorporated frame element
	 * and creates the corresponding SemanticArgument
	 * @param incorporatedFEName
	 * @return corresponding SemanticArgument
	 */
	SemanticArgument createIncorporatedSemanticArgument(String incorporatedFEName) {
		SemanticArgument semanticArgument = new SemanticArgument();
		// Setting id
		StringBuffer semArgID = new StringBuffer(32);
		semArgID.append("FN_SemanticArgument_").append(semanticArgumentNumber++);
		semanticArgument.setId(semArgID.toString());
		semanticArgument.setSemanticRole(incorporatedFEName);
		semanticArgument.setIncorporated(true);
//		incorporatedSemArgs.add(semanticArgument);
		return semanticArgument;
	}
	
	/**
	 * This method is called when a {@link SemanticArgument}, that corresponds to the consumed {@link FrameElement}
	 * could not be found. <br>
	 * It terminates the execution of {@link SemanticPredicateGenerator}
	 * @param fe FrameElement for which a SemanticArgument could not be found
	 */
	private void semanticArgumentNotFound(FrameElement fe){
		StringBuffer sb = new StringBuffer(256);
		sb.append("SemanticPredicateGenerator could not find SemanticArgument for FrameElement: ");
		sb.append(fe).append('\n');
		sb.append("Aborting all operations!");
		logger.log(Level.SEVERE, sb.toString());
		System.exit(1);
	}
	
}
