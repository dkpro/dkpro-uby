/**
 * Copyright 2016
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
package de.tudarmstadt.ukp.lmf.transform.framenet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;

/**
 * Instance of this class converts <a
 * href="URL#https://framenet.icsi.berkeley.edu/fndrupal/">FrameNet 1.5</a> to LMF-format
 * 
 * @author Zijad Maksuti
 * @author Silvana Hartmann
 */
public class FNConverter {

	private final FrameNet fn; // FrameNet Object
	private final String dtd_version;

	private final LexicalResource lexicalResource;

	private final String resourceVersion;
	
	private final Log logger = LogFactory.getLog(getClass());

    /**
     * Constructs a {@link FNConverter} based on the consumed parameters
     * 
     * @param frameNet
     *            initialized {@link FrameNet} object
     * @param resourceVersion version
     *            of this resource
     * @param lexicalResource
     *            initialized object of {@link LexicalResource}, which will be filled with
     *            FrameNet's data
     * @param dtd_version
     *            specifies the version of the .dtd which will be written to lexicalResource
     */
	public FNConverter(FrameNet frameNet, LexicalResource lexicalResource, 
			String resourceVersion, String dtd_version){
		this.fn = frameNet;
		this.lexicalResource = lexicalResource;
		this.resourceVersion = resourceVersion;
		this.dtd_version = dtd_version;
	}

    /**
     * Converts the informations provided by the initialized {@link FrameNet} object to LMF-format.
     * The result of the conversion can be obtained by calling
     * {@link FNConverter#getLexicalResource()}
     */
	public void toLMF() {

		// Setting attributes of LexicalResource
		lexicalResource.setName("FrameNet");
		lexicalResource.setDtdVersion(this.dtd_version);

		// *** Setting GlobalInformation *** //
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("LMF representation of FrameNet 1.5");
		lexicalResource.setGlobalInformation(globalInformation);

		//*** Setting Lexicon (only one since FrameNet is monolingual)***//
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
		lexicon.setId("FN_Lexicon_0");
		lexicon.setName("FrameNet");
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(lexicon);
		lexicalResource.setLexicons(lexicons);

		// *** Creating SemanticPredicates *** //
		logger.info("Generating SemanticPredicates...");
		SemanticPredicateGenerator semanticPredicateGenerator = new SemanticPredicateGenerator(fn, resourceVersion);
		List<SemanticPredicate> semanticPredicates = new ArrayList<SemanticPredicate>();
		semanticPredicates.addAll(semanticPredicateGenerator.getSemanticPredicates());
		lexicon.setSemanticPredicates(semanticPredicates);
		logger.info("Generating SemanticPredicates done");

		// *** Creating LexicalEntries *** //
		logger.info("Generating LexicalEntries...");
		LexicalEntryGenerator lexicalEntryGenerator = new LexicalEntryGenerator(fn, 
				semanticPredicateGenerator, resourceVersion);
		lexicon.setLexicalEntries(lexicalEntryGenerator.getLexicalEntries());
		logger.info("Generating LexicalEntries done");
	}


    /**
     * Returns the {@link LexicalResource} object, which contains the results of the conversion
     * 
     * @return the lexicalResource
     */
	public LexicalResource getLexicalResource() {
		return lexicalResource;
	}
}
