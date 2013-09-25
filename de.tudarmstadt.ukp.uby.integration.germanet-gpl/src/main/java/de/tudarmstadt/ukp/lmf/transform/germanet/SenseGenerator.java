/**
 * Copyright 2012
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

package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexRel;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;

/**
 * This class offers methods for generating instances of {@link Sense} class from
 * <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class SenseGenerator {
	private final SynsetGenerator synsetGenerator; // SynsetGenerator
	private final SemanticClassLabelExtractor semanticClassLabelExtractor; // for extraction of semantic class labels
	private final Map<LexUnit, Sense> luSenseMappings = new HashMap<LexUnit, Sense>();
	private final SenseExampleGenerator senseExampleGenerator;

	/**
	 * Constructs a {@link SenseGenerator} for the consumed {@link GermaNet} instance
	 * @param gnet GermaNet instance used for obtaining GermaNet's information
	 */
	public SenseGenerator(GermaNet gnet){
		SynsetGenerator synsetGenerator = new SynsetGenerator(gnet);
		synsetGenerator.initialize();
		this.synsetGenerator= synsetGenerator;
		this.semanticClassLabelExtractor = new SemanticClassLabelExtractor(gnet);
		this.senseExampleGenerator = new SenseExampleGenerator();
		}

	/**
	 * This method consumes a group of LexUnits and creates list of {@link Sense}-instances
	 * for every {@link LexUnit} of the consumed group
	 * @param luGroup a group of LexUnits for which senses should be created
	 * @return a {@link List} of senses generated from every LexUnit in luGroup
	 */
	public List<Sense> generateSenses(Set<LexUnit> luGroup){
		List<Sense> senses = new ArrayList<Sense>();
		// Generate Sense for each LexUnit in the group
		for(LexUnit lu : luGroup){
			Sense sense = synsetGenerator.getSense(lu);
			luSenseMappings.put(lu, sense);
			// Creating Index
			sense.setIndex(lu.getSense());
			// Create synset-reference
			de.tudarmstadt.ukp.lmf.model.semantics.Synset lmfSynset = synsetGenerator.getLMFSynset(lu);
			sense.setSynset(lmfSynset);

			// set semanticLabels
			List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();
			SemanticLabel semanticLabel = new SemanticLabel();
			semanticLabels.add(semanticLabel);
			semanticLabel.setLabel(semanticClassLabelExtractor.getLUSemanticClassLabel(lu));
			semanticLabel.setType(ELabelTypeSemantics.semanticField);
			if(lu.isArtificial()){ // another semantic label for artificial LexUnits
				SemanticLabel semanticLabelArt = new SemanticLabel();
				semanticLabelArt.setLabel("artificialConcept");
				semanticLabelArt.setType(ELabelTypeSemantics.resourceSpecific);
				semanticLabels.add(semanticLabelArt);
			}
			sense.setSemanticLabels(semanticLabels);

			//*** Generating SenseExamples ***//
			List<SenseExample> senseExamples = senseExampleGenerator.generateSenseExamples(lu);
			sense.setSenseExamples(senseExamples);

			//*** Setting Definitions ***//
			sense.setDefinitions(lmfSynset.getDefinitions());

			// *** Generating Monolingual ExternalRef**//
			MonolingualExternalRef mer = new MonolingualExternalRef();
			mer.setExternalReference(Integer.toString(lu.getId()));
			mer.setExternalSystem("GermaNet 7.0 LexicalUnit-ID");
			LinkedList<MonolingualExternalRef> mers = new LinkedList<MonolingualExternalRef>();
			mers.add(mer);
			sense.setMonolingualExternalRefs(mers);

			// Adding this sense to the group of generated sense for the consumed luGroup
			senses.add(sense);

			// Set the senseRelations
			setSenseRelations(lu);
		}
		return senses;
	}

	/**
	 * This method consumes an instance of {@link LexUnit} and generates sense relations associated with the
	 * consumed lexical relation
	 * @param lu LexUnit for which corresponding instances of {@link SenseRelation} should be generated,
	 * based on the consumed {@link LexRel}
	 * @param lexRelation lexical relation for which the list of associated sense relations should be returned
	 * @return a {@link List} of SenseRelations for the consumed lu and lexRelation
	 */
	private List<SenseRelation> generateSenseRelation(LexUnit lu, LexRel lexRelation){
		List<SenseRelation> senseRelations = new LinkedList<SenseRelation>();
		// Generating Relations
		List<LexUnit> targets = lu.getRelatedLexUnits(lexRelation);
		if(!targets.isEmpty()){
			// If lu has a lexRelation to other lus, a sense Relation should be generated
			HashSet<SenseRelation> temp = new HashSet<SenseRelation>();
			ERelTypeSemantics relType = getRelType(lexRelation);
			String relName = getRelName(lexRelation);
			for(LexUnit target : targets){
				// Generate a SenseRelation
				SenseRelation senseRelation = new SenseRelation();
				// Set targeted Sense
				senseRelation.setTarget(synsetGenerator.getSense(target));
				// Setting relType and relName
				senseRelation.setRelType(relType);
				senseRelation.setRelName(relName);
                temp.add(senseRelation);
			}
			senseRelations.addAll(temp);
		}
		return senseRelations;
	}

	/**
	 * This method consumes an instance of {@link LexUnit} and generates sense relations associated with the
	 * consumed conceptual relation
	 * @param lu LexUnit for which corresponding instances of {@link SenseRelation} should be generated,
	 * based on the consumed {@link ConRel}
	 * @param conRelation conceptual relation for which the list of associated sense relations should be returned
	 * @return a {@link List} of SenseRelations for the consumed lu and conRelation
	 */
	private List<SenseRelation> generateSenseRelation(LexUnit lu, ConRel conRelation){
		List<SenseRelation> senseRelations = new LinkedList<SenseRelation>();
		// Generating Relation
		List<Synset> targets = lu.getSynset().getRelatedSynsets(conRelation);
		if(!targets.isEmpty()){
			// If lu's Synset has a conRelation to other Synsets, a sense Relation should be generated
			HashSet<SenseRelation> temp = new HashSet<SenseRelation>();
			ERelTypeSemantics relType = getRelType(conRelation);
			String relName = getRelName(conRelation);
			for(Synset target : targets){
				// Generate a SenseRelation
				SenseRelation senseRelation = new SenseRelation();
				senseRelation.setTarget(synsetGenerator.getSense(target));
				senseRelation.setRelType(relType);
				senseRelation.setRelName(relName);
				temp.add(senseRelation);
			}
			senseRelations.addAll(temp);
		}
		return senseRelations;
	}

	/**
	 * This method consumes a lexical relation and returns the
	 * corresponding Uby-LMF relation type
	 * @param lexRel for which Uby-LMF relation type should be returned
	 * @return lexRel's relation type in Uby-LMF or null if no entry for lexRel exists
	 * @see LexRel
	 * @see ERelTypeSemantics
	 */
	private ERelTypeSemantics getRelType(LexRel lexRel){
		ERelTypeSemantics relType = null;
		switch (lexRel){
		case has_antonym : relType = ERelTypeSemantics.complementary; break;
		case has_synonym : relType = ERelTypeSemantics.association; break;
		default : System.err.println("Error, LexicalRelation not recognized: "+lexRel); System.exit(1);
		}
		return relType;
	}

	/**
	 * This method consumes a lexical relation and returns the
	 * corresponding Uby-LMF relation name
	 * @param lexRel for which Uby-LMF relation name should be returned
	 * @return lexRel's relation name in Uby-LMF or null if no entry for lexRel exists
	 * @see LexRel
	 * @see ERelTypeSemantics
	 */
	private String getRelName(LexRel lexRel){
		String relName;
		switch (lexRel){
		case has_antonym : relName = ERelNameSemantics.ANTONYM; break;
		case has_synonym : relName = ERelNameSemantics.SYNONYM; break;
		default : relName = null;
		}
		return relName;
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
		case causes : relName = ERelNameSemantics.CAUSES; break;
		case entails : relName = ERelNameSemantics.ENTAILS; break;
		case has_component_holonym : relName = ERelNameSemantics.HOLONYMCOMPONENT; break;
		case has_component_meronym : relName = ERelNameSemantics.MERONYMCOMPONENT; break;
		case has_hypernym : relName = ERelNameSemantics.HYPERNYM; break;
		case has_hyponym : relName = ERelNameSemantics.HYPONYM; break;
		case has_member_holonym : relName = ERelNameSemantics.HOLONYMMEMBER; break;
		case has_member_meronym : relName = ERelNameSemantics.MERONYMMEMBER; break;
		case has_portion_holonym : relName = ERelNameSemantics.HOLONYMPORTION; break;
		case has_portion_meronym : relName = ERelNameSemantics.MERONYMPORTION; break;
		case has_substance_holonym : relName = ERelNameSemantics.HOLONYMSUBSTANCE; break;
		case has_substance_meronym : relName = ERelNameSemantics.MERONYMSUBSTANCE; break;
		case is_entailed_by : relName = ERelNameSemantics.ENTAILEDBY; break;
		case is_related_to : relName = ERelNameSemantics.RELATED; break;
		default : relName = null;
		}
		return relName;
	}

	/**
	 * This method consumes an instance of {@link LexUnit} and appends all sense relations to
	 * sense associated with the consumed lexical unit.
	 * @param lu to which sense, sense relation should be generated and appended
	 * @see Sense
	 * @see SenseRelation
	 */
	private void setSenseRelations(LexUnit lu){
		List<SenseRelation> senseRelations = new ArrayList<SenseRelation>();

		senseRelations.addAll(generateSenseRelation(lu, LexRel.has_antonym));
		senseRelations.addAll(generateSenseRelation(lu, LexRel.has_synonym));
		for(ConRel conRel : ConRel.values()) {
            senseRelations.addAll(generateSenseRelation(lu, conRel));
        }
		Sense sense = synsetGenerator.getSense(lu);
		if(!senseRelations.isEmpty()) {
            sense.setSenseRelations(senseRelations);
        }
	}

	/**
	 * Returns instance of {@link SynsetGenerator} associated with this {@link SenseGenerator}
	 * @return the synset generator associated with this sense generator
	 */
	public SynsetGenerator getSynsetGenerator() {
		return synsetGenerator;
	}

	/**
	 * This method consumes a lexical unit and returns the associated sense
	 * @param lu which associated sense will be returned
	 * @return sense that corresponds to the consumed lu, or null if this sense generator has not processed this lu yet
	 * @see LexUnit
	 * @see Sense
	 * @see SenseGenerator
	 */
	public Sense getSense(LexUnit lu){
		return luSenseMappings.get(lu);
	}
}
