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

package de.tudarmstadt.ukp.lmf.transform;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;

public class LMFDBUtils {
	
	/**
	 * Create all LMF Tables in the database based on the hibernate mapping
	 * @param dbConfig
	 * @throws FileNotFoundException
	 */
	public static void createTables(DBConfig dbConfig) 
			throws FileNotFoundException{
		// public static  void createTables(DBConfig dbConfig/*, boolean constraints*/) 
		System.out.println("CREATE TABLES");
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SchemaExport  se = new SchemaExport(cfg);
		se.create(true, true);
		
		/*if (constraints) {
			turnOnConstraints(dbConfig);
		}*/
	}
	
	
	/**
	 * Create all LMF Tables in the database based on the hibernate mapping
	 * @param dbConfig
	 * @throws FileNotFoundException
	 */
	public static  void updateTables(DBConfig dbConfig) 
			throws FileNotFoundException{
		// public static  void updateTables(DBConfig dbConfig/*, boolean constraints*/)
		System.out.println("UPDATE TABLES");
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SchemaUpdate su = new SchemaUpdate(cfg);
		su.execute(true, true);
		/*if (constraints) {
			turnOnConstraints(dbConfig);
		}*/
	}
	
	public static void dropTables(final DBConfig dbConfig) {
		System.out.println("DROP TABLES");
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SessionFactory sf = cfg.buildSessionFactory(
				new ServiceRegistryBuilder().applySettings(
				cfg.getProperties()).buildServiceRegistry());
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		try {
			// Create a set of all tables.
			Set<String> dropSQL = new TreeSet<String>();
			Iterator<Table> iter = cfg.getTableMappings();
			while (iter.hasNext())
				dropSQL.add("DROP TABLE " + iter.next().getName());
		
			// Try to delete them repeatedly until no tables are left or 
			// there have been too many repetitions.
			int remainingLoops = dropSQL.size();
			while (!dropSQL.isEmpty() && remainingLoops >= 0) {
				Iterator<String> sqlIter = dropSQL.iterator(); 
				while (sqlIter.hasNext()) {
					try {
						String sql = sqlIter.next();
						session.createSQLQuery(sql).executeUpdate();
						sqlIter.remove();
						System.out.println(sql);
					} catch (HibernateException e) {}
				}
				remainingLoops--;
			}
		} finally {
			tx.commit();
			session.disconnect();
			session.close();
		}
	}

	public static void truncateTables(final DBConfig dbConfig) {
		System.out.println("TRUNCATE TABLES");
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SessionFactory sf = cfg.buildSessionFactory(
				new ServiceRegistryBuilder().applySettings(
				cfg.getProperties()).buildServiceRegistry());
		Session session = sf.openSession();
		try {
			session.createSQLQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
			Iterator<Table> iter = cfg.getTableMappings();
			while (iter.hasNext())
				session.createSQLQuery("TRUNCATE TABLE " + iter.next().getName()).executeUpdate();
		} finally {
			session.createSQLQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
			session.disconnect();
			session.close();
		}
	}
	
}
