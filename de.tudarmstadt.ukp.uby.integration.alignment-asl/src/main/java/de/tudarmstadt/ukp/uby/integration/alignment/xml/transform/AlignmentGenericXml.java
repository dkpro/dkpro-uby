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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlReader;
import de.tudarmstadt.ukp.integration.alignment.xml.model.Source;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;

/**
 * Create uby lexical resource containing sense axes directly from generic
 * alignment xml file Replaces SenseAlignment and children of SenseAlignment
 */
public abstract class AlignmentGenericXml {

	// enum not possible: match any externalReference String in UBY
	public static final String UBY_SENSE_ID = "UBY_SENSE_ID";
	public static final String UBY_SYNSET_ID = "UBY_SYNSET_ID";
	public static final String UBY_SEMPRED_ID = "UBY_SEMANTIC_PREDICATE_ID";
	public static final String UBY_SEMPRED_LABEL = "UBY_SEMANTIC_PREDICATE_LABEL";
	public static final String UBY_SEMARG_ID = "UBY_SEMANTIC_ARGUMENT_ID";
	public static final String UBY_SEMARG_ROLE = "UBY_SEMANTIC_ARGUMENT_ROLE";

	public static final Double DEFAULTCONFSCORE = 1.0;

	public StringBuilder logString;
	public int nullAlignment = 0;

	protected static String UBY_HOME = System.getenv("UBY_HOME");
	protected static String LF = System.getProperty("line.separator");

	protected final static Log logger = LogFactory
			.getLog(AlignmentGenericXml.class);

	protected File alignment;

	protected LinkedList<MetaData> lmfMetaData;
	protected Uby uby;

	protected List<Source> alignments;
	protected XmlMeta metadata;

	public AlignmentGenericXml(String sourceUrl, String dbDriver,
			String dbVendor, String alignmentFile, String user, String pass) {

		this.alignment = new File(alignmentFile);

		lmfMetaData = new LinkedList<>();
		logString = new StringBuilder();

		if (!alignment.exists() && !alignment.isFile()) {
			logger.warn("Alignment file: " + alignmentFile + " doesn't exist! ");
			System.exit(1);
		}

		DBConfig dbConfig = new DBConfig(sourceUrl, dbDriver, dbVendor, user,
				pass, false);
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

	public AlignmentGenericXml(DBConfig dbconf, String alignmentFile) {

		this.alignment = new File(alignmentFile);

		lmfMetaData = new LinkedList<>();
		logString = new StringBuilder();

		if (!alignment.exists() && !alignment.isFile()) {
			logger.warn("Alignment file: " + alignmentFile + " doesn't exist! ");
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
	 * Convert alignments from generic xml format to UBY
	 */
	public abstract void getAlignment(String idPrefix) throws ParseException;

	/**
	 * Write alignments to UBY LMF xml
	 */
	public abstract void toLMF(String idPrefix, String dtdVersion,
			String outfile) throws IOException, TransformerException,
			SAXException;

	/**
	 * Get list senses for given lexicon, id and type of id
	 * 
	 * @param sourceType
	 * @param sourceID
	 * @param sourceLexicon
	 * @return a list of {@link Sense} objects
	 */
	protected List<Sense> getSenses(String sourceType, String sourceID,
			Lexicon sourceLexicon) {
		List<Sense> senses = new ArrayList<>();
		if (sourceType.equals(UBY_SENSE_ID)) {
			senses.add(uby.getSenseById(sourceID));
		} else if (sourceType.equals(UBY_SYNSET_ID)) {
			senses = uby.getSynsetById(sourceID).getSenses();
		} else {
			senses = uby.getSensesByOriginalReference(sourceType, sourceID,
					sourceLexicon);
		}
		if (senses.size() == 0) {
		}
		return senses;
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
	protected static void convertToDB(DBConfig dbConfig, File xmlSource,
			String fullPrefix) throws DocumentException, FileNotFoundException,
			IllegalArgumentException {
		XMLToDBTransformer xml2DB = new XMLToDBTransformer(dbConfig);
		xml2DB.transform(xmlSource, fullPrefix);
	}

	/**
	 * Read file containing sense alignment or predicate argument alignment
	 * 
	 * @param alignmentFile
	 *            - file in generic alignment format
	 * @throws IOException
	 */
	protected void readAlignmentFile(File alignmentFile) throws IOException {
		AlignmentXmlReader reader = null;
		try {
			reader = new AlignmentXmlReader(alignmentFile);
			metadata = reader.readMetaData();
			alignments = reader.readAlignments().source;
			logger.info("Read so many alignments : " + alignments.size());

		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			reader.close();
		}
	}
}