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
package de.tudarmstadt.ukp.lmf.transform.verbnet;

import java.io.IOException;
import java.util.LinkedList;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
	
	
/**
 * This class converts a preprocessed version of VerbNet to Uby-LMF
 * @author Eckle-Kohler
 *
 */
public class VNConverter {	
	
	
	public LexicalResource lexicalResource;  
	private final String dtd_version;
	
	public VNConverter(LexicalResource lexicalResource, String dtd)
	{
		super();
		this.lexicalResource = lexicalResource;
		dtd_version = dtd;
	}
	
	/**
	 * Converts a preprocessed version of VerbNet to Uby-LMF
	 * @throws IOException
	 */
	public void toLMF(VerbNetExtractor verbNetExtractor) throws IOException {
		
		// Setting GlobalInformation 
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("VerbNet extracted from VerbNet v3.1 with Inspector API and Perl-script");
		
		// Setting attributes of LexicalResource
		lexicalResource.setGlobalInformation(globalInformation);
		lexicalResource.setName("VerbNet");
		lexicalResource.setDtdVersion(dtd_version);
		
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(verbNetExtractor.lexicon); // Setting the lexicon (extracted from VerbNet)
		lexicalResource.setLexicons(lexicons);		
	}

}
