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

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;

public class WiktionaryWNAlignment extends SenseAlignment
{
	private String debug;
	public StringBuilder logString;
	public int nullAlignment;

	private final SenseAlignmentUtils saUtils;

	public WiktionaryWNAlignment(String sourceUrl, String destUrl, String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass,String UBY_HOME)
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl, alignmentFile);
		logString = new StringBuilder();
		nullAlignment = 0;

		// new
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, true);
		// temp_Duc is the name of temporary table
		if (sourceUrl.equals(destUrl)) {
			saUtils = new SenseAlignmentUtils(s, s, 1, 0, "temp_source", "temp_dest");
		}else{
			saUtils = new SenseAlignmentUtils(s, d, 1, 0, "temp_source", "temp_dest");
		}
		saUtils.createDefaultTempTables(false);
	}

	@Override
	public void getAlignment()
	{
		int lineNumber = 0;
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					getAlignmentFileLocation()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// each component is separated by tab character
				lineNumber++;
				if (lineNumber != 1) {
					String[] line_splitter = line.split("\t");
					if (line_splitter.length == 2) {
						String sourceID = line_splitter[0];
						String destID = line_splitter[1];

						if (!sourceID.equals("null") && !destID.equals("null")) {
							debug = line;
							List<Sense> sourceSenses;
							// direct connection
							String[]temp= sourceID.split("-");
							String refId="[POS: noun] "+temp[0];

							if (temp[1].equals("a")){
								refId=refId.replaceAll("noun", "adjective");
							}else if (temp[1].equals("r")){
								refId=refId.replaceAll("noun", "adverb");
							}else if (temp[1].equals("v")){
								refId=refId.replaceAll("noun", "verb");
							}

							sourceSenses = saUtils.getSensesByExternalRefID(
									refId, 0, false);
							List<Sense> senseWKN = saUtils
									.getSensesByExternalRefID(destID, 1,false);

							// System.out.println("Sense Alignment between "+senseKeys.get(0).getSynset().getId()+
							// " and " +senseKeysWKN.get(0).getId());
							System.out.println(count);
							if (sourceSenses.size() != 0 && senseWKN.size() != 0) {
								for (Sense WordnetSense : sourceSenses) {

									count++;
									addSourceSense(WordnetSense);
									if(senseWKN.get(0).getId().startsWith("WktEN")) {
										addDestSense(senseWKN.get(0));
									}
									else if (senseWKN.size()>1 && senseWKN.get(1).getId().startsWith("WktEN")) {
										addDestSense(senseWKN.get(1));
									}
								}
							}
							else {
								System.out.println(sourceSenses.size() + "  " + senseWKN.size());
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
			reader.close();
			//restore memories
			saUtils.destroyTempTable();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

