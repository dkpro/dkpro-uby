/*******************************************************************************
 * Copyright 2016
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

package de.tudarmstadt.ukp.lmf.transform.sensealignments;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;

public class OmegaWikiDeEnAlignment
	extends SenseAlignment
{
	public StringBuilder logString;
	private final OmegaWiki ow;
	private final int sourceLang;
	private final int targetLang;
	public OmegaWikiDeEnAlignment(String sourceUrl, String destUrl, String dbDriver, String dbVendor, 
		String alignmentFile, String user, String pass, OmegaWiki ow, int sourceLang, int targetLang) 
		throws FileNotFoundException{

		super(sourceUrl, destUrl, dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		this.ow = ow;
	//	super();
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
		logString = new StringBuilder();
	}

	@Override
	public void getAlignment()
	{
		System.out.println("Starting getting alignment for OmegaWiki " + getAlignmentFileLocation());
		try {
			int count = 0;
			Map <SynTrans,Set<SynTrans>> stm = ow.getInterlanguageSTLinks(sourceLang, targetLang);
			for(SynTrans source : stm.keySet() ) {
					List<Sense> first = ubySource.getSensesByOriginalReference("OW SynTrans ID", ""+source.getSyntransid());;
					if (first.size()>0) {
					Sense sourceSense = first.get(0);
					for (SynTrans target : stm.get(source)){
						List<Sense> second = ubyDest.getSensesByOriginalReference("OW SynTrans ID", ""+target.getSyntransid());;
						if (second.size()>0) {
							Sense targetSense = second.get(0);
							System.out.println(count);
							addSourceSense(sourceSense);
							addDestSense(targetSense);
							count++;
						}
					}
				}
			}
			System.out.println("Number of alignment:" + count);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
