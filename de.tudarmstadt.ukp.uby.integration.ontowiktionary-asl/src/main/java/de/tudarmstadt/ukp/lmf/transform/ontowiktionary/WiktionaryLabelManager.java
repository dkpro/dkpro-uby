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
package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maintains a list of {@link PragmaticLabel}s divided into different 
 * groups.
 * @author Christian M. Meyer
 * @since 2.0.0
 */
public class WiktionaryLabelManager {

	/**
	 * Models a pragmatic label which describes the technical domain
	 * or language variety a Wiktionary word sense belongs to.
	 * @author Christian M. Meyer
	 * @since 2.0.0
	 */
	public static class PragmaticLabel {

		protected String label;
		protected int labelIndex;
		protected String labelType;
		protected String labelGroup;
		protected String standardizedLabel;

		/** Instanciates the label with the given parameters. */
		public PragmaticLabel(final String label, int labelIndex, 
				final String labelType, final String labelGroup,
				final String standardizedLabel) {
			this.label = label;
			this.labelIndex = labelIndex;
			this.labelType = labelType;
			this.labelGroup = labelGroup;
			this.standardizedLabel = standardizedLabel;
		}
		
		/** Returns the label text. */
		public String getLabel() {
			return label;
		}
		
		/** Returns the running index number of the label (i.e., its 
		 *  position within the sense definition). */
		public int getLabelIndex() {
			return labelIndex;
		}
		
		/** Returns the general label type (e.g., "dom" for 
		 *  "domain labels". */
		public String getLabelType() {
			return labelType;
		}
		
		/** Returns the subgroup of the label's type (e.g., "dom:misc:agra" 
		 *  for labels covering agriculture). */
		public String getLabelGroup() {
			return labelGroup;
		}
		
		public String getStandardizedLabel() {
			return standardizedLabel;
		}
		
		@Override
		public String toString() {
			return label + "(" + labelIndex + ":" + labelGroup + ")";
		}
		
	}
	
	
	protected File pragmaticLabelsFile;
	protected File wordFormLabelsFile;
	protected Map<String, String[]> labelGroups;
	protected Set<String> wordFormLabels;
	
	/** Instanciates the label manager for the given file of label mappings. */
	public WiktionaryLabelManager(final File prgamaticLabels,
			final File wordFormLabels) {
		this.pragmaticLabelsFile = prgamaticLabels;
		this.wordFormLabelsFile = wordFormLabels;
	}
	
