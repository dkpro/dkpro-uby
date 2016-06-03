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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.omegawiki.OmegaWikiLMFMap;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;

public class OmegaWikiCrossLingualAlignment extends SenseAlignment
{
	public StringBuilder logString;
	private final OmegaWiki ow;
	private final int sourceLang;
	private final int targetLang;
	private final String sourceLangId;
	private final String targetLangId;

	private java.sql.Connection connection;
	public OmegaWikiCrossLingualAlignment(String sourceUrl, String destUrl, String dbDriver, 
			String dbVendor, String user, String pass, OmegaWiki ow, int sourceLang, int targetLang) throws ClassNotFoundException, SQLException, FileNotFoundException
	{
		super(sourceUrl,destUrl,dbDriver,dbVendor, user, pass,UBY_HOME);
		this.ow = ow;
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
		this.sourceLangId = OmegaWikiLMFMap.mapLanguage(sourceLang);
		this.targetLangId = OmegaWikiLMFMap.mapLanguage(targetLang);
		logString = new StringBuilder();
		Class.forName(dbDriver);
		connection = DriverManager.getConnection("jdbc:"+dbVendor+"://"+sourceUrl,user,pass);
	}


	public void getAlignmentDirectSQL(){
		System.out.println("Starting getting alignment for OmegaWiki "
				+ getAlignmentFileLocation());

		int count = 0;
		try {
			FileOutputStream outStream = new FileOutputStream(getAlignmentFile());;
			PrintStream p = new PrintStream(outStream);
			java.sql.Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM (SELECT Sense.senseId AS sense1, mr1.synsetId AS synset1, externalReference FROM MonolingualExternalRef AS mr1 JOIN Sense ON Sense.synsetId = mr1.synsetId WHERE mr1.synsetId LIKE 'OW_en%' AND mr1.senseId IS NULL) AS X JOIN (SELECT Sense.senseId AS sense2, mr1.synsetId AS synset2, externalReference FROM MonolingualExternalRef AS mr1 JOIN Sense ON Sense.synsetId = mr1.synsetId WHERE mr1.synsetId LIKE 'OW_de%' AND mr1.senseId IS NULL) AS Y ON X.externalReference = Y.externalReference");

			while (rs.next()){
				p.println("insert into SenseAxis(senseAxisId,senseAxisType,senseOneId,senseTwoId,synsetOneId,synsetTwoId,lexicalResourceID) VALUES (" +
					"'"+"OW_de_en_alignment_"+count+++"'"+","+
				 	"'"+"crosslingualSenseAlignment" +"'"+","+
				 	"'"+rs.getString("sense1")+"'"+","+
				 	"'"+rs.getString("sense2")+"'"+","+
				 	"'"+rs.getString("synset1")+"'"+","+
				 	"'"+rs.getString("synset2")+"'"+","+
			 		"NULL"+")");

			}
			p.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Number of alignment:" + count);
	}


	@Override
	public void getAlignment()
	{
		System.out.println("Starting getting alignment for OmegaWiki ");
		try {
			int count = 0;
		
			Map <SynTrans,Set<SynTrans>> stm = ow.getInterlanguageSTLinks(sourceLang, targetLang);
			System.out.println("Number of STlinks:" + stm.size());
			System.out.println("source language: " + sourceLangId + " target language: " + targetLangId);
			for(SynTrans source : stm.keySet()) {
			//	List<Sense> first = ubySource.getSensesByOWSynTransId(""+source.getSyntransid());
				// old external system value: "OW SynTrans ID"
				// new external system value, e.g. for English: "OmegaWiki_eng_synTrans"
				List<Sense> first = ubySource.getSensesByOriginalReference("OmegaWiki_"+sourceLangId+"_synTrans", ""+source.getSyntransid());
				if (first.size()>0) {
					Sense sourceSense = first.get(0);
					System.out.print("First:" + sourceSense.getId());
					for (SynTrans target : stm.get(source)){
						List<Sense> second = ubyDest.getSensesByOriginalReference("OmegaWiki_"+targetLangId+"_synTrans", ""+target.getSyntransid());
						if (second.size() > 0) {
							Sense targetSense = second.get(0);
							System.out.print("Second:" + targetSense.getId());
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
