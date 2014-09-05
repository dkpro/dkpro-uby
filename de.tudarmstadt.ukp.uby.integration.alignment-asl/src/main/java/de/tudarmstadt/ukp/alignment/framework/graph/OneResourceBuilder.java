package de.tudarmstadt.ukp.alignment.framework.graph;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.alignment.framework.Global;
import de.tudarmstadt.ukp.alignment.framework.uima.Toolkit;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
public class OneResourceBuilder
{


	public TreeMap<String,HashSet<String>> lemmaPosSenses;
	public TreeMap<String,HashSet<String>> senseIdLemma;
	public TreeMap<String,String> lemmaIdWrittenForm;
	public TreeMap<String,Integer> lexemeFreqInGlosses;
	public TreeMap<String,Integer> lemmaFreqInGlosses;
	public TreeMap<String, String> senseIdGloss;
	public TreeMap<String,String> senseIdGlossPos;
	public Connection connection;
	public int prefix;
	public String prefix_string;
	public boolean synset;
	public boolean pos;
	public String language;
	public int gloss_count;
	public OneResourceBuilder(String dbname, String user, String pass, int prefix, String language, boolean synset, boolean pos)
	{

		senseIdLemma = new TreeMap<String, HashSet<String>>();
		lemmaIdWrittenForm = new TreeMap<String, String>();
		lemmaPosSenses = new TreeMap<String, HashSet<String>>();
		lexemeFreqInGlosses = new TreeMap<String, Integer>();
		lemmaFreqInGlosses = new TreeMap<String, Integer>();
		HashMap<Integer,String> senseIdGloss = new HashMap<Integer, String>();
		HashMap<Integer,String> senseIdGlossPos = new HashMap<Integer, String>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbname,user,pass);

