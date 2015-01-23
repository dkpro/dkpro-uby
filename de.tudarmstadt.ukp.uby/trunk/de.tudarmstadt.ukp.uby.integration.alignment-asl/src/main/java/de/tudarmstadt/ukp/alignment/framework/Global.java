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
package de.tudarmstadt.ukp.alignment.framework;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.alignment.framework.graph.OneResourceBuilder;

public class Global
{
	public static final int WN_Synset_prefix =  10;
	public static final int WN_Sense_prefix =  11;
	public static final int WKT_EN_prefix =  12;
	public static final int OW_EN_Synset_prefix =  13;
	public static final int OW_EN_Sense_prefix =  14;
	public static final int WP_EN_prefix =  15;
	public static final int FN_prefix =  16;
	public static final int VN_prefix =  17;

	public static final int WKT_DE_prefix =  18;
	public static final int WP_DE_prefix =  19;
	public static final int OW_DE_Synset_prefix =  20;
	public static final int OW_DE_Sense_prefix =  21;
	public static final int GN_Synset_prefix =  22;
	public static final int GN_Sense_prefix =  23;
	public static final int IMS_prefix =  24;

	public static  HashMap<Integer, String> prefixTableLong = new HashMap<Integer, String>();
	public static  HashMap<Integer, String> prefixTable = new HashMap<Integer, String>();
	public static final String LF = System.getProperty("line.separator");
	public static void init()
	{
		prefixTableLong.put(Global.GN_Synset_prefix, "GN_Synset_");
		prefixTableLong.put(Global.GN_Sense_prefix, "GN_Sense_");
		prefixTableLong.put(Global.WN_Synset_prefix, "WN_Synset_");
		prefixTableLong.put(Global.WN_Sense_prefix, "WN_Sense_");
		prefixTableLong.put(Global.WKT_EN_prefix, "WktEN_sense_");
		prefixTableLong.put(Global.WKT_DE_prefix, "WktDN_sense_");
		prefixTableLong.put(Global.WP_EN_prefix, "WikiEn_sense_");
		prefixTableLong.put(Global.WP_DE_prefix, "WikiDe_sense_");
		prefixTableLong.put(Global.OW_EN_Synset_prefix, "OW_en_Synset_");
		prefixTableLong.put(Global.OW_EN_Sense_prefix, "OW_en_Sense_");
		prefixTableLong.put(Global.OW_DE_Synset_prefix, "OW_de_Synset_");
		prefixTableLong.put(Global.OW_DE_Sense_prefix, "OW_de_Sense_");
		prefixTableLong.put(Global.FN_prefix, "FN_Sense_");
		prefixTableLong.put(Global.VN_prefix, "VN_Sense_");
		prefixTableLong.put(Global.IMS_prefix, "IMSLexSubcat_Sense_");

		prefixTable.put(Global.GN_Synset_prefix, "GN");
		prefixTable.put(Global.GN_Sense_prefix, "GN");
		prefixTable.put(Global.WN_Synset_prefix, "WN");
		prefixTable.put(Global.WN_Sense_prefix, "WN");
		prefixTable.put(Global.WKT_EN_prefix, "WktEn");
		prefixTable.put(Global.WKT_DE_prefix, "WktDe");
		prefixTable.put(Global.WP_EN_prefix, "WikiEn");
		prefixTable.put(Global.WP_DE_prefix, "WikiDe");
		prefixTable.put(Global.OW_EN_Synset_prefix, "OW_en");
		prefixTable.put(Global.OW_EN_Sense_prefix, "OW_en");
		prefixTable.put(Global.OW_DE_Synset_prefix, "OW_de");
		prefixTable.put(Global.OW_DE_Sense_prefix, "OW_de");
		prefixTable.put(Global.FN_prefix, "FN");
		prefixTable.put(Global.VN_prefix, "VN");
		prefixTable.put(Global.IMS_prefix, "IMSLex");
	}

