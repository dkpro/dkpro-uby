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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;

public class MySQLConnect
{
	private DBConfig dbConfig;
	private Connection connection;
	private Statement statement;
	private boolean useTemporaryTable;

    /**
     *
     * @param dbconfig
     *            database's configuration
     */
    public MySQLConnect(DBConfig dbconfig)
        throws SQLException, ClassNotFoundException
    {
		dbConfig=dbconfig;
		Class.forName(dbconfig.getJdbc_driver_class());
		connection=DriverManager.getConnection("jdbc:"+dbconfig.getDb_vendor()+"://" +dbconfig.getJdbc_url()+
				"?characterEncoding=UTF-8&useUnicode=true",dbconfig.getUser(),dbconfig.getPassword());
		statement=connection.createStatement();
		useTemporaryTable=false;
	}

    /**
     * @param dbconfig
     *            database's configuration
     * @param useTemporaryTable
     *            true if you want to use temporary tables to speed queries (like 10k or more
     *            queries) up
     */
    public MySQLConnect(DBConfig dbconfig, boolean useTemporaryTable)
        throws SQLException, ClassNotFoundException
    {
		dbConfig=dbconfig;
		Class.forName(dbconfig.getJdbc_driver_class());
		connection=DriverManager.getConnection("jdbc:"+dbconfig.getDb_vendor()+"://" +dbconfig.getJdbc_url()+
				"?characterEncoding=UTF-8&useUnicode=true",dbconfig.getUser(),dbconfig.getPassword());
		statement=connection.createStatement();
		useTemporaryTable=false;
	}

    /**
     * TODO: re-connect to database
     */
    public void reConnect()
        throws SQLException
    {
		String sql="Select 1";
		try{
			//test if the connection is whether alive
			statement.executeQuery(sql);
		}catch(Exception ex){
			connection=DriverManager.getConnection("jdbc:"+dbConfig.getDb_vendor()+"://" +dbConfig.getJdbc_url()+
				"?characterEncoding=UTF-8&useUnicode=true",dbConfig.getUser(),dbConfig.getPassword());
			statement=connection.createStatement();
		}
	}

    /**
     * TODO: Disconnect to the database
     */
    public void disconnect()
        throws SQLException
    {
		connection.close();
		statement.close();
	}

    /**
     * @return true is Temporary table(s) is/are used
     */
    public boolean isUseTemporaryTable()
    {
		return useTemporaryTable;
	}

    /**
     * @param sql
     *            SQL String query; just SELECT. Use executeUpdate for other purposes.
     * @return Result set of query
     */
    public ResultSet execute(String sql)
        throws SQLException
    {
		ResultSet rs=statement.executeQuery(sql);
		return rs;
	}

    /**
     * @param sql
     *            SQL String query: INSERT, DELETE, UPDATE queries
     */
    public void executeUpdate(String sql)
        throws SQLException
    {
		statement.executeUpdate(sql);
	}

    // FIXME Finalize should never be used! There needs to be another way of handling connections!
    @Override
    public void finalize()
    {
        try {
			statement.close();
			connection.close();
		}catch (Exception ex){
			//TODO nothing
		}
		statement=null;
		connection=null;
		dbConfig=null;
	}
}
