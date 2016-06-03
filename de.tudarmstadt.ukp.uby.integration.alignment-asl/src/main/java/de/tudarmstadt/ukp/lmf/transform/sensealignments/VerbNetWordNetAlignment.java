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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;

public class VerbNetWordNetAlignment extends SenseAlignment
{

	List<String> missMatchs;
	StringBuilder logString;
	public VerbNetWordNetAlignment(String sourceUrl, String destUrl,String dbDriver,String dbVendor,
			String alignmentFile,String user, String pass, String UBY_HOME) throws FileNotFoundException
	{
		super(sourceUrl, destUrl,dbDriver,dbVendor,alignmentFile,user,pass,UBY_HOME);
		missMatchs = new ArrayList<String>();
		logString = new StringBuilder();
	}
	
	public VerbNetWordNetAlignment(String sourceUrl, String destUrl,String dbDriver,String dbVendor,
			String alignmentFile,String user, String pass) throws FileNotFoundException
	{
		this(sourceUrl, destUrl,dbDriver,dbVendor,alignmentFile,user,pass,UBY_HOME);
	}


	public void getAlignmentDirectSQL(String sourceUrl,String destUrl,
			String dbDriver, String dbVendor, String user, String pass,
			String UBY_HOME, boolean createLMFobjects) throws SQLException, ClassNotFoundException, IOException, IllegalArgumentException
	{
		Class.forName(dbDriver);
		Connection connection = DriverManager.getConnection("jdbc:"+dbVendor+"://"+sourceUrl,user,pass);
		Statement statement  = connection.createStatement();
		HashMap<String,String> vn = new HashMap<String, String>();
		HashMap<String,String> wn = new HashMap<String, String>();
		ResultSet rs = statement .executeQuery("SELECT externalReference,senseId FROM MonolingualExternalRef where senseId like 'VN%' order by externalReference");
		int senseno = 0;
		String previous  = "";
		System.out.println();
		while (rs.next())
		{
			String line = rs.getString(1);
			if (!line.equals(previous)){
				previous=line;
				senseno=0;
			}else{
				senseno++;
			}
			System.out.println(line+"$$$"+senseno+" "+rs.getString(2));
			vn.put(line+"$$$"+senseno, rs.getString(2));
		}
		System.out.println("First filled");
		rs = statement .executeQuery("SELECT externalReference,senseId FROM MonolingualExternalRef where senseId like 'WN%' and externalReference like '[POS: v%'");
		while (rs.next())
		{
			wn.put(rs.getString(1), rs.getString(2));
			System.out.println(rs.getString(1)+" "+rs.getString(2));

		}
		System.out.println("Second filled");
		System.out.println(getAlignmentFileLocation());
		BufferedReader reader = parseMetaData();
		String line = null;
		int lineNumber = 0;
		int count=0;
		// System
		String previousLine="";
		int senseIndex=0;
		while ((line = reader.readLine()) != null) {

			if (!line.equals(previousLine)){
				previousLine=line;
				senseIndex=0;
			}else{
				senseIndex++;
			}

			String temp[] = line.split("#");
			String verbnetItem = temp[0];
			String wordNetItem = temp[1];
			Double confidence = null;
			String metaDataId = null;
			if(temp.length > 2 && !temp[2].equals("null"))
				confidence = Double.parseDouble(temp[2]);
			if(temp.length > 3 && !temp[3].equals("null"))
				metaDataId = temp[3];
			
			wordNetItem = wordNetItem.substring(4);
			wordNetItem = wordNetItem.replace(")", "").trim();
			//System.out.println(lineNumber+":"+verbnetItem+" \t"+
			 //wordNetItem);

			if (wordNetItem.length() != 0) {

				String vnId = vn.get(verbnetItem+"$$$"+senseIndex);
				// each element separates by space bar to each other.
				String wordNetItems[] = wordNetItem.split(" ");

				for (String wordnet : wordNetItems) {
					// wordnet
					if (wordnet.trim().length() > 0) {
						//replace question mark
						wordnet=wordnet.replaceAll("\\?","");
						String[] tmp = wordnet.split("%");
						String word = tmp[0].trim();

						// verbnet
						System.out.println(count);
						String wnid = wn.get("[POS: verb] "+wordnet+"::");

						//we should check wnSense has just only one item! if not Stop and write the error out!
						//System.out.println(lineNumber+":"+vnId+" \t"+ wnid);
						
						if (vnId !=null && wnid!=null){
							if (createLMFobjects) {
								Sense sourceSense = ubySource.getSenseById(vnId);
								Sense destSense = ubyDest.getSenseById(wnid);
								addSourceSense(sourceSense);
								addDestSense(destSense);
								if (metaDataId != null && confidence != null) {
									addMetaData(metaDataId, confidence);
								}
							}
							/*statement.execute("insert into SenseAxis(senseAxisId,senseAxisType,senseOneId,senseTwoId,synsetOneId,synsetTwoId,lexicalResourceID) VALUES (" +
								 	"'"+"VN_WN_alignment_"+count+++"'"+","+
								 	"'"+"monolingualSenseAlignment" +"'"+","+
								 	"'"+vnId+"'"+","+
								 	"'"+wnid+"'"+","+
								 	"'"+"null"+"'"+","+
								 	"'"+"null"+"'"+","+
								 		"'Uby'"+
								 		")");
							*/
							count++;
						}else{

							missMatchs.add(wordnet);
							logString.append(verbnetItem+"\t"+wordnet);
							logString.append(LF);
						}
					}
				}
			}
			lineNumber++;
		}
		System.out.println("number of alignment:"+count);
	}



