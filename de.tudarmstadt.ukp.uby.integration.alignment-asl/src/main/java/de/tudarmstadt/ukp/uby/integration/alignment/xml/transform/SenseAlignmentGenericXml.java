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

package de.tudarmstadt.ukp.uby.integration.alignment.xml.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.integration.alignment.xml.model.Decisiontype;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Target;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;

/**
 * Create uby lexical resource containing sense axes
 * directly from generic alignment xml file
 * Replaces SenseAlignment and children of SenseAlignment
 */
public class SenseAlignmentGenericXml extends AlignmentGenericXml {

	private final TreeMap<String, SenseAxis> senseAxisMap;

	/*
	protected final static Log logger = LogFactory
			.getLog(SenseAlignmentGenericXml.class);
	*/

	public SenseAlignmentGenericXml(String sourceUrl, String dbDriver,
			String dbVendor, String alignmentFile, String user, String pass) {
		super(sourceUrl, dbDriver, dbVendor, alignmentFile, user, pass);
		senseAxisMap = new TreeMap<>();
	}

	public SenseAlignmentGenericXml(DBConfig dbconf, String alignmentFile) {
		super(dbconf,alignmentFile);
		senseAxisMap = new TreeMap<>();
	}

	/**
	 * Convert sense alignment in generic alignment xml format to LMF
	 *
	 * @param idPrefix
	 * @throws ParseException
	 */
	@Override
    public void getAlignment(String idPrefix) throws ParseException {

		logger.info("looking up alignment");
		// expect single decisiontype
		Decisiontype decisiontype = metadata.decisiontypes.get(0);
		String sourceType = metadata.sourceResource.identifiertype;
		String destType = metadata.targetResource.identifiertype;

		/* determine sense axis type based on languages */
		ESenseAxisType senseAxisType = null;
		if (metadata.sourceResource.language
				.equals(metadata.targetResource.language)) {
			senseAxisType = ESenseAxisType.monolingualSenseAlignment;
		} else {
			senseAxisType = ESenseAxisType.crosslingualSenseAlignment;
		}

		MetaData meta = new MetaData();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date d = formatter.parse(metadata.date);
		meta.setCreationDate(d);
		meta.setId(metadata.identifier);
		meta.setVersion(metadata.version);

		meta.setAutomatic(decisiontype.type == Decisiontype.Decision.AUTOMATIC);
		meta.setCreationProcess(decisiontype.id);
		meta.setCreationTool(metadata.description);
		lmfMetaData.add(meta);

		Lexicon sourceLexicon = uby
				.getLexiconById(metadata.sourceResource.id);
		Lexicon destLexicon = uby
				.getLexiconById(metadata.targetResource.id);
		int id = 0;

		/* Lookup of alignments in UBY */
		for (Source source : alignments) {
			List<Sense> sourceSenses = getSenses(sourceType, source.ref,
					sourceLexicon);
			for (Target target : source.targets) {
				// only add "positive" alignments for now! - nonalignments are
				// not modeled
				if (target.decision.value == true) {
					List<Sense> destSenses = getSenses(destType, target.ref,
							destLexicon);
					for (Sense sourceSense : sourceSenses) {
						for (Sense destSense : destSenses) {
							if (destSense != null && sourceSense != null) {
								// avoid duplicates
								if (!senseAxisMap.containsKey(sourceSense
										.getId() + "%%" + destSense.getId())) {
									SenseAxis axis = new SenseAxis();
									axis.setId(idPrefix + "_" + id);
									// set confidence score if available
									if (target.decision.confidence != null) {
										axis.setConfidence(target.decision.confidence);
									} else {
										axis.setConfidence(DEFAULTCONFSCORE);
									}
									axis.setLexiconOne(sourceLexicon);
									axis.setLexiconTwo(destLexicon);
									axis.setMetaData(meta);
									axis.setSenseAxisType(senseAxisType);
									axis.setSenseOne(sourceSense);
									axis.setSenseTwo(destSense);
									if (sourceType.equals(UBY_SYNSET_ID)) {
										axis.setSynsetOne(sourceSense
												.getSynset());
									}
									if (destType.equals(UBY_SYNSET_ID)) {
										axis.setSynsetTwo(destSense.getSynset());
									}
									// axis.setSenseAxisRelations(senseAxisRelations);
									senseAxisMap.put(sourceSense.getId() + "%%"
											+ destSense.getId(), axis);
									id++;
								} else {
									System.err.println("catching duplicates "
											+ sourceSense.getId() + "%%"
											+ destSense.getId());
								}
							} else {
								logString.append("No alignment for: "
										+ source.ref + " " + target.ref);
								logString.append(LF);
								nullAlignment++;
							}
						}
					}
				}
			}
		}
		logString.append("So many input id pairs could not be aligned: "
				+ nullAlignment);
		logger.info(logString.toString());
	}

	/**
	 * Write sense alignments to UBY LMF xml
	 *
	 * @param idPrefix
	 * @param dtdVersion
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	@Override
    public void toLMF(String idPrefix, String dtdVersion, String outfile)
			throws IOException, TransformerException, SAXException {
		LMFXmlWriter xmlWriter = new LMFXmlWriter(outfile, UBY_HOME
				+ "/resources/dtd/DTD_unifiedModel_" + dtdVersion + ".dtd");

		LexicalResource lexicalResource = new LexicalResource();
		List<MetaData> metaDataList = new ArrayList<MetaData>(
				lmfMetaData.size());
		int i = 0;
		for (MetaData meta : lmfMetaData) {
			meta.setId(idPrefix + "_Meta_" + i);
			metaDataList.add(meta);
			i++;
		}
		lexicalResource.setMetaData(metaDataList); // set metadata for
													// lexicalresource!
		LinkedList<SenseAxis> senseaxes = new LinkedList<>();
		senseaxes.addAll(senseAxisMap.values());
		lexicalResource.setSenseAxes(senseaxes);
		lexicalResource.setDtdVersion(dtdVersion);
		lexicalResource.setName("Uby_Alignments_" + idPrefix);
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("Alignments_" + idPrefix);
		lexicalResource.setGlobalInformation(globalInformation);
		xmlWriter.writeElement(lexicalResource);
		xmlWriter.writeEndDocument();

	}

	/**
	 * Read UBY LMF XML to database
	 *
	 * @param dbConfig
	 * @param xmlSource
	 * @param idPrefix
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	public static void toDB(DBConfig dbConfig, File xmlSource, String idPrefix)
			throws DocumentException, FileNotFoundException,
			IllegalArgumentException {
		convertToDB(dbConfig, xmlSource, "Uby_Alignments_" + idPrefix);
	}

}