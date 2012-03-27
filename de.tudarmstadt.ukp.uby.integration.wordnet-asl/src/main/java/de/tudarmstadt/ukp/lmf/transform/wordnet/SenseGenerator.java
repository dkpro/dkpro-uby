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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.data.Word;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.IndexSenseReader;

/**
 * This class offers methods for generating instances of {@link Sense} class from
 * <a href="URL#http://wordnet.princeton.edu/">WordNet 3.0</a>
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
class SenseGenerator {
	
	private IndexSenseReader isr;

	/*
	 * Synset generator used for obtaining mappings between
	 * WordNet's synsets and synsets defined in Uby-LMF
	 */
	private SynsetGenerator synsetGenerator;

	// used for creating IDs of SenseExamples
	private int senseExampleNumber=0;

	// Running number for creating Sense-IDs
	private int lmfSenseNumber; 
	
	// Mappings between lexemes and associated Senses
	private Map<Word, Sense> lexemeSenseMappings = new HashMap<Word, Sense>();
	
	private Logger logger = Logger.getLogger(WNConverter.class.getName());
	
	/**
	 * Constructs a {@link SenseGenerator} based on the consumed parameters
	 * @param synsetGenerator a SynsetGenerator used for obtaining Synsets
	 * @param isr reader used for parsing WordNet's index.sense file
	 * @see Sense
	 * @see Synset
	 * @see IndexSenseReader
	 */
	public SenseGenerator(SynsetGenerator synsetGenerator, IndexSenseReader isr){
		this.synsetGenerator = synsetGenerator;
		this.isr = isr;
	}
	
	/**
	 * This method consumes a {@link Set} of lexemes and generates a list of Senses. <br>
	 * Every {@link Sense} in the returned list is associated with one lexeme in the consumed Set.
	 * @param lexemeGroup a group of lexemes with equal lemma and part of speech
	 * @return list of Sense-instances, based on the consumed group of lexemes
	 * @see Word
	 */
	public List<Sense> generateSenses(Set<Word> lexemeGroup){
		List<Sense> result = new ArrayList<Sense>();
		
		// a list of Senses that need a dummy sense number
		List<Sense> needDummySenseNumber = new ArrayList<Sense>();
		int nextIndex = 1; // dummy index
		
		// every lexeme has a sense of it's own
		for(Word lexeme : lexemeGroup){
			Sense sense = new Sense();
			lexemeSenseMappings.put(lexeme, sense);
			//set ID
			sense.setId(getNewID());
			// setting index of the Sense (lexeme's Position in the WN-Synset)
			String senseNumber = isr.getSenseNumber(lexeme.getSenseKey());
			if(senseNumber != null){
				int index = Integer.parseInt(senseNumber);
				if(nextIndex <= index)
					nextIndex = index+1;
				sense.setIndex(index);
			}
			else{
				// sense needs a dummy value for index
				needDummySenseNumber.add(sense);
				StringBuffer sb = new StringBuffer(128);
				sb.append("IndexSenseReader did not provide sense number for senseKey ");
				sb.append(lexeme.getSenseKey()).append('\n');
				sb.append("adding a dummy value of sense number");
				logger.log(Level.WARNING, sb.toString());
			}
			
			net.sf.extjwnl.data.Synset lexemeSynset = lexeme.getSynset(); // exeme's Synset
			
			//set Synset
			Synset lmfSynset = synsetGenerator.getLMFSynset(lexemeSynset);
			if(lmfSynset == null){
				StringBuffer sb = new StringBuffer(512);
				sb.append("Synset generator did not provide Uby-LMF Synset for WordNet's Synset ");
				sb.append(lexemeSynset).append('\n');
				sb.append("Closing VM");
				logger.log(Level.SEVERE, sb.toString());
				System.exit(1);
			}
			
			sense.setSynset(lmfSynset);
			// set semanticLabel
			List<SemanticLabel> semanticLabels = new LinkedList<SemanticLabel>();
			SemanticLabel semanticLabel = new SemanticLabel();
			semanticLabels.add(semanticLabel);
			semanticLabel.setLabel(lexemeSynset.getLexFileName());
			semanticLabel.setType("lexicographerFileName");
			
			sense.setSemanticLabels(semanticLabels);
			
			// Creating MonolingualExternalRef for a Sense
			MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
			
			// create an external reference
			StringBuffer sb = new StringBuffer(32);
			sb.append(lexeme.getSynset().getPOS());
			sb.append(" ");
			sb.append(lexeme.getSenseKey());

			monolingualExternalRef.setExternalSystem("WordNet 3.0 part of speech and sense key");
			monolingualExternalRef.setExternalReference(sb.toString());
			List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
			monolingualExternalRefs.add(monolingualExternalRef);
			sense.setMonolingualExternalRefs(monolingualExternalRefs);
			
			//*** create sense examples of the sense *** //
			List<SenseExample> senseExamples = new ArrayList<SenseExample>();
			List<String> exampleStrings = synsetGenerator.getExamples(lexeme);
			if(exampleStrings != null)
				for(String exampleSentence : exampleStrings){
					SenseExample senseExample = new SenseExample();
					
					// Create an id for the senseExample
					StringBuffer id = new StringBuffer(32);
					id.append("WN_SenseExample_").append(senseExampleNumber++);
					senseExample.setId(id.toString());
					senseExample.setExampleType(EExampleType.other);
					TextRepresentation textRepresentation = new TextRepresentation();
					textRepresentation.setLanguageIdentifier(ELanguageIdentifier.en);
					textRepresentation.setWrittenText(exampleSentence);
					senseExample.setTextRepresentations(new ArrayList<TextRepresentation>(Arrays.asList(textRepresentation)));
					senseExamples.add(senseExample);
				}
			// setting senseExamples
			sense.setSenseExamples(senseExamples);
			
			// Add the created Sense to the result
			result.add(sense);
			}
		
		/*
		 * Adding dummy indexes to senses if needed
		 */
		for(Sense sense : needDummySenseNumber)
			sense.setIndex(nextIndex++);
		
		return result;
	}
	
	/**
	 * This method generates a Sense-ID. <br>
	 * Every time the method is called, it increments the running number used for the creation of the ID.
	 * @return an ID of a Sense-instance
	 * @see Sense
	 */
	private String getNewID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("WN_Sense_").append(Integer.toString(lmfSenseNumber));
		lmfSenseNumber++;
		return sb.toString();
	}
	
	/**
	 * Returns the Sense-instance associated with the consumed lexeme
	 * @param lexeme a lexeme for which the generated Sense-intance should be returned
	 * @return Sense-instance associated with the consumed lexeme,<br>
	 * or null if this generator has not generated a Sense for the consumed lexeme
	 * @see Sense
	 * @see Word 
	 */
	public Sense getSense(Word lexeme){
		return lexemeSenseMappings.get(lexeme);
	}
	
	/**
	 * Returns all lexemes processed by this {@link SenseGenerator}
	 * @return all lexemes processed by this SenseGenerator
	 * @see Word
	 */
	public Set<Word> getProcessedLexemes(){
		return lexemeSenseMappings.keySet();
	}
}
