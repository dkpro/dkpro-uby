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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.data.POS;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;
import de.tudarmstadt.ukp.lmf.transform.omegawiki.OmegaWikiLMFMap;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;

public class OmegaWikiWordNetAlignment extends SenseAlignment
{
	public StringBuilder logString;
	private final SenseAlignmentUtils saUtils;
	//private final Connection alignment_connection;
	private final OmegaWiki ow;
	private final int owLanguage;
	private final String owLanguageId;

	// the param alignmentFile is a dummy param that is not used; passing the name of any existing file is ok
	public OmegaWikiWordNetAlignment(String sourceUrl, String destUrl,String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass,OmegaWiki ow, int language)
		    throws SQLException, InstantiationException, IllegalAccessException,
		    ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl,dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		this.ow = ow;
		this.owLanguage = language;
		this.owLanguageId = OmegaWikiLMFMap.mapLanguage(owLanguage);
		Class.forName(dbDriver);
//		alignment_connection=  DriverManager.getConnection("jdbc:"+db_vendor+"://"+alignment_host+"/"+alignment_db, alignment_user, alignment_pass);
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor,user, pass, true);

		saUtils = new SenseAlignmentUtils(s, d, 1, 1, "temp_MiM", "temp_MiM");
		saUtils.createDefaultTempTables(true);
		logString = new StringBuilder();
	}

	@Override
	public void getAlignment()
	{
		System.out.println("Starting getting alignment for OmegaWiki -WordNet "
				+ getAlignmentFileLocation());

		try {
			int count = 1;
			FileReader in = new FileReader( getAlignmentFileLocation());
			BufferedReader input =  new BufferedReader(in);
			String line ;
			while((line = input.readLine()) != null){
				String owId = line.split(",")[1].replaceAll("\"","");
				String wnId = line.split(",")[0].replaceAll("\"","");
				DefinedMeaning dm = ow.getDefinedMeaningById(Integer.parseInt(owId)); //Synset!!!
				Set<SynTrans> sts = dm.getSynTranses(owLanguage); //Senses!!!
				String[] temp = wnId.split("#");
				POS pos;
				if(temp[1].equals("n")) {
					pos = POS.NOUN;
				}
				else if(temp[1].equals("v")) {
					pos = POS.VERB;
				}
				else if(temp[1].equals("a")) {
					pos = POS.ADJECTIVE;
				}
				else {
					pos = POS.ADVERB;
				}
				String refId = temp[0];
				String prefix ="[POS: noun] ";
				if (pos.equals(POS.ADJECTIVE)){
					prefix = prefix.replaceAll("noun", "adjective");
				}else if (pos.equals(POS.ADVERB)){
					prefix = prefix.replaceAll("noun", "adverb");
				}else if (pos.equals(POS.VERB)){
					prefix = prefix.replaceAll("noun", "verb");
				}
				refId = prefix+refId;
				List<Sense> WNRef = saUtils.getSensesByExternalRefID(refId, 1, true);

				for (SynTrans st : sts){
					List<Sense> first = ubySource.getSensesByOriginalReference("OmegaWiki_"+owLanguageId+"_synTrans", ""+st.getSyntransid());
					Sense sourceSense = first.get(0);
					for (Sense targetSense: WNRef){
						addSourceSense(sourceSense);
						addDestSense(targetSense);
						System.out.println(count++);
					}
				}
			}
			input.close();
			//Retrieve alignments
			saUtils.destroyTempTable();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
