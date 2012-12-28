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

import de.tudarmstadt.ukp.lmf.exceptions.UbyInvalidArgumentException;
import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticArgument;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Uby class represents the main entrance point to the UBY API.
 * It holds methods for searching of different UBY-LMF elements in a database
 * containing a {@link LexicalResource}.<p>
 *
 * Methods of this class return fully initialized UBY-LMF class instances.
 * For performance reasons, you also may want to use {@link UbyQuickAPI}.
 *
 * @author Judith Eckle-Kohler
 * @author Michael Matuschek
 * @author Tri-Duc Nghiem
 * @author Silvana Hartmann
 * @author Zijad Maksuti
 *
 */
public class Uby
{

	protected DBConfig dbConfig;
	protected Configuration cfg;
	protected SessionFactory sessionFactory;
	protected Session session;

	/**
	 * Constructor for a {@link Uby} instance used for
	 * searching of different elements in a database containing
	 * UBY-LMF {@link LexicalResource}.
	 *
	 * The connection to the database is specified using a {@link DBConfig}
	 * instance.
	 *
	 * @param dbConfig configuration of the database containing
	 * UBY-LMF lexical resource.
	 * @throws UbyInvalidArgumentException if the specified dbConfig is null
	 */
	public Uby(DBConfig dbConfig) throws UbyInvalidArgumentException
	{
		if(dbConfig == null) {
			throw new UbyInvalidArgumentException("database configuration is null");
		}
		this.dbConfig = dbConfig;
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

	/**
	 *
	 * @deprecated this method is marked for deletion
	 */
	@Deprecated
	public SessionFactory getSessionFactory(){
		return sessionFactory;
	}

	/**
	 * Opens hibernate database session
	 *
	 * @deprecated marked for deletion
	 */
	@Deprecated
	public void openSession()
	{
		session = sessionFactory.openSession();
	}

	/**
	 * Closes hibernate database session
	 *
	 * @deprecated marked for deletion
	 */
	@Deprecated
	public void closeSession()
	{
		session.close();
	}

	/**
	 * Returns the hibernate {@link Session} of this {@link Uby} instance.
	 *
	 * @return the session created by this Uby instance
	 */
	public Session getSession()
	{
		return session;
	}

	/**
	 * Fetches a {@link LexicalResource} from the UBY-Database by its name.
	 *
	 * @param name the name of the lexical resource to be fetched
	 *
	 * @return the lexical resource with the specified name or null if the
	 * database accessed by this {@link Uby} instance does not contain a
	 * lexical resource with the specified name
	 *
	 * @see LexicalResource#getName()
	 */
	private LexicalResource getLexicalResource(String name)
	{
		LexicalResource lexicalResource = (LexicalResource) session.get(
				LexicalResource.class, name);
		return lexicalResource;
	}

	/**
	 * Fetches the one UBY-LMF {@link LexicalResource} instance named "Uby" from the database accessed by this {@link Uby}
	 * instance.
	 *
	 * This should work if the database has been created correctly and is the recommended way to obtain the UBY-LMF
	 * lexical resource.
	 *
	 * @return a lexical resource named "Uby", contained in the accessed database, or null if the database does not contain
	 * the lexical resource with the name "Uby"
	 */
	public LexicalResource getLexicalResource()
	{
		return this.getLexicalResource("Uby");
	}


	/**
	 * Fetches a {@link List} of names of all {@link Lexicon} instances contained in the database
	 * accessed by this {@link Uby} instance.
	 *
	 * @return a list of names of all lexicons contained in the accessed UBY-LMF database or an empty list
	 * if the database does not contain any lexicons
	 *
	 * @see Lexicon#getName()
	 */
	public List<String> getLexiconNames(){
		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.setProjection(Property.forName("name"));
		@SuppressWarnings("unchecked")
		List<String> result = criteria.list();

		if(result == null) {
			result = new ArrayList<String>(0);
		}

		return result;
	}

	/**
	 * Fetches a {@link Lexicon} with the specified name from the database accessed by this {@link Uby} instance.
	 *
	 * @param name the name of the Lexicon to be fetched.<p>
	 *
	 * Possible values of this argument are:<br>
	 *
	 * <list>
	 * <li>"FrameNet"</li>
	 * <li>"OmegaWikide"</li>
	 * <li>"OmegaWikien"</li>
	 * <li>"Wikipedia"</li>
	 * <li>"WikipediaDE"</li>
	 * <li>"WiktionaryEN"</li>
	 * <li>"WiktionaryDE"</li>
	 * <li>"VerbNet"</li>
	 * <li>"WordNet"</li>
	 * </list>
	 *
	 * @return the lexicon with the specified name
	 *
	 * @throws UbyInvalidArgumentException if no lexicon with the given name is found
	 *
	 * @see Lexicon#getName()
	 */
	public Lexicon getLexiconByName(String name) throws UbyInvalidArgumentException
	{
		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(Restrictions.sqlRestriction("lexiconName = '"+name+"'"));
		Lexicon result = (Lexicon) criteria.uniqueResult();
		if (result==null) {
			throw new UbyInvalidArgumentException("Database does not contain a lexicon with such name");
		}
		return result;

	}

    /**
     * Fetches a {@link List} of {@link LexicalEntry} instances which written representation is the specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param word the written representation of the lexical entries to be fetched
     *
     * @param lexicon If not null, filters lexical entries by the specified lexicon. Note that the Lexicon instance has to be
     * obtained beforehand.
     *
     * @return A list of lexical entries matching the specified criteria. If no lexical entry matches the specified
     * criteria, this method returns an empty list.
     *
     * @see LexicalEntry#getLemma()
     */
    public List<LexicalEntry> getLexicalEntries(String word, Lexicon lexicon)
    {
        return getLexicalEntries(word, null, lexicon);
    }	
	
	/**
	 * Fetches a {@link List} of {@link LexicalEntry} instances which written representation is the specified word.
	 *
	 * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
	 *
	 * @param word the written representation of the lexical entries to be fetched
	 *
	 * @param pos the part-of-speech of the lexical entries to be fetched. Set to null in order to skip
	 * part-of-speech filtering and fetch all lexical entries matching other constraints, regardless of their part-of-speech.
	 *
	 * @param lexicon If not null, filters lexical entries by the specified lexicon. Note that the Lexicon instance has to be
	 * obtained beforehand.
	 *
	 * @return A list of lexical entries matching the specified criteria. If no lexical entry matches the specified
	 * criteria, this method returns an empty list.
	 *
	 * @see EPartOfSpeech
	 * @see LexicalEntry#getLemma()
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

		@SuppressWarnings("unchecked")
		List<LexicalEntry> result = criteria.list();

		if(result == null) {
			result = new ArrayList<LexicalEntry>(0);
		}

		return result;
	}

    /**
     * Returns an {@link Iterator} over {@link LexicalEntry} instances which written representation is the specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param word the written representation of the lexical entries to be iterated over
     *
     * @param lexicon If not null, filters lexical entries by the specified lexicon. Note that the Lexicon instance has to be
     * obtained beforehand.
     *
     * @return An Iterator over lexical entries matching the specified criteria
     *
     * @see EPartOfSpeech
     * @see LexicalEntry#getLemma()
     */
    public Iterator<LexicalEntry> getLexicalEntryIterator(Lexicon lexicon)
    {
        return getLexicalEntryIterator(null, lexicon);
    }	
	
	/**
	 * Returns an {@link Iterator} over {@link LexicalEntry} instances which written representation is the specified word.
	 *
	 * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
	 *
	 * @param word the written representation of the lexical entries to be iterated over
	 *
	 * @param pos the part-of-speech of the lexical entries to be iterated over. Set to null in order to skip
	 * part-of-speech filtering and create an iterator over all lexical entries matching other constraints, regardless of
	 * their part-of-speech.
	 *
	 * @param lexicon If not null, filters lexical entries by the specified lexicon. Note that the Lexicon instance has to be
	 * obtained beforehand.
	 *
	 * @return An Iterator over lexical entries matching the specified criteria
	 *
	 * @see EPartOfSpeech
	 * @see LexicalEntry#getLemma()
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
	 * This methods allows retrieving a {@link LexicalEntry} instance by its exact
	 * identifier.
	 *
	 * @param lexicalEntryId
	 *            the unique identifier of the LexicalEntry which should be returned
	 *
	 * @return the LexicalEntry with the consumed lexicalEntryId
	 *
	 * @throws UbyInvalidArgumentException
	 *             if a LexicalEntry with this identifier does not exist
	 */
	public LexicalEntry getLexicalEntryById(String lexicalEntryId)
			throws UbyInvalidArgumentException {
		Criteria criteria = session.createCriteria(LexicalEntry.class).add(
				Restrictions.sqlRestriction("lexicalEntryId = \"" + lexicalEntryId + "\""));
		LexicalEntry ret = null;
		if (criteria.list() != null && criteria.list().size() > 0) {
			ret = (LexicalEntry) criteria.list().get(0);
		}
		if (ret == null) {
			throw new UbyInvalidArgumentException(
					"LexicalEntry with this ID does not exist");
		}
		return ret;
	}

	/**
	 * Retrieves a {@link List} of {@link LexicalEntry} instances with lemmas that start with the parameter lemma.
	 * E.g. lemma = "leave" -> LexicalEntry with lemma = "leave no stone unturned" is retrieved (among others)
	 *
	 * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
	 *
	 * @param lemma the lemma the lexical entries has to start with
	 *
	 * @param pos the part-of-speech of the lexical entries to be fetched. Set to null in order to skip
	 * part-of-speech filtering and fetch all lexical entries matching other constraints, regardless of their part-of-speech.
	 *
	 * @param lexicon If not null, filters lexical entries by the specified lexicon. Note that the Lexicon instance has to be
	 * obtained beforehand.
	 *
	 * @return A list of lexical entries matching the specified criteria. If no lexical entry matches the specified
	 * criteria, this method returns an empty list.
	 *
	 * @see EPartOfSpeech
	 * @see LexicalEntry#getLemma()
	 */
	public List<LexicalEntry> getLexicalEntriesByLemmaPrefix(String lemma, EPartOfSpeech pos, Lexicon lexicon)
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
			.add(Restrictions.or(
					Restrictions.sqlRestriction("writtenForm like '" +lemma +" %'"),
					Restrictions.eq( "writtenForm", lemma )
			));
		
		@SuppressWarnings("unchecked")
		List<LexicalEntry> result = criteria.list();
		if(result == null) {
			result = new ArrayList<LexicalEntry>(0);
		}
		return result;
	}

	
	/**
	 * Returns a {@link List} of all {@link Lexicon} instances contained in the database accessed by this
	 * {@link Uby} instance.
	 *
	 * @return a list of all lexicons contained in the database or an empty list if the
	 * database contains no lexicons
	 */
	public List<Lexicon> getLexicons()
	{
		Criteria criteria = session.createCriteria(Lexicon.class);
		@SuppressWarnings("unchecked")
		List<Lexicon> result = criteria.list();
		if(result == null) {
			result = new ArrayList<Lexicon>(0);
		}
		return result;
	}

