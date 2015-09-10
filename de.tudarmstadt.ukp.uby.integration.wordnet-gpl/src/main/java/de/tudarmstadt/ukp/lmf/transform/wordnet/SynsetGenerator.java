/**
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.lmf.transform.wordnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.Statement;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.WNConvUtil;

/**
 * Instance of this class offers methods for creating {@link Synset} instances
 * out of WordNet's data.
 */
public class SynsetGenerator {

	protected static class ExampleMapping {

		protected String senseKey;
		protected String lemma;
		protected int score;

		public ExampleMapping(final String senseKey, final String lemma) {
			this.senseKey = senseKey;
			this.lemma = lemma;
		}

		public String getSenseKey() {
			return senseKey;
		}

		public String getLemma() {
			return lemma;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public void addScore(final int increment) {
			this.score += increment;
		}

	}

	public final static String EXTERNAL_SYSTEM_SYNSET_OFFSET = "synsetOffset";

	private final Log logger = LogFactory.getLog(getClass());

	private final Dictionary wordnet; // WordNet Dictionary
	private MorphologicalProcessor morphProcessor;
	private final String resourceVersion;
	private boolean initialized = false;

	private final List<Synset> synsets = new ArrayList<Synset>();
	private int lmfSynsetNumber = 0; // running number used for creating IDs of Synsets

	// Mappings betweenWordNet's synsets and Uby-LMF synsets
	private final Map<net.sf.extjwnl.data.Synset, Synset>
		wnSynsetLMFSynsetMappings = new HashMap<net.sf.extjwnl.data.Synset, Synset>();

	// Mappings between lexemes and associated example sentences (extracted from WordNet's glosses)
	private final Map<String, List<String>> examples = new TreeMap<String, List<String>>();

	protected List<String> annotationList;
	protected int[] annotationCounter = new int[10];

	/**
	 * This method constructs a {@link SynsetGenerator} based on the consumed parameters
	 * @param wordnet initialized {@link Dictionary}-instance, used for accessing information encoded in WordNet's files
	 * @param lexemeMappingFile the file containing manually entered mappings of example senteneces to lexemes
	 * @param resourceVersion Version of the resource
	 * @return SynsetGenerator
	 */
	public SynsetGenerator(final Dictionary wordnet, final String resourceVersion) {
		this.wordnet = wordnet;
		this.resourceVersion = resourceVersion;
	}

	/** Transforms WordNet synsets to UBY synsets and stores the result in
	 *  member variables. Initialization is done only once. */
	public void initialize() throws JWNLException {
		if (initialized) {
            return;
        }

		// Create UBY-LMF synsets.
		for (POS pos : POS.getAllPOS()) {
			logger.info("processing " + pos.getLabel());

			Iterator<net.sf.extjwnl.data.Synset> synIter = wordnet.getSynsetIterator(pos);
			while (synIter.hasNext()) {
				net.sf.extjwnl.data.Synset wnSynset = synIter.next();

				// Synset.
				Synset lmfSynset = new Synset();
				lmfSynset.setId("WN_Synset_" + lmfSynsetNumber);
				lmfSynsetNumber++;
				synsets.add(lmfSynset);
				wnSynsetLMFSynsetMappings.put(wnSynset, lmfSynset);

				// Definition.
				List<String> statementTexts = new ArrayList<String>();
				String senseDefinition = processGloss(wnSynset, lmfSynset, statementTexts);
				if (senseDefinition != null && !senseDefinition.isEmpty()) {
					List<Definition> definitions = new LinkedList<Definition>();
					Definition definition = new Definition();
					definition.setTextRepresentations(
							WNConvUtil.makeTextRepresentationList(senseDefinition,
									ELanguageIdentifier.ENGLISH));
					definitions.add(definition);
					lmfSynset.setDefinitions(definitions);

					// Statement.
					if (statementTexts.size() > 0) {
						List<Statement> statements = new ArrayList<Statement>();
						for (String statementText : statementTexts) {
							Statement statement = new Statement();
							statement.setStatementType(EStatementType.usageNote);
							statement.setTextRepresentations(
									WNConvUtil.makeTextRepresentationList(statementText,
											ELanguageIdentifier.ENGLISH));
							statements.add(statement);
						}
						definition.setStatements(statements);
					}
				}

				// MonolingualExternalRef.
				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				monolingualExternalRef.setExternalSystem(resourceVersion + "_" + EXTERNAL_SYSTEM_SYNSET_OFFSET);
				monolingualExternalRef.setExternalReference(wnSynset.getPOS() + " " + wnSynset.getOffset());
//TODO: implications?				monolingualExternalRef.setExternalReference(wnSynset.getOffset() + "-" + wnSynset.getPOS().getKey());
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				lmfSynset.setMonolingualExternalRefs(monolingualExternalRefs);
			}
		}

		// Write out missing annotations.
		/** /
		if (annotationList != null) {
			for (int i = 0; i < 10; i++)
				System.out.println(i + "\t" + annotationCounter[i]);

			try {
				logger.warn("Example disambiguation missing. Check annotations.txt");
				PrintWriter writer = new PrintWriter("annotations.txt");
				for (String annotLine : annotationList)
					writer.println(annotLine);
				writer.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		/**/

		initialized = true;
	}

