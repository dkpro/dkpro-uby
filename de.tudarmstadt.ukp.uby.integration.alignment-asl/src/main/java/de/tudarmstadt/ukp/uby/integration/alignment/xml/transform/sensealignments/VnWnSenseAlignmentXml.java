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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decision;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decisiontype;
import de.tudarmstadt.ukp.integration.alignment.xml.model.ResourceXml;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Target;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

/**
 * Convert given VerbNet to WordNet alignment file to generic alignment xml
 */
public class VnWnSenseAlignmentXml extends SenseAlignmentXml {

    private final Log logger = LogFactory.getLog(getClass());

	public VnWnSenseAlignmentXml(String alignmentFile, String outFile) {
		super(alignmentFile, outFile);
	}

	@Override
    public void toAlignmentXml(XmlMeta metadata) {
		String decisionSrc = metadata.decisiontypes.get(0).id;
		BufferedReader reader = null;
		List<Source> sources = new ArrayList<>();
		try {
			System.out.println(alignmentFile);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(alignmentFile))));
			String line = null;
			int lineNumber = 0;
			int paircount = 0;
			while ((line = reader.readLine()) != null) {

				String temp[] = line.split("#");
				String verbnetItem = temp[0]; // VerbNet_3.2_eng_sense
				String wordNetItem = temp[1];

				wordNetItem = wordNetItem.substring(4);
				wordNetItem = wordNetItem.replace(")", "").trim();

				if (wordNetItem.length() != 0) {
					// create the source object
					Source source = new Source();
					source.ref = verbnetItem;
					List<Target> targets = new ArrayList<>();

					// elements separated by whitespace
					String wordNetItems[] = wordNetItem.split(" ");

					for (String wordnet : wordNetItems) {
						if (wordnet.trim().length() > 0) {
							// format to match wordnet part of speech and sense
							// key
							wordnet = wordnet.replaceAll("\\?", "");

							String refString = "[POS: verb] ";
							String wordnetref = refString + wordnet + "::";

							Target target = new Target();
							target.ref = wordnetref;
							target.decision = new Decision();
							target.decision.confidence = DEFAULTCONFIDENCE;
							target.decision.src = decisionSrc;
							target.decision.value = true; // default
							targets.add(target);
							paircount++;
						}
					}
					source.targets = targets;
					sources.add(source);
				} else {
					logString.append("No alignment target in input for: "
							+ verbnetItem);
					logString.append(LF);
				}
				lineNumber++;
			}
			logger.info(logString.toString());
			logger.info("number of input lines:" + lineNumber);
			logger.info("number of alignment pairs:" + paircount);

			writer.writeMetaData(metadata);
			Alignments alignments = new Alignments();
			alignments.source = sources;
			writer.writeAlignments(alignments);
			writer.close();
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
    public XmlMeta getDefaultXmlMeta() {
		/*
		 * Generate Metadata
		 */
		XmlMeta metadata = new XmlMeta();
		metadata.title = "VerbNet-WordNet mapping from VerbNet version 3.2";
		metadata.creator = "Kipper et al., A large-scale Classification of English Verbs, LRE Journal, 42(1), 2008";
		metadata.date = "2015-03-12"; // download date
		metadata.description = "Manual mapping of VerbNet class members to WordNet sense keys, the mapping is part of the VerbNet";
		metadata.identifier = "VNWN32";
		metadata.publisher = "University of Colorado";
		metadata.rights = "VerbNet 3.0 (and 3.x) License";
		metadata.version = "3.2";
		ResourceXml sourceResource = new ResourceXml();
		sourceResource.description = "VerbNet version 3.2";
		sourceResource.id = "VN_Lexicon_0"; // matches UBY lexiconId
		sourceResource.language = "en";
		// identifiertype needs to match externalSystem in UBY:
		sourceResource.identifiertype = "VerbNet_3.2_eng_sense";
		metadata.sourceResource = sourceResource;
		ResourceXml targetResource = new ResourceXml();
		targetResource.description = "WordNet version 3.x";
		targetResource.id = "WN_Lexicon_0"; // matches UBY lexiconId
		targetResource.language = "en";
		// identifiertype needs to match externalSystem in UBY:
		targetResource.identifiertype = "WordNet 3.0 part of speech and sense key";
		metadata.targetResource = targetResource;
		Decisiontype type = new Decisiontype();
		type.id = "VerbNet_VNWN";
		type.name = "VerbNet 3.1 VNWN";
		type.type = Decisiontype.Decision.MANUAL;
		List<Decisiontype> decisionTypes = new ArrayList<>();
		decisionTypes.add(type);
		metadata.decisiontypes = decisionTypes;
		// no separate scores given => no scoretype information
		return metadata;
	}

	public static void main(String[] args) {
		String UBY_HOME = System.getenv("UBY_HOME");
		String alfile = UBY_HOME + "/VerbNet/verbNetWordNetAlignment3.2.srt";
		String outFile = UBY_HOME
				+ "/target/verbNetWordNetAlignment3.2_gen.xml";
		VnWnSenseAlignmentXml al = new VnWnSenseAlignmentXml(alfile, outFile);
		al.toAlignmentXml(al.getDefaultXmlMeta());
	}
}
