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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tuebingen.uni.sfs.germanet.api.Example;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;

/**
 * This class offers methods for generating {@link SenseExample} instances from GermaNet's data.
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 * @see Sense
 */
public class SenseExampleGenerator {
	private int exampleNumber=0; // This is the running number used for creating IDs of SenseExamples
	private Map<LexUnit, List<SenseExample>> generatedExamples = new HashMap<LexUnit, List<SenseExample>>();
	
	/**
	 * This class consumes an instance of {@link LexUnit} and 
	 * generates a List of SenseExamples for the consumed LexUnit
	 * @param lu LexicalUnit for which a list SenseExamples should be returned
	 * @return a list of SenseExamples for the consumed lu
	 * @see SenseExample
	 */
	public List<SenseExample> generateSenseExamples(LexUnit lu){
		List<SenseExample> result = generatedExamples.get(lu);
		if(result == null){
			result = new ArrayList<SenseExample>();
			List<Example> examples = lu.getExamples();
			for(Example example : examples)
				result.add(generateSenseExample(example, lu.getWordCategory()));
			generatedExamples.put(lu, result);
		}
		return result;
	}

	/**
	 * This method generates a new ID for an instance of {@link SenseExample} class
	 * @return an ID for an instance of SenseExample class
	 */
	private String getNewID(){
			StringBuffer sb = new StringBuffer(32);
			sb.append("GN_SenseExample_").append(exampleNumber);
			exampleNumber++;
			return sb.toString();
			}
	
	/**
	 * This method consumes an instance of {@link Example} class and generates the corresponding instance of
	 * {@link SenseExample} class.
	 * @param example an instance of Example class for which an instance of Uby's SenseExample class should be returned
	 * @param pos part of speech of the lexical unit from which the example is derived 
	 * @return instance of SenseExample class equal to consumed example
	 */
	private SenseExample generateSenseExample(Example example, WordCategory pos){
		SenseExample senseExample = null;
		String exampleText = example.getText();
		if(exampleText != null && !exampleText.equals("") && !exampleText.equals(" ")){
			senseExample = new SenseExample();
			senseExample.setId(getNewID());
			if(pos.equals(WordCategory.verben))
				senseExample.setExampleType(EExampleType.subcatFrame);
			else
				senseExample.setExampleType(EExampleType.senseInstance);
			TextRepresentation tr = new TextRepresentation();
			tr.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
			tr.setWrittenText(exampleText);
			List<TextRepresentation> temp = new ArrayList<TextRepresentation>();
			temp.add(tr);
			senseExample.setTextRepresentations(temp);
		}
		return senseExample;	
	}
}