	@Override
	public void getAlignment()
	{
		String error = "";
		try {
			System.out.println(getAlignmentFileLocation());
			BufferedReader reader = parseMetaData();// new BufferedReader(new FileReader(getAlignmentFileLocation()));
			String line = null;
			int lineNumber = 0;
			int count=0;
			// System
			String previousLine="";
			int senseIndex = 0;
			while((line = reader.readLine()) != null) {
				if (!line.equals(previousLine)){
					previousLine = line;
					senseIndex = 0;
				}else{
					senseIndex++;
				}

				String temp[] = line.split("#");
				String verbnetItem = temp[0];
				String wordNetItem = temp[1];
				
				Double confidence = null;
				String metaDataId = null;
				if(temp.length > 2 && !temp[2].equals("null"))
					confidence = Double.parseDouble(temp[2]);
				if(temp.length > 3 && !temp[3].equals("null"))
					metaDataId = temp[3];
				
				wordNetItem = wordNetItem.substring(4);
				wordNetItem = wordNetItem.replace(")", "").trim();
				// System.out.println(lineNumber+":"+verbnetItem+" \t"+
				// wordNetItem);

				if (wordNetItem.length() != 0) {
					//if the line has alignment with not null wordnet
					List<Sense>senses = ubySource.getSensesByOriginalReference("VerbNet_3.2_eng_sense", verbnetItem);
					//System.out.println(verbnetItem+" -- # sense: "+senses.size());

					//current sense in verb net
					Sense currentVerbNetSense = senses.get(senseIndex);

					// each element separates by space bar to each other.
					String wordNetItems[] = wordNetItem.split(" ");

					for (String wordnet : wordNetItems) {
						// wordnet
						if (wordnet.trim().length() > 0) {
							//replace question mark
							wordnet = wordnet.replaceAll("\\?","");
							String[] tmp = wordnet.split("%");
							String word = tmp[0].trim();
							error = wordnet;
							// verbnet
							String verb = verbnetItem.replaceAll(word + "_", "").split("-")[0];

							// System.out.println(verb + " --> " + word + "--"
							// + lex_filenum + "--" + lex_id);
							// now searching the sense in each database in LMF
							// format
							System.out.println(count);
							String refString="[POS: verb] ";
							List<Sense> wnSenses = ubyDest.getSensesByOriginalReference("WordNet_3.0_eng_senseKey", refString+wordnet+"::");
							//we should check wnSense has just only one item! if not Stop and write the error out!

							if (wnSenses.size()!=0 && currentVerbNetSense!=null){
								addSourceSense(currentVerbNetSense);
								addDestSense(wnSenses.get(0));
								if (metaDataId != null && confidence != null) {
									addMetaData(metaDataId, confidence);
								}
								count++;
							}else{
								System.out.println(wordnet + " "+verb);
								missMatchs.add(wordnet);
								logString.append(verbnetItem+"\t"+wordnet);
								logString.append(LF);
							}
						}
					}
				}
				lineNumber++;

			}

			System.out.println("number of alignment:"+count);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Debug:" + error);
			e.printStackTrace();
		}
	}
}
