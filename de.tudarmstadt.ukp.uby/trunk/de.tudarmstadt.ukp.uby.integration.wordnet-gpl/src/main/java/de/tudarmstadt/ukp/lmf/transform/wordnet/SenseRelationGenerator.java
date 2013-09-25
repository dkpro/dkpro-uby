/*******************************************************************************
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
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.Word;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ERelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;

/**
 * This class offers methods for generating instances of {@link SenseRelation}-class
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 * @see Sense
 */
public class SenseRelationGenerator {

	/*
	 * Mappings between WordNet's pointer types and corresponding relation names defined in Uby-LMF
	 * depending also from part of speech
	 * The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private Map<String, String[]> pointerTypeRelNameMappings;

	/*
	 * Mappings between WordNet's pointer types and corresponding relation types defined in Uby-LMF
	 * depending also from part of speech
	 * The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private Map<String, ERelTypeSemantics[]> pointerTypeRelTypeMappings;

	private final LexicalEntryGenerator lexicalEntryGenerator;

	/**
	 * Constructs a new {@link SenseRelationGenerator}.<br>
	 * The constructor initializes the mappings used by the generator.
	 */
	public SenseRelationGenerator(LexicalEntryGenerator lexicalEntryGenerator) {
		this.lexicalEntryGenerator = lexicalEntryGenerator;
		initializePointerMappings();
	}

	/**
	 * This method initializes the mappings of WordNet's pointers
	 * and Uby's relation names and relation types
	 */
	private void initializePointerMappings() {
		// The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
		pointerTypeRelNameMappings = new HashMap<String, String[]>();
		pointerTypeRelTypeMappings = new HashMap<String, ERelTypeSemantics[]>();

		// Adding mappings

		// antonym
		pointerTypeRelNameMappings.put("!", new String[]
		  {ERelNameSemantics.ANTONYM, ERelNameSemantics.ANTONYM, ERelNameSemantics.ANTONYM, ERelNameSemantics.ANTONYM});

		pointerTypeRelTypeMappings.put("!", new ERelTypeSemantics[]
		  {ERelTypeSemantics.complementary, ERelTypeSemantics.complementary, ERelTypeSemantics.complementary, ERelTypeSemantics.complementary});

		// seeAlso
		pointerTypeRelNameMappings.put("^", new String[]
          {null, ERelNameSemantics.SEEALSO, ERelNameSemantics.SEEALSO, null});

		pointerTypeRelTypeMappings.put("^", new ERelTypeSemantics[]
		  {null, ERelTypeSemantics.association, ERelTypeSemantics.association, null});
	}

	/**
	 * This method updates the SenseRelations of all Senses provided by the
	 * instance of {@link LexicalEntryGenerator}, used by this {@link SenseRelationGenerator}
	 * @see Sense
	 * @see SenseRelation
	 */
	public void updateSenseRelations(){
		for(Word lexeme : lexicalEntryGenerator.getSenseGenerator().getProcessedLexemes()){
			updateSenseRelations(lexeme);
		}
	}

	/**
	 * This method updates SenseRelations of the Sense associated with the consumed lexeme.
	 * @see Sense
	 * @see SenseRelation
	 * @see Word
	 */
	private void updateSenseRelations(Word lexeme) {

		SenseGenerator senseGenerator = lexicalEntryGenerator.getSenseGenerator();

		Sense sense = senseGenerator.getSense(lexeme);

		List<SenseRelation> senseRelations = new LinkedList<SenseRelation>();

		for(Pointer pointer : lexeme.getPointers()){
			if(pointerTypeRelNameMappings.containsKey(pointer.getType().getKey())) {
                senseRelations.add(createSenseRelation(pointer));
            }
		}
		sense.setSenseRelations(senseRelations);
	}

	/**
	 * This method creates a SenseRelation based on the consumed WordNet-pointer
	 * @param pointer WordNet's {@link Pointer}
	 * @return instance of {@link SenseRelation}-class associated with the consumed pointer
	 */
	private SenseRelation createSenseRelation(Pointer pointer){

		SenseRelation senseRelation = new SenseRelation();

		// Setting the target
		Word target = (Word) pointer.getTarget();
		senseRelation.setTarget(lexicalEntryGenerator.getSenseGenerator().getSense(target));

		String pointerKey = pointer.getType().getKey();
		int posOrdinal = POS.getAllPOS().indexOf(pointer.getSource().getPOS());

		// setting RelationName
		String relName = getRelName(pointerKey, posOrdinal);

		senseRelation.setRelName(relName);

		// settin relation type
		ERelTypeSemantics relType = getRelType(pointerKey, posOrdinal);

		senseRelation.setRelType(relType);

		return senseRelation;
	}

	/**
	 * This method consumes a WordNet's pointer-symbol and returns the relation name of the
	 * associated {@link SenseRelation}, defined in Uby-LMF
	 * @param pointerSymbol WordNet's pointer symbol for which a relation name should be returned
	 * @param posOrdinal the ordinal of pointer's source part of speech
	 * @return relation name of the associated SenseRelation-instance
	 * @see Pointer
	 * @see POS
	 */
	private String getRelName(String pointerSymbol, int posOrdinal) {
		// The relType also depends on the POS of the pointer's source
		return pointerTypeRelNameMappings.get(pointerSymbol)[posOrdinal];
	}

	/**
	 * This method consumes a WordNet's pointer-symbol and returns the relation type of the
	 * associated {@link SenseRelation}, defined in Uby-LMF
	 * @param pointerSymbol WordNet's pointer symbol for which a relation type should be returned
	 * @param posOrdinal the ordinal of pointer's source part of speech
	 * @return relation type of the associated SenseRelation-instance
	 * @see Pointer
	 * @see POS
	 */
	private ERelTypeSemantics getRelType(String pointerSymbol, int posOrdinal) {
		// The relType also depends on the POS of the pointer's source
		return pointerTypeRelTypeMappings.get(pointerSymbol)[posOrdinal];
	}

}