			this.prefix =prefix;
			this.prefix_string = Global.prefixTable.get(prefix);
			this.synset = synset;
			this.pos = pos;
			this.language = language;
			int gloss_count = 0;
		}
		catch (SQLException e) {

			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}


	/**
	 * param filterByGloss only considers relation targets that are contained within the gloss, or the first paragraph. This option mostly contains Wikipedia
	 * as described in the paper.

	 */
	public void builtRelationGraphFromDb(boolean filterByGloss) throws ClassNotFoundException, SQLException, IOException
	{
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_relationgraph"+(filterByGloss?"_filtered":"")+".txt");
		p = new PrintStream( outstream );
		StringBuilder sb = new StringBuilder();
		Statement statement = connection.createStatement();

		ResultSet rs;
		if(synset) {
			rs =	statement.executeQuery("SELECT synsetId,target FROM SynsetRelation where synsetId like '"+prefix_string+"%'");
		}
		else {
			if(prefix == Global.FN_prefix)
			{
				rs =	statement.executeQuery("SELECT distinct pr1.senseId, pr2.senseId FROM PredicativeRepresentation pr1  join PredicativeRepresentation pr2 where pr1.predicate = pr2.predicate and  pr1.senseId like 'FN%' and pr2.senseId like 'FN%' and pr1.senseId != pr2.senseId");
			}
			else
			{
				rs =	statement.executeQuery("SELECT senseId,target FROM SenseRelation where senseId like '"+prefix_string+"%'");
			}
		}
		int max_id = 0;
		int count = 0;

		while(rs.next())
		{

			String id1 = rs.getString(1);
			String id2 = rs.getString(2);
	//		System.out.println(id1+" "+id2);
			System.out.println(count+=1);
			if(id2 == null) {
				continue;
			}
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
			//HashSet<String> lemmas1 = senseIdLemma.get(id1);
			HashSet<String> lemmas2 = senseIdLemma.get(id2);
			String gloss1 = senseIdGlossPos.get(id1);
			if(gloss1 == null) {
				gloss1 = "";
			}
			String[] gloss_array1 = gloss1.split(" ");

			//String[] gloss2 = senseIdGlossPos.get(id2).split(" ");
			for(String s : gloss_array1)
			{
				if(lemmas2.contains(s) || !filterByGloss )
				{

					int id1_num = Integer.parseInt(id1);
					int id2_num = Integer.parseInt(id2);
					if(id1_num > max_id) {
						max_id = id1_num;
					}
					if(id2_num > max_id) {
						max_id = id2_num;
					}

					sb.append("a "+id1_num+" "+id2_num+" 1\n");
					count+=1;
					if(prefix != Global.FN_prefix)
					{
						sb.append("a "+id2_num+" "+id1_num+" 1\n");
						count+=1;
					}

					break;

					//System.out.println(senseIdGlossPos.get(id1));
//					for(String l : lemmas2) {
//						System.out.println(l);
//					}
				}

			}

		}

		String header = "p sp "+max_id+" "+count;
		p.println(header);
		p.print(sb.toString());
		p.flush();
		p.close();
		rs.close();
		statement.close();

	}

	public void analyizeLemmaList(String input) throws ClassNotFoundException, SQLException, IOException
	{

		FileReader in = new FileReader("/home/matuschek/ClusterEvaluationTM/GermaNet/WebCAGe-2.0_lemmas.tsv");

		double total_count =  0;
		double n_count = 0 ;
		double v_count = 0 ;
		double a_count = 0 ;
		double total_counts =  0;
		double n_counts = 0 ;
		double v_counts = 0 ;
		double a_counts = 0 ;
		double total_mono =  0;
		double n_mono = 0 ;
		double v_mono = 0 ;
		double a_mono = 0 ;
		 BufferedReader inp =  new BufferedReader(in);
		 Statement statement = connection.createStatement();
		 String line;
		 String lemma ="";
		 String pos ="";
		 ResultSet rs;
		 while((line =inp.readLine())!=null)
		 {
			 lemma = line.split("\t")[0];
			 pos = line.split("\t")[1];
				rs =	statement.executeQuery("select writtenForm,count(senseId) from LexicalEntry join FormRepresentation_Lemma  join Sense where Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and FormRepresentation_Lemma.lemmaId= LexicalEntry.lemmaId and writtenForm = '"+lemma+"' and partOfSpeech like '"+pos+"%' and Sense.senseId like 'GN%' group by writtenForm");
				while(rs.next())
				{
					total_count++;
					String id1 = rs.getString(1);
					int senses = rs.getInt(2);
					if(senses==1) {
						total_mono++;
					}
					total_counts+=senses;
					if(pos.equals("n"))
					{
						n_count++;
						n_counts+=senses;
						if(senses==1) {
							n_mono++;
						}
					}
					else if(pos.equals("v"))
					{
						v_count++;
						v_counts+=senses;
						if(senses==1) {
							v_mono++;
						}
					}
					else if(pos.equals("a"))
					{
						a_count++;
						a_counts+=senses;
						if(senses==1) {
							a_mono++;
						}
					}
				}

		 }
		 System.out.println("Total:");
		 System.out.println(total_count);
		 System.out.println(total_counts);
		 System.out.println(total_counts/total_count);
		 System.out.println(total_mono);

		 System.out.println("N:");
		 System.out.println(n_count);
		 System.out.println(n_counts);
		 System.out.println(n_counts/n_count);
		 System.out.println(n_mono);

		 System.out.println("V:");
		 System.out.println(v_count);
		 System.out.println(v_counts);
		 System.out.println(v_counts/v_count);
		 System.out.println(v_mono);

		 System.out.println("A:");
		 System.out.println(a_count);
		 System.out.println(a_counts);
		 System.out.println(a_counts/a_count);
		 System.out.println(a_mono);




	}


	public void typeTokenRatio() throws ClassNotFoundException, SQLException, IOException
	{
		double token_count = 0;
		FileOutputStream outstream;
		int count = 0;
		TreeSet<String> coveredSenses = new TreeSet<String>();
		PrintStream p;
		outstream = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_ttr.txt");

		p = new PrintStream( outstream );
//		outstream_freq = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_lemma_frequencies.txt");
//		p_freq = new PrintStream( outstream_freq );
		Statement statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs;
		final Pattern CLEANUP = Pattern.compile("[^A-Za-z0-9äöüÄÖÜß]+");
		if(synset)
		{
			rs = statement.executeQuery("SELECT synsetId, writtenText FROM Definition join TextRepresentation_Definition where synsetId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId and length(writtenText)>0");
		}
		else
		{
			rs =	statement.executeQuery("SELECT senseId, writtenText FROM Definition join TextRepresentation_Definition where senseId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId and length(writtenText)>0");
		}
		System.out.println("Reached");
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
				count++;
				if(count % 1000 == 0) {
					System.out.println(count);
				}
				String gloss =  CLEANUP.matcher(rs.getString(2)).replaceAll(" ");
				gloss = gloss.replace("\n", "").replace("\r", "").replace("\t", " ").trim();

				coveredSenses.add(id);
				String[] result=gloss.split(" ");
				 for(String s : result) {
					 token_count++;
						if(!lemmaFreqInGlosses.containsKey(s))
						{
							lemmaFreqInGlosses.put(s, 0);
						}
						int freq = lemmaFreqInGlosses.get(s);
						lemmaFreqInGlosses.put(s, freq+1);

					//System.out.println(s);
				}
			}
		}
		p.println("Tokens: "+token_count);
		p.println("Types: "+lemmaFreqInGlosses.keySet().size());
		p.println("Ratio: "+(lemmaFreqInGlosses.keySet().size()/token_count ));

		rs.close();
		statement.close();
		p.close();
	}


	public void createGlossFile(boolean createLexicalFieldIfEmpty) throws ClassNotFoundException, SQLException, IOException
	{
		FileOutputStream outstream;
		FileOutputStream outstream_freq;
		HashSet<String> coveredSenses = new HashSet<String>();
		PrintStream p;
		PrintStream p_freq;
		outstream = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses.txt");

		p = new PrintStream( outstream );
		outstream_freq = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_lemma_frequencies.txt");
		p_freq = new PrintStream( outstream_freq );
		Statement statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
		ResultSet rs;
		final Pattern CLEANUP = Pattern.compile("[^A-Za-z0-9äöüÄÖÜß]+");
		if(synset)
		{
			rs = statement.executeQuery("SELECT synsetId, writtenText FROM Definition join TextRepresentation_Definition where synsetId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId and length(writtenText)>0");
		}
		else
		{
			rs =	statement.executeQuery("SELECT senseId, writtenText FROM Definition join TextRepresentation_Definition where senseId like '"+prefix_string+"%' and Definition.definitionId = TextRepresentation_Definition.definitionId and length(writtenText)>0");
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
				gloss = gloss.replace("\n", "").replace("\r", "").replace("\t", " ").trim();
				p.println(id+"\t"+gloss);
				coveredSenses.add(id);
				 String[] result=gloss.split(" ");
				 for(String s : result) {
						if(!lemmaFreqInGlosses.containsKey(s))
						{
							lemmaFreqInGlosses.put(s, 0);
						}
						int freq = lemmaFreqInGlosses.get(s);
						lemmaFreqInGlosses.put(s, freq+1);

					//System.out.println(s);
				}
			}
		}
		if(createLexicalFieldIfEmpty)
		{
			HashMap<String,HashSet<String>> idMap = new HashMap<String, HashSet<String>>();
			rs =	statement.executeQuery("SELECT SenseRelation.senseId, writtenForm FROM SenseRelation join Sense join LexicalEntry join FormRepresentation_Lemma where Sense.lexicalEntryId = LexicalEntry.lexicalEntryId and SenseRelation.target =Sense.senseId and Sense.senseID like '"+prefix_string+"%' and FormRepresentation_Lemma.lemmaId = LexicalEntry.lemmaId and (relName like 'hyperynym' or relName like 'hyponym' or relName like 'synonym')");
			while(rs.next())
			{
				String id1 = rs.getString(1);
				if(!idMap.containsKey(id1)) {
					idMap.put(id1,new HashSet<String>());
				}
				HashSet<String> temp = idMap.get(id1);
				temp.add(rs.getString(2));
			}
			for(String s : idMap.keySet())
			{
				String lf = "";
				for(String l : idMap.get(s))
				{
					lf+=l+" ";
				}
				lf.trim();
				lf =  CLEANUP.matcher(lf).replaceAll(" ");
				lf = lf.replace("\n", "").replace("\r", "").replace("\t", " ").trim();
				p.println(s+"\t"+lf);
				 String[] result=lf.split(" ");
				 for(String r : result) {
						if(!lemmaFreqInGlosses.containsKey(r))
						{
							lemmaFreqInGlosses.put(r, 0);
						}
						int freq = lemmaFreqInGlosses.get(r);
						lemmaFreqInGlosses.put(r, freq+1);

					//System.out.println(s);
				}
			}
		}

		 for(String lemma : lemmaFreqInGlosses.keySet())
		 {
			 p_freq.println(lemma+"\t"+lemmaFreqInGlosses.get(lemma));
		 }
		 p_freq.close();
		rs.close();
		statement.close();
		p.close();
	}

	public void lemmatizePOStagGlossFileInChunks(int chunk_size)
	{




		int i = 0;
		int line_count = 1;
		FileOutputStream outstream;
		FileOutputStream outstream_freq;
		PrintStream p;
		PrintStream p_freq ;
		try
		{

			 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses.txt");
			 BufferedReader input_reader =  new BufferedReader(in);
			 String line;
			 StringBuilder sb = new StringBuilder();
			 outstream = new FileOutputStream("target/"+prefix+"temp_"+i);

				p = new PrintStream( outstream );
			 while((line =input_reader.readLine())!=null)
			 {
				 if(line_count % chunk_size ==0)
				 {
					 outstream.flush();
					 outstream.close();
					 p.close();
					 lemmatizePOStagGlossFile(prefix+"temp_"+i, prefix+"temp_tagged_"+i,prefix+"temp_freq_"+i, prefix, language);
					 File f = new File("target/"+prefix+"temp_"+i);
					 f.delete();
					 i++;
					 outstream = new FileOutputStream("target/"+prefix+"temp_"+i);
					 p = new PrintStream( outstream );
					 }

				 p.println(line);
				line_count++;
			 }
			 p.close();
			 lemmatizePOStagGlossFile(prefix+"temp_"+i, prefix+"temp_tagged_"+i,prefix+"temp_freq_"+i, prefix, language);
			 File f = new File("target/"+prefix+"temp_"+i);
			 f.delete();
			 outstream = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses_tagged.txt");
			 outstream_freq = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_lexeme_frequencies.txt");
			 p = new PrintStream(outstream);
			 p_freq = new PrintStream(outstream_freq);
			 for(int x =0 ; x<= i;x++)
			 {
				  f = new File("target/"+prefix+"temp_tagged_"+x);
				 in = new FileReader(f);
				  input_reader =  new BufferedReader(in);
				  while((line =input_reader.readLine())!=null)
				 {
					  p.println(line);
				 }
				  f.delete();
				 in.close();
			 }
			lexemeFreqInGlosses = new TreeMap<String, Integer>();
			 for(int x =0 ; x<= i;x++)
			 {
				  f = new File("target/"+prefix+"temp_freq_"+x);
				 in = new FileReader(f);
				  input_reader =  new BufferedReader(in);
				  while((line =input_reader.readLine())!=null)
				  {
						String lexeme = line.split("\t")[0];

						 int frequency = Integer.parseInt(line.split("\t")[1]);
							if(!lexemeFreqInGlosses.containsKey(lexeme))
							{
								lexemeFreqInGlosses.put(lexeme, 0);
							}
							int freq = lexemeFreqInGlosses.get(lexeme);
							lexemeFreqInGlosses.put(lexeme, frequency+freq);

				  }
				  f.delete();
				 in.close();
			 }
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


	public void lemmatizePOStagGlossFile(String input, String output1,String output2, int prefix, String lang)
	{
		if(lexemeFreqInGlosses == null) {
			lexemeFreqInGlosses = new TreeMap<String, Integer>();
		}

		int i = 0;
		FileOutputStream outstream;
		FileOutputStream outstream_freq;
		PrintStream p;
		PrintStream p_freq;
		try
		{
		outstream = new FileOutputStream("target/"+output1);
		outstream_freq = new FileOutputStream("target/"+output2);
		// Connect print stream to the output stream
		p = new PrintStream( outstream );
		p_freq = new PrintStream(outstream_freq);
		 FileReader in = new FileReader("target/"+input);
		 BufferedReader input_reader =  new BufferedReader(in);
		 String line;
		 StringBuilder sb = new StringBuilder();
		 while((line =input_reader.readLine())!=null)
		 {

			sb.append(line.replace("\t","TABULATOR ")+" ENDOFLINE ");

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
		resultline = resultline.replaceAll("endofline#\\S*\\s", Global.LF);
		resultline = resultline.replaceAll("TABULATOR#\\S*\\s", "\t");
		resultline = resultline.replaceAll("ENDOFLINE#\\S*\\s", Global.LF);
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


	public void fillIndexTables() throws ClassNotFoundException, SQLException, IOException
		{
			String prefix_string  = Global.prefixTable.get(prefix);


//			if(lemmaFreqInGlosses.size()==0) {
//				lemmaFreqInGlosses = new TreeMap<String, Integer>();
//				 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_lemma_frequencies.txt");
//				 BufferedReader input_reader =  new BufferedReader(in);
//				 String line;
//				 while((line =input_reader.readLine())!=null)
//				 {
//
//					 String lemma = line.split("\t")[0];
//					 int frequency = Integer.parseInt(line.split("\t")[1]);
//	//				 System.out.println(lexeme+" "+frequency);
//					 lemmaFreqInGlosses.put(lemma, frequency);
//				 }
//
//			}
//			System.out.println("Lemma frequencies filled for "+this.prefix_string);
//
//			if(lexemeFreqInGlosses.size()==0) {
//				lexemeFreqInGlosses = new TreeMap<String, Integer>();
//				 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_lexeme_frequencies.txt");
//				 BufferedReader input_reader =  new BufferedReader(in);
//				 String line;
//				 while((line =input_reader.readLine())!=null)
//				 {
//
//					 String lexeme = line.split("\t")[0];
//					 int frequency = Integer.parseInt(line.split("\t")[1]);
//	//				 System.out.println(lexeme+" "+frequency);
//					 lexemeFreqInGlosses.put(lexeme, frequency);
//				 }
//
//			}
			System.out.println("Lexeme frequencies filled for "+this.prefix_string);
//			if(senseIdGloss == null || senseIdGloss.size()==0) {
//				senseIdGloss = new TreeMap<String, String>();
//				 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses.txt");
//				 BufferedReader input_reader =  new BufferedReader(in);
//				 String line;
//				 while((line =input_reader.readLine())!=null)
//				 {
//					 gloss_count++;
//					 String id = line.split("\t")[0];
//					 if(line.split("\t").length != 2) {
//						continue;
//					}
//					 String gloss = line.split("\t")[1];
//					 senseIdGloss.put(id,gloss);
//				 }
//
//			}
			System.out.println("Glosses filled for "+this.prefix_string);

			if(senseIdGlossPos == null || senseIdGlossPos.size()==0) {
				senseIdGlossPos = new TreeMap<String, String>();
				 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses_tagged.txt");
				 BufferedReader input_reader =  new BufferedReader(in);
				 String line;
				 while((line =input_reader.readLine())!=null)
				 {
					  String id = line.split("\t")[0];
						 if(line.split("\t").length != 2) {
								continue;
							}

					 String gloss = line.split("\t")[1];
					 senseIdGlossPos.put(id,gloss);
				 }

			}
			System.out.println("Tagged glosses filled for "+this.prefix_string);

//			Statement statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
//			statement.setFetchSize(Integer.MIN_VALUE);
//			ResultSet rs =	statement.executeQuery("select distinct LexicalEntry.lemmaId,writtenForm from FormRepresentation_Lemma join LexicalEntry where LexicalEntry.lexicalEntryId like '"+prefix_string+"%' and LexicalEntry.lemmaId = FormRepresentation_Lemma.lemmaId");
//			while(rs.next())
//			{
//				String lemmaId = rs.getString(1);
//				String writtenForm = rs.getString(2);
//
//				lemmaIdWrittenForm.put(lemmaId,writtenForm);
//			}
//			rs =	statement.executeQuery("select distinct lemmaId,partOfSpeech, "+ (synset ? "synsetId" : "senseId") +" from LexicalEntry join Sense where LexicalEntry.lexicalEntryId like '"+prefix_string+"%' and LexicalEntry.lexicalEntryId = Sense.lexicalEntryId");

//			while(rs.next())
//			{
//				String lemmaId = rs.getString(1);
//				String lemma;
//				lemma = lemmaIdWrittenForm.get(lemmaId);
//				String POS = rs.getString(2);
//				String senseId = rs.getString(3);
//				if(synset)
//				{
//					senseId = prefix+senseId.split("ynset_")[1];
//
//				}
//				else
//				{
//					senseId = prefix+senseId.split("ense_")[1];
//				}
//				//System.out.println(lemma+" "+senseId);
//	//			System.out.println(senseId);
//
//				if(lemma == null) {
//					continue;
//				}
//				String key = "";
//				if(pos) {
//
//					if(POS == null ) {
//						key =lemma.toLowerCase()+"#"+"null";
//					}
//					else {
//						key =lemma.toLowerCase()+"#"+POS.replace("Common", "");
//					}
//				}
//				else
//				{
//					key =lemma.toLowerCase();
//				}
//				if(!senseIdLemma.containsKey(senseId))
//				{
//					senseIdLemma.put(senseId, new HashSet<String>());
//				}
//				senseIdLemma.get(senseId).add(key);
//
//
//				if(!lemmaPosSenses.containsKey(key))
//				{
//					lemmaPosSenses.put(key, new HashSet<String>());
//				}
//				lemmaPosSenses.get(key).add(senseId);
//
//
//			}
	//		for(String key : lemmaPosSenses.keySet())
	//		{
	//			System.out.println(key+" "+lemmaPosSenses.get(key).size());
	//		}
//			rs.close();
//			statement.close();
//			System.out.println("Lexeme-sense map filled for "+this.prefix_string);
		}


	public void createMonosemousLinks(int phi) throws ClassNotFoundException, SQLException, IOException
		{

			StringBuilder sb = new StringBuilder();
			int count = 0;
			int max_id = 0;
			FileOutputStream outstream;
			PrintStream p;
			 FileReader in = new FileReader("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_glosses_tagged.txt");
			 BufferedReader input_reader =  new BufferedReader(in);
				outstream = new FileOutputStream("target/"+prefix_string+"_"+(synset?"synset":"sense")+"_"+(pos ? "Pos":"noPos")+"_monosemousLinks"+"_"+phi+".txt");
				p = new PrintStream( outstream );
			 String line;
			 while((line =input_reader.readLine())!=null)
			 {
				 String id1 = line.split("\t")[0];
				 if((line.split("\t")).length<2) {
					continue;
				}
				 String[] lexemes = line.split("\t")[1].split(" ");
				 for(String lexeme:lexemes)
				 {
//					 System.out.println(lexeme);

					 if(lexemeFreqInGlosses == null || lexemeFreqInGlosses.size() ==0) {
							System.err.println("Index Tables not initialized");
					}

					 if(lexemeFreqInGlosses.get(lexeme) == null || lexemeFreqInGlosses.get(lexeme)>phi)
					 {
						 continue;
					 }
					 if(pos)
					 {
	//					 System.out.println(lexeme);
						 if(lemmaPosSenses.get(lexeme)!= null && lemmaPosSenses.get(lexeme).size()==1)
						 {
	//						 System.out.println("Mono found!");
							 String id2= lemmaPosSenses.get(lexeme).iterator().next();
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
						 if(lemmaPosSenses.get(lemma)!= null && lemmaPosSenses.get(lemma).size()==1)
						 {
							 String id2= lemmaPosSenses.get(lemma).iterator().next();
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










}
