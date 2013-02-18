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
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * An instance of this class updates the SenseRelations of already created Senses
 * @author matuschek
 *
 */
public class SenseRelationGenerator {

	/*
	 * Mappings between OW relation names and corresponding Uby relation names (depends also from Synset's POS)
	 * Used for SynsetRelations only
	 */
	private  HashMap<String, String> relNameMappings;

	/*
	 * Mappings between OW relations and corresponding relTypes (depends also from Synset's POS)
	 * Used for SynsetRelations only
	 */
	private  HashMap<String, ERelTypeSemantics> relTypeMappings;

	private final LexicalEntryGenerator lexicalEntryGenerator;
	private final SynsetGenerator synsetGenerator;

	public SenseRelationGenerator(LexicalEntryGenerator lexicalEntryGenerator, SynsetGenerator synsetGenerator) {

		this.lexicalEntryGenerator = lexicalEntryGenerator;
		this.synsetGenerator = synsetGenerator;
		if(relNameMappings == null) {
			initializeMappings();
		}
	}

	/**
	 * This method updates the SenseRelations
	 * of alredy created Senses
	 * @throws OmegaWikiException
	 */
	public void updateSenseRelations() throws OmegaWikiException {

		// Iterate over all LexemeGroups and update
		for(SynTrans lexeme : lexicalEntryGenerator.getSenseGenerator().getProcessedLexemes()){
			updateSenseRelations(lexeme);
		}
	}

	/**
	 * This method updates the SenseRelation
	 * of lexeme's Sense
	 * @param lexeme
	 * @throws OmegaWikiException
	 */
	private void updateSenseRelations(SynTrans lexeme) throws OmegaWikiException {

		SenseGenerator senseGenerator = lexicalEntryGenerator.getSenseGenerator();
		Sense sense = senseGenerator.getSense(lexeme);
		List<SenseRelation> senseRelations = new LinkedList<SenseRelation>();
		DefinedMeaning dm = lexeme.getDefinedMeaning();
		Synset sourceSS = synsetGenerator.getLMFSynset(dm);
		for (SynsetRelation ssr : sourceSS.getSynsetRelations())
		{
			Synset targetSS;
			if((targetSS = ssr.getTarget())!=null) {

			for (Sense targetsense : targetSS.getSenses())
			{
				if(relNameMappings.containsKey(ssr.getRelName())) {
					senseRelations.add(createSenseRelation(targetsense,ssr.getRelName()));
				}
			}
			}
		}

		sense.setSenseRelations(senseRelations);
	}

	/**
	 * This method creates a SenseRelation based on the consumed type
	 * @param pointer
	 * @return
	 * @throws OmegaWikiException
	 */
	private SenseRelation createSenseRelation( Sense t, String type) throws OmegaWikiException {

		SenseRelation senseRelation = new SenseRelation();

		// Setting the target

		senseRelation.setTarget(t);


		// setting RelationName
		senseRelation.setRelName(getRelName(type));

		// setting RelationName
		senseRelation.setRelType(getRelType(type));

		return senseRelation;
	}

	/**
	 * This method consumes an OW relation and returns the corresponding SenseRelation-relName
	 * @param relation
	 * @return corresponding relation name
	 */
	private String getRelName(String relation) {
		return relNameMappings.get(relation);
	}

