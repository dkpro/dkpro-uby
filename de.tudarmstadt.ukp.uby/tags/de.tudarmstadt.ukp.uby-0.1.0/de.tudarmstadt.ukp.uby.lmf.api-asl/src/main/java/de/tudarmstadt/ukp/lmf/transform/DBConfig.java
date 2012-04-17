/*******************************************************************************
 * Copyright 2012
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
package de.tudarmstadt.ukp.lmf.transform;

/**
 * Holds database configuration of LMF Database
 *
 * @author chebotar
 *
 */

public class DBConfig
{

	private String host; // Host of the database
	private String jdbc_driver_class;
	private String db_vendor;
	private String jdbc_url;
	// private String host; // Host of the database
	private String user; // User for accessing the database
	private String password; // Password for accessing the database
	// private String database; // Database name
	private String hibernateMapPath; // Path with Hibernate mapping files
	private boolean showSQL = false; // If true all SQL queries are printed on
										// the console

	/**
	 * This constructor do nothing to all attributes. If you use it, you have to
	 * set values for all attributes by using setter-methods. Those attributes
	 * should be set:<br>
	 * <ul>
	 * <li><b>host</b>: host of database</li>
	 * <li><b>user</b>: User for accessing the database</li>
	 * <li><b>password</b>: Password for accessing the database</li>
	 * <li><b>database</b>: DB name</li>
	 * <li><b>hibernateMapPath</b>: Path with Hibernate mapping files</li>
	 * <li><b>showSQL</b>: default=false; set to true if you want to print all
	 * sql queries.</li>
	 * </ul>
	 */
	public DBConfig()
	{
		// Nothing to do
	}

	/**
	 *
	 * @param url
	 *            Host_to_the_database/database_name
	 * @param jdbc_driver_class
	 *            The jdbc driver class using to access database
	 * @param db_vendor
	 * @param user
	 *            Password for accessing the database
	 * @param password
	 *            Database name
	 * @param hibernateMapPath
	 *            Path with Hibernate mapping files<br>
	 *            <ul>
	 *            <li>if null: automatically search in
	 *            classpath:hibernate/access/</li>
	 *            <li>if !null: search files in the given path</li>
	 *            <li>if FileNotFound: search in classpath:+ given path</li>
	 *            </ul>
	 * @param showSQL
	 *            If true all SQL queries are printed on the console
	 */

	public DBConfig(String url, String jdbc_driver_class, String db_vendor,
			String user, String password, String hibernateMapPath,
			boolean showSQL)
	{
		// this.host = host;
		this.db_vendor = db_vendor;
		this.jdbc_driver_class = jdbc_driver_class;
		this.jdbc_url = url;
		this.user = user;
		this.password = password;
		// this.database = database;
		this.hibernateMapPath = hibernateMapPath;
		this.showSQL = showSQL;
	}

	public String getJdbc_driver_class()
	{
		return jdbc_driver_class;
	}

	public void setJdbc_driver_class(String jdbcDriverClass)
	{
		jdbc_driver_class = jdbcDriverClass;
	}

	public String getDb_vendor()
	{
		return db_vendor;
	}

	public void setDb_vendor(String dbVendor)
	{
		db_vendor = dbVendor;
	}

	// /**
	// * @return the host
	// */
	// public String getHost() {
	// return host;
	// }
	// /**
	// * @param host the host to set
	// */
	// public void setHost(String host) {
	// this.host = host;
	// }
	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	public String getJdbc_url()
	{
		return jdbc_url;
	}

	public void setJdbc_url(String jdbcUrl)
	{
		jdbc_url = jdbcUrl;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	// /**
	// * @return the database
	// */
	// public String getDatabase() {
	// return database;
	// }
	// /**
	// * @param database the database to set
	// */
	// public void setDatabase(String database) {
	// this.database = database;
	// }
	/**
	 * @return the hibernateMapPath
	 */
	public String getHibernateMapPath()
	{
		return hibernateMapPath;
	}

	/**
	 * @param hibernateMapPath
	 *            the hibernateMapPath to set
	 */
	public void setHibernateMapPath(String hibernateMapPath)
	{
		this.hibernateMapPath = hibernateMapPath;
	}

	/**
	 * @return the showSQL
	 */
	public boolean isShowSQL()
	{
		return showSQL;
	}

	/**
	 * @param showSQL
	 *            the showSQL to set
	 */
	public void setShowSQL(boolean showSQL)
	{
		this.showSQL = showSQL;
	}

}
