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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Uby class represents the main entrance point to the UBY API.
 * It holds methods for searching of different UBY-LMF elements in a databse
 * containing a {@link LexicalResource}.
 *
 */
public class Uby
{

	private DBConfig dbConfig;
	private Configuration cfg;
	private SessionFactory sessionFactory;
	protected Session session;

	/**
	 * Constructor for a {@link Uby} instance used for
	 * searching for different elements in a database containing
	 * UBY-LMF {@link LexicalResource}.
	 * 
	 * The connection to the database is specified using a {@link DBConfig}
	 * instance.
	 *
	 * @param dbConfig configuration of the database containing
	 * UBY-LMF lexical resource. 
	 */
	public Uby(DBConfig dbConfig)
	{
		// TODO
//		if(dbConfig == null)
//			throw new UbyInvalidArgumentException("database configuration is null");
//		this.dbConfig = dbConfig;
		cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory();
		openSession();
	}

	/**
	 * Using this empty constructor, you have to set Value for parameter dbConfig afterwards.
	 * @deprecated marked for deletion, use {@link #Uby(DBConfig)} instead.
	 */
	@Deprecated
	public Uby(){
		//do nothing
	}

	/**
	 * Setting the configuration for the Uby database
	 *
	 * @param dbConfig Database configuration of the Uby database
	 * @throws FileNotFoundException
	 * @deprecated marked for deletion
	 */
	@Deprecated
	public void setDbConfig(DBConfig dbconfig) throws FileNotFoundException{
		this.dbConfig=dbconfig;
		cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory();
		openSession();
	}

	/**
	 * Returns the {@link DBConfig} instance used by this
	 * {@link Uby} instance to access the UBY-LMF database. 
	 * 
	 * @return  Database configuration of the Uby database
	 */
	public DBConfig getDbConfig(){
		return dbConfig;
	}

	public SessionFactory getSessionFactory(){
		return sessionFactory;
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

	/**
	 * Returns current hibernate database session
	 *
	 * @return  Current database session
	 */
	public Session getSession()
	{
		return session;
	}

	/**
	 * Fetches a LexicalResource from the database by its name. This method is private as it is only used internally
	 *
	 * @param Name of the LexicalResource to be fetched
	 * @return The LexicalResource
	 */
	private LexicalResource getLexicalResource(String name)
	{
		LexicalResource lexicalResource = (LexicalResource) session.get(
				LexicalResource.class, name);
		return lexicalResource;
	}

	/**
	 * Fetches the one Uby LexicalResource named "Uby" from the database. This should work if the database has been created correctly and is the recommended way to obtain the LexicalResource
	 *
	 * @return The LexicalResource Uby
	 */
	public LexicalResource getLexicalResource()
	{
		return this.getLexicalResource("Uby");
	}


	/**
	 * Fetches all lexicon names from the database
	 *
	 * @return a list of names of all Lexicons in Uby
	 */
	public List<String> getLexiconNames(){
		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.setProjection(Property.forName("name"));
		return criteria.list();
	}

	/**
	 * Fetches a Lexicon from the database by name. Possible values as of the first release are:
	 *
	 * FrameNet
	 * OmegaWikide
	 * OmegaWikien
	 * Wikipedia
	 * WikipediaDE
	 * WiktionaryEN
	 * WiktionaryDE
	 * VerbNet
	 * WordNet
	 *
	 * @param Name of the Lexicon to be obtained
	 * @return The Lexicon
	 * @throws UbyInvalidArgumentException if no lexicon with the given identifier is found
	 */
	public Lexicon getLexiconByName(String name) throws UbyInvalidArgumentException
	{

		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(Restrictions.sqlRestriction("lexiconName = '"+name+"'"));
		Lexicon result = (Lexicon) criteria.uniqueResult();
		if (result==null) {
			throw new UbyInvalidArgumentException(new Exception("Lexicon does not exist"));
		}
		return result;

	}

	/**
	 * Searches for lexical entries for given word. Optionally the words can be
	 * filtered by part-of-speech and lexicon
	 *
	 * @param word  Word to search
	 * @param pos  If not null, filters lexical entries by part-of-speech given by the Enum element
	 * @param lexicon If not null, filters lexical entries by lexicon. Note that the Lexicon object has to be obtained beforehand
	 * @return A list of LexicalEntry objects which mathc the criteria
	 */
	public List<LexicalEntry> getLexicalEntries(String word, EPartOfSpeech pos, Lexicon lexicon)
	{
		Criteria criteria = session.createCriteria(LexicalEntry.class);
		if (pos != null) {
			criteria = criteria.add(Restrictions.eq("partOfSpeech", pos));
		}

		if (lexicon != null) {
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId = '"
					+ lexicon.getId() + "'"));
		}

