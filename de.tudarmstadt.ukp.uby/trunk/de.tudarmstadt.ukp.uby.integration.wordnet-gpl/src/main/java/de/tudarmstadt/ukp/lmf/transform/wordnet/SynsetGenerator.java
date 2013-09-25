/**
 * Copyright 2012
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import de.tudarmstadt.ukp.lmf.model.core.Definition;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.wordnet.util.WNConvUtil;

/**
 * Instance of this class offers methods for creating {@link Synset} instances out of WordNet's data
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class SynsetGenerator {

	private final Dictionary wordnet; // WordNet Dictionary

	private int lmfSynsetNumber = 0; // running number used for creating IDs of Synsets

	// Mappings between Uby-LMF synsets and WordNet's synsets
	private final Map<Synset, net.sf.extjwnl.data.Synset>
		lmfSynsetWNSynsetMappings = new HashMap<Synset, net.sf.extjwnl.data.Synset>();

	// Mappings betweenWordNet's synsets and Uby-LMF synsets
	private final Map<net.sf.extjwnl.data.Synset, Synset>
		wnSynsetLMFSynsetMappings = new HashMap<net.sf.extjwnl.data.Synset, Synset>();

	// Mappings between lexemes and associated example sentences (extracted from WordNet's glosses)
	private final Map<Word, List<String>> examples = new HashMap<Word, List<String>>();

	private boolean initialized = false;

	private final File lexemeMappingXML;

	private Document lexemeMapping;

	// set of synsets with manually mapped example sentences
	private final Set<net.sf.extjwnl.data.Synset> manuallyMapped = new HashSet<net.sf.extjwnl.data.Synset>();

	private final Logger logger = Logger.getLogger(WNConverter.class.getName());

	/**
	 * This method constructs a {@link SynsetGenerator} based on the consumed parameters
	 * @param wordnet initialized {@link Dictionary}-instance, used for accessing information encoded in WordNet's files
	 * @param lexemeMappingFile the file containing manually entered mappings of example senteneces to lexemes
	 * @return SynsetGenerator
	 */
	@SuppressWarnings("unchecked")
	public SynsetGenerator(Dictionary wordnet, File lexemeMappingFile) {
		this.wordnet = wordnet;
		this.lexemeMappingXML = lexemeMappingFile;
		boolean readFile = true;
		SAXReader reader = new SAXReader();
		try {
			lexemeMapping = reader.read(lexemeMappingXML);
		} catch (DocumentException e1) {
			StringBuffer sb = new StringBuffer(256);
			sb.append("File contianing manually entered example sentence mappings does not exist or corrupt. ");
			sb.append("New one will be created. ");
			sb.append("This will reduce performance of the SynsetGenerator. ");
			logger.log(Level.WARNING, sb.toString());
			lexemeMappingFile.delete();
			readFile = false;
			try {
				lexemeMappingFile.createNewFile();
			} catch (IOException e) {
				logger.severe("Error on creating new file!");
			}
			lexemeMapping = DocumentHelper.createDocument();
			lexemeMapping.addElement("ExampleSentenceLexemeMapping");
		}

		if(readFile){
			/**
			 * Parsing the lexemeMapping-file
			 */
			logger.log(Level.INFO, "parsing lexeme mappings file...");
			Element root = lexemeMapping.getRootElement();
			List<Element> synsets = root.elements("Synset");
			for(Element eSynset : synsets){
				net.sf.extjwnl.data.Synset synset = null;
				try {
					synset = wordnet.getSynsetAt(POS.getPOSForLabel(eSynset.attributeValue("pos")), Long.parseLong(eSynset.attributeValue("offset")));
				} catch (Exception e) {
					StringBuffer sb = new StringBuffer(256);
					sb.append("Error on retriving WordNet's synset").append('\n');
					sb.append("printing stack trace and closing vm!");
					logger.log(Level.SEVERE, sb.toString());
					e.printStackTrace();
					System.exit(1);
				}

				manuallyMapped.add(synset);
				if(eSynset.attributeValue("approved").equalsIgnoreCase("yes")){
					/*
					 * iterate over all example sentences and record the correspondences
					 */
					List<Element> eExampleSentences = eSynset.elements("ExampleSentence");
					for(Element eExampleSentence : eExampleSentences){
						String exampleSentence = eExampleSentence.attributeValue("text");
						Word lexeme = synset.getWords().get(Integer.parseInt(eExampleSentence.attributeValue("index")));
						List<String> temp = examples.get(lexeme);
						if(temp == null) {
                            temp = new ArrayList<String>();
                        }
						temp.add(exampleSentence);
						examples.put(lexeme, temp);
					}
				}
			}
			logger.log(Level.INFO, "done parsing");
			}
	}



	/**
	 * This method initializes the {@link SynsetGenerator}.
	 */
	public void initialize(){
		if(!initialized){
		// extract all WordNet's synsets and create the associated Uby-LMF synset for each of them
		for(POS pos : POS.getAllPOS()){
			Iterator<net.sf.extjwnl.data.Synset> synIter = null;
			try {
				synIter = wordnet.getSynsetIterator(pos);
			} catch (JWNLException e) {
				e.printStackTrace();
			}
			logger.log(Level.INFO, "processing " + pos.getLabel());
			while(synIter.hasNext()){
				net.sf.extjwnl.data.Synset wnSynset = synIter.next();
				Synset lmfSynset = new Synset();
				lmfSynset.setId(getNewID());
				lmfSynsetWNSynsetMappings.put(lmfSynset, wnSynset);
				wnSynsetLMFSynsetMappings.put(wnSynset, lmfSynset);

				List<Definition> definitions = new LinkedList<Definition>();

				// Generating Definition(s) of the Synset
				for(String definitionString : getDefinitions(wnSynset)){
					Definition definition = new Definition();
					TextRepresentation textRepresentation = new TextRepresentation();
					textRepresentation.setLanguageIdentifier(ELanguageIdentifier.ENGLISH);
					textRepresentation.setWrittenText(definitionString);
					List<TextRepresentation> textRepresentations = new LinkedList<TextRepresentation>();
					textRepresentations.add(textRepresentation);
					definition.setTextRepresentations(textRepresentations);
					definitions.add(definition);
				}

				lmfSynset.setDefinitions(definitions);

				// *** Creating MonolingualExternalRef ***//
				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				// Generating MonolingualExternalRef ID
				monolingualExternalRef.setExternalSystem("WordNet 3.0 part of speech and synset offset");
				StringBuffer sb = new StringBuffer(16);
				sb.append(wnSynset.getPOS());
				sb.append(" ");
				sb.append(wnSynset.getOffset());
				monolingualExternalRef.setExternalReference(sb.toString());
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				lmfSynset.setMonolingualExternalRefs(monolingualExternalRefs);
			}
			}

		/*
		 * Rewriting the xml file containain manually entered mappings of example sentences
		 */
		logger.log(Level.INFO, "rewriting lexeme mapping file...");
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter output;
		try {

			output = new XMLWriter(new FileWriter(lexemeMappingXML), format);
			output.write(lexemeMapping);
			output.close();
		} catch (IOException e) {
			StringBuffer sb = new StringBuffer(256);
			sb.append("Error on rewriting lexeme mapping file ").append(lexemeMappingXML).append('\n');
			sb.append("printing stack trace");
			logger.log(Level.WARNING, sb.toString());
			e.printStackTrace();
		}
		logger.log(Level.INFO, "done");
		initialized = true;
		}
	}

	/**
	 * Returns the sorted list of all synsets generated by this generator
	 * @return a sorted list of all synsets generated by this generator
	 * @see Synset
	 */
	public List<Synset> getSynsets(){
		List<Synset> result = new LinkedList<Synset>();
		result.addAll(lmfSynsetWNSynsetMappings.keySet());
		Collections.sort(result);
		return result;
	}



	/**
	 * Generates a Synset-ID.<br>
	 * The running number used for the creation of the id is incremented each time thie method is called.
	 * @return an ID of a {@link Synset}
	 */
	private String getNewID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("WN_Synset_").append(lmfSynsetNumber);
		lmfSynsetNumber++;
		return sb.toString();
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
	 * with Uby-LMF synsets as keys.
	 * @return synset mappings created by this generator
	 * @see Synset
	 * @see net.sf.extjwnl.data.Synset
	 */
	Map<Synset, net.sf.extjwnl.data.Synset> getLMFSynsetWNSynsetMappings() {
		return lmfSynsetWNSynsetMappings;
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
	 * This method consumes WordNet's synset and returns a list of all definitions,
	 * contained in synset's gloss.<br>
	 * As a side-effect, the method also extracts the example sentences out of the gloss,
	 * and mapps them to associated lexemes in the synset in {@link SynsetGenerator#examples}. <br>
	 * If the algorithm, used in the method, does not succesfully map the found example sentences to lexemes of the synset,
	 * an entry is created in the {@link SynsetGenerator#lexemeMapping}-document,<br>
	 * used for entering manual mappings.
	 * @param synset WordNet's synset
	 * @return a {@link List} of definitions contained in synset's gloss
	 * @see net.sf.extjwnl.data.Synset
	 * @see Word
	 */
	private List<String> getDefinitions(net.sf.extjwnl.data.Synset synset){
		String gloss = synset.getGloss();
		List<String> result = new ArrayList<String>();
		String[] parts = gloss.split(";");
		List<Word> lexemes = synset.getWords();
		Map<Word, String> lemmaCleaned = null;
		List<String> foundExamples = new LinkedList<String>(); // found example sentences in this synset

		// <Lexeme> <segments>, <lemmatized segments>
		HashMap<Word, List<List<String>>> lemmaForms = null;

		boolean needsManualEntering = false;

		for(String part : parts) {
            if(!part.contains("\"")) {
                // definitions do not contain "
				result.add(part.trim());
            }
            else
				/*
				 * Finding correspondences
				 */
				if(!manuallyMapped.contains(synset)){
					// example sentences contain "
					String example = part.replaceAll("\"", "").trim();
					foundExamples.add(example);

					if(needsManualEntering)
                     {
                        continue; // no need to do further disambiguation if the synset needs manual mapping
                    }

					if(lexemes.size() == 1){
						// synset has only one lexeme
						Word theLexeme = lexemes.get(0);
						List<String> temp = examples.get(theLexeme);
						if(temp == null) {
                            temp = new ArrayList<String>();
                        }
						temp.add(example);

						examples.put(theLexeme, temp);
					}
					else{
						// synset has more than one lexeme - need to find the one that corresponds to the example

						// initialize lemmaForms
						if(lemmaForms == null){
							lemmaForms = new HashMap<Word, List<List<String>>>();
							for(Word lexeme : lexemes){
								List<List<String>> aList= new ArrayList<List<String>>(2);
								aList.add(0, null);
								aList.add(1, null);
								lemmaForms.put(lexeme, aList);
							}
						}

						List<Word> correspondingLexemes = new ArrayList<Word>();

						// PHASE 1 working with not-lemmatized tokens
						List<String> tokenizedExample = WNConvUtil.tokens(example);
						for(Word lexeme : lexemes){
							// iterate over every lexeme and check if the example corresponds to it
							List<String> lexemeTokens = lemmaForms.get(lexeme).get(0);

							if(lexemeTokens == null){
								// tokenize every lexeme
								if(lemmaCleaned == null) {
                                    lemmaCleaned = new HashMap<Word, String>();
                                }

								String lemma = lemmaCleaned.get(lexeme);
								if(lemma == null){
									lemma = lexeme.getLemma();
									if(lexeme.getPOS().equals(POS.ADJECTIVE) && lemma.contains("("))
                                     {
                                        lemma = lemma.substring(0, lemma.indexOf("(")); // remove the syntactic marker
                                    }
									lemma = lemma.replaceAll("_", " ").trim();
									lemmaCleaned.put(lexeme, lemma);
								}
									lexemeTokens = WNConvUtil.tokens(lemma);
									lemmaForms.get(lexeme).set(0, lexemeTokens);
							}

							if(tokenizedExample.containsAll(lexemeTokens)) {
                                correspondingLexemes.add(lexeme);
                            }
						}

						// PHASE 2 working with lemmatized tokens if no corresponding lexemes found
						List<String> lemmatizedExample = WNConvUtil.lemmatize(example);
						if(correspondingLexemes.isEmpty()) {
                            for(Word lexeme : lexemes){
								List<String> lemmatizedTokens = lemmaForms.get(lexeme).get(1);
								if(lemmatizedTokens == null){
									lemmatizedTokens = WNConvUtil.lemmatize(lemmaCleaned.get(lexeme));
									lemmaForms.get(lexeme).set(1, lemmatizedTokens);
								}
								if(lemmatizedExample.containsAll(lemmatizedTokens)) {
                                    correspondingLexemes.add(lexeme);
                                }
							}
                        }

						// EVALUATE THE FINDINGS START

						Word correspondingLexeme = null; // the corresponding lexeme of example


						if(correspondingLexemes.size() == 1) {
                            correspondingLexeme = correspondingLexemes.get(0);
                        }
                        else
							if(correspondingLexemes.size() > 1){
								// choose the right lexeme
								// the lexeme with the highest number of tokens is the right one
								boolean sameLength = false;
								Word longestLexeme = null;
								int longestLexemeNTokens = -1;
								for(Word lexeme : correspondingLexemes){
									int temp = lemmaForms.get(lexeme).get(0).size();
									if(temp > longestLexemeNTokens){
										longestLexeme = lexeme;
										longestLexemeNTokens = temp;
										sameLength = false;
									}
									else if(temp == longestLexemeNTokens) {
                                        sameLength = true;
                                    }
								}
								if(!sameLength && longestLexeme != null) {
                                    correspondingLexeme = longestLexeme;
                                }
							}


						if(correspondingLexeme == null) {
                            needsManualEntering = true;
                        }
                        else{
							List<String> previous = examples.get(correspondingLexeme);
							if(previous == null) {
                                previous = new ArrayList<String>();
                            }
							previous.add(example);
							// add the record
							examples.put(correspondingLexeme, previous);
							}
						}
					}
        }

		if(needsManualEntering) {
            /*
			 * if unmapped examples occur
			 * add the synset to the mapping file
			 * for manually entering the correspondences
			 */
			addToMappingFile(synset, foundExamples);
        }

		return result;

	}

	/**
	 * This method consumes a WordNet's synset and creates the appropriate record
	 * in the {@link SynsetGenerator#lexemeMappingXML}-document,<br>
	 * for manual entering the correspondences between example sentences and lexemes of the synset.
	 * @param synset WordNet's synset for which lexemes the example sentences need manual entering
	 * @param examples list of example sentences in the synset's gloss
	 * @see net.sf.extjwnl.data.Synset
	 */
	private void addToMappingFile(net.sf.extjwnl.data.Synset synset, List<String> examples) {
		Element root = lexemeMapping.getRootElement();
		Element eSynset = root.addElement("Synset");
		eSynset.addAttribute("approved", "no");
		eSynset.addAttribute("pos", synset.getPOS().getLabel());
		eSynset.addAttribute("offset", Long.toString(synset.getOffset()));
		List<Word> lexemes = synset.getWords();
		for(int i=0; i<lexemes.size(); i++){
			Element eLexeme = eSynset.addElement("Lexeme");
			eLexeme.addAttribute("index", Integer.toString(i));
			eLexeme.addAttribute("lemma", lexemes.get(i).getLemma());
		}

		for(String example : examples){
			Element eExampleSentence = eSynset.addElement("ExampleSentence");
			eExampleSentence.addAttribute("index", "NULL");
			eExampleSentence.addAttribute("text", example);
		}
		StringBuffer sb = new StringBuffer(128);
		sb.append("Synset needs manual entering of the example-sentence mappings: ").append(synset).append('\n');
		sb.append("The synset will be logged for manual entering of the correspondences");
		logger.log(Level.INFO,sb.toString());
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
		return examples.get(lexeme);
	}

	/**
	 * This method returns the WordNet-{@link Dictionary} used by this generator.
	 * @return WordNet's-Dictionary instance used by this generator
	 */
	public Dictionary getWordnet() {
		return wordnet;
	}

}
