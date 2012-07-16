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
package de.tudarmstadt.ukp.lmf.api;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * This class represents an extension of the {@link Uby} class and offers additional
 * methods for quick searching of different UBY-LMF elements in a database
 * containing a {@link LexicalResource}.<p>
 * For performance reasons, the methods offered by this class do not return
 * fully initialized Uby-LMF class instances.
 * 
 * @author Silvana Hartmann
 * @author Zijad Maksuti
 *
 */
public class UbyQuickAPI
{
	/**
	 * This UBY API provides some methods that directly connects to database.
	 * All Information extract from database is raw (i.e not full Object's
	 * information like the UBY API provided).
	 */
	private DBConfig dbconfig;
	private Session session;
	private Configuration cfg;
	private SessionFactory sessionFactory;

//	private MySQLConnect mysql;

	private boolean useHibernate;
	private boolean useTemporaryTables;

	/**
	 * Using this constructor, you have to call setDbConfig before using any
	 * method.
	 */
	public UbyQuickAPI()
	{
		// TO-DO nothing
	}

	/**
	 *
	 * @param config
	 *            : Configuration to connect to database
	 * @throws FileNotFoundException
	 */
	public UbyQuickAPI(DBConfig config) throws FileNotFoundException
	{
		setDbConfig(config);
	}

	/**
	 *
	 * @param config: database's configuration
	 * @param useHibernate: false for direct access; true connect via Hibernate
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public UbyQuickAPI(DBConfig config, boolean useHibernate,boolean useTempTables) throws ClassNotFoundException, SQLException, FileNotFoundException{
		setDBConfig(config,useHibernate,useTempTables);
	}

	/**
	 *
	 * @param session
	 *            Session of hibernate connection. In case you don't run query
	 *            directly
	 *
	 */
	public UbyQuickAPI(Session session)
	{
		this.session = session;
	}

	/**
	 *
	 * @param sf
	 */
	public UbyQuickAPI(SessionFactory sf){
		sessionFactory=sf;
		session=sessionFactory.openSession();
	}

	/**
	 *
	 * @param config
	 *            : Configuration to connect to database
	 * @throws FileNotFoundException
	 */
	public void setDbConfig(DBConfig config) throws FileNotFoundException
	{
		this.dbconfig = config;
		cfg = HibernateConnect.getConfiguration(dbconfig);
		sessionFactory = cfg.buildSessionFactory();
		openSession();
		useHibernate=false;
	}

	/**
	 *
	 * @param config: Database's Configuration
	 * @param useHibernate: true if you want to connect to database via Hibernate; false for direct access
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws FileNotFoundException
	 *
	 */
	public void setDBConfig(DBConfig config,boolean useHibernate,boolean useTemporaryTables) throws ClassNotFoundException, SQLException, FileNotFoundException{
		this.dbconfig = config;
		this.useHibernate=useHibernate;
		if(useHibernate){
			setDbConfig(config);
		}/*else{
			this.useTemporaryTables=useTemporaryTables;
			mysql=new MySQLConnect(config,this.useTemporaryTables);
		}*/
	}

	/**
	 *
	 * @return the database configuration object. See class DBConfig.
	 */
	public DBConfig getDBConfig()
	{
		return this.dbconfig;
	}

	/**
	 * Opens hibernate database session
	 */
	public void openSession()
	{
		session = sessionFactory.openSession();
	}

	/**
	 * Closes hibernate database session
	 */
	public void closeSession()
	{
		session.close();
	}

