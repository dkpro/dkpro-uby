/*******************************************************************************
 * Copyright 2016
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

package de.tudarmstadt.ukp.lmf.transform.sensealignments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;

public class VerbNetFrameNetSenseAlignment
	extends SenseAlignment
{

	static String UBY_HOME = System.getenv("UBY_HOME");
	public int inputsize = 0;
	public ArrayList<String> notAdded;

	public VerbNetFrameNetSenseAlignment(String sourceUrl, String destUrl,String dbDriver,String dbVendor,
			String alignmentFile,String user, String pass, String UBY_HOME) throws FileNotFoundException
	{
		super(sourceUrl, destUrl, dbDriver,dbVendor, alignmentFile, user, pass,UBY_HOME);
		notAdded = new ArrayList<String>();
	}



	public VerbNetFrameNetSenseAlignment(String sourceUrl, String destUrl,
			String alignmentFile,String user, String pass) throws FileNotFoundException
	{
		this(sourceUrl, destUrl, "com.mysql.jdbc.Driver","mysql", alignmentFile, user, pass,UBY_HOME);
	}

	@Override
	public void getAlignment() throws IllegalArgumentException
	{
		BufferedReader reader = null;
		try {
			System.out.println(getAlignmentFileLocation());
			reader = new BufferedReader(new FileReader(getAlignmentFileLocation()));
			String line = null;
			int count=0;
			Lexicon vnLex = ubySource.getLexiconByName("VerbNet");
			while ((line = reader.readLine()) != null) {
				inputsize++;
				if (inputsize%200==0){
					System.out.println("# alignments: " + inputsize);
				}
				StringBuffer lineinfo = new StringBuffer();
				String[] items = line.split("\t");
				String luId = items[0];
				String vnLemma = items[1].trim();
				String vnClass = items[2];
				int added = 0;
				// get FrameNet senses by given luId
				List<Sense> senses=ubySource.getSensesByOriginalReference("FrameNet_1.5_eng_lexicalUnit", luId);
				if (senses.size() > 0){
					for (Sense fns: senses){
						// get potential vn targets (defined by lemma and pos, and VN-class)
						List<LexicalEntry> entries = ubySource.getLexicalEntries(vnLemma, EPartOfSpeech.verb, vnLex);
						if (entries.size() > 0){
							for (LexicalEntry e: entries){
								List<Sense> vnSenses = e.getSenses();
								for (Sense vns: vnSenses){
									String senseId = vns.getId();
									// filter by VN-class
									List<SemanticLabel> labels = ubySource.getSemanticLabelsbySenseIdbyType(senseId, ELabelTypeSemantics.verbnetClass.toString());
									for (SemanticLabel l: labels){
										String[] labelItems = l.getLabel().split("-");
										StringBuffer parsedLabel = new StringBuffer();
										parsedLabel.append(labelItems[1]);
										for (int i=2;i<labelItems.length;i++) {
											parsedLabel.append("-"+labelItems[i]);
										}
										if (parsedLabel.toString().equals(vnClass)) {
											addSourceSense(fns);
											addDestSense(vns);
											added++;
											count++;
										}
									}
								}
							}

						} else {
							lineinfo.append("-VN sense for this lemma-POS not found. ");
						}
					}
				} else {
					lineinfo.append("-FN sense not found: " + luId);
				}
				if (added == 0){
					notAdded.add(lineinfo.toString());
				}
			}
			for (String lineinfo : notAdded){
				System.out.println(lineinfo);
			}
			System.out.println("number of alignments:" + count);
			System.out.println("number of lines in infile:" + inputsize);
			reader.close();
		}	catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public static void convertVnFnSemlink(String inFile, String outFile) throws XMLStreamException,
		ParserConfigurationException, SAXException, IOException{

		int noTarget = 0;
		int lines = 0;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(inFile));
		doc.getDocumentElement().normalize();
		NodeList entries = doc.getElementsByTagName("vncls");
		ArrayList<String> output = new ArrayList<String>();
		for (int i=0;i<entries.getLength();i++){
			Node alignment = entries.item(i);
			NamedNodeMap atts = alignment.getAttributes();
			String vnclass = atts.getNamedItem("class").getTextContent();
			String vnlemma = atts.getNamedItem("vnmember").getTextContent();
			String luId = atts.getNamedItem("fnlexent").getTextContent();
			// there are mappings with empty (fn) target:
			if (luId.equals("")){
				noTarget++;
			} else {
				output.add( luId+"\t"+ vnlemma+"\t"+vnclass+"\n");
			}
			lines++;
		}
		System.out.println("Converted " + inFile + ", statistics:");
		System.out.println("\tInput Lines: " + lines);
		System.out.println("\tOutput: " + output.size());
		System.out.println("\tNo alignment target: " + noTarget);
		System.out.println("\tControl: output +  no alignment = input lines: " + (output.size() + noTarget));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFile)));
			for (String line: output){
				writer.write(line);
			}
		} catch (IOException e) {
			System.err.println("Exception" + e + "could not write to" + outFile);
		} finally {
			if (writer!=null) {
				writer.close();
			}
		}
	}
}
