/**
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package de.tudarmstadt.ukp.lmf.transform.germanet;

import germanet.GermaNet;
import germanet.LexUnit;
import germanet.Synset;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This class offers methods for extraction of semantic class labels
 * of LexUnits and Synsets. <br>
 * Instance of this class parses the names of <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/">GermaNet 6.0</a>
 * files (semantic labels) and determines the LexUnits <br> and Synsets contained in the file for which the semantic label applies.
 * @author Zijad Maksuti, Judith Eckle-Kohler
 * @see LexUnit
 * @see Synset
 * 
 */
public class SemanticClassLabelExtractor {

	private File gnData; // directory containing the GermaNet files
	private Map<Integer, String> luMappings = new HashMap<Integer, String>(); // luID <-> filename mappings
	private Map<Integer, String> synsetMappings = new HashMap<Integer, String>(); // synsetID <-> filename mappings
	
	private Logger logger = Logger.getLogger(GNConverter.class.getName());

	/**
	 * Constructs an instance of {@link SemanticClassLabelExtractor}
	 * 
	 * @param gn initialized {@link GermaNet} object used to access GermaNet's information
	 * 
	 */
	public SemanticClassLabelExtractor(GermaNet gn) {
		this.gnData = new File(gn.getDir());
		this.initialize();
	}
	
	/**
	 * This method consumes an instance of {@link LexUnit}, and returns it's semantic class label
	 * @param lu a LexUnit for which semantic class label should be extracted
	 * @return lu's semantic class label or null if the extractor contains no mapping for the lu's id
	 */
	public String getLUSemanticClassLabel(LexUnit lu){
		String result = luMappings.get(lu.getId());
		if(result != null)
			return result.split("\\.")[1]; // extract the semantic class label
		return result;
	}
	
	/**
	 * This class consumes an instance of {@link Synset}, and returns it's semantic class label
	 * @param synset a Synset for which semantic class lable should be extracted 
	 * @return synset's semantic class label or null if the extractor contains no mapping for the synset's ID
	 */
	public String getSynsetSemanticClassLabel(Synset synset){
		int synsetID=synset.getId();
		String result = synsetMappings.get(synsetID);
		if(result != null)
			return result.split("\\.")[1]; // extract the SemanticClasLabel
		return result;
	}

	/**
	 * This method iterates over GermaNet's files and extracts semantic class labels <br>
	 * of Synsets and LexUnits
	 * @see LexUnit
	 * @see Synset
	 */
	private void initialize() {
		if(luMappings.isEmpty()){
			logger.log(Level.INFO, "Initializing SemanticClassLabelExtractor... ");
		
			String[] fileNames = gnData.list(); // Names of all files in GermaNet's directory
			
			for (String fileName : fileNames) {
				// If a file starts with "adj.", "nomen." or "verben." and ends with ".xml" it should be examined
				if ((fileName.startsWith("adj.") || fileName.startsWith("nomen.") || fileName.startsWith("verben")) && fileName.endsWith(".xml")) {
	
					SAXReader reader = new SAXReader();
					Document document = null;
					try {
						document = reader.read(new File(gnData.getAbsolutePath() + "/" + fileName));
					} catch (DocumentException e) {
						StringBuffer sb = new StringBuffer(128);
						sb.append("SemanticClassLabelExtractor: error on reading GermaNet's files");
						sb.append('\n').append("Aborting all operations!").append('\n');
						sb.append("cause").append('\n');
						sb.append(e.getMessage());
						logger.log(Level.SEVERE, sb.toString());
						System.exit(1);
					}
					Element root = document.getRootElement();
	
					// Extracting synsets
					List<?> synsets = root.elements("synset");
					for (Object synset : synsets) {
						Element synsetElem = (Element) synset;
						String synsetID = synsetElem.attributeValue("id").substring(1);
						synsetMappings.put(Integer.parseInt(synsetID), fileName);
						
						// Extracting LUs
						List<?> lus = synsetElem.elements("lexUnit");
						for (Object lu : lus) {
							Element luElem = (Element) lu;
							String luID = luElem.attributeValue("id").substring(1);
							luMappings.put(Integer.parseInt(luID), fileName);
						}
					}
				}
			}
			logger.log(Level.INFO, "Initializing SemanticClassLabelExtractor done");
		}
	}
}
