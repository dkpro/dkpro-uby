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
package de.tudarmstadt.ukp.alignment.framework.candidates;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class CandidateExtractor
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{

			/* GLOBAL SETTINGS */

			Global.init();
			final String language = ELanguageIdentifier.ENGLISH;

			/*RESOURCE 1*/

			boolean synset1 = true;
			boolean usePos1 = true;
			final int prefix1 = Global.WN_Synset_prefix;

			//OneGraphBuilder bg_1 = new OneGraphBuilder("uby_lite_0_4_0","root","fortuna");
			OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);

			bg_1.fillIndexTables();

			/*RESOURCE 2*/
			boolean synset2 = false;
			boolean usePos2 = true;
			final int prefix2 = Global.WKT_EN_prefix;
			OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);
			bg_2.fillIndexTables();




			/*Calculate alignment candidates between the two LSRs*/
			/*Index tables must be filled at this point*/

	//	Global.processExtRefGoldstandardFileWKTWP(bg_1, bg_2, "target/ijcnlp2011-meyer-dataset.txt", true);
	//	createCandidateFileFull(bg_1, bg_2);
	//	createCandidateFileGoldStandard(bg_1, bg_2, "target/ijcnlp2011-meyer-dataset_graph.csv",false);
	//	createCandidateFileLemmaList(bg_1, bg_2, "target/lemmas.tsv");



	}


	/**
	 * This method extracts the possible alignment candidates (those with matching lemma and POS) from two resources
	 *
	 *
	 *
	 *
	 */
	public static void createCandidateFileFull(OneResourceBuilder gb1, OneResourceBuilder gb2) throws ClassNotFoundException, SQLException, IOException
	{
		StringBuilder sb = new StringBuilder();
		int count = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_candidates_"+(gb2.pos ? "Pos": "noPos")+".txt");
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(gb2.pos) {
				if(gb2.lemmaPosSenses.get(lemmaPos)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemmaPos))
						{
							sb.append("q "+id1+" "+id2+""+Global.LF);
							count++;
						}
					}
				}
			}
			else
			{
				String lemma = lemmaPos.split("#")[0];
				if(gb2.lemmaPosSenses.get(lemma)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemma))
						{
							sb.append("q "+id1+" "+id2+""+Global.LF);
							count++;
						}
					}
				}
			}

		}
	p.println("p aux sp p2p "+count);
	p.print(sb.toString());
	p.close();
	}
	/**
	 * This method creates a list of alignment candidates (those with matching lemma and POS) from a given list
	 *
	 *
	 *
	 *
	 */
	public static void createCandidateFileLemmaList(OneResourceBuilder gb1, OneResourceBuilder gb2,String input) throws ClassNotFoundException, SQLException, IOException
	{

		HashMap<String, String> lemmaPosList = new HashMap<String,String>();


		FileReader in = new FileReader(input);
		BufferedReader input_reader =  new BufferedReader(in);
		String line;

		while((line =input_reader.readLine())!=null)
		{
			lemmaPosList.put(line.split("\t")[0],line.split("\t")[1]);
		}

		StringBuilder sb = new StringBuilder();
		int count = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_LemmaListCandidates_"+(gb2.pos ? "Pos": "noPos")+".txt");
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			String lemma = lemmaPos.split("#")[0];
			String pos = lemmaPos.split("#")[1];
			System.out.println(lemma);
			System.out.println(pos);
			if(!(lemmaPosList.containsKey(lemma) && lemmaPosList.get(lemma).equals(pos))) {
                continue;
            }
			if(gb2.pos) {
				if(gb2.lemmaPosSenses.get(lemmaPos)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemmaPos))
						{
							sb.append("q "+id1+" "+id2+""+Global.LF);
							count++;
						}
					}
				}
			}
			else
			{

				if(gb2.lemmaPosSenses.get(lemma)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemma))
						{
							sb.append("q "+id1+" "+id2+""+Global.LF);
							count++;
						}
					}
				}
			}

		}
	p.println("p aux sp p2p "+count);
	p.print(sb.toString());
	p.close();
	input_reader.close();
	in.close();
	}


	/**
	 * This method extracts the possible alignment candidates from a gold standard file
	 *
	 *
	 *
	 * @param checkIntegrity This parameter toggles if the gold standard should be checked for correctness of lemma/POS combinations. If unchecked, the GS is just output in the correct format
	 */
	public static void createCandidateFileGoldStandard(OneResourceBuilder gb1, OneResourceBuilder gb2,String input, boolean checkIntegrity) throws ClassNotFoundException, SQLException, IOException
	{


		int count = 0;
		HashSet<String> candidates = new HashSet<String>();
		FileReader in = new FileReader(input);
		BufferedReader input_reader =  new BufferedReader(in);
		String line;
		StringBuilder sb = new StringBuilder();
		while((line =input_reader.readLine())!=null)
		{
			if(checkIntegrity)
			{
				candidates.add(line.split("\t")[0]+"###"+line.split("\t")[1]);
			}
			else
			{
				sb.append("q "+line.split(" ")[0]+"\t"+line.split(" ")[1]+""+Global.LF);
				count++;
			}
		}

		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_GScandidates_"+(!checkIntegrity? "noCheck":(gb2.pos ? "Pos": "noPos"))+".txt");
		p = new PrintStream( outstream );
		if(checkIntegrity)
		{
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(gb2.pos) {
				if(gb2.lemmaPosSenses.get(lemmaPos)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemmaPos))
						{
							if(candidates.contains(id1+"###"+id2))
							{
								sb.append("q "+id1+" "+id2+""+Global.LF);
								count++;
							}
						}
					}
				}
			}
			else
			{
				String lemma = lemmaPos.split("#")[0];
				if(gb2.lemmaPosSenses.get(lemma)!= null)
				{
					for(String id1 :gb1.lemmaPosSenses.get(lemmaPos))
					{
						for(String id2 :gb2.lemmaPosSenses.get(lemma))
						{
							if(candidates.contains(id1+"###"+id2))
							{
								sb.append("q "+id1+" "+id2+""+Global.LF);
								count++;
							}
						}
					}
				}
			}

		}
		}
	p.println("p aux sp p2p "+count);
	p.print(sb.toString());
	p.close();
	input_reader.close();
	in.close();
	}



}
