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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * This class helps to query a large table with more efficiently by
 * using direct mysql query.
 * @author nghiem 
 */
public class MySQLDirectQueries
{
	private DBConfig dbConfig;
	private Connection connect;

	private Statement statement;

	public MySQLDirectQueries(DBConfig dbConfig) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		this.dbConfig = dbConfig;
		Class.forName(dbConfig.getJdbc_driver_class()).newInstance();
		String connect2mysql = null;
		switch (dbConfig.getDBType()) {
		case DBConfig.H2:
			connect2mysql = dbConfig.getJdbc_url();
			break;
		case DBConfig.MYSQL:
			connect2mysql = "jdbc:mysql://"+dbConfig.getJdbc_url()+"?characterEncoding=UTF-8&useUnicode=true";
			break;
		}

		System.out.println(connect2mysql);
		connect = DriverManager.getConnection(connect2mysql, this.dbConfig.getUser(), this.dbConfig.getPassword());
		statement = connect.createStatement();
	}

	public ResultSet doQuery(String sql) throws SQLException{
		return statement.executeQuery(sql);
	}

	public void executeUpdateQuery(String sql)throws SQLException{
		int rows = statement.executeUpdate(sql);
		System.out.println(sql);
		System.out.println(rows + " updated");
	}
}
