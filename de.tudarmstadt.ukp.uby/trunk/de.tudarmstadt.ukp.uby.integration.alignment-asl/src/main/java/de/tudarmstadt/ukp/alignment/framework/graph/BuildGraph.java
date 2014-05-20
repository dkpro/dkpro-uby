package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;

import de.tudarmstadt.ukp.alignment.framework.Prefixes;
import de.tudarmstadt.ukp.alignment.framework.uima.Toolkit;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
public class BuildGraph
{
	public static HashMap<Integer,String> prefixTable;
	public static final String LF = System.getProperty("line.separator");
	public static HashMap<String,HashSet<String>> lemmaPosSensesLSR1;
	public static HashMap<String,String> lemmaIdWrittenFormLSR1;
	public static HashMap<String,Integer> lexemeFreqInGlosses;
	public static HashMap<Integer,String> senseIdWGlossFormLSR1;
	public static Connection connection;

	public BuildGraph(String dbname, String user, String pass)
	{
		prefixTable = new HashMap<Integer, String>();
		prefixTable.put(Prefixes.GN_prefix, "GN");
		prefixTable.put(Prefixes.WN_prefix, "WN");
		prefixTable.put(Prefixes.WKT_EN_prefix, "WktEn");
		prefixTable.put(Prefixes.WKT_DE_prefix, "WktDe");
		prefixTable.put(Prefixes.WP_EN_prefix, "WikiEn");
		prefixTable.put(Prefixes.WP_DE_prefix, "WikiDe");
		prefixTable.put(Prefixes.OW_EN_prefix, "OW_en");
		prefixTable.put(Prefixes.OW_DE_prefix, "OW_de");
		prefixTable.put(Prefixes.FN_prefix, "FN");
		prefixTable.put(Prefixes.VN_prefix, "VN");
		prefixTable.put(Prefixes.IMS_prefix, "IMS");
		lemmaIdWrittenFormLSR1 = new HashMap<String, String>();
		lemmaPosSensesLSR1 = new HashMap<String, HashSet<String>>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbname,user,pass);
		}
		catch (SQLException e) {

			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
		{
			boolean synset = true;
			boolean usePos = true;
			BuildGraph bg = new BuildGraph("uby_release_1_0","root","fortuna");
	//		bg.builtInitialGraphFromDb("ow_de_synset_new_framework",Prefixes.OW_DE_prefix,synset);
	//		bg.createGlossFile(Prefixes.OW_DE_prefix,synset);
		//	bg.LemmatizePOStagGlossFile(Prefixes.OW_DE_prefix, ELanguageIdentifier.GERMAN);
			bg.fillIndexTablesForOneResource(Prefixes.OW_DE_prefix, lemmaPosSensesLSR1, lemmaIdWrittenFormLSR1, synset, usePos);
			bg.createMonosemousLinks(Prefixes.OW_DE_prefix, 50, usePos);


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


	public void builtInitialGraphFromDb(String output, int prefix, boolean synset) throws ClassNotFoundException, SQLException, IOException
	{
		FileOutputStream outstream;
		String prefix_string = prefixTable.get(prefix);


		PrintStream p;
		outstream = new FileOutputStream("target/"+output+".txt");
		p = new PrintStream( outstream );
		StringBuilder sb = new StringBuilder();
		Statement statement = connection.createStatement();

		ResultSet rs;
		if(synset) {
			rs =	statement.executeQuery("SELECT synsetId,target FROM SynsetRelation where synsetId like '"+prefix_string+"%'");
		}
		else {
			rs =	statement.executeQuery("SELECT senseId,target FROM SenseRelation where senseId like '"+prefix_string+"%'");
		}
		int max_id = 0;
		int count = 0;

		while(rs.next())
		{
			String id1 = rs.getString(1);
			String id2 = rs.getString(2);
			if(synset)
			{
				id1 = prefix+id1.split("ynset_")[1];
				id2 = prefix+id2.split("ynset_")[1];
			}
			else
			{
				id1 = prefix+id1.split("ense_")[1];
				id2 = prefix+id2.split("ense_")[1];
			}
			int id1_num = Integer.parseInt(id1);
			int id2_num = Integer.parseInt(id2);
			if(id1_num > max_id) {
				max_id = id1_num;
			}
			if(id2_num > max_id) {
				max_id = id2_num;
			}
			sb.append("a "+id1_num+" "+id2_num+" 1\n");
			sb.append("a "+id2_num+" "+id1_num+" 1\n");
			count+=2;
		}

		String header = "p sp "+max_id+" "+count;
		p.println(header);
		p.print(sb.toString());
		p.flush();
		p.close();
		rs.close();
		statement.close();

	}


	public void createGlossFile(int prefix, boolean synset) throws ClassNotFoundException, SQLException, IOException
	{

		String prefix_string = prefixTable.get(prefix);
			FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+prefix_string+"_glosses.txt");
		p = new PrintStream( outstream );
		Statement statement = connection.createStatement();
		ResultSet rs;
		final Pattern CLEANUP = Pattern.compile("[^A-Za-z0-9äöüÄÖÜß]+");
		if(synset)
		{
			rs = statement.executeQuery("SELECT synsetId, writtenText FROM Definition join TextRepresentation_Definition where synsetId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId");
		}
		else
		{
			rs =	statement.executeQuery("SELECT senseId, writtenText FROM Definition join TextRepresentation_Definition where senseId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId");
		}
		while(rs.next())
		{
			if(rs.getString(2)!=null) {
				String id = rs.getString(1);

				if(synset)
				{
					id = prefix+id.split("ynset_")[1];

				}
				else
				{
					id = prefix+id.split("ense_")[1];
				}
				String gloss =  CLEANUP.matcher(rs.getString(2)).replaceAll(" ");
				p.println(id+"\t"+gloss.replace("\n", "").replace("\r", "").replace("\t", " ").trim());
			}
		}
		rs.close();
		statement.close();

	}


	public void LemmatizePOStagGlossFile(int prefix, String lang)
	{
		if(lexemeFreqInGlosses == null) {
			lexemeFreqInGlosses = new HashMap<String, Integer>();
		}
		String prefix_string = prefixTable.get(prefix);
		CharMatcher d = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.WHITESPACE).or(CharMatcher.anyOf("-_äöüÄÜÖ"));
		int i = 0;
		FileOutputStream outstream;
		FileOutputStream outstream_freq;
		PrintStream p;
		PrintStream p_freq;
		try
		{
		outstream = new FileOutputStream("target/"+prefix_string+"_glosses_POS_tagged.txt");
		outstream_freq = new FileOutputStream("target/"+prefix_string+"_lexeme_freq.txt");
		// Connect print stream to the output stream
		p = new PrintStream( outstream );
		p_freq = new PrintStream(outstream_freq);
		 FileReader in = new FileReader("target/"+prefix_string+"_glosses.txt");
		 BufferedReader input =  new BufferedReader(in);
		 String line;
		 StringBuilder sb = new StringBuilder();
		 while((line =input.readLine())!=null)
		 {

			sb.append(line.replace("\t","TABULATOR ")+" ENDOFLINE ");
			// sb.append(line+" ENDOFLINE ");
				System.out.println("lines appended "+i++);
		 }

		 String[] result = null;
		 if(lang.equals(ELanguageIdentifier.ENGLISH))
		 {
			 Toolkit.initializePOS();
			 result = Toolkit.lemmatizeEnglish(sb.toString());
		 }
		 else if(lang.equals(ELanguageIdentifier.GERMAN))
		 {
			 Toolkit.initializePOSGerman();
			 result = Toolkit.lemmatizeGerman(sb.toString());
		 }
		// p.println(result);
		 String resultline="";
		 for(String s : result) {
			resultline+=s+" ";
			if(!s.contains("TABULATOR") && !s.contains("ENDOFLINE"))
			{
				if(!lexemeFreqInGlosses.containsKey(s))
				{
					lexemeFreqInGlosses.put(s, 0);
				}
				int freq = lexemeFreqInGlosses.get(s);
				lexemeFreqInGlosses.put(s, freq+1);
			}
			//System.out.println(s);
		}
		resultline = resultline.replaceAll("tabulator#\\S*\\s", "\t");
		resultline = resultline.replaceAll("endofline#\\S*\\s", LF);
		resultline = resultline.replaceAll("TABULATOR#\\S*\\s", "\t");
		resultline = resultline.replaceAll("ENDOFLINE#\\S*\\s", LF);
		p.print(resultline);
		 p.flush();
		 p.close();
		 for(String lexeme : lexemeFreqInGlosses.keySet())
		 {
			 p_freq.println(lexeme+"\t"+lexemeFreqInGlosses.get(lexeme));
		 }
		 p_freq.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}

	}


	public void fillIndexTablesForOneResource(int prefix, HashMap<String,HashSet<String>> lemmaPosSenses,HashMap<String,String> lemmaIdWrittenForm, boolean synset, boolean usePos) throws ClassNotFoundException, SQLException, IOException
		{
			String prefix_string  = prefixTable.get(prefix);
			if(lexemeFreqInGlosses == null) {
				lexemeFreqInGlosses = new HashMap<String, Integer>();
				 FileReader in = new FileReader("target/"+prefix_string+"_lexeme_freq.txt");
				 BufferedReader input =  new BufferedReader(in);
				 String line;
				 while((line =input.readLine())!=null)
				 {

					 String lexeme = line.split("\t")[0];
					 int frequency = Integer.parseInt(line.split("\t")[1]);
	//				 System.out.println(lexeme+" "+frequency);
					 lexemeFreqInGlosses.put(lexeme, frequency);
				 }

			}
			Statement statement = connection.createStatement();
			ResultSet rs =	statement.executeQuery("select distinct LexicalEntry.lemmaId,writtenForm from FormRepresentation_Lemma join LexicalEntry where LexicalEntry.lexicalEntryId like '"+prefix_string+"%' and LexicalEntry.lemmaId = FormRepresentation_Lemma.lemmaId");
			while(rs.next())
			{
				String lemmaId = rs.getString(1);
				String writtenForm = rs.getString(2);
				lemmaIdWrittenForm.put(lemmaId,writtenForm);
			}
			rs =	statement.executeQuery("select distinct lemmaId,partOfSpeech, "+ (synset ? "synsetId" : "senseId") +" from LexicalEntry join Sense where LexicalEntry.lexicalEntryId like '"+prefix_string+"%' and LexicalEntry.lexicalEntryId = Sense.lexicalEntryId");
			int c = 0;
			while(rs.next())
			{
				String lemmaId = rs.getString(1);
				String lemma;
				lemma = lemmaIdWrittenForm.get(lemmaId);
				String POS = rs.getString(2);
				String senseId = rs.getString(3);
				if(synset)
				{
					senseId = prefix+senseId.split("ynset_")[1];

				}
				else
				{
					senseId = prefix+senseId.split("ense_")[1];
				}
				//System.out.println(lemma+" "+senseId);
	//			System.out.println(senseId);

				if(lemma == null) {
					continue;
				}
				String key = "";
				if(usePos) {

					if(POS == null ) {
						key =lemma.toLowerCase()+"#"+"null";
					}
					else {
						key =lemma.toLowerCase()+"#"+POS.replace("Common", "");
					}
				}
				else
				{
					key =lemma.toLowerCase();
				}
				if(!lemmaPosSenses.containsKey(key))
				{
					lemmaPosSenses.put(key, new HashSet<String>());
				}
				lemmaPosSenses.get(key).add(senseId);


			}
	//		for(String key : lemmaPosSenses.keySet())
	//		{
	//			System.out.println(key+" "+lemmaPosSenses.get(key).size());
	//		}
			rs.close();
			statement.close();
		}


	public void createMonosemousLinks(int prefix, int phi, boolean usePos) throws ClassNotFoundException, SQLException, IOException
		{
			String prefix_string = prefixTable.get(prefix);

			StringBuilder sb = new StringBuilder();
			int count = 0;
			int max_id = 0;
			FileOutputStream outstream;
			PrintStream p;
			 FileReader in = new FileReader("target/"+prefix_string+"_glosses_POS_tagged.txt");
			 BufferedReader input =  new BufferedReader(in);
				outstream = new FileOutputStream("target/"+prefix_string+"_monoLinks_"+phi+"_"+(usePos ? "Pos": "noPos")+".txt");
				p = new PrintStream( outstream );
			 String line;
			 while((line =input.readLine())!=null)
			 {
				 String id1 = line.split("\t")[0];
				 if((line.split("\t")).length<2) {
					continue;
				}
				 String[] lexemes = line.split("\t")[1].split(" ");
				 for(String lexeme:lexemes)
				 {
	//				 System.out.println(lexeme);
	//				 System.out.println(lexemeFreqInGlosses.size());
					 if(lexemeFreqInGlosses.get(lexeme) == null || lexemeFreqInGlosses.get(lexeme)>phi)
					 {
						 continue;
					 }
					 if(usePos)
					 {
	//					 System.out.println(lexeme);
						 if(lemmaPosSensesLSR1.get(lexeme)!= null && lemmaPosSensesLSR1.get(lexeme).size()==1)
						 {
	//						 System.out.println("Mono found!");
							 String id2= lemmaPosSensesLSR1.get(lexeme).iterator().next();
	//						 p.println("a "+id1+" "+id2+" 1");
	//						 p.println("a "+id2+" "+id1+" 1");
							 count+=2;
							int id1_num = Integer.parseInt(id1);
							int id2_num = Integer.parseInt(id2);
							if(id1_num > max_id) {
								max_id = id1_num;
							}
							if(id2_num > max_id) {
								max_id = id2_num;
							}
							sb.append("a "+id1_num+" "+id2_num+" 1\n");
							sb.append("a "+id2_num+" "+id1_num+" 1\n");
						 }
					 }
					 else
					 {
						 String lemma = lexeme.split("#")[0];
						 if(lemmaPosSensesLSR1.get(lemma)!= null && lemmaPosSensesLSR1.get(lemma).size()==1)
						 {
							 String id2= lemmaPosSensesLSR1.get(lemma).iterator().next();
							 //p.println("a "+id1+" "+id2+" 1");
							 //p.println("a "+id2+" "+id1+" 1");
							 count+=2;
							int id1_num = Integer.parseInt(id1);
							int id2_num = Integer.parseInt(id2);
							if(id1_num > max_id) {
							max_id = id1_num;
							}
							if(id2_num > max_id) {
								max_id = id2_num;
							}
							sb.append("a "+id1_num+" "+id2_num+" 1\n");
							sb.append("a "+id2_num+" "+id1_num+" 1\n");
						 }
					 }
				 }
			 }
				String header = "p sp "+max_id+" "+count;
				p.println(header);
				p.print(sb.toString());
				p.flush();
				p.close();
		}


	public static void mergeTwoEdgeLists(String infile1,String infile2, String outfile, String edgeCount) throws ClassNotFoundException,  IOException
	{
		StringBuilder sb = new StringBuilder();
		/*Take care of having it undirected*/
		FileReader in = new FileReader(infile1);
		BufferedReader input =  new BufferedReader(in);
			FileOutputStream outstream;
			PrintStream p;
			outstream = new FileOutputStream(outfile);
			p = new PrintStream( outstream );

		int maxId = 0;
		int size = 0;
		 String line;

		int i = 0;

	//	p.println("p sp "+Offsets.Overall_maxid+" "+edgeCount);
		 while((line =input.readLine())!=null)
		 {
			 if(line.startsWith("p"))
			 {
				 String[] info = line.split(" ");
				 maxId = Integer.parseInt(info[2]);
				 size = Integer.parseInt(info[3]);


			}
			 else
			 {

			 }
			 if(i++ % 1000 ==0) {
			System.out.println("Lines processed "+i);
			}


		 }
		 //p.println("p sp "+nodes_count+" "+arcs_count);
			in = new FileReader(infile2);
			input =  new BufferedReader(in);
			 while((line =input.readLine())!=null)
			 {
				 if(line.contains("p"))
				 {
					 String[] info = line.split(" ");
					 int max = Integer.parseInt(info[2]);
					 if(max>maxId) {
						maxId = max;
					}
					 size = size+ Integer.parseInt(info[3]);
				}
				 else
				 {
					 sb.append(line+LF);
				 }
				if(i++ % 1000 ==0) {
				System.out.println("Lines processed "+i);
				}


			 }
			 p.println("p sp "+maxId+" "+size);
			 p.print(sb.toString());
		 //System.out.println("p sp "+nodes_count+" "+arcs_count);
	}


	public static double overlap(List<String> o1, List<String> o2)
	{
		Set<Object> set1 = new HashSet<Object>(o1);
		Set<Object> set2 = new HashSet<Object>(o2);

		set2.retainAll(set1);
		return set2.size();
	}

}
