package de.tudarmstadt.ukp.alignment.framework.candidates;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.graph.OneGraphBuilder;

public class CandidateExtractor
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
	{

			/* GLOBAL SETTINGS */

			Global.init();

			/*RESOURCE 1*/

			boolean synset1 = true;
			boolean usePos1 = true;
			//OneGraphBuilder bg_1 = new OneGraphBuilder("uby_lite_0_4_0","root","fortuna");
			OneGraphBuilder bg_1 = new OneGraphBuilder("uby_release_1_0","root","fortuna");
			final int prefix1 = Global.WN_Synset_prefix;
			final String prefix_string1 = Global.prefixTable.get(prefix1);

			bg_1.fillIndexTables(prefix_string1+"_"+(synset1?"synset":"sense")+"_lexeme_frequencies.txt",
					prefix1,
					synset1, usePos1);

			/*RESOURCE 2*/
			boolean synset2 = true;
			boolean usePos2 = true;

			OneGraphBuilder bg_2 = new OneGraphBuilder("uby_release_1_0","root","fortuna");

			final int prefix2 = Global.OW_EN_Synset_prefix;
			final String prefix_string2 = Global.prefixTable.get(prefix2);

			bg_2.fillIndexTables(prefix_string2+"_"+(synset2?"synset":"sense")+"_lexeme_frequencies.txt",
					prefix2,
					synset2, usePos2);




			/*Calculate alignment candidates between the two LSRs*/
			/*Index tables must be filled at this point*/

		createCandidateFileFull(bg_1, bg_2, "target/"+prefix_string1+"_"+prefix_string2+"_candidates_"+(usePos2 ? "Pos": "noPos"), usePos2);



	}
	public static void createCandidateFileFull(OneGraphBuilder gb1, OneGraphBuilder gb2,String output, boolean usePosForTwo) throws ClassNotFoundException, SQLException, IOException
	{
		StringBuilder sb = new StringBuilder();
		int count = 0;
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream(output);
		p = new PrintStream( outstream );
		for(String lemmaPos: gb1.lemmaPosSenses.keySet())
		{
			if(usePosForTwo) {
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
	}

	public static void createCandidateFileLemmaList(OneGraphBuilder gb1, OneGraphBuilder gb2,String input, String output) throws ClassNotFoundException, SQLException, IOException
	{
		/*TODO*/
	}

	public static void createCandidateFileGoldStandard(OneGraphBuilder gb1, OneGraphBuilder gb2,String input, String output) throws ClassNotFoundException, SQLException, IOException
	{
		/*TODO*/
	}



}
