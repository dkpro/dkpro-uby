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
	 * @param args
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
		boolean synset2 = true;
		boolean usePos2 = true;
		final int monoLinkThreshold2 = 500;
		final int prefix2 = Global.OW_EN_Synset_prefix;
		OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);

		/*Alignment parameters*/
		int depth = 5; // Manually set; exhaustive search can be triggered by depth >20
		boolean allowMultiple = true;
		boolean alignSingle = false;

		boolean backoff=true;
		String backoff_file = "WN_OW_en_alignment_similarity_Pos_tfidf_nonZero.txt";

		createAlignment(bg_1,bg_2,monoLinkThreshold1,monoLinkThreshold2, depth, allowMultiple,alignSingle, backoff, backoff_file);

		boolean extRef = true;

		//Global.mapAlignmentToUby(bg_1,bg_2,bg_1.prefix_string+"_"+bg_2.prefix_string+"_alignment_"+(bg_2.pos ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":""), extRef);
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	public static void createAlignment(OneResourceBuilder gb1,OneResourceBuilder gb2, int monoLinkThreshold1, int monoLinkThreshold2,  int depth, boolean allowMultiple,boolean alignSingle, boolean backoff, String backoff_file)
	{

		HashMap<String,TreeSet<NodeWithDistance> > alignment_results = new HashMap<String, TreeSet<NodeWithDistance>>();
		HashMap<String,HashSet<String> > reverse_candidates = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > candidates = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > backoff_alignments = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String> > final_clusters = new HashMap<String, HashSet<String>>();
		try
		{
		FileReader in = new FileReader("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_candidates_"+(gb2.pos ? "Pos": "noPos")+".txt");
		BufferedReader input =  new BufferedReader(in);
		FileReader in2 = new FileReader("target/"+gb1.prefix_string+"_"+(gb1.synset?"synset":"sense")+"_"+(gb1.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
				+"_MERGED_"+
				gb2.prefix_string+"_"+(gb2.synset?"synset":"sense")+"_"+(gb2.pos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+
				"_trivial_result.txt");
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
		FileOutputStream outstream = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_alignment_dwsa_"+(gb2.pos ? "Pos": "noPos")+"_"+depth+"_"+(allowMultiple? "1toN"  :"1to1")+(alignSingle ? "_alignSingle":"")+(backoff ? "_backoff":"")+".txt");
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



					if(backoff) //Einfach adden
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


//							if(!backoff_alignments.containsKey(id_1))
//							{
//								backoff_alignments.put(id_1, new HashSet<String>());
//							}
//							backoff_alignments.get(id_1).add(id_2+"#"+conf);
						}

//						for(String key : backoff_alignments.keySet())
//						{
//
//							if(candidates.containsKey(key)) {
//								System.out.println("Already aligned!!");
//								continue;
//
//							}
//							candidates.put(key, new HashSet<String>());
//							for(String value : backoff_alignments.get(key))
//							{
//
//
//									p.println(key+" "+value+" "+"SIM");
//
//
//
//							}
//						}
					}
					}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}




}
