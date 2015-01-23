/*******************************************************************************
 * Copyright 2015
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
package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Set;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class JointGraphBuilder
{

	/**
	 *
	 *This method is the "starting point" of the alignment framework, encoding the process from creation of the graphs to their merging into one big graph using monosemous linking  
	 *
	 *
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{

			/* GLOBAL SETTINGS */

			Global.init();
			String language = ELanguageIdentifier.ENGLISH; //We cover only the monolingual case for now
			
			/*RESOURCE 1*/

			boolean synset1 = true;
			boolean usePos1 = true;

			//Chose the resource we want to align by selecting the appropriate prefixes

			 int prefix1 = Global.WN_Synset_prefix;
			 String prefix_string1 = Global.prefixTable.get(prefix1);				

		     //Frequency threshold for the monosemous linking
			final int monoLinkThreshold1 = 1000;
			
			//Chunksize for the POS-Tagging of the glosses. This is mostly a memory issues, higher values are faster, but might lead to crashes
//			final int chunksize1 = 2000;
					
			 //Build the resource by using the appropriate databases	
			 
		    OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);
		
			//Create text files with glosses for the two resources, and do POS tagging
					    
//			bg_1.createGlossFile(false);
//			bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
			
			// Fill the index, build graphs from the relations and the monosemous linking - merge in the end
			bg_1.fillIndexTables();
//			bg_1.builtRelationGraphFromDb(false);
//			bg_1.createMonosemousLinks(monoLinkThreshold1);
//			Global.mergeTwoGraphs(prefix_string1+"_"+(synset1?"synset":"sense")+"_relationgraph.txt" ,
//					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold1+".txt",
//					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1+".txt");




			/*RESOURCE 2*/
			
			boolean synset2 = false;
			boolean usePos2 = true;
			final int prefix2 = Global.WKT_EN_prefix;
			final String prefix_string2 = Global.prefixTable.get(prefix2);
			final int monoLinkThreshold2 = 2000;
//			final int chunksize2 = 2000;
			OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna",prefix2,language,synset2,usePos2);

	
		//	bg_2.createGlossFile(false);
			//bg_2.lemmatizePOStagGlossFileInChunks(chunksize2);
			bg_2.fillIndexTables();
//			boolean filter = false;
// 			bg_2.builtRelationGraphFromDb(filter);
//			bg_2.createMonosemousLinks(monoLinkThreshold2);
//
//			Global.mergeTwoGraphs(prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph.txt" ,
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold2+".txt",
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt");


			/*Merge the two graphs*/

			Global.mergeTwoGraphs(
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1+".txt",
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt",
				//	prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph"+(filter ? "_filtered":"")+".txt",
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
					+"_MERGED_"+
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt"
					);

			/*Create trivial alignments between the two LSRs*/
			/*Index tables must be filled at this point!!!*/

			createTrivialAlignments(bg_1, bg_2);

			/*Merge the joint graphs and trivial alignments*/

			Global.mergeTwoGraphs(
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
					+"_MERGED_"+
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt",
					prefix_string1+"_"+prefix_string2+"_trivial_"+(usePos2 ? "Pos": "noPos")+".txt",
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
					+"_MERGED_"+
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+
					"_trivial.txt"
					);

			//Done! We now have two linked graphs which are connected via monosemous links
		}


	/**
	 *
	 * Creates the trivial alignment between two resource graphs, i.e. those between lemmas with only one sense in either LSR
	 *
	 * @param gb1: First LSR
	 * @param gb2: Second LSR
	 *
	 */
	public static void createTrivialAlignments(OneResourceBuilder gb1, OneResourceBuilder gb2) throws ClassNotFoundException, SQLException, IOException
		{
		StringBuilder sb = new StringBuilder();
		int edge_count = 0;
		int maxId = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream( "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_trivial_"+(gb2.pos ? "Pos": "noPos")+".txt");
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(gb1.lemmaPosSenses.get(lemmaPos).size()==1) //if there is only one sense for this lexeme
			{
				String id1= gb1.lemmaPosSenses.get(lemmaPos).iterator().next();
				int id1_int = Integer.parseInt(id1);
				if(gb2.pos) { //if resource 2 uses POS
					if(gb2.lemmaPosSenses.get(lemmaPos)!= null && gb2.lemmaPosSenses.get(lemmaPos).size()==1) //if there is only one sense for this lexeme
					{
						String id2= gb2.lemmaPosSenses.get(lemmaPos).iterator().next();
						int id2_int = Integer.parseInt(id2);
						//Retrieve the largest ID, so that it can be used as a value for the graph algortihm input file
						if(id1_int > maxId) {
							maxId = id1_int; 
						}
						if(id2_int > maxId) {
							maxId = id2_int;
						}
						//sb.append("a "+id1+" "+id2+" 1"+Global.LF); //edges are unweighted
						//sb.append("a "+id2+" "+id1+" 1"+Global.LF);
						sb.append("e"+edge_count+++" "+id1+" "+id2+Global.LF);
					
					}
				}
				else
				{
					String lemma = lemmaPos.split("#")[0];
					if(gb2.lemmaPosSenses.get(lemma)!= null && gb2.lemmaPosSenses.get(lemma).size()==1)
					{
						String id2= gb2.lemmaPosSenses.get(lemma).iterator().next();
						int id2_int = Integer.parseInt(id2);
						if(id1_int > maxId) {
							maxId = id1_int;
						}
						if(id2_int > maxId) {
							maxId = id2_int;
						}
//						sb.append("a "+id1+" "+id2+" 1"+Global.LF); //edges are unweighted
//						sb.append("a "+id2+" "+id1+" 1"+Global.LF);
						sb.append("e"+edge_count+++" "+id1+" "+id2+Global.LF);
						
					}
				}
			}
		}
		 //p.println("p sp "+maxId+" "+count);
		 String header = "graph class=grph.in_memory.InMemoryGrph";
		 p.println(header);
		 p.print(sb.toString());
		 p.close();
	}

	/**
	 *
	 * Calculates the overlap between the vocabulary used in two resources
	 *
	 * @param gb1: First LSR
	 * @param gb2: Second LSR
	 *
	 */
	public static void calculateLexicalGlossOverlap(OneResourceBuilder gb1, OneResourceBuilder gb2) throws ClassNotFoundException, SQLException, IOException
	{
		Set<String> lexemes1 = gb1.lemmaFreqInGlosses.keySet();
		Set<String> lexemes2 = gb2.lemmaFreqInGlosses.keySet();

		double size1 = lexemes1.size();
		double size2 = lexemes2.size();
		lexemes1.retainAll(lexemes2);
		double overlap = lexemes1.size();
		System.out.println(gb1.prefix_string+" "+size1);
		System.out.println(gb2.prefix_string+" "+size2);
		System.out.println("Common: "+" "+overlap);
		System.out.println(gb1.prefix_string+" overlap "+(overlap/size1));
		System.out.println(gb2.prefix_string+" overlap "+(overlap/size2));


	}
}