		criteria = criteria.createCriteria("lemma")
				.createCriteria("formRepresentations")
				.add(Restrictions.eq("writtenForm", word));
		return criteria.list();
	}

	/**
	 * Returns an iterator of all lexical entries, which can be optionally
	 * filtered by part-of-speech and lexicon
	 *	TODO: why not filter by lexiconName?
	 *
	 * @param pos
	 *            If not null, filters lexical entries by part-of-speech
	 * @param lexicon
	 *            If not null, filters lexical entries by lexicon
	 * @return
	 */
	public Iterator<LexicalEntry> getLexicalEntryIterator(EPartOfSpeech pos,
			Lexicon lexicon)
	{
		Criteria criteria = session.createCriteria(LexicalEntry.class);
		if (pos != null) {
			criteria = criteria.add(Restrictions.eq("partOfSpeech", pos));
		}
		if (lexicon != null) {
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId = '"
					+ lexicon.getId() + "'"));
		}

		CriteriaIterator<LexicalEntry> lexEntryIterator = new CriteriaIterator<LexicalEntry>(
				criteria, 10);
		return lexEntryIterator;
	}


	/**
	 * Fetches all Lexicons from the database
	 *
	 * @return
	 */
	public List<Lexicon> getLexicons()
	{
		Criteria criteria = session.createCriteria(Lexicon.class);
		return criteria.list();
	}

	/**
	 * This method just fetches lexiconId and lexiconName for each Lexicon in order<br>
	 * to speed up the loading process.
	 * @return
	 */
	public List<Lexicon> getLightLexicons(){
		List<Lexicon>lexicons=new ArrayList<Lexicon>();
		String sql="Select lexiconId,lexiconName from Lexicon";
		SQLQuery query = session.createSQLQuery(sql);
		Iterator iter = query.list().iterator();
		while (iter.hasNext()) {
			Object[] row = (Object[]) iter.next();
			Lexicon lexicon=new Lexicon();
			lexicon.setId((String)row[0]);
			lexicon.setName((String)row[1]);
			lexicons.add(lexicon);
		}
		return lexicons;
	}

	/**
	 * Fetches all Lexicons from the database by language
	 *
	 * @return
	 */
	public List<Lexicon> getLexiconsByLanguage(ELanguageIdentifier lang)
	{

		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(
				Restrictions.eq("languageIdentifier", lang));
		return criteria.list();
	}

	/**
	 * Returns iterator over all Senses, optionally filtered by lexicon
	 *
	 * @param lexicon
	 *            If not null, Senses are filtered by lexicon
	 * @return
	 */
	public Iterator<Sense> getSenseIterator(Lexicon lexicon)
	{
		Criteria criteria = session.createCriteria(Sense.class);
		if (lexicon != null) {
			criteria = criteria.createCriteria("lexicalEntry").add(
					Restrictions.eq("lexicon", lexicon));
		}
		CriteriaIterator<Sense> senseIterator = new CriteriaIterator<Sense>(
				criteria, 10);
		return senseIterator;
	}

	/**
	 * Returns iterator over all Synsets, optionally filtered by lexicon
	 *
	 * @param lexicon
	 *            If not null, Synsets are filtered by lexicon
	 * @return
	 */
	public Iterator<Synset> getSynsetIterator(Lexicon lexicon)
	{
		Criteria criteria = session.createCriteria(Synset.class);
		if (lexicon != null) {
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId = '"
					+ lexicon.getId() + "'"));
		}
		CriteriaIterator<Synset> synsetIterator = new CriteriaIterator<Synset>(
				criteria, 10);
		return synsetIterator;
	}

	/**
	 * @param externalSys: the string represents the name of External system,
	 *  such as: VerbNet, WordNet
	 * @param externalRef: the reference String from ExtSys,
	 * such as:
	 * 			 with verbnet: "retire_withdraw-82-3"
	 *           with wordnet: "bow_out%2:41:01::"
	 * @return
	 */
	public List<Sense> getSensesByOriginalReference(String externalSys, String externalRef)
	{
		Criteria criteria = session.createCriteria(Sense.class);

		criteria = criteria.createCriteria("monolingualExternalRefs").add(
				Restrictions.sqlRestriction("externalSystem like '%"
						+ externalSys + "%' and externalReference =\""+ externalRef + "\""));

		return criteria.list();
	}

	/**
	 *
	 *
	 * @param Lemma
	 * @return List of LexicalEntries with that lemma
	 * No longer supported, use getLexcialEntries() instead
	 */
	@Deprecated
	public List<LexicalEntry> getLexicalEntryByLemma(Lemma lemma){
		Criteria criteria = session.createCriteria(LexicalEntry.class);
		//lemma.

		if (lemma != null) {
			criteria = criteria.add(Restrictions.sqlRestriction("lexicalEntryId = '"
					+ lemma + "'"));
		}

		CriteriaIterator<LexicalEntry> lexEntryIterator = new CriteriaIterator<LexicalEntry>(
				criteria, 10);

		return null;
	}

	/**
	 * @return All SenseAxes in table SenseAxis
	 */
	public List<SenseAxis> getSenseAxis(){
		Criteria criteria = session.createCriteria(SenseAxis.class);
		return criteria.list();
	}

	/**
	 * This method find all senseAxis having senseAxisId like '%senseAxisId%'
	 * @param senseAxisId: the template of senseAxisId that you need to find.
	 * @return the list of senseAxis object to the condition
	 */
	public List<SenseAxis> getSenseAxisbyId(String senseAxisId){
		Criteria criteria= session.createCriteria(SenseAxis.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseAxisId like \"%"+ senseAxisId+"%\""));
		return criteria.list();
	}

	/**
	 * Retrieve sense axis by sense
	 * @param sense
	 * @return all pair (sense alignment) that contains this sense
	 */
	public List<SenseAxis> getSenseAxisBySense(Sense sense){
		if (sense!=null && sense.getId()!=null && !sense.getId().equals("")){
			Criteria criteria=session.createCriteria(SenseAxis.class);
			criteria=criteria.add(Restrictions.sqlRestriction("senseOneId=\""+sense.getId()+"\" or senseTwoId=\""+sense.getId()+"\""));
			return criteria.list();
		}
		else {
			return null;
		}
	}

	/**
	 *
	 * @param sense
	 *            : The Input Sense
	 * @return: List ID of all senses appear with input sense in senseAxis table
	 * @throws SQLException
	 */
	public List<String> getLightSenseAxisBySense(Sense sense)
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
				throw new SQLException();
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

	/**
	 * @param senseId: the pattern of senseId
	 * @return the list of senses that have id match to the given pattern.<br>
	 * This method is no longer supported, Please use getListSensesByIdPattern(...).
	 */
	@Deprecated
	public List<Sense> getListSensesbyId(String senseId){
		Criteria criteria= session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId like \"%"+ senseId+"%\""));
		return criteria.list();
	}

	/**
	 * @param senseId: the pattern of senseId
	 * @return the list of senses that have id match to the given pattern.
	 */
	public List<Sense> getListSensesbyIdPattern(String senseId){
		Criteria criteria= session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId like \"%"+ senseId+"%\""));
		return criteria.list();
	}

	/**
	 * This methods allows to retrieve senses by their exact id, if known
	 * @param senseId
	 * @return The sense with the specified ID
	 * @throws UbyInvalidArgumentException if a sense with this ID does not exist
	 */
	public Sense getSenseByExactId(String senseId) throws UbyInvalidArgumentException{
		Criteria criteria= session.createCriteria(Sense.class).add(Restrictions.sqlRestriction("senseId = \""+ senseId+"\""));
		Sense ret=null;
		if (criteria.list()!=null && criteria.list().size()>0){
			ret=(Sense)criteria.list().get(0);
		}
		if (ret==null) {
			throw new UbyInvalidArgumentException(new Exception("Sense with this ID does not exist"));
		}
		return ret;
	}


	/**
	 * This methods allows to retrieve synsets by their exact id, if known
	 * @param synsetId
	 * @return The synset with the specified ID
	 * @throws UbyInvalidArgumentException if a synset with this ID does not exist
	 */
	public Synset getSynsetByExactId(String synsetId) throws UbyInvalidArgumentException{
		Criteria criteria= session.createCriteria(Synset.class).add(Restrictions.sqlRestriction("synsetId = \""+ synsetId+"\""));
		Synset ret=null;
		if (criteria.list()!=null && criteria.list().size()>0){
			ret=(Synset)criteria.list().get(0);
		}
		if (ret==null) {
			throw new UbyInvalidArgumentException(new Exception("Synset with this ID does not exist"));
		}
		return ret;
	}

	/**
	 *
	 * @param extRef: ExternalReference of Sense, i.e: the original ID of sense in Wordnet
	 * @param POS: POS tag of sense. Valid values are: noun, verb, adverb, adjective.
	 * @return: List of senses (1-element-list) match input arguments.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Sense> getWNSensebyExtRef(String extRef, String POS) throws ClassNotFoundException, SQLException{

		String refId="[POS: noun] ";
		if (POS.equals("adjective")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (POS.equals("adverb")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (POS.equals("verb")){
			refId=refId.replaceAll("noun", "verb");
		}

		refId=refId+extRef;

		/*
		 * This direct query avoids the joining huge table done by using normal hibernate, while we just need the ID
		 */
		String sqlQueryString="SELECT synsetId FROM MonolingualExternalRef WHERE externalReference = '"+refId.trim() +"'";
		SQLQuery query = session.createSQLQuery(sqlQueryString);
		Iterator iter = query.list().iterator();
		String ss_id ="";
		while (iter.hasNext()) {
			Object[] row = (Object[]) iter.next();
			ss_id = (String) row[0];
		}

		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("synsetId='"+ss_id.trim()+"'"));
		return criteria.list();

	}

	/**
	 * @param WNSynsetId:for example 1740-n
	 * @return the list of senses belong to that synset
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Sense>getSensesByWNSynsetId(String WNSynsetId) throws ClassNotFoundException, SQLException{
		String[]temp=WNSynsetId.split("-");
		String refId="[POS: noun] ";

		if (temp[1].equals("a")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (temp[1].equals("r")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (temp[1].equals("v")){
			refId=refId.replaceAll("noun", "verb");
		}

		refId=refId+temp[0];
		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.createCriteria("synset").createCriteria("monolingualExternalRefs").add(Restrictions.sqlRestriction("externalReference='"+refId.trim()+"'"));
		return criteria.list();
	}

	/**
	 * @param OmegaWiki SynTransId
	 * @return list of senses in OmegaWiki by the SynTransId
	 */
	public List<Sense>getSensesByOWSynTransId(String SynTransId){
		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.eq("index",Integer.parseInt(SynTransId.trim())));
		return criteria.list();
	}

	/**
	 * @param POS: POS value=comment<br>
	 * 					 a  = adj;<br>
	 * 					 n  = noun;<br>
	 * 					 r  = adv<br>
	 * 					 v  = verb<br>
	 * @param SynsetOffset: offset value of Synset;
	 * @return list of senses belong to the given synset
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<Sense>getSensesByWNSynsetId(String POS, String SynsetOffset) throws ClassNotFoundException, SQLException{

		String refId="[POS: noun] ";
		if (POS.equals("a")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (POS.equals("r")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (POS.equals("v")){
			refId=refId.replaceAll("noun", "verb");
		}

		refId=refId+SynsetOffset;


		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.createCriteria("monolingualExternalRefs").add(Restrictions.sqlRestriction("externalReference='"+refId.trim()+"'"));

		return criteria.list();
	}

	/**
	 * Return all semantic labels for a particular sense
	 *
	 * @param senseId
	 * @return List of semantic labels associated with the given sense
	 */
	public List<SemanticLabel> getSemanticLabelbySenseId(String senseId){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId=\""+ senseId+"\""));
		return criteria.list();
	}

	/**
	 * Return all semantic labels for a particular sense and type
	 *
	 * @param senseId
	 * @param type
	 * @return list of semantic labels
	 */
	public List<SemanticLabel> getSemanticLabelbySenseIdbyType(String senseId, String type){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId=\""+ senseId +"\" and type =\""+type+"\""));
		return criteria.list();
	}

	/**
	 * Return the semantic predicate with the given Id
	 * @param predicateId
	 * @return semantic predicate
	 */
	public SemanticPredicate getSemanticPredicateByExactId(String predicateId){
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		criteria=criteria.add(Restrictions.sqlRestriction("semanticPredicateId=\""+predicateId+"\""));
		return (SemanticPredicate) criteria.uniqueResult();

	}

	/**
	 * Returns all semantic predicates, optionally filtered by lexicon
	 * @param lexicon
	 * @return list of semantic predicates
	 */
	public List<SemanticPredicate> getSemanticPredicates(Lexicon lexicon){
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		if (lexicon != null) {
			String lexId = lexicon.getId();
			criteria = criteria.add(
					Restrictions.sqlRestriction("lexiconId=\""+lexId+"\""));
		}
		return criteria.list();
	}

	/**
	 * Return iterator over semantic predicates, optionally filtered by lexicon
	 * @param lexicon
	 * @return iterator over semantic predicates
	 */
	public Iterator<SemanticPredicate> getSemanticPredicateIterator(Lexicon lexicon){
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		if (lexicon != null) {
			String lexId = lexicon.getId();
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId=\""+lexId+"\""));
		}
		CriteriaIterator<SemanticPredicate> predicateIterator = new CriteriaIterator<SemanticPredicate>(
				criteria, 10);
		return predicateIterator;
	}

	/**
	 * Return the semantic argument with the given Id
	 * @param argumentId
	 * @return semantic argument
	 */
	public SemanticArgument getSemanticArgumentByExactId(String argumentId){
		Criteria criteria = session.createCriteria(SemanticArgument.class);
		criteria=criteria.add(Restrictions.sqlRestriction("semanticArgumentId=\""+argumentId+"\""));
		return (SemanticArgument) criteria.uniqueResult();
	}

	/**
	 * Return all SynSemArgMaps
	 * @return A list of all SynSemArgMaps
	 */
    public List<SynSemArgMap> getSynSemArgMaps()
    {

        Criteria criteriaSynSem = session.createCriteria(SynSemArgMap.class);
        List<SynSemArgMap> result = criteriaSynSem.list();

        return result;
    }

	/**
	 * Returns the String describing a specific SubcatFrame
	 * @param frame The SubCatFraem
	 * @param yourLemma The lemma
	 * @return String describing the SubcatFrame
	 */
    public String getSubcatFrameString(SubcategorizationFrame frame, String yourLemma){
		StringBuilder sbFrame = new StringBuilder();
		List<String> arguments = new ArrayList<String>();
		for (int j = 0; j < frame.getSyntacticArguments().size(); j++) {
			SyntacticArgument arg = frame.getSyntacticArguments().get(j);
			StringBuilder sbArg = new StringBuilder();
			sbArg.append(arg.getGrammaticalFunction().toString() +"_" +arg.getSyntacticCategory().toString());

			List<String> additional = new ArrayList<String>();
			if (arg.getComplementizer() != null){
				additional.add(arg.getComplementizer().toString());
			}
			additional.add("isOptional=" +arg.isOptional());
			
			if (arg.getDeterminer() != null){
				additional.add(arg.getDeterminer().toString());
			}
			if (arg.getCase() != null){
				additional.add(arg.getCase().toString());
			}
			if (arg.getLexeme() != null){
				additional.add(arg.getLexeme());
			}
			if (arg.getNumber() != null){
				additional.add(arg.getNumber().toString());
			}
			if (arg.getPreposition() != null){
				additional.add(arg.getPreposition());
			}
			if (arg.getPrepositionType() != null){
				additional.add(arg.getPrepositionType());
			}
			if (arg.getTense() != null){
				additional.add(arg.getTense().toString());
			}
			if (arg.getVerbForm() != null){
				additional.add(arg.getVerbForm().toString());
			}
			if (additional.size() > 0) {
				String additionalAsString = join(additional,",");

				sbArg.append("(" +additionalAsString +")");
			}

			if (j == 0) {
				sbArg.append(" " +yourLemma);
			}
			arguments.add(sbArg.toString());
		}
		String argumentString = this.join(arguments," ");
		sbFrame.append(argumentString);
		if (frame.getLexemeProperty() != null){
			sbFrame.append(" - " +frame.getLexemeProperty().getSyntacticProperty().toString());
		}
		return sbFrame.toString();
	}

	/**
	 * Returns the String describing a specific SyntacticArgument
	 * @param arg The SyntacticArgument
	 * @return String describing the SyntacticArgument
	 */
	public String getArgumentString(SyntacticArgument arg){
			StringBuilder sbArg = new StringBuilder();
			sbArg.append(arg.getGrammaticalFunction().toString() +"_" +arg.getSyntacticCategory().toString());

			List<String> additional = new ArrayList<String>();
			if (arg.getComplementizer() != null){
				additional.add(arg.getComplementizer().toString());
			}
			
			additional.add("isOptional=" +arg.isOptional());
			
			
			if (arg.getDeterminer() != null){
				additional.add(arg.getDeterminer().toString());
			}
			if (arg.getCase() != null){
				additional.add(arg.getCase().toString());
			}
			if (arg.getLexeme() != null){
				additional.add(arg.getLexeme());
			}
			if (arg.getNumber() != null){
				additional.add(arg.getNumber().toString());
			}
			if (arg.getPreposition() != null){
				additional.add(arg.getPreposition());
			}
			if (arg.getPrepositionType() != null){
				additional.add(arg.getPrepositionType());
			}
			if (arg.getTense() != null){
				additional.add(arg.getTense().toString());
			}
			if (arg.getVerbForm() != null){
				additional.add(arg.getVerbForm().toString());
			}
			if (additional.size() > 0) {
				String additionalAsString = join(additional,",");

				sbArg.append("(" +additionalAsString +")");
			}

		return sbArg.toString();
	}

	/**
	 * Utility method for transforming a List of Strings into a String with delimiters
	 * @param list The list of Strings
	 * @param delimiter The delimiter to be used
	 * @return String containing the concatenated list
	 */

	public String join(List<String> list, String delimiter){
		if (list == null || list.isEmpty()) {
			return "";
		}
		Iterator<String> iter = list.iterator();
		StringBuilder builder = new StringBuilder(iter.next());
		while( iter.hasNext() ) {
		  builder.append(delimiter).append(iter.next());
		}
		return builder.toString();
	}

}
