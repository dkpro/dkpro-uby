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

package de.tudarmstadt.ukp.lmf.transform.gvc;


import java.io.IOException;
import java.util.LinkedList;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
	

	
/**
 * This class converts a preprocessed version of a subset of IMSLex (PhD thesis of Judith Eckle-Kohler) to Uby-LMF
 * GVC stands for German Verb Classes, as a part of the subcategorization lexicon is classified into semantic verb classes
 * @author Judith Eckle-Kohler
 *
 */

public class GermanVcConverter {	
	
	
	public LexicalResource lexicalResource; 
	private final String dtd_version;
	
	public GermanVcConverter(LexicalResource lexicalResource, String dtd)
	{
		super();
		this.lexicalResource = lexicalResource;
		dtd_version = dtd;
	}
	
	/**
	 * Converts a preprocessed version of a subset of IMSLex to Uby-LMF
	 * @throws IOException
	 */
	public void toLMF(GermanVcExtractor germanVcExtractor) throws IOException {
		
		// *** Setting GlobalInformation *** //
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("Subset of IMSLex, see Eckle-Kohler (1999), Version of 09/2011");
		
		// Setting attributes of LexicalResource
		lexicalResource.setGlobalInformation(globalInformation);
		lexicalResource.setName("IMSLexSubset");
		lexicalResource.setDtdVersion(dtd_version);
		
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(germanVcExtractor.lexicon); // Setting lexicon (extracted from the IMSlex subset)
		lexicalResource.setLexicons(lexicons);		
	}

}
