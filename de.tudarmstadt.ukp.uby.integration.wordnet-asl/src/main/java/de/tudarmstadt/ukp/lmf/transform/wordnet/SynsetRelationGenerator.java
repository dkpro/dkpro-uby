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
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerTarget;
import net.sf.extjwnl.data.Word;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;

/**
 * This class is used for extraction of synset-relations, defined in Uby-LMF, out of WordNet's data.
 * @author Zijad Maksuti, Judith Eckle-Kohler
 * @see Synset
 * @see SynsetRelation
 *
 */
public class SynsetRelationGenerator {
	
	private SynsetGenerator synsetGenerator;
	private LexicalEntryGenerator lexicalEntryGenerator;
	
	/*
	 * Mappings between WordNet's pointer-types and corresponding relation names, defined in Uby-LMF 
	 * The mapping is used for SynsetRelations only
	 * Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private Map<String, String[]> pointerTypeRelNameMappings;
	
	/*
	 * Mappings between WordNet's pointer-types and corresponding relation types, defined in Uby-LMF 
	 * The mapping is used for SynsetRelations only
	 * Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private Map<String, ERelTypeSemantics[]> pointerTypeRelTypeMappings;
	
	private Set<String> ignoredPointerKeys; // A set of ignored pointer keys, used for error-detection purposes
	
	private Map<String, String> domainOfRegisterMappings = new HashMap<String,String>(); // <domainOfRelationKey, register>
	
	// String representations of relation names
	private static final String hypernym = "hypernym";
	private static final String hypernymInstance = "hypernymInstance";
	private static final String hyponym = "hyponym";
	private static final String hyponymInstance = "hyponymInstance";
	private static final String holonymMember = "holonymMember";
	private static final String holonymSubstance = "holonymSubstance";
	private static final String holonymPart = "holonymPart";
	private static final String meronymMember = "meronymMember";
	private static final String meronymPart = "meronymPart";
	private static final String meronymSubstance = "meronymSubstance";
	private static final String attribute = "attribute";
	private static final String topic = "topic";
	private static final String isTopicOf = "isTopicOf";
	private static final String region = "region";
	private static final String isRegionOf = "isRegionOf";
	private static final String usage = "usage";
	private static final String isUsageOf = "isUsageOf";
	private static final String entails = "entails";
	private static final String causation = "causation";
	private static final String seeAlso = "seeAlso";
	private static final String verbGroup = "verbGroup";
	private static final String nearSynonym = "nearSynonym";
	
	/**
	 * Constructs an instance of {@link SynsetRelationGenerator} based on the consumed parameters.
	 * @param synsetGenerator an instance of {@link SynsetGenerator} used for accessing generated Uby-LMF synsets.
	 * @param lexicalEntryGenerator an instance of {@link LexicalEntryGenerator} used for
	 * accessing generated {@link LexicalEntry}-instances.<br>
	 * Both synsetGenerator and lexicalEntryGenerator must be initialized.
	 * @see Synset 
	 */
	public SynsetRelationGenerator(SynsetGenerator synsetGenerator, LexicalEntryGenerator lexicalEntryGenerator){
		this.synsetGenerator = synsetGenerator;
		this.lexicalEntryGenerator = lexicalEntryGenerator;
		if(pointerTypeRelNameMappings == null)
			initializePointerMappings();
	}
	
	/**
	 * This method iterates over all synsets, provided by synset-generator
	 * and updates their {@link SynsetRelations}
	 * @see Synset 
	 */
	public void updateSynsetRelations() {
		// Iterate over all Synset-Bindings and 
		for(Entry<net.sf.extjwnl.data.Synset, Synset> binding : synsetGenerator.getWNSynsetLMFSynsetMappings().entrySet())
			updateSynsetRelations(binding);
	}

