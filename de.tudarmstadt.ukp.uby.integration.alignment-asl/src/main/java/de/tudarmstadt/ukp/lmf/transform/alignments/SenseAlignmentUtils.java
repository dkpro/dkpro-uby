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

package de.tudarmstadt.ukp.lmf.transform.alignments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

public class SenseAlignmentUtils
{

	private final DBConfig source;
	private final DBConfig dest;
	private final int typeSource;
	private final int typeDest;
	private final MySQLDirectQueries sourceConnection;
	private final MySQLDirectQueries destConnection;
	private final String tempTable1, tempTable2;

	/**
	 *
	 * @param source
	 *            : Source Database
	 * @param dest
	 *            : Dest Database
	 * @param typeSource
	 *            : Type of Source (synset 1 or sense 0)
	 * @param typeDest
	 *            : Type of Dest (synset 1 or sense 0)
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */

	public SenseAlignmentUtils(DBConfig source, DBConfig dest, int typeSource,
			int typeDest, String tempTableSource, String tempTableDest)
		throws SQLException, InstantiationException, IllegalAccessException,
		ClassNotFoundException
	{
		sourceConnection = new MySQLDirectQueries(source);
		destConnection = new MySQLDirectQueries(dest);

		this.typeSource = typeSource;
		this.typeDest = typeDest;

		this.tempTable1 = tempTableSource;
		this.tempTable2 = tempTableDest;

		this.source = source;
		this.dest = dest;

	}

	/**
	 * In my case, I just need temporary tables like that
	 *
	 * @param usingSynsetAxis
	 *            : if true: the table has column: synsetId else no
	 * @throws SQLException
	 */

	public void createDefaultTempTables(boolean usingSynsetAxis)
		throws SQLException
	{
		// create temp table
		String sql1 = null;
		String sql2 = null;

		switch (source.getDBType()) {
		case DBConfig.H2:
			sql1 = "CREATE TEMPORARY TABLE "
					+ tempTable1
					+ " (senseId varchar(255) ,"
					+ ((usingSynsetAxis == true) ? "synsetId  varchar(255) ,"
							: "") + "externalReference varchar(255) )";
			sql2 = "CREATE TEMPORARY TABLE "
					+ tempTable2
					+ " (senseId varchar(255) ,"
					+ ((usingSynsetAxis == true) ? "synsetId  varchar(255) ,"
							: "") + "externalReference varchar(255) )";
			break;
		case DBConfig.MYSQL:
			sql1 = "CREATE TEMPORARY TABLE "
					+ tempTable1
					+ " (senseId varchar(255) CHARACTER SET utf8 NOT NULL,"
					+ ((usingSynsetAxis == true) ? "synsetId  varchar(255) CHARACTER SET utf8 ,"
							: "") + "externalReference varchar(255) CHARACTER SET utf8 NOT NULL)";
			sql2 = "CREATE TEMPORARY TABLE "
					+ tempTable2
					+ " (senseId varchar(255) CHARACTER SET utf8 NOT NULL,"
					+ ((usingSynsetAxis == true) ? "synsetId  varchar(255) CHARACTER SET utf8,"
							: "") + "externalReference varchar(255) CHARACTER SET utf8)";
			break;
		}

		sourceConnection.executeUpdateQuery(sql1);
		destConnection.executeUpdateQuery(sql2);

		// Insert data into these temp tables
		//source
		insert2DefaultTemporaryTable(0, usingSynsetAxis);
		//dest
		insert2DefaultTemporaryTable(1, usingSynsetAxis);

		switch (source.getDBType()) {
		case DBConfig.H2:
			sourceConnection.executeUpdateQuery("CREATE INDEX IF NOT EXISTS  i_" + tempTable1 + " ON " + tempTable1 + " (externalReference)");
			destConnection.executeUpdateQuery("CREATE INDEX IF NOT EXISTS i_" + tempTable1 + " ON " + tempTable1 + " (externalReference)");
			break;
		case DBConfig.MYSQL:
			// Create an index on externalReference.
			sourceConnection.executeUpdateQuery("ALTER TABLE " + tempTable1
					+ " ADD INDEX i_" + tempTable1
					+ "_externalReference (externalReference)");
			destConnection.executeUpdateQuery("ALTER TABLE " + tempTable2
					+ " ADD INDEX i_" + tempTable2
					+ "_externalReference (externalReference)");
			break;
		}
	}

