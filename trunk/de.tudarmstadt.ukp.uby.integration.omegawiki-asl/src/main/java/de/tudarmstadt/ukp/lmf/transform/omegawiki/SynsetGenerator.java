/*******************************************************************************
 * Copyright 2013
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.TranslatedContent;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * This class generates Synset and all it's underlying children
 *
 * @author matuschek
 *
 */
public class SynsetGenerator {
	
	public final static String DEFINED_MEANING = "definedMeaning";
	
	private final String resourceVersion;
	
	private int GlobalLanguage;
	public int getGlobalLanguage()
	{
		return GlobalLanguage;
	}

	public void setGlobalLanguage(int globalLanguage)
	{
		GlobalLanguage = globalLanguage;
	}

	public String getGlobalLanguageLMF()
	{
		return GlobalLanguageLMF;
	}

	public void setGlobalLanguageLMF(String globalLanguageLMF)
	{
		GlobalLanguageLMF = globalLanguageLMF;
	}

	public OmegaWiki getOmegawiki()
	{
		return omegawiki;
	}

	private String GlobalLanguageLMF;

	private final OmegaWiki  omegawiki; // Omegawiki Dictionary

	private int LMFSynsetNumber = 0; // This is the running number used for creating IDs of LMFSynsets

	// Mappings between LMF-Synsets and OW-DefinedMeaning
	private final HashMap<de.tudarmstadt.ukp.lmf.model.semantics.Synset, DefinedMeaning>
		LMFSynsetOWSynsetMappings = new HashMap<de.tudarmstadt.ukp.lmf.model.semantics.Synset, DefinedMeaning>();

	// Mappings between OW-Synsets and LMF-DefinedMeaning
	private static HashMap<DefinedMeaning, de.tudarmstadt.ukp.lmf.model.semantics.Synset>
		OWSynsetLMFSynsetMappings = new HashMap<DefinedMeaning, de.tudarmstadt.ukp.lmf.model.semantics.Synset>();

	private boolean initialized = false; // true only if SynsetGenerator is already initialized

	/**
	 *  This Method Constructs a SynsetGenerator based on consumed OmegaWiki Dictionary
	 * @param omegawiki OmegaWiki Dictionary
	 * @param language
	 * @param resourceVersion Version of the resource
	 * @return SynsetGenerator
	 */
	public SynsetGenerator(OmegaWiki omegawiki, int language, String resourceVersion){
	//	if(language==OWLanguage.English)
		{
			this.GlobalLanguage = language;
			this.GlobalLanguageLMF = OmegaWikiLMFMap.mapLanguage(language);
			this.resourceVersion = resourceVersion;
		}
//		else if(language==OWLanguage.German)
//		{
//			System.out.println("German");
//			this.GlobalLanguage=OWLanguage.German;
//			this.GlobalLanguageLMF= ELanguageIdentifier.ISO639_DEU;
//		}
		this.omegawiki = omegawiki;
	}



	/**
	 * This method initializes the SynsetGenerator
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	public void initialize() throws UnsupportedEncodingException, OmegaWikiException {
		if(!initialized){
			//Iterate over all DefinedMeanings in the specified language
			Iterator<DefinedMeaning> dmIter = null;
			double overall = 0;
			double current = 0;
			try {
				dmIter = omegawiki.getAllDefinedMeanings(this.GlobalLanguage).iterator();
				overall = omegawiki.getAllDefinedMeanings(this.GlobalLanguage).size();
			} catch (OmegaWikiException e) {
				e.printStackTrace();
			}

			while(dmIter.hasNext() ) {//&& i++<=100 ){
				DefinedMeaning dm = dmIter.next();
				if(current++ % 100 == 0) {
					System.out.println((current) / overall+"");
				}
				Synset lmfSynset = new Synset();
				lmfSynset.setId(getNewID());
				LMFSynsetOWSynsetMappings.put(lmfSynset, dm);
				OWSynsetLMFSynsetMappings.put(dm, lmfSynset);

				// Generating Definition(s) of the Synset
				Definition definition = new Definition();
				TextRepresentation textRepresentation = new TextRepresentation();
				textRepresentation.setLanguageIdentifier(OmegaWikiLMFMap.mapLanguage(GlobalLanguage));
				if(dm.getGlosses(GlobalLanguage).size()>0) {
					textRepresentation.setWrittenText(((TranslatedContent)dm.getGlosses(GlobalLanguage).toArray()[0]).getGloss());
				}
				List<TextRepresentation> textRepresentations = new LinkedList<TextRepresentation>();
				textRepresentations.add(textRepresentation);
				definition.setTextRepresentations(textRepresentations);
				List<Definition> definitions = new LinkedList<Definition>();
				definitions.add(definition);
				lmfSynset.setDefinitions(definitions);

				// *** Creating MonolingualExternalRef ***//
				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				// Generating MonolingualExternalRef ID
				monolingualExternalRef.setExternalSystem(resourceVersion + "_" + DEFINED_MEANING);
				StringBuffer sb = new StringBuffer(16);
				sb.append(dm.getDefinedMeaningId());
				monolingualExternalRef.setExternalReference(sb.toString());
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				lmfSynset.setMonolingualExternalRefs(monolingualExternalRefs);
			}

		initialized = true;
		}

	}

	/**
	 * This method returns a list of LMF-Synsets
	 */
	public List<de.tudarmstadt.ukp.lmf.model.semantics.Synset> getSynsets(){
		List<de.tudarmstadt.ukp.lmf.model.semantics.Synset> result = new LinkedList<de.tudarmstadt.ukp.lmf.model.semantics.Synset>();
		result.addAll(LMFSynsetOWSynsetMappings.keySet());
		Collections.sort(result);
		return result;
	}



	/**
	 * This method generates a Synset-ID
	 */
	private String getNewID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("OW_"+GlobalLanguageLMF+"_Synset_").append(Integer.toString(LMFSynsetNumber));
		LMFSynsetNumber++;
		return sb.toString();
	}

	/**
	 * This method consumes a OW DM returns It's corresponding Uby-LMFSynset
	 *
	 * Evoke this method only after SynsetGenerator has been initialized!
	 *
	 * @param dn DefinedMeaning
	 * @return Uby-LMFSynset that corresponds to dm
	 */
	public de.tudarmstadt.ukp.lmf.model.semantics.Synset getLMFSynset(DefinedMeaning dm){
		return OWSynsetLMFSynsetMappings.get(dm);
	}



	/**
	 * @return the lMFSynsetOWSynsetMappings
	 */
	public HashMap<de.tudarmstadt.ukp.lmf.model.semantics.Synset, DefinedMeaning> getLMFSynsetOWSynsetMappings() {
		return this.LMFSynsetOWSynsetMappings;
	}



	/**
	 * @return the OWSynsetLMFSynsetMappings
	 */
	public HashMap<DefinedMeaning, de.tudarmstadt.ukp.lmf.model.semantics.Synset> getOWSynsetLMFSynsetMappings() {
		return OWSynsetLMFSynsetMappings;
	}



	/**
	 * @return the OmegaWiki
	 */
	public OmegaWiki getOmegaWiki() {
		return omegawiki;
	}
}
