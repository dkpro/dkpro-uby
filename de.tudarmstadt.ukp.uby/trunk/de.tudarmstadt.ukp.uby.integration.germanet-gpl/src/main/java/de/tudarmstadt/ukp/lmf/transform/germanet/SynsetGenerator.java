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
package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;

/**
 * Instance of this class offers methods for creating {@link Synset} instances out of GermaNet's data
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 * 
 */
public class SynsetGenerator {
	private GermaNet gnet; // GermaNet Object
	private int lmfSynsetNumber = 0; // running number used for creating IDs of LMFSynsets
	private int senseNumber = 0; // running number used for creating IDs of Senses
	private Map<Synset, Sense> gnSynsetSenseMappings = new HashMap<Synset, Sense>(); // The mappings between Synset provided by GN and Senses
	private Map<LexUnit, de.tudarmstadt.ukp.lmf.model.semantics.Synset> LexUnitSynsetMappings = new HashMap<LexUnit, de.tudarmstadt.ukp.lmf.model.semantics.Synset>();
	private Map<LexUnit, Sense> luSenseMappings = new HashMap<LexUnit, Sense>();
	// Mappings between LMF-Synsets and Senses 
	private static Map<de.tudarmstadt.ukp.lmf.model.semantics.Synset, List<Sense>> 
		synsetSenseMappings = new HashMap<de.tudarmstadt.ukp.lmf.model.semantics.Synset, List<Sense>>();
	// Mappings between LMF-Synsets and GN-Synsets
	private static Map<de.tudarmstadt.ukp.lmf.model.semantics.Synset, Synset>
		lmfSynsetGNSynsetMappings = new HashMap<de.tudarmstadt.ukp.lmf.model.semantics.Synset, Synset>();
	// Mappings between GN-Synsets and LMF-Synsets
	private static Map<Synset, de.tudarmstadt.ukp.lmf.model.semantics.Synset>
		gnSynsetLMFSynsetMappings = new HashMap<Synset, de.tudarmstadt.ukp.lmf.model.semantics.Synset>();
	
	private static final String has_component_holonym ="has_component_holonym";
	private static final String has_component_meronym = "has_component_meronym";
	private static final String has_member_holonym = "has_member_holonym";
	private static final String has_member_meronym = "has_member_meronym";
	private static final String causes = "causes";
	private static final String has_portion_holonym = "has_portion_holonym";
	private static final String has_portion_meronym = "has_portion_meronym";
	private static final String has_substance_holonym = "has_substance_holonym";
	private static final String has_substance_meronym = "has_substance_meronym";
	private static final String has_hypernym = "has_hypernym";
	private static final String is_entailed_by = "is_entailed_by";
	private static final String is_related_to = "is_related_to";
	private static final String has_hyponym = "has_hyponym";
	private static final String entails = "entails";
	
