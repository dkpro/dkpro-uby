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

package de.tudarmstadt.ukp.uby.integration.alignment.xml.transform.sensealignments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decision;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decisiontype;
import de.tudarmstadt.ukp.integration.alignment.xml.model.ResourceXml;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Target;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.uby.integration.alignment.xml.transform.SenseAlignmentGenericXml;

/**
 * Convert the FrameNet-WordNet alignments to UBY format. This class takes the
 * FrameNet 1.5 and WordNet 3.0 ids from a file and integrates them to UBY
 *
 * @author Silvana Hartmann
 *
 */
public class FnWnSenseAlignmentXml extends SenseAlignmentXml {

	static String UBY_HOME = System.getenv("UBY_HOME");
	static String DKPRO_HOME = System.getenv("DKPRO_HOME");
	protected static Log logger = LogFactory
			.getLog(FnWnSenseAlignmentXml.class);
	private final Uby uby;

	ArrayList<String> notfoundWn = null;
	ArrayList<String> notfoundFn = null;
	ArrayList<String> notAddedAll;
	int inputsize = 0;

	/**
	 *
	 * @param alignmentFile
	 * @param outFile
	 * @param dbConfig
	 */
	public FnWnSenseAlignmentXml(String alignmentFile, String outFile,
			DBConfig dbConfig) {
		super(alignmentFile, outFile);
		uby = new Uby(dbConfig);
		notfoundFn = new ArrayList<String>();
		notfoundWn = new ArrayList<String>();
		notAddedAll = new ArrayList<String>();
	}

	/**
	 * Collect UBY SenseIds for the aligned senses based on synsetId and lemma
	 * for WordNet and based on lexical unit id for FrameNet
	 *
	 * @throws IOException
	 */
	@Override
    public void toAlignmentXml(XmlMeta metadata) throws IOException {
		System.err.println("to Alignment Xml");
		TreeMap<String, Source> sourceMap = new TreeMap<>();
		List<String[]> data = null;
		data = readAlignmentFile();
		int counter = 0; // input sense pairs
		int found = 0; // output sense pairs

		// iterate over alignment entries
		for (String[] d : data) {
			counter++;
			// show progress:
			if ((counter % 1000) == 0) {
				logger.info("# processed alignments: " + counter);
			}

			// use FrameNet sense externalReference (lexical unit Id)
			String fnSenseId = d[0]; // SOURCE

			Source source = null;
			if (sourceMap.containsKey(fnSenseId)) {
				source = sourceMap.get(fnSenseId);
			} else {
				source = new Source();
			}
			source.ref = fnSenseId;
			List<Target> targets = new LinkedList<Target>();
			// get WordNet sense by Synset Offset and Lemma
			List<Sense> wnSenses = uby.getSensesByWNSynsetId(d[1]);
			// List<Sense> wnSenses = uby.wordNetSenses(partOfSpeech, offset);

			for (Sense wnSense : wnSenses) {
				Target target = new Target();
				target.ref = wnSense.getId();
				Decision decision = new Decision();
				decision.confidence = SenseAlignmentGenericXml.DEFAULTCONFSCORE;
				decision.value = true;
				// decision.src = metadata.decisiontypes.get(0).name;
				target.decision = decision;
				targets.add(target);
				found++;
			}
			if (targets.size() > 0) {
				source.targets = targets;
				sourceMap.put(source.ref, source);
			}
		}

		writer.writeMetaData(metadata);
		Alignments alignments = new Alignments();
		alignments.source = new LinkedList<>();
		alignments.source.addAll(sourceMap.values());
		writer.writeAlignments(alignments);
		writer.close();
		System.err.println("Alignments in: " + counter + " OUT" + found);

		logger.info("Alignments in: " + counter + "Alignments out: " + found);
	}

	/**
	 * Read alignment file in standard format, e.g.: fn_luId, wn_synset ID,
	 * wn_lemma, fn_lemma
	 *
	 * @return
	 * @throws IOException
	 */
	private List<String[]> readAlignmentFile() {
		List<String[]> alignment = new ArrayList<String[]>();
		int lineNumber = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(alignmentFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				String[] items = line.split("\t");
				alignment.add(items);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + alignmentFile);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File could not be opended: " + alignmentFile);
			IOUtils.closeQuietly(reader);
		}
		inputsize = lineNumber;
		return alignment;
	}

	/**
	 * Write output lines to given file
	 *
	 * @param outFile
	 * @param lines
	 * @throws IOException
	 */
	protected static void writeLines(String outFile, Collection<String> lines)
			throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFile)));
			for (String line : lines) {
				writer.write(line + "\n");
			}
		} catch (IOException e) {
			System.err
					.println("Exception" + e + "could not write to" + outFile);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	@Override
	public XmlMeta getDefaultXmlMeta() {
		XmlMeta metadata = new XmlMeta();
		metadata.title = "FrameNet - WordNet sense alignment from WordFrameNet ";
		metadata.creator = "http://adimen.si.ehu.es/web/WordFrameNet";
		metadata.date = "2010-03-23"; // from download
		metadata.description = "WordFrameNet: ";
		metadata.identifier = "FNWNwfn";
		metadata.publisher = "Laparra E. and Rigau G";
		metadata.rights = "http://creativecommons.org/licenses/by/3.0/";
		metadata.version = "TODO";
		ResourceXml targetResource = new ResourceXml();
		targetResource.description = "WordNet version 3.x"; // TODO check
		// matches lexiconId
		targetResource.id = "WN_Lexicon_0";
		targetResource.language = "en";
		// matches externalSystem
		targetResource.identifiertype = SenseAlignmentGenericXml.UBY_SENSE_ID;
		metadata.targetResource = targetResource;
		ResourceXml sourceResource = new ResourceXml();
		sourceResource.description = "FrameNet version 1.x"; // TODO check
		// matches lexiconId
		sourceResource.id = "FN_Lexicon_0";
		sourceResource.language = "en";
		// matches externalSystem
		sourceResource.identifiertype = "FrameNet_1.5_eng_lexicalUnit";
		metadata.sourceResource = sourceResource;
		Decisiontype type = new Decisiontype();
		type.id = "WFN_FNWN";
		type.name = "WFN_FNWN";
		type.type = Decisiontype.Decision.AUTOMATIC;
		List<Decisiontype> decisionTypes = new ArrayList<>();
		decisionTypes.add(type);
		metadata.decisiontypes = decisionTypes;
		// no separate scores given => no scoretype information
		return metadata;
	}

	public static void main(String[] args) throws Exception {
		String UBY_HOME = System.getenv("UBY_HOME");
		String alignmentFile = UBY_HOME
				+ "/alignments/WordFrameNet_formatted.tsv";
		String outFile = UBY_HOME + "/target/wordFrameNet_newXml.xml";
		DBConfig dbConfig = new DBConfig("localhost/uby_clarin_0_7_0",
				"com.mysql.jdbc.Driver", "mysql", "root", "pass", false);
		FnWnSenseAlignmentXml al = new FnWnSenseAlignmentXml(
				alignmentFile, outFile, dbConfig);
		al.toAlignmentXml(al.getDefaultXmlMeta());
	}

}
