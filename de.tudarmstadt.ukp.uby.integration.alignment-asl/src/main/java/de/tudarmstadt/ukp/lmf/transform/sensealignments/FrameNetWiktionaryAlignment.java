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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;

/**
 * Convert FrameNet-Wiktionary alignment to UBY Format
 * @author sh
 * 
 */
public class FrameNetWiktionaryAlignment
	extends SenseAlignment
{
	private String debug;
	public StringBuilder logString;
	public int nullAlignment;
	protected static Log logger = LogFactory.getLog(FrameNetWiktionaryAlignment.class);
	private final SenseAlignmentUtils saUtils;

	public FrameNetWiktionaryAlignment(String sourceUrl, String destUrl, String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass, String UBY_HOME)
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl, alignmentFile);
		logString = new StringBuilder();
		nullAlignment = 0;

		// new
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, true);
		System.out.println("here");
		// temp_Duc is the name of temporary table
		if (sourceUrl.equals(destUrl)) {
			saUtils = new SenseAlignmentUtils(s, s, 0, 0, "temp_Duc", "temp_Duc");
		}
		else {
			saUtils = new SenseAlignmentUtils(s, d, 0, 0, "temp_Duc", "temp_Duc");
		}
		String decFields1 =  "senseId varchar(255) NOT NULL, externalReference varchar(255)";
		String insData1 = "SELECT S.senseId, "
				+ " M.externalReference  "
				+ " FROM Sense S JOIN MonolingualExternalRef M"
				+ " ON (S.senseId=M.senseId)"
				+ "where substring(S.senseId,1,2)=\"FN\"";
		String insData2 = "SELECT S.senseId, "
				+ " M.externalReference  "
				+ " FROM Sense S JOIN MonolingualExternalRef M"
				+ " ON (S.senseId=M.senseId)"
				+ "where substring(S.senseId,1,2)=\"Wk\"";
		saUtils.createTempTable(decFields1, insData1, 0);
		saUtils.createTempTable(decFields1, insData2, 1);
	}

	@Override
	public void getAlignment()
	{
		int lineNumber = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getAlignmentFileLocation()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// each component is separated by tab character
				lineNumber++;
				if ((lineNumber != 1) && !line.contains("###")) {
					String[] lineSplitter = line.split("\t");
					if (lineSplitter.length >= 2) {
						String sourceID = lineSplitter[0];
						String destID = lineSplitter[1]; 

						if (!sourceID.equals("null") && !destID.equals("null")) {
							debug = line;
							List<Sense> sourceSenses;
							sourceSenses = saUtils.getSensesByExternalRefID(
									sourceID, 0, false);
							List<Sense> senseWKN = saUtils.getSensesByExternalRefID(
									destID, 1, false);
							if (sourceSenses.size() != 0
									&& senseWKN.size() != 0) {
								for (Sense FNSense : sourceSenses) {
									if(senseWKN.get(0).getId().startsWith("WktEN")) {
										addSourceSense(FNSense);
										addDestSense(senseWKN.get(0));
									}
									else if (senseWKN.size()>1 && senseWKN.get(1).getId().startsWith("WktEN")) {
										addSourceSense(FNSense);
										addDestSense(senseWKN.get(1));
										
									} else {
										System.out.println("no target sense found: "+line);
									}
								}
							}
							else {
								logString.append(debug);
								logString.append(LF);
							}
						}
						else {
							nullAlignment++;
						}
					}
				}
			}
			//restore memories
			saUtils.destroyTempTable();
		}
		catch (Exception ex) {
			IOUtils.closeQuietly(reader);
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param classifierOut - output tsv of weka classification
	 * @param classifierIn - data section of arff input file for classification
	 *        (contains ids in same order as classifications in classifierOut)
	 * @param tsvFile
	 * @throws IOException 
	 */
	public static void classifierOutputToTsv(String classifierOut, String classifierIn, String tsvFile) throws IOException{
			List<String> res = new ArrayList<String>();
			// read classification
			InputStream is = new BufferedInputStream(new FileInputStream(new File(classifierOut)));
			Reader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			line = br.readLine();
			ArrayList<String> scoreLines = new ArrayList<String>();
			while (line!=null){
				scoreLines.add(line);
				line = br.readLine();
				System.out.println(scoreLines.size());
			}
			// read ids
			InputStream is2 = new BufferedInputStream(new FileInputStream(new File(classifierIn)));
			Reader reader2 = new InputStreamReader(is2);
			BufferedReader br2 = new BufferedReader(reader2);
			String line2 = br2.readLine();
			line2 = br2.readLine();
			ArrayList<String> idLines = new ArrayList<String>();
			while (line2!=null){
					idLines.add(line2);
					line2 = br2.readLine();
			}
			System.out.println(scoreLines.size());
			System.out.println(idLines.size());
			if (scoreLines.size()!=idLines.size()){// 
				logger.warn("files do not agree");
			}
			int positive = 0;
			int negative = 0;
			 for (int i=0;i<scoreLines.size();i++){
			 	String[] scoreitems = scoreLines.get(i).split(":");
			 	String[] iditems = idLines.get(i).split(",");
			 	String first = iditems[0];
			 	String second = iditems[1];
			 	String sysScore = scoreitems[2].split(",")[0];
			 	if (sysScore.equals("1")){// pair classified as alignment
					res.add(first + "\t"+ second);
					positive++;
			 	} else {
			 		negative++;
			 	}
			 }
			 logger.info("positive class-->added as alignment: " + positive);
			 logger.info("negative class-->no alignment: " + negative);
			 System.out.println("write positive class to file");
			 FileWriter  fw = new FileWriter(new File(tsvFile));
			 for (String r: res){
				 fw.write(r+"\n");
			 }
			 fw.close();
			 br2.close();
			 br.close();
		}
}