	protected String processGloss(final net.sf.extjwnl.data.Synset wnSynset,
			final Synset lmfSynset, final List<String> statements)
			throws JWNLException {
		// Split gloss into sense definition and sense examples.
		String gloss = wnSynset.getGloss();
		String senseDefinition = "";
		String senseExamples = null;
		boolean endsWithDelim = false;
		do {
			int idx = gloss.indexOf("\"");
			if (idx >= 0) {
				senseDefinition = senseDefinition + gloss.substring(0, idx);
				gloss = gloss.substring(idx + 1);
				senseExamples = gloss;
			}
            else {
                senseDefinition = senseDefinition + gloss + ";";
            }
			String tmp = senseDefinition.trim();
			endsWithDelim = (";:.,)".indexOf(tmp.charAt(tmp.length() - 1)) >= 0);
			if (!endsWithDelim) {
                senseDefinition = senseDefinition + "\"";
            }
		} while (!endsWithDelim);
		senseDefinition = senseDefinition.trim();
		if (!senseDefinition.isEmpty()) {
            senseDefinition = senseDefinition.substring(0, senseDefinition.length() - 1).trim();
        }

		// Separate sense examples.
		if (senseExamples != null) {
			int idx;
			do {
				idx = senseExamples.indexOf("\"");
				if (idx >= 0) {
					String senseExample = senseExamples.substring(0, idx);
					processExample(wnSynset, senseExample, statements);
					senseExamples = senseExamples.substring(idx + 1);

					idx = senseExamples.indexOf("\"");
					if (idx >= 0) {
                        senseExamples = senseExamples.substring(idx + 1);
                    }
				}
			} while (idx >= 0);
		}
		return senseDefinition;
	}

	protected String cleanText(final String text) {
		StringBuilder result = new StringBuilder();
		boolean wasWhitespace = false;
		for (char c : text.toCharArray()) {
            if (" \t\n\r.,!?:;()`'-".indexOf(c) >= 0) {
				if (!wasWhitespace) {
                    result.append(' ');
                }
				wasWhitespace = true;
			} else {
				result.append(Character.toLowerCase(c));
				wasWhitespace = false;
			}
        }
		return result.toString().trim();
	}

	protected void processExample(final net.sf.extjwnl.data.Synset wnSynset,
			final String senseExample, final List<String> statements)
			throws JWNLException {
		// Clean example and sense lemmas.
		String example = " " + cleanText(senseExample) + " ";
		List<ExampleMapping> mappings = new ArrayList<ExampleMapping>();
		for (Word word : wnSynset.getWords()) {
            mappings.add(new ExampleMapping(word.getSenseKey(), cleanText(word.getLemma())));
        }

		// Step 0: Check if there is a manual disambiguation.
//		String senseKey = manualDisambiguation.get(wnSynset.getOffset() + wnSynset.getPOS().getKey());
//		if (senseKey != null)
//			saveExampleMapping(senseExample, senseKey);

		// Step 1: Check whether the lemma is a substring.
		boolean hasExactWordMatch = false;
		boolean hasPrefixMatch = false;
		for (ExampleMapping mapping : mappings) {
			String lemma = mapping.getLemma();
			int idx = example.indexOf(" " + lemma + " ");
			if (idx >= 0) {
				// Found exact or prefix match.
				mapping.setScore(3);
				hasExactWordMatch = true;
				continue;
			}
			idx = example.indexOf(" " + lemma);
			if (idx >= 0) {
				mapping.setScore(2);
				hasPrefixMatch = true;
				continue;
			}

			// Check for prefix matches for the full list of lemma tokens.
			String regEx = lemma.replace(" ", "\\S*? ") + "\\S*?";
			if (Pattern.compile(regEx).matcher(example).find()) {
				mapping.setScore(1);
				hasPrefixMatch = true;
			}
			/*boolean hasPrefixTokenMatch = true;
			List<String> lemmaTokens = segmentTokens(lemma);
			for (String lemmaToken : lemmaTokens) {
				if (example.indexOf(" " + lemmaToken) < 0) {
					hasPrefixTokenMatch = false;
					break;
				}
			}
			if (hasPrefixTokenMatch) {
				mapping.setScore(1);
				hasPrefixMatch = true;
			}*/
		}

		if (hasExactWordMatch) {
			saveExampleMappings(senseExample, mappings, 3, true);
			annotationCounter[0]++;
			return;
		}
		annotationCounter[1]++;
		if (hasPrefixMatch) {
			saveExampleMappings(senseExample, mappings, 1, true);
			annotationCounter[2]++;
			return;
		}
		annotationCounter[3]++;

		// Step 2: Match single word lemmas with all base forms.
		Set<String> baseForms = makeBaseFormList(example);
		boolean hasBaseFormMatch = false;
		for (ExampleMapping mapping : mappings) {
			String lemma = mapping.getLemma();
			if (baseForms.contains(lemma)) {
				mapping.addScore(1);
				hasBaseFormMatch = true;
			}
		}

		if (hasBaseFormMatch) {
			saveExampleMappings(senseExample, mappings, 1, true);
			annotationCounter[4]++;
			return;
		}
		annotationCounter[5]++;

		// Step 3: Match multi-word lemmas with all base forms.
		hasBaseFormMatch = false;
		for (ExampleMapping mapping : mappings) {
			String lemma = mapping.getLemma();
			List<String> lemmaTokens = segmentTokens(lemma);
			boolean hasMultiWordBaseFormMatch = true;
			for (String lemmaToken : lemmaTokens) {
                if (!baseForms.contains(lemmaToken)) {
					hasMultiWordBaseFormMatch = false;
					break;
				}
            }
			if (hasMultiWordBaseFormMatch) {
				mapping.addScore(1);
				hasBaseFormMatch = true;
			}
		}

		if (hasBaseFormMatch) {
			saveExampleMappings(senseExample, mappings, 1, false);
			annotationCounter[6]++;
			return;
		}
		annotationCounter[7]++;

		// Step 4: Find the longest prefix matches of all lemma tokens.
		int maxScore1 = 0;
		int maxScore2 = 0;
		for (ExampleMapping mapping : mappings) {
			String lemma = mapping.getLemma();
			List<String> lemmaTokens = segmentTokens(lemma);
			for (String lemmaToken : lemmaTokens) {
				// Trim each lemma token letter by letter and check for the longest
				// prefix match in the example sentence.
				int tokenLen = lemmaToken.length();
				for (int i = 0; i < tokenLen - 2; i++) {
					String lemmaPrefix = " " + lemmaToken.substring(0, tokenLen - i);
					if (example.indexOf(lemmaPrefix) >= 0) {
						mapping.addScore(lemmaPrefix.length() - 1);
						break;
					}
				}

			}

			int score = mapping.getScore();
			if (score >= maxScore1) {
				maxScore2 = maxScore1;
				maxScore1 = score;
			} else
			if (score >= maxScore2) {
                maxScore2 = score;
            }
		}

		if (maxScore1 > 0 && maxScore2 == 0) {
			saveExampleMappings(senseExample, mappings, maxScore1, false);
			annotationCounter[8]++;
			return;
		}
		annotationCounter[9]++;

		// Step 5: This example requires manual disambiguation. Add it to the
		// 	annotation list.
		if (annotationList == null) {
            annotationList = new ArrayList<String>();
        }
		annotationList.add(wnSynset.getOffset() + wnSynset.getPOS().getKey() + "\t" + senseExample);
		for (Word word : wnSynset.getWords()) {
            annotationList.add("\t\t" + word.getSenseKey() + "\t" + word.getLemma());
        }
		annotationList.add("");

		// Step 6: If we still have no clue about the example, add it to the
		// 	statement class.
		statements.add(senseExample);
	}

