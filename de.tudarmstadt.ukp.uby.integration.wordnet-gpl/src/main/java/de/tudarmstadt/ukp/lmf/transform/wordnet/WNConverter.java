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

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private final Dictionary extWordnet; // extWordNet Dictionary
	protected File dictionaryPath;

	private final LexicalResource lexicalResource;

	private InputStream subcatStream; // subcat mapping file

	private final String dtd_version;
	private final String resourceVersion;

	private final Log LOG = LogFactory.getLog(getClass());


	/**
	 * Constructs a {@link WNConverter} based on the consumed parameters
	 * @param dictionaryPath the path of the WordNet dictionary files
	 * @param wordNet initialized WordNet's {@link Dictionary} object
	 * @param lexicalResource initialized object of  {@link LexicalResource}, which will be filled with WordNet's data
	 * @param resourceVersion Version of this resource
	 * @param dtd_version specifies the version of the .dtd which will be written to lexicalResource
	 * @param exMappingPath path of the file containing manually entered mappings of lexemes and example sentences
	 */
	public WNConverter(final File dictionaryPath, final Dictionary wordNet,
			final LexicalResource lexicalResource, final String resourceVersion,
			final String dtd) {
		this.dictionaryPath = dictionaryPath;
		this.extWordnet = wordNet;
		this.lexicalResource = lexicalResource;
		this.resourceVersion = resourceVersion;
		this.dtd_version = dtd;
		try {
			this.subcatStream = getClass().getClassLoader().getResource("WordNetSubcatMappings/wnFrameMapping.txt").openStream();
		} catch (Exception e) {
			LOG.error("Unable to load subcat mapping file. Aborting all operations");
			System.exit(1);
		}
	}

	/** @deprecated Use alternative constructor instead! */
	@Deprecated
	public WNConverter(File dictionaryPath, Dictionary wordNet, LexicalResource lexicalResource, String resourceVersion,
			String dtd, String exMappingPath) {
		this(dictionaryPath, wordNet, lexicalResource, resourceVersion, dtd);
		/*try {
			File exMapping = new File(exMappingPath);
		} catch (Exception e) {
			LOG.error(
					"Unable to load the file containing manually entered mappings of example sentences. Aborting all operations");
			System.exit(1);
		}*/
	}

	/**
	 * Converts the informations provided by the initialized WordNet-{@link Dictionary} instance to LMF-format. <br>
	 * The result of the conversion can be obtained by calling {@link WNConverter#getLexicalResource()}
	 */
	public void toLMF() {
		try {
			LOG.info("Started converting WordNet to LMF...");
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
			lexicon.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
			lexicon.setId("WN_Lexicon_0");
			lexicon.setName("WordNet");
			LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
			lexicons.add(lexicon);
			lexicalResource.setLexicons(lexicons);

			// *** Creating Synsets *** //
			LOG.info("Generating Synsets...");
			SynsetGenerator synsetGenerator = new SynsetGenerator(extWordnet, resourceVersion);
			synsetGenerator.initialize();
			// Setting Synsets
			lexicon.setSynsets(synsetGenerator.getSynsets());
			LOG.info("Generating Synsets done");

			// *** Creating LexicalEntries *** //
			LOG.info("Generating LexicalEntries...");
			LexicalEntryGenerator lexicalEntryGenerator = new LexicalEntryGenerator(dictionaryPath, extWordnet,
					synsetGenerator, subcategorizationFrameExtractor, resourceVersion);
			lexicon.setLexicalEntries(lexicalEntryGenerator.getLexicalEntries());
			LOG.info("Generating LexicalEntries done");

			// *** Creating SynsetRelations *** //
			LOG.info("Generating SynsetRelations...");
			SynsetRelationGenerator synsetRelationGenerator = new SynsetRelationGenerator(synsetGenerator, lexicalEntryGenerator);
			// Update the relatios of previously extracted (and generated) Synsets
			synsetRelationGenerator.updateSynsetRelations();
			LOG.info("Generating SynsetRelations done");

			// *** Creating RelatedForms of LexicalEntries *** //
			LOG.info("Generating RelatedForms...");
			RelatedFormGenerator relatedFormGenerator = new RelatedFormGenerator(lexicalEntryGenerator);
			relatedFormGenerator.updateRelatedForms();
			LOG.info("Generating RelatedForms done");

			// *** Creating SenseRelations *** //
			LOG.info("Generating SenseRelations...");
			SenseRelationGenerator senseRelationGenerator = new SenseRelationGenerator(lexicalEntryGenerator);
			senseRelationGenerator.updateSenseRelations();
			LOG.info("Generating SenseRelations done");

			// *** Setting SubcategorizationFrames ***//
			lexicon.setSubcategorizationFrames(subcategorizationFrameExtractor.getSubcategorizationFrames());
			// setting SemanticPredicates
			lexicon.setSemanticPredicates(subcategorizationFrameExtractor.getSemanticPredicates());
			// setting SynSemCorrespondences
			lexicon.setSynSemCorrespondences(subcategorizationFrameExtractor.getSynSemCorrespondences());
		} catch (JWNLException e) {
			throw new RuntimeException("UBY-LMF creation failed", e);
		}
	}

	/**
	 * Returns the {@link LexicalResource} object, which contains the results of the conversion
	 * @return an instance of LexicalResource, which contains the results of the conversion
	 */
	public LexicalResource getLexicalResource() {
		return this.lexicalResource;
	}

}
