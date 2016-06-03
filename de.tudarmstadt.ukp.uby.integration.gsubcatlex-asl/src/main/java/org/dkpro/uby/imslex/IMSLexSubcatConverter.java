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
package org.dkpro.uby.imslex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EComplementizer;
import de.tudarmstadt.ukp.lmf.model.enums.EDeterminer;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalFunction;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelNameSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticCategory;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.ETense;
import de.tudarmstadt.ukp.lmf.model.enums.EVerbForm;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;

/**
 * This class converts IMSLex - Subcategorization Frames (SCF)
 * (see PhD thesis by Judith Eckle-Kohler, 1999) to UBY-LMF.
 * This lexical resource provides SCFs of verbs, nouns and adjectives
 * as well as some additional syntactic and semantic properties of
 * nouns and adjectives.
 */
public class IMSLexSubcatConverter {

	protected static class IMSLexEntry {

		protected String lemma;
		protected EPartOfSpeech pos;
		protected List<IMSLexSubcatFrame> subcatFrames;

		public IMSLexEntry(final String lemma, final EPartOfSpeech pos) {
			this.lemma = lemma;
			this.pos = pos;
			subcatFrames = new ArrayList<IMSLexSubcatFrame>();
		}

		public String getLemma() {
			return lemma;
		}

		public EPartOfSpeech getPos() {
			return pos;
		}

		public void addSubcatFrame(final IMSLexSubcatFrame subcatFrame) {
			subcatFrames.add(subcatFrame);
		}

		public List<IMSLexSubcatFrame> getSubcatFrames() {
			return subcatFrames;
		}

	}

	protected static class IMSLexSubcatFrame {

		protected String subcatLabel;
		protected String auxiliary;
		protected String argumentStr;
		protected String semanticLabel;

		public IMSLexSubcatFrame(final String subcatLabel,
				final String semanticLabel, final String auxiliary,
				final String argumentStr) {
			this.subcatLabel = subcatLabel;
			this.auxiliary = auxiliary;
			this.argumentStr = argumentStr;
			this.semanticLabel = semanticLabel;
		}

		public String getSubcatLabel() {
			return subcatLabel;
		}

		public String getAuxiliary() {
			return auxiliary;
		}

		public String getArgumentStr() {
			return argumentStr;
		}

		public String getSemanticLabel() {
			return semanticLabel;
		}

		public String getSyntaxStr() {
			return argumentStr.replaceAll(",role=[a-z]+", "");
		}

	}


	public static final String EXTREF_LEMMA = "lemma";

	protected Map<String, IMSLexEntry> lexicalEntryIndex;
	protected Map<String, IMSLexSubcatFrame> subcatIndex;
	protected String resourceName;
	protected String resourceVersion;
	protected String dtdVersion;
	protected IMSLexSubcatMap subcatMap;

	protected int syntacticArgumentId = 0;

	/** Reads the subcategorization frame files of IMSLexSubcat.
	 *  Use {@link #toLMF()} to begin the actual conversion process.
	 *  @param lexiconDir the directory path containing the IMSLexSubcat files.
	 *  @param resourceName the resource name (e.g., "IMSLexSubcat")
	 *  @param resourceVersion the resource name (e.g., "IMSLex_2012-06-17_deu")
	 *  @param dtdVersion name of UBY's DTD file
	 *  @throws IOException */
	public IMSLexSubcatConverter(final File lexiconDir,
			final String resourceName, final String resourceVersion,
			final String dtdVersion) throws IOException {
		this.resourceName = resourceName;
		this.resourceVersion = resourceVersion;
		this.dtdVersion = dtdVersion;
		loadIMSLexSubcatFiles(lexiconDir);
	}

	protected void loadIMSLexSubcatFiles(final File lexiconDir) throws IOException {
		subcatMap = new IMSLexSubcatMap();
		lexicalEntryIndex = new TreeMap<String, IMSLexEntry>();
		subcatIndex = new TreeMap<String, IMSLexSubcatFrame>();
		loadIMSLexSubcatFile(new File(lexiconDir, "Subcat_V.txt"), EPartOfSpeech.verbMain, false);
		loadIMSLexSubcatFile(new File(lexiconDir, "Subcat_PartV.txt"), EPartOfSpeech.verbMain,true);
		loadIMSLexSubcatFile(new File(lexiconDir, "Subcat_ADJ.txt"), EPartOfSpeech.adjective, false);
		loadIMSLexSubcatFile(new File(lexiconDir, "Subcat_NN.txt"), EPartOfSpeech.nounCommon, false);
		loadIMSLexSubcatFile(new File(lexiconDir, "Subcat_ADV.txt"), EPartOfSpeech.adverb, false);
	}

