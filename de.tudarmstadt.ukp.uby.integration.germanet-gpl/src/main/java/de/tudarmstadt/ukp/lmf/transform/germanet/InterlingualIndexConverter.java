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
package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tuebingen.uni.sfs.germanet.api.EwnRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.IliRecord;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;

/**
 * This class contains methods for converting the German part of the Interlingual Index (ILI),
 * provided in <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet</a>,
 * to {@link SenseAxis} instances.
 *
 * @since UBY 0.2.0
 *
 *
 */
public class InterlingualIndexConverter {

	private final GNConverter gnConverter;
	private final GermaNet gnet;
	private final Lexicon wordNetLexicon;
	private final List<SenseAxis> senseAxes = new ArrayList<SenseAxis>();

	// UBY-LMF synsets sorted by external reference
	private final Map<EPartOfSpeech, Map<Long, Synset>> synsetMappings = new HashMap<EPartOfSpeech, Map<Long,Synset>>();

	//  mappings between part of speech keys and part of speech specified by Uby-LMF
	private static final Map<String, EPartOfSpeech> _posKeyMappings = new HashMap<String, EPartOfSpeech>();

	static{
		// Put the POS mappings pos Key <-> EPartOfSpeech
		_posKeyMappings.put("n", EPartOfSpeech.noun);
		_posKeyMappings.put("v", EPartOfSpeech.verb);
		_posKeyMappings.put("a", EPartOfSpeech.adjective);
		_posKeyMappings.put("r", EPartOfSpeech.adverb);
	}


	//  mappings between part of speech labels and part of speech specified by Uby-LMF
	private static final Map<String, EPartOfSpeech> _posLabelMappings = new HashMap<String, EPartOfSpeech>();

	static{
		// Put the POS mappings pos label <-> EPartOfSpeech
		_posLabelMappings.put("noun", EPartOfSpeech.noun);
		_posLabelMappings.put("verb", EPartOfSpeech.verb);
		_posLabelMappings.put("adjective", EPartOfSpeech.adjective);
		_posLabelMappings.put("adverb", EPartOfSpeech.adverb);
	}


	private final Logger logger = Logger.getLogger(InterlingualIndexConverter.class.getName());

	/**
	 * Constructs an instance of {@link InterlingualIndexConverter} based on the consumed
	 * parameters.
	 *
	 * @since 0.2.0
	 *
	 * @param gnConverter an instance of {@link GNConverter} associated with this generator
	 *
	 * @param gnet {@link GermaNet} instance used for accessing GermaNet data.
	 *
	 * @param wordNetLexicalResource {@link LexicalResource} instance containing
	 * <a href="URL#https://wordnet.princeton.edu/wordnet/">WordNet 3.0</a>.
	 */
	public InterlingualIndexConverter(GNConverter gnConverter, GermaNet gnet, Lexicon wordNetLexicon) {
		this.gnConverter = gnConverter;
		this.gnet = gnet;
		this.wordNetLexicon = wordNetLexicon;
	}

	/**
	 * Starts the conversion process of GermaNets Interlingual Index to {@link SenseAxis} instances.
	 * The generated sense axes can be obtained by invoking {@link #getSenseAxes()}.
	 */
	public void convert(){

		createSynsetMappings(wordNetLexicon);

		SynsetGenerator synsetGenerator = gnConverter.getSynsetGenerator();
		synsetGenerator.initialize();

		List<IliRecord> iliRecords = gnet.getIliRecords();
		int synsetAlignmentCounter = 0;
		int senseAlignmentCounter = 0;


		for(IliRecord iliRecord : iliRecords){

			EwnRel relation = iliRecord.getEwnRelation();
			if(relation.toString().equals("synonym")){
				/*
				 * Only synonyms are converted to sense axes
				 */
				String pwn30Id = iliRecord.getPwn30Id();
				String offsetString = pwn30Id.replaceAll("ENG30-", "");
				String[] temp = offsetString.split("-");
				offsetString = temp[0];

				EPartOfSpeech pos = getUbyPosFromKey(temp[1]);
				if(offsetString.contains("null")) {
					logger.warning("offsetString contains null-string for " + iliRecord);
					continue; // skip
				}


				long offset = Long.valueOf(offsetString);

				LexUnit lexUnit = gnet.getLexUnitByID(iliRecord.getLexUnitId());

				Synset gnUBYSynset = synsetGenerator.getLMFSynset(lexUnit);

				/*
				 * Obtain the UBY-LMF synset that corresponds to the WordNet 3.0 synset
				 * targeted by the ILI-record
				 */
				 Synset wnUBYSynset = synsetMappings.get(pos).get(offset);

				 if(wnUBYSynset == null){
					logger.warning("Synset for the given WordNet word could not be found. SenseAxis will not be generated." + iliRecord);
					continue; // skip
				 }
				 else{
					/*
					 * Create SenseAxis for Synset
					 */
					SenseAxis senseAxisSynset = new SenseAxis();
					senseAxisSynset.setSynsetOne(gnUBYSynset);
					senseAxisSynset.setSynsetTwo(wnUBYSynset);
					senseAxisSynset.setSenseAxisType(ESenseAxisType.crosslingualSenseAlignment);
					senseAxisSynset.setId("GN_WN_Synset_Alignment_Interlingual_Index_"+synsetAlignmentCounter++);
					senseAxes.add(senseAxisSynset);
				 }

				/*
				 * Create SenseAxis for Sense
				 */
				String pwnWord = iliRecord.getPwnWord();

				Sense gnUBYSense = gnConverter.getSynsetGenerator().getSense(lexUnit);
				Sense wnUBYSense = getSense(wnUBYSynset, pwnWord);

				if(wnUBYSense == null){
					logger.warning("Sense for the given WordNet word ##" +pwnWord +"## could not be found. SenseAxis will not be generated." + iliRecord);
					continue; // skip
				}
				else{
					SenseAxis senseAxisSense = new SenseAxis();
					senseAxisSense.setSenseOne(gnUBYSense);
					senseAxisSense.setSenseTwo(wnUBYSense);
					senseAxisSense.setSenseAxisType(ESenseAxisType.crosslingualSenseAlignment);
					senseAxisSense.setId("GN_WN_Sense_Alignment_Interlingual_Index_"+senseAlignmentCounter++);
					senseAxes.add(senseAxisSense);
				}

			}
		}
	}