	/**
	 * This method fetches a {@link List} of light {@link Lexicon} instances containing only
	 * the name and the id.<p>
	 *
	 * The method is meant for fast fetching of lexicons. In order to get complete
	 * Lexicon instances use {@link #getLexicons()} instead.
	 *
	 * @return a list of all lexicons contained in the database. The returned lexicons are light
	 * and consist only of an id and a name. If the accessed database does not contain any lexicons,
	 * this method returns an empty list.
	 *
	 * @see Lexicon#getName()
	 * @see Lexicon#getId()
	 * @deprecate use {@link UbyQuickAPI#lightLexicons()} instead
	 */
	@Deprecated
	public List<Lexicon> getLightLexicons(){
		List<Lexicon>lexicons=new ArrayList<Lexicon>();
		String sql="Select lexiconId,lexiconName from Lexicon";
		SQLQuery query = session.createSQLQuery(sql);
		Iterator<?> iter = query.list().iterator();
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
	 * This method fetches all {@link Lexicon} instances from the accessed database by the
	 * specified language identifier.
	 *
	 * @param lang the language identifier of the lexicons to be fetched
	 *
	 * @return A {@link List} of all lexicons with the specified language identifier.<br>
	 * This method returns an empty list if the specified identifier is null or the
	 * database accessed by this {@link Uby} instance does not contain any lexicon with the given identifier.
	 *
	 * @see ELanguageIdentifier
	 * @see Lexicon#getLanguageIdentifier()
	 *
	 */
	public List<Lexicon> getLexiconsByLanguage(ELanguageIdentifier lang)
	{

		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(
				Restrictions.eq("languageIdentifier", lang));
		@SuppressWarnings("unchecked")
		List<Lexicon> result =  criteria.list();
		if(result == null) {
			result = new ArrayList<Lexicon>(0);
		}
		return result;
	}

	/**
	 * Returns {@link Iterator} over all {@link Sense} instances contained in the database accessed
	 * by this {@link Uby} instance.<br>
	 * Optionally, the returned senses can be filtered by {@link Lexicon}.
	 *
	 * @param lexicon
	 *            If not null, senses are filtered by the given lexicon
	 *
	 * @return an iterator over all senses in the accessed database filtered by the given
	 * lexicon if not null
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
	 * Returns {@link Iterator} over all {@link Synset} instances contained in the database accessed
	 * by this {@link Uby} instance.<br>
	 * Optionally, the returned synsets can be filtered by {@link Lexicon}.
	 *
	 * @param lexicon
	 *            If not null, synsets are filtered by the given lexicon
	 *
	 * @return an iterator over all synsets in the accessed database filtered by the given
	 * lexicon if not null
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
	 * Returns the {@link Sense} instance contained in the database accessed by this
	 * {@link Uby} instance. The returned senses are filtered by the given
	 * name of the external system and external reference.
	 *
	 * @param externalSys the {@link String} representing the name of external system,
	 * such as "VerbNet" or "WordNet".
	 *
	 * @param externalRef the reference string from external system,
	 * such as:
	 * <list>
	 * 			 <li>with verbnet: "retire_withdraw-82-3"</li>
	 *           <li>with wordnet: "bow_out%2:41:01::"</li>
	 * </list>
	 *
	 * @return a {@link List} of all senses filtered by the given arguments or an empty list if
	 * if one of the given arguments is null or the accessed database does not contain any
	 * senses matching both constraints
	 */
	public List<Sense> getSensesByOriginalReference(String externalSys, String externalRef)
	{
		Criteria criteria = session.createCriteria(Sense.class);

		criteria = criteria.createCriteria("monolingualExternalRefs").add(
				Restrictions.sqlRestriction("externalSystem like '%"
						+ externalSys + "%' and externalReference =\""+ externalRef + "\""));

		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();

		if(result == null) {
			result = new ArrayList<Sense>(0);
		}
		return result;
	}

	/**
	 * Returns a {@link List} of all {@link SenseAxis} instances contained in the database
	 * accessed by this {@link Uby} instnace.
	 *
	 * @return a list of all sense axes in the accessed database or an empty list
	 * if the accessed database does not contain any sense axes
	 */
	public List<SenseAxis> getSenseAxis(){
		Criteria criteria = session.createCriteria(SenseAxis.class);
		@SuppressWarnings("unchecked")
		List<SenseAxis> result = criteria.list();
		if(result == null) {
			result = new ArrayList<SenseAxis>(0);
		}
		return result;
	}

	/**
	 * This method finds all {@link SenseAxis} instances which id contains the specified {@link String} in
	 * the database accessed by this {@link Uby} instance.
	 *
	 * @param senseAxisId string contained in the identifiers of the sense axes to be returned
	 *
	 * @return the {@link List} of all sense axes which id contains the specified string.<br>
	 * This method returns an empty list is no sense axis contains the specified string in its id
	 * or the specified string is null.
	 *
	 * @see #getSenseAxis()
	 * @see #getSenseAxisBySense(Sense)
	 * @see #getSenseAxisBySenseID(String)
	 *
	 */
	public List<SenseAxis> getSenseAxisbyId(String senseAxisId){
		Criteria criteria= session.createCriteria(SenseAxis.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseAxisId like \"%"+ senseAxisId+"%\""));

		@SuppressWarnings("unchecked")
		List<SenseAxis> result =  criteria.list();
		if(result == null) {
			result = new ArrayList<SenseAxis>(0);
		}
		return result;
	}

