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
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{

			/* GLOBAL SETTINGS */

			Global.init();
			String language = ELanguageIdentifier.GERMAN;
			/*RESOURCE 1*/

			boolean synset1 = true;
			boolean usePos1 = true;

			boolean synset2 = true;
			boolean usePos2 = true;


			 int prefix1 = Global.GN_Synset_prefix;
			 String prefix_string1 = Global.prefixTable.get(prefix1);
			 int prefix2 = Global.GN_Synset_prefix;
			 String prefix_string2 = Global.prefixTable.get(prefix2);
		     OneResourceBuilder bg_1 = new OneResourceBuilder("uby_lite_0_4_0","root","fortuna", prefix2,language,synset1,usePos1);


		//     OneResourceBuilder bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);
		     OneResourceBuilder bg_2 = new OneResourceBuilder("uby_germanet7","root","fortuna", prefix1,language,synset1,usePos1);
	//		 OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);


			final int monoLinkThreshold1 = 1000;
			final int chunksize1 = 2000;
//			bg_1.createGlossFile(false);
			//bg_2.createGlossFile(false);

			bg_1.analyizeLemmaList("");
//			bg_1.typeTokenRatio();
//			bg_2.typeTokenRatio();
//			calculateLexicalGlossOverlap(bg_1, bg_2);
			System.exit(1);
			synset2 = true;
			prefix2 = Global.WN_Synset_prefix;
			prefix_string2 = Global.prefixTable.get(prefix2);
			bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);
			bg_2.typeTokenRatio();
			calculateLexicalGlossOverlap(bg_1, bg_2);

			synset2 = false;
			prefix2 = Global.FN_prefix;
			prefix_string2 = Global.prefixTable.get(prefix2);
			bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix2,language,synset2,usePos2);
			bg_2.typeTokenRatio();
			calculateLexicalGlossOverlap(bg_1, bg_2);

			System.exit(1);
		//	bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
		//	bg_1.fillIndexTables();
		//	bg_1.builtRelationGraphFromDb(false);

		//

			prefix1 = Global.WP_DE_prefix;
			prefix_string1 = Global.prefixTable.get(prefix1);
	//		OneResourceBuilder bg_1 = new OneResourceBuilder("uby_lite_0_4_0","root","fortuna", prefix1,language,synset1,usePos1);
	//		OneResourceBuilder 	bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);
//			bg_1.createGlossFile(false);
		//	bg_1.typeTokenRatio();
//			bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
//			bg_1.fillIndexTables();
//			bg_1.builtRelationGraphFromDb(false);

//			 	System.exit(0);language = ELanguageIdentifier.ENGLISH;
//			prefix1 = Global.WKT_EN_prefix;
//			prefix_string1 = Global.prefixTable.get(prefix1);
//	//		OneResourceBuilder bg_1 = new OneResourceBuilder("uby_lite_0_4_0","root","fortuna", prefix1,language,synset1,usePos1);
//			bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);
//			bg_1.createGlossFile(false);
//		//	bg_1.typeTokenRatio();
//			bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
//			bg_1.fillIndexTables();
//			bg_1.builtRelationGraphFromDb(false);


			 language = ELanguageIdentifier.ENGLISH;
				prefix1 = Global.WP_EN_prefix;
				prefix_string1 = Global.prefixTable.get(prefix1);
		//		OneResourceBuilder bg_1 = new OneResourceBuilder("uby_lite_0_4_0","root","fortuna", prefix1,language,synset1,usePos1);
				bg_1 = new OneResourceBuilder("uby_release_1_0","root","fortuna", prefix1,language,synset1,usePos1);
				bg_1.createGlossFile(false);
			//	bg_1.typeTokenRatio();
				bg_1.lemmatizePOStagGlossFileInChunks(chunksize1);
				bg_1.fillIndexTables();

				bg_1.builtRelationGraphFromDb(false);
				System.exit(0);



			//bg_1.createMonosemousLinks(monoLinkThreshold1);




			Global.mergeTwoGraphs(prefix_string1+"_"+(synset1?"synset":"sense")+"_relationgraph.txt" ,
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold1+".txt",
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1+".txt");




			/*RESOURCE 2*/
//			boolean synset2 = true;
//			boolean usePos2 = true;
//			final int prefix2 = Global.OW_EN_Synset_prefix;
//			final String prefix_string2 = Global.prefixTable.get(prefix2);
			final int monoLinkThreshold2 = 500;
			final int chunksize2 = 1000;
//			OneResourceBuilder bg_2 = new OneResourceBuilder("uby_release_1_0","root","fortuna",prefix2,language,synset2,usePos2);

			bg_2.builtRelationGraphFromDb(false);

			bg_2.createGlossFile(false);

			bg_2.lemmatizePOStagGlossFileInChunks(chunksize2);

			bg_2.fillIndexTables();

			bg_2.createMonosemousLinks(monoLinkThreshold2);



