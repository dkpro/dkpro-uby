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

package de.tudarmstadt.ukp.lmf.transform.sensealignments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

public class OmegaWikiWiktionaryAlignment
	extends SenseAlignment
{
	public StringBuilder logString;
	private final SenseAlignmentUtils saUtils;
	private final Connection alignmentConnection;
	private final OmegaWiki ow;
	private final int owLanguage;
	
	public OmegaWikiWiktionaryAlignment(String sourceUrl, String destUrl,String dbDriver,
			String dbVendor, String alignmentFile, String user, String pass,
			String alignmentHost,String alignmentDb,String alignmentUser, 
			String alignmentPass,OmegaWiki ow,int language) throws SQLException, InstantiationException, 
			IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl,dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		this.ow = ow;
		this.owLanguage = language;
		Class.forName(dbDriver);
		alignmentConnection=  DriverManager.getConnection("jdbc:"+dbVendor+"://"+alignmentHost+"/"+alignmentDb, alignmentUser, alignmentPass);
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor,user, pass, true);

		saUtils = new SenseAlignmentUtils(s, d, 1, 0, "temp_MiM", "temp_MiM");
		saUtils.createDefaultTempTables(false);
		//saUtils=null;
		logString = new StringBuilder();
	}

	public void getAlignmentDirectSQL() throws SQLException, OmegaWikiException, UnsupportedEncodingException, ClassNotFoundException
	{
		int count =0;
		HashMap<String,String> ubyMap = new HashMap<String, String>();
		HashMap<String,String> owMap = new HashMap<String, String>();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://uby.ukp.informatik.tu-darmstadt.de/uby_release_1_0","matuschek","p?h;fkyt");
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT Sense.senseId, MonolingualExternalRef.externalReference  FROM Sense JOIN MonolingualExternalRef ON (Sense.senseId=MonolingualExternalRef.senseId) where Sense.senseId like 'WktE%'or Sense.senseId like 'OW_en%' ");
		while (resultSet.next())
		{
			ubyMap.put(resultSet.getString(2), resultSet.getString(1));

		}
		resultSet = statement.executeQuery("SELECT _index,  senseId, synsetId FROM Sense where senseId like 'OW_%' ");
		while (resultSet.next())
		{
			owMap.put(resultSet.getString(1), resultSet.getString(2)+"#"+resultSet.getString(3));
		}
		statement = alignmentConnection.createStatement();
		resultSet = statement.executeQuery("select ID1,ID2 from WKTOWRelatedness_classify where ClassTraining = '1'");
		while (resultSet.next()){
			DefinedMeaning dm = ow.getDefinedMeaningById(resultSet.getInt("ID2")); //Synset!!!
			Set<SynTrans> sts = dm.getSynTranses(owLanguage); //Senses!!!
			String[] temp = resultSet.getString("ID1").split("--");
			String wktId = ubyMap.get(temp[1]);
			statement = conn.createStatement();
			for (SynTrans st : sts)
			{
				String sourceSense = owMap.get(st.getSyntransid()+"");
				if(wktId == null) {
					continue;
				}
				String[] temp2 = sourceSense.split("#");
				String command = "insert into SenseAxis(senseAxisId, senseAxisType, senseOneID, senseTwoId,synsetOneId,synsetTwoId,lexicalResourceId,idx) Values("+
								"'Wiktionary_OmegaWiki_Matuschek_2012_"+count+++"','monolingualSenseAlignment','"+temp2[0]+"','"+wktId+"','"+temp2[1]+"',NULL,'Uby',NULL);";
				statement.executeUpdate(command);
			}

		}

	}
	@Override
	public void getAlignment()
	{
		System.out.println("Starting getting alignment for OmegaWiki - Wiktionary "
				+ getAlignmentFileLocation());
		try {
			int count = 1;
			FileReader in = new FileReader( getAlignmentFileLocation());
//			String UBY_HOME = System.getenv("UBY_HOME");
//			Statement statement = alignment_connection.createStatement();
//			ResultSet resultSet = statement.executeQuery("select id1,id2 from WNOWRelatedness_MiM_EN_DE_PPR_classify where ClassTraining = 1");
			BufferedReader input =  new BufferedReader(in);
			String line ;
			while((line =input.readLine()) != null){

				String owId = line.split(",")[1].replaceAll("\"","");
				String wktId = line.split(",")[0].replaceAll("\"","");
				DefinedMeaning dm = ow.getDefinedMeaningById(Integer.parseInt(owId)); //Synset!!!
				Set<SynTrans> sts = dm.getSynTranses(owLanguage); //Senses!!!
				String[] temp = wktId.split("--");
				if(temp.length < 2) {
					continue;
				}
				System.out.println(saUtils);
				List<Sense> senseWKN = saUtils.getSensesByExternalRefID(temp[1], 1,false);
				System.out.println(senseWKN.size());

				for (SynTrans st : sts)
				{
					List<Sense> first = ubySource.getSensesByOriginalReference("OW SynTrans ID", ""+st.getSyntransid());
					Sense sourceSense = first.get(0);
					for (Sense targetSense: senseWKN)
					{
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