	/**
	 * This method retrieves all {@link SenseAxis} which bind the specified {@link Sense}.
	 *
	 * @param sense all returned sense axes should bind this sense
	 *
	 * @return all sense axes (sense alignments) that contain the consumed sense.<br>
	 * This method returns an empty list if the accessed UBY-LMF database does not contain
	 * any alignments of the specified sense, or the specified sense is null.
	 */
	public List<SenseAxis> getSenseAxisBySense(Sense sense){
		if (sense!=null && sense.getId()!=null && !sense.getId().equals("")){
			Criteria criteria=session.createCriteria(SenseAxis.class);
			criteria=criteria.add(Restrictions.sqlRestriction("senseOneId=\""+sense.getId()+"\" or senseTwoId=\""+sense.getId()+"\""));
			@SuppressWarnings("unchecked")
			List<SenseAxis> result =  criteria.list();
			return result;
		}
		else {
			return new ArrayList<SenseAxis>(0);
		}
	}

	/**
	 *
	 * @param sense
	 *            : The Input Sense
	 * @return: List ID of all senses appear with input sense in senseAxis table
	 * @throws SQLException
	 * @deprecated use {@link UbyQuickAPI#alignedSenseIDs(Sense)} instead
	 */
	@Deprecated
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

				@SuppressWarnings("rawtypes")
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
	 * This method fetches a {@link List} of all identifiers of {@link Sense}
	 * instances which are aligned by a {@link SenseAxis} with the sense
	 * specified by its identifier.
	 * <p>
	 *
	 * The method is meant for fast fetching of alignments. For retrieving of
	 * complete alignments use {@link #getSenseAxisBySense(Sense)} instead.
	 *
	 * @param id
	 *            all returned identifiers must belong to senses which are
	 *            aligned to the sense represented by the id
	 *
	 * @return a list of identifiers of all senses which are aligned with the
	 *         specified sense by a sense axis.<br>
	 *         If the sense specified by its identifier is not contained in any
	 *         alignment or the specified id is null, this method returns an
	 *         empty list.
	 *         
	 * @deprecated use {@link UbyQuickAPI#alignedSenseIDs(String)}
	 *
	 */
	@Deprecated
	public List<String> getSenseAxisBySenseID(String id) {
		List<String> list = new ArrayList<String>();
		if (id != null && !id.equals("")) {
			// Select senseOneId, senseTwoId from SenseAxis where
			// senseOneId='WN_Sense_100' or senseTwoId='WN_Sense_100'
			String sql = "Select senseOneId, senseTwoId from SenseAxis where senseOneId='"
					+ id + "' or senseTwoId='" + id + "'";
			@SuppressWarnings("rawtypes")
			List query = session.createSQLQuery(sql).list();
			@SuppressWarnings("rawtypes")
			Iterator iter = query.iterator();
			while (iter.hasNext()) {
				Object[] row = (Object[]) iter.next();
				String sense1 = (String) row[0];
				String sense2 = (String) row[1];
				if (sense1.matches(id)) {
					list.add(sense2);
				} else {
					list.add(sense1);
				}
			}
		}
		return list;
	}

	/**
	 * Consumes two {@link Sense} instances and returns true if and only if the
	 * consumed instances are aligned by a {@link SenseAxis} instance.
	 * <p>
	 *
	 * @param sense1
	 *            the sense to be checked for alignment with
	 *            sense2
	 * @param sense2
	 *            the sense to be checked for alignment with
	 *            sense1
	 *
	 * @return true if and only if sense1 has an alignment to sense2 by a sense
	 *         axis instance so that sense1 is the first sense of a sense axis
	 *         and sense2 the second.<br>
	 *         This method returns false if one of the consumed senses is null.
	 *
	 * @see SenseAxis#getSenseOne()
	 * @see SenseAxis#getSenseTwo()
	 */

	public boolean areSensesAxes(Sense sense1, Sense sense2) {
		boolean ret = false;
		if (sense1 != null && sense2 != null && sense1.getId() != null
				&& sense1.getId().length() > 0 && sense2.getId() != null
				&& sense2.getId().length() > 0) {
			String sql = "Select senseOneId, senseTwoId from SenseAxis where "
					+ "(senseOneId='" + sense1.getId() + "' and senseTwoId='"
					+ sense2.getId() + "')" + "or(senseOneId='"
					+ sense2.getId() + "' and senseTwoId='" + sense1.getId()
					+ "')";

			List<?> query = session.createSQLQuery(sql).list();
			if (query.size() > 0) {
				ret = true;
			}

		}

		return ret;
	}

	/**
	 * Consumes a {@link List} of {@link Sense} instances and returns a List of
	 * all {@link SenseAxis} instances aligning senses from the consumed list.
	 * In particular, every returned sense axis aligns two senses from the
	 * consumed list.
	 *
	 * @param listSense
	 *            A list of senses for which the sense alignments should be
	 *            returned.
	 *            <br>
	 *            Note that sense instances contained in the list must not be
	 *            fully initialized. It sufficient to provide a list of senses
	 *            where each sense only has its unique identifier set.
	 *
	 * @return a list of sense alignments available from the input list.
	 *         <p>
	 *         If no sense alignments are available, this method returns an
	 *         empty list.
	 *         
	 * @deprecated use {@link UbyQuickAPI#lightSenseAxes(List)} instead
	 */
	@Deprecated
	public List<SenseAxis> getSensesAxis(List<Sense> listSense) {
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

		Query query = session.createSQLQuery(sql);
		Iterator<?> iter = query.list().iterator();
		while (iter.hasNext()) {
			Object[] rows = (Object[]) iter.next();

			SenseAxis sa = new SenseAxis();
			Sense sense1 = getSenseFromList(listSense, (String) rows[0]);
			Sense sense2 = getSenseFromList(listSense, (String) rows[1]);
			sa.setSenseOne(sense1);
			sa.setSenseTwo(sense2);

			senseAxes.add(sa);
		}
		return senseAxes;
	}

	/**
	 * Consumes a {@link List} of unique identifiers of {@link Sense} instances
	 * and returns a List of all {@link SenseAxis} instances aligning senses
	 * which identifiers are in the consumed list. In particular, every returned
	 * sense axis aligns two senses which unique identifiers are in the consumed
	 * list.
	 *
	 * @param listSenseId
	 *            A list of sense identifiers for which the sense alignments
	 *            should be returned.
	 *
	 * @return a list of sense alignments available from the input list.
	 *         <p>
	 *         If no sense alignments are available, this method returns an
	 *         empty list.
	 *
	 * @see #getSensesAxis(List)
	 * 
	 * @deprecated {@link UbyQuickAPI#lightSenseAxesBySenseIDs(List)}
	 */
	@Deprecated
	public List<SenseAxis> getSensesAxisbyListSenseId(List<String> listSenseId) {
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

		Query query = session.createSQLQuery(sql);
		Iterator<?> iter = query.list().iterator();
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
		return senseAxes;
	}

	/**
	 * Consumes a {@link List} of {@link Sense} instances and a {@link String}
	 * representing the unique identifier of a sense. It returns the sense from
	 * the consumed list which unique identifier is equal to the consumed
	 * identifier.
	 *
	 * @param senses
	 *            a list of sense to be searched in
	 *
	 * @param senseId
	 *            the unique identifier of the searched sense
	 *
	 * @return the sense in the consumed list which unique identifier matches
	 *         the consumed unique identifier or null if the list does not
	 *         contain such sense
	 */
	protected Sense getSenseFromList(List<Sense> senses, String senseId) {
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
	 * Returns a {@link List} of all {@link Sense} instances which unique
	 * identifier contains the consumed {@link String}.
	 *
	 * @param idPattern
	 *            the pattern which identifiers of the returned senses must
	 *            contain
	 *
	 * @return the list of all senses which unique identifier contains the
	 *         idPattern
	 *         <p>
	 *         If none of the senses contained in the UBY-Database accessed by
	 *         this {@link Uby} instance contains the consumed pattern this
	 *         method returns an empty list.
	 */
	public List<Sense> getListSensesbyIdPattern(String idPattern) {
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.add(Restrictions.sqlRestriction("senseId like \"%"
				+ idPattern + "%\""));
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

	/**
	 * This methods allows retrieving a {@link Sense} instance by its exact
	 * identifier.
	 *
	 * @param senseId
	 *            the unique identifier of the sense which should be returned
	 *
	 * @return the sense with the consumed senseId
	 *
	 * @throws UbyInvalidArgumentException
	 *             if a sense with this identifier does not exist
	 */
	public Sense getSenseByExactId(String senseId)
			throws UbyInvalidArgumentException {
		Criteria criteria = session.createCriteria(Sense.class).add(
				Restrictions.sqlRestriction("senseId = \"" + senseId + "\""));
		Sense ret = null;
		if (criteria.list() != null && criteria.list().size() > 0) {
			ret = (Sense) criteria.list().get(0);
		}
		if (ret == null) {
			throw new UbyInvalidArgumentException(
					"Sense with this ID does not exist");
		}
		return ret;
	}


	/**
	 * This methods allows retrieving a {@link Synset} instance by its exact
	 * identifier.
	 *
	 * @param synsetId
	 *            the unique identifier of the synset which should be returned
	 *
	 * @return the synset with the consumed senseId
	 *
	 * @throws UbyInvalidArgumentException
	 *             if a synset with this identifier does not exist
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
	 * @deprecated use {@link #wordNetSenses(String, String)} or
	 * {@link #wordNetSense(String, String)} instead
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Sense> getWNSensebyExtRef(String offset, String POS) throws ClassNotFoundException, SQLException{

		String refId="[POS: noun] ";
		if (POS.equals("adjective")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (POS.equals("adverb")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (POS.equals("verb")){
			refId=refId.replaceAll("noun", "verb");
		}

		refId=refId+offset;

		/*
		 * This direct query avoids the joining huge table done by using normal hibernate, while we just need the ID
		 */
		String sqlQueryString="SELECT synsetId FROM MonolingualExternalRef WHERE externalReference = '"+refId.trim() +"'";
		SQLQuery query = session.createSQLQuery(sqlQueryString);
		@SuppressWarnings("rawtypes")
		Iterator iter = query.list().iterator();
		String ss_id ="";
		while (iter.hasNext()) {
			ss_id = (String) iter.next();
		}

		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("synsetId='"+ss_id.trim()+"'"));
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
	 * @Deprecated use {@link #wordNetSenses(String, String)} or
	 * {@link #wordNetSense(String, String)} instead
	 */
	@Deprecated
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
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

	/**
	 * Consumes a synset identifier (in WordNet terminology) and returns
	 * a {@link List} of {@link Sense} instances which are derived from the WordNets synset,
	 * specified by the consumed identifier.
	 *
	 * @param WNSynsetId string representation of the WordNets synset identifier
	 * i.e. "1740-n"
	 *
	 * @return a list of senses derived from the WordNets synset, specified by the consumed identifier
	 * <p>
	 * This method returns an empty list if the database accessed by this {@link Uby} instance does not contain
	 * senses derived from the specified WordNet synset.
	 * 
	 * @deprecated use {@link #wordNetSense(String, String)} and {@link #wordNetSense(String, String)}
	 * instead
	 */
	@Deprecated
	public List<Sense>getSensesByWNSynsetId(String wnSynsetId) {
		String[]temp=wnSynsetId.split("-");
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
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

	/**
	 * Consumes a synset offset (in WordNet terminology) and a part-of-speech. Returns
	 * a {@link List} of all {@link Sense} instances which are derived from the WordNets synset, identified
	 * by the consumed arguments.
	 *
	 * @param partOfSpeech a string describing part of speech of the senses to be returned.<p>
	 * Valid values are:
	 * <list>
	 * <li>"noun"</li>
	 * <li>"verb"</li>
	 * <li>"adverb"</li>
	 * <li>"adjective"</li>
	 * </list>
	 * 
	 * @param offset string representation of the WordNets synset offset i.e. "14469014"
	 *
	 * @return senses derived from the WordNets synset, described by the consumed arguments
	 * <p>
	 * This method returns an empty list if the database accessed by this {@link Uby} instance does not contain
	 * the specified sense.
	 * 
	 * @throws UbyInvalidArgumentException if the specified part of speech is not valid or
	 * one of consumed arguments is null
	 * 
	 * @since 0.2.0
	 */
	public List<Sense> wordNetSenses(String partOfSpeech, String offset) throws UbyInvalidArgumentException {
		
		if(partOfSpeech == null) {
            throw new UbyInvalidArgumentException("partOfSpeech is null");
        }
		
		if(offset == null) {
            throw new UbyInvalidArgumentException("offset is null");
        }

		String refId="[POS: noun] ";
		if (partOfSpeech.equals("adjective")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (partOfSpeech.equals("adverb")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (partOfSpeech.equals("verb")){
			refId=refId.replaceAll("noun", "verb");
		}else if (!partOfSpeech.equals("noun")) {
            throw new UbyInvalidArgumentException(
            		"\""+partOfSpeech+"\""+
            		" is not a valid part of speech. Only \"noun\", \"verb\", \"adverb\" or \"adjective\" are allowed"
            		);
        }

		refId=refId+offset;

		/*
		 * This direct query avoids the joining huge table done by using normal hibernate, while we just need the ID
		 */
		String sqlQueryString="SELECT synsetId FROM MonolingualExternalRef WHERE externalReference = '"+refId.trim() +"'";
		SQLQuery query = session.createSQLQuery(sqlQueryString);
		String ss_id = (String) query.uniqueResult();
		if(ss_id == null) {
			return new ArrayList<Sense>(0);
		}

		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("synsetId='"+ss_id.trim()+"'"));
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}
	
	/**
	 * Consumes a sense key (in WordNet terminology) and a part-of-speech. Returns
	 * a {@link Sense} instance which is derived from the WordNets sense, identified by
	 * the consumed arguments.
	 *
	 * @param partOfSpeech a string describing part of speech of the sense to be returned.<p>
	 * Valid values are:
	 * <list>
	 * <li>"noun"</li>
	 * <li>"verb"</li>
	 * <li>"adverb"</li>
	 * <li>"adjective"</li>
	 * </list>
	 * 
	 * @param senseKey string representation of the WordNets identifier of a sense
	 * i.e. "enter%2:33:00::"
	 *
	 * @return UBY-LMF sense derived from the WordNets word, described by the consumed arguments
	 * <p>
	 * This method returns null if the database accessed by this {@link Uby} instance does not contain
	 * the specified sense.
	 * 
	 * @throws UbyInvalidArgumentException if the specified part of speech is not valid or
	 * one of the consumed arguments is null
	 * 
	 * @since 0.2.0
	 */
	public Sense wordNetSense(String partOfSpeech, String senseKey) throws UbyInvalidArgumentException {
		
		if(partOfSpeech == null) {
            throw new UbyInvalidArgumentException("partOfSpeech is null");
        }
		
		if(senseKey == null) {
            throw new UbyInvalidArgumentException("senseKey is null");
        }

		String refId="[POS: noun] ";
		if (partOfSpeech.equals("adjective")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (partOfSpeech.equals("adverb")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (partOfSpeech.equals("verb")){
			refId=refId.replaceAll("noun", "verb");
		}
		else if(!partOfSpeech.equals("noun")) {
            throw new UbyInvalidArgumentException(
            		"\""+partOfSpeech+"\""+
            		" is not a valid part of speech. Only \"noun\", \"verb\", \"adverb\" or \"adjective\" are allowed"
            		);
        }

		refId=refId+senseKey;

		/*
		 * This direct query avoids the joining huge table done by using normal hibernate, while we just need the ID
		 */
		String sqlQueryString="SELECT senseId FROM MonolingualExternalRef WHERE externalReference = '"+refId.trim() +"'";
		SQLQuery query = session.createSQLQuery(sqlQueryString);
		String ss_id = (String) query.uniqueResult();
		if(ss_id == null) {
			return null;
		}

		Criteria criteria=session.createCriteria(Sense.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId='"+ss_id.trim()+"'"));

		Sense result = (Sense) criteria.uniqueResult();
		return result;
	}

	/**
	 * Consumes an identifier of a SynTrans (in OmegaWiki terminology) and
	 * returns a {@link List} of all {@link Sense} instances derived from the
	 * specified SynTrans.
	 * <br>
	 * A SynTrans in OmegaWiki corresponds to a sense in WordNet. As OmegaWikis
	 * senses are not ordered by frequency, the otherwise unused index field is
	 * used to store the original SynTransId, hence making the additional join
	 * with {@link MonolingualExternalRef} unnecessary.
	 * 
	 * @param synTransId
	 *            a {@link String} representation of a unique identifier of
	 *            OmegaWikis SynTrans
	 * 
	 * @return list of all senses derived from specified OmegaWikis SynTrans.
	 *         <br>
	 *         This method returns an empty list if a SynTrans with specified
	 *         identifier does not exist or the database accessed by this
	 *         {@link Uby} instance does not contain a OmegaWiki {@link Lexicon}.
	 * 
	 */
	public List<Sense> getSensesByOWSynTransId(String synTransId) {
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.add(Restrictions.eq("index",
				Integer.parseInt(synTransId.trim())));
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

	/**
	 * Consumes a unique identifier of a {@link Sense} instance and returns
	 * a {@link List} of all {@link SemanticLabel} instances associated to the
	 * specified sense.
	 *
	 * @param senseId a unique identifier of the sense for which
	 * semantic labels should be returned
	 *
	 * @return a list of all semantic labels of the specified sense or an empty list
	 * if a sense with such identifier does not exist or the sense does not
	 * have any associated semantic labels
	 */
	public List<SemanticLabel> getSemanticLabelbySenseId(String senseId){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId=\""+ senseId+"\""));
		@SuppressWarnings("unchecked")
		List<SemanticLabel> result = criteria.list();
		return result;
	}

	/**
	 * Consumes a unique identifier of a {@link Sense} instance and returns
	 * a {@link List} of all {@link SemanticLabel} instances associated to the
	 * specified sense. The returned semantic labels are filtered by the specified
	 * type.
	 *
	 *
	 * @param senseId a unique identifier of the sense for which
	 * semantic labels should be returned
	 *
	 * @param type returned semantic labels must have this type
	 *
	 * @return a list of all semantic labels of the specified sense filtered by the
	 * type or an empty list if the database accessed by this {@link Uby} instance
	 * does not contain any semantic labels matching the criteria
	 */
	public List<SemanticLabel> getSemanticLabelbySenseIdbyType(String senseId, String type){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId=\""+ senseId +"\" and type =\""+type+"\""));
		@SuppressWarnings("unchecked")
		List<SemanticLabel> result = criteria.list();
		return result;
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
	 * Returns a {@link List} of all {@link SemanticPredicate} instances in the
	 * database accessed by this {@link Uby} instance, optionally filtered by
	 * {@link Lexicon}.
	 *
	 * @param lexicon
	 *            if not null, all returned semantic predicates will belong to
	 *            the specified lexicon
	 * @return list of semantic predicates which matches the criteria or an
	 *         empty list if none of the semantic predicates matches the
	 *         criteria
	 */
	public List<SemanticPredicate> getSemanticPredicates(Lexicon lexicon) {
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		if (lexicon != null) {
			String lexId = lexicon.getId();
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId=\""
					+ lexId + "\""));
		}
		@SuppressWarnings("unchecked")
		List<SemanticPredicate> result = criteria.list();
		return result;
	}

	/**
	 * Return an {@link Iterator} over {@link SemanticPredicate} instances,
	 * optionally filtered by a {@link Lexicon}.
	 *
	 * @param lexicon
	 *            if not null, the iterator will only be for semantic predicates
	 *            of the specified lexicon
	 *
	 * @return iterator over the semantic predicates in the specified lexicon.<br>
	 *         If the specified lexicon is null, this method returns an iterator
	 *         over all semantic predicates in the {@link LexicalResource},
	 *         accessed by this {@link Uby} instance.
	 */
	public Iterator<SemanticPredicate> getSemanticPredicateIterator(
			Lexicon lexicon) {
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		if (lexicon != null) {
			String lexId = lexicon.getId();
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId=\""
					+ lexId + "\""));
		}
		CriteriaIterator<SemanticPredicate> predicateIterator = new CriteriaIterator<SemanticPredicate>(
				criteria, 10);
		return predicateIterator;
	}

	/**
	 * Returns the {@link SemanticArgument} instance with the specified unique identifier.
	 *
	 * @param argumentId the unique identifier of the semantic argument to be returned
	 *
	 * @return semantic argument with the specified unique identifier, contained in the database
	 * accessed by this {@link Uby} instance.<br>
	 * If a semantic argument with the specified identifier does not exist, this method
	 * return null.
	 */
	public SemanticArgument getSemanticArgumentByExactId(String argumentId){
		Criteria criteria = session.createCriteria(SemanticArgument.class);
		criteria=criteria.add(Restrictions.sqlRestriction("semanticArgumentId=\""+argumentId+"\""));
		return (SemanticArgument) criteria.uniqueResult();
	}

	/**
	 * Returns all {@link SynSemArgMap} instances contained in the database
	 * accessed by this {@link Uby} instance.
	 *
	 * @return a list of all mappings between syntactic and semantic arguments.<br>
	 * If the database does not contain any mappings, this method returns an empty list.
	 */
    public List<SynSemArgMap> getSynSemArgMaps()
    {

        Criteria criteriaSynSem = session.createCriteria(SynSemArgMap.class);
        @SuppressWarnings("unchecked")
		List<SynSemArgMap> result = criteriaSynSem.list();

        return result;
    }

	/**
	 * Returns the String describing a specific {@link SubcategorizationFrame} instance which occurs with the
	 * specified lemma. It omits the field of subcategorization frame which are set to null when creating
	 * the pretty print.
	 * 
	 * @param frame the subcategorization frame to be printed 
	 * @param yourLemma the {@link String} representation of the lemma which occurs with the consumed {@link SubcategorizationFrame}.
	 * 
	 * @return string representing the consumed subcategorization frame with the specified lemma
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
				String additionalAsString = concat(additional,",");

				sbArg.append("(" +additionalAsString +")");
			}

			if (j == 0) {
				sbArg.append(" " +yourLemma);
			}
			arguments.add(sbArg.toString());
		}
		String argumentString = this.concat(arguments," ");
		sbFrame.append(argumentString);
		if (frame.getLexemeProperty() != null){
			sbFrame.append(" - " +frame.getLexemeProperty().getSyntacticProperty().toString());
		}
		return sbFrame.toString();
	}

	/**
	 * Returns the {@link String} describing a specific {@link SyntacticArgument} instance.
	 *
	 * @param arg the syntactic argument for which the string representation should be returned
	 *
	 * @return string representation of the consumed syntactic argument
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
				String additionalAsString = concat(additional,",");

				sbArg.append("(" +additionalAsString +")");
			}

		return sbArg.toString();
	}

	/**
	 * Utility method for transforming a {@link List} of {@link String} instances into a
	 * String with delimiters.
	 *
	 * @param list the list of strings
	 * @param delimiter the delimiter to be used
	 * @return string containing the concatenated list
	 * @deprecated marked for deletion
	 */
	@Deprecated
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

	/**
	 * Utility method for transforming a {@link List} of {@link String} instances into a
	 * String with delimiters.
	 *
	 * @param list the list of strings
	 * @param delimiter the delimiter to be used
	 * @return string containing the concatenated list
	 */
	private String concat(List<String> list, String delimiter){
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
	
	@Override
    protected void finalize()
		throws Throwable
	{
		dbConfig = null;
		session.close();
	}

}
