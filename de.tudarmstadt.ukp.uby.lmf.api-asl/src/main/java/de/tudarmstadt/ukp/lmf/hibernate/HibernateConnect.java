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
package de.tudarmstadt.ukp.lmf.hibernate;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * This class offers methods for conecting to a database.
 *
 * @author Yevgen Chebotar
 * @author Zijad Maksuti
 *
 */
public class HibernateConnect
{
	private static Log logger = LogFactory.getLog(HibernateConnect.class.getName());

    /**
     * Creates Hibernate {@link Configuration} and adds all files from Hibernate mapping folder to
     * the model.
     *
     * @param dbConfig
     *            database configuration holder
     *
     * @return the created Hibernate Configuration
     */
    public static Configuration getConfiguration(DBConfig dbConfig)
    {
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
			mappings = resolver
				.getResources("hibernatemap/access/**/*.hbm.xml");
			for (Resource mapping : mappings) {
				cfg.addURL(mapping.getURL());
			}

		} catch (IOException e) {
			logger.error("Hibernate mappings not found!");
			e.printStackTrace();
		}

		return cfg;
	}


    /**
     * This method creates and returns Hibernate Properties.
     *
     * @param jdbc_url
     *            Host_to_the_database/database_name
     * @param jdbc_driver_class
     *            driver used to connect
     * @param db_vendor
     *            database vendor
     * @param user
     *            user name
     * @param password
     *            password
     * @param showSQL
     *            set to true in order to print all SQL-queries to the console
     *
     * @return hibernate properties based on the consumed parameters
     *
     * @see Properties
     */
    public static Properties getProperties(String jdbc_url, String jdbc_driver_class,
            String db_vendor, String user, String password, boolean showSQL)
    {
        Properties p = new Properties();
        /*
         *         <property name="driverClassName" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
    </bean>

    <bean id="jpaAdaptor" class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
        <property name="showSql" value="false" />
        <!-- Let Hibernate generate the DDL for the schema -->
        <property name="generateDdl" value="true" />
        <property name="databasePlatform" value="org.hibernate.dialect.H2Dialect" />

         */

        // Database connection settings common for mysql and h2
        p.setProperty("hibernate.connection.driver_class", jdbc_driver_class);
        p.setProperty("hibernate.connection.characterEncoding", "UTF-8");
        p.setProperty("hibernate.connection.useUnicode", "true");
        p.setProperty("hibernate.connection.charSet", "UTF-8");
        p.setProperty("hibernate.connection.username", user);
        p.setProperty("hibernate.connection.password", password);

        // connection url
        if (!jdbc_url.startsWith("jdbc:")) {
	        if (db_vendor.equals("mysql")) {
	        	p.setProperty("hibernate.connection.url", "jdbc:"+db_vendor+"://" +jdbc_url+"?characterEncoding=UTF-8&useUnicode=true");
	        } else if (db_vendor.equals("h2")){
	        	p.setProperty("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
	        }
        }
        else {
        	p.setProperty("hibernate.connection.url", jdbc_url);
        }


        // JDBC connection pool (use the built-in) -->
        //  p.setProperty("hibernate.connection.pool_size","1");

        //Using c3p0 instead now for better connection handling
        p.setProperty("hibernate.c3p0.min_size","1");
        p.setProperty("hibernate.c3p0.max_size","1");
        p.setProperty("hibernate.c3p0.timeout","0");
        p.setProperty("hibernate.c3p0.max_statements","0");
        p.setProperty("hibernate.c3p0.idle_test_period","5");

        // SQL dialect
        if (db_vendor.equals("mysql")) {
            p.setProperty("hibernate.dialect", UBYMySQLDialect.class.getName());
        }
        else if (db_vendor.equals("h2")) {
            p.setProperty("hibernate.dialect", UBYH2Dialect.class.getName());
        }
        else {
            p.setProperty("hibernate.dialect", db_vendor);
        }

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
//        p.setProperty("hibernate.hbm2ddl.auto","update");

        // JEK see http://stackoverflow.com/questions/3179765/how-to-turn-off-hbm2ddl
        p.setProperty("hibernate.hbm2ddl.auto","validate");
//        if (db_vendor.equals("mysql")) {
//        	p.setProperty("hibernate.hbm2ddl.auto","validate");
//        } else if (db_vendor.equals("h2")) {
//        	p.setProperty("hibernate.hbm2ddl.auto","update");
//        }
        // p.setProperty("hibernate.hbm2ddl.auto","none");

        return p;
    }

    /**
     * Returns all files from the folder and its subfolders
     *
     * @deprecated this method is marked for deletion
     */
	@Deprecated
    public static Set<File> getAllFiles(File folder)
    {
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
