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
package de.tudarmstadt.ukp.alignment.framework.gloss;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.CosineSimilarity;

public class GlossSimilarityCalculator
{

	public static HashMap<String,Double> combinedLexemeFreqInGlosses = new HashMap<String, Double>();
	public static HashMap<String,Double> combinedLemmaFreqInGlosses = new HashMap<String, Double>();

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{


			/* GLOBAL SETTINGS */

			Global.init();
			final String language = ELanguageIdentifier.ENGLISH;

			/*RESOURCE 1*/

			boolean synset1 = true;
			boolean usePos1 = true;
			final int prefix1 = Global.WN_Synset_prefix;

			OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);

//			final int chunksize1 = 2000;

//			bg_1.createGlossFile(false);
//			bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
			bg_1.fillIndexTables();


			/*RESOURCE 2*/
			boolean synset2 = false;
			boolean usePos2 = true;
			final int prefix2 = Global.WKT_EN_prefix;

//			final int chunksize2 = 1000;

			OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna",prefix2,language,synset2,usePos2);

//			bg_2.createGlossFile(false);
//			bg_2.lemmatizePOStagGlossFileInChunks(chunksize2);
			bg_2.fillIndexTables();

		

			boolean useTaggedGloss = true;
			boolean tfidf = true;

			createIdfFiles(bg_1, bg_2);

			calculateSimilarityForCandidates(bg_1, bg_2,useTaggedGloss, tfidf,"target/ijcnlp2011-meyer-dataset_graph.csv");
			boolean onlyGreaterZero = true;

            createAlignmentFromSimilarityFileUnsupervised(bg_1, bg_2,useTaggedGloss, tfidf, onlyGreaterZero);
//			boolean extRef = true;
	//		Global.mapAlignmentToUby(bg_1,bg_2,"target/"+bg_1.prefix_string+"_"+bg_2.prefix_string+"_alignment_similarity_"+(bg_2.pos ? "Pos": "noPos")+(tfidf ? "_tfidf": "")+(onlyGreaterZero ? "_nonZero"  :"")+".txt", extRef);

		}


	/**
	 * This method creates a combined idf value for the two resources to enable tfidf weighted cosine similarity calcujlation
	 */
	public static void createIdfFiles(OneResourceBuilder gb1, OneResourceBuilder gb2)
	{
		for(String s : gb1.lexemeFreqInGlosses.keySet())
		{
			double d = gb1.lexemeFreqInGlosses.get(s);
			combinedLexemeFreqInGlosses.put(s,d);
		}
		for(String s : gb1.lemmaFreqInGlosses.keySet())
		{
			double d = gb1.lemmaFreqInGlosses.get(s);
			combinedLemmaFreqInGlosses.put(s,d);
		}


		for(String s : gb2.lexemeFreqInGlosses.keySet())
		{
			double d = gb2.lexemeFreqInGlosses.get(s);
			if(!combinedLexemeFreqInGlosses.containsKey(s))
			{
				combinedLexemeFreqInGlosses.put(s, 0.0);
			}
			double freq = combinedLexemeFreqInGlosses.get(s);
			combinedLexemeFreqInGlosses.put(s, d+freq);

		}
		for(String s : gb2.lemmaFreqInGlosses.keySet())
		{
			double d = gb2.lemmaFreqInGlosses.get(s);
			if(!combinedLemmaFreqInGlosses.containsKey(s))
			{
				combinedLemmaFreqInGlosses.put(s, 0.0);
			}
			double freq = combinedLemmaFreqInGlosses.get(s);
			combinedLemmaFreqInGlosses.put(s, d+freq);
		}

		try
		{
		double overallGlossSize  = gb1.gloss_count+gb2.gloss_count;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream( "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_combined_lexeme_idf.txt");
		p = new PrintStream( outstream );
		for(String lexeme : combinedLexemeFreqInGlosses.keySet())
		{
			double freq = combinedLexemeFreqInGlosses.get(lexeme);
			double idf =  Math.log(overallGlossSize/freq);
			p.println(lexeme+"\t"+idf);
		}
		p.close();
		outstream.close();
		outstream = new FileOutputStream( "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_combined_lemma_idf.txt");
		p = new PrintStream( outstream );
		for(String lemma : combinedLemmaFreqInGlosses.keySet())
		{
			double freq = combinedLemmaFreqInGlosses.get(lemma);
			double idf =  Math.log(overallGlossSize/freq);
			p.println(lemma+"\t"+idf);
		}
		p.close();
		outstream.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}


	/**
	 * This method calculates the cosine similarities between the glosses of two resources. Candidate files need to be created beforehand
	 * 
	 * @param pos Consider pos-tagged lexemes or only lemmas
	 * @param tfidf Use tfidf weighting
	 */
	public static void calculateSimilarityForCandidates(OneResourceBuilder gb1, OneResourceBuilder gb2, boolean pos, boolean tfidf, String candidatesFile)
	{
		try
		{
		 FileReader in = new FileReader(candidatesFile); //+"_short"
		 BufferedReader input_reader =  new BufferedReader(in);
		 String line;	
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream( "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_glossSimilarities"+(pos? "_tagged": "_plain")+(tfidf? "_tfidf": "")+".txt");
		p = new PrintStream( outstream );
		TextSimilarityMeasure measure = null;

		/*TODO 
		 * 
		 * Other similarity measures can be integrated here
		 * 
		 * 
		 * 
		 * */
		if(!tfidf)
		{
			measure  = new CosineSimilarity();
		}
		else
		{
			String idfScoresFile= "";
			if(pos)
			{
				idfScoresFile =  "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_combined_lexeme_idf.txt";
			}
			else
			{
				idfScoresFile =  "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_combined_lemma_idf.txt";
			}
			measure = new CosineSimilarity(CosineSimilarity.WeightingModeTf.FREQUENCY_LOGPLUSONE,CosineSimilarity.WeightingModeIdf.LOGPLUSONE,CosineSimilarity.NormalizationMode.L2,idfScoresFile);
		}

		while((line =input_reader.readLine())!=null)
		{
			 if(line.startsWith("p"))
			 {
				 p.println("f "+gb1.prefix_string+"_"+gb2.prefix_string+"_candidates_"+(gb2.pos ? "Pos": "noPos")+" "+"Cosine similarity");
				 continue;
			 } else if(line.startsWith("q"))
			 {

				 String id1 = line.split(" ")[1]; 
				 String id2 = line.split(" ")[2]; 
	
				 String gloss1 = "";
				 String gloss2 = "";
				 if(pos)
				 {
					 gloss1 = gb1.senseIdGlossPos.get(id1);
					 gloss2 = gb2.senseIdGlossPos.get(id2);
				 }
				 else
				 {
					 gloss1 = gb1.senseIdGloss.get(id1);
					 gloss2 = gb2.senseIdGloss.get(id2);
				 }
	
	
				 if(gloss1 == null || gloss2 ==null) {
					p.println(id1+"\t"+id2+"\t"+0.0);
				 }
				 else
				 {
					 String [] gloss_arr1 = gloss1.split(" ");
					 List<String> gloss_set1 =Arrays.asList(gloss_arr1);
					 String [] gloss_arr2 = gloss2.split(" ");
					 List<String> gloss_set2 =Arrays.asList(gloss_arr2);
					 double similarity = measure.getSimilarity(gloss_set1, gloss_set2);
					 p.println(id1+"\t"+id2+"\t"+similarity);
				 }
			 }
		 }
		input_reader.close();
		p.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}

}


	/**
	 * This method creates an alignment from the similarity files in an unsupervised way by picking the candidate with the greatest value
	 * 
	 * @param pos Consider pos-tagged lexemes or only lemmas
	 * @param tfidf Use tfidf weighting
	 * @param onlyGreaterZero only consider candidates with a non-zero similarity value
	 */
	public static void createAlignmentFromSimilarityFileUnsupervised(OneResourceBuilder gb1, OneResourceBuilder gb2, boolean pos, boolean tfidf,boolean onlyGreaterZero)
	{
		try
		{
				HashMap<String,HashSet<String> >  alignment_candidates = new HashMap<String,HashSet<String> > ();
				FileReader in = new FileReader("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_glossSimilarities"+(pos? "_tagged": "_plain")+(tfidf? "_tfidf": "")+".txt");
				BufferedReader input =  new BufferedReader(in);
				String current_id1 ="";
				String current_id2 ="";
				Double similarity =0.0;
				String line = "";
				
				FileOutputStream outstream_alignment = new FileOutputStream("target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_alignment_similarity_"+(gb2.pos ? "Pos": "noPos")+""+(tfidf? "_tfidf"  :"")+(onlyGreaterZero ? "_nonZero":"")+".txt");
				PrintStream p_align = new PrintStream( outstream_alignment );

				int i =0;
				while((line = input.readLine())!=null )
				{

						if(line.startsWith("f"))
						{
							 p_align.println(line+" alignment");
							 continue;
						}

						System.out.println("Source Nodes parsed "+i++);
						current_id1 = line.split("\t")[0];
						current_id2 = line.split("\t")[1];
					    similarity = Double.parseDouble(line.split("\t")[2]);

						if(alignment_candidates.get(current_id1)==null)
						{
							alignment_candidates.put(current_id1, new HashSet<String>());
						}
						HashSet<String> temp  = alignment_candidates.get(current_id1);
						temp.add(current_id2+"#"+similarity);

				}
				in.close();


				/*HERE THE ACTUAL ANALYISIS BEGINS*/

					for(String s : alignment_candidates.keySet())
					{

								HashSet<String> temp  = alignment_candidates.get(s);
								HashSet<String> targets = new HashSet<String>();
								if(temp.size()==1)
								{
									String[] targsim=  temp.iterator().next().split("#");
									String targ = targsim[0];
									double sim  = Double.parseDouble(targsim[1]);
									if(sim >0 || !onlyGreaterZero)
									{
										targets.add(targ);
										p_align.println(s+"\t"+targ+"\t"+sim);
									}
								}
								else
								{
									double max = 0.0;
									String targetid = "";
									for(String t : temp)
									{
										String[] targsim=t.split("#");
										String id  =targsim[0];
										double sim  =Double.parseDouble(targsim[1]);
										if(sim >= max)
										{
											max = sim;
											targetid = id;
										}
									}
									if(max >0 || !onlyGreaterZero)
									{
										targets.add(targetid);
										p_align.println(s+"\t"+targetid+"\t"+max);
									}

								}

							
								
							}
				p_align.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}




















}






