package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import de.tudarmstadt.ukp.alignment.framework.Global;

public class CreateAlignmentFromGraphOutput
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		/* GLOBAL SETTINGS */

		Global.init();

		try
		{
		/*RESOURCE 1*/

		boolean synset1 = true;
		boolean usePos1 = true;
		final int monoLinkThreshold1 = 1000;
		final int prefix1 = Global.WN_Synset_prefix;
		final String prefix_string1 = Global.prefixTable.get(prefix1);

		/*RESOURCE 2*/
		boolean synset2 = true;
		boolean usePos2 = true;
		final int monoLinkThreshold2 = 500;
		final int prefix2 = Global.OW_EN_Synset_prefix;
		final String prefix_string2 = Global.prefixTable.get(prefix2);

		/*Alignment parameters*/
		int depth = 5; // Manually set; exhaustive search can be triggered by depth >20
		boolean allowMultiple = true;
		boolean alignSingle = false;
		boolean backoff=false;
		String backoff_file = "";


		createAlignment("target/"+prefix_string1+"_"+prefix_string2+"_candidates_"+(usePos2 ? "Pos": "noPos")+".txt",
				"target/"+prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
				+"_MERGED_"+
				prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+
				"_trivial_result.txt",
				"target/"+prefix_string1+"_"+prefix_string2+"_alignment_"+(usePos2 ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":""),
				 depth, allowMultiple,alignSingle, backoff, backoff_file);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void mapAlignmentToUby(String candidate_file, String result_file, String output, int depth, boolean allowMultiple,boolean alignSingle, boolean backoff, String backoff_file)
	{
		/*TODO: Conform to newly defined standard*/
		/*TODO: Parameter: UBYId or extRef?
		 *
		 * Uby sollte jetzt easy nur über die Id zu machen sein, ohne externe Ressourcen, Indexe, etc...
		 *
		 * Man könnte in "Global" sogar eine separate Methofde "mapIDtoUby" machen, Jawohl!!!
		 *
		 *
		 * */
		/*TODO: Create actual SenseAxis instances? NO! Use import class in UBY!*/
	}

	public static void createAlignment(String candidate_file, String result_file, String output, int depth, boolean allowMultiple,boolean alignSingle, boolean backoff, String backoff_file)
	{

		HashMap<String,TreeSet<NodeWithDistance> > alignment_results = new HashMap<String, TreeSet<NodeWithDistance>>();
		HashMap<String,HashSet<String> > reverse_candidates = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > candidates = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > backoff_alignments = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > final_clusters = new HashMap<String, HashSet<String>>();
		try
		{
		FileReader in = new FileReader(candidate_file);
		BufferedReader input =  new BufferedReader(in);
		FileReader in2 = new FileReader(result_file);
		BufferedReader input2 =  new BufferedReader(in2);

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
				current_id1 = line.split(" ")[1];
				current_id2 = line.split(" ")[2];
			    distance = line2.split(" ")[1];
				if(alignment_results.get(current_id1)==null)
				{
					alignment_results.put(current_id1, new TreeSet<NodeWithDistance>());
				}
				}
//					if(distance.length()>3)
//					{
//						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2),1000);
//						alignment_results.get(current_id1).add(nwd);
//					}
//					else
					{
						NodeWithDistance nwd = new NodeWithDistance(Integer.parseInt(current_id2), Integer.parseInt(distance));
						alignment_results.get(current_id1).add(nwd);
					}
					}


		}
		in.close();
		in2.close();

		/*HERE THE ACTUAL ANALYISIS BEGINS*/


		candidates  = new HashMap<String, HashSet<String>>();
		FileOutputStream outstream = new FileOutputStream(output);
		PrintStream p = new PrintStream( outstream );
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



					if(backoff) //Einfach adden???
					{
						in = new FileReader("target/"+backoff_file) ;
						input =  new BufferedReader(in);
						while((line = input.readLine())!=null)
						{
							String id_1 = line.split("\t")[0];
							String id_2 = line.split("\t")[1];
							if(!backoff_alignments.containsKey(id_1))
							{
								backoff_alignments.put(id_1, new HashSet<String>());
							}
							backoff_alignments.get(id_1).add(id_2);
						}

						for(String key : backoff_alignments.keySet())
						{

							if(candidates.containsKey(key)) {
								System.out.println("Already aligned!!");
								continue;

							}
							candidates.put(key, new HashSet<String>());
							for(String value : backoff_alignments.get(key))
							{
								String id_2 = value;

//								if(!confidence)
								{
									//p.println(key+" "+value);
								}
//								else
								{
									p.println(key+" "+value+" "+"SIM");
								}


							}
						}
					}
					}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}




}