	/**
	 * This method consumes a binding of a WordNet's synset and it's associated Uby-LMF-synset.
	 * It updates the {@link SynsetRelations} of the Uby-LMF-synset.
	 * @param binding the binding of two synset, with WordNet's synset as key
	 * @see Synset
	 * @see net.sf.extjwnl.data.Synset
	 */
	private void updateSynsetRelations(Entry<net.sf.extjwnl.data.Synset, Synset> binding){
		// Create SynsetRelation for the binding
		List<SynsetRelation> synsetRelations = new LinkedList<SynsetRelation>();
		
		net.sf.extjwnl.data.Synset synset = binding.getKey();
		List<Pointer> pointers = synset.getPointers();
		int posOrdinal = POS.getAllPOS().indexOf(synset.getPOS()); // ordinal of synset's POS
		
		
		// Iterate over all pointers of the WNSynset and generate the corresponding SynsetRelation
		for(Pointer pointer : pointers)
			if(!ignoredPointerKeys.contains(pointer.getType().getKey()))
			synsetRelations.add(generateSynsetRelation(pointer, posOrdinal));
		binding.getValue().setSynsetRelations(synsetRelations);
	}

	/**
	 * This method consumes a pointer of a WordNet's synset and generates the corresponding {@link SynsetRelation}-instance
	 * @param pointer a {@link Pointer}-instance 
	 * @param posOrdinal the ordinal of the pointer's source part of speech
	 * @return synset-relation that corresponds to the consumed pointer
	 * @see Synset
	 * @see net.sf.extjwnl.data.Synset
	 * @see POS
	 */
	private SynsetRelation generateSynsetRelation(Pointer pointer, int posOrdinal){
		
		// Create a SynsetRelation for the pointer
		SynsetRelation synsetRelation = new SynsetRelation();
		
		// Setting relationType
		String pointerSymbol = pointer.getType().getKey();
		ERelTypeSemantics relType = getRelType(pointerSymbol, posOrdinal);

		synsetRelation.setRelType(relType);
		
		
		// Setting relationName 
		String relationName = getRelationName(pointerSymbol, posOrdinal);

		synsetRelation.setRelName(relationName);
		
		// Setting the target
		PointerTarget pointerTarget = pointer.getTarget();
		if(pointerTarget instanceof net.sf.extjwnl.data.Synset){
			// the target is a Synset
			synsetRelation.setTarget(synsetGenerator.getLMFSynset((net.sf.extjwnl.data.Synset)pointerTarget));
			
			/*
			 * Updating SubjectField-class
			 * this block will only be executed for DOMAIN-OF pointers
			 */
			if(domainOfRegisterMappings.keySet().contains(pointerSymbol)){
				// SenseGenerator is needed in order to obtain the Lexeme's corresponding Sense
				SenseGenerator senseGenerator = lexicalEntryGenerator.getSenseGenerator();
				
				net.sf.extjwnl.data.Synset targetSynset = (net.sf.extjwnl.data.Synset) pointer.getTarget();
				
				// iterate over every lexeme of the source synset
				for(Word lexeme : ((net.sf.extjwnl.data.Synset)pointer.getSource()).getWords()){
					// Obtain lexeme's Sense
					Sense sense = senseGenerator.getSense(lexeme);
					// obtain semantic labels
					List<SemanticLabel> semanticLabels = sense.getSemanticLabels();
					if(semanticLabels == null)
						semanticLabels = new LinkedList<SemanticLabel>();
					
					// create a new SemanticLabel and add it to the list
					SemanticLabel semanticLabel = createSemanticLabel(targetSynset, pointerSymbol);
					semanticLabels.add(semanticLabel);
					// set the subjectField
					sense.setSemanticLabels(semanticLabels);
				}	
			}
		}
		
		return synsetRelation;
	}

	/**
	 * This method consumes targeted synset of a WordNet's "Domain-Of" relation and generates the
	 * corresponding instance of {@link SemanticLabel}-class 
	 * @param targetSynset synset targeted by a "Domain-Of" relation
	 * @param key WordNet's symbol describing the relation 
	 * @return instance of SemanticLabel class associated with the consumed parameters
	 */
	private SemanticLabel createSemanticLabel(net.sf.extjwnl.data.Synset targetSynset, String key) {
		
		SemanticLabel semanticLabel = new SemanticLabel();
		semanticLabel.setLabel(getSemanticLabel(targetSynset));
		semanticLabel.setType(domainOfRegisterMappings.get(key));
		
		return semanticLabel;
	}

