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
package de.tudarmstadt.ukp.lmf.hibernate;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.cfg.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;

public class HibernateConnect {

	private static Logger logger = Logger.getLogger(HibernateConnect.class.getName());

	/**
	 * Creates Hibernate {@link Configuration} and
	 * adds all files from Hibernate mapping folder to the model
	 * @param dbConfig
	 * @return the created Hibernate Configuration
	 */
	public static Configuration getConfiguration(DBConfig dbConfig) {
		Configuration cfg = new Configuration().addProperties(getProperties(
				dbConfig.getJdbc_url(), dbConfig.getJdbc_driver_class(),
				dbConfig.getDb_vendor(), dbConfig.getUser(),
				dbConfig.getPassword(), dbConfig.isShowSQL()));

		// load hibernate mappings
		ClassLoader cl = HibernateConnect.class.getClassLoader();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
				cl);
		Resource[] mappings = null;
		try {
			if (dbConfig.isAccessMode()) {
				// load access mappings
				mappings = resolver
						.getResources("hibernatemap/access/**/*.hbm.xml");
			} else {
				// load transform (write) mappings
				mappings = resolver
						.getResources("hibernatemap/transform/**/*.hbm.xml");
			}

			for (Resource mapping : mappings) {
				cfg.addURL(mapping.getURL());
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, "Hibernate mappings not found!");
			e.printStackTrace();
		}

		return cfg;
	}


	/**
	 * Create Hibernate Properties
	 * @param host
	 * @param user
	 * @param password
	 * @param db
	 * @param showSQL
	 * @return
	 */
	public static Properties getProperties(String jdbc_url, String jdbc_driver_class, String db_vendor,String user, String password, boolean showSQL) {

        Properties p = new Properties();

        // Database connection settings
        p.setProperty("hibernate.connection.driver_class", jdbc_driver_class);
        p.setProperty("hibernate.connection.url", "jdbc:"+db_vendor+"://" +jdbc_url+"?characterEncoding=UTF-8&useUnicode=true");
        p.setProperty("hibernate.connection.characterEncoding", "UTF-8");
        p.setProperty("hibernate.connection.useUnicode", "true");
        p.setProperty("hibernate.connection.charSet", "UTF-8");
        p.setProperty("hibernate.connection.username", user);
        p.setProperty("hibernate.connection.password", password);

        // JDBC connection pool (use the built-in) -->
        //  p.setProperty("hibernate.connection.pool_size","1");

        //Using c3p0 instead now for better connection handling
        p.setProperty("hibernate.c3p0.min_size","1");
        p.setProperty("hibernate.c3p0.max_size","100");
        p.setProperty("hibernate.c3p0.timeout","0");
        p.setProperty("hibernate.c3p0.max_statements","500");
        p.setProperty("hibernate.c3p0.idle_test_period","5");

        // Custom SQL dialect
        p.setProperty("hibernate.dialect","de.tudarmstadt.ukp.lmf.hibernate.CustomMySQLDialect");

        // Enable Hibernate's automatic session context management
        p.setProperty("hibernate.current_session_context_class","thread");

        // Disable the second-level cache
        p.setProperty("hibernate.cache.provider_class","org.hibernate.cache.NoCacheProvider");
        //p.setProperty("hibernate.cache.provider_class","org.hibernate.connection.C3P0ConnectionProvider");
        p.setProperty("hibernate.order_inserts", "true");
        p.setProperty("hibernate.order_updates", "true");

        //p.setProperty("hibernate.cache.provider_class","org.hibernate.cache.OSCacheProvider");

        p.setProperty("hibernate.jdbc.batch_size", "100");
        p.setProperty("hibernate.cache.use_second_level_cache" , "false");

        p.setProperty("hibernate.cache.use_query_cache", "false");

        // Echo all executed SQL to stdout
        if(showSQL) {
			p.setProperty("hibernate.show_sql","true");
		}
		else {
			p.setProperty("hibernate.show_sql","false");
		}

        // Do only update schema on changes e.g. validate | update | create | create-drop
        p.setProperty("hibernate.hbm2ddl.auto","update");

          return p;
    }

	/**
	 * Returns all files from the folder and its subfolders
	 * @param folder
	 * @return
	 */
	public static Set<File> getAllFiles(File folder){
		Set<File> result = new HashSet<File>();
		if(folder.isFile() && folder.getName().endsWith(".hbm.xml")){
			result.add(folder);
		}else if(folder.isDirectory()){
	        for(File f : folder.listFiles()){
	        	result.addAll(getAllFiles(f));
	        }
		}
		return result;
	}

}