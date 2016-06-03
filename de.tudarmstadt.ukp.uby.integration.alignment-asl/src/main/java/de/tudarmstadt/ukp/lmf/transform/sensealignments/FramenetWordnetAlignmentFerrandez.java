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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Convert the FrameNet-WordNet alignment by Ferrandez et al. (2010) to UBY
 * format.
 * 
 * Reference: O. Ferrández, M. Ellsworth, R. Muñoz, and C. F. Baker: Aligning
 * FrameNet and WordNet Based on Semantic Neighborhoods, LREC 2010
 * 
 * @author Silvana Hartmann
 * 
 * FOR UKP INTERNAL USE ONLY see header
 * 
 */
public class FramenetWordnetAlignmentFerrandez extends FramenetWordnetAlignment {

	public FramenetWordnetAlignmentFerrandez(String sourceUrl, String destUrl,
			String alignmentFile, String user, String pass)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			FileNotFoundException {
		super(sourceUrl, destUrl, alignmentFile, user, pass);
	}

	/**
	 * Convert original alignment file to .tsv format Output format: fn-luId,
	 * wn-sensekey, wn lemma, fn lemma
	 * 
	 * @param inFile
	 *            location of original alignment file
	 * @param outFile
	 *            output file
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private static void convertFerrandezEtAl(String inFile, String outFile)
			throws XMLStreamException, ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(inFile));
		doc.getDocumentElement().normalize();
		NodeList entries = doc.getElementsByTagName("alignment");// <alignment
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < entries.getLength(); i++) {
			Element alignment = (Element) entries.item(i);
			NamedNodeMap atts = alignment.getAttributes();
			String lemma = atts.getNamedItem("lemma").getTextContent();
			lemma = lemma.replace("_", " ");
			NodeList lus = alignment.getElementsByTagName("LU");// <LU
			for (int k = 0; k < lus.getLength(); k++) {
				Element fn = (Element) lus.item(k);
				NamedNodeMap atts2 = fn.getAttributes();
				String luId = atts2.getNamedItem("ID").getTextContent();
				String fnLemma = atts2.getNamedItem("lemma").getTextContent();
				fnLemma = fnLemma.replace("_", " "); // replace underscores in
														// multiword lemmas by
														// whitespace
				Node wn = fn.getChildNodes().item(1);
				String synsetId = wn.getAttributes().getNamedItem("ID").getNodeValue();
				String synsetPos = wn.getAttributes().getNamedItem("PoS").getNodeValue();
				NodeList senses = wn.getChildNodes();
				for (int j = 0; j < senses.getLength(); j++) {
					Node sense = senses.item(j);
					String node = sense.getNodeName();
					if (node.equals("word")) {
						String wnLemma = sense.getTextContent();
						wnLemma = wnLemma.replace("_", " ");
						// need pos abbreviation for external reference id for
						// WN synsets:
						String pos = "n";
						if (synsetPos.equals("noun")) {
							pos = "n";
						} else if (synsetPos.equals("adjective")) {
							pos = "a";
						} else if (synsetPos.equals("adverb")) {
							pos = "r";
						} else if (synsetPos.equals("verb")) {
							pos = "v";
						}
						// remove semantic qualifiers from WN lemmas, such as
						// "sleep ((quantity))":
						// Problem: some compound-like structures are also
						// filtered: "take ((upon))"
						String wnCleanLemma = wnLemma.split("\\(")[0];
						output.add(luId + "\t" + synsetId + "-" + pos + "\t"
								+ wnCleanLemma + "\t" + fnLemma);
					}
				}
			}
		}
		writeLines(outFile, output);
		logger.info("# entries in original file " + inFile + ": "
				+ entries.getLength());
		logger.info("# output entries (written to " + outFile
				+ "): " + output.size());
	}
}
