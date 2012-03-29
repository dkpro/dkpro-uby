/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.dictionary.Dictionary;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;


/**
 * 
 * Instance of this class converts
 * <a href="URL#https://wordnet.princeton.edu/wordnet/">WordNet 3.0</a>
 * to LMF-format
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class WNConverter {

	private Dictionary extWordnet; // extWordNet Dictionary

	private LexicalResource lexicalResource;

	private InputStream subcatStream; // subcat mapping file
	private File exMapping; // the file containing manually entered mappings of the lexemes and example sentences 
	private final String dtd_version;
	
	private Logger logger = Logger.getLogger(WNConverter.class.getName());


	/**
	 * Constructs a {@link WNConverter} based on the consumed parameters
	 * @param wordNet initialized WordNet's {@link Dictionary} object
	 * @param lexicalResource initialized object of  {@link LexicalResource}, which will be filled with WordNet's data 
	 * @param dtd_version specifies the version of the .dtd which will be written to lexicalResource
	 * @param exMappingPath path of the file containing manually entered mappings of lexemes and example sentences
	 */
	public WNConverter(Dictionary wordNet, LexicalResource lexicalResource, String dtd, String exMappingPath)
	{
		this.extWordnet = wordNet;
		this.lexicalResource = lexicalResource;
		this.dtd_version = dtd;

		try {
			this.subcatStream = getClass().getClassLoader().getResource("WordNetSubcatMappings/wnFrameMapping.txt").openStream();
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to load subcat mapping file. Aborting all operations");
			System.exit(1);
		}
		
		try {
			exMapping = new File(exMappingPath);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE,
					"Unable to load the file containing manually entered mappings of example sentences. Aborting all operations");
			System.exit(1);
		}
		
	}

	/**
	 * Converts the informations provided by the initialized WordNet-{@link Dictionary} instance to LMF-format. <br> 
	 * The result of the conversion can be obtained by calling {@link WNConverter#getLexicalResource()}
	 */
	public void toLMF(){
		logger.log(Level.INFO, "Started converting WordNet to LMF...");
		SubcategorizationFrameExtractor subcategorizationFrameExtractor = new SubcategorizationFrameExtractor(subcatStream);
		
		// Setting attributes of LexicalResource
		lexicalResource.setName("WordNet");
		lexicalResource.setDtdVersion(dtd_version);


		// *** Setting GlobalInformation *** //
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("LMF representation of WordNet 3.0");
		lexicalResource.setGlobalInformation(globalInformation);

		//*** Setting Lexicon (only one since WordNet is monolingual)***//
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(ELanguageIdentifier.en);
		lexicon.setId("WN_Lexicon_0");
		lexicon.setName("WordNet");
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(lexicon);
		lexicalResource.setLexicons(lexicons);

		// *** Creating Synsets *** //
		logger.log(Level.INFO, "Generating Synsets...");
		SynsetGenerator synsetGenerator = new SynsetGenerator(extWordnet, exMapping);
		synsetGenerator.initialize();
		// Setting Synsets
		lexicon.setSynsets(synsetGenerator.getSynsets());
		logger.log(Level.INFO, "Generating Synsets done");

		// *** Creating LexicalEntries *** //
		logger.log(Level.INFO, "Generating LexicalEntries...");
		LexicalEntryGenerator lexicalEntryGenerator = new LexicalEntryGenerator(extWordnet, synsetGenerator, subcategorizationFrameExtractor);
		lexicon.setLexicalEntries(lexicalEntryGenerator.getLexicalEntries());
		logger.log(Level.INFO, "Generating LexicalEntries done");

		// *** Creating SynsetRelations *** //
		logger.log(Level.INFO, "Generating SynsetRelations...");
		SynsetRelationGenerator synsetRelationGenerator = new SynsetRelationGenerator(synsetGenerator, lexicalEntryGenerator);
		// Update the relatios of previously extracted (and generated) Synsets
		synsetRelationGenerator.updateSynsetRelations();
		logger.log(Level.INFO, "Generating SynsetRelations done");

		// *** Creating RelatedForms of LexicalEntries *** //
		logger.log(Level.INFO, "Generating RelatedForms...");
		RelatedFormGenerator relatedFormGenerator = new RelatedFormGenerator(lexicalEntryGenerator);
		relatedFormGenerator.updateRelatedForms();
		logger.log(Level.INFO, "Generating RelatedForms done");

		// *** Creating SenseRelations *** //
		logger.log(Level.INFO, "Generating SenseRelations...");
		SenseRelationGenerator senseRelationGenerator = new SenseRelationGenerator(lexicalEntryGenerator);
		senseRelationGenerator.updateSenseRelations();
		logger.log(Level.INFO, "Generating SenseRelations done");

		// *** Setting SubcategorizationFrames ***//
		lexicon.setSubcategorizationFrames(subcategorizationFrameExtractor.getSubcategorizationFrames());
		// setting SemanticPredicates
		lexicon.setSemanticPredicates(subcategorizationFrameExtractor.getSemanticPredicates());
		// setting SynSemCorrespondences
		lexicon.setSynSemCorrespondences(subcategorizationFrameExtractor.getSynSemCorrespondences());

	}
	
	/**
	 * Returns the {@link LexicalResource} object, which contains the results of the conversion 
	 * @return an instance of LexicalResource, which contains the results of the conversion
	 */
	public LexicalResource getLexicalResource() {
		return this.lexicalResource;
	}

}
