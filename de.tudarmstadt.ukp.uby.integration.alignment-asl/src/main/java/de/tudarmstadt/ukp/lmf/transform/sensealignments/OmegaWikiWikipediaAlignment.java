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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;
import de.tudarmstadt.ukp.lmf.transform.omegawiki.OmegaWikiLMFMap;
import de.tudarmstadt.ukp.omegawiki.api.OWLanguage;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;

public class OmegaWikiWikipediaAlignment
	extends SenseAlignment
{
	public StringBuilder logString;
	private final SenseAlignmentUtils saUtils;
	private final OmegaWiki ow;
	private final int owLanguage;
	private final String owLangId;


	private String WPlanguage;
	public OmegaWikiWikipediaAlignment(String sourceUrl, String destUrl,String dbDriver,String dbVendor, 
			String alignmentFile, String user, String pass, OmegaWiki ow, int language, String UBY_HOME) 
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl,destUrl,dbDriver,dbVendor,alignmentFile,user,pass,UBY_HOME);
		logString = new StringBuilder();
		this.owLanguage = language;
		this.owLangId = OmegaWikiLMFMap.mapLanguage(owLanguage);

		if(language == OWLanguage.English) {
			WPlanguage = "en";
		}else if(language == OWLanguage.German) {
			WPlanguage = "de";
		}
		this.ow = ow;
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass,true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, true);
		if(sourceUrl.equals(destUrl)) {
			saUtils = new SenseAlignmentUtils(s, s, 1, 0, "temp_MiM2", "temp_MiM2");
		}else {
			saUtils = new SenseAlignmentUtils(s, d, 1, 0, "temp_MiM2", "temp_MiM2");
		}
		saUtils.createDefaultTempTables(false);
	}

	@Override
	public void getAlignment()
	{
		System.out.println("Starting getting alignment for OmegaWiki - Wikipedia "
				+ getAlignmentFileLocation());
		try {
			
			int count = 0;

			Map <SynTrans,String> stm = ow.getWPLinks(owLanguage);

			for(SynTrans source : stm.keySet() )
			{
				List<Sense> first = ubySource.getSensesByOriginalReference("OmegaWiki_"+owLangId+"_synTrans", ""+source.getSyntransid());

				Sense sourceSense = first.get(0);
				String wikiRef = stm.get(source);

				if(wikiRef.contains(WPlanguage+".wikipedia")){
					String[] s = wikiRef.split("/");
					wikiRef = s[s.length-1].replace('_', ' ');;
					s = wikiRef.split("#");
					wikiRef = s[0];
					wikiRef = wikiRef.replace("'", "\\'");
					List<Sense> second = saUtils.getSensesByExternalRefID(wikiRef,1,false);
					
					if(second.size() > 0){
						for (Sense targetSense : second){
							if(targetSense.getId().startsWith("Wiki"+WPlanguage.toUpperCase())){
								System.out.println(count);
								addSourceSense(sourceSense);
								addDestSense(targetSense);
								count++;
							}
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
