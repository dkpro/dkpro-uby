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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class CreateAlignmentFromGraphOutput
{

	/**
	 *This method creates an alignment from the distances output by the Dijkstra-WSA implementation and outputs them in the desired format
	 */
	public static void main(String[] args)
	{
		/* GLOBAL SETTINGS */

		Global.init();
		final String language = ELanguageIdentifier.ENGLISH;
		try
		{
		/*RESOURCE 1*/

		boolean synset1 = true;
		boolean usePos1 = true;
		final int monoLinkThreshold1 = 1000;
		final int prefix1 = Global.WN_Synset_prefix;
		OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);

		/*RESOURCE 2*/
		boolean synset2 = false;
		boolean usePos2 = true;
		final int monoLinkThreshold2 = 2000;
		final int prefix2 = Global.WKT_EN_prefix;
		OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);

		/*Alignment parameters*/
		int depth = 5; // Manually set; exhaustive search can be triggered by depth >20
		boolean allowMultiple = true; //allow 1:n alignments 
		boolean alignSingle = false; //allow instant alignment in case of only ine candidate
 
		boolean backoff=false;  //use a similarity-based backoff file in case no alignment can be found
		String backoff_file = "WN_OW_en_alignment_similarity_Pos_tfidf_nonZero.txt";

		createAlignment(bg_1,bg_2,monoLinkThreshold1,monoLinkThreshold2, depth, allowMultiple,alignSingle, backoff, backoff_file);

		boolean extRef = false; //Use either UBY-Ids or the original IDs for the final alignment file

		Global.mapAlignmentToUby(bg_1,bg_2,"target/"+bg_1.prefix_string+"_"+bg_2.prefix_string+"_alignment_dwsa_"+(bg_2.pos ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":"")+".txt", extRef);
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * This method creates an alignment from the distances output by the Dijkstra-WSA implementation
	 */
	@Deprecated
	public static void createAlignmentOldGraphFormat(OneResourceBuilder gb1,OneResourceBuilder gb2, int monoLinkThreshold1, int monoLinkThreshold2,  int depth, boolean allowMultiple,boolean alignSingle, boolean backoff, String backoff_file, boolean all_distances, String candidate_file)
	{

		HashMap<String,TreeSet<NodeWithDistance> > alignment_results = new HashMap<String, TreeSet<NodeWithDistance>>();
		HashMap<String,HashSet<String> > candidates = new HashMap<String, HashSet<String>>();
		
		//Read the candidates and distance files
		try
		{
		FileReader in = new FileReader(candidate_file);
		BufferedReader input =  new BufferedReader(in);
		FileReader in2 = new FileReader("target/"+gb1.prefix_string+"_"+(gb1.synset?"synset":"sense")+"_"+(gb1.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
				+"_MERGED_"+
				gb2.prefix_string+"_"+(gb2.synset?"synset":"sense")+"_"+(gb2.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+
				"_trivial_result.txt");
		BufferedReader input2 =  new BufferedReader(in2);
		FileOutputStream outstream;
		PrintStream p = null;
		if(all_distances)
		{
			 outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_distances_dwsa_"+(gb2.pos ? "Pos": "noPos")+".txt");
			 p = new PrintStream( outstream );
			 p.println("f\t"+gb1.prefix_string+"_"+gb2.prefix_string+"_candidates_"+(gb2.pos ? "Pos": "noPos")+".txt"+"\t"+"DWSA distances");
		}
		String current_id1 ="";
		String current_id2 ="";
		String distance ="";
		String line = "";
		String line2 = "";
		int i =0;
		while((line = input.readLine())!=null && (line2 = input2.readLine())!=null)
		{

			if(line.startsWith("p") || line.startsWith("f")) {
				continue;
			}
			if(line.startsWith("q"))
			{
				if(line2.startsWith("d"))
				{
				System.out.println("Source Nodes parsed "+i++);
//				current_id1 = line.split(" ")[1];
//				current_id2 = line.split(" ")[2];
				current_id1 = line.split("\t")[0];
				current_id2 = line.split("\t")[1];
			    distance = line2.split(" ")[1];
			    if(distance.length()>3)
				{
			    	distance = "1000";
				}
			    if(all_distances)
			    {
			    	p.println(current_id1+"\t"+current_id2+"\t"+distance);
			    	continue;
			    }
				if(alignment_results.get(current_id1)==null)
				{
					alignment_results.put(current_id1, new TreeSet<NodeWithDistance>());
				}
				
					if(distance.length()>3)
					{
						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2),1000);
						alignment_results.get(current_id1).add(nwd);
					}
					else
					{
						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2), Integer.parseInt(distance));
						alignment_results.get(current_id1).add(nwd);
					}
					}
			}

		}
		in.close();
		in2.close();

		/*HERE THE ACTUAL ANALYISIS BEGINS*/

		if(!all_distances)
		{
		candidates  = new HashMap<String, HashSet<String>>();
		 outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_alignment_dwsa_"+(gb2.pos ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":"")+".txt");
		 p = new PrintStream( outstream );
		for(String s : alignment_results.keySet())
		{
				TreeSet<NodeWithDistance> cands = alignment_results.get(s);
				TreeSet<NodeWithDistance> polled_out = new TreeSet<NodeWithDistance>();
				HashSet<NodeWithDistance> targets = new HashSet<NodeWithDistance>();
				int observed_d = 0;
				while(observed_d <=depth)
				{
					NodeWithDistance nwd =cands.pollFirst();
					if(nwd==null) {
						break;
					}
					polled_out.add(nwd);
					observed_d = nwd.path_length;
					if(observed_d<=depth || (cands.size() ==1  && alignSingle))
					{
						targets.add(nwd);
					}
					if(!allowMultiple) {
						break;
					}
				}
				if(cands!=null && polled_out!= null && !polled_out.isEmpty()) {
							cands.addAll(polled_out);
							
				/*HERE THE OUTPUT BEGINS*/
				}
				for(NodeWithDistance t : targets)
				{
						/*Preparation for Backoff*/
							if(!candidates.containsKey(s)) {
								candidates.put(s, new HashSet<String>());
							}
							candidates.get(s).add(t+"");
							p.println(s+"\t"+t.id+"\t"+t.path_length);

						}

		 		}



					if(backoff) // We add the alignment from the backoff for this which were not aligned using DWSA
					{
						in = new FileReader("target/"+backoff_file) ;
						input =  new BufferedReader(in);
						while((line = input.readLine())!=null)
						{
							if(line.startsWith("f")) {
								continue;
							}
							String id_1 = line.split("\t")[0];
							String id_2 = line.split("\t")[1];
							String conf = line.split("\t")[2];

							if(candidates.containsKey(id_1)) {
								System.out.println("Already aligned!!");
								continue;

							}
							p.println(id_1+"\t"+id_2+"\t"+conf);

						}

					}
		
					p.close();
					}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * This method creates an alignment from the distances output by the Dijkstra-WSA implementation
	 */
	public static void createAlignment(OneResourceBuilder gb1,OneResourceBuilder gb2, int monoLinkThreshold1, int monoLinkThreshold2,  int depth, boolean allowMultiple,boolean alignSingle, boolean backoff, String backoff_file)
	{

		HashMap<String,TreeSet<NodeWithDistance> > alignment_results = new HashMap<String, TreeSet<NodeWithDistance>>();
		HashMap<String,HashSet<String> > candidates = new HashMap<String, HashSet<String>>();
		
		//Read the candidates and distance files
		try
		{

		FileReader in2 = new FileReader("target/"+gb1.prefix_string+"_"+(gb1.synset?"synset":"sense")+"_"+(gb1.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
				+"_MERGED_"+
				gb2.prefix_string+"_"+(gb2.synset?"synset":"sense")+"_"+(gb2.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+
				"_trivial_result.txt");
		BufferedReader input2 =  new BufferedReader(in2);
		FileOutputStream outstream;
		PrintStream p = null;
		String current_id1 ="";
		String current_id2 ="";
		String distance ="";
		
		String line2 = "";
		int i =0;
		while( (line2 = input2.readLine())!=null)
		{

				System.out.println("Source Nodes parsed "+i++);
//				current_id1 = line.split(" ")[1];
//				current_id2 = line.split(" ")[2];
				current_id1 = line2.split("\t")[0];
				current_id2 = line2.split("\t")[1];
			    distance = line2.split("\t")[2];
			    if(distance.length()>5)
				{
			    	distance = "1000";
				}
				if(alignment_results.get(current_id1)==null)
				{
					alignment_results.put(current_id1, new TreeSet<NodeWithDistance>());
				}
				
					if(distance.length()>3)
					{
						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2),1000);
						alignment_results.get(current_id1).add(nwd);
					}
					else
					{
						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2), Integer.parseInt(distance));
						alignment_results.get(current_id1).add(nwd);
					}
					
			

		}

		in2.close();

		/*HERE THE ACTUAL ANALYISIS BEGINS*/

		candidates  = new HashMap<String, HashSet<String>>();
		outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_alignment_dwsa_"+(gb2.pos ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":"")+".txt");
		p = new PrintStream( outstream );
		for(String s : alignment_results.keySet())
		{
				TreeSet<NodeWithDistance> cands = alignment_results.get(s);
				TreeSet<NodeWithDistance> polled_out = new TreeSet<NodeWithDistance>();
				HashSet<NodeWithDistance> targets = new HashSet<NodeWithDistance>();
				int observed_d = 0;
				while(observed_d <=depth)
				{
					NodeWithDistance nwd =cands.pollFirst();
					if(nwd==null) {
						break;
					}
					polled_out.add(nwd);
					observed_d = nwd.path_length;
					if(observed_d<=depth || (cands.size() ==1  && alignSingle))
					{
						targets.add(nwd);
					}
					if(!allowMultiple) {
						break;
					}
				}
				if(cands!=null && polled_out!= null && !polled_out.isEmpty()) {
							cands.addAll(polled_out);
							
				/*HERE THE OUTPUT BEGINS*/
				}
				for(NodeWithDistance t : targets)
				{
						/*Preparation for Backoff*/
							if(!candidates.containsKey(s)) {
								candidates.put(s, new HashSet<String>());
							}
							candidates.get(s).add(t+"");
							p.println(s+"\t"+t.id+"\t"+t.path_length);

						}

		 		}



					if(backoff) // We add the alignment from the backoff for this which were not aligned using DWSA
					{
						in2 = new FileReader("target/"+backoff_file) ;
						input2 =  new BufferedReader(in2);
						while((line2 = input2.readLine())!=null)
						{
							if(line2.startsWith("f")) {
								continue;
							}
							String id_1 = line2.split("\t")[0];
							String id_2 = line2.split("\t")[1];
							String conf = line2.split("\t")[2];

							if(candidates.containsKey(id_1)) {
								System.out.println("Already aligned!!");
								continue;

							}
							p.println(id_1+"\t"+id_2+"\t"+conf);

						}

					}
		
					p.close();
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