	protected List<String> segmentTokens(String text) {
		List<String> result = new ArrayList<String>();
		int idx;
		String remainingString = text;
		do {
			idx = remainingString.indexOf(' ');
			String token;
			if (idx >= 0) {
				token = remainingString.substring(0, idx);
				remainingString = remainingString.substring(idx + 1);
			}
            else {
                token = remainingString;
            }
			result.add(token);
		} while (idx >= 0);
		return result;
	}

	protected Set<String> makeBaseFormList(final String example)
			throws JWNLException {
		if (morphProcessor == null) {
            morphProcessor = wordnet.getMorphologicalProcessor();
        }

		Set<String> result = new TreeSet<String>();
		int idx;
		String remainingString = example;
		do {
			idx = remainingString.indexOf(' ');
			String token;
			if (idx >= 0) {
				token = remainingString.substring(0, idx);
				remainingString = remainingString.substring(idx + 1);
			}
            else {
                token = remainingString;
            }

			// Generate base forms for all POS to avoid POS tagging errors.
			if (!token.isEmpty()) {
                result.add(token);
            }
			for (POS pos : POS.values()) {
                result.addAll(morphProcessor.lookupAllBaseForms(pos, token));
            }
		} while (idx >= 0);

		return result;
	}

	protected void saveExampleMappings(final String example,
			final List<ExampleMapping> mappings, final int minScore,
			final boolean preferLongerLemmas) {
		// Select all senses that scored at least the minimal score.
		List<ExampleMapping> selection = new ArrayList<ExampleMapping>();
		for (ExampleMapping mapping : mappings) {
            if (mapping.getScore() >= minScore) {
                selection.add(mapping);
            }
        }

		// If there are ties, prefer the longer ones.
		if (preferLongerLemmas) {
			List<ExampleMapping> temp = new ArrayList<ExampleMapping>();
			for (ExampleMapping mapping1 : selection) {
				String lemma1 = mapping1.getLemma();
				boolean select = true;
				for (ExampleMapping mapping2 : selection) {
					String lemma2 = mapping2.getLemma();
					if (lemma1.equals(lemma2)) {
                        continue;
                    }

					if (lemma2.contains(lemma1)) {
						select = false;
						break;
					}
				}
				if (select) {
                    temp.add(mapping1);
                }
			}
			selection = temp;
		}

		// Save the selected example mappings.
		for (ExampleMapping mapping : selection) {
            saveExampleMapping(example, mapping.getSenseKey());
        }
	}

	protected void saveExampleMapping(String example, String senseKey) {
		List<String> list = examples.get(senseKey);
		if (list == null) {
			list = new ArrayList<String>();
			examples.put(senseKey, list);
		}
		list.add(example);
	}

	/** Returns the list of all UBY synsets generated by this generator. */
	public List<Synset> getSynsets() {
		return synsets;
	}

	/**
	 * This method consumes a WordNet's synset, and returns it's associated Uby-LMF synset,
	 * generated by this generator.<br>
	 * This method should be called after the generator has been initialized.
	 * @param wnSynset WordNet's synset for which the generateed Uby-LMF synset should be returned
	 * @return Uby-LMF synset associated with the consumed wnSynset
	 * @see Synset
	 * @see net.sf.extjwnl.data.Synset
	 * @see SynsetGenerator#initialize()
	 */
	public Synset getLMFSynset(net.sf.extjwnl.data.Synset wnSynset){
		return wnSynsetLMFSynsetMappings.get(wnSynset);
	}

	/**
	 * This method returns all mappings between WordNet's synsets, and corresponding Uby-LMF synsets,
	 * with WordNet's synsets as keys.
	 * @return synset mappings created by this generator
	 * @see Synset
	 * @see net.sf.extjwnl.data.Synset
	 */
	Map<net.sf.extjwnl.data.Synset, Synset> getWNSynsetLMFSynsetMappings() {
		return wnSynsetLMFSynsetMappings;
	}

	/**
	 * This method consumes a WordNet's lexeme and returns a list of lexeme's example-sentences, extracted by this generator<br>
	 * from lexeme's synset.
	 * @param lexeme a WordNet's lexeme which example sentences should be returned
	 * @return lexeme's example sentences extracted by this generator
	 * @see Word
	 * @see net.sf.extjwnl.data.Synset
	 */
	public List<String> getExamples(Word lexeme){
		try {
            return examples.get(lexeme.getSenseKey());
        }
        catch (JWNLException e) {
            throw new IllegalArgumentException(e);
        }
	}

}
