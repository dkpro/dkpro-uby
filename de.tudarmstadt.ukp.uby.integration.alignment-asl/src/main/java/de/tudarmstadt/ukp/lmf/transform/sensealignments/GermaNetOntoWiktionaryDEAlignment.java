/*******************************************************************************
 * Copyright 2017
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
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;

/**
 * Converts GermaNet sense ID - WiktionaryDE sense ID alignment file to UBY format (SenseAxis)
 * by looking up original sense IDs in a Uby database containing GermaNet and OntoWiktionary DE
 * 
 */
public class GermaNetOntoWiktionaryDEAlignment extends SenseAlignment{
	
	private String debug;
	public StringBuilder logString;
	public int nullAlignment;
	protected static Logger logger = Logger
			.getLogger(FrameNetWiktionaryAlignment.class.getName());
	private Uby uby;

	public GermaNetOntoWiktionaryDEAlignment(String sourceUrl, String destUrl, String dbDriver,String dbVendor,
			String alignmentFile, String user, String pass)
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException
	{
		super(sourceUrl, destUrl, alignmentFile);
		logString = new StringBuilder();
		nullAlignment = 0;
		
		DBConfig db = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		uby = new Uby(db);
		
		// source and target DBConfig is never used! just initiated because class extends SenseAlignment
		DBConfig s = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, true);
		DBConfig d = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, true);
	}

	@Override
	public void getAlignment()
	{
		// source = GermaNet, destination = WiktionaryDE
		int lineNumber = 0;
		BufferedReader reader = null;
		String targetPrefix = "OntoWktDE";
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
														
							// externalSystem="GermaNet_9.0_deu_lexicalUnit" externalReference="65739"
							// externalSystem="Wiktionary_1.0.0_2013-02-20_deu_sense" externalReference="113219:0:1
							List<Sense> sourceSenses = uby.getSensesByOriginalReference("GermaNet_9.0_deu_lexicalUnit", sourceID);
							List<Sense> destSenses = uby.getSensesByOriginalReference("Wiktionary_1.0.0_2013-02-20_deu_sense", destID);
							
							if (sourceSenses.size() != 0
									&& destSenses.size() != 0) {
								for (Sense sourceSense : sourceSenses) {
									if(destSenses.get(0).getId().startsWith(targetPrefix)) {
										addSourceSense(sourceSense); 										
										addDestSense(destSenses.get(0)); 
										
										System.out.println(sourceID +" " +sourceSense +"\t" 
												+destID +" " +destSenses.get(0));
									}
									else if (destSenses.size()>1 && destSenses.get(1).getId().startsWith(targetPrefix)) {
										addSourceSense(sourceSense); 
										addDestSense(destSenses.get(1)); 
										System.out.println(sourceID +" " +sourceSense +"\t" 
												+destID +" " +destSenses.get(0));

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
		}
		catch (Exception ex) {
			IOUtils.closeQuietly(reader);
			ex.printStackTrace();
		}
	}

}