	/**
	 * In case you need your own temporary structure, use this method
	 *
	 * @param declareFields
	 *            : Declare types, constraints... for all fields <br>
	 *            E.g:
	 *            "senseId varchar(255) NOT NULL, externalReference varchar(255)"
	 * @param sqlInsertData
	 *            : the data collected from other tables. <br>
	 *            E.g: "Select * from A join B on (A.a=B.b)". The selection
	 *            should be all rows, data you need for your later processing
	 * @param DB
	 *            : 0 if source, 1 if dest RESOURCE
	 *
	 * @throws SQLException
	 */
	public void createTempTable(String declareFields, String sqlInsertData,int DB)
		throws SQLException{
		switch (DB) {
		case 0:// Source
			// new: instead doQuery
			sourceConnection.executeUpdateQuery("Create TEMPORARY TABLE " + tempTable1
					+ " (" + declareFields + ")");
			sourceConnection.executeUpdateQuery("INSERT INTO " + tempTable1 + " "
					+ sqlInsertData);
			break;
		case 1:// Dest
			destConnection.executeUpdateQuery("Create TEMPORARY TABLE " + tempTable2
					+ " (" + declareFields + ")");
			destConnection.executeUpdateQuery("INSERT INTO " + tempTable2 + " "
					+ sqlInsertData);
			break;
		}

	}

