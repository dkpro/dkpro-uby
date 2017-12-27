/*******************************************************************************
 * Copyright 2017
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OWLanguage;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;


/**
 * This class generates LexicalEntries
 * @author Michael Matuschek
 *
 */
public class LexicalEntryGenerator {
	private  int GlobalLanguage;
	private  String GlobalLanguageLMF;
	private final OmegaWiki  omegawiki;
	private final Lexicon  lexicon;

	/*
	 * LexemeGroups with same Lemma
	 * corresponding LexicalEntry is also mapped
	 */
	private  Map<Set<SynTrans>, LexicalEntry> lexemeGroupLexicalEntryMaping;
	/*
	 * Mapping from OW to Uby POS
	 */

	private  final HashMap<String, EPartOfSpeech> posMappings = new HashMap<String, EPartOfSpeech>();

	private final  HashMap<String,HashMap<String, HashSet<SynTrans>>> posLemmaLexemeGroup = new HashMap<String, HashMap<String, HashSet<SynTrans>>>();

	private final  Map<SynTrans, Set<SynTrans>> lexemeToGroupMappings = new HashMap<SynTrans, Set<SynTrans>> ();

	/*
	 * All generated LexicalEntries
	 */
	private final List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();

	private int lexicalEntryNumber;

	private  boolean initialized=false; // True when lexicalEntryGenerator is initialized

	private  SenseGenerator senseGenerator; // SenseGenerator

//	private final String resourceVersion;

