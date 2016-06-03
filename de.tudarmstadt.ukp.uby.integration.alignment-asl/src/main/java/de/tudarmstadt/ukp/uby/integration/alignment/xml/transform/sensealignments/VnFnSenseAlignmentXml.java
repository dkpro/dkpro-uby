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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Alignments;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decision;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Decisiontype;
import de.tudarmstadt.ukp.integration.alignment.xml.model.ResourceXml;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Target;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELabelTypeSemantics;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Convert given alignment FrameNet to VerbNet alignment file to generic
 * alignment xml Requires UBY lookup for VerbNet external reference
 */
public class VnFnSenseAlignmentXml extends SenseAlignmentXml {
    private final Log logger = LogFactory.getLog(VnFnSenseAlignmentXml.class);

	private final Uby uby;
	private final String lexiconName = "VerbNet";

	public int inputsize = 0;

	// public ArrayList<String> notAdded;

	public VnFnSenseAlignmentXml(String alignmentFile, String outFile,
			DBConfig dbConfig) throws FileNotFoundException {
		super(alignmentFile, outFile);
		// notAdded = new ArrayList<String>();
		uby = new Uby(dbConfig);
	}

	/**
	 * @param metadata
	 * @throws IOException
	 */
	@Override
    public void toAlignmentXml(XmlMeta metadata) throws IOException {

		Lexicon vn = uby.getLexiconByName(lexiconName);
		TreeMap<String, Source> sourceMap = new TreeMap<>();

		int noSource = 0;
		int lines = 0;
		int count = 0;
		ArrayList<String> output = new ArrayList<String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(alignmentFile));
			doc.getDocumentElement().normalize();
			NodeList entries = doc.getElementsByTagName("vncls");
			for (int i = 0; i < entries.getLength(); i++) {
				Node alignment = entries.item(i);
				NamedNodeMap atts = alignment.getAttributes();
				String vnClass = atts.getNamedItem("class").getTextContent();
				String vnLemma = atts.getNamedItem("vnmember").getTextContent();
				String luId = atts.getNamedItem("fnlexent").getTextContent();
				// there are mappings with empty (fn) target:
				if (luId.equals("")) {
					noSource++;
				} else {
					// add output here
					output.add(luId + "\t" + vnLemma + "\t" + vnClass + "\n");

					List<LexicalEntry> vnentries = uby.getLexicalEntries(
							vnLemma, EPartOfSpeech.verb, vn);
					if (vnentries.size() > 0) {
						for (LexicalEntry e : vnentries) {
							List<Sense> vnSenses = e.getSenses();
							for (Sense vns : vnSenses) {
								String senseId = vns.getId();
								// filter by VN-class
								List<SemanticLabel> labels = uby
										.getSemanticLabelsbySenseIdbyType(
												senseId,
												ELabelTypeSemantics.verbnetClass
														.toString());
								for (SemanticLabel l : labels) {
									String[] labelItems = l.getLabel().split(
											"-");
									StringBuffer parsedLabel = new StringBuffer();
									parsedLabel.append(labelItems[1]);
									for (int ji = 2; ji < labelItems.length; ji++) {
										parsedLabel
												.append("-" + labelItems[ji]);
									}
									if (parsedLabel.toString().equals(vnClass)) {
										// get sourceMa
										Source source = null;
										if (sourceMap.containsKey(luId)) {
											source = sourceMap.get(luId);
										} else {
											source = new Source();
											source.ref = luId;
										}

										Target target = new Target();
										target.ref = vns
												.getMonolingualExternalRefs()
												.iterator().next()
												.getExternalReference();
										target.decision = new Decision();
										target.decision.value = true;
										target.decision.confidence = DEFAULTCONFIDENCE;

										// add target to source
										if (source.targets.size() > 0) {
											source.targets.add(target);
										} else {
											source.targets.add(target);
										}
										count++;
										sourceMap.put(source.ref, source);
									}
								}
							}
						}
					}
				}
				lines++;
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
		logString.append("Converted " + alignmentFile + ", statistics:" + LF);
		logString.append("\tInput Lines: " + lines +LF);
		logString.append("\tOutput: " + output.size()+LF);
		logString.append("\tNo alignment target: " + noSource + LF);
		logString.append("\tControl: output +  no alignment = input lines: "
				+ (output.size() + noSource) + LF);
		logString.append("\tNumber of alignment pairs in output:" + count);
		logger.info(logString.toString());

		writer.writeMetaData(metadata);
		Alignments alignments = new Alignments();
		alignments.source = new LinkedList<>();
		alignments.source.addAll(sourceMap.values());
		writer.writeAlignments(alignments);
		writer.close();
	}

	@Override
	public XmlMeta getDefaultXmlMeta() {
		XmlMeta metadata = new XmlMeta();
		metadata.title = "VerbNet-FrameNet mapping from SemLink version ";
		metadata.creator = "http://verbs.colorado.edu/semlink/";
		metadata.date = "2015-03-13"; // download date
		metadata.description = "Manual mapping of VerbNet class members to FrameNet Senses, the mapping is part of SemLink";
		metadata.identifier = "VNFN32";
		metadata.publisher = "University of Colorado";
		metadata.rights = "VerbNet 3.0 (and 3.x) License";
		metadata.version = "3.2";
		ResourceXml targetResource = new ResourceXml();
		targetResource.description = "VerbNet version 3.2";
		// matches lexiconId
		targetResource.id = "VN_Lexicon_0";
		targetResource.language = "en";
		// matches externalSystem
		targetResource.identifiertype = "VerbNet_3.2_eng_sense";
		metadata.targetResource = targetResource;
		ResourceXml sourceResource = new ResourceXml();
		sourceResource.description = "FrameNet version 1.x";
		// matches lexiconId
		sourceResource.id = "FN_Lexicon_0";
		sourceResource.language = "en";
		// matches externalSystem
		sourceResource.identifiertype = "FrameNet_1.5_eng_lexicalUnit";
		metadata.sourceResource = sourceResource;
		Decisiontype type = new Decisiontype();
		type.id = "SemLink_VNFN";
		type.name = "SemLink VNFN";
		type.type = Decisiontype.Decision.MANUAL;
		List<Decisiontype> decisionTypes = new ArrayList<>();
		decisionTypes.add(type);
		metadata.decisiontypes = decisionTypes;
		// no separate scores given => no scoretype information
		return metadata;
	}

	public static void main(String[] args) throws Exception {
		String UBY_HOME = System.getenv("UBY_HOME");
		String alignmentFile = UBY_HOME + "SemLink/1.2.2c/vn-fn/VNC-FNF.s";
		String outFile = UBY_HOME
				+ "/target/verbNetFrameNetAlignment22c_newXml.xml";
		DBConfig dbConfig = new DBConfig("localhost/uby_clarin_0_7_0w",
				"com.mysql.jdbc.Driver", "mysql", "root", "pass", false);
		VnFnSenseAlignmentXml al = new VnFnSenseAlignmentXml(alignmentFile,
				outFile, dbConfig);
		al.toAlignmentXml(al.getDefaultXmlMeta());
	}
}
