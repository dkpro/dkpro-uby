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

import java.io.File;
import java.io.FileNotFoundException;

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
	private File hibernateMapDirectory; // Directory with Hibernate mapping files
	private boolean showSQL = false; // If true all SQL queries are printed on
										// the console

	/*
	 * true only if the user wants to provide custom Hibernate Mapping files
	 */
	private boolean loadDefaultMappings=true;

	/*
	 * true if the user wants to load default Hibernate mapping files used only for reading LMF database
	 * false if the user wants to load default Hibernate mapping files used only for writing to LMF database
	 * the value of this field is ignored if loadDefaultMappings is set to false
	 */
	private boolean accessMode = true;

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
	 * <li><b>loadDefaultMappings</b>: default=true; set to false if you want to provide custom hibernate Mapping files</li>
	 * <li><b>accessMode</b>: default=true; set to false if you want to use default Hibernate mappings for writing to LMF database
	 * <br><i>this field is ignored if loadDefaultMappings is set to false</i>.</li>
	 * <li><b>showSQL</b>: default=false; set to true if you want to print all
	 * sql queries.</li>
	 * </ul>
	 *
	 */
	public DBConfig()
	{
		// Nothing to do
	}

	/**
	 * Creates a new configuration of LMF database based on the consumed parameters. <br><br>
	 * Users of this constructor should provide a valid path to custom Hibernate mapping files used for reading/writing LMF database.<br>
	 * For creating the configuration of LMF database with default Hibernate mappings, located in src/main/resources/,<br>use
	 * {@link DBConfig#DBConfig(String, String, String, String, String, boolean, boolean)}
	 *
	 * @param url
	 *            Host_to_the_database/database_name
	 * @param jdbc_driver_class
	 *            The jdbc driver class using to access database
	 * @param db_vendor
	 * @param user
	 * 			  User name used for accessing the database
	 * @param password
	 *            Password for accessing the database
	 * @param hibernateMapPath
	 *            Path with Hibernate mapping files
	 * @param showSQL
	 *            If true all SQL queries are printed on the console
	 * @throws FileNotFoundException
	 * 			  If the specified path of Hibernate mappings does not exist or not a directory
	 */
	public DBConfig(String url, String jdbc_driver_class, String db_vendor,
			String user, String password, String hibernateMapPath,
			boolean showSQL) throws FileNotFoundException
	{
		this.db_vendor = db_vendor;
		this.jdbc_driver_class = jdbc_driver_class;
		this.jdbc_url = url;
		this.user = user;
		this.password = password;
		this.hibernateMapDirectory = new File(hibernateMapPath);
		this.loadDefaultMappings = false;
		if(!this.hibernateMapDirectory.isDirectory()) {
			throw new FileNotFoundException("Specified path of Hibernate mappings does not exist or not a directory");
		}

		this.showSQL = showSQL;
	}

	/**
	 * Creates a new configuration of LMF database based on the consumed parameters. <br><br>
	 * This constructor forces usage of default Hibernate mapping files, located in src/main/resources.<br>
	 * For creating the configuration of LMF database with custom Hibernate mappings<br> use
	 * {@link DBConfig#DBConfig(String, String, String, String, String, String, boolean)}
	 * @param url
	 *            Host_to_the_database/database_name
	 * @param jdbc_driver_class
	 *            The jdbc driver class using to access database
	 * @param db_vendor
	 * @param user
	 * 			  User name used for accessing the database
	 * @param password
	 *            Password for accessing the database
	 * @param isAccess
	 * 			  This argument should be true if mappings for reading the LMF database should be loaded,<br>
	 * false for loading the mappings for writing to LMF database
	 * @param showSQL
	 *            If true all SQL queries are printed on the console
	 */
	public DBConfig(String url, String jdbc_driver_class, String db_vendor,
			String user, String password, boolean isAccess,
			boolean showSQL)
	{
		this.db_vendor = db_vendor;
		this.jdbc_driver_class = jdbc_driver_class;
		this.jdbc_url = url;
		this.user = user;
		this.password = password;
		this.accessMode = isAccess;
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

	/**
	 * @return the loadDefaultMappings
	 */
	public boolean isLoadDefaultMappings() {
		return loadDefaultMappings;
	}

	/**
	 * @param loadDefaultMappings the loadDefaultMappings to set
	 */
	public void setLoadDefaultMappings(boolean loadDefaultMappings) {
		this.loadDefaultMappings = loadDefaultMappings;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the hibernateMapDirectory
	 */
	public File getHibernateMapDirectory() {
		return hibernateMapDirectory;
	}

	/**
	 * @param hibernateMapDirectory the hibernateMapDirectory to set
	 */
	public void setHibernateMapDirectory(File hibernateMapDirectory) {
		this.hibernateMapDirectory = hibernateMapDirectory;
	}

	/**
	 * @return the accessMode
	 */
	public boolean isAccessMode() {
		return accessMode;
	}

	/**
	 * @param accessMode the accessMode to set
	 */
	public void setAccessMode(boolean accessMode) {
		this.accessMode = accessMode;
	}

}