	/**
	 *
	 * @param wnSynsetOffset
	 * @param wnLemma
	 * @param DB
	 * 				: 0 = if source, 1 if dest resource
	 * @return list of sense IDs
	 * @throws SQLException
	 */
	public List<String> getSensesByWNSynsetOffsetAndLemma(String wnSynsetOffset, String wnLemma, int DB) throws SQLException {
		String[] temp = wnSynsetOffset.split("-");
		String refId = "[POS: noun] ";
		if (temp[1].equals("a")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (temp[1].equals("r")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (temp[1].equals("v")){
			refId=refId.replaceAll("noun", "verb");
		}
		refId=refId+temp[0];
		List<String> returnList = null;
		String sql = "";
		ResultSet rs = null;

		switch (DB) {
		case 0:
			switch (source.getDBType()) {
			case DBConfig.H2:
				sql = "SELECT senseId, writtenForm, externalReference " +
				"FROM " + tempTable1 + " WHERE externalReference='" + refId + "' AND writtenForm='"+ wnLemma+ "'";
				rs = sourceConnection.doQuery(sql);
				break;
			case DBConfig.MYSQL:
				sql = "SELECT senseId, writtenForm, externalReference " +
						"FROM " + tempTable1 + " WHERE externalReference=\"" + refId + "\" AND writtenForm=\""+ wnLemma+ "\"";
				rs = sourceConnection.doQuery(sql);
				break;
			}
			break;
		case 1:
			switch (source.getDBType()) {
			case DBConfig.H2:
				sql = "SELECT senseId, writtenForm, externalReference " +
				"FROM " + tempTable1 + " WHERE externalReference='" + refId + "' AND writtenForm='"+ wnLemma+ "'";
				rs = destConnection.doQuery(sql);
				break;
			case DBConfig.MYSQL:
				sql = "SELECT senseId, writtenForm, externalReference " +
					"FROM " + tempTable1 + " WHERE externalReference=\"" + refId + "\" AND writtenForm=\""+ wnLemma+ "\"";
				rs = destConnection.doQuery(sql);
				break;
			}
			break;
		}


		if (rs != null) {
			returnList = new ArrayList<String>();
			while (rs.next()) {
				String sId = rs.getString("senseId");
				returnList.add(sId);
			}
		}
		return returnList;
	}
	/**
	 *
	 * @param referenceID
	 *            : externalReference value
	 * @param DB
	 *            : O if source, 1 if dest RESOURCE
	 * @return list of senses by external reference ID
	 * @throws SQLException
	 */

	public List<Sense> getSensesByExternalRefID(String referenceID, int DB,boolean usingSynsetId)
		throws SQLException
	{
		List<Sense> returnList = null;
		String sql = "";
		ResultSet rs = null;
		switch (DB) {
		case 0:
			switch (source.getDBType()) {
			case DBConfig.H2:
				sql = "Select senseId, externalReference "
						+ ((usingSynsetId == true) ? ",synsetId" : " ") + " from "
						+ tempTable1 + " where externalReference='" + referenceID
						+ "'";
				rs = sourceConnection.doQuery(sql);
				break;
			case DBConfig.MYSQL:
				sql = "Select senseId, externalReference "
						+ ((usingSynsetId == true) ? ",synsetId" : " ") + " from "
						+ tempTable1 + " where externalReference=\"" + referenceID
						+ "\"";
				rs = sourceConnection.doQuery(sql);
				break;
			}
			break;
		case 1:
			switch (source.getDBType()) {
			case DBConfig.H2:
				sql = "Select senseId, externalReference "
						+ ((usingSynsetId == true) ? ",synsetId" : " ") + " from "
						+ tempTable2 + " where externalReference='" + referenceID
						+ "'";
				rs = destConnection.doQuery(sql);
				break;
			case DBConfig.MYSQL:
				sql = "Select senseId, externalReference "
						+ ((usingSynsetId == true) ? ",synsetId" : " ") + " from "
						+ tempTable2 + " where externalReference=\"" + referenceID
						+ "\"";
				rs = destConnection.doQuery(sql);
				break;
			}
			break;
		}

		if (rs != null) {
			returnList = new ArrayList<Sense>();
			while (rs.next()) {
				String ref = rs.getString("externalReference");
				if (ref.equals(referenceID)) {
					Sense sense = new Sense();
					// senseAlignment just need sense ID so hopefully, the null
					// value of
					// other attributes will not cause any problem
					sense.setId(rs.getString("senseId"));
					if (usingSynsetId) {
						Synset synset = new Synset();
						synset.setId(rs.getString("synsetId"));
						sense.setSynset(synset);
					}
					returnList.add(sense);
				}
			}
		}
		return returnList;
	}

	public void destroyTempTable()
		throws SQLException
	{
		destConnection.executeUpdateQuery("DROP TABLE " + tempTable1);
		sourceConnection.executeUpdateQuery("DROP TABLE " + tempTable2);
	}

	private void insert2DefaultTemporaryTable(int DB,boolean usingSynsetAxis)
		throws SQLException
	{
		ResultSet rs = null;
		int rows = 0;
		int loop = 0;
		int limitRowsEachInsertion=1000000;
		/*
		 * I have to limit the number of rows for each time insert database, because
		 * the innodb_buffer_pool_size is limited to 8MB.
		 * So I decided to import data into db each time 100k rows.
		 *
		 * Tested! Not efficient! Please set innodb_buffer_pool_size to 256MB
		 */

		String sql1 = " Select Count(Sense.senseId) as count from Sense JOIN MonolingualExternalRef on "
				+ " (Sense.senseId=MonolingualExternalRef.senseId)";

		String sql2 = " Select Count(Sense.senseId) as count "
				+ "FROM Sense "
				+ "JOIN Synset ON Sense.synsetId=Synset.synsetId "
				+ "JOIN MonolingualExternalRef ON Synset.synsetId=MonolingualExternalRef.synsetId";

		// Insert data into these temp tables
		String sql1_Insert = "INSERT INTO " + ((DB==0)?tempTable1:tempTable2) + " SELECT Sense.senseId, "
				+ ((usingSynsetAxis == true) ? "Sense.synsetId," : "")
				+ " MonolingualExternalRef.externalReference  "
				+ " FROM Sense JOIN MonolingualExternalRef"
				+ " ON (Sense.senseId=MonolingualExternalRef.senseId)";
		String sql2_Insert = "INSERT INTO "
				+ ((DB==0)?tempTable1:tempTable2)
				+ " SELECT Sense.senseId, "
				+ ((usingSynsetAxis == true) ? "Sense.synsetId," : "")
				+ " MonolingualExternalRef.externalReference "
				+ "FROM Sense "
				+ "JOIN Synset ON Sense.synsetId=Synset.synsetId "
				+ "JOIN MonolingualExternalRef ON Synset.synsetId=MonolingualExternalRef.synsetId";

		switch (DB) {
		case 0:

			if (typeSource == 1) {
				rs = sourceConnection.doQuery(sql2);
			}
			else if (typeSource == 0) {
				rs = sourceConnection.doQuery(sql1);
			}
			rs.next();
			rows = rs.getInt("count");
			loop = rows / limitRowsEachInsertion + 1;

			for (int i = 0; i < loop; i++) {
				int first = i*limitRowsEachInsertion;
				if (typeSource == 1) {
					sourceConnection.executeUpdateQuery(sql2_Insert+" LIMIT "+first+","+limitRowsEachInsertion);
				}
				else if (typeSource == 0) {
					sourceConnection.executeUpdateQuery(sql1_Insert+" LIMIT "+first+","+limitRowsEachInsertion);
				}
			}
			break;
		case 1:
			rs = null;
			if (typeDest == 1) {
				rs = destConnection.doQuery(sql2);
			}
			else if (typeDest == 0) {
				rs = destConnection.doQuery(sql1);
			}
			rs.next();
			rows = rs.getInt("count");
			//100k rows for each time
			loop = rows / limitRowsEachInsertion + 1;

			for (int i = 0; i < loop; i++) {
				int first=i*limitRowsEachInsertion;
				if (typeDest == 1) {
					destConnection.executeUpdateQuery(sql2_Insert+" LIMIT "+first+","+limitRowsEachInsertion);
				}
				else if (typeDest == 0) {
					destConnection.executeUpdateQuery(sql1_Insert+" LIMIT "+first+","+limitRowsEachInsertion);
				}
			}
			break;
		}
	}
}
