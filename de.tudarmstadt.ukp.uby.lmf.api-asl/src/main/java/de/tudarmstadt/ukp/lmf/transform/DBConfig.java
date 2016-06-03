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
package de.tudarmstadt.ukp.lmf.transform;

import de.tudarmstadt.ukp.lmf.hibernate.UBYH2Dialect;

/**
 * Instance of this class holds database configuration of UBY-LMF Database.
 *
 * @author Yevgen Chebotar
 * @author Zijad Maksuti
 */
public class DBConfig
{

	private String host; // Host of the database
	private String jdbc_driver_class;
	private String db_vendor;
	private String jdbc_url;
	private String user; // User for accessing the database
	private String password; // Password for accessing the database
	private boolean showSQL = false; // If true all SQL queries are printed on
										// the console

	/*
	 * true if the user wants to load Hibernate mapping files used only for reading LMF database
	 * false if the user wants to load Hibernate mapping files used only for writing to LMF database
	 */
	//private boolean accessMode = true;

    /**
     * This constructor does nothing to all attributes. If you use it, you have to set values for
     * all attributes by using setter-methods. Those attributes should be set:<br>
     * <ul>
     * <li><b>host</b>: host of database</li>
     * <li><b>user</b>: User for accessing the database</li>
     * <li><b>password</b>: Password for accessing the database</li>
     * <li><b>url</b>: Host_to_the_database/database_name</li>
     * <li><b>showSQL</b>: default=false; set to true if you want to print all sql queries.</li>
     * </ul>
     */
	public DBConfig()
	{
		// Nothing to do
	}

    /**
     * Creates a new configuration of UBY-LMF database based on the consumed parameters.
     * 
     * @param url
     *            Host_to_the_database/database_name
     * @param jdbc_driver_class
     *            The jdbc driver class using to access database
     * @param db_vendor
     *            vendor name of the accessed database, e.g. mysql, hsqldb used in combination with
     *            the @param url to glue together the hibernate.connection.url
     * @param user
     *            User name used for accessing the database
     * @param password
     *            Password for accessing the database
     * @param showSQL
     *            If true all SQL queries are printed on the console
     */
	public DBConfig(String url, String jdbc_driver_class, String db_vendor,
			String user, String password, 
			boolean showSQL)
	{
		this.db_vendor = db_vendor;
		this.jdbc_driver_class = jdbc_driver_class;
		this.jdbc_url = url;
		this.user = user;
		this.password = password;
		this.showSQL = showSQL;
	}

    /**
     * Returns the jdbc driver class used by this {@link DBConfig} instance to access the database.
     * 
     * @return the jdbc driver class assigned to this DBConfig or null if the driver class is not
     *         set
     */
	public String getJdbc_driver_class()
	{
		return jdbc_driver_class;
	}

    /**
     * Sets the jdbc driver class used by this {@link DBConfig} instance to access the database.
     * 
     * @param jdbcDriverClass
     *            the jdbc driver class to set
     */
	public void setJdbc_driver_class(String jdbcDriverClass)
	{
		jdbc_driver_class = jdbcDriverClass;
	}

    /**
     * Returns the name the vendors name of the database accessed by this {@link DBConfig} instance.
     * 
     * @return the vendor of the accessed database or null if the name is not set
     */
	public String getDb_vendor()
	{
		return db_vendor;
	}

    /**
     * Sets the the vendors name of the database accessed by this {@link DBConfig} instance.
     * 
     * @param dbVendor
     *            the vendor name of the accessed database to set
     */
	public void setDb_vendor(String dbVendor)
	{
		db_vendor = dbVendor;
	}

    /**
     * Returns a {@link String} instance representing the user name needed to access the database.
     * 
     * @return the user name needed to access the database or null if the user name is not set
     * 
     * @see DBConfig
     */
	public String getUser()
	{
		return user;
	}

    /**
     * Returns the {@link String} instance representing the user name needed to access the database.
     * 
     * @return the user name needed to access the database or null if the user name is not set
     * 
     * @see DBConfig
     */
	public String getJdbc_url()
	{
		return jdbc_url;
	}

	public void setJdbc_url(String jdbcUrl)
	{
		jdbc_url = jdbcUrl;
	}

    /**
     * Sets the {@link String} instance representing the user name needed to access the database.
     * 
     * @param user
     *            the user name to set
     * 
     * @see DBConfig
     */
	public void setUser(String user)
	{
		this.user = user;
	}

    /**
     * Returns the {@link String} instance representing the password needed to access the database.
     * 
     * @return the password needed to access the database or null if the password is not set
     * 
     * @see DBConfig
     */
	public String getPassword()
	{
		return password;
	}

    /**
     * Sets the {@link String} instance representing the password needed to access the database.
     * 
     * @param password
     *            the password to set
     * 
     * @see DBConfig
     */
	public void setPassword(String password)
	{
		this.password = password;
	}

    /**
     * Returns true if the SQL queries to the database, accessed using this {@link DBConfig}
     * instance, should be printed to the console.
     * 
     * @return true if the SQL queries should be printed to the console, false otherwise
     */
	public boolean isShowSQL()
	{
		return showSQL;
	}

    /**
     * Specifies if the SQL queries to the database, accessed using this {@link DBConfig} instance,
     * should be printed to the console.
     * <p>
     * 
     * By default, the SQL queries to the database are printed to console.
     * 
     * @param showSQL
     *            set to true if the SQL queries should be printed to the console, set to false
     *            otherwise
     */
	public void setShowSQL(boolean showSQL)
	{
		this.showSQL = showSQL;
	}

    /**
     * Returns the {@link String} instance representing the name of the accessed databases' host.
     * 
     * @return the name of the accessed databases' host or null if the name is not set
     * 
     * @see DBConfig
     */
	public String getHost() {
		return host;
	}

    /**
     * Sets the {@link String} instance representing the name of the accessed databases' host.
     * 
     * @param host
     *            the name of the accessed databases' host to set
     * 
     * @see DBConfig
     */
    public void setHost(String host)
    {
        this.host = host;
    }
    
    public String getDBType()
    {
    	if (db_vendor.equals("h2") || db_vendor.equals(UBYH2Dialect.class.getName())) {
    		return H2;
    	}
		else {
			return MYSQL;
    	}
    }
    
    public static final String H2 = "h2";
    public static final String MYSQL = "mysql";
}
