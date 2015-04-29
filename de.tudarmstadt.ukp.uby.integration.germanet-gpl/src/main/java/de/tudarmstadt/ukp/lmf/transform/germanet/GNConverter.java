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

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;

/**
 *
 * Instance of this class converts
 * <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * to LMF-format
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class GNConverter {

	private final GermaNet gnet; // GermaNet Object

	private final LexicalResource lexicalResource;

	private InputStream subcatStream; // subcat mapping file

	/*
	 * Groups of LexUnits with equal lemma and part of speech
	 */
	private Set<Set<LexUnit>> luGroups;

	private SubcategorizationFrameExtractor subcategorizationFrameExtractor;

	private final SynsetGenerator synsetGenerator;

	private final String dtd_version;

	private final Log logger = LogFactory.getLog(getClass());

	private final String resourceVersion;

	private MetaData alignmentMetaData;
	
	/**
	 * Constructs a {@link GNConverter} based on the consumed parameters
	 * @param germaNet initialized {@link GermaNet} object
	 * @param lexicalResource initialized object of  {@link LexicalResource}, which will be filled with GermaNet's data
	 * @param alignmentMeta MetaData of ili Alignment
	 * @param resourceVersion Version of this resource
	 * @param dtd_version specifies the version of the .dtd which will be written to lexicalResource
	 */
	public GNConverter(GermaNet germaNet, LexicalResource lexicalResource, MetaData alignmentMeta, String resourceVersion, String dtd_version)
	{
		this.gnet = germaNet;
		this.lexicalResource = lexicalResource;
		this.alignmentMetaData = alignmentMeta;
		
		if (alignmentMetaData==null){
			alignmentMetaData = InterlingualIndexConverter.getDefaultMetaData();
		}
		alignmentMetaData.setLexicalResource(this.lexicalResource);
		List<MetaData> metaList = this.lexicalResource.getMetaData();
		metaList.add(this.alignmentMetaData);
		this.lexicalResource.setMetaData(metaList);
		
		this.dtd_version = dtd_version;
		this.resourceVersion = resourceVersion;
		try {
			this.subcatStream = getClass().getClassLoader().getResource("GermaNetSubcatMappings/gnFrameMapping.txt").openStream();
			subcategorizationFrameExtractor = new SubcategorizationFrameExtractor(subcatStream);
		}
		catch (Exception e) {
			logger.error("GNConverter: unable to load subcat mapping file. Aborting all operations");
			System.exit(1);
		}

		this.synsetGenerator = new SynsetGenerator(this.gnet, resourceVersion);
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
		globalInformation.setLabel("LMF representation of GermaNet 7.0");
		lexicalResource.setGlobalInformation(globalInformation);

		//*** Setting Lexicon (only one since GermaNet is monolingual)***//
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
		lexicon.setId("GN_Lexicon_0");
		lexicon.setName("GermaNet");
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(lexicon);
		lexicalResource.setLexicons(lexicons);

		// *** Creating LexicalEntries *** //
		logger.info("Generating LexicalEntries...");
		this.groupLUs();
		LexicalEntryGenerator leGen = new LexicalEntryGenerator(this, resourceVersion);
		List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
		// Create a LexicalEntry for each luGroup
		for (Set<LexUnit> luGroup : luGroups) {
            lexicalEntries.add(leGen.createLexicalEntry(luGroup));
        }
		// Setting RelatedForms of LexicalEntries
		for(LexicalEntry lexicalEntry : lexicalEntries) {
            leGen.setRelatedForms(lexicalEntry);
        }
		// appending lexicalEntries
		lexicon.setLexicalEntries(lexicalEntries);
		StringBuffer sb = new StringBuffer(64);
		sb.append("Generated LexicalEntries: ").append(lexicalEntries.size());
		logger.info(sb.toString());
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
		logger.info(sb.toString());

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
		logger.info(sb.toString());

		// *** Appending SynSemCorrespondences *** //
		lexicon.setSynSemCorrespondences(subcategorizationFrameExtractor.getSynSemCorrespondences());

	}

	/**
	 *
	 * @param wordNetLexicalResource
	 */
	public void toLMF(Lexicon wordNetLexicon){
		toLMF();
		InterlingualIndexConverter iliConverter = new InterlingualIndexConverter(this, gnet, wordNetLexicon, alignmentMetaData);
		iliConverter.convert();
		lexicalResource.setSenseAxes(iliConverter.getSenseAxes());
	}

	/**
	 * This method groups all LexUnits by lemma and {@link WordCategory}
	 * @see LexUnit
	 */
	private void groupLUs() {
		if (luGroups == null) {
            luGroups = new LinkedHashSet<Set<LexUnit>>();
        }
		for (WordCategory pos : WordCategory.values()) {
			List<LexUnit> lus = gnet.getLexUnits(pos);
			Map<String, Set<LexUnit>> orthFormLUGroupMappings = 
					new TreeMap<String, Set<LexUnit>>();
			for (LexUnit lu : lus) {
				String orthForm = lu.getOrthForm();
				Set<LexUnit> luGroup = orthFormLUGroupMappings.get(orthForm);
				if(luGroup == null) {
                    luGroup = new LinkedHashSet<LexUnit>();
					orthFormLUGroupMappings.put(orthForm, luGroup);
				}
				luGroup.add(lu);
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
	protected Set<LexUnit> getLUGroup(LexUnit lexUnit){
		for (Set<LexUnit> luGroup : luGroups) {
            if(luGroup.contains(lexUnit)) {
                return luGroup;
            }
        }
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

	/**
	 * Returns the {@link SynsetGenerator} instance associated with this {@link GNConverter}.
	 *
	 * @return the synset generator associated with this converter
	 */
	public SynsetGenerator getSynsetGenerator() {
		return synsetGenerator;
	}
}

