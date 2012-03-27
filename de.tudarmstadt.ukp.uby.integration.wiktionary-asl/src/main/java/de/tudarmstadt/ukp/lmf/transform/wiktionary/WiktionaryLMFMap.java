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
package de.tudarmstadt.ukp.lmf.transform.wiktionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.wiktionary.api.Gender;
import de.tudarmstadt.ukp.wiktionary.api.Language;
import de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech;
import de.tudarmstadt.ukp.wiktionary.api.RelationType;

/**
 * Maps Wiktionary constants to LMF constants
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WiktionaryLMFMap {
		
	private static Map<String, ELanguageIdentifier> languageMap; // Language maps from Wiktionary to LMF
	private static String langCode = "language_code.en";
	
	/**
	 * Load language codes from the given path 
	 * create language mappings from Wiktionary to LMF
	 * @param path
	 */
	public static void loadLanguageCodes(String path) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			loadLanguageCodes(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads language codes from the given stream 
	 * create language mappings from Wiktionary to LMF
	 * @param stream the stream of language codes
	 * @throws IOException
	 */
	public static void loadLanguageCodes(InputStream stream) throws IOException {
		languageMap  = new HashMap<String, ELanguageIdentifier>();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = "";
		while((line = br.readLine()) != null)
			if(!line.startsWith("#")){ // Omit comments
				String[] parts = line.split("\t");			
				if(parts.length < 2)
					continue;
				String langName = parts[0].trim();
				String langCode = parts[1].trim();
				try{
					if(langCode.length() <= 3){	// Consider only languages that have two- or three-letter code
						ELanguageIdentifier lmfLang = ELanguageIdentifier.valueOf(langCode);
//						System.out.println(langName);
//						System.out.println(langCode);
						languageMap.put(langName.toUpperCase(), lmfLang);
					}
				}catch(IllegalArgumentException ex){	// No language found				
					continue;
				}
		}
		//
		br.close();
		languageMap.put("UNKNOWN", ELanguageIdentifier.unknown);
	}
	
	/**
	 * This method loads english language codes
	 * @throws IOException 
	 */
	public static void loadEnglishLanguageCodes() throws IOException{
		ClassLoader cl = WiktionaryLMFMap.class.getClassLoader();
		WiktionaryLMFMap.loadLanguageCodes(cl.getResource(langCode).openStream());
	}
	
	
	
	/**
	 * Maps Wiktionary Language to LMF LanguageIdentifier
	 * http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
	 * @param lang
	 */
	public static ELanguageIdentifier mapLanguage(Language lang){
		if(!languageMap.containsKey(lang.getName())){
			//System.out.println("Language not found: "+lang.getName());
			return ELanguageIdentifier.unknown;
		}
		return languageMap.get(lang.getName());
	}
	
	    
	
	/**
	 * Maps Wiktionary PartOfSpeech to LMF PartOfSpeech
	 * @param pos
	 */
	public static EPartOfSpeech mapPos(PartOfSpeech pos){
		if(pos.equals(PartOfSpeech.NOUN))
			return EPartOfSpeech.noun;		
		else if(pos.equals(PartOfSpeech.VERB)){
			return EPartOfSpeech.verb;
		}else if (pos.equals(PartOfSpeech.ADJECTIVE)){
			return EPartOfSpeech.adjective;
		}else if(pos.equals(PartOfSpeech.ADVERB))
			return EPartOfSpeech.adverb;
		else if (pos.equals(PartOfSpeech.NUMBER))
			return EPartOfSpeech.numeral;
		else if (pos.equals(PartOfSpeech.INTERJECTION))
			return EPartOfSpeech.interjection;		
		else if(pos.equals(PartOfSpeech.ANSWERING_PARTICLE))
			return EPartOfSpeech.answerParticle;
		else if(pos.equals(PartOfSpeech.AUXILIARY_VERB))
			return EPartOfSpeech.verbAuxiliary;
		else if(pos.equals(PartOfSpeech.COMPARATIVE_PARTICLE))
			return EPartOfSpeech.comparativeParticle;
		else if(pos.equals(PartOfSpeech.DETERMINER))
			return EPartOfSpeech.determiner;
		else if(pos.equals(PartOfSpeech.INTERROGATIVE_PRONOUN))
			return EPartOfSpeech.interrogativePronoun;
		else if(pos.equals(PartOfSpeech.NEGATIVE_PARTICLE))
			return EPartOfSpeech.negativeParticle;
		else if(pos.equals(PartOfSpeech.NUMERAL))
			return EPartOfSpeech.numeral;
		else if(pos.equals(PartOfSpeech.PROPER_NOUN))
			return EPartOfSpeech.nounProper;
		else if (pos.equals(PartOfSpeech.PREPOSITION))
			return EPartOfSpeech.preposition;
		else if (pos.equals(PartOfSpeech.PRONOUN))
			return EPartOfSpeech.pronoun;
		else if (pos.equals(PartOfSpeech.CONJUNCTION))
			return EPartOfSpeech.conjunction;
		else {
			
			//System.out.println("CAN't map pos " + pos.name());
			return null;
		}
	}
	/**
	 * Maps Wiktionary Gender to LMF GrammaticalGender
	 * @param gender
	 */
	public static EGrammaticalGender mapGender(Gender gender){
		if(gender.equals(Gender.NEUTER))
			return EGrammaticalGender.neuter;
		else if(gender.equals(Gender.FEMININE))
			return EGrammaticalGender.feminine;
		else return null;
	}
	/**
	 * Maps Wiktionary Relation type to LMF RelTypeSemantics
	 * @param relationType
	 */
	public static ERelTypeSemantics mapRelationType(RelationType relationType) {
		if(relationType.equals(RelationType.ANTONYM))
			return ERelTypeSemantics.complementary;
		else if(relationType.equals(RelationType.HOLONYM))
			return ERelTypeSemantics.partWhole;
		else if(relationType.equals(RelationType.HYPERNYM))
			return ERelTypeSemantics.taxonomic;
		else if(relationType.equals(RelationType.HYPONYM))
			return ERelTypeSemantics.taxonomic;
		else if(relationType.equals(RelationType.MERONYM))
			return ERelTypeSemantics.partWhole;
		else if(relationType.equals(RelationType.SYNONYM))
			return ERelTypeSemantics.association;
		else 
			return null;		
	} 
	
	

	
}