	/**
	 * Constructs a LexicalEntryGenerator
	 * based on consumed OmegaWiki Dictionary
	 * @param omegawiki
	 * @param synsetGenerator
	 * @param lexicon
	 * @param resourceVersion Version of the resource
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	public LexicalEntryGenerator(OmegaWiki omegawiki, SynsetGenerator synsetGenerator, 
			Lexicon lexicon, String resourceVersion) throws UnsupportedEncodingException, OmegaWikiException {
		this.omegawiki = omegawiki;
		this.lexicon=lexicon;
//		this.resourceVersion = resourceVersion;
		if(!initialized){
			this.GlobalLanguage=synsetGenerator.getGlobalLanguage();
			this.GlobalLanguageLMF=synsetGenerator.getGlobalLanguageLMF();
			lexicalEntryNumber = 0;
			groupLexemes();

			// Put the POS mappings to map an EPartOfSpeech
			posMappings.put("noun", EPartOfSpeech.noun);
			posMappings.put("verb", EPartOfSpeech.verb);
			posMappings.put("adjective", EPartOfSpeech.adjective);
			posMappings.put("adverb", EPartOfSpeech.adverb);
			posMappings.put("personal pronoun", EPartOfSpeech.pronounPersonal);
			posMappings.put("indefinite article", EPartOfSpeech.determinerIndefinite);
			posMappings.put("demonstrative", EPartOfSpeech.pronoun);
			posMappings.put("definite article", EPartOfSpeech.determiner);
			posMappings.put("conjunction", EPartOfSpeech.conjunction);
			posMappings.put("cardinal number", EPartOfSpeech.numeral);
			posMappings.put("preposition", EPartOfSpeech.adpositionPreposition);
			posMappings.put("independent verb", EPartOfSpeech.verb);
			posMappings.put("determined cardinal", EPartOfSpeech.numeral);
			posMappings.put("contraction", EPartOfSpeech.particle);
			posMappings.put("pronoun", EPartOfSpeech.pronoun);
			posMappings.put("interjection", EPartOfSpeech.interjection);
			posMappings.put("article", EPartOfSpeech.determiner);
			posMappings.put("determiner", EPartOfSpeech.determiner);
			posMappings.put("subjunktion", EPartOfSpeech.conjunctionSubordinating);
			posMappings.put("name", EPartOfSpeech.nounProper);
			posMappings.put("transitive verb", EPartOfSpeech.verb);
			posMappings.put("numeral", EPartOfSpeech.numeral);
			posMappings.put("intransitive verb", EPartOfSpeech.verb);
			posMappings.put("interrogative pronoun", EPartOfSpeech.pronounInterrogative);
			posMappings.put("indefinite pronoun", EPartOfSpeech.pronoun);
			posMappings.put("impersonal verb", EPartOfSpeech.verb);
			posMappings.put("relative pronou", EPartOfSpeech.pronoun);
			posMappings.put("possessive pronoun", EPartOfSpeech.pronoun);
			posMappings.put("reflexive verb", EPartOfSpeech.verb);
			posMappings.put("prefix", EPartOfSpeech.particle);
			posMappings.put("suffix", EPartOfSpeech.particle);
			posMappings.put("exclaiming pronoun", EPartOfSpeech.pronoun);
			posMappings.put("Determinativpronomen", EPartOfSpeech.pronoun);

			posMappings.put("aanwijzend voornaamwoord", EPartOfSpeech.pronounDemonstrative);
			posMappings.put("adjective", EPartOfSpeech.adjective);
			posMappings.put("name", EPartOfSpeech.nounProper);
			posMappings.put("betrekkelijk voornaamwoord", EPartOfSpeech.pronounRelative);
			posMappings.put("koppelwerkwoord", EPartOfSpeech.verb);
			posMappings.put("noun", EPartOfSpeech.noun);
			posMappings.put("soortnaam", EPartOfSpeech.noun);
			posMappings.put("telwoord", EPartOfSpeech.numeral);
			posMappings.put("onbepaald lidwoord", EPartOfSpeech.determinerIndefinite);
			posMappings.put("article", EPartOfSpeech.determiner);
			posMappings.put("auxiliary verb", EPartOfSpeech.verbAuxiliary);
			posMappings.put("bezittelijk voornaamwoord", EPartOfSpeech.pronounPossessive);
			posMappings.put("subjunktion", EPartOfSpeech.conjunctionSubordinating);
			posMappings.put("tussenwerpsel", EPartOfSpeech.interjection);
			posMappings.put("bepaald lidwoord", EPartOfSpeech.determinerDefinite);
			posMappings.put("onbepaald voornaamwoord", EPartOfSpeech.pronounIndefinite);
			posMappings.put("voegwoord", EPartOfSpeech.conjunction);
			posMappings.put("bepaald hoofdtelwoord", EPartOfSpeech.numeral);
			posMappings.put("onbepaald hoofdtelwoord", EPartOfSpeech.numeral);
			posMappings.put("preposition", EPartOfSpeech.adpositionPreposition);
			posMappings.put("Ortsadverb", EPartOfSpeech.adverb);
			posMappings.put("onomatopoeia", EPartOfSpeech.interjection);
			posMappings.put("wederkerend voornaamwoord", EPartOfSpeech.pronounPersonalReflexive);
			posMappings.put("uitroepend voornaamwoord", EPartOfSpeech.pronoun);
			posMappings.put("intransitive verb", EPartOfSpeech.verb);
			posMappings.put("transitive verb", EPartOfSpeech.verb);
			posMappings.put("vragend voornaamwoord", EPartOfSpeech.pronounInterrogative);
			posMappings.put("wederkerig voornaamwoord", EPartOfSpeech.pronoun);
			posMappings.put("determiner", EPartOfSpeech.determiner);
			posMappings.put("Abstrakta", EPartOfSpeech.noun);
//			posMappings.put("ナ形容詞", EPartOfSpeech.symbol);
//			posMappings.put("adnominal", EPartOfSpeech.adjective);
//			posMappings.put("adposition", EPartOfSpeech.symbol);
			posMappings.put("Richtungsadverb", EPartOfSpeech.adverb);
			posMappings.put("adverb", EPartOfSpeech.adverb);
			posMappings.put("Kausaladverb", EPartOfSpeech.adverb);
			posMappings.put("verb", EPartOfSpeech.verb);
			posMappings.put("Sammelname", EPartOfSpeech.noun);
			posMappings.put("Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("disjunktives Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("kausales Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("konzessives Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("adversatives Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("konsekutives Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("kopulatives Konjunktionaladverb", EPartOfSpeech.adverb);
			posMappings.put("measure word", EPartOfSpeech.numeral);
			posMappings.put("demonstrative", EPartOfSpeech.determinerDemonstrative);
			posMappings.put("Ausdruckswort", EPartOfSpeech.interjection);
			posMappings.put("Modaladverb", EPartOfSpeech.adverb);
//			posMappings.put("終助詞", EPartOfSpeech.symbol);
			posMappings.put("formal noun", EPartOfSpeech.noun);
			posMappings.put("Grußwort", EPartOfSpeech.interjection);
//			posMappings.put("イ形容詞", EPartOfSpeech.symbol);
			posMappings.put("personal pronoun", EPartOfSpeech.pronounPersonal);
			posMappings.put("Interrogativadverb", EPartOfSpeech.adverb);
			posMappings.put("contraction", EPartOfSpeech.contraction);
			posMappings.put("Lokaladverb", EPartOfSpeech.adverb);
			posMappings.put("Stoffname", EPartOfSpeech.noun);
//			posMappings.put("動作名詞", EPartOfSpeech.symbol);
			posMappings.put("nominativ gebrauchtes Verb", EPartOfSpeech.noun);
			posMappings.put("Partikel der Bejahung oder Verneinung", EPartOfSpeech.particleAnswer);
//			posMappings.put("助詞", EPartOfSpeech.symbol);
			posMappings.put("pronoun", EPartOfSpeech.pronoun);
			posMappings.put("interrogative word", EPartOfSpeech.pronounInterrogative);
			posMappings.put("Satzadverb", EPartOfSpeech.adverb);
			posMappings.put("Temporaladverb", EPartOfSpeech.adverb);
//			posMappings.put("bacru", EPartOfSpeech.symbol);
//			posMappings.put("outrecuidant", EPartOfSpeech.symbol); arrogant
			posMappings.put("Determinativpronomen", EPartOfSpeech.pronoun);
//			posMappings.put("grammatical property", EPartOfSpeech.symbol);
			posMappings.put("Oronym", EPartOfSpeech.nounProper);
			posMappings.put("Nomen", EPartOfSpeech.noun);
			posMappings.put("coordonnant", EPartOfSpeech.conjunctionCoordinating);
			posMappings.put("subordinating conjunction", EPartOfSpeech.conjunctionSubordinating);
			posMappings.put("proword", EPartOfSpeech.pronoun);
			posMappings.put("proword for verb", EPartOfSpeech.pronoun);
			posMappings.put("common noun", EPartOfSpeech.nounCommon);
//			posMappings.put("stupid", EPartOfSpeech.symbol);

			senseGenerator = new SenseGenerator(synsetGenerator,resourceVersion);

			createLexicalEntries();
			initialized = true;
		}
	}

	/**
	 * This method groups all Lexemes (SynTranses) by lemma
	 *
	 * Optimization potential here with a dedicated method for retrieving SynTranses = Lexemes
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException, OmegaWikiException
	 */
	@SuppressWarnings("unchecked")
	private void groupLexemes() throws UnsupportedEncodingException, OmegaWikiException {
		int overall = 0;
		int current = 0;
		lexemeGroupLexicalEntryMaping= new HashMap<Set<SynTrans>, LexicalEntry>();
		Iterator<DefinedMeaning> dmIter = null; // DefinedMeaning iterator
			HashMap<String, HashSet<SynTrans>>lemmaLexemeGroup = new HashMap<String, HashSet<SynTrans>>();
			try {
				Set<DefinedMeaning> defMeanings = omegawiki.getAllDefinedMeanings(this.GlobalLanguage); 
				dmIter = defMeanings.iterator();
				overall = defMeanings.size();
			} catch (Exception e) {
				e.printStackTrace();
			}
			while(dmIter.hasNext()){
				if(current++ % 1000 == 0)
				{
					System.out.println("Grouping lexemes... " + ((current * 100) / overall) + "%");
				}
				DefinedMeaning dm = dmIter.next();
				String pos="";
				Set<SynTrans> lexemes = dm.getSynTranses(GlobalLanguage);// lexemes of the Synset = SynTranses in the desired language
				//Distinction by variants of English
				if(OWLanguage.English == GlobalLanguage)
				{
					for(SynTrans stuk : dm.getSynTranses(OWLanguage.English_United_Kingdom))
					{
						@SuppressWarnings("rawtypes")
						HashSet toAdd = new HashSet();
						boolean found = false;
						for (SynTrans orig : lexemes)
						{

							if(orig.getSyntrans().getSpelling().equals( stuk.getSyntrans().getSpelling()))
							{
							 found = true;
							 break;
							}
							if(!found) {
								toAdd.add(stuk);
							}
						}
						lexemes.addAll(toAdd);
					}
					for(SynTrans stus : dm.getSynTranses(OWLanguage.English_United_States))
					{
						@SuppressWarnings("rawtypes")
						HashSet toAdd = new HashSet();
						boolean found = false;
						for (SynTrans orig : lexemes)
						{

							if(orig.getSyntrans().getSpelling().equals( stus.getSyntrans().getSpelling()))
							{
							 found = true;
							 break;
							}
							if(!found) {
								toAdd.add(stus);
							}
						}
						lexemes.addAll(toAdd);
					}

				}
				//Handle unknown POS properly
				for(SynTrans lexeme : lexemes){
					pos = (lexeme.getPOS()==null? "unknown": lexeme.getPOS().getValue());
					if((lemmaLexemeGroup = posLemmaLexemeGroup.get(pos)) == null)
						{
						lemmaLexemeGroup = new HashMap<String, HashSet<SynTrans>>();
						}


					HashSet<SynTrans> lexemeGroup; // Group of Lexemes with the same lemma
					String lemma = lexeme.getSyntrans().getSpelling(); // Lemma's Lexeme
					//
					if((lexemeGroup = lemmaLexemeGroup.get(lemma)) == null){
						lexemeGroup = new HashSet<SynTrans>();
						lemmaLexemeGroup.put(lemma, lexemeGroup);
					}
					lexemeGroup.add(lexeme);
					lexemeToGroupMappings.put(lexeme, lexemeGroup);
					posLemmaLexemeGroup.put(pos, lemmaLexemeGroup);

				}



			}

		System.out.println("Grouping lexemes... done");
	}

