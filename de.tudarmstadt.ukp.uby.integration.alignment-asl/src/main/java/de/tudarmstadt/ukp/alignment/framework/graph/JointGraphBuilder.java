package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

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
			final String language = ELanguageIdentifier.ENGLISH;

			/*RESOURCE 1*/

			boolean synset = true;
			boolean usePos = true;
			//OneGraphBuilder bg_1 = new OneGraphBuilder("uby_lite_0_4_0","root","fortuna");
			OneGraphBuilder bg_1 = new OneGraphBuilder("uby_release_1_0","root","fortuna");
			final int prefix1 = Global.WN_prefix;
			final String prefix_string1 = Global.prefixTable.get(prefix1);
			final int monoLinkThreshold1 = 1000;



			bg_1.builtRelationGraphFromDb(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationgraph.txt",
					prefix1,synset);
			bg_1.createGlossFile(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses.txt",
					prefix1,synset);

		bg_1.lemmatizePOStagGlossFileInChunks(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses.txt",
					prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses_tagged.txt",
					prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_lexeme_frequencies.txt" ,
					prefix1, language,1000);

			bg_1.fillIndexTables(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_lexeme_frequencies.txt",
					prefix1,
					synset, usePos);

			bg_1.createMonosemousLinks(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses_tagged.txt",
					prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold1+".txt",
					prefix1, monoLinkThreshold1, usePos);
			Global.mergeTwoGraphs(prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationgraph.txt" ,
					prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold1+".txt",
					prefix_string1+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold1+".txt");




			/*RESOURCE 2*/
			synset = true;
			usePos = true;
			OneGraphBuilder bg_2 = new OneGraphBuilder("uby_release_1_0","root","fortuna");

			final int prefix2 = Global.OW_EN_prefix;
			final String prefix_string2 = Global.prefixTable.get(prefix2);
			final int monoLinkThreshold2 = 500;



			bg_2.builtRelationGraphFromDb(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationgraph.txt",
					prefix2,synset);

			bg_2.createGlossFile(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses.txt",
					prefix2,synset);

//			bg_2.lemmatizePOStagGlossFile(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses.txt",
//					prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses_tagged.txt",
//					prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_lexeme_frequencies.txt" ,
//					prefix2,language);

			bg_2.lemmatizePOStagGlossFileInChunks(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses.txt",
			prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses_tagged.txt",
			prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_lexeme_frequencies.txt" ,
			prefix2,language,1000);


			bg_2.fillIndexTables(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_lexeme_frequencies.txt",
					prefix2,
					synset, usePos);

			bg_2.createMonosemousLinks(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_glosses_tagged.txt",
					prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold2+".txt",

					prefix2, monoLinkThreshold2, usePos);

			Global.mergeTwoGraphs(prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationgraph.txt" ,
					prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_monosemousLinks"+"_"+monoLinkThreshold2+".txt",
					prefix_string2+"_"+(synset?"synset":"sense")+"_"+(usePos ? "Pos":"noPos")+"_relationMLgraph"+"_"+monoLinkThreshold2+".txt");


			//createLexicalFieldsGN();
			//lemmaIdWrittenFromLSR1= new HashMap<String, String>();
	//		lemmaIdWrittenFromLSR2= new HashMap<String, String>();
	//		lemmaPosSensesLSR1=  new HashMap<String, HashSet<Integer>>();
	//		lemmaPosSensesLSR2=  new HashMap<String, HashSet<Integer>>();
	//		senseIdWGlossFromLSR1 = new HashMap<Integer, String>();
	//		senseIdWGlossFromLSR2 = new HashMap<Integer, String>();
			//fillIndexTableForOneResource("uby_wiktionary_de","WiktionaryDE", Offsets.GN_offset+Offsets.WKT_DE_offset+Offsets.WP_DE_offset,1,true);
			//fillIndexTableForOneResource("uby_release_1_0","OmegaWikiDE", Offsets.GN_offset+Offsets.WKT_DE_offset+Offsets.WP_DE_offset+Offsets.OW_DE_offset,2,true);

			//fillIndexTableForOneResource("uby_wiktionary_de","WiktionaryDE",Offsets.WKT_DE_offset,1,true);
	//		fillIndexTableForOneResource("uby_release_1_0","WordNet",Offsets.WN_offset,1,true);
	//		fillIndexTableForOneResource("uby_release_1_0","WiktionaryEN",Offsets.WN_offset+Offsets.WP_offset+Offsets.WKT_offset,1,true);
	//		fillIndexTableForOneResource("uby_release_1_0","OmegaWikiEN",Offsets.WN_offset+Offsets.WP_offset+Offsets.OW_offset+Offsets.WKT_offset,2,false);


		//	fillIndexTableForOneResource("uby_lite_0_4_0","IMSLex",Offsets.WN_offset+Offsets.WP_offset,1,true);
			//fillIndexTableForOneResource("uby_release_1_0","FrameNet",Offsets.WN_offset+Offsets.WP_offset,1,true);
	//		fillGlossTableForOneResource("GermaNet", Offsets.GN_offset+Offsets.WKT_DE_offset+Offsets.WP_DE_offset,1);
	//		fillGlossTableForOneResource("WiktionaryDE", Offsets.WKT_DE_offset,2);
	//		CreateSimilarityFileLemmaList("target/WebCAGe-2.0_lemmas.tsv","gn_wkt_ids","gn_wkt_glosses","gn_wkt_overlap", true);



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

	public void createTrivialAlignments(OneGraphBuilder gb1, OneGraphBuilder gb2, String output, boolean usePosForSecond) throws ClassNotFoundException, SQLException, IOException
		{
		StringBuilder sb = new StringBuilder();
		int count = 0;
		int maxId = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+output);
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(gb1.lemmaPosSenses.get(lemmaPos).size()==1)
			{
				String id1= gb1.lemmaPosSenses.get(lemmaPos).iterator().next();
				int id1_int = Integer.parseInt(id1);
				if(usePosForSecond) {
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
//					if(lemmaPosSensesLSR2.get(lemma)!= null && lemmaPosSensesLSR2.get(lemma).size()==1)
//					{
//						String id2= lemmaPosSensesLSR2.get(lemma).iterator().next();
//						p.println("a "+id1+" "+id2+" 1");
//						p.println("a "+id2+" "+id1+" 1");
//						count+=2;
//					}
				}
			}
		}
		// p.println("p sp "+0+" "+count);
	}


}