/*******************************************************************************
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
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;

/**
 * This class extracts subcategorization frames of
 * <a href="URL#http://www.sfs.uni-tuebingen.de/lsd/index.shtml">GermaNet 7.0</a>
 * by parising subcategorization-mapping file.
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class SubcategorizationFrameExtractor {
	private final InputStream subcatStream; // The file containing subcatMappings
	private int subcatFrameNumber = 0;
	private int syntacticArgumentNumber = 0; // Running number for creating ID's of SyntacticArguments
	private final Map<String, SubcategorizationFrame> codeFrameMappings  = new HashMap<String, SubcategorizationFrame>();
	private final Map<String, SemanticPredicate> codePredMappings  = new HashMap<String, SemanticPredicate>();

	private final Map<String, String> codeSynSemArgMapping  = new HashMap<String, String>();
	private final Map<String, String> synSemArgSynArgMapping  = new HashMap<String, String>();
	private final Map<String, SubcategorizationFrame> synArgSubcatFrameMapping  = new HashMap<String, SubcategorizationFrame>();

	private final List<SemanticPredicate> semanticPredicates = new LinkedList<SemanticPredicate>(); // List of all SemanticPredicates
	private int semanticPredicateNumber = 0;
	private int semanticArgumentNumber = 0;

	private final List<SynSemCorrespondence> synSemCorrespondences = new LinkedList<SynSemCorrespondence>();
	private int synSemCorrespondenceNumber = 0; // Running number for creating IDs

	// Mapping between LMF-Code of arguments (syntactic + semantic roles) and SynSemCorrespondence
	private final Map<String, SynSemCorrespondence> synsemargsSynSemCorrMap  = new HashMap<String, SynSemCorrespondence>();

	private final Logger logger = Logger.getLogger(GNConverter.class.getName());

	/**
	 * Constructs a {@link SubcategorizationFrameExtractor}
	 * @param subcatStream stream of the File containing the SubcategorizationFrame-mappings
	 * @return subcategorization-frame extractor based on the consumed subcatStream
	 */
	public SubcategorizationFrameExtractor(InputStream subcatStream){
		this.subcatStream = subcatStream;
		parseSubcatMappings();
	}

	/**
	 * This method consumes a frame
	 * and returns it's corresponding instance of {@link SubcategorizationFrame} class
	 * @param frame a frame for which subcategorization frame should be returned
	 * @return subcategorization frame associated with the consumed frame
	 */
	public SubcategorizationFrame getSubcategorizationFrame(String frame){
		return codeFrameMappings.get(frame);
	}

	/**
	 * This method consumes a frame string-representation
	 * and returns it's corresponding instance of {@link SemanticPredicate} class
	 * @param frame a frame for which semantic predicate should be returned
	 * @return semantic predicate associated with the consumed frame
	 */
	public SemanticPredicate getSemanticPredicate(String frame){
		return codePredMappings.get(frame);
	}


	/**
	 * Returns a sorted list of all subcategorization frames contained in this extractor
	 * @return all subcategorization frames in this extractor
	 * @see SubcategorizationFrame
	 * @see SubcategorizationFrameExtractor
	 */
	public List<SubcategorizationFrame> getSubcategorizationFrames(){
		LinkedList<SubcategorizationFrame> result = new LinkedList<SubcategorizationFrame>();
		result.addAll(synArgSubcatFrameMapping.values());
		Collections.sort(result);
		return result;
	}

	/**
	 * This method returns all semantic predicates generated by this extractor
	 * @return a list of all semantic predicates generated by this extractor
	 * @see SemanticPredicate
	 * @see SubcategorizationFrameExtractor
	 */
	public List<SemanticPredicate> getSemanticPredicates(){
		return semanticPredicates;
	}

	/**
	 * This method returns all instances of {@link SynSemCorrespondence} class, generated by this extractor
	 * @return syntacti-semantic correspondences generated by this extractor.
	 */
	public List<SynSemCorrespondence> getSynSemCorrespondences() {
		return synSemCorrespondences;
	}

	/**
	 * This method parses the file containing the SubcategorizationFrame-mappings
	 */
	private void parseSubcatMappings(){
		logger.log(Level.INFO, "Parsing subcategorization mappings...");

		BufferedReader input;
		try {
			input = new BufferedReader(new InputStreamReader(subcatStream));

			String line;
			while ((line = input.readLine()) != null) {
                if(!line.startsWith("#")) {
                    parseLine(line);
                }
            }
		}
		catch (Exception e) {
			StringBuffer sb = new StringBuffer(128);
			sb.append("Error on reading subcategorization mapping file").append('\n');
			sb.append("Aborting all operations!");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}

		logger.log(Level.INFO, "Parsing subcategorization mappings done");

		// create SubcategorizationFrames
		Iterator<String> codeIterator = codeSynSemArgMapping.keySet().iterator();
		while (codeIterator.hasNext()) {
			String code = codeIterator.next();
			String synSemArgs = codeSynSemArgMapping.get(code);
			String synArgs = synSemArgSynArgMapping.get(synSemArgs);

			if (!synArgSubcatFrameMapping.containsKey(synArgs)) {
				SubcategorizationFrame subcategorizationFrame = new SubcategorizationFrame();
				subcategorizationFrame.setId("GN_SubcategorizationFrame_".concat(Integer.toString(subcatFrameNumber)));
				subcatFrameNumber++;
				subcategorizationFrame = parseArguments(synSemArgs,subcategorizationFrame);

				synArgSubcatFrameMapping.put(synArgs,subcategorizationFrame);
				codeFrameMappings.put(code, subcategorizationFrame);
				if (synSemArgs.contains("semanticRole")) { //only few GN-frames specify a semantic role
					SemanticPredicate semanticPredicate = new SemanticPredicate();
					semanticPredicate = parseSemanticArguments(synSemArgs,subcategorizationFrame);
					codePredMappings.put(code, semanticPredicate);
					semanticPredicates.add(semanticPredicate);
				}
			}
			else {

				SubcategorizationFrame subcategorizationFrame = synArgSubcatFrameMapping.get(synArgs);
				codeFrameMappings.put(code, subcategorizationFrame);

				if (synSemArgs.contains("semanticRole")) { //only few GN-frames specify a semantic role
					SemanticPredicate semanticPredicate = new SemanticPredicate();
					semanticPredicate = parseSemanticArguments(synSemArgs,subcategorizationFrame);
					codePredMappings.put(code, semanticPredicate);
					semanticPredicates.add(semanticPredicate);
				}
			}
		}

	}

	/**
	 * This method parses a line of SubcatMappings-file
	 * Line of SubcatMappings-file has the form: {@literal <CODE>%<Arg>:..:<Arg>}
	 * @param line line of SubcatMappings-file
	 */
	private void parseLine(String line) {
		String [] parts = line.split("%");
		codeSynSemArgMapping.put(parts[0], parts[1]);

		if (parts[1].contains("semanticRole")) {
			String synArgs = parts[1].replaceFirst(",semanticRole=[a-z]+", "");
			synSemArgSynArgMapping.put(parts[1], synArgs);

		} else {
			synSemArgSynArgMapping.put(parts[1], parts[1]);
		}
	}

	/**
	 * This method parses syntactic arguments encoded in a line of subcategorization mapping file
	 * @param synSemArgs part of the line encoding the arguments
	 * @param subcatFrame subcategorization frame to which syntactic arguments should be appended
	 * @return subcategorization frame with appended syntactic arguments
	 * @see SubcategorizationFrame
	 * @see SyntacticArgument
	 */
	private SubcategorizationFrame parseArguments(String synSemArgs, SubcategorizationFrame subcatFrame) {
		SubcategorizationFrame scFrame = subcatFrame;
		List<SyntacticArgument> synArgs = new LinkedList<SyntacticArgument>();
		String[] args = synSemArgs.split(":");
		for(String arg : args) {
			if (!arg.contains("syntacticProperty")) {
				SyntacticArgument syntacticArgument = new SyntacticArgument();
				syntacticArgument.setId("GN_SyntacticArgument_".concat(Integer.toString(syntacticArgumentNumber)));
				syntacticArgumentNumber++;
				String[] atts = arg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					if (attName.equals("grammaticalFunction")){
						// needs some extra care because of incomplete names in the mappings-file...
						String gf=splits[1];
						if(gf.endsWith("Comp")) {
							gf = gf.concat("lement");
						}
						syntacticArgument.setGrammaticalFunction(EGrammaticalFunction.valueOf(gf));
					}
					if(attName.equals("syntacticCategory")) {
						syntacticArgument.setSyntacticCategory(ESyntacticCategory.valueOf(splits[1]));
					}
					if(attName.equals("optional")) {
						syntacticArgument.setOptional(splits[1].equals("yes"));
					}
					if(attName.equals("case")) {
						syntacticArgument.setCase(ECase.valueOf(splits[1]));
					}
					if(attName.equals("determiner")) {
						syntacticArgument.setDeterminer(EDeterminer.valueOf(splits[1]));
					}
					if(attName.equals("preposition")) {
						syntacticArgument.setPreposition(splits[1]);
					}
					if(attName.equals("prepositionType")) {
						syntacticArgument.setPrepositionType(splits[1]);
					}
					if(attName.equals("number")) {
						syntacticArgument.setNumber(EGrammaticalNumber.valueOf(splits[1]));
					}
					if(attName.equals("lex")) {
						syntacticArgument.setLexeme(splits[1]);
					}
					if(attName.equals("verbForm")) {
						syntacticArgument.setVerbForm(EVerbForm.valueOf(splits[1]));
					}
					if(attName.equals("tense")) {
						syntacticArgument.setTense(ETense.valueOf(splits[1]));
					}
					if(attName.equals("complementizer")) {
						syntacticArgument.setComplementizer(EComplementizer.valueOf(splits[1]));
					}
				}
				synArgs.add(syntacticArgument);
			} else {
				String [] splits = arg.split("=");
				String sp = splits[1];
				if (sp.equals("raising")) {
					sp = sp.replaceAll("raising", "subjectRaising");
				}
				LexemeProperty lexemeProperty = new LexemeProperty();
				lexemeProperty.setSyntacticProperty(ESyntacticProperty.valueOf(sp));
				scFrame.setLexemeProperty(lexemeProperty);
			}
		}
		scFrame.setSyntacticArguments(synArgs);
		return scFrame;
	}


	/**
	 * This method consumes the part of the line of subcategorization mapping file encoding semantic arguments. <br>
	 * It parses the arguments and returns an instance of {@link SemanticPredicate} class containing the arguments
	 * @param synSemArgs part of the line encoding semantic arguments.
	 * @param subcategorizationFrame instance of {@link SubcategorizationFrame} class used for creating
	 * instances of {@link SynSemArgMap} class
	 * @return semantic predicate containing parsed semantic arguments
	 */
	private SemanticPredicate parseSemanticArguments(String synSemArgs,SubcategorizationFrame subcategorizationFrame) {
		// list of mappings between Syntactic and Semantic Arguments is to be created
		SemanticPredicate semanticPredicate = new SemanticPredicate();
		semanticPredicate.setId("GN_SemanticPredicate_".concat(Integer.toString(semanticPredicateNumber)));
		semanticPredicateNumber++;
		List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
		List<SynSemArgMap> synSemArgMaps = new LinkedList<SynSemArgMap>();
		SynSemArgMap synSemArgMap = new SynSemArgMap();

		String[] args = synSemArgs.split(":");
		int index = 0;
		// iterate over syntactic Arguments
		for (SyntacticArgument synArg: subcategorizationFrame.getSyntacticArguments()) {
			String synsemArg = args[index];
			if (synsemArg.contains("syntacticProperty")) {
				index++;
				synsemArg = args[index];
			}
			// look at synsemArg: is semantic role defined? if yes: create corresponding semanticArg
			String[] atts = synsemArg.split(",");
			for(String att : atts){
				String [] splits = att.split("=");
				String attName = splits[0];
				if(attName.equals("semanticRole")){
					SemanticArgument semanticArgument = new SemanticArgument();
					semanticArgument.setId("GN_SemanticArgument_".concat(Integer.toString(semanticArgumentNumber)));
					semanticArgumentNumber++;
					semanticArgument.setSemanticRole(splits[1]);
					semanticArguments.add(semanticArgument);
					// Generate SynSemArgMapping
					synSemArgMap.setSyntacticArgument(synArg);
					synSemArgMap.setSemanticArgument(semanticArgument);
					synSemArgMaps.add(synSemArgMap);
				}
			}
			index++;
		}
		semanticPredicate.setSemanticArguments(semanticArguments);

		SynSemCorrespondence synSemCorrespondence = new SynSemCorrespondence();
		synSemCorrespondence.setId("GN_SynSemCorrespondence_".concat(Integer.toString(synSemCorrespondenceNumber)));
		synSemCorrespondenceNumber++;
		synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
		synSemCorrespondences.add(synSemCorrespondence);
		synsemargsSynSemCorrMap.put(synSemArgs,synSemCorrespondence);
		return semanticPredicate;
	}

}