	/**
	 * Consumes a {@link Synset} instance that corresponds to a WordNet 3.0 synset and
	 * a {@link String} representation of a word. It returns the
	 * first {@link Sense} instance of the consumed UBY-LMF Synset, that belongs to a
	 * LexicalEntry which has a lemma equal to the consumed word.
	 *
	 * @param wnUBYSynset synset that contains the senses to be queried
	 *
	 * @param pwnWord the returned must belong to a lexical entry with lemma equal to pwnWord
	 *
	 * @return sense that corresponds to the consumed word, or null if no sense in the
	 * consumed synset belongs to a lexical entry with lemma that is equal to pwnWord
	 */
	private Sense getSense(Synset wnUBYSynset, String pwnWord) {
		List<Sense> senses = wnUBYSynset.getSenses();
		for(Sense sense : senses) {
            for(FormRepresentation formRepresentation : sense.getLexicalEntry().getLemma().getFormRepresentations()) {
                if(formRepresentation.getWrittenForm().equals(pwnWord)) {
                    return sense;
                }
            }
        }
		return null;
	}

	/**
	 * Initializes {@link #synsetMappings} field. The field makes an efficient search for a
	 * {@link Synset} possible, for a given {@link EPartOfSpeech} and WordNet 3.0 synset offset.
	 *
	 * @param wordNetLexicon {@link Lexicon} used for extracting the mappings
	 */
	private void createSynsetMappings(Lexicon wordNetLexicon) {

		List<Synset> synsets = wordNetLexicon.getSynsets();

		synsetMappings.put(EPartOfSpeech.noun, new HashMap<Long, Synset>());
		synsetMappings.put(EPartOfSpeech.verb, new HashMap<Long, Synset>());
		synsetMappings.put(EPartOfSpeech.adjective, new HashMap<Long, Synset>());
		synsetMappings.put(EPartOfSpeech.adverb, new HashMap<Long, Synset>());

		for(Synset synset : synsets){
			MonolingualExternalRef monolingualExternalRef = synset.getMonolingualExternalRefs().get(0);
			String posOffset = monolingualExternalRef.getExternalReference();
			String[] temp = posOffset.split("]");

			EPartOfSpeech pos = getUbyPosFromLabel(temp[0].split(" ")[1]);

			String stringOffset = temp[1].trim();
			if(stringOffset.contains("null")) {
				logger.warning("stringOffset contains null-string");
				continue; // skip
			}

			long offset = Long.valueOf(stringOffset);
			Map<Long, Synset> mapping = synsetMappings.get(pos);
			mapping.put(offset, synset);
		}
	}

	/**
	 * This method consumes a {@link String}
	 * and returns corresponding {@link EPartOfSpeech}
	 * @param POS string as key, e.g. n, v, a
	 * @return associated part of speech defined in UBY-LMF
	 * @since 0.7.0
	 */
	private static EPartOfSpeech getUbyPosFromKey(String pos) {
		EPartOfSpeech result = _posKeyMappings.get(pos);
		return result;
	}

	/**
	 * This method consumes a {@link String}
	 * and returns corresponding {@link EPartOfSpeech}
	 * @param POS string as label, e.g. noun, verb
	 * @return associated part of speech defined in UBY-LMF
	 * @since 0.7.0
	 */
	private static EPartOfSpeech getUbyPosFromLabel(String pos) {
		EPartOfSpeech result = _posLabelMappings.get(pos);
		return result;
	}


	/**
	 * Returns the {@link List} of all {@link SenseAxis} instances, generated by this {@link InterlingualIndexConverter}.
	 *
	 * @return a list of sense axes generated by this converter, or an empty list if the converter
	 * has not generated any sense axes
	 */
	public List<SenseAxis> getSenseAxes(){
		return this.senseAxes;
	}

}
