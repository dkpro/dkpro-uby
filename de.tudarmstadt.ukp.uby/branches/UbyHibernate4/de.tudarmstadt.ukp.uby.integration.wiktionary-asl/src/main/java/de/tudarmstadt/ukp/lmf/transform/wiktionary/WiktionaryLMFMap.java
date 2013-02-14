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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeSemantics;
import de.tudarmstadt.ukp.wiktionary.api.Gender;
import de.tudarmstadt.ukp.wiktionary.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.wiktionary.api.RelationType;
import de.tudarmstadt.ukp.wiktionary.api.util.ILanguage;

/**
 * Maps Wiktionary constants to LMF constants
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WiktionaryLMFMap {
	
	private static final boolean PRINT_MISSING_POS = false;
	private static final boolean PRINT_MISSING_LANGUAGES = true;
	
	private static final String LANGUAGE_CODES_RESOURCE = "language_codes.txt";
	
	private static Map<String, String> languageMap; // Language maps from Wiktionary to LMF
	private static Set<String> missingLanguages;
	
	/** Loads the mapping of Wiktionary language codes to the ISO 639 codes
	 *  used in UBY from an internal resource. 
	 *  @throws IOException if the mapping file could not be loaded. */
	public static void loadLanguageCodes() throws IOException {
		loadLanguageCodes(WiktionaryLMFMap.class.getClassLoader()
				.getResource(LANGUAGE_CODES_RESOURCE).openStream());
	}
	
	/** Loads the mapping of Wiktionary language codes to the ISO 639 codes
	 *  used in UBY from the given file. 
	 *  @throws IOException if the mapping file could not be loaded. */
	public static void loadLanguageCodes(final File file) throws IOException {
		loadLanguageCodes(new FileInputStream(file));
	}

	/** Loads the mapping of Wiktionary language codes to the ISO 639 codes
	 *  used in UBY from the given input stream. 
	 *  @throws IOException if the mapping file could not be loaded. */
	public static void loadLanguageCodes(final InputStream stream) throws IOException {
		Map<String, String> languages = new TreeMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				// Skip empty lines and comments.
				if (line.isEmpty() || line.startsWith("#")) 
					continue;
				
				int idx = line.indexOf('\t');
				if (idx < 0)
					continue;
				
				String wktCode = line.substring(0, idx);
				String ubyCode = line.substring(idx + 1);
				languages.put(wktCode, ubyCode);
			}
		} finally {
			reader.close();
		}
		missingLanguages = new TreeSet<String>();
		languageMap  = languages;
	}
	
	
	/**
	 * Maps Wiktionary Language to LMF LanguageIdentifier
	 * http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
	 * @param lang
	 */
	public static String mapLanguage(final ILanguage lang) {
		if (lang == null)
			return null;
		
		if (languageMap == null)
			try {
				loadLanguageCodes();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		
		String result = languageMap.get(lang.getCode());
		if (result == null) {
			if (PRINT_MISSING_LANGUAGES && missingLanguages.add(lang.getCode()))
				System.out.println("Language not found: " + lang.getCode() + "\t" + lang.getName());
			return lang.getCode(); // return the original code. 
		} else
			return result;
	}
	
	/**
	 * Maps Wiktionary PartOfSpeech to LMF PartOfSpeech
	 * @param pos
	 */
	public static EPartOfSpeech mapPos(final IWiktionaryEntry entry) {
		switch (entry.getPartOfSpeech()) {
			case NOUN:
				return EPartOfSpeech.noun;
			case VERB:
				return EPartOfSpeech.verb;
			case ADJECTIVE:
				return EPartOfSpeech.adjective;
			case ADVERB:
				return EPartOfSpeech.adverb;
			
			case NUMBER:
			case NUMERAL:
				return EPartOfSpeech.numeral;
	
			case AUXILIARY_VERB:
				return EPartOfSpeech.verbAuxiliary;

			case ARTICLE:
			case DETERMINER:
				return EPartOfSpeech.determiner;

			case PROPER_NOUN:
			case TOPONYM: // further distinguished by Semantic Labels.
				return EPartOfSpeech.nounProper;
			case FIRST_NAME:
				return EPartOfSpeech.nounProperFirstName;
			case LAST_NAME:
				return EPartOfSpeech.nounProperLastName;
			case SINGULARE_TANTUM: // further distinguished by Semantic Labels.
			case PLURALE_TANTUM: // further distinguished by Semantic Labels.
				return EPartOfSpeech.noun;
				
			case CONJUNCTION:
				return EPartOfSpeech.conjunction;
			case SUBORDINATOR:
				return EPartOfSpeech.conjunctionSubordinating;

			case PREPOSITION:
				return EPartOfSpeech.adpositionPreposition;
			case POSTPOSITION:
				return EPartOfSpeech.adpositionPostposition;
			
			case INTERJECTION: 
			case SALUTATION: // further distinguished by Semantic Labels.
			case ONOMATOPOEIA: // further distinguished by Semantic Labels.
				return EPartOfSpeech.interjection;				

			case PHRASE:
			case IDIOM: // further distinguished by Semantic Labels.
			case COLLOCATION: // further distinguished by Semantic Labels.
			case PROVERB: // further distinguished by Semantic Labels.
			case MNEMONIC: // further distinguished by Semantic Labels.
			case NOUN_PHRASE: // not used anymore!
				return EPartOfSpeech.phraseme;

			case PRONOUN:
				return EPartOfSpeech.pronoun;
			case PERSONAL_PRONOUN:
				return EPartOfSpeech.pronounPersonal;
			case REFLEXIVE_PRONOUN:
				return EPartOfSpeech.pronounPersonalReflexive;
			case DEMONSTRATIVE_PRONOUN:
				return EPartOfSpeech.pronounDemonstrative;
			case INDEFINITE_PRONOUN:
				return EPartOfSpeech.pronounIndefinite;
			case POSSESSIVE_PRONOUN:
				return EPartOfSpeech.pronounPossessive;
			case RELATIVE_PRONOUN:
				return EPartOfSpeech.pronounRelative;
			case INTERROGATIVE_ADVERB:
			case INTERROGATIVE_PRONOUN:
				return EPartOfSpeech.pronounInterrogative;
				
			case PARTICLE:
			case MODAL_PARTICLE: // further distinguished by Semantic Labels.
			case FOCUS_PARTICLE: // further distinguished by Semantic Labels.
			case INTENSIFYING_PARTICLE: // further distinguished by Semantic Labels.
				return EPartOfSpeech.particle;
			case NEGATIVE_PARTICLE:
				return EPartOfSpeech.particleNegative;
			case COMPARATIVE_PARTICLE:
				return EPartOfSpeech.particleComparative;
			case ANSWERING_PARTICLE:
				return EPartOfSpeech.particleAnswer;
				
			case ABBREVIATION:
				return EPartOfSpeech.abbreviation;
			case INITIALISM:
				return EPartOfSpeech.abbreviationInitialism;
			case ACRONYM:
				return EPartOfSpeech.abbreviationAcronym;
				
			case AFFIX:
			case LEXEME: // = bound lexeme
			case PLACE_NAME_ENDING:
				return EPartOfSpeech.affix;
			case PREFIX:
				return EPartOfSpeech.affixPrefix;
			case SUFFIX:
				return EPartOfSpeech.affixSuffix;
				
			case CONTRACTION:
			case EXPRESSION:
				return EPartOfSpeech.contraction;
				
			case LETTER:
			case SYMBOL:
			case CHARACTER:
			case PUNCTUATION_MARK:
			case GISMU:
			case HANZI:
			case HIRAGANA:
			case KANJI:
			case KATAKANA:
				return EPartOfSpeech.symbol;

			case TRANSLITERATION:
			case WORD_FORM:
			case PARTICIPLE:

			case COMBINING_FORM:
			case MEASURE_WORD: // not used
			case UNKNOWN:
			case UNSPECIFIED:
				if (PRINT_MISSING_POS)
					System.out.println("Unknown POS: " + entry.getWord() + "/" + entry.getPartOfSpeech());
				return null;
				
		}
		return null;
	}
	/**
	 * Maps Wiktionary Gender to LMF GrammaticalGender
	 * @param gender
	 */
	public static EGrammaticalGender mapGender(Gender gender){
		switch (gender) {
			case MASCULINE:
				return EGrammaticalGender.masculine;
			case FEMININE:
				return EGrammaticalGender.feminine;
			case NEUTER:
				return EGrammaticalGender.neuter;
			case UNDEFINED:
				return null;
		}
		return null;
	}
	
	/**
	 * Maps Wiktionary relation type to LMF ERelTypeSemantics
	 * @param relationType
	 */
	public static ERelTypeSemantics mapRelationType(final RelationType relationType) {
		switch (relationType) {
			case SYNONYM:
				return ERelTypeSemantics.association;
			case ANTONYM:
				return ERelTypeSemantics.complementary;
			case HYPERNYM:
			case HYPONYM:
				return ERelTypeSemantics.taxonomic;
			case MERONYM:
			case HOLONYM:
				return ERelTypeSemantics.partWhole;
			
			case TROPONYM:
				return ERelTypeSemantics.taxonomic;
			case COORDINATE_TERM:
				return ERelTypeSemantics.taxonomic;
			case SEE_ALSO:
				return ERelTypeSemantics.association;
			//TODO: other types?
			default:
				return null;
		}		
	}

	/**
	 * Maps Wiktionary relation name to LMF ERelNameSemantics
	 * @param relationType
	 */
	public static String mapRelationName(final RelationType relationType) {
		switch (relationType) {
			case SYNONYM:
				return ERelNameSemantics.SYNONYM;
			case ANTONYM:
				return ERelNameSemantics.ANTONYM;
			case HYPERNYM:
				return ERelNameSemantics.HYPERNYM;
			case HYPONYM:
				return ERelNameSemantics.HYPONYM;
			case MERONYM:
				return ERelNameSemantics.MERONYM;
			case HOLONYM:
				return ERelNameSemantics.HOLONYM;
				
			case COORDINATE_TERM:
				return "cohyponym";
			case TROPONYM:
				return "troponym";
			case SEE_ALSO:
				return ERelNameSemantics.RELATED;

			//TODO: other types?
			default:
				return null;
		}
	}

	/**
	 * Maps Wiktionary relation name to LMF ERelTypeMorphology
	 * @param relationType
	 */
	public static ERelTypeMorphology mapMorphologicalRelation(final RelationType relationType) {
		switch (relationType) {
			case CHARACTERISTIC_WORD_COMBINATION:
				return null;
			case DERIVED_TERM:
				return ERelTypeMorphology.derivative;
			case DESCENDANT:
				return ERelTypeMorphology.etymology;
			case ETYMOLOGICALLY_RELATED_TERM:
				return ERelTypeMorphology.etymology;
//			case SEE_ALSO:
//				return null;
			default:
				return null;
		}
	}
	
}