	public WiktionaryLabelManager(final InputStream prgamaticLabels,
			final InputStream wordFormLabels) {
		try {
			loadLabelGroups(prgamaticLabels);
			loadWordForms(wordFormLabels);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** Load the pragmatic labels mapping file. */
	protected void loadLabelGroups() {
		try {
			loadLabelGroups(new FileInputStream(pragmaticLabelsFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Load the pragmatic labels mapping file. */
	protected void loadLabelGroups(final InputStream inputStream) throws IOException {
		labelGroups = new TreeMap<String, String[]>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = reader.readLine(); // Skip header.
		while ((line = reader.readLine()) != null) {
			int idx = line.indexOf('\t');
			String group1 = line.substring(0, idx);
			line = line.substring(idx + 1);

			idx = line.indexOf('\t');
			String group2 = line.substring(0, idx);
			line = line.substring(idx + 1);

			idx = line.indexOf('\t');
			String group3 = line.substring(0, idx);
			String label = line.substring(idx + 1);

			labelGroups.put(label, new String[]{group1, group2, group3});
		}
		reader.close();
	}

	/** Load the word form labels mapping file. */
	protected void loadWordForms() {
		try {
			loadWordForms(new FileInputStream(wordFormLabelsFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Load the word form labels mapping file. */
	protected void loadWordForms(final InputStream inputStream) throws IOException {
			wordFormLabels = new TreeSet<String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null)
				wordFormLabels.add(line);
			reader.close();
	}
	
	/** Checks if the given label is a word form label. */
	public boolean isWordFormLabel(final PragmaticLabel label) {
		if (wordFormLabels == null)
			loadWordForms();
		return wordFormLabels.contains(label.getLabel());
	}
	
	/** Extracts the target word form from a form_of pattern. For example, 
	 *  the target word form "bank" from the pattern {{plural_of|bank}}. */
	public String extractTargetWordForm(final String senseDefinition) {
		if (wordFormLabels == null)
			loadWordForms();
		
		Pattern pattern = Pattern.compile("\\x7b\\x7b((.*?))\\x7d\\x7d"); // {{...}} Label pattern		
		Matcher matcher = pattern.matcher(senseDefinition);		
		while (matcher.find()) {
			String matches[] = matcher.group(1).split("\\|");
			if (matches == null || matches.length < 2)
				continue;
						
			if (!wordFormLabels.contains(matches[0]))
				continue;
					
			return matches[1];
		}
		return null;		
	}
	
	/** Returns a newly constructed instance of {@link PragmaticLabel}
	 *  for the given label string and index. */
	protected PragmaticLabel makeLabel(final String label, int labelIndex) {
		if (label == null)
			return null;
		
		if (labelGroups == null)
			loadLabelGroups();
			
		String[] labelGroup = labelGroups.get(label);
		if (labelGroup != null)
			return new PragmaticLabel(label, labelIndex, 
					labelGroup[0], labelGroup[1], labelGroup[2]);
		else		
			return new PragmaticLabel(label, labelIndex,
					null, null, null);
	}

	/** Extracts pragmatic labels from the given sense definitions. Usually,
	 *  pragmatic labels are encoded using parenthesis or typographic markers
	 *  at the beginning of the sense definitions. */
	public List<PragmaticLabel> parseLabels(String senseDefinition,
			final String lemma) {
		// Identify the label segment and normalize it. 
		String rawLabels;
		int idx = -1;
		senseDefinition = senseDefinition.trim();
		do {
			String endDelim = null;
			if (senseDefinition.startsWith("{{")) {
				endDelim = "}}";
				senseDefinition = senseDefinition.substring(2);
			} else if (senseDefinition.startsWith("(")) {
				endDelim = ")";
				senseDefinition = senseDefinition.substring(1);
			} else if (senseDefinition.startsWith("''")) {
				endDelim = "''";
				senseDefinition = senseDefinition.substring(2);
			} else
				break;

			idx = senseDefinition.indexOf(endDelim);
			if (idx >= 0)
				break;			
		} while (true);

		if (idx < 0)
			return null;
		
		rawLabels = senseDefinition.substring(0, idx);
		if (rawLabels.length() == 0)
			return null;

		rawLabels = rawLabels.replace("„", "");
		rawLabels = rawLabels.replace("“", "");
		
		rawLabels = rawLabels.replace("||", "|");
		if (rawLabels.startsWith("|"))
			rawLabels = rawLabels.substring(1);
		if (rawLabels.endsWith("|"))
			rawLabels = rawLabels.substring(0, rawLabels.length() - 2);	
		if (rawLabels == null || rawLabels.length() == 0)
			return null;
	
		// Extract the individual labels.
		List<PragmaticLabel> result = new LinkedList<PragmaticLabel>();
		int labelIdx = 0;
		int startIdx = 0;
		for (int j = 0; j <= rawLabels.length(); j++) {
			if (j == rawLabels.length() || ",;|".indexOf(rawLabels.charAt(j)) >= 0) {
				if (startIdx == rawLabels.length())
					continue;

				String rawLabel = rawLabels.substring(startIdx, j);
				startIdx = j + 1;
				if (rawLabel.length() == 0)
					continue;

				while (rawLabel.length() > 0 && "'[{(: *".indexOf(rawLabel.charAt(0)) >= 0)
					rawLabel = rawLabel.substring(1);
				while (rawLabel.length() > 0 && "']}): ".indexOf(rawLabel.charAt(rawLabel.length() - 1)) >= 0)
					rawLabel = rawLabel.substring(0, rawLabel.length() - 1);

				rawLabel = rawLabel.replace("...", "");
				rawLabel = normalizeLabel(rawLabel);

				if (rawLabel == null || rawLabel.length() <= 1)
					continue;
				if (rawLabel.equals(lemma))
					continue;
				if (rawLabel.equals("label"))
					continue;
				if (rawLabel.length() > 255)
					rawLabel = rawLabel.substring(0, 255);
			
				PragmaticLabel label = makeLabel(rawLabel, labelIdx + 1);
				result.add(label);
			
				labelIdx++;
			}
		}
		return result;
	}
	

	protected static final Pattern SQUARE_BRACKETS = Pattern.compile("\\[.*?\\]");
	
	protected String normalizeLabel(final String term) {
		String result = term;
		if (result.indexOf(':') >= 0)
			return null;
		
		// Remove formatting markup.
		result = result.replace("'''", "");
		result = result.replace("''", "");
		result = result.replace("</small>", "");
		result = result.replace("<small>", "");
		result = result.replace("</sup>", "");
		result = result.replace("<sup>", "");
		result = result.replace("&nbsp;", " ");
		
		// Remove link markup.
		result = result.replace("[[", "");
		result = result.replace("]]", "");
		result = SQUARE_BRACKETS.matcher(result).replaceAll("");
				
		// Remove bracket-only words.
		result = result.trim();
		if (result.startsWith("(") && result.endsWith(")"))
			result = "";
		if (result.startsWith("{") && result.endsWith("}"))
			result = "";
		
		// Remove not-terminated brackets.
		int idx = result.indexOf("(");
		if (idx >= 0 && result.indexOf(")") < 0)
			result = result.substring(0, idx);
		idx = result.indexOf("{");
		if (idx >= 0 && result.indexOf("}") < 0)
			result = result.substring(0, idx);
		idx = result.indexOf("[");
		if (idx >= 0 && result.indexOf("]") < 0)
			result = result.substring(0, idx);
		idx = result.indexOf(")");
		if (idx >= 0 && result.indexOf("(") < 0)
			result = result.substring(idx + 1);
		idx = result.indexOf("}");
		if (idx >= 0 && result.indexOf("{") < 0)
			result = result.substring(idx + 1);
		idx = result.indexOf("]");
		if (idx >= 0 && result.indexOf("[") < 0)
			result = result.substring(idx + 1);
		result = result.replace("{", "");
		result = result.replace("}", "");
		result = result.replaceAll("[^\\u0000-\\uFFFF]", "");
		return result.trim();
	}
	
	/***/
	@Deprecated
	public String getLabel(String gloss) {
		int idx = -1;
		gloss = gloss.trim();
		do {
			String endDelim = null;
			if (gloss.startsWith("{{")) {
				endDelim = "}}";
				gloss = gloss.substring(2);
			} else if (gloss.startsWith("(")) {
				endDelim = ")";
				gloss = gloss.substring(1);
			} else if (gloss.startsWith("''")) {
				endDelim = "''";
				gloss = gloss.substring(2);
			} else
				break;

			idx = gloss.indexOf(endDelim);
			//if (idx >= 0 && idx < gloss.length() - endDelim.length())
			if (idx >= 0)
				break;			
		} while (true);

		if (idx < 0)
			return "";
		
		String label = gloss.substring(0, idx);
		if (label.length() == 0)
			return "";

		label = label.replace("„", "");
		label = label.replace("“", "");
		
		label = label.replace("||", "|");
		if (label.startsWith("|"))
			label = label.substring(1);
		if (label.endsWith("|"))
			label = label.substring(0, label.length() - 2);

		return label;
	}
	
}