	public List<Lexicon> getLexicons() throws SQLException{
		List<Lexicon>lexicons=new ArrayList<Lexicon>();
		String sql="Select lexiconId,lexiconName from Lexicon";
	/*	if(mysql!=null){
			ResultSet rs=mysql.execute(sql);
			if (rs!=null){
				while (rs.next()){
					Lexicon lexicon=new Lexicon();
					lexicon.setId(rs.getString("lexiconId"));
					lexicon.setName(rs.getString("lexiconName"));
					lexicons.add(lexicon);
				}
			}
		}else*/ if(session!=null){
			SQLQuery query = session.createSQLQuery(sql);
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				Object[] row = (Object[]) iter.next();
				Lexicon lexicon=new Lexicon();
				lexicon.setId((String)row[0]);
				lexicon.setName((String)row[1]);
				lexicons.add(lexicon);
			}
		}
		return lexicons;
	}

	/**
	 * TODO: this function just provides a part of objects' information
	 * @param lemma
	 * @param Postag
	 * @param LexiconName
	 * @return
	 * @throws SQLException
	 */
/*	public List<LexicalEntry> getLexicalEntries(String lemma, String Postag,String LexiconName) throws SQLException{
		List<LexicalEntry>lexicons=new ArrayList<LexicalEntry>();
		String sql="Select lexicalEntryId,partOfSpeech,separableParticle,lemmaId,lexiconId,inflection,singularetantum,pluraletantum,listOfComponentsId from ";
		ResultSet rs=mysql.execute(sql);
		if (rs!=null){
			while (rs.next()){
				Lexicon lx=new Lexicon();
				lx.setId(rs.getString("lexiconId"));
				lx.setName(rs.getString("lexiconName"));

				Lemma lm=new Lemma();
				//lemma.setFormRepresentations(formRepresentations);

				LexicalEntry le=new LexicalEntry();
				le.setId(rs.getString("lexicalEntryId"));

				le.setLexicon(lx);
				le.setLemma(lm);

			}
		}
		return lexicons;
	}*/
	/*d
	 * This part for getting SenseAlignment in SenseAxis table
	 */
	/**
	 *
	 * @param sense
	 *            : The Input Sense
	 * @return: List ID of all senses appear with input sense in senseAxis table
	 * @throws SQLException
	 */
	public List<String> getSenseAxisBySense(Sense sense)
		throws SQLException
	{
		List<String> list = new ArrayList<String>();

		String id = sense.getId();
		if (id != null && !id.equals("")) {
			// Select senseOneId, senseTwoId from SenseAxis where
			// senseOneId='WN_Sense_100' or senseTwoId='WN_Sense_100'
			String sql = "Select senseOneId, senseTwoId from SenseAxis where senseOneId='"
					+ id + "' or senseTwoId='" + id + "'";
			try {
				// use Hibernate query
				SQLQuery query = session.createSQLQuery(sql);

				Iterator iter = query.list().iterator();
				while (iter.hasNext()) {
					Object[] row = (Object[]) iter.next();
					String sense1 = (String) row[0];
					String sense2 = (String) row[1];
					if (sense1.matches(id)) {
						list.add(sense2);
					}
					else {
						list.add(sense1);
					}
				}
			}
			catch (Exception ex) {
				throw new SQLException(
						"Please set configuration or session before using any method");
			}
		}

		return list;
	}

	/**
	 *
	 * @param id
	 *            : Id of the input Sense
	 * @return same to the method getSenseAxisBySense(Sense)
	 * @throws SQLException
	 */
	public List<String> getSenseAxisBySenseID(String id)
		throws SQLException
	{
		List<String> list = new ArrayList<String>();
		if (id != null && !id.equals("")) {
			// Select senseOneId, senseTwoId from SenseAxis where
			// senseOneId='WN_Sense_100' or senseTwoId='WN_Sense_100'
			String sql = "Select senseOneId, senseTwoId from SenseAxis where senseOneId='"
					+ id + "' or senseTwoId='" + id + "'";
			try {
				List query = session.createSQLQuery(sql).list();
				Iterator iter = query.iterator();
				while (iter.hasNext()) {
					Object[] row = (Object[]) iter.next();
					String sense1 = (String) row[0];
					String sense2 = (String) row[1];
					if (sense1.matches(id)) {
						list.add(sense2);
					}
					else {
						list.add(sense1);
					}
				}
			}
			catch (Exception ex) {
				throw new SQLException(
						"Please set configuration or session before using any method");
			}
		}
		return list;
	}

	/**
	 *
	 * @param sense1
	 * @param sense2
	 * @return true: if sense1 and sense 2 have alignment, vice verse false will
	 *         be output.
	 */

	public boolean areSensesAxes(Sense sense1, Sense sense2)
	{
		boolean ret = false;
		if (sense1 != null && sense2 != null && sense1.getId() != null
				&& sense1.getId().length() > 0 && sense2.getId() != null
				&& sense2.getId().length() > 0) {
			String sql = "Select senseOneId, senseTwoId from SenseAxis where "
					+ "(senseOneId='" + sense1.getId() + "' and senseTwoId='"
					+ sense2.getId() + "')" + "or(senseOneId='"
					+ sense2.getId() + "' and senseTwoId='" + sense1.getId()
					+ "')";

			try {
				List query = session.createSQLQuery(sql).list();
				if (query.size() > 0) {
					ret = true;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ret;

	}

	/**
	 *
	 * @param listSense
	 *            :list of all senses need to detect sense alignment. <br>
	 *            Just senseId is enough for each sense (no need to full fill
	 *            with all information)
	 * @return List of sense alignment available from the input list.
	 */
	public List<SenseAxis> getSensesAxis(List<Sense> listSense)
	{
		String list = "";
		List<SenseAxis> senseAxes = new ArrayList<SenseAxis>();

		for (Sense sense : listSense) {
			list += "'" + sense.getId() + "',";
		}
		if (list.endsWith(",")) {
			list = list.substring(0, list.length() - 1);
		}
		String sql = "Select senseOneId,senseTwoId from SenseAxis where senseOneId in ("
				+ list + ") and senseTwoId in (" + list + ")";
		try {

			Query query = session.createSQLQuery(sql);
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				Object[] rows = (Object[]) iter.next();

				SenseAxis sa = new SenseAxis();
				Sense sense1 = getSenseFromList(listSense, (String) rows[0]);
				Sense sense2 = getSenseFromList(listSense, (String) rows[1]);
				sa.setSenseOne(sense1);
				sa.setSenseTwo(sense2);

				senseAxes.add(sa);
			}

		}
		catch (Exception ex) {
			System.out.println(sql);
			ex.printStackTrace();
		}
		return senseAxes;
	}

	/**
	 * Similar to function getSensesAxis.
	 *
	 * @param listSenseId
	 *            : List senses'id need to detect alignment among them.
	 * @return list of SenseAxis, i.e: available alignment(s) among the input
	 *         list senses.
	 */
	public List<SenseAxis> getSensesAxisbyListSenseId(List<String> listSenseId)
	{
		String list = "";
		List<SenseAxis> senseAxes = new ArrayList<SenseAxis>();

		for (String senseId : listSenseId) {
			list += "'" + senseId + "',";
		}
		if (list.endsWith(",")) {
			list = list.substring(0, list.length() - 1);
		}
		String sql = "Select senseOneId,senseTwoId from SenseAxis where senseOneId in ("
				+ list + ") and senseTwoId in (" + list + ")";
		try {
			Query query = session.createSQLQuery(sql);
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				Object[] rows = (Object[]) iter.next();
					SenseAxis sa = new SenseAxis();
					Sense sense1 = new Sense();
					Sense sense2 = new Sense();
					sense1.setId((String) rows[0]);
					sense2.setId((String) rows[1]);
					sa.setSenseOne(sense1);
					sa.setSenseTwo(sense2);
					senseAxes.add(sa);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return senseAxes;
	}

	/**
	 *
	 * @param senses
	 *            : List of Senses
	 * @param senseId
	 *            : Id of returned sense
	 * @return null if not found.<br>
	 *         else the sense with given ID.
	 */
	private Sense getSenseFromList(List<Sense> senses, String senseId)
	{
		Sense sense = null;
		for (Sense s : senses) {
			if (s.getId().equals(senseId)) {
				sense = s;
				break;
			}
		}
		return sense;
	}



	@Override
	protected void finalize()
		throws Throwable
	{
		dbconfig = null;
		closeSession();
	}
}