	/**
	 * Constructs a {@link SynsetGenerator} associated with the consumed {@link GermaNet} instance 
	 * @param gnet instance of GermaNet used for obtaining information from GermaNet's files
	 */
	public SynsetGenerator(GermaNet gnet){
		this.gnet = gnet;
	}
	
	
	/**
	 * This method initializes the {@link SynsetGenerator}
	 */
	public void initialize(){
		if(LexUnitSynsetMappings.isEmpty()){
		// List of all GermaNet Synsets
		List<Synset> gnSynsets = gnet.getSynsets();
		for(Synset gnSynset : gnSynsets){
			// Create a LMF-Synset for each gn-Synset
			de.tudarmstadt.ukp.lmf.model.semantics.Synset lmfSynset = new de.tudarmstadt.ukp.lmf.model.semantics.Synset();
			lmfSynset.setId(getNewID());
			
			// *** Generating Monolingual ExternalRef**//
			MonolingualExternalRef mer = new MonolingualExternalRef();
			mer.setExternalReference(Integer.toString(gnSynset.getId()));
			mer.setExternalSystem("GermaNet 7.0 Synset-ID");
			LinkedList<MonolingualExternalRef> mers = new LinkedList<MonolingualExternalRef>();
			mers.add(mer);
			lmfSynset.setMonolingualExternalRefs(mers);
			
			lmfSynsetGNSynsetMappings.put(lmfSynset, gnSynset);
			gnSynsetLMFSynsetMappings.put(gnSynset, lmfSynset);
				// Setting Definitions
				String writtenText = gnSynset.getParaphrase();
				if (writtenText != null && !writtenText.equals("") && !writtenText.equals(" ")) {
					// Definition for a Synset can be created only if the Synset has a paraphrase (gloss)
					List<Definition> definitions = new LinkedList<Definition>();
					Definition definition = new Definition();
					List<TextRepresentation> textRepresentations = new LinkedList<TextRepresentation>();
					TextRepresentation textRepresentation = new TextRepresentation();
					textRepresentation.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
					textRepresentation.setWrittenText(writtenText);
					textRepresentations.add(textRepresentation);
					definition.setTextRepresentations(textRepresentations);
					definitions.add(definition);
					lmfSynset.setDefinitions(definitions);
				}
			
			for(LexUnit lu : gnSynset.getLexUnits()){
				// Generating a Sense for each LU
				Sense sense = new Sense();
				sense.setId(getNewSenseID());
				LexUnitSynsetMappings.put(lu, lmfSynset);
				luSenseMappings.put(lu, sense);
				gnSynsetSenseMappings.put(gnSynset, sense);
				// Adding LMF-Synset_Sense Mappings
				if(!synsetSenseMappings.containsKey(lmfSynset)){
					LinkedList<Sense> temp = new LinkedList<Sense>();
					synsetSenseMappings.put(lmfSynset, temp);
				}
				synsetSenseMappings.get(lmfSynset).add(sense);
				}
			}
		finalizeGeneration();
		}
		}

	
	/**
	 * This method consumes an instance of {@link LexUnit} and returns it's corresponing instance of
	 * {@link de.tudarmstadt.ukp.lmf.model.semantics.Synset}. <br>
	 * This method should be evoked after the initialization
	 * of this SynsetGenerator!
	 * @param lu LexUnit for which generated synset should be returned
	 * @return synset that corresponds to the consumed lu
	 */
	public de.tudarmstadt.ukp.lmf.model.semantics.Synset getLMFSynset(LexUnit lu){
		return LexUnitSynsetMappings.get(lu);
	}
	
		
		/**
		 * This method generates a Synset-ID
		 * @see de.tudarmstadt.ukp.lmf.model.semantics.Synset
		 */
		private String getNewID(){
			StringBuffer sb = new StringBuffer(64);
			sb.append("GN_Synset_").append(Integer.toString(lmfSynsetNumber));
			lmfSynsetNumber++;
			return sb.toString();
		}
		
		/**
		 * This method generates a Sense-ID
		 * @see de.tudarmstadt.ukp.lmf.model.semantics.Sense
		 */
		private String getNewSenseID(){
			StringBuffer sb = new StringBuffer(64);
			sb.append("GN_Sense_").append(Integer.toString(senseNumber));
			senseNumber++;
			return sb.toString();
		}
		
		/**
		 * This method returns a Sense for the consumed {@link LexUnit}
		 * @param lu LexicalUnit for which the corresponding instance of {@link Sense} class should be returned
		 * @return the lu's corresponding sense
		 */
		public Sense getSense(LexUnit lu){
			return luSenseMappings.get(lu);
		}
		
		/**
		 * This method returns an instance of {@link Sense} class associated with the consumed instance of
		 * {@link Synset} class 
		 * @param gnSynset synset for which associated sense should be returned
		 * @return sense associated with the consumed gnSynset
		 */
		public Sense getSense(Synset gnSynset){
			return gnSynsetSenseMappings.get(gnSynset);
		}
		
		/**
		 * This method returns a sorted list of all {@link de.tudarmstadt.ukp.lmf.model.semantics.Synset} instances generated by this generator.
		 * @return a sorted {@link List} of all synsets generated by this generator
		 */
		public List<de.tudarmstadt.ukp.lmf.model.semantics.Synset> getSynsets(){
			List<de.tudarmstadt.ukp.lmf.model.semantics.Synset> result = new LinkedList<de.tudarmstadt.ukp.lmf.model.semantics.Synset>();
			result.addAll(lmfSynsetGNSynsetMappings.keySet());
			Collections.sort(result);
			return result;
		}
		
		/**
		 * This method consumes an instance of {@link Synset} and generates and returns a {@link List} of synset relations that correspond to
		 * consumed conceptual relation.
		 * @param gnSynset synset for which corresponding list of synset relations should be generated
		 * @param conRelation conceptual relation equal to the type of the returned synset relations
		 * @return a list of synset relations based on the consumed arguments
		 * @see SynsetRelation
		 * @see ConRel
		 */
		private List<SynsetRelation> generateSynsetRelations(Synset gnSynset, ConRel conRelation){
			List<SynsetRelation> synsetRelations = new LinkedList<SynsetRelation>();
			// Generating Relation
			List<Synset> targets = gnSynset.getRelatedSynsets(conRelation);
			if(!targets.isEmpty()){
				// If Synset has a conRelation to other Synsets, a SynsetRelation should be generated
				ERelTypeSemantics relType = getRelType(conRelation);
				String relName = getRelName(conRelation);
				for(Synset target : targets){
					// Generate a SynsetRelation
					SynsetRelation synsetRelation = new SynsetRelation();
					synsetRelation.setTarget(gnSynsetLMFSynsetMappings.get(target));
					synsetRelation.setRelType(relType);
					synsetRelation.setRelName(relName);
					synsetRelations.add(synsetRelation);
				}
			}
			return synsetRelations;
		}
		
