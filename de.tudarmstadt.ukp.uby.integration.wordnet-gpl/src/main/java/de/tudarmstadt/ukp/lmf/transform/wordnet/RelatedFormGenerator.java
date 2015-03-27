/**
 * Copyright 2015
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
package de.tudarmstadt.ukp.lmf.transform.wordnet;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.Word;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;

/**
 * This class offers methods for generating RelatedForms of LexicalEntries
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 * @see RelatedForm
 * @see LexicalEntry
 *
 */
public class RelatedFormGenerator {

	// LexicalEntryGenerator used for obtaining LexicalEntries
	private final LexicalEntryGenerator lexicalEntryGenerator;

	/*
	 * Mappings between PointerTypes and corresponding ERelTypes (depends also from lexeme's POS)
	 * Used for RelatedForms only
	 * The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private final static Map<String, ERelTypeMorphology[]> pointerTypeRelTypeMappings = new TreeMap<String, ERelTypeMorphology[]>();

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Constructs a {@link RelatedFormGenerator} based on consumed {@link LexicalEntryGenerator}
	 * @param lexicalEntryGenerator a LexicalEntryGenerator used for obtaining the LexicalEntries
	 * @see RelatedForm
	 * @see LexicalEntry
	 */
	public RelatedFormGenerator(LexicalEntryGenerator lexicalEntryGenerator) {
		this.lexicalEntryGenerator = lexicalEntryGenerator;
		initializePointerMappings();
	}

	/**
	 * This method updates RelatedForms of all LexicalEntries provided by the {@link LexicalEntryGenerator}
	 * @see LexicalEntry
	 * @see RelatedForm
	 */
	void updateRelatedForms() {
		Map<Set<Word>, LexicalEntry> mappings = lexicalEntryGenerator.getLexemeGroupLexicalEntryMaping();
		// Iterate over all lexemeGroups and update the RelatedForms of corresponding LexicalEntries
		for(Set<Word> lexemeGroup : mappings.keySet()){
			Set<RelatedForm> relatedForms = new LinkedHashSet<RelatedForm>();
			// Iterate over every Lexeme and check for possible RelatedForms
			for(Word lexeme : lexemeGroup) {
                for(Pointer pointer : lexeme.getPointers()) {
                    if(pointerTypeRelTypeMappings.containsKey(pointer.getType().getKey())){
						relatedForms.add(generateRelatedForm(pointer));
					}
                }
            }
			mappings.get(lexemeGroup).setRelatedForms(new ArrayList<RelatedForm>(relatedForms));
		}
	}


	/**
	 * This method initializes the mappings between WordNet's pointer types and <br>
	 * associated relation type in Uby-LMF
	 * @see ERelTypeMorphology
	 */
	private void initializePointerMappings() {

		//The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}

		// derivation
		pointerTypeRelTypeMappings.put("+", new ERelTypeMorphology[]
		  {ERelTypeMorphology.derivative, ERelTypeMorphology.derivative, ERelTypeMorphology.derivative, ERelTypeMorphology.derivationBaseAdj});

		// derivationBaseVerb
		pointerTypeRelTypeMappings.put("<", new ERelTypeMorphology[]
          {null, null, ERelTypeMorphology.derivationBaseVerb, null});

		// derivationBaseNound and derivationBaseAdj
		pointerTypeRelTypeMappings.put("\\", new ERelTypeMorphology[]
		  {null, null, ERelTypeMorphology.derivationBaseNoun, ERelTypeMorphology.derivationBaseAdj});
	}

	/**
	 * This method consumes a lexical pointer and generates the associated RelatedForm, defined in Uby-LMF
	 * @param pointer WordNet's lexical pointer
	 * @return RelatedForm associated with the consumed pointer
	 * @see RelatedForm
	 * @see Pointer
	 */
	private RelatedForm generateRelatedForm(Pointer pointer){
		RelatedForm relatedForm = new RelatedForm();

		// setting RelationType
		ERelTypeMorphology relType = getRelType(pointer.getType().getKey(), POS.getAllPOS().indexOf(pointer.getSource().getPOS()));

		relatedForm.setRelType(relType);

		// setting targetSense
		Word targetLexeme = (Word) pointer.getTarget();
		relatedForm.setTargetSense(lexicalEntryGenerator.getSenseGenerator().getSense(targetLexeme));

		// setting targeted LexicalEntry
		LexicalEntry targetLexicalEntry = lexicalEntryGenerator.getLexicalEntry(targetLexeme);

		if(targetLexicalEntry == null){
			StringBuffer sb = new StringBuffer(512);
			sb.append("LexicalEntryGenerator did not provide a LexicalEntry for lexeme: ").append(targetLexeme);
			sb.append('\n').append("closing virtual machine");
			logger.error(sb.toString());
			System.exit(1);
		}
		relatedForm.setTargetLexicalEntry(targetLexicalEntry);

		return relatedForm;
	}

	/**
	 * This method consumes a WN-PointerSymbol and returns the corresponding RelatedForm-relType
	 * @param pointerSymbol the Pointer's symbol
	 * @param posOrdinal the ordinal of Synset's POS
	 * @return corresponding RelType
	 * @see RelatedForm
	 * @see Pointer
	 * @see ERelTypeMorphology
	 */
	private ERelTypeMorphology getRelType(String pointerSymbol, int posOrdinal) {
		// The relType also depends on the POS of the pointer's source
		return pointerTypeRelTypeMappings.get(pointerSymbol)[posOrdinal];
	}
}
