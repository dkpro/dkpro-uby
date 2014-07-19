/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.utilities;

import java.sql.SQLException;
import java.util.HashMap;

public class TemporaryTableUtils
{
	private String tablename;
	private HashMap<String, String> fields;
	private MySQLConnect mysql;

	private static boolean table_Lexical_Lemma_Sense_is_available = false;

	public TemporaryTableUtils(String tablename,
			HashMap<String, String> fields, MySQLConnect mysql)
		throws SQLException
	{
		this.tablename = tablename;
		this.fields = fields;
		this.mysql = mysql;
	}

	private void createtable()
		throws SQLException
	{
		String sql = "CREATE TEMPORARY TABLE IF NOT EXISTS " + tablename + " (";
		for (String fieldname : fields.keySet()) {
			String fiedltype = fields.get(fieldname);
			sql += fieldname + " " + fiedltype + " , ";
		}
		sql = sql.trim();
		if (sql.endsWith(",")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		sql += ")";

		mysql.reConnect();
		mysql.executeUpdate(sql);
	}

	public void initializationTempTable(String sqlInsertData)
		throws SQLException
	{
		createtable();
		mysql.executeUpdate(sqlInsertData);
	}

    /**
     * @param mysql
     *            database connection object
     * @return name of temp table. The Temp table will be created with all data from Lexical Entry
     *         joined to Sense,Lexicon, FormRepresentation_Lemma tables
     */
	public static String table_LexicalEntry_Sense_Lemma(MySQLConnect mysql)
		throws SQLException
	{
		if (!table_Lexical_Lemma_Sense_is_available) {
			HashMap<String, String> fields = new HashMap<String, String>();
			System.out.println("Creating temporary table...");
			fields.put("lexicalEntryId", "varchar(255)");
			fields.put("partOfSpeech", "varchar(255)");
			fields.put("separableParticle", "varchar(255)");
			fields.put("lemmaId", "bigint(20)");
			fields.put("lexiconId", "varchar(255)");
			fields.put("lexiconName", "varchar (255)");
			fields.put("inflection", "varchar(255)");
			fields.put("singularetantum", "varchar(255)");
			fields.put("pluraletantum", "varchar(255)");
			fields.put("listOfComponentsId", "bigint(20)");

			TemporaryTableUtils ttu = new TemporaryTableUtils(
					"LexicalEntry_Sense_Lemma", fields, mysql);
			String sqlInsertData = "Insert into LexicalEntry_Sense_Lemma Select senseId,synsetId,externalReference from "
					+ " Sense JOIN (Synset, MonolingualExternalRef)"
					+ " ON (Sense.synsetId=Synset.synsetId AND Synset.synsetId=MonolingualExternalRef.synsetId)";

			try{
				ttu.initializationTempTable(sqlInsertData);
				table_Lexical_Lemma_Sense_is_available=true;
				System.out.println("...finished creating temporary table");
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		String returnValue=(table_Lexical_Lemma_Sense_is_available)?"LexicalEntry_Sense_Lemma":null;
		return returnValue;
	}

	/*
	 * This part for getting Senses via original senseID or synsetID.
	 * SenseAlignment converters have to use it to speed the performance up.
	 */
    /**
     * @param mysql
     *            : database connection object
     * @return name of temp table. The Temp table will be created with all data from Sense joined to
     *         Synset and MonolingualExternalRef tables
     */
	public static String table_Sense_Synset_MonolingualExternalRef(
			MySQLConnect mysql)
		throws SQLException
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("senseId", "varchar(255) NOT NULL");
		fields.put("synsetId", "varchar(255) NOT NULL");
		fields.put("externalReference", "varchar(255)");
		TemporaryTableUtils ttu = new TemporaryTableUtils(
				"Sense_Synset_MonolingualExternalRef", fields, mysql);
		String sqlInsertData = "Insert into Sense_Synset_MonolingualExternalRef Select Sense.senseId,Synset.synsetId,externalReference from "
				+ "Sense JOIN (Synset, MonolingualExternalRef)"
				+ " ON (Sense.synsetId=Synset.synsetId AND Synset.synsetId=MonolingualExternalRef.synsetId)";
		ttu.initializationTempTable(sqlInsertData);
		return "Sense_Synset_MonolingualExternalRef";
	}

	public static String table_Sense_MonolingualExternalRef(MySQLConnect mysql)
		throws SQLException
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("senseId", "varchar(255) NOT NULL");
		fields.put("externalReference", "varchar(255)");
		TemporaryTableUtils ttu = new TemporaryTableUtils(
				"Sense_MonolingualExternalRef", fields, mysql);
		String sqlInsertData = "Insert into Sense_MonolingualExternalRef Select Sense.senseId,externalReference from "
				+ "Sense JOIN MonolingualExternalRef"
				+ " ON Sense.senseId=MonolingualExternalRef.senseId";
		ttu.initializationTempTable(sqlInsertData);
		return "Sense_MonolingualExternalRef";
	}

    /**
     * TODO: DROP TEMPORARY TABLE
     *
     * @param temp_table
     *            : temporary table's name
     */
	public void destroyTempTable(String temp_table)
		throws SQLException
	{
		mysql.executeUpdate("DROP TABLE " + temp_table);
	}
}
