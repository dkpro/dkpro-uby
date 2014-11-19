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
package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


import de.tudarmstadt.ukp.alignment.framework.Global;
import grph.Grph;

import grph.io.GraphBuildException;
import grph.io.ParseException;
import grph.path.Path;


public class CalculateDijkstraWSA
{
	public static void main(String args[]) throws IOException, ParseException, GraphBuildException
	{
		calculateDijkstraWSAdistances("target/WN_synset_Pos_relationMLgraph_1000_MERGED_WktEn_sense_Pos_relationMLgraph_2000_trivial.txt", "target/WN_WktEn_GScandidates_noCheck.txt");
	}
	
	/***
	 * This method takes a graph and candidate file as input and calculates the distances between the candidates in the graph
	 * 
	 * 
	 * @param graph_file The graph
	 * @param candidate_file The candidate pairs
	 */
	
	public static void calculateDijkstraWSAdistances(String graph_file, String candidate_file) throws IOException, ParseException, GraphBuildException
	{
		try
		{
				FileReader in = new FileReader(graph_file);
				BufferedReader inp =  new BufferedReader(in);
				String line;
				StringBuilder graph = new StringBuilder();
				 
				 while((line =inp.readLine())!=null)
				 {
					 graph.append(line+Global.LF);
				 }
				 inp.close();
				Grph g = Grph.fromGrphText(graph.toString());
				FileOutputStream outstream;
				PrintStream p;
				
				outstream = new FileOutputStream(graph_file.replace(".txt","_result.txt"));

				p = new PrintStream( outstream );
				in = new FileReader(candidate_file);
				inp =  new BufferedReader(in);
				
				 while((line =inp.readLine())!=null)
				 {
					if(line.startsWith("q"))
					{
						int id1 = Integer.parseInt(line.split(" ")[1]);
						int id2 = Integer.parseInt(line.split(" ")[2]);
						try
						{
						Path path = g.getShortestPath(id1, id2);
						p.println(id1+"\t"+id2+"\t"+path.getLength());
						}
						catch(Exception ise)
						{
							p.println(id1+"\t"+id2+"\t"+1000);							
						}
					}
				 }
				 p.close();
				 inp.close();
	}

	catch(Exception e)
	{
		e.printStackTrace();
	}
	}
}
