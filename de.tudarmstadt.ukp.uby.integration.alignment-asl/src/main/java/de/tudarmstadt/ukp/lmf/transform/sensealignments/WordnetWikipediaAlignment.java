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

public class WordnetWikipediaAlignment extends SenseAlignment
{
	public StringBuilder logString;
	private final SenseAlignmentUtils saUtils;

	public WordnetWikipediaAlignment(String sourceUrl, String destUrl,String dbDriver,String dbVendor,
			String alignmentFile, String user, String pass,String UBY_HOME)
		throws SQLException, InstantiationException, IllegalAccessException,
		ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl, alignmentFile);
		logString = new StringBuilder();

		// new
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl, user,dbDriver,dbVendor, pass, true);
		// temp_Duc is the name of temporary table
		if (sourceUrl.equals(destUrl)) {
			saUtils = new SenseAlignmentUtils(s, s, 1, 0, "temp_Duc", "temp_Duc");
		}
		else {
			saUtils = new SenseAlignmentUtils(s, d, 1, 0, "temp_Duc", "temp_Duc");
		}
		saUtils.createDefaultTempTables(true);
	}

	@Override
	public void getAlignment()
	{
		int lineNumber = 0;
		System.out.println("Starting getting alignment from "
				+ getAlignmentFileLocation());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					getAlignmentFileLocation()));
			String line = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				String temp[] = line.split("\t");

				// if connection is available
				if (temp.length == 2 ) {

					//String wordNetRef = "[POS: noun] " + temp[0]; // old format
					String wordNetRef = temp[0]; //
					System.out.println(wordNetRef);
					String wikiRef = temp[1];
					wikiRef = wikiRef.replaceAll("'", "\\'");
					wikiRef = wikiRef.replaceAll("\"", "\\\\\"");

					List<Sense> WNRefs = saUtils.getSensesByExternalRefID(wordNetRef, 0, true); // true, if wordNetRef is a Synset ID
					List<Sense> WikiRefs = saUtils.getSensesByExternalRefID(wikiRef,1,false);

					System.out.println("Line: "+lineNumber + " #alignments " +count);
					if (WNRefs.size() != 0 && WikiRefs.size() == 1) {
						for (Sense sense : WNRefs) {
							addSourceSense(sense);
							addDestSense(WikiRefs.get(0));
							count++;
						}
					}
					else {
						System.out.println(WNRefs.size() + " "
								+ WikiRefs.size() + " ??");
						logString.append(wordNetRef + "\t" + wikiRef);
						logString.append(LF);
					}

				}
			}
			reader.close();
			saUtils.destroyTempTable();
			System.out.println("Number of alignment:" + count);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
