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
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OWLanguage;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.db.DatabaseStatements;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * This class is used for generating of SynsetRelations
 * @author matuschek
 *
 */
public class SynsetRelationGenerator {
	private final   int GlobalLanguage;
	private final SynsetGenerator synsetGenerator; // SynsetGenerator is needed for extraction of Synset-Information
	private final LexicalEntryGenerator lexicalEntryGenerator;
	private final OmegaWiki ow ;
	/*
	 * Mappings between relation names and and corresponding internal RelNames
	 * Used for SynsetRelations only
	 */
	private  HashMap<String, String> relNameMappings;

	/*
	 * Mappings between relations and corresponding ERelTypes
	 * Used for SynsetRelations only
	 */
	private  HashMap<String, ERelTypeSemantics> relTypeMappings;

	private final  Map<String, ELabelTypeSemantics> domainOfRegisterMappings = new HashMap<String,ELabelTypeSemantics>(); // <domainOfRelationKey, register>

	/**
	 * Constructs a SynsetRelationGenerator
	 * @param synsetGenerator SynsetGenerator is used for extraction of Synset-Informations, SynsetGenerator must be initialized!
	 * @param lexicalEntryGenerator is used for extraction of LE-Information
	 */
	public SynsetRelationGenerator(SynsetGenerator synsetGenerator, LexicalEntryGenerator leg){
		this.lexicalEntryGenerator=leg;
		this.synsetGenerator = synsetGenerator;
		this.GlobalLanguage=synsetGenerator.getGlobalLanguage();
		ow = this.synsetGenerator.getOmegaWiki();
		if(relNameMappings == null) {
			initializeMappings();
		}
	}

	/**
	 * This method iterates over all Synsets (provided by SynsetGenerator)
	 *  and updates their SynsetRelations
	 * @throws UnsupportedEncodingException
	 * @throws OmegaWikiException
	 */
	public void updateSynsetRelations() throws OmegaWikiException, UnsupportedEncodingException{
		// Iterate over all Synset-Bindings and update
		int overall = synsetGenerator.getOWSynsetLMFSynsetMappings().size();
		int current = 0;
		for(Entry<DefinedMeaning, Synset> binding : synsetGenerator.getOWSynsetLMFSynsetMappings().entrySet()) {
			updateSynsetRelations(binding);
			if(current++ % 1000 == 0)
			{
				System.out.println("Generating SynsetRelations... Finished " + ((current * 100) / overall) + "%");
			}
		}
	}

	/**
	 * This method consumes a Synset-Binding and updates the corresponding SynsetRelations
	 * @param binding
	 * @throws UnsupportedEncodingException
	 * @throws OmegaWikiException
	 */
	private void updateSynsetRelations(
			Entry<DefinedMeaning, Synset> binding) throws OmegaWikiException, UnsupportedEncodingException {
		// Create SynsetRelation for this Binding
		List<SynsetRelation> synsetRelations = new LinkedList<SynsetRelation>();
		DefinedMeaning dm = binding.getKey( );
		Map<DefinedMeaning,Integer> targets = dm.getDefinedMeaningLinksAllLang(GlobalLanguage);
		//Handle invalid relations
		DefinedMeaning dummy;
		if ((dummy = dm.getSubject()) != null) {
			targets.put(dummy, -1);
		}

		// Iterate over all pointers of the DM and generate the corresponding SynsetRelation
		for(DefinedMeaning dmt : targets.keySet()) {
			SynsetRelation ssr = generateSynsetRelation(dm,dmt, targets.get(dmt));
			if(ssr!=null) {
				synsetRelations.add(ssr);
			}

		}
		binding.getValue().setSynsetRelations(synsetRelations);
	}

	private DatabaseStatements dbStatements;

