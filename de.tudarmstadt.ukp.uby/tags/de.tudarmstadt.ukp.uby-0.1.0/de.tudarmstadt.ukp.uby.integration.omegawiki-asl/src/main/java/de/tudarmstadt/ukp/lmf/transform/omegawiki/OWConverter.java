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
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.omegawiki.api.OWLanguage;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * This class converts OmegaWiki to the LMF-Model
 * @author matuschek
 *
 */
public class OWConverter {
	public int GlobalLanguage=OWLanguage.German;
	public  ELanguageIdentifier GlobalLanguageLMF; //OW Language to be converted
	public OmegaWiki  omegawiki; // Dictionary
	private LexicalResource lexicalResource;  // Top-level class of LMF
	private final String dtd_version;
	public OWConverter(LexicalResource lexicalResource, int language, String dtd) throws ClassNotFoundException, SQLException
	{
		super();
		this.lexicalResource = lexicalResource;
		dtd_version = dtd;
		if(language == OWLanguage.English)
		{
			GlobalLanguageLMF = ELanguageIdentifier.en;
			GlobalLanguage = OWLanguage.English;
		}
		else if(language == OWLanguage.German)
		{
			GlobalLanguageLMF = ELanguageIdentifier.de;
			GlobalLanguage = OWLanguage.German;
		}
	}



	public LexicalResource getLexicalResource()
	{
		return lexicalResource;
	}

	public void setLexicalResource(LexicalResource lexicalResource)
	{
		this.lexicalResource = lexicalResource;
	}

	public  int getGloballanguage()
	{
		return this.GlobalLanguage;
	}

	public  ELanguageIdentifier getGloballanguagelmf()
	{
		return GlobalLanguageLMF;
	}

	/**
	 * Converts OmegaWiki into LMF-Format
	 * @throws OmegaWikiException
	 * @throws IOException
	 */
	public void toLMF() throws OmegaWikiException, IOException{
		System.out.println("Started converting OmegaWiki to LMF...");
		// Setting attributes of LexicalResource
		lexicalResource.setName("Uby_OmegaWiki"+GlobalLanguageLMF);
		lexicalResource.setDtdVersion(dtd_version);


		// *** Setting GlobalInformation *** //
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("Uby_OW_"+GlobalLanguageLMF);
		lexicalResource.setGlobalInformation(globalInformation);

		//*** Setting Lexicon (only one since we deal only with the one language)***//
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(GlobalLanguageLMF);
		lexicon.setId("OW_Lexicon_"+GlobalLanguage);
		lexicon.setName("OmegaWiki"+GlobalLanguageLMF.toString());
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(lexicon);
		lexicalResource.setLexicons(lexicons);

		// *** Creating Synsets *** //
		System.out.print("Generating Synsets...");
		SynsetGenerator synsetGenerator = new SynsetGenerator(omegawiki,GlobalLanguage);
		synsetGenerator.initialize();
		// Setting Synsets
		lexicon.setSynsets(synsetGenerator.getSynsets());
		System.out.println("done");

		// *** Creating LexicalEntries *** //
		System.out.print("Generating LexicalEntries...");
		LexicalEntryGenerator lexicalEntryGenerator = new LexicalEntryGenerator(omegawiki, synsetGenerator,lexicon);
		lexicon.setLexicalEntries(lexicalEntryGenerator.getLexicalEntries());
		System.out.println("done");

		// *** Creating SynsetRelations *** //
		System.out.print("Generating SynsetRelations...");
		SynsetRelationGenerator synsetRelationGenerator = new SynsetRelationGenerator(synsetGenerator,lexicalEntryGenerator);
		// Update the relatios of previously extracted (and generated) Synsets
		synsetRelationGenerator.updateSynsetRelations();
		System.out.println("done");

		// *** Creating SenseRelations *** //
		System.out.print("Generating SenseRelations");
		SenseRelationGenerator senseRelationGenerator = new SenseRelationGenerator(lexicalEntryGenerator, synsetGenerator);
		senseRelationGenerator.updateSenseRelations();
		System.out.println("done");
	}
}
