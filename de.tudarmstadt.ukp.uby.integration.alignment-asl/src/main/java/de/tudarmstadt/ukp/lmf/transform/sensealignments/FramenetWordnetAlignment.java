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

package de.tudarmstadt.ukp.lmf.transform.sensealignments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignment;
import de.tudarmstadt.ukp.lmf.transform.alignments.SenseAlignmentUtils;

/**
 * Convert the FrameNet-WordNet alignments to UBY format. This class takes the
 * FrameNet 1.5 and WordNet 3.0 ids from a file and integrates them to UBY
 *
 * @author Silvana Hartmann
 *
 */
public class FramenetWordnetAlignment extends SenseAlignment {

	static String UBY_HOME = System.getenv("UBY_HOME");
	static String DKPRO_HOME = System.getenv("DKPRO_HOME");
	protected static Log logger = LogFactory.getLog
			(FramenetWordnetAlignment.class);
	protected SenseAlignmentUtils saUtils;
	ArrayList<String> notfoundWn = null;
	ArrayList<String> notfoundFn = null;
	ArrayList<String> notAddedAll;
	int inputsize = 0;
	DBConfig tempsource;

	/**
	 *
	 * @param sourceUrl
	 * @param destUrl
	 * @param alignmentFile
	 * @param user
	 * @param pass
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public FramenetWordnetAlignment(String sourceUrl, String destUrl, String dbDriver, String dbVendor,
			String alignmentFile, String user, String pass, String UBY_HOME)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, FileNotFoundException {
		super(sourceUrl, destUrl, dbDriver, dbVendor, alignmentFile, user, pass, UBY_HOME);
		notfoundFn = new ArrayList<String>();
		notfoundWn = new ArrayList<String>();
		notAddedAll = new ArrayList<String>();
		tempsource = new DBConfig(sourceUrl, dbDriver, dbVendor, user, pass, false);
		saUtils = new SenseAlignmentUtils(tempsource, tempsource, 0, 0,"temp_Duc", "temp_Duc");
	}

	public FramenetWordnetAlignment(String sourceUrl, String destUrl,
			String alignmentFile, String user, String pass) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, FileNotFoundException{
		this(sourceUrl, destUrl,"com.mysql.jdbc.Driver",
				"mysql", alignmentFile, user, pass, UBY_HOME);
	}
	/**
	 * Collect UBY SenseIds for the aligned senses based on synsetId and lemma
	 * for WordNet and based on lexical unit id for FrameNet
	 *
	 * @throws IllegalArgumentException
	 */
	@Override
	public void getAlignment()
			throws IllegalArgumentException {
		List<String[]> data = null;
		data = readAlignmentFile();
		if (ubySource == null) {
			logger.warn("uby source is empty");
		}
		int counter = 0; // input sense pairs
		int found = 0; // output sense pairs
		// temp table for FN
		String declareFieldsFN = "senseId varchar(255) NOT NULL, externalReference varchar(255)";
		String sqlInsertDataFN = "SELECT S.senseId, "
				+ " M.externalReference  "
				+ " FROM Sense S JOIN MonolingualExternalRef M"
				+ " ON (S.senseId=M.senseId)"
				+ "where substring(S.senseId,1,2)=\"FN\"";
		String declareFieldsWN = "senseId varchar(255) NOT NULL, "
				+ "synsetId varchar(255) NOT NULL, "
				+ "writtenForm varchar(255) NOT NULL, "
				+ "lexicalEntryId varchar(255) NOT NULL, "
				+ "externalReference varchar(255)";
		// temp table for WN
		String sqlInsertDataWN = "SELECT Sense.senseId, Sense.synsetId,"
				+ "FormRepresentation_Lemma.writtenForm,LexicalEntry.lexicalEntryId, "
				+ "MonolingualExternalRef.externalReference "
				+ "FROM Sense JOIN (MonolingualExternalRef,LexicalEntry,FormRepresentation_Lemma) "
				+ "ON (Sense.synsetId=MonolingualExternalRef.synsetId "
				+ "AND Sense.lexicalEntryId=LexicalEntry.lexicalEntryId "
				+ "AND FormRepresentation_Lemma.lemmaId=LexicalEntry.lemmaId) "
				+ "WHERE MonolingualExternalRef.externalSystem=\"WordNet_3.0_eng_synsetOffset\"";
		try {
			saUtils.createTempTable(declareFieldsWN, sqlInsertDataWN, 0);
			saUtils.createTempTable(declareFieldsFN, sqlInsertDataFN, 1);

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// iterate over alignment entries
		for (String[] d : data) {
			counter++;
			// show progress:
			if ((counter % 1000) == 0) {
				logger.info("# processed alignments: " + counter);
			}

			List<String> wnSenses;
			List<Sense> fnSenses;
			try {
				// get FrameNet sense by ExternalReference (lexical unit Id)
				fnSenses = saUtils.getSensesByExternalRefID(d[0], 1, false);
				// get WordNet sense by Synset Offset and Lemma
				wnSenses = saUtils.getSensesByWNSynsetOffsetAndLemma(d[1],
						d[2].replace("_", " "), 0);
				if (fnSenses.size() == 1) { // exactly one fn sense
					Sense fns = fnSenses.get(0);
					if (wnSenses.size() == 1) { // exactly one wn sense
						// add the data
						addSourceSense(fns);
						Sense wns = ubySource.getSenseById(wnSenses.get(0));
						addDestSense(wns);
						found++;
					} else if (wnSenses.size() == 0) { // no WN sense
						logger.warn("WN sense not found: " + d[1]
								+ " " + d[2].replace("_", " "));
					} else { // more than one WN sense
						logger.info(
								"More than one WN sense for this key: " + d[1]
										+ " " + d[2].replace("_", " "));
						for (String sid : wnSenses) {
							Sense wns = ubySource.getSenseById(sid);
							addSourceSense(fns);
							addDestSense(wns);
						}
					}
				} else if (fnSenses.size() == 0) {
					logger.warn("No FN sense for this key: "
							+ d[0]);
				} else {
					logger.warn(
							"More than one FN sense for this key: " + d[0]);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
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
			reader = new BufferedReader(new FileReader(getAlignmentFileLocation()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				String[] items = line.split("\t");
				alignment.add(items);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + getAlignmentFileLocation());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("File could not be opended: " + getAlignmentFileLocation());
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
			System.err.println("Exception" + e + "could not write to" + outFile);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
