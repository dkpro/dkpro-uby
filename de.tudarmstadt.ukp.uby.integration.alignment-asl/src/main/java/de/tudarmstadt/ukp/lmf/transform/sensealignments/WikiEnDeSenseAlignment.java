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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;

public class WikiEnDeSenseAlignment extends SenseAlignment
{
	private StringBuilder logString;

	public WikiEnDeSenseAlignment(String sourceUrl,
			String destUrl,String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass, String UBY_HOME) throws FileNotFoundException
	{
		super(sourceUrl, destUrl,dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		logString = new StringBuilder();
	}
	
	public WikiEnDeSenseAlignment(String sourceUrl,
			String destUrl,String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass) throws FileNotFoundException
	{
		super(sourceUrl, destUrl,dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		logString = new StringBuilder();
	}

	@Override
	public void getAlignment() // TODO check!
	{
		try {
			System.out.println(getAlignmentFileLocation());
			BufferedReader reader = new BufferedReader(new FileReader(getAlignmentFileLocation()));
			String line = null;
			int lineNumber = 0;
			//int count = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (lineNumber != 1) {
					String[] tmp = line.split("\t");
					String titleWikiEn = tmp[0];
					String titleWikiDe = tmp[1];
					System.out.println("...processing line: "+lineNumber+"...");
					List<Sense> sensesSource = ubySource.getSensesByOriginalReference("Wikipedia",titleWikiEn);//FIXME change to new external System label
					List<Sense> sensesDest = ubyDest.getSensesByOriginalReference("Wikipedia",titleWikiDe);    //FIXME change to new external System label

					Sense source = null, dest = null;
					if (sensesSource.size() != 0 && sensesDest.size() != 0) {
						for (Sense senseSource : sensesSource) {
							if (senseSource.getMonolingualExternalRefs().get(0)
									.getExternalReference().equals(titleWikiEn)) {
								source = senseSource;
								break;
							}
						}
						for (Sense senseDest : sensesDest) {
							if (senseDest.getMonolingualExternalRefs().get(0)
									.getExternalReference().equals(titleWikiDe)) {
								dest = senseDest;
								break;
							}
						}

						if (source != null && dest != null) {
							addSourceSense(source);
							addDestSense(dest);
						}
						else {
							// log
							System.out.println("Log mistakes!" + tmp[0] + " \t" + tmp[1]);
							logString.append(LF);
							logString.append(titleWikiEn + "\t" + titleWikiDe);
						}
					}
					else {
						// log
						System.out.println("Log mistakes!" + tmp[0] + " \t" + tmp[1]);
						logString.append(LF);
						logString.append(titleWikiEn + "\t" + titleWikiDe);
					}
				}
			}
			reader.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Deprecated
	public void insertAlignment(String sourceUrl,
			String destUrl,String dbDriver, String dbVendor, String user, String pass,String UBY_HOME) 
			throws SQLException, ClassNotFoundException, IOException
	{
		Class.forName(dbDriver);
		Connection connection = DriverManager.getConnection("jdbc:" + dbVendor + "://" + sourceUrl,user,pass);
		Statement statement  = connection.createStatement();
		//ResultSet rs = statement  .executeQuery("SELECT externalReference, senseId FROM MonolingualExternalRef where senseId like 'WikiD%'");
		FileReader in = new FileReader(UBY_HOME + "/alignment_wp_en_de_all_titles_keys_2012_02_27.txt");

		BufferedReader input =  new BufferedReader(in);
		String line;
		int count = 1;
		boolean start = true;
		while((line =input.readLine())!=null){
			if(start) {
				start = false;
				continue;
			}
			String[] tokens = line.split("\t");
			String wpdename = tokens[3];
			String wpenname = tokens[2];
			statement.executeUpdate("Insert into SenseAxis(senseAxisId,senseAxisType,senseOneId,senseTwoId,lexicalResourceId,idx) Values('WP_en_de_alignment_"+count+"','crosslingualSenseAlignment','"+wpenname+"','"+wpdename+"','Uby',"+count+")");
			System.out.println(count++);
		}
		input.close();
	}
	
	@Deprecated
	public void correctAlignmentFile(String sourceUrl,
			String destUrl,String dbDriver, String dbVendor, String user, String pass,String UBY_HOME) throws SQLException, ClassNotFoundException, IOException
	{
		Class.forName(dbDriver);
		Connection connection = DriverManager.getConnection("jdbc:"+dbVendor+"://"+sourceUrl,user,pass);
		Statement statement  = connection.createStatement();
		HashMap<String,String> wpde = new HashMap<String, String>();
		HashMap<String,String> wpen = new HashMap<String, String>();
		ResultSet rs = statement  .executeQuery("SELECT externalReference, senseId FROM MonolingualExternalRef where senseId like 'WikiD%'");

		while (rs.next()){
			wpde.put(rs.getString(1), rs.getString(2));
		}
		System.out.println("First filled");
		rs = statement .executeQuery("SELECT externalReference, senseId FROM MonolingualExternalRef where senseId like 'WikiE%'");
		while (rs.next()){
			wpen.put(rs.getString(1), rs.getString(2));
		}
		System.out.println("Second filled");
		FileReader in = new FileReader("/home/matuschek/UBY_HOME/alignment_wp_en_de_all_titles_keys_2011_11_08.txt");

		BufferedReader input =  new BufferedReader(in);
		String line;
		FileOutputStream out = new FileOutputStream("target/WPDEEN_aligment");

          // Connect print stream to the output stream
        PrintStream p = new PrintStream(out);
		while((line =input.readLine()) != null){

			 String[] tokens = line.split("\t");
			 String wpdename = tokens[1];
			 String wpenname = tokens[0];
			 String wpenid = wpen.get(wpenname);
			 String wpdeid = wpde.get(wpdename);
			 p.println(wpenname +"\t"+wpdename +"\t"+wpenid +"\t"+wpdeid);
			 System.out.println(wpenname +"\t"+wpdename +"\t"+wpenid +"\t"+wpdeid);
		 }
		input.close();
		p.close();
	}
	
	
	public void getAlignmentQuickly(String sourceUrl,
			String destUrl,String dbDriver, String dbVendor, String user, String pass,String UBY_HOME){
		String line = null;
		try {
			// new
			SenseAlignmentUtils saUtils;

			DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor,user,pass,true);
			DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor,user,pass,true);

			// temp_Duc is the name of temporary table
			if(sourceUrl.equals(destUrl)) {
				saUtils = new SenseAlignmentUtils(s, s, 0, 0, "temp_Duc3", "temp_Duc3");
			}
			else {
				saUtils = new SenseAlignmentUtils(s, d, 0, 0, "temp_Duc3", "temp_Duc3");
			}
			saUtils.createDefaultTempTables(false);

			System.out.println(getAlignmentFileLocation());
			BufferedReader reader = new BufferedReader(new FileReader(getAlignmentFileLocation()));

			int lineNumber = 0;
			//int count = 0;
			//saUtils.getSensesByExternalRefID("Aldege \"Baz\" Bastien Memorial Award", 0, false);
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				line = new String(line.getBytes(),"UTF-8");
				if (lineNumber != 1) {
					String[] tmp = line.split("\t");
					String titleWikiEn = tmp[0];
					String titleWikiDe = tmp[1];
					System.out.println("...processing line: "+lineNumber+"...");

					List<Sense> sensesSource = saUtils.getSensesByExternalRefID(titleWikiEn, 0, false);
					List<Sense> sensesDest = saUtils.getSensesByExternalRefID(titleWikiDe, 1, false);

					Sense source = null, dest = null;
					if (sensesSource.size() == 1 && sensesDest.size() == 1) {
						source = sensesSource.get(0);
						dest = sensesDest.get(0);

						if (source != null && dest != null) {
							addSourceSense(source);
							addDestSense(dest);
						}
						else {
							System.out.println("Log mistakes!" + tmp[0] + " \t"+ tmp[1]);
							logString.append(LF);
							logString.append(titleWikiEn + "\t" + titleWikiDe);
						}
					}
					else {
						// log
						System.out.println("Log mistakes!" + tmp[0] + " \t"+ tmp[1]);
						logString.append(LF);
						logString.append(titleWikiEn + "\t" + titleWikiDe);
					}
				}
			}
			reader.close();
			//	saUtils.destroyTempTable();
		}
		catch (Exception ex) {
			System.out.println("Error happens here:"+line);
			ex.printStackTrace();
		}
	}
}