//			bg_2.builtRelationGraphFromDb(prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph.txt",
//					prefix2,synset2);
//
//			bg_2.createGlossFile(prefix_string2+"_"+(synset2?"synset":"sense")+"_glosses.txt",
//					prefix2,synset2);
//
//			bg_2.lemmatizePOStagGlossFile(prefix_string2+"_"+(synset2?"synset":"sense")+"_glosses.txt",
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+"_glosses_tagged.txt",
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+"_lexeme_frequencies.txt" ,
//					prefix2,language);
//
//			bg_2.lemmatizePOStagGlossFileInChunks(prefix_string2+"_"+(synset2?"synset":"sense")+"_glosses.txt",
//			prefix_string2+"_"+(synset2?"synset":"sense")+"_glosses_tagged.txt",
//			prefix_string2+"_"+(synset2?"synset":"sense")+"_lexeme_frequencies.txt" ,
//			prefix2,language,5000);
//
//
//			bg_2.fillIndexTables(prefix_string2+"_"+(synset2?"synset":"sense")+"_lexeme_frequencies.txt",
//					prefix2,
//					synset2, usePos2);

//			bg_2.createMonosemousLinks(prefix_string2+"_"+(synset2?"synset":"sense")+"_glosses_tagged.txt",
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold2+".txt",
//					prefix2, monoLinkThreshold2, usePos2);
//
//			Global.mergeTwoGraphs(prefix_string2+"_"+(synset2?"synset":"sense")+"_relationgraph.txt" ,
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold2+".txt",
//					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt");


			/*Merge the two graphs*/

			Global.mergeTwoGraphs(
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1+".txt",
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt",
					prefix_string1+"_"+(synset1?"synset":"sense")+"_"+(usePos1 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1
					+"_MERGED_"+
					prefix_string2+"_"+(synset2?"synset":"sense")+"_"+(usePos2 ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt"
					);

			/*Create trivial alignments between the two LSRs*/
			/*Index tables must be filled at this point*/



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

		}


	/**
	 *
	 * Creates the trivial alignment between two resource graphs, i.e. those between lemmas with only one sense in either LSR
	 *
	 * @param gb1: First LSR
	 * @param gb2: Second LSR
	 * @param output: File which is written to
	 * @param usePosForSecond: It is valid to use POS in one graph, but not the other - this caters to the fact that OW
	 * has very few POS labels. In this case only the lemmas in the second LSR are considered for linking. Note that this implies that
	 * OW MUST be the second LSR to get correct results
	 *
	 *
	 *
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

	public static void createTrivialAlignments(OneResourceBuilder gb1, OneResourceBuilder gb2) throws ClassNotFoundException, SQLException, IOException
		{
		StringBuilder sb = new StringBuilder();
		int count = 0;
		int maxId = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream( "target/"+gb1.prefix_string+"_"+gb2.prefix_string+"_trivial_"+(gb2.pos ? "Pos": "noPos")+".txt");
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(gb1.lemmaPosSenses.get(lemmaPos).size()==1)
			{
				String id1= gb1.lemmaPosSenses.get(lemmaPos).iterator().next();
				int id1_int = Integer.parseInt(id1);
				if(gb2.pos) {
					if(gb2.lemmaPosSenses.get(lemmaPos)!= null && gb2.lemmaPosSenses.get(lemmaPos).size()==1)
					{
						String id2= gb2.lemmaPosSenses.get(lemmaPos).iterator().next();
						int id2_int = Integer.parseInt(id2);
						if(id1_int > maxId) {
							maxId = id1_int;
						}
						if(id2_int > maxId) {
							maxId = id2_int;
						}
						sb.append("a "+id1+" "+id2+" 1"+Global.LF); //edges are unweighted
						sb.append("a "+id2+" "+id1+" 1"+Global.LF);
						count+=2;
					}
				}
				else
				{
					String lemma = lemmaPos.split("#")[0];
					if((gb2.lemmaPosSenses.get(lemma)!= null && gb2.lemmaPosSenses.get(lemma).size()==1))
					{
						String id2= gb2.lemmaPosSenses.get(lemma).iterator().next();
						int id2_int = Integer.parseInt(id2);
						if(id1_int > maxId) {
							maxId = id1_int;
						}
						if(id2_int > maxId) {
							maxId = id2_int;
						}
						sb.append("a "+id1+" "+id2+" 1"+Global.LF); //edges are unweighted
						sb.append("a "+id2+" "+id1+" 1"+Global.LF);
						count+=2;
						count+=2;
					}
				}
			}
		}
		 p.println("p sp "+maxId+" "+count);
		 p.print(sb.toString());
	}


}
