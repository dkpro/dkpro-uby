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
import de.tudarmstadt.ukp.integration.alignment.xml.model.SubSource;
import de.tudarmstadt.ukp.integration.alignment.xml.model.SubTarget;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Target;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;

/**
 * Create uby lexical resource containing predicate argument alignments from
 * generic alignment xml file
 */
public class PredicateAlignmentGenericXml extends AlignmentGenericXml {

	/* determine axis type */
	public static String axisType = "uby_predicate_axis";
	public static String subAxisType = "uby_argument_axis";

	private final TreeMap<String, PredicateArgumentAxis> axisMap;

	/*
	 * protected final static Log logger = LogFactory
	 * .getLog(PredicateAlignmentGenericXml.class);
	 */

	public PredicateAlignmentGenericXml(String sourceUrl, String dbDriver,
			String dbVendor, String alignmentFile, String user, String pass) {

		super(sourceUrl, dbDriver, dbVendor, alignmentFile, user, pass);
		this.alignment = new File(alignmentFile);
		axisMap = new TreeMap<>();
	}

	public PredicateAlignmentGenericXml(DBConfig dbconf, String alignmentFile) {

		super(dbconf, alignmentFile);
		this.alignment = new File(alignmentFile);

		lmfMetaData = new LinkedList<>();
		axisMap = new TreeMap<>();
		logString = new StringBuilder();

		if (!alignment.exists() && !alignment.isFile()) {
			System.out.println("Alignment file: " + alignmentFile
					+ " doesn't exist! ");
			System.exit(1);
		}

		DBConfig dbConfig = dbconf;
		try {
			uby = new Uby(dbConfig);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		try {
			readAlignmentFile(alignment);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert generic alignment xml format to LMF
	 *
	 * @param idPrefix
	 * @throws ParseException
	 */
	@Override
    public void getAlignment(String idPrefix) throws ParseException {

		String subIdPrefix = idPrefix + "_arg_";
		logger.info("looking up alignment");
		// expect single decisiontype
		Decisiontype decisiontype = metadata.decisiontypes.get(0);
		String sourceType = metadata.sourceResource.identifiertype;
		String destType = metadata.targetResource.identifiertype;
		String subSourceType = metadata.subSource.identifiertype;
		String subDestType = metadata.subTarget.identifiertype;

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

		Lexicon sourceLexicon = uby.getLexiconById(metadata.sourceResource.id);
		Lexicon destLexicon = uby.getLexiconById(metadata.targetResource.id);
		int id = 0;
		int subId = 0;
		/* Lookup of alignments in UBY */
		for (Source source : alignments) {
			List<SemanticPredicate> sourcePredicates = getPredicates(
					sourceType, source.ref, sourceLexicon);
			for (Target target : source.targets) {
				// only add "positive" alignments for now! - nonalignments are
				// not modeled
				if (target.decision.value == true) {
					List<SemanticPredicate> destPredicates = getPredicates(
							destType, target.ref, destLexicon);
					for (SemanticPredicate sourcePred : sourcePredicates) { // should
																			// be
																			// source
																			// pred
						for (SemanticPredicate destPred : destPredicates) {
							if (destPred != null && sourcePred != null) {
								// avoid duplicates
								if (!axisMap.containsKey(sourcePred.getId()
										+ "%%" + destPred.getId())) {
									PredicateArgumentAxis axis = new PredicateArgumentAxis();
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
									axis.setAxisType(axisType);
									axis.setSemanticPredicateOne(sourcePred);
									axis.setSemanticPredicateTwo(destPred);
									// axis.setSenseAxisRelations(senseAxisRelations);
									axisMap.put(sourcePred.getId() + "%%"
											+ destPred.getId(), axis);
									id++;

									// add new predicate alignment => add the
									// corresponding argument alignment
									for (SubSource subSource : target.subsources) {
										PredicateArgumentAxis argumentAxis = new PredicateArgumentAxis();
										argumentAxis.setId(subIdPrefix + "_"
												+ subId);
										argumentAxis
												.setLexiconOne(sourceLexicon);
										argumentAxis.setLexiconTwo(destLexicon);
										argumentAxis.setMetaData(meta); // same
																		// metadata
																		// as on
																		// predicate
																		// level
										argumentAxis.setAxisType(subAxisType);
										argumentAxis
												.setSemanticPredicateOne(sourcePred);
										argumentAxis
												.setSemanticPredicateTwo(destPred);
										List<SemanticArgument> sourceArgs = getArguments(
												subSourceType, subSource.ref,
												sourcePred);
										for (SubTarget subTarget : subSource.subtargets) {
											List<SemanticArgument> destArgs = getArguments(
													subDestType, subTarget.ref,
													destPred);
											for (SemanticArgument sourceArg : sourceArgs) {
												for (SemanticArgument destArg : destArgs) {
													argumentAxis
															.setSemanticArgumentOne(sourceArg);
													argumentAxis
															.setSemanticArgumentTwo(destArg);
													axisMap.put(
															sourceArg.getId()
																	+ "%%"
																	+ destArg
																			.getId(),
															argumentAxis);
													subId++;
												}
											}
										}

									}
								} else {
									System.err.println("catching duplicates "
											+ sourcePred.getId() + "%%"
											+ destPred.getId());
								}
							} else {
								logString.append("No predlevel alignment for: "
										+ source.ref + " " + target.ref);
								logString.append(LF);
								nullAlignment++;
								System.err.println("Cannot align these guys!");
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
	 * Get {@link List} of {@link SemanticPredicate} for given lexicon, id and
	 * type of id
	 *
	 * @param type
	 * @param ref
	 * @param lexicon
	 * @return
	 */
	private List<SemanticPredicate> getPredicates(String type, String ref,
			Lexicon lexicon) {
		List<SemanticPredicate> predicates = new ArrayList<>();
		if (type.equals(UBY_SEMPRED_ID)) {
			predicates.add(uby.getSemanticPredicateById(ref));
		} else {
			predicates = uby.getSemanticPredicatesByLabelAndLexicon(ref,
					lexicon);
		}
		if (predicates.size() == 0) {
			logger.info("Could not find semantic predicate for " + type + " "
					+ ref + " " + lexicon.getName());
		}
		return predicates;
	}

	/**
	 * Get {@link List} of {@link SemanticArgument} for given lexicon, id and
	 * type of id
	 *
	 * @param type
	 * @param ref
	 * @param predicate
	 * @return
	 */
	private List<SemanticArgument> getArguments(String type, String ref,
			SemanticPredicate predicate) {
		List<SemanticArgument> arguments = new ArrayList<>();
		if (type.equals(UBY_SEMARG_ID)) {
			arguments.add(uby.getSemanticArgumentById(ref));
		} else {
			arguments = uby.getSemanticArgumentsByLabelAndPredicate(ref,
					predicate);
		}
		if (arguments.size() == 0) {
			logger.info("Could not find semantic argument for " + type + " "
					+ ref + " " + predicate.getId());
		}
		return arguments;
	}

	/**
	 * Write alignments to UBY LMF xml
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
		LinkedList<PredicateArgumentAxis> predaxes = new LinkedList<>();
		predaxes.addAll(axisMap.values());
		lexicalResource.setPredicateArgumentAxes(predaxes);
		lexicalResource.setDtdVersion(dtdVersion);
		lexicalResource.setName("Uby_PredicateArgumentAlignments_" + idPrefix);
		GlobalInformation globalInformation = new GlobalInformation();
		globalInformation.setLabel("PredicateArgumentAlignments_" + idPrefix);
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
		convertToDB(dbConfig, xmlSource, "Uby_PredicateArgumentAlignments_"
				+ idPrefix);
	}

}