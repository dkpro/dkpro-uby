package de.tudarmstadt.ukp.alignment.framework.evaluation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

public class Evaluator
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{


		Global.init();
		final String language = ELanguageIdentifier.ENGLISH;

		boolean synset1 = true;
		boolean usePos1 = true;
		final int prefix1 = Global.WN_Synset_prefix;

		OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);




		/*RESOURCE 2*/
		boolean synset2 = true;
		boolean usePos2 = true;
		final int prefix2 = Global.OW_EN_Synset_prefix;

		OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna",prefix2,language,synset2,usePos2);

		//Global.processExtRefGoldstandardFile(bg_1,bg_2, "target/WN_OW_alignment_gold_standard.txt", true);
		performEvaluation("target/WN_OW_en_alignment_similarity_Pos_tfidf_nonZero.txt", "target/WN_OW_alignment_gold_standard_graph.txt", false);

	}
	public static void performEvaluation(String alignment, String goldstandard, boolean pos)
	{

		try {
			HashSet<String> current =new HashSet<String>();
			HashSet<String> gold_pos =new HashSet<String>();
			HashSet<String> gold_neg =new HashSet<String>();

			System.out.println("   * Configuration: ");

			FileReader in = new FileReader(alignment);
			FileReader in2 = new FileReader(goldstandard);


			 BufferedReader input =  new BufferedReader(in);
			 String line;
			 while((line =input.readLine())!=null)
			 {
				 if(line.startsWith("f")) {
					continue;
				}

				 String[] info = line.split("\t");
				 current.add(info[0]+"\t"+info[1]);
			  }

			 input =  new BufferedReader(in2);
			 while((line =input.readLine())!=null) {
				 String[] info = line.split("\t");
				 if(info[2].equals("1")) {
					gold_pos.add(info[0]+"\t"+info[1]);
				}
				else {
					gold_neg.add(info[0]+"\t"+info[1]);
				}

			 }

			 /*TODO* for later use*/
			 String[] poses = {"noun","adjective","adverb","verb"};
			 if(!pos)
			 {
				 poses = new String[1];
				 poses[0]="";
			 }

			 for(String pos_string : poses)
			 {
			 double tp_1 = 0.0;
			 double fn_1 = 0.0;
			 double fp_1 = 0.0;
			 double tp_0 = 0.0;
			 double fn_0 = 0.0;
			 double fp_0 = 0.0;
			 double tn_1 = 0.0;
			 double tn_0 = 0.0;
			 for(String gp : gold_pos)
			 {

				 if(gp.contains(pos_string)) {
					if(current.contains(gp))
					 {
						tp_1++;
					}
					else
						{
						fn_1++;
						fp_0++;
					}
				}
			 }

			 for(String gn : gold_neg)
			 {
				 if(gn.contains(pos_string)) {
					if(current.contains(gn) )
					 {
						fp_1++;
						fn_0++;
					}
					else {
						tp_0++;
						tn_1++;
					}
				}
			 }

			 double precision_1 = tp_1 / (tp_1+fp_1);
			 double recall_1 = tp_1 / (tp_1+fn_1);
			 double precision_0 = tp_0 / (tp_0+fp_0);
			 double recall_0 = tp_0/ (tp_0+fn_0);
			 double accuracy = (tp_1+tp_0) / (tp_1+tp_0+fn_0+fn_1);
			 double f_1_1 = (precision_1 * recall_1 *2) / (precision_1 + recall_1);
			 double f_1_0 = (precision_0 * recall_0 *2) / (precision_0 + recall_0);
			 double overall_size=  gold_neg.size()+gold_pos.size();
			 double weight_1 = gold_pos.size() / overall_size;
			 double weight_0 = gold_neg.size() / overall_size;
//			 System.out.println("Class 1 Size: "+gold_pos.size()+" Weight: "+weight_1);
//			 System.out.println("Class 0 Size: "+gold_neg.size()+" Weight: "+weight_0);
//			 System.out.println("Overall Size: "+overall_size);
			 System.out.println("TP_1: "+tp_1);
			 System.out.println("FP_1: "+fp_1);
			 System.out.println("FN_1: "+fn_1);
			 System.out.println("TN_1: "+tp_0);
			 System.out.println("FP_0: "+fp_0);
			 System.out.println("FN_0: "+fn_0);
			 System.out.println("TP_0: "+tp_0);
			 System.out.println("TN_0: "+tn_0);
			 System.out.print("   * Results "+pos_string+ " *Precision Class 1: "+precision_1);
			 System.out.print(" Recall Class 1: "+recall_1);
			 System.out.println(" F1-Measure: "+f_1_1+"*");
			 System.out.print("   * Results "+pos_string+ " *Precision Class 0: "+precision_0);
			 System.out.print(" Recall Class 0: "+recall_0);
			 System.out.println(" F1-Measure: "+f_1_0+"*");
			 System.out.print("   * Results "+pos_string+ " *Precision Avg. "+(weight_0 *precision_0+weight_1 *precision_1));
			 System.out.print(" Recall Avg.: "+(weight_0 *recall_0+weight_1 *recall_1));
			 System.out.println(" F1-Measure Avg: "+(weight_0 *f_1_0+weight_1 *f_1_1)+"*");
			 System.out.println("   * Accuracy: "+accuracy+"*");
			 System.out.println();

		}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			 System.out.println("File not found");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