	protected void loadIMSLexSubcatFile(final File lexiconFile,
			EPartOfSpeech pos, boolean particleVerbs) throws IOException {
		Map<String, String> semanticMappings = new TreeMap<String, String>();
		semanticMappings.put("n-type(measure)", ELabelNameSemantics.SEMANTIC_NOUN_CLASS_MEASURE_NOUN);
		semanticMappings.put("n-type(mass)", ELabelNameSemantics.SEMANTIC_NOUN_CLASS_MASS_NOUN);
		// Nouns with the following specifications have not been considered (very noisy):
		semanticMappings.put("ntype(app-buchst-zahl)", "");
		semanticMappings.put("ntype(name)", "");
		semanticMappings.put("ntype(app)", "");
		semanticMappings.put("ntype(beruf)", "");
		semanticMappings.put("ntype(name-det)", "");

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(lexiconFile), "UTF-8"));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String lemma;
				String auxiliary;
				String subcatLabel;
				if (particleVerbs) {
					int idx = line.indexOf(" ");
					if (idx < 0 && !line.isEmpty())
						System.err.println("Skipping line " + line);
					if (idx < 0)
						continue;

					lemma = line.substring(0, idx);
					line = line.substring(idx + 1);
					idx = line.indexOf(" ");
					if (idx < 0 && !line.isEmpty())
						System.err.println("Skipping line " + line);
					if (idx < 0)
						continue;

					auxiliary = line.substring(0, idx);
					subcatLabel = line.substring(idx + 1);
				} else {
					int idx = line.indexOf("\t");
					if (idx < 0 && !line.isEmpty())
						System.err.println("Skipping line " + line);
					if (idx < 0)
						continue;

					lemma = line.substring(0, idx);
					auxiliary = null;
					subcatLabel = line.substring(idx + 1);
				}
				subcatLabel = subcatLabel.trim();

				if (subcatLabel.isEmpty())
					continue;
				if (subcatLabel.startsWith("(") && subcatLabel.endsWith(")"))
					subcatLabel = subcatLabel.substring(1, subcatLabel.length() - 1);

				String semanticLabel = null;
				for (Entry<String, String> labelMapping : semanticMappings.entrySet()) {
					if (!subcatLabel.contains(labelMapping.getKey()))
						continue;

					semanticLabel = labelMapping.getValue();
					subcatLabel = subcatLabel.replace(labelMapping.getKey(), "");
				}
				if (semanticLabel != null && semanticLabel.isEmpty())
					continue;

				String argumentString = subcatMap.createArgumentString(subcatLabel);
				/*
				//TODO: Expand alternatives.
				List<String> subcatKeys = new LinkedList<String>();
				if (subcatLabel.contains("(C_wh/ob)")) {
					//subcatKeys.add(subcatLabel.replace("(C_wh/ob)", "(C_wh)"));
					subcatKeys.add(subcatLabel.replace("(C_wh/ob)", "(C_ob)"));
				} else {
					subcatKeys.add(subcatLabel);
				}

				for (String key : subcatKeys) {*/

				String key = lemma + "\t" + pos.name();
				IMSLexEntry entry = lexicalEntryIndex.get(key);
				if (entry == null) {
					entry = new IMSLexEntry(lemma, pos);
					lexicalEntryIndex.put(key, entry);
				}
				IMSLexSubcatFrame subcatFrame = new IMSLexSubcatFrame(subcatLabel,
						semanticLabel, auxiliary, argumentString);
				entry.addSubcatFrame(subcatFrame);
			}
		} finally {
			reader.close();
		}
	}


	private List<SynSemArgMap> parseArgumentStrForRole(final String argumentStr, final SubcategorizationFrame subcatFrame) {
		List<SynSemArgMap> result = new LinkedList<SynSemArgMap>();
		String[] args = argumentStr.split(":");
		int idx = 0;
		for (String arg : args) {
			if (!arg.contains("syntacticProperty")) {
				String[] atts = arg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					if (attName.equals("role")) {
						SyntacticArgument synArg = subcatFrame.getSyntacticArguments().get(idx);

						SemanticArgument semArg = new SemanticArgument();
						semArg.setSemanticRole(splits[1]);

						SynSemArgMap synSemArgMap = new SynSemArgMap();
						synSemArgMap.setSemanticArgument(semArg);
						synSemArgMap.setSyntacticArgument(synArg);
						result.add(synSemArgMap);
					}
				}
			}
			idx++;
		}
		return result;
	}

	/**
	 * This method creates (purely syntactic) subcategorization frames
	 * @param IMSlexSubcatSense a IMSlexSubcat sense
	 * @param subcatFrame a subcategorization frame
	 * @return the subcategorization frame
	 */
	private SubcategorizationFrame parseArgumentStr(final String syntaxtStr) {
		SubcategorizationFrame scFrame = new SubcategorizationFrame();
		List<SyntacticArgument> synArgs = new LinkedList<SyntacticArgument>();
		String[] args = syntaxtStr.split(":");
		for(String arg : args) {
			if (!arg.contains("syntacticProperty")) {
				SyntacticArgument syntacticArgument = new SyntacticArgument();
				syntacticArgument.setId("IMSLexSubcat_SyntacticArgument_" + syntacticArgumentId);
				syntacticArgumentId++;
				String[] atts = arg.split(",");
				for(String att : atts){
					String [] splits = att.split("=");
					String attName = splits[0];
					if (attName.equals("grammaticalFunction")){
						String gf = splits[1];
						if (gf.equals("object")) {
							gf = gf.replaceAll("object", "directObject");
						}
						syntacticArgument.setGrammaticalFunction(EGrammaticalFunction.valueOf(gf));
					}
					if(attName.equals("syntacticCategory")) {
						syntacticArgument.setSyntacticCategory(ESyntacticCategory.valueOf(splits[1]));
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
					if(attName.equals("lexeme")) {
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
	 * Converts a preprocessed version of IMSLex to Uby-LMF
	 * name of the LMF Lexicon instance: "IMSLexSubcat"
	 * @throws IOException
	 */
	public LexicalResource toLMF() throws IOException {
		// LexicalResource.
		LexicalResource lexicalResource = new LexicalResource();
		lexicalResource.setName(resourceName);
		lexicalResource.setDtdVersion(dtdVersion);

		// GlobalInformation.
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("IMSLex, see PhD thesis of Eckle-Kohler (1999), Version of 06/2012");
		lexicalResource.setGlobalInformation(globalInformation);

		// Lexicon.
		LinkedList<Lexicon> lexicons = new LinkedList<Lexicon>();
		lexicons.add(createLexicon());
		lexicalResource.setLexicons(lexicons);
		return lexicalResource;
	}

	protected Lexicon createLexicon() {
		// Lexicon.
		Lexicon lexicon = new Lexicon();
		lexicon.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
		lexicon.setId("IMSLexSubcat_Lexicon_0");
		lexicon.setName("IMSLexSubcat");

		// Sort.
		List<IMSLexEntry> entries = new ArrayList<IMSLexEntry>(lexicalEntryIndex.values());
		Collections.sort(entries, new Comparator<IMSLexEntry>() {
			public int compare(final IMSLexEntry o1, final IMSLexEntry o2) {
				String key1 = o1.getLemma() + "\t" + o1.getPos().name();
				String key2 = o2.getLemma() + "\t" + o2.getPos().name();
				if (o1.getPos() == EPartOfSpeech.adverb)
					key1 = "\uFF00" + key1; // ensure adverbs are at the end.
				if (o2.getPos() == EPartOfSpeech.adverb)
					key2 = "\uFF00" + key2; // ensure adverbs are at the end.
				return key1.compareTo(key2);
			}
		});

		final Map<String, String> realizedSCFs = new TreeMap<String, String>();
		List<IMSLexSubcatFrame> scfs = new ArrayList<IMSLexSubcatFrame>();
		for (IMSLexEntry entry : entries) {
			String lemmaPos = entry.getLemma() + "\t" + entry.getPos().name();
			if (entry.getPos() == EPartOfSpeech.adverb)
				lemmaPos = "\uFF00" + lemmaPos; // ensure adverbs are at the end.
			for (IMSLexSubcatFrame scf : entry.getSubcatFrames()) {
				if (scf.getSemanticLabel() != null)
					continue;

				String argStr = scf.getSyntaxStr();
				if (argStr.isEmpty())
					continue;
				String scfId = realizedSCFs.get(argStr);
				if (scfId == null || lemmaPos.compareTo(scfId) < 0)
					realizedSCFs.put(argStr, lemmaPos);
				if (scfId == null)
					scfs.add(scf);
			}
		}
		Collections.sort(scfs, new Comparator<IMSLexSubcatFrame>() {
			public int compare(final IMSLexSubcatFrame o1, final IMSLexSubcatFrame o2) {
				String key1 = o1.getSyntaxStr();
				key1 = realizedSCFs.get(key1) + "\t1" + key1;
				String key2 = o2.getSyntaxStr();
				key2 = realizedSCFs.get(key2) + "\t1" + key2;
				return key1.compareTo(key2);
			}
		});

		Collections.sort(entries, new Comparator<IMSLexEntry>() {
			public String makePOSSort(final EPartOfSpeech pos) {
				switch (pos) {
					case verb:
					case verbMain:
						return "1";
					case adjective:
						return "2";
					case noun:
					case nounCommon:
					case nounProper:
						return "3";
					default:
						return "4";
				}
			}

			public int compare(final IMSLexEntry o1, final IMSLexEntry o2) {
				String key1 = makePOSSort(o1.getPos()) + o1.getLemma();
				String key2 = makePOSSort(o2.getPos()) + o2.getLemma();
				return key1.compareTo(key2);
			}
		});

		// SubcategorationFrame.
		createSubcategorizationFrames(lexicon, scfs);

		// Lexical entries.
		createLexicalEntries(lexicon, entries);

		return lexicon;
	}

	protected void createLexicalEntries(final Lexicon lexicon,
			final List<IMSLexEntry> entries) {
		List<SynSemCorrespondence> synSemCorrespondences = new ArrayList<SynSemCorrespondence>();
		List<SemanticPredicate> semanticPredicates = new LinkedList<SemanticPredicate>();
		List<LexicalEntry> lexicalEntries = new LinkedList<LexicalEntry>();
		int lexicalEntryId = 0;
		int senseId = 0;
		int syntacticBehaviourId = 0;
		int semanticArgumentId = 0;
		int synSemCorrespondenceId = 0;
		for (IMSLexEntry entry : entries) {
			// LexicalEntry.
			LexicalEntry lexicalEntry = new LexicalEntry();
			lexicalEntry.setId("IMSLexSubcat_LexicalEntry_" + lexicalEntryId);
			lexicalEntryId++;
			lexicalEntry.setPartOfSpeech(entry.getPos());

			// Lemma.
			Lemma lemma = new Lemma();
			List<FormRepresentation> formReps = new LinkedList<FormRepresentation>();
			FormRepresentation formRep = new FormRepresentation();
			String[] lemmaParts = entry.getLemma().split("#");
			if (lemmaParts.length > 1) {
				// Verb with separable prefix (marked by #).
				lexicalEntry.setSeparableParticle(lemmaParts[0]);
				formRep.setWrittenForm(lemmaParts[0] + lemmaParts[1]);
			} else {
				formRep.setWrittenForm(lemmaParts[0]);
			}
			formReps.add(formRep);
			lemma.setFormRepresentations(formReps);
			lexicalEntry.setLemma(lemma);

			// Senses (in IMSLex-Subcat defined by subcat frames).
			List<IMSLexSubcatFrame> entrySCFs = new ArrayList<IMSLexSubcatFrame>(entry.getSubcatFrames());
			Collections.sort(entrySCFs, new Comparator<IMSLexSubcatFrame>() {
				public int compare(final IMSLexSubcatFrame o1, final IMSLexSubcatFrame o2) {
					String key1 = (o1.getSemanticLabel() != null ? "9" : "0") + o1.getArgumentStr();
					String key2 = (o2.getSemanticLabel() != null ? "9" : "0") + o2.getArgumentStr();
					return key1.compareTo(key2);
				}
			});
			List<SyntacticBehaviour> syntacticBehaviours = new LinkedList<SyntacticBehaviour>();
			List <Sense> senses = new LinkedList<Sense>();
			String previousArgumentStr = null;
			for (IMSLexSubcatFrame scf : entrySCFs) {
				String argumentStr = scf.getSyntaxStr();
				if (argumentStr.equals(previousArgumentStr))
					continue; //TODO: is that correct?
				previousArgumentStr = argumentStr;

				// Sense.
				Sense sense = new Sense();
				sense.setId("IMSLexSubcat_Sense_" + senseId);
				senseId++;
				sense.setIndex(senses.size() + 1);
				senses.add(sense);

				// MonolingualExternalRef.
				MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
				monolingualExternalRef.setExternalSystem(resourceVersion + "_" + EXTREF_LEMMA);
				monolingualExternalRef.setExternalReference(entry.getLemma());
				List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
				monolingualExternalRefs.add(monolingualExternalRef);
				sense.setMonolingualExternalRefs(monolingualExternalRefs);

				// SemanticLabel.
				String semanticLabelText = scf.getSemanticLabel();
				if (semanticLabelText != null) {
					List<SemanticLabel> semanticLabels = new ArrayList<SemanticLabel>();
					SemanticLabel semanticLabel = new SemanticLabel();
					semanticLabel.setLabel(semanticLabelText);
					semanticLabel.setType(ELabelTypeSemantics.semanticNounClass);
					semanticLabels.add(semanticLabel);
					sense.setSemanticLabels(semanticLabels);
				}

				// SubcategorizationFrame.
				if (!scf.getArgumentStr().isEmpty()) {
					SubcategorizationFrame subcatFrame = subcategorizationFrameIndex.get(scf.getSyntaxStr());

					// SyntacticBehavior.
					SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
					syntacticBehaviour.setId("IMSLexSubcat_SyntacticBehaviour_" + syntacticBehaviourId);
					syntacticBehaviourId++;
					syntacticBehaviour.setSense(sense);
					syntacticBehaviour.setSubcategorizationFrame(subcatFrame);
					syntacticBehaviours.add(syntacticBehaviour);

					// SynSemArgMap.
					List<SynSemArgMap> synSemArgMaps = parseArgumentStrForRole(scf.getArgumentStr(), subcatFrame);
					if (synSemArgMaps != null && !synSemArgMaps.isEmpty()) {
						// SemanticPredicate.
						SemanticPredicate semanticPredicate = new SemanticPredicate();
						semanticPredicate.setId("IMSLexSubcat_SemanticPredicate_" + semanticPredicates.size());
						semanticPredicates.add(semanticPredicate);

						// SemanticArgument.
						List<SemanticArgument> semanticArguments = new LinkedList<SemanticArgument>();
						for (SynSemArgMap synSemArgMap : synSemArgMaps) {
							SemanticArgument semanticArgument = synSemArgMap.getSemanticArgument();
							semanticArgument.setId("IMSLexSubcat_SemanticArgument_" + semanticArgumentId);
							semanticArgumentId++;
							semanticArgument.setPredicate(semanticPredicate);
							semanticArguments.add(semanticArgument);
						}
						semanticPredicate.setSemanticArguments(semanticArguments);

						// PredicativeRepresentation.
						List<PredicativeRepresentation> predicativeRepresentations = new LinkedList<PredicativeRepresentation>();
						PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
						predicativeRepresentation.setPredicate(semanticPredicate);
						predicativeRepresentations.add(predicativeRepresentation);
						sense.setPredicativeRepresentations(predicativeRepresentations);

						// SynSemCorrespondence.
						SynSemCorrespondence synSemCorrespondence = new SynSemCorrespondence();
						synSemCorrespondence.setId("IMSLexSubcat_SynSemCorrespondence_" + synSemCorrespondenceId);
						synSemCorrespondenceId++;
						synSemCorrespondence.setSynSemArgMaps(synSemArgMaps);
						synSemCorrespondences.add(synSemCorrespondence);
					}
				}
			}
			lexicalEntry.setSenses(senses);
			if (!syntacticBehaviours.isEmpty())
				lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);
			lexicalEntries.add(lexicalEntry);
		}
		lexicon.setLexicalEntries(lexicalEntries);
		lexicon.setSemanticPredicates(semanticPredicates);
		lexicon.setSynSemCorrespondences(synSemCorrespondences);
	}


	protected Map<String, SubcategorizationFrame> subcategorizationFrameIndex = new TreeMap<String, SubcategorizationFrame>();

	protected void createSubcategorizationFrames(final Lexicon lexicon,
			final List<IMSLexSubcatFrame> scfs) {
		int subcatFrameId = 0;
		List<SubcategorizationFrame> subcategorizationFrames = new ArrayList<SubcategorizationFrame>();
		for (IMSLexSubcatFrame scf : scfs) {
			SubcategorizationFrame subcategorizationFrame = parseArgumentStr(scf.getArgumentStr());
			subcategorizationFrame.setId("IMSLexSubcat_SubcategorizationFrame_" + subcatFrameId);
			subcatFrameId++;
			subcategorizationFrame.setSubcatLabel(scf.getSubcatLabel());
			subcategorizationFrames.add(subcategorizationFrame);
			subcategorizationFrameIndex.put(scf.getSyntaxStr(), subcategorizationFrame);
		}
		Collections.sort(subcategorizationFrames, new Comparator<SubcategorizationFrame>() {
			public int compare(final SubcategorizationFrame o1, final SubcategorizationFrame o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		lexicon.setSubcategorizationFrames(subcategorizationFrames);
	}

}