	/**
	 * This method iterates over All lexemeGroups and
	 * creates a List of LexicalEntries
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	private void createLexicalEntries() throws UnsupportedEncodingException, OmegaWikiException{
		System.out.println("Transforming lexeme groups... 0%");
		for (Entry<String, HashMap<String, HashSet<SynTrans>>> pos : posLemmaLexemeGroup.entrySet())
		{
			int current = 0;
			int overall = pos.getValue().size();
			for(Entry<String, HashSet<SynTrans>> lemmaSet : pos.getValue().entrySet())
			{
				Set<SynTrans> sts = lemmaSet.getValue();
				if(current++ % 1000 == 0)
					System.out.println("Transforming lexeme groups " + pos.getKey() + "... " + ((current * 100) / overall) + "%");
				LexicalEntry lexicalEntry = createLexicalEntry(sts, pos.getKey(), lemmaSet.getKey(), lexicon);
				lexicalEntries.add(lexicalEntry);
				lexemeGroupLexicalEntryMaping.put(sts, lexicalEntry);
			}
		}
		System.out.println("Transforming lexeme groups... done");
	}

	/**
	 * This method consumes a lexemeGroup and returns
	 * the corresponding LexicalEntry
	 * @param lexemeGroup
	 * @param pos
	 * @param ow_lemma
	 * @param lexicon
	 * @return LexicalEntry that corresponds to lexemeGroup
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	private LexicalEntry createLexicalEntry(Set<SynTrans> lexemeGroup, String pos, String ow_lemma,Lexicon lexicon) throws UnsupportedEncodingException, OmegaWikiException {
		// Create a new LexicalEntry for this group of lexemes
		LexicalEntry lexicalEntry = new LexicalEntry();
		lexicalEntry.setLexicon(lexicon);
		// Create ID for this lexicalEntry
		lexicalEntry.setId(createID());
		lexicalEntry.setPartOfSpeech(getPOS(pos));

		//*** Creating Lemma ***//
		Lemma lemma = new Lemma();
		List<FormRepresentation> formRepresentations = new LinkedList<FormRepresentation>();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setLanguageIdentifier(OmegaWikiLMFMap.mapLanguage(GlobalLanguage));
		formRepresentation.setWrittenForm(ow_lemma);
		formRepresentations.add(formRepresentation);
		for(SynTrans st : lexemeGroup)
		{
			if(st.getSyntrans().getLanguageId()!=GlobalLanguage)
			{
				formRepresentation.setGeographicalVariant(OWLanguage.getName(st.getSyntrans().getLanguageId()));
			}
		}

