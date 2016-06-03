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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;

/**
 * Converts GermaNet sense ID - WiktionaryDE sense ID alignment file to UBY format (SenseAxis)
 * @author Judith Eckle-Kohler
 * 
 */
public class GermaNetWiktionaryDeAlignment extends SenseAlignment{
	
	private String debug;
	public StringBuilder logString;
	public int nullAlignment;
    protected static Log logger = LogFactory.getLog(FrameNetWiktionaryAlignment.class);
	private final SenseAlignmentUtils saUtils;

	public GermaNetWiktionaryDeAlignment(String sourceUrl, String destUrl, String dbDriver,String dbVendor,
			String alignmentFile, String user, String pass, String UBY_HOME)
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl, alignmentFile);
		logString = new StringBuilder();
		nullAlignment = 0;

		// source = GermaNet, destination = WiktionaryDE
		DBConfig sourceDbConfig = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig destDbConfig = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, true);
		System.out.println("here");

		if (sourceUrl.equals(destUrl)) {
			saUtils = new SenseAlignmentUtils(sourceDbConfig, sourceDbConfig, 0, 0, "tempTableSource", "tempTableSource");
			// type of source ID = destination ID = 0 - this means the alignment is between sense ID (in contrast to synset ID)
		}
		else {
			saUtils = new SenseAlignmentUtils(sourceDbConfig, destDbConfig, 0, 0, "tempTableSource", "tempTableDest");
		}
		String decFields1 =  "senseId varchar(255) NOT NULL, externalReference varchar(255)"; // declare fields
		String insData1 = "SELECT S.senseId, "
				+ " M.externalReference  "
				+ " FROM Sense S JOIN MonolingualExternalRef M"
				+ " ON (S.senseId=M.senseId)"
				+ "where substring(S.senseId,1,2)=\"GN\"";
		String insData2 = "SELECT S.senseId, "
				+ " M.externalReference  "
				+ " FROM Sense S JOIN MonolingualExternalRef M"
				+ " ON (S.senseId=M.senseId)"
				+ "where substring(S.senseId,1,4)=\"WktD\"";
		saUtils.createTempTable(decFields1, insData1, 0);
		saUtils.createTempTable(decFields1, insData2, 1);
	}

	@Override
	public void getAlignment()
	{
		// source = GermaNet, destination = WiktionaryDE
		int lineNumber = 0;
		BufferedReader reader = null;
		try {
			 reader = new BufferedReader(new FileReader(
					getAlignmentFileLocation()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// each component is separated by tab character
				lineNumber++;
				if (lineNumber != 1) {
					String[] line_splitter = line.split("\t");
					if (line_splitter.length >= 2) {
						String germaNetID = line_splitter[0];
						String sourceID = germaNetID.replaceAll("l", "");
						System.out.println(sourceID);
						String destID = line_splitter[1]; 

						if (!sourceID.equals("null") && !destID.equals("null")) {
							debug = line;
							List<Sense> sourceSenses = saUtils.getSensesByExternalRefID(
									sourceID, 0, false);
							List<Sense> destSenses = saUtils
									.getSensesByExternalRefID(destID, 1,false);
							if (sourceSenses.size() != 0
									&& destSenses.size() != 0) {
								for (Sense sourceSense : sourceSenses) {
									if(destSenses.get(0).getId().startsWith("WktDE")) {
										addSourceSense(sourceSense);
										addDestSense(destSenses.get(0));
									}
									else if (destSenses.size()>1 && destSenses.get(1).getId().startsWith("WktDE")) {
										addSourceSense(sourceSense);
										addDestSense(destSenses.get(1));
										
									} else {
										System.out.println("no target sense found: "+line +" " +sourceSenses
												+" " +destSenses);
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

}