	/**
	 * This method consumes two  DMs  plus relationtype as int and generates the corresponding SynsetRelation for it
	 *
	 * @param DefinedMeaning source
	 * @param DefinedMeaning target
	 * @param int type
	 * @return generated SynsetRelation
	 * @throws UnsupportedEncodingException
	 * @throws OmegaWikiException
	 */
	private SynsetRelation generateSynsetRelation(DefinedMeaning source, DefinedMeaning target, int type) throws OmegaWikiException, UnsupportedEncodingException {
		// Create a SynsetRelation for these DMs
		SynsetRelation synsetRelation = new SynsetRelation();
		DefinedMeaning rel = null;
		String relationName="";
		if(type >0)
		{
//			rel = ow.getDefinedMeaningById(type);
			//TODO: Dirty hack! Return to old code as soon as https://code.google.com/p/jowkl/issues/detail?id=2 is fixed and released.
			if (dbStatements == null) {
                try {
					dbStatements = new DatabaseStatements(ow.getDatabaseConfiguration());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
            }
			rel = new DefinedMeaning(type, dbStatements);
		Set<SynTrans> sta = rel.getSynTranses(OWLanguage.English);
		if (!sta.isEmpty()) {
            relationName = sta.iterator().next().getSyntrans().getSpelling();
        }
		}
		else {
			relationName="subject";
			// Setting relationType
		}

	ERelTypeSemantics relType = getRelType(relationName);
		synsetRelation.setRelType(relType);

		// Setting relationName
		synsetRelation.setRelName(getRelationName(relationName));
		// Setting the target
		// the target is a Synset. If it's not in the resource, skip it
			if(synsetGenerator.getLMFSynset(target)!=null) {
				synsetRelation.setTarget(synsetGenerator.getLMFSynset(target));
			}
			else {
				return null;
			}

			/*
			 * Updating SubjectField class
			 * this block will only be executed for domain relations
			 */
			if(domainOfRegisterMappings.keySet().contains(getRelationName(relationName))){
				// SenseGenerator is needed in order to obtain the Lexeme's corresponding Sense
				SenseGenerator senseGenerator = lexicalEntryGenerator.getSenseGenerator();
				// Iterate over every lexeme of the source synset
				for(SynTrans lexeme : source.getSynTranses(GlobalLanguage)){
					// Obtain lexeme's Sense
					Sense sense = senseGenerator.getSense(lexeme);
					List<SemanticLabel> semanticLabels = null;
					if(sense != null)
					{
					// obtain semantic labels
					semanticLabels = sense.getSemanticLabels();

					}
					if(semanticLabels == null) {
						semanticLabels = new LinkedList<SemanticLabel>();
						// Create a new SemanticLabel and add it to the list
						SemanticLabel semanticLabel = createSemanticLabel(target, getRelationName(relationName));
						semanticLabels.add(semanticLabel);
						// set the subjectField
						sense.setSemanticLabels(semanticLabels);
					}

				}
			}


		return synsetRelation;
	}

	/**
	 * This method consumes target DM of a Domain relation and generates the
	 * corresponding instance of SemanticLabel class
	 * @param targetDM
	 * @param key describes the relation
	 * @return instance of SemanticLabel class
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	private SemanticLabel createSemanticLabel(DefinedMeaning targetDM, String key) throws UnsupportedEncodingException, OmegaWikiException {

		SemanticLabel semanticLabel = new SemanticLabel();
		semanticLabel.setLabel(getSemanticLabel(targetDM));
		semanticLabel.setType(domainOfRegisterMappings.get(key));

		return semanticLabel;
	}

	/**
	 * This method consumes a DM
	 * and returns the the lemma of the targetSynset's first lexeme
	 * @param targetDM
	 * @return the the lemma of the targetSynset's first lexeme
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	private String getSemanticLabel(DefinedMeaning targetDM) throws UnsupportedEncodingException, OmegaWikiException {

		Set<SynTrans> st = targetDM.getSynTranses(GlobalLanguage);
		if (!st.isEmpty()) {
            return st.iterator().next().getSyntrans().getSpelling();
        }
		return "";
	}

	/**
	 * This method consumes a OW relation type and returns the corresponding Synset-relType
	 * @param relation the relation in OW
	 * @return corresponding RelType
	 */
	private ERelTypeSemantics getRelType(String relation) {
		ERelTypeSemantics ret = null;
		if ((ret = relTypeMappings.get(relation))!=null) {
			return ret;
		}
		else {
			return ERelTypeSemantics.predicative;
		}

	}

	/**
	 * This method consumes a DM relation name and returns the corresponding Synset-relationName
	 * @param relation the relation name
	 * @return corresponding relationName
	 */
	private String getRelationName(String relation) {
		String ret = null;
		if ((ret = relNameMappings.get(relation))!=null) {
			return ret;
		}
		else {
			return relation;
		}
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
		domainOfRegisterMappings.put("is part of theme", ELabelTypeSemantics.domain);
		domainOfRegisterMappings.put("subject", ELabelTypeSemantics.domain);
		domainOfRegisterMappings.put("topic", ELabelTypeSemantics.domain);

	}
}