		lemma.setFormRepresentations(formRepresentations);
		lexicalEntry.setLemma(lemma);

		//*** Creating Senses ***//
		lexicalEntry.setSenses(senseGenerator.generateSenses(lexemeGroup,lexicalEntry));
		lexicalEntry.getSenses();

		return lexicalEntry;
	}

	/**
	 * This method return the Uby Pos for a OW pos
	 * @param pos
	 * @return Uby Pos
	 */

	private EPartOfSpeech getPOS(String pos) {
		EPartOfSpeech result = posMappings.get(pos);
		return result;
	}

	/**
	 * This method creates an ID for a LexicalEntry
	 * @return ID for lexicalEntry
	 */
	private String createID() {
		StringBuffer sb = new StringBuffer(32);
		sb.append("OW_"+GlobalLanguageLMF+"_LexicalEntry_").append(lexicalEntryNumber++);
		return sb.toString();
	}

	/**
	 * @return the lexicalEntries
	 */
	public  List<LexicalEntry> getLexicalEntries() {
		return lexicalEntries;
	}

	/**
	 * Returns a LexicalEntry that corresponds to the consumed lexemeGroup
	 * @param lexemeGroup
	 * @return the LexicalEntry that corresponds to the consumed lexemeGroup
	 */
	public LexicalEntry getLexicalEntry(Set<SynTrans> lexemeGroup){
		return lexemeGroupLexicalEntryMaping.get(lexemeGroup);
	}

	/**
	 * @return the lexemeGroupLexicalEntryMaping
	 */
	public Map<Set<SynTrans>, LexicalEntry> getLexemeGroupLexicalEntryMaping() {
		return lexemeGroupLexicalEntryMaping;
	}

	/**
	 * @return the senseGenerator
	 */
	public  SenseGenerator getSenseGenerator() {
		return senseGenerator;
	}

	/**
	 * Returns the lexemeGroup of the consumed lexeme
	 */
	public Set<SynTrans> getGroup(SynTrans lexeme){
		return lexemeToGroupMappings.get(lexeme);
	}

}