	/**
	 * This method consumes an OW relation and returns the corresponding SenseRelation-relType
	 * @param relation
	 * @return corresponding relation type
	 */
	private ERelTypeSemantics getRelType(String relation) {
		// The relType also depends on the POS of the pointer's source
		return relTypeMappings.get(relation);
	}
	/**
	 * This method initializes the relation mappings
	 */
	private void initializeMappings() {

		relNameMappings = new HashMap<String, String>();
		relTypeMappings = new HashMap<String, ERelTypeSemantics>();

		// Adding mappings

		// hypernym
		relNameMappings.put("hypernym", ERelNameSemantics.HYPERNYM);

		relTypeMappings.put("hypernym", ERelTypeSemantics.taxonomic);

		relNameMappings.put("has_hypernym", ERelNameSemantics.HYPERNYM);

		relTypeMappings.put("has_hypernym", ERelTypeSemantics.taxonomic);


		relNameMappings.put("hypernymInstance", ERelNameSemantics.HYPERNYM);

		relTypeMappings.put("hypernymInstance", ERelTypeSemantics.taxonomic);

		relNameMappings.put("broader term", ERelNameSemantics.HYPERNYM);

		relTypeMappings.put("broader term", ERelTypeSemantics.taxonomic);
		relNameMappings.put("parent", ERelNameSemantics.HYPERNYM);

		relTypeMappings.put("parent", ERelTypeSemantics.taxonomic);

		// hyponym
		relNameMappings.put("hyponym", ERelNameSemantics.HYPONYM);

		relTypeMappings.put("hyponym", ERelTypeSemantics.taxonomic);

		relNameMappings.put("has_hyponym", ERelNameSemantics.HYPONYM);

		relTypeMappings.put("has_hyponym", ERelTypeSemantics.taxonomic);


		relNameMappings.put("hyponymInstance", ERelNameSemantics.HYPONYM);

		relTypeMappings.put("hyponymInstance", ERelTypeSemantics.taxonomic);

		relNameMappings.put("narrower term", ERelNameSemantics.HYPONYM);

		relTypeMappings.put("narrower term", ERelTypeSemantics.taxonomic);

		relNameMappings.put("child", ERelNameSemantics.HYPONYM);

		relTypeMappings.put("child", ERelTypeSemantics.taxonomic);

		// holonymMember
		relNameMappings.put("holonym", ERelNameSemantics.HOLONYM);

		relTypeMappings.put("holonym",ERelTypeSemantics.partWhole);

		// meronymMember
		relNameMappings.put("meronym", ERelNameSemantics.MERONYM);

		relTypeMappings.put("meronym", ERelTypeSemantics.partWhole);

		// topic
		relNameMappings.put("is part of theme",ERelNameSemantics.RELATED);

		relTypeMappings.put("is part of theme", ERelTypeSemantics.association);
		// topic
		relNameMappings.put("subject",ERelNameSemantics.RELATED);

		relTypeMappings.put("subject", ERelTypeSemantics.association);
		// seeAlso
		relNameMappings.put("related term", ERelNameSemantics.RELATED);

		relTypeMappings.put("related term", ERelTypeSemantics.association);
		relNameMappings.put("is_related_to", ERelNameSemantics.RELATED);

		relTypeMappings.put("is_related_to", ERelTypeSemantics.association);

		// seeAlso
		// seeAlso
		relNameMappings.put("seeAlso", ERelNameSemantics.SEEALSO);

		relTypeMappings.put("seeAlso", ERelTypeSemantics.association);

		relNameMappings.put("holonymMember", ERelNameSemantics.HOLONYMMEMBER);
		relTypeMappings.put("holonymMember", ERelTypeSemantics.partWhole);

		relNameMappings.put("holonymPart", ERelNameSemantics.HOLONYMPART);
		relTypeMappings.put("holonymPart", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_component_holonym", ERelNameSemantics.HOLONYMCOMPONENT);
		relTypeMappings.put("has_component_holonym", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_substance_holonym", ERelNameSemantics.HOLONYMSUBSTANCE);
		relTypeMappings.put("has_substance_holonym", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_member_holonym", ERelNameSemantics.MERONYMMEMBER);
		relTypeMappings.put("has_member_holonym", ERelTypeSemantics.partWhole);

		relNameMappings.put("meronymMember", ERelNameSemantics.MERONYMMEMBER);
		relTypeMappings.put("meronymMember", ERelTypeSemantics.partWhole);

		relNameMappings.put("meronymPart", ERelNameSemantics.MERONYMPART);
		relTypeMappings.put("meronymPart", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_component_meronym", ERelNameSemantics.MERONYMCOMPONENT);
		relTypeMappings.put("has_component_meronym", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_substance_meronym", ERelNameSemantics.MERONYMSUBSTANCE);
		relTypeMappings.put("has_substance_meronym", ERelTypeSemantics.partWhole);

		relNameMappings.put("has_member_meronym", ERelNameSemantics.MERONYMMEMBER);
		relTypeMappings.put("has_member_meronym", ERelTypeSemantics.partWhole);

		relNameMappings.put("entails", ERelNameSemantics.ENTAILS);
		relTypeMappings.put("entails", ERelTypeSemantics.predicative);

		relNameMappings.put("is_entailed_by", ERelNameSemantics.ENTAILEDBY);
		relTypeMappings.put("is_entailed_by", ERelTypeSemantics.predicative);

		relNameMappings.put("causes", ERelNameSemantics.CAUSES);
		relTypeMappings.put("causes", ERelTypeSemantics.predicative);


		relNameMappings.put("seeAlso", ERelNameSemantics.SEEALSO);

		relTypeMappings.put("seeAlso", ERelTypeSemantics.association);


		relNameMappings.put("antonym", ERelNameSemantics.ANTONYM);

		relTypeMappings.put("antonym", ERelTypeSemantics.complementary);

		// Setting mappings related to domainOf Relations
//		domainOfRegisterMappings.put("is part of theme", ELabelTypeSemantics.DOMAIN);
//		domainOfRegisterMappings.put("subject", ELabelTypeSemantics.DOMAIN);
//		domainOfRegisterMappings.put("topic", ELabelTypeSemantics.DOMAIN);

	}
}
