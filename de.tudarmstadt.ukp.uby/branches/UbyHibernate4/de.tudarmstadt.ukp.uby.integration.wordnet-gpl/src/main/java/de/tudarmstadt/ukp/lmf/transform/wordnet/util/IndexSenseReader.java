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
package de.tudarmstadt.ukp.lmf.transform.wordnet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tudarmstadt.ukp.lmf.transform.wordnet.WNConverter;

/**
 * Intstance of this class parses index.sense file of
 * <a href="URL#http://wordnet.princeton.edu/">WordNet 3.0</a>
 * @author Zijad Maksuti
 *
 */
public class IndexSenseReader {
	
	private File indexSense; // index.sense - file
	
	private Map<String, String> senseKeySenseNumberMpg; // mappings between sense keys and sense numbers
	
	private Logger logger = Logger.getLogger(WNConverter.class.getName());
	
	/**
	 * Initializes this instance of {@link IndexSenseReader} by parsing the index.sense file
	 */
	public void initialize(){
		
		String UBY_HOME = System.getenv("UBY_HOME");
		
		indexSense = new File(UBY_HOME+"/WordNet/wordnet3/dict/index.sense");
		
		senseKeySenseNumberMpg = new HashMap<String, String>();
		
		read();
	}
	
	/**
	 * This method parses index.sense file
	 */
	private void read() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(indexSense));
		} catch (FileNotFoundException e) {
			StringBuffer sb = new StringBuffer(256);
			sb.append("Could not open index.sense file at ");
			sb.append(indexSense).append('\n');
			sb.append("closing vm");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}
		
		String line = null;
		 try {
			while ((line = br.readLine()) != null){
				 String[] parts = line.split(" ");
				 senseKeySenseNumberMpg.put(parts[0], parts[2]);
			 }
		} catch (IOException e) {
			StringBuffer sb = new StringBuffer(256);
			sb.append("Error while reading a line of index.sense file located at ");
			sb.append(indexSense).append('\n');
			sb.append("closing vm");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}
	}
	
	/**
	 * Consumes a sense key of a lexeme and returns it's sense number
	 * @param senseKey the sense key of a lexeme, for which sense number should be returned
	 * @return sense number of the lexeme associated to the consumed senseKey or null, <br> if index.sense file parsed by this instance of {@link IndexSenseReader} contains no entry for the consumed senseKey
	 */
	public String getSenseNumber(String senseKey){
		return senseKeySenseNumberMpg.get(senseKey);
	}
	
}