	public static void mergeTwoGraphs(String infile1,String infile2, String outfile ) throws ClassNotFoundException,  IOException
	{
		StringBuilder sb = new StringBuilder();
		FileReader in = new FileReader("target/"+infile1);
		BufferedReader input =  new BufferedReader(in);
		FileOutputStream outstream;
		PrintStream p;
		outstream = new FileOutputStream("target/"+outfile);
		p = new PrintStream( outstream );

		int maxId = 0;
		int size = 0;
		 String line;

		int i = 0;
		int edge_count=0;
		 while((line =input.readLine())!=null)
		 {
			 if(line.startsWith("p"))
			 {
				 String[] info = line.split(" ");
				 maxId = Integer.parseInt(info[2]);
				 size = Integer.parseInt(info[3]);


			}
			 else if(line.startsWith("e"))
			 {
				 String[] temp = line.split(" ");
				 //sb.append(line+Global.LF);
				 sb.append("e"+edge_count+++" "+temp[1]+" "+temp[2]+Global.LF);
			 }
			 if(i++ % 1000 ==0) {
			System.out.println("Lines processed "+i);
			}


		 }
		 input.close();
			in = new FileReader("target/"+infile2);
			input =  new BufferedReader(in);
			 while((line =input.readLine())!=null)
			 {
				 if(line.startsWith("p"))
				 {
					 String[] info = line.split(" ");
					 int max = Integer.parseInt(info[2]);
					 if(max>maxId) {
						maxId = max;
					}
					 size = size+ Integer.parseInt(info[3]);
				}
				 else if (line.startsWith("e"))
				 { String[] temp = line.split(" ");
					 sb.append("e"+edge_count+++" "+temp[1]+" "+temp[2]+Global.LF);
				//	 sb.append(line+Global.LF);
				 }
				if(i++ % 1000 ==0) {
				System.out.println("Lines processed "+i);
				}


			 }
		//	 p.println("p sp "+maxId+" "+size);
			 p.println("graph class=grph.in_memory.InMemoryGrph");
			 p.print(sb.toString());
			 input.close();
			 p.close();
	}

