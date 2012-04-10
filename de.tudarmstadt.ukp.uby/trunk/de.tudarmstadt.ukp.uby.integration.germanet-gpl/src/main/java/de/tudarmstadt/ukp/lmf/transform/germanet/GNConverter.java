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

import germanet.GermaNet;
import germanet.LexUnit;
import germanet.WordCategory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;

/**
 * 
 * Instance of this class converts
 * <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/">GermaNet 6.0</a>
 * to LMF-format
 * @author Zijad Maksuti, Judith Eckle-Kohler
 *
 */
public class GNConverter {
	
	private GermaNet gnet; // GermaNet Object
	
	private LexicalResource lexicalResource;
	
	private InputStream subcatStream; // subcat mapping file
	
	/*
	 * Groups of LexUnits with equal lemma and part of speech
	 */
	private HashSet<HashSet<LexUnit>> luGroups;
	
	private SubcategorizationFrameExtractor subcategorizationFrameExtractor;
	
	private SynsetGenerator synsetGenerator;
	
	private String dtd_version;
	
	private Logger logger = Logger.getLogger(GNConverter.class.getName());

	/**
	 * Constructs a {@link GNConverter} based on the consumed parameters
	 * @param germaNet initialized {@link GermaNet} object
	 * @param lexicalResource initialized object of  {@link LexicalResource}, which will be filled with GermaNet's data 
	 * @param dtd_version specifies the version of the .dtd which will be written to lexicalResource
	 */
	public GNConverter(GermaNet germaNet, LexicalResource lexicalResource, String dtd_version)
	{
		this.gnet = germaNet;
		this.lexicalResource = lexicalResource;
		this.dtd_version = dtd_version;
		
		try {
			this.subcatStream = getClass().getClassLoader().getResource("GermaNetSubcatMappings/gnFrameMapping.txt").openStream();
			subcategorizationFrameExtractor = new SubcategorizationFrameExtractor(subcatStream);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "GNConverter: unable to load subcat mapping file. Aborting all operations");
			System.exit(1);
		}
		
		this.synsetGenerator = new SynsetGenerator(this.gnet);
	}

	/**
	 * Converts the informations provided by the initialized {@link GermaNet} object to LMF-format. <br> 
	 * The result of the conversion can be obtained by calling {@link GNConverter#getLexicalResource()}
	 */
	public void toLMF() {
		
		// Setting attributes of LexicalResource
		lexicalResource.setName("GermaNet");
		lexicalResource.setDtdVersion(dtd_version);
		
		// *** Setting GlobalInformation *** //
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("LMF representation of GermaNet 6.0");
		lexicalResource.setGlobalInformation(globalInformation);
		
		//*** Setting Lexicon (only one since GermaNet is monolingual)***//
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(ELanguageIdentifier.de);
		lexicon.setId("GN_Lexicon_0");
		lexicon.setName("GermaNet");
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(lexicon);
		lexicalResource.setLexicons(lexicons);
		
		// *** Creating LexicalEntries *** //
		logger.log(Level.INFO, "Generating LexicalEntries...");
		this.groupLUs();
		LexicalEntryGenerator leGen = new LexicalEntryGenerator(this);
		List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
		// Create a LexicalEntry for each luGroup
		for (HashSet<LexUnit> luGroup : luGroups)
			lexicalEntries.add(leGen.createLexicalEntry(luGroup));
		// Setting RelatedForms of LexicalEntries
		for(LexicalEntry lexicalEntry : lexicalEntries)
			leGen.setRelatedForms(lexicalEntry);
		// appending lexicalEntries
		lexicon.setLexicalEntries(lexicalEntries);
		StringBuffer sb = new StringBuffer(64);
		sb.append("Generated LexicalEntries: ").append(lexicalEntries.size());
		logger.log(Level.INFO, sb.toString());
		int noVerbs = 0;
		int noVerbSenses = 0;
		for (LexicalEntry le : lexicalEntries) {
			if (le.getPartOfSpeech().equals(EPartOfSpeech.verb)) {
				noVerbs++;
				noVerbSenses = noVerbSenses + le.getSenses().size();
			}
		}
		sb = new StringBuffer(128);
		sb.append("Generated verb lemmas: ").append(noVerbs).append('\n');
		sb.append("Generated verb senses: ").append(noVerbSenses);
		logger.log(Level.INFO, sb.toString());
		
		// *** Appending SubcategorizationFrames *** //
		lexicon.setSubcategorizationFrames(subcategorizationFrameExtractor.getSubcategorizationFrames());
		
		// *** Appending SemanticPredicates *** //
		lexicon.setSemanticPredicates(subcategorizationFrameExtractor.getSemanticPredicates());
		
		// *** Appending Synsets *** //
		synsetGenerator.initialize();
		List<Synset> synsets = synsetGenerator.getSynsets();
		lexicon.setSynsets(synsets);
		sb = new StringBuffer(64);
		sb.append("Generated synsets: ").append(synsets.size());
		logger.log(Level.INFO, sb.toString());
		
		// *** Appending SynSemCorrespondences *** //
		lexicon.setSynSemCorrespondences(subcategorizationFrameExtractor.getSynSemCorrespondences());
	}

	/**
	 * This method groups all LexUnits by lemma and {@link WordCategory}
	 * @see LexUnit
	 */
	private void groupLUs() {
		if (luGroups == null)
			luGroups = new HashSet<HashSet<LexUnit>>();
		for(WordCategory pos : WordCategory.values()){
			List<LexUnit> lus = gnet.getLexUnits(pos);
			HashMap<String, HashSet<LexUnit>> orthFormLUGroupMappings = new HashMap<String, HashSet<LexUnit>>();
			for (LexUnit lu : lus) {
				String orthForm = lu.getOrthForm();
				HashSet<LexUnit> luGroup = orthFormLUGroupMappings.get(orthForm);
				if(luGroup == null)
					luGroup = new HashSet<LexUnit>();
				luGroup.add(lu);
				orthFormLUGroupMappings.put(orthForm, luGroup);
		}
		luGroups.addAll(orthFormLUGroupMappings.values());
	}
	}

	/**
	 * This method consumes a {@link LexUnit} and returns a {@link Set} of
	 * LexUnits with equal lemma and {@link WordCategory}
	 * @param lexUnit an instance of LexUnit for which
	 *  a Set of LexUnits with equal lemma and WordCategory should be returned
	 * @return a set of LexUnits with equal lemma and WordCategory to consumed lexUnit
	 */
	protected HashSet<LexUnit> getLUGroup(LexUnit lexUnit){
		for(HashSet<LexUnit> luGroup : luGroups)
			if(luGroup.contains(lexUnit))
				return luGroup;
		return null;
	}
	
	/**
	 * Returns the {@link GermaNet} object associated to this {@link GNConverter} 
	 * @return GermaNet object associated to this GNConveter
	 */
	public GermaNet getGnet() {
		return gnet;
	}

	/**
	 * Returns the {@link SubcategorizationFrameExtractor} associated to this {@link GNConverter}
	 * @return the SubcategorizationFrameExtractor associated to this GNConverter
	 */
	public SubcategorizationFrameExtractor getSubcategorizationFrameExtractor() {
		return subcategorizationFrameExtractor;
	}
	
	/**
	 * Returns the {@link LexicalResource} object, which contains the results of the conversion 
	 * @return an instance of LexicalResource, which contains the results of the conversion
	 */
	public LexicalResource getLexicalResource() {
		return lexicalResource;
	}
}

