/*******************************************************************************
 * Copyright 2012
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
package de.tudarmstadt.ukp.lmf.transform.wiktionary.labels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.wiktionary.api.WikiString;

/**
 * Loads Wiktionary Labels of different types from files,
 * extracts this labels from Wiktionary glosses 
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 * @author Zijad Maksuti
 */
public class WiktionaryLabelLoader {
 
	// Map from label aliases, as they occur in the Wiktionary mark-up to WiktionaryLabel objects
	private HashMap<String, WiktionaryLabel> labelAliases; 
	
	/**
	 * WiktionaryLabelLoader
	 */
	public WiktionaryLabelLoader(){
		this.labelAliases = new HashMap<String,WiktionaryLabel>();
	}
	
	/**
	 * Add file, which lists Wiktionary labels of a specific labelType with their aliases
	 * @param labelType
	 * @param path
	 */
	public void addListFile(WiktionaryLabelType labelType, String path){
		HashMap<String, String> labels = loadContextLabels(path);
		
		for(String labelAlias : labels.keySet()){
			String labelName = labels.get(labelAlias);
			WiktionaryLabel label = new WiktionaryLabel(labelName, labelAlias, labelType);			
			labelAliases.put(labelAlias, label);
		}
	}
	
	
	/**
	 * Add a stream, which lists Wiktionary labels of a specific labelType with their aliases
	 * @param labelType
	 * @param stream
	 */
	public void addListStream(WiktionaryLabelType labelType, InputStream stream){
		HashMap<String, String> labels = loadContextLabels(stream);
		
		for(String labelAlias : labels.keySet()){
			String labelName = labels.get(labelAlias);
			WiktionaryLabel label = new WiktionaryLabel(labelName, labelAlias, labelType);			
			labelAliases.put(labelAlias, label);
		}
	}
	
	
	
	/**
	 * Loads all label types
	 */
	public void loadAllLabels(){
		ClassLoader cl = getClass().getClassLoader();
		try{
			addListStream(WiktionaryLabelType.TOPIC, cl.getResource("wkt_labels/TopicLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.FORM_OF, cl.getResource("wkt_labels/FormOfLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.GRAMMATICAL, cl.getResource("wkt_labels/GrammaticalLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.PERIOD, cl.getResource("wkt_labels/PeriodLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.QUALIFIER, cl.getResource("wkt_labels/QualifierLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.USAGE, cl.getResource("wkt_labels/UsageLabelsWKT_EN.csv").openStream());
			addListStream(WiktionaryLabelType.REGIONAL, cl.getResource("wkt_labels/RegionalLabelsWKT_EN.csv").openStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Loads Wiktionary labels from a file
	 * @param filePath
	 * @param filter
	 */
	private HashMap<String, String> loadContextLabels(String filePath) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loadContextLabels(stream);
	}
	
	/**
	 * Loads Wiktionary labels from a stream
	 * @param stream
	 * @param filter
	 * @throws Exception
	 */
	private HashMap<String, String> loadContextLabels(InputStream stream){
		
		HashMap<String, String> result = new HashMap<String, String>();		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			while((line = br.readLine())!=null)
				if(!line.startsWith("#")){// Omit comments
					String [] lineArr = line.split(";");
					String label = lineArr[0].replace("\"", "");
					result.put(label.replace("_", " "), lineArr[1].replace("\"", "").replace("_", " "));
//					System.out.println("LABEL" + lineArr[0] + " " + lineArr[1]);
			}
			br.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * Searches for Wiktionary labels in gloss and sets their parameters
	 * @param gloss
	 */
	public Set<WiktionaryLabel> getLabels(WikiString gloss){
		Set<WiktionaryLabel> result = new HashSet<WiktionaryLabel>();
		Pattern pattern = Pattern.compile("\\x7b\\x7b((.*?))\\x7d\\x7d"); // {{...}} Label pattern		
		Matcher matcher = pattern.matcher(gloss.getTextIncludingWikiMarkup());		
		while(matcher.find()){
			String matches[] = matcher.group(1).split("\\|");
			Set<WiktionaryLabel> foundLabels = new HashSet<WiktionaryLabel>();
			
			for(int i = 0; i<matches.length; i++){
				if(labelAliases.containsKey(matches[i]))
					foundLabels.add(labelAliases.get(matches[i]));	
			}
			
			for(WiktionaryLabel label : foundLabels){	// Set parameters of this Wiktionary label
				label.setUnparsedText(matcher.group(1));
				for(int i = 0; i<matches.length; i++){
					if(!label.getName().equals(matches[i])){ // The label name is not its parameter
						String[] paramValue = matches[i].split("=");
						
						// If parameter contains "=", then it can be splitted in name and value
						// Otherwise, set parameter number as its name
						if(paramValue.length == 2) 
							label.addParameter(paramValue[0], paramValue[1]);
						else label.addParameter(String.valueOf(i), matches[i]);
					}
				}				
			}
			result.addAll(foundLabels);
		}		
		return result;		
	}
}