	public static double overlap(List<String> o1, List<String> o2)
	{
		Set<Object> set1 = new HashSet<Object>(o1);
		Set<Object> set2 = new HashSet<Object>(o2);

		set2.retainAll(set1);
		return set2.size();
	}
	/**
	 * This method maps an alignment file with numerical IDs to either UBY-IDs or the original IDs
	 * 
	 * @param extRef states whether the original ids should be used
	 * 
	 * */
	public static void mapAlignmentToUby(OneResourceBuilder gb1, OneResourceBuilder gb2, String alignmentfile, boolean extRef)
	{

		int i = 0;
		FileOutputStream outstream;
		PrintStream p;
		try
		{
		String alignment_file= alignmentfile;
		outstream = new FileOutputStream(alignment_file.replace(".txt", "")+"_"+(extRef? "extRef": "UbyID")+".txt");
		 p = new PrintStream( outstream );
		 FileReader in = new FileReader(alignment_file);
		 BufferedReader input_reader =  new BufferedReader(in);
		 String line;
		 HashMap<String,String> extRefs1 = new HashMap<String, String>();
		 HashMap<String,String> extRefs2 = new HashMap<String, String>();
		 if(extRef)
		 {

			 Statement statement= gb1.connection.createStatement();
			 ResultSet rs = statement.executeQuery("SELECT externalReference, "+(gb1.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb1.synset? "synsetId" : "senseId")+" like '"+gb1.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs1.put(rs.getString(2),rs.getString(1));
			 }
			 statement.close();
			 statement= gb2.connection.createStatement();
			 rs = statement.executeQuery("SELECT externalReference, "+(gb2.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb2.synset? "synsetId" : "senseId")+" like '"+gb2.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs2.put(rs.getString(2),rs.getString(1));
			 }
			 statement.close();
			 rs.close();
		 }
		 while((line =input_reader.readLine())!=null)
		 {
			if(line.startsWith("f"))
			{
				p.println(line);
				continue;
			}
			String[] temp = line.split("\t");
			String id1 = temp[0];
			String id2 = temp[1];
			String conf = temp[2];
			String uby_id1 = Global.prefixTableLong.get(Integer.parseInt(id1.substring(0, 2)))+id1.substring(2);
			String uby_id2 = Global.prefixTableLong.get(Integer.parseInt(id2.substring(0, 2)))+id2.substring(2);
			if(extRef)
			{
				p.println(extRefs1.get(uby_id1)+"\t"+extRefs2.get(uby_id2)+"\t"+conf);
			}
			else
			{
				p.println(uby_id1+"\t"+uby_id2+"\t"+conf);
			}

			System.out.println("lines processed "+i++);
		 }
		 input_reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();


		}
	
		/*TODO: Conform to newly defined XML standard*/
		/*
		 *
		 * */
		/*TODO: Create actual SenseAxis instances? NO! Use import class in UBY!*/

	}
	/**
	 * This method streamlines proprieatary alignment gold standard files
	 * 
	 * @param graph states whether numerical ids for the graph should be created
	 * 
	 * */
	public static void processExtRefGoldstandardFile(OneResourceBuilder gb1, OneResourceBuilder gb2, String alignmentfile, boolean graph)
	{
		/*TODO Has to be adapted to new standard - this is a propietary solution for now*/

		int i = 0;
		FileOutputStream outstream;
		PrintStream p;
		try
		{
		String alignment_file= alignmentfile;
		outstream = new FileOutputStream(alignment_file.replace(".csv", "")+"_"+(graph? "graph": "UbyID")+".csv");
		p = new PrintStream( outstream );
	 FileReader in = new FileReader(alignment_file);
	 BufferedReader input_reader =  new BufferedReader(in);
		 String line;
		 StringBuilder sb = new StringBuilder();
		 HashMap<String,String> extRefs1 = new HashMap<String, String>();
		 HashMap<String,String> extRefs2 = new HashMap<String, String>();
		 Statement statement= gb1.connection.createStatement();
		 ResultSet rs = statement.executeQuery("SELECT externalReference, "+(gb1.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb1.synset? "synsetId" : "senseId")+" like '"+gb1.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs1.put(rs.getString(1),rs.getString(2));
			 }
			 statement.close();
			 statement= gb2.connection.createStatement();
			 rs = statement.executeQuery("SELECT externalReference, "+(gb2.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb2.synset? "synsetId" : "senseId")+" like '"+gb2.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs2.put(rs.getString(1),rs.getString(2));
			 }
			 statement.close();
			 rs.close();

		 while((line =input_reader.readLine())!=null)
		 {
			if(line.startsWith("f"))
			{
				p.println(line);
				continue;
			}
			line = line.replace("\"","");
			line = line.replace(":","\t");
			String[] temp = line.split("\t");
			String id1 = temp[0];
			if (id1.endsWith("#n"))
			{
				id1 = "[POS: noun] "+id1.split("#")[0];
			}
			else if (id1.endsWith("#v"))
			{
				id1 = "[POS: verb] "+id1.split("#")[0];
			}
			else if (id1.endsWith("#a"))
			{
				id1 = "[POS: adjective] "+id1.split("#")[0];
			}
			else if (id1.endsWith("#r"))
			{
				id1 = "[POS: adverb] "+id1.split("#")[0];
			}

			String id2 = temp[1];
			String conf = temp[2];
			String uby_id1 = extRefs1.get(id1);//Global.prefixTableLong.get(Integer.parseInt(id1.substring(0, 2)))+id1.substring(2);
			String uby_id2 = extRefs2.get(id2);//Global.prefixTableLong.get(Integer.parseInt(id2.substring(0, 2)))+id2.substring(2);
			if(uby_id1==null || uby_id2 == null) {
				continue;
			}
			if(graph)
			{
				p.println(gb1.prefix+uby_id1.split(Global.prefixTableLong.get(gb1.prefix))[1] + "\t" +gb2.prefix+uby_id2.split(Global.prefixTableLong.get(gb2.prefix))[1]+"\t"+conf);
			}
			else
			{
				p.println(uby_id1+"\t"+uby_id2+"\t"+conf);
			}

			System.out.println("lines processed "+i++);
		 }
		 input_reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();


		}
		//CONTINUE HERE
		/*TODO: Conform to newly defined standard*/
		/*
		 *
		 * */
		/*TODO: Create actual SenseAxis instances? NO! Use import class in UBY!*/

	}
	/**
	 * This method streamlines proprieatary alignment gold standard files as provided by EW and ChM
	 * 
	 * @param graph states whether numerical ids for the graph should be created
	 * 
	 * */
	public static void processExtRefGoldstandardFileWKTWP(OneResourceBuilder gb1, OneResourceBuilder gb2, String alignmentfile, boolean graph)
	{
		/*TODO Has to be adapted to new standard - this is a propietary solution for now*/

		int i = 0;
		FileOutputStream outstream;
		PrintStream p;
		try
		{
		String alignment_file= alignmentfile;
		outstream = new FileOutputStream(alignment_file.replace(".txt", "")+"_"+(graph? "graph": "UbyID")+".csv");
		p = new PrintStream( outstream );
	 FileReader in = new FileReader(alignment_file);
	 BufferedReader input_reader =  new BufferedReader(in);
		 String line;
		 StringBuilder sb = new StringBuilder();
		 HashMap<String,String> extRefs1 = new HashMap<String, String>();
		 HashMap<String,String> extRefs2 = new HashMap<String, String>();
		 Statement statement= gb1.connection.createStatement();
		 ResultSet rs = statement.executeQuery("SELECT externalReference, "+(gb1.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb1.synset? "synsetId" : "senseId")+" like '"+gb1.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs1.put(rs.getString(1),rs.getString(2));
			 }
			 statement.close();
			 statement= gb2.connection.createStatement();
			 rs = statement.executeQuery("SELECT externalReference, "+(gb2.synset? "synsetId" : "senseId")+" FROM MonolingualExternalRef where "+(gb2.synset? "synsetId" : "senseId")+" like '"+gb2.prefix_string+"%' ");
			 while(rs.next())
			 {
				 extRefs2.put(rs.getString(1),rs.getString(2));
			 }
			 statement.close();
			 rs.close();

		 while((line =input_reader.readLine())!=null)
		 {
			if(line.startsWith("#")  )
			{
			//	p.println(line);
				continue;
			}
			
			String[] temp = line.split(" ; ");
			String id1 = temp[0];
			String pos = temp[1];
			if (pos.equals("noun"))
			{
				id1 = "[POS: noun] "+id1;
			}
			else if (pos.equals("verb"))
			{
				id1 = "[POS: verb] "+id1;
			}
			else if (pos.equals("adjective"))
			{
				id1 = "[POS: adjective] "+id1;
			}
			else if (pos.equals("#adverb"))
			{
				id1 = "[POS: adverb] "+id1;
			}

			String id2 = temp[3];
			String conf = temp[4];
			System.out.println(id1);
			System.out.println(id2);
			String uby_id1 = extRefs1.get(id1);//Global.prefixTableLong.get(Integer.parseInt(id1.substring(0, 2)))+id1.substring(2);
			String uby_id2 = extRefs2.get(id2);//Global.prefixTableLong.get(Integer.parseInt(id2.substring(0, 2)))+id2.substring(2);
			System.out.println(uby_id1);
			System.out.println(uby_id2);
			if(uby_id1==null || uby_id2 == null) {
				continue;
			}
			if(graph)
			{
				
			System.out.println(uby_id2);				System.out.println(Global.prefixTableLong.get(gb2.prefix));
				p.println(gb1.prefix+uby_id1.split(Global.prefixTableLong.get(gb1.prefix))[1] 
						+ "\t" +gb2.prefix+uby_id2.split(Global.prefixTableLong.get(gb2.prefix))[1]+"\t"+conf);
			}
			else
			{
				p.println(uby_id1+"\t"+uby_id2+"\t"+conf);
			}

			System.out.println("lines processed "+i++);
		 }
		 input_reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();


		}
		//CONTINUE HERE
		/*TODO: Conform to newly defined standard*/
		/*
		 *
		 * */
		/*TODO: Create actual SenseAxis instances? NO! Use import class in UBY!*/

	}
}
