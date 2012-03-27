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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.POS;
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
	private LexicalEntryGenerator lexicalEntryGenerator;
	
	/*
	 * Mappings between PointerTypes and corresponding ERelTypes (depends also from lexeme's POS) 
	 * Used for RelatedForms only
	 * The Mappings for different POS are as follows {NOUN, VERB, ADJECTIVE, ADVERB}
	 */
	private final static Map<String, ERelTypeMorphology[]> pointerTypeRelTypeMappings = new HashMap<String, ERelTypeMorphology[]>();
	
	private Logger logger = Logger.getLogger(WNConverter.class.getName());

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
			Set<RelatedForm> relatedForms = new HashSet<RelatedForm>();
			// Iterate over every Lexeme and check for possible RelatedForms
			for(Word lexeme : lexemeGroup)
				for(Pointer pointer : lexeme.getPointers())
					if(pointerTypeRelTypeMappings.containsKey(pointer.getType().getKey())){
						relatedForms.add(generateRelatedForm(pointer));
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
			logger.log(Level.SEVERE, sb.toString());
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