		/**
		 * This method consumes a conceptual relation and returns the 
		 * corresponding Uby-LMF relation type
		 * @param conRel for which Uby-LMF relation type should be returned
		 * @return conRel relation type in Uby-LMF or null if no entry for conRel exists
		 * @see ConRel
		 * @see ERelTypeSemantics
		 */
		private ERelTypeSemantics getRelType(ConRel conRel){
			ERelTypeSemantics relType;
			switch (conRel){
			case causes : relType = ERelTypeSemantics.taxonomic; break;
			case entails : relType = ERelTypeSemantics.taxonomic; break;
			case has_component_holonym : relType = ERelTypeSemantics.partWhole; break;
			case has_component_meronym : relType = ERelTypeSemantics.partWhole; break;
			case has_hypernym : relType = ERelTypeSemantics.taxonomic ; break;
			case has_hyponym : relType = ERelTypeSemantics.taxonomic; break;
			case has_member_holonym : relType = ERelTypeSemantics.partWhole; break;
			case has_member_meronym : relType = ERelTypeSemantics.partWhole; break;
			case has_portion_holonym : relType = ERelTypeSemantics.partWhole; break;
			case has_portion_meronym : relType = ERelTypeSemantics.partWhole; break;
			case has_substance_holonym : relType = ERelTypeSemantics.partWhole; break;
			case has_substance_meronym : relType = ERelTypeSemantics.partWhole; break;
			case is_entailed_by : relType = ERelTypeSemantics.taxonomic; break;
			case is_related_to : relType = ERelTypeSemantics.association; break;
			default : relType = null; 
			}
			return relType;
		}
		
		
		/**
		 * This method consumes a conceptual relation and returns the 
		 * corresponding Uby-LMF relation name
		 * @param conRel for which Uby-LMF relation name should be returned
		 * @return conRel's relation name in Uby-LMF or null if no entry for conRel exists
		 * @see ConRel
		 * @see ERelTypeSemantics
		 */
		private String getRelName(ConRel conRel){
			String relName;
			switch (conRel){
			case causes : relName = causes; break;
			case entails : relName = entails; break;
			case has_component_holonym : relName = has_component_holonym; break;
			case has_component_meronym : relName = has_component_meronym; break;
			case has_hypernym : relName = has_hypernym ; break;
			case has_hyponym : relName = has_hyponym; break;
			case has_member_holonym : relName = has_member_holonym; break;
			case has_member_meronym : relName = has_member_meronym; break;
			case has_portion_holonym : relName = has_portion_holonym; break;
			case has_portion_meronym : relName = has_portion_meronym; break;
			case has_substance_holonym : relName = has_substance_holonym; break;
			case has_substance_meronym : relName = has_substance_meronym; break;
			case is_entailed_by : relName = is_entailed_by; break;
			case is_related_to : relName = is_related_to; break;
			default : relName = null; 
			}
			return relName;
		}
		
		/**
		 * This method consumes an instance of {@link Synset} and appends all synset relations to
		 * it's corresponding instance of {@link de.tudarmstadt.ukp.lmf.model.semantics.Synset}
		 * @param synset GermaNet's synset on which associated Uby-LMF synset, should be appended
		 * @see SynsetRelation 
		 */
		private void setSynsetRelations(Synset gnSynset){
			List<SynsetRelation> synsetRelations = new LinkedList<SynsetRelation>();
			for(ConRel conRel : ConRel.values()){
				synsetRelations.addAll(generateSynsetRelations(gnSynset,conRel));
			}
			de.tudarmstadt.ukp.lmf.model.semantics.Synset lmfSynset = gnSynsetLMFSynsetMappings.get(gnSynset);
			if(!synsetRelations.isEmpty())
				lmfSynset.setSynsetRelations(synsetRelations);
		}
		
		/**
		 * This method finalizes the generation of the {@link de.tudarmstadt.ukp.lmf.model.semantics.Synset} instances <br>
		 * produced by this generator, by appending synset relations to them. <br>
		 * It should be evoked after all {@link Sense} instances are initialized.
		 * @see SynsetRelation
		 */
		private void finalizeGeneration(){
			// append all SynsetRelations
			for(Synset gnSynset : gnSynsetLMFSynsetMappings.keySet()){
				this.setSynsetRelations(gnSynset);
			}
		}
	
}
