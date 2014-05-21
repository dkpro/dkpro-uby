package de.tudarmstadt.ukp.alignment.framework;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Global
{
	public static final int WN_prefix =  10;
	public static final int WKT_EN_prefix =  11;
	public static final int WP_EN_prefix =  12;
	public static final int OW_EN_prefix =  13;
	public static final int FN_prefix =  14;
	public static final int VN_prefix =  15;

	public static final int WKT_DE_prefix =  16;
	public static final int WP_DE_prefix =  17;
	public static final int OW_DE_prefix =  18;
	public static final int GN_prefix =  19;
	public static final int IMS_prefix =  20;
	public static  HashMap<Integer, String> prefixTable = new HashMap<Integer, String>();
	public static final String LF = System.getProperty("line.separator");
	public static void init()
	{
		prefixTable.put(Global.GN_prefix, "GN");
		prefixTable.put(Global.WN_prefix, "WN");
		prefixTable.put(Global.WKT_EN_prefix, "WktEn");
		prefixTable.put(Global.WKT_DE_prefix, "WktDe");
		prefixTable.put(Global.WP_EN_prefix, "WikiEn");
		prefixTable.put(Global.WP_DE_prefix, "WikiDe");
		prefixTable.put(Global.OW_EN_prefix, "OW_en");
		prefixTable.put(Global.OW_DE_prefix, "OW_de");
		prefixTable.put(Global.FN_prefix, "FN");
		prefixTable.put(Global.VN_prefix, "VN");
		prefixTable.put(Global.IMS_prefix, "IMS");
	}

	public static void mergeTwoGraphs(String infile1,String infile2, String outfile ) throws ClassNotFoundException,  IOException
	{
		StringBuilder sb = new StringBuilder();
		/*Take care of having it undirected*/
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
				 sb.append(line+Global.LF);
			 }
			 if(i++ % 1000 ==0) {
			System.out.println("Lines processed "+i);
			}


		 }
		 //p.println("p sp "+nodes_count+" "+arcs_count);
			in = new FileReader("target/"+infile2);
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
					 sb.append(line+Global.LF);
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
