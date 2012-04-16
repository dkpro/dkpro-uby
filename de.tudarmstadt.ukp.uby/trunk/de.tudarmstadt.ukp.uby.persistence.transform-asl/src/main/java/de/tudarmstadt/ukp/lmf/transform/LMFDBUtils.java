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

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;

public class LMFDBUtils {
	/**
	 * Create all LMF Tables in the database based on the hibernate mapping
	 * @param dbConfig
	 * @throws FileNotFoundException
	 */
	public static  void createTables(DBConfig dbConfig, boolean constraints) throws FileNotFoundException{
		System.out.println("CREATE TABLES");
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SchemaExport  se = new SchemaExport(cfg);
		se.create(true, true);
		if (constraints) {
			turnOnConstraints(dbConfig);
		}
	}


	/**
	 * Adds some constraints to the Uby database, that can not be automatically added by Hibernate
	 * @param dbConfig
	 */
	public static void turnOnConstraints(DBConfig dbConfig){
		try{
			Connection connection = DriverManager.getConnection("jdbc:"+dbConfig.getDb_vendor()+"://"
				+ dbConfig.getJdbc_url() + "?user="
				+ dbConfig.getUser() + "&password=" + dbConfig.getPassword()
				+ "&useUnicode=true");

			Statement stmt = connection.createStatement();

			String[] queries = { "alter table Lemma add constraint LexicalEntryConstraint  " +
					"foreign key (lemmaId) references LexicalEntry (lemmaId) on delete cascade",

					"alter table GlobalInformation add constraint LexicalResourceConstraint " +
					"foreign key (globalInformationId) references LexicalResource (globalInformationId) on delete cascade",

					"alter table LexemeProperty add constraint SubCatFrameConstraint " +
					"foreign key (lexemePropertyId) references SubcategorizationFrame (lexemePropertyId) on delete cascade",

					"alter table FormRepresentation_SenseRelation add constraint SenseRelationConstraint " +
					"foreign key (formRepresentationId) references SenseRelation (formRepresentationId) on delete cascade"
			};

			for(String query : queries){
				try{
					stmt.execute(query);
				}catch(SQLException ex){
					System.out.println("Turn on constraints: '"+query+"', "+ex.getMessage());
//					ex.printStackTrace();
				}
			}

				stmt.close();
		}catch(SQLException ex){
			System.out.println("Turn on constraints: "+ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println("Turned on constraints");
	}

	/**
	 * Drops constraints from the Uby database, that were added manually in turnOnConstraints(..)
	 * @param dbConfig
	 */
	public static void turnOffConstraints(DBConfig dbConfig){
		try{
			Connection connection = DriverManager.getConnection("jdbc:"+dbConfig.getDb_vendor()+"://"
					+ dbConfig.getJdbc_url() + "?user="
					+ dbConfig.getUser() + "&password=" + dbConfig.getPassword()
					+ "&useUnicode=true");
			Statement stmt = connection.createStatement();
			String[] queries = {"alter table Lemma drop foreign key LexicalEntryConstraint",
					"alter table GlobalInformation drop foreign key LexicalResourceConstraint",
					"alter table FormRepresentation_SenseRelation drop foreign key SenseRelationConstraint",
					"alter table LexemeProperty drop foreign key SubCatFrameConstraint"
			};

			for(String query : queries){
				try{
					stmt.execute(query);
				}catch(SQLException ex){
					ex.printStackTrace();
//					System.out.println("Turn off constraints: '"+query+"', "+ex.getMessage());
				}
			}
			stmt.close();
			System.out.println("Turned off constraints");
		}catch(SQLException ex){
//			System.out.println("Turn off constraints: "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Deletes LexicalResource and all its elements from the database
	 * @param lexicalResource
	 * @param dbConfig
	 */
	public static void deleteLexicalResourceFromDatabase(LexicalResource lexicalResource, DBConfig dbConfig){
		try{
			turnOnConstraints(dbConfig); // To be sure that all constraints are turned on
			 							 // and cascade deleting will work
			Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
			SessionFactory sf = cfg.buildSessionFactory();
			Session session = sf.openSession();
			Transaction tx = session.beginTransaction();

			String sql = "delete from LexicalResource where lexicalResourceId='"+lexicalResource.getName()+"'";
			System.out.println(sql);
			session.createQuery(sql).executeUpdate();

			tx.commit();
			session.close();
			System.out.println("deleted "+lexicalResource.getName());
			turnOffConstraints(dbConfig);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Deletes Lexicon and all its elements from the database
	 * @param lexicon
	 * @param dbConfig
	 * @throws FileNotFoundException
	 */
	public static void deleteLexiconFromDatabase(Lexicon lexicon, DBConfig dbConfig) throws FileNotFoundException{

		turnOnConstraints(dbConfig); // To be sure that all constraints are turned on
									 // and cascade deleting will work
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		SessionFactory sf = cfg.buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		String sql = "delete from Lexicon where lexiconId='"+lexicon.getId()+"'";
		System.out.println(sql);
		session.createQuery(sql).executeUpdate();

		tx.commit();
		session.close();
		System.out.println("deleted "+lexicon.getId());
	}
}