	/**
	 * This method consumes a targeted WordNet-synset, and returns the semantic label.<br>
	 * Semantic label is the lemma of the synset's first lexeme. 
	 * @param targetSynset WordNet's synset from which the semantic label should be extracted
	 * @return the the lemma of the targetSynset's first lexeme
	 */
	private String getSemanticLabel(net.sf.extjwnl.data.Synset targetSynset) {
		return targetSynset.getWords().get(0).getLemma();
	}

	/**
	 * This method consumes a WN-PointerSymbol and returns the corresponding SynsetRelation-relType 
	 * @param pointerSymbol the Pointer's symbol
	 * @param posOrdinal the ordinal of synset's POS
	 * @return corresponding relation type
	 * @see SynsetRelation
	 * @see Pointer
	 * @see ERelTypeSemantics
	 */
	private ERelTypeSemantics getRelType(String pointerSymbol, int posOrdinal) {
		// The relType also depends on the POS of the pointer's Synset
		return pointerTypeRelTypeMappings.get(pointerSymbol)[posOrdinal];
	}

	/**
	 * This method consumes a WN-PointerSymbol and returns the corresponding SynsetRelation-relName 
	 * @param pointerSymbol the Pointer's symbol
	 * @param posOrdinal the ordinal of synset's POS
	 * @return corresponding relation name
	 * @see SynsetRelation
	 * @see Pointer
	 * @see ERelTypeSemantics
	 */
	private String getRelationName(String pointerSymbol, int posOrdinal) {
		return pointerTypeRelNameMappings.get(pointerSymbol)[posOrdinal];
	}

	/**
	 * This method initializes the pointerMappings
	 */
	private void initializePointerMappings() {
		// The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
		pointerTypeRelNameMappings = new HashMap<String, String[]>();
		pointerTypeRelTypeMappings = new HashMap<String, ERelTypeSemantics[]>();
		
		// Adding mappings
			
		// hypernym
		pointerTypeRelNameMappings.put("@", new String[]
		  {hypernym, hypernym, null, null});
		
		pointerTypeRelTypeMappings.put("@", new ERelTypeSemantics[]
		  {ERelTypeSemantics.taxonomic, ERelTypeSemantics.taxonomic, null, null});
		
		// hypernymInstance
		pointerTypeRelNameMappings.put("@i", new String[]
          {hypernymInstance, null, null, null});
		
		pointerTypeRelTypeMappings.put("@i", new ERelTypeSemantics[]
          {ERelTypeSemantics.taxonomic, null, null, null});
		
		// hyponym
		pointerTypeRelNameMappings.put("~", new String[]
		  {hyponym, hyponym, null, null});
		
		pointerTypeRelTypeMappings.put("~", new ERelTypeSemantics[]
          {ERelTypeSemantics.taxonomic, ERelTypeSemantics.taxonomic, null, null});
		
		// hyponymInstance
		pointerTypeRelNameMappings.put("~i", new String[]
		  {hyponymInstance, null, null, null});
		
		pointerTypeRelTypeMappings.put("~i", new ERelTypeSemantics[]
          {ERelTypeSemantics.taxonomic, null, null, null});
		
		// holonymMember
		pointerTypeRelNameMappings.put("#m", new String[]
		  {holonymMember, null, null, null});
		
		pointerTypeRelTypeMappings.put("#m", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// holonymSubstance
		pointerTypeRelNameMappings.put("#s", new String[]
		  {holonymSubstance, null, null, null});
		
		pointerTypeRelTypeMappings.put("#s", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// holonymPart
		pointerTypeRelNameMappings.put("#p", new String[]
		  {holonymPart, null, null, null});
		
		pointerTypeRelTypeMappings.put("#p", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// meronymMember
		pointerTypeRelNameMappings.put("%m", new String[]
		  {meronymMember, null, null, null});
		
		pointerTypeRelTypeMappings.put("%m", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// meronymSubstance
		pointerTypeRelNameMappings.put("%s", new String[]
		  {meronymSubstance, null, null, null});
		
		pointerTypeRelTypeMappings.put("%s", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// meronymPart
		pointerTypeRelNameMappings.put("%p", new String[]
		  {meronymPart, null, null, null});
		
		pointerTypeRelTypeMappings.put("%p", new ERelTypeSemantics[]
		  {ERelTypeSemantics.partWhole, null, null, null});
		
		// nounAdjPair nounAdjGroup
		pointerTypeRelNameMappings.put("=", new String[]
		  {attribute, null, attribute, null});

		pointerTypeRelTypeMappings.put("=", new ERelTypeSemantics[]
		  {ERelTypeSemantics.association, null, ERelTypeSemantics.association, null});
		
		// topic
		pointerTypeRelNameMappings.put(";c", new String[]
		  {topic, topic, topic, topic});
		
		pointerTypeRelTypeMappings.put(";c", new ERelTypeSemantics[]
          {ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label});
		
		// isTopicOf
		pointerTypeRelNameMappings.put("-c", new String[]
		  {isTopicOf, null, null, null});
		
		pointerTypeRelTypeMappings.put("-c", new ERelTypeSemantics[]
          {ERelTypeSemantics.predicative, null, null, null});
		
		// region
		pointerTypeRelNameMappings.put(";r", new String[]
		  {region, region, region, region});
		
		pointerTypeRelTypeMappings.put(";r", new ERelTypeSemantics[]
          {ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label});
		
		// isRegionOf
		pointerTypeRelNameMappings.put("-r", new String[]
		  {isRegionOf, null, null, null});
		
		pointerTypeRelTypeMappings.put("-r", new ERelTypeSemantics[]
          {ERelTypeSemantics.predicative, null, null, null});
		
		// usage
		pointerTypeRelNameMappings.put(";u", new String[]
		  {usage, usage, usage, usage});
		
		pointerTypeRelTypeMappings.put(";u", new ERelTypeSemantics[]
          {ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label, ERelTypeSemantics.label});
		
		// isUsageOf
		pointerTypeRelNameMappings.put("-u", new String[]
		  {isUsageOf, null, null, null});
		
		pointerTypeRelTypeMappings.put("-u", new ERelTypeSemantics[]
          {ERelTypeSemantics.predicative, null, null, null});
		
		// entails
		pointerTypeRelNameMappings.put("*", new String[]
		  {null, entails, null, null});
		
		pointerTypeRelTypeMappings.put("*", new ERelTypeSemantics[]
          {null, ERelTypeSemantics.taxonomic, null, null});
		
		// causation
		pointerTypeRelNameMappings.put(">", new String[]
		  {null, causation, null, null});
		
		pointerTypeRelTypeMappings.put(">", new ERelTypeSemantics[]
          {null, ERelTypeSemantics.taxonomic, null, null});
		
		// seeAlso
		pointerTypeRelNameMappings.put("^", new String[]
		  {null, seeAlso, seeAlso, null});
		
		pointerTypeRelTypeMappings.put("^", new ERelTypeSemantics[]
          {null, ERelTypeSemantics.association, ERelTypeSemantics.association, null});
		
		// verbGroup
		pointerTypeRelNameMappings.put("$", new String[]
		  {null, verbGroup, null, null});
		
		pointerTypeRelTypeMappings.put("$", new ERelTypeSemantics[]
          {null, ERelTypeSemantics.association, null, null});
		
		// nearSynonym
		pointerTypeRelNameMappings.put("&", new String[]
		  {null, null, nearSynonym, null});

		pointerTypeRelTypeMappings.put("&", new ERelTypeSemantics[]
          {null, null, ERelTypeSemantics.association, null});
		
		// Set the ignored keys
		ignoredPointerKeys = new HashSet<String>();
		ignoredPointerKeys.add("!");
		ignoredPointerKeys.add("+");
		ignoredPointerKeys.add("<");
		ignoredPointerKeys.add("\\");

		
		// Setting mappings related to domainOf Relations
		domainOfRegisterMappings.put(";c", "topic");
		domainOfRegisterMappings.put(";r", "region");
		domainOfRegisterMappings.put(";u", "usage");
	}
}
