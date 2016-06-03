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
package de.tudarmstadt.ukp.lmf.api;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistryBuilder;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticArgument;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemArgMap;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Uby class represents the main entrance point to the UBY API. It holds methods for searching of
 * different UBY-LMF elements in a database containing a {@link LexicalResource}.
 * <p>
 *
 * Methods of this class return fully initialized UBY-LMF class instances. For performance reasons,
 * you also may want to use {@link UbyQuickAPI}.
 *
 * @author Judith Eckle-Kohler
 * @author Michael Matuschek
 * @author Christian M. Meyer
 * @author Tri-Duc Nghiem
 * @author Silvana Hartmann
 * @author Zijad Maksuti
 */
public class Uby
{
	protected DBConfig dbConfig;
	protected Configuration cfg;
	protected SessionFactory sessionFactory;
	protected Session session;

    /**
     * Constructor for a {@link Uby} instance used for searching of different elements in a database
     * containing UBY-LMF {@link LexicalResource}.
     *
     * The connection to the database is specified using a {@link DBConfig} instance.
     *
     * @param dbConfig
     *            configuration of the database containing UBY-LMF lexical resource.
     * @throws UbyInvalidArgumentException
     *             if the specified dbConfig is null
     */
	public Uby(DBConfig dbConfig) throws IllegalArgumentException
	{
		if(dbConfig == null) {
			throw new IllegalArgumentException("database configuration is null");
		}
		this.dbConfig = dbConfig;
		cfg = HibernateConnect.getConfiguration(dbConfig);
		ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder()
				.applySettings(cfg.getProperties());
		sessionFactory = cfg.buildSessionFactory(serviceRegistryBuilder.buildServiceRegistry());
		openSession();
	}

    /**
     * Using this empty constructor, you have to set Value for parameter dbConfig afterwards.
     *
     * @deprecated marked for deletion, use {@link #Uby(DBConfig)} instead.
     */
	@Deprecated
	public Uby(){
		//do nothing
	}

    /**
     * Setting the configuration for the Uby database
     *
     * @param dbconfig
     *            Database configuration of the Uby database
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
     * Returns the {@link DBConfig} instance used by this {@link Uby} instance to access the UBY-LMF
     * database.
     *
     * @return Database configuration of the Uby database
     */
	public DBConfig getDbConfig(){
		return dbConfig;
	}

    /**
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
     * Closes Hibernate database session
     *
     * @deprecated marked for deletion
     */
	@Deprecated
	public void closeSession()
	{
		session.close();
	}

    /**
     * Returns the Hibernate {@link Session} of this {@link Uby} instance.
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
     * @param name
     *            the name of the lexical resource to be fetched
     *
     * @return the lexical resource with the specified name or null if the database accessed by this
     *         {@link Uby} instance does not contain a lexical resource with the specified name
     *
     * @see LexicalResource#getName()
     */
	public LexicalResource getLexicalResource(String name)
	{
		LexicalResource lexicalResource = (LexicalResource) session.get(
				LexicalResource.class, name);
		return lexicalResource;
	}

    /**
     * Fetches the one UBY-LMF {@link LexicalResource} instance named "Uby" from the database
     * accessed by this {@link Uby} instance.
     *
     * This should work if the database has been created correctly and is the recommended way to
     * obtain the UBY-LMF lexical resource.
     *
     * @return a lexical resource named "Uby", contained in the accessed database, or null if the
     *         database does not contain the lexical resource with the name "Uby"
     */
	public LexicalResource getLexicalResource()
	{
		return this.getLexicalResource("Uby");
	}


    /**
     * Fetches a {@link List} of names of all {@link Lexicon} instances contained in the database
     * accessed by this {@link Uby} instance.
     *
     * @return a list of names of all lexicons contained in the accessed UBY-LMF database or an
     *         empty list if the database does not contain any lexicons
     *
     * @see Lexicon#getName()
     */
    public List<String> getLexiconNames()
    {
        Criteria criteria = session.createCriteria(Lexicon.class);
        criteria = criteria.setProjection(Property.forName("name"));
        @SuppressWarnings("unchecked")
        List<String> result = criteria.list();

        if (result == null) {
            result = new ArrayList<String>(0);
        }

        return result;
    }

    /**
     * Fetches a {@link Lexicon} with the specified name from the database accessed by this
     * {@link Uby} instance.
     *
     * @param lexiconName
     *            the name of the Lexicon to be fetched. Possible values of this argument are:
     *            <ul>
     *            <li>"FrameNet"</li>
     *            <li>"OmegaWikide"</li>
     *            <li>"OmegaWikien"</li>
     *            <li>"Wikipedia"</li>
     *            <li>"WikipediaDE"</li>
     *            <li>"WiktionaryEN"</li>
     *            <li>"WiktionaryDE"</li>
     *            <li>"VerbNet"</li>
     *            <li>"WordNet"</li>
     *            </ul>
     * @return the lexicon with the specified name
     * @throws UbyInvalidArgumentException
     *             if no lexicon with the given name is found
     * @see Lexicon#getName()
     */
	public Lexicon getLexiconByName(String lexiconName) throws IllegalArgumentException
	{
		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(Restrictions.eq("name", lexiconName));
		Lexicon result = (Lexicon) criteria.uniqueResult();
		if (result==null) {
			throw new IllegalArgumentException("Database does not contain a lexicon called " +lexiconName);
		}
		return result;

	}

    /**
     * Fetches a {@link Lexicon} with the specified name from the database accessed by this
     * {@link Uby} instance.
     *
     * @param lexiconId
     *            the id of the Lexicon to be fetched. 
     * @return the lexicon with the specified lexiconId
     * @throws UbyInvalidArgumentException
     *             if no lexicon with the given name is found
     * @see Lexicon#getId()
     */
	public Lexicon getLexiconById(String lexiconId) throws IllegalArgumentException
	{
		Criteria criteria = session.createCriteria(Lexicon.class);
		criteria = criteria.add(Restrictions.eq("id", lexiconId));
		Lexicon result = (Lexicon) criteria.uniqueResult();
		if (result==null) {
			throw new IllegalArgumentException("Database does not contain a lexicon with id " +lexiconId);
		}
		return result;

	}
    /**
     * Fetches a {@link List} of {@link LexicalEntry} instances which written representation is the
     * specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param word
     *            the written representation of the lexical entries to be fetched
     * @param lexicon
     *            If not null, filters lexical entries by the specified lexicon. Note that the
     *            Lexicon instance has to be obtained beforehand.
     * @return A list of lexical entries matching the specified criteria. If no lexical entry
     *         matches the specified criteria, this method returns an empty list.
     * @see LexicalEntry#getLemma()
     */
    public List<LexicalEntry> getLexicalEntries(String word, Lexicon lexicon)
    {
        return getLexicalEntries(word, null, lexicon);
    }

    /**
     * Fetches a {@link List} of {@link LexicalEntry} instances which written representation is the
     * specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param word
     *            the written representation of the lexical entries to be fetched
     * @param pos
     *            the part-of-speech of the lexical entries to be fetched. Set to null in order to
     *            skip part-of-speech filtering and fetch all lexical entries matching other
     *            constraints, regardless of their part-of-speech.
     * @param lexicon
     *            If not null, filters lexical entries by the specified lexicon. Note that the
     *            Lexicon instance has to be obtained beforehand.
     * @return A list of lexical entries matching the specified criteria. If no lexical entry
     *         matches the specified criteria, this method returns an empty list.
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
			criteria = criteria.add(Restrictions.eq("lexicon", lexicon));
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
     * Returns an {@link Iterator} over {@link LexicalEntry} instances which written representation
     * is the specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param lexicon
     *            If not null, filters lexical entries by the specified lexicon. Note that the
     *            Lexicon instance has to be obtained beforehand.
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
     * Returns an {@link Iterator} over {@link LexicalEntry} instances which written representation
     * is the specified word.
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param pos
     *            the part-of-speech of the lexical entries to be iterated over. Set to null in
     *            order to skip part-of-speech filtering and create an iterator over all lexical
     *            entries matching other constraints, regardless of their part-of-speech.
     * @param lexicon
     *            If not null, filters lexical entries by the specified lexicon. Note that the
     *            Lexicon instance has to be obtained beforehand.
     * @return An Iterator over lexical entries matching the specified criteria
     *
     * @see EPartOfSpeech
     * @see LexicalEntry#getLemma()
     */
	public Iterator<LexicalEntry> getLexicalEntryIterator(EPartOfSpeech pos,
			Lexicon lexicon)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(LexicalEntry.class);
		if (pos != null) {
			criteria = criteria.add(Restrictions.eq("partOfSpeech", pos));
		}
		if (lexicon != null) {
			criteria = criteria.add(Restrictions.eq("lexicon", lexicon));
		}

		CriteriaIterator<LexicalEntry> lexEntryIterator = new CriteriaIterator<LexicalEntry>(
				criteria, sessionFactory, 500);
		return lexEntryIterator;
	}

    /**
     * Returns an {@link Iterator} over {@link SenseAxis} instances
     *
     * @return An iterator over sense axes matching the specified criteria
     */
	public Iterator<SenseAxis> getSenseAxisIterator()
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(SenseAxis.class);

		CriteriaIterator<SenseAxis> senseAxisIterator = new CriteriaIterator<SenseAxis>(
				criteria, sessionFactory, 500);
		return senseAxisIterator;
	}

    /**
     * This methods allows retrieving a {@link LexicalEntry} instance by its exact identifier.
     *
     * @param lexicalEntryId
     *            the unique identifier of the LexicalEntry which should be returned
     * @return the LexicalEntry with the consumed lexicalEntryId
     * @throws UbyInvalidArgumentException
     *             if a LexicalEntry with this identifier does not exist
     */
	public LexicalEntry getLexicalEntryById(String lexicalEntryId)
			throws IllegalArgumentException {
		Criteria criteria = session.createCriteria(LexicalEntry.class).add(
				Restrictions.eq("id", lexicalEntryId));
		LexicalEntry ret = null;
		if (criteria.list() != null && criteria.list().size() > 0) {
			ret = (LexicalEntry) criteria.list().get(0);
		}
		if (ret == null) {
			throw new IllegalArgumentException(
					"LexicalEntry with the ID " +lexicalEntryId +" does not exist");
		}
		return ret;
	}

    /**
     * Retrieves a {@link List} of {@link LexicalEntry} instances with lemmas that start with the
     * parameter lemma. E.g. lemma = "leave" -> LexicalEntry with lemma = "leave no stone unturned"
     * is retrieved (among others)
     *
     * Optionally lexical entries can be filtered by part-of-speech and a {@link Lexicon}.
     *
     * @param lemma
     *            the lemma the lexical entries has to start with
     * @param pos
     *            the part-of-speech of the lexical entries to be fetched. Set to null in order to
     *            skip part-of-speech filtering and fetch all lexical entries matching other
     *            constraints, regardless of their part-of-speech.
     * @param lexicon
     *            If not null, filters lexical entries by the specified lexicon. Note that the
     *            Lexicon instance has to be obtained beforehand.
     * @return A list of lexical entries matching the specified criteria. If no lexical entry
     *         matches the specified criteria, this method returns an empty list.
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
			criteria = criteria.add(Restrictions.eq("lexicon", lexicon));
		}
		criteria = criteria.createCriteria("lemma")
			.createCriteria("formRepresentations")
			.add(Restrictions.like("writtenForm", lemma + "%"));

		@SuppressWarnings("unchecked")
		List<LexicalEntry> result = criteria.list();
		if(result == null) {
			result = new ArrayList<LexicalEntry>(0);
		}
		return result;
	}

    /**
     * Returns a {@link List} of all {@link Lexicon} instances contained in the database accessed by
     * this {@link Uby} instance.
     *
     * @return a list of all lexicons contained in the database or an empty list if the database
     *         contains no lexicons
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
     * This method fetches all {@link Lexicon} instances from the accessed database by the specified
     * language identifier.
     *
     * @param lang
     *            the language identifier of the lexicons to be fetched
     *
     * @return A {@link List} of all lexicons with the specified language identifier.<br>
     *         This method returns an empty list if the specified identifier is null or the database
     *         accessed by this {@link Uby} instance does not contain any lexicon with the given
     *         identifier.
     *
     * @see ELanguageIdentifier
     * @see Lexicon#getLanguageIdentifier()
     */
	//TODO LanguageIdentifier is now a String
	public List<Lexicon> getLexiconsByLanguage(String lang)
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
     * @return an iterator over all senses in the accessed database filtered by the given lexicon if
     *         not null
     */
	public Iterator<Sense> getSenseIterator(Lexicon lexicon)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Sense.class);
		if (lexicon != null) {
			criteria = criteria.createCriteria("lexicalEntry").add(
					Restrictions.eq("lexicon", lexicon));
		}
		CriteriaIterator<Sense> senseIterator = new CriteriaIterator<Sense>(
				criteria, sessionFactory, 500);
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
     * @return an iterator over all synsets in the accessed database filtered by the given lexicon
     *         if not null
     */
	public Iterator<Synset> getSynsetIterator(Lexicon lexicon)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(Synset.class);
		if (lexicon != null) {
			criteria = criteria.add(Restrictions.eq("lexicon", lexicon));
		}
		CriteriaIterator<Synset> synsetIterator = new CriteriaIterator<Synset>(
				criteria, sessionFactory, 500);
		return synsetIterator;
	}

    /**
     * Returns the {@link Sense} instance contained in the database accessed by this {@link Uby}
     * instance. The returned senses are filtered by the given name of the external system and
     * external reference.
     *
     * @param externalSys
     *            the {@link String} representing the name of external system such as:
     *            FrameNet_1.5_eng_lexicalUnit VerbNet_3.1_eng_sense
     *            OmegaWiki_<version>_<language>_synTrans WordNet_3.0_eng_senseKey
     *            Wiktionary_<version>_<language>_sense Wikipedia_<version>_<language>_articleTitle
     *            GermaNet_7.0_deu_lexicalUnit
     * @param externalRef
     *            the reference string from external system, such as:
     *            <ul>
     *            <li>WordNet: "[POS: noun] house%1:15:00::" - POS and sense key Returns a list of
     *            one sense</li>
     *            <li>VerbNet: "retire_withdraw-82-3" Several UBY senses can have the same original
     *            sense ID</li>
     *            <li>FrameNet: "2676" - lexical unit ID</li>
     *            <li>Wiktionary: "16:0:1" - sense key</li>
     *            <li>Wikipedia: "House" - article title</li>
     *            <li>OW: "303002" - OW SynTrans Id</li>
     *            </ul>
     * @return a {@link List} of all senses filtered by the given arguments or an empty list if if
     *         one of the given arguments is null or the accessed database does not contain any
     *         senses matching both constraints
     */
	public List<Sense> getSensesByOriginalReference(String externalSys, String externalRef){
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.createCriteria("monolingualExternalRefs").add(
				Restrictions.and(
						Restrictions.eq("externalSystem", externalSys),
						Restrictions.eq("externalReference", externalRef)));

		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		if(result == null) {
			result = new ArrayList<Sense>(0);
		}
		return result;
	}

    /**
     * Returns the {@link Synset} instance contained in the database accessed by this {@link Uby}
     * instance.
     *
     * @param externalSys
     *            the {@link String} representing the name of external system such as:
     *            OmegaWiki_2010-01-03_eng_definedMeaning WordNet_3.0_eng_synsetOffset
     *            GermaNet_7.0_deu_synset
     *
     * @param externalRef
     *            the Synset ID used in the external system,
     *
     * @returns the {@link Synset} specified by the given arguments
     */
	public Synset getSynsetByOriginalReference(String externalSys, String externalRef){
		Criteria criteria = session.createCriteria(Synset.class);
		criteria = criteria.createCriteria("monolingualExternalRefs").add(
				Restrictions.and(
						Restrictions.eq("externalSystem", externalSys),
						Restrictions.eq("externalReference", externalRef)));

		return (Synset) criteria.uniqueResult();

	}

    /**
     * Returns the {@link Sense} instance contained in the database accessed by this {@link Uby}
     * instance. The returned senses are filtered by the given name of the external system, external
     * reference and lexicon.
     *
     * @return a {@link List} of all senses filtered by the given arguments or an empty list if one
     *         of the given arguments is null or the accessed database does not contain any senses
     *         matching both constraints.
     **/
	public List<Sense> getSensesByOriginalReference(String externalSys, String externalRef, Lexicon lexicon){
	    Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.createCriteria("monolingualExternalRefs").add(
				Restrictions.and(
						Restrictions.eq("externalSystem", externalSys),
						Restrictions.eq("externalReference", externalRef)));

        @SuppressWarnings("unchecked")
        List<Sense> result = criteria.list();

        if(result == null) {
            result = new ArrayList<Sense>(0);
        }

        List<Sense> temp = new ArrayList<Sense>(result);
        for(Sense s: temp){
            if(!s.getLexicalEntry().getLexicon().getName().equals(lexicon.getName())){
                result.remove(s);
            }
        }

        return result;
    }

    /**
     * Returns a {@link List} of all {@link SenseAxis} instances contained in the database accessed
     * by this {@link Uby} instance.
     *
     * @return a list of all sense axes in the accessed database or an empty list if the accessed
     *         database does not contain any sense axes
     */
	public List<SenseAxis> getSenseAxes(){
		Criteria criteria = session.createCriteria(SenseAxis.class);
		@SuppressWarnings("unchecked")
		List<SenseAxis> result = criteria.list();
		if(result == null) {
			result = new ArrayList<SenseAxis>(0);
		}
		return result;
	}

    /**
     * This method finds all {@link SenseAxis} instances which id contains the specified
     * {@link String} in the database accessed by this {@link Uby} instance.
     *
     * @param senseAxisId
     *            string contained in the identifiers of the sense axes to be returned
     *
     * @return the {@link List} of all sense axes which id contains the specified string.<br>
     *         This method returns an empty list if no sense axis contains the specified string in
     *         its id or the specified string is null.
     *
     * @see #getSenseAxes()
     * @see #getSenseAxesBySense(Sense)
     * @see #getSenseAxesByIdPattern(String)
     */
	public List<SenseAxis> getSenseAxesByIdPattern(String senseAxisId){
		Criteria criteria= session.createCriteria(SenseAxis.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseAxisId like '%"+ senseAxisId+"%'"));

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
     * @param sense
     *            all returned sense axes should bind this sense
     *
     * @return all sense axes (sense alignments) that contain the consumed sense.<br>
     *         This method returns an empty list if the accessed UBY-LMF database does not contain
     *         any alignments of the specified sense, or the specified sense is null.
     */
	public List<SenseAxis> getSenseAxesBySense(Sense sense) {
		if (sense != null && sense.getId() != null && !sense.getId().equals("")) {
			Criteria criteria = session.createCriteria(SenseAxis.class);
			criteria = criteria.add(Restrictions.or(
					Restrictions.eq("senseOne", sense),
					Restrictions.eq("senseTwo", sense)));
			@SuppressWarnings("unchecked")
			List<SenseAxis> result = criteria.list();
			return result;
		}
		else {
			return new ArrayList<SenseAxis>(0);
		}
	}

    /**
     * Consumes two {@link Sense} instances and returns true if and only if the consumed instances
     * are aligned by a {@link SenseAxis} instance.
     *
     * @param sense1
     *            the sense to be checked for alignment with sense2
     * @param sense2
     *            the sense to be checked for alignment with sense1
     * @return true if and only if sense1 has an alignment to sense2 by a sense axis instance so
     *         that sense1 is the first sense of a sense axis and sense2 the second.<br>
     *         This method returns false if one of the consumed senses is null.
     * @see SenseAxis#getSenseOne()
     * @see SenseAxis#getSenseTwo()
     */
	public boolean hasSensesAxis(Sense sense1, Sense sense2) {
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
     * Returns a {@link List} of all {@link Sense} instances which unique identifier contains the
     * consumed {@link String}.
     *
     * @param idPattern
     *            the pattern which identifiers of the returned senses must contain
     * @return the list of all senses which unique identifier contains the idPattern
     *         <p>
     *         If none of the senses contained in the UBY-Database accessed by this {@link Uby}
     *         instance contains the consumed pattern this method returns an empty list.
     */
	public List<Sense> getSensesbyIdPattern(String idPattern) {
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.add(Restrictions.sqlRestriction("senseId like '%"
				+ idPattern + "%'"));
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

    /**
     * This methods allows retrieving a {@link Sense} instance by its exact identifier.
     *
     * @param senseId
     *            the unique identifier of the sense which should be returned
     * @return the sense with the consumed senseId
     * @throws UbyInvalidArgumentException
     *             if a sense with this identifier does not exist
     */
	public Sense getSenseById(String senseId)
			throws IllegalArgumentException {
		Criteria criteria = session.createCriteria(Sense.class).add(
				Restrictions.eq("id", senseId));
		List<?> result = criteria.list();
		if (result.size() < 0) {
            throw new IllegalArgumentException(
					"Sense with this ID does not exist");
        }

		return (Sense) result.get(0);
	}

    /**
     * This methods allows retrieving a {@link Synset} instance by its exact identifier.
     *
     * @param synsetId
     *            the unique identifier of the synset which should be returned
     * @return the synset with the consumed senseId
     * @throws UbyInvalidArgumentException
     *             if a synset with this identifier does not exist
     */
	public Synset getSynsetById(String synsetId) throws IllegalArgumentException{
		Criteria criteria = session.createCriteria(Synset.class).add(
				Restrictions.eq("id", synsetId));
		List<?> result = criteria.list();
		if (result.size() < 0) {
            throw new IllegalArgumentException(
					"Synset with the ID " +synsetId +" does not exist");
        }

		return (Synset) result.get(0);
	}


    /**
     * @deprecated use {@link #wordNetSenses(String, String)} or
     *             {@link #wordNetSense(String, String)} instead
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
     * @param POS
     *            : POS value=comment<br>
     *            a = adj;<br>
     *            n = noun;<br>
     *            r = adv<br>
     *            v = verb<br>
     * @param SynsetOffset
     *            : offset value of Synset;
     * @return list of senses belong to the given synset
     * @Deprecated use {@link #wordNetSenses(String, String)} or
     *             {@link #wordNetSense(String, String)} instead
     */
	@Deprecated
    public List<Sense> getSensesByWNSynsetId(String POS, String SynsetOffset)
        throws ClassNotFoundException, SQLException
    {

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
     * Consumes a synset identifier (in WordNet terminology) and returns a {@link List} of
     * {@link Sense} instances which are derived from the WordNets synset, specified by the consumed
     * identifier.
     *
     * @param wnSynsetId
     *            string representation of the WordNets synset identifier i.e. "1740-n"
     *
     * @return a list of senses derived from the WordNets synset, specified by the consumed
     *         identifier
     *         <p>
     *         This method returns an empty list if the database accessed by this {@link Uby}
     *         instance does not contain senses derived from the specified WordNet synset.
     *
     * @deprecated use {@link #wordNetSense(String, String)} and
     *             {@link #wordNetSense(String, String)} instead
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
     * Consumes a synset offset (in WordNet terminology) and a part-of-speech. Returns a
     * {@link List} of all {@link Sense} instances which are derived from the WordNets synset,
     * identified by the consumed arguments.
     *
     * @param partOfSpeech
     *            a string describing part of speech of the senses to be returned.
     *            <p>
     *            Valid values are: <list>
     *            <li>"noun"</li>
     *            <li>"verb"</li>
     *            <li>"adverb"</li>
     *            <li>"adjective"</li> </list>
     *
     * @param offset
     *            string representation of the WordNets synset offset i.e. "14469014"
     *
     * @return senses derived from the WordNets synset, described by the consumed arguments
     *         <p>
     *         This method returns an empty list if the database accessed by this {@link Uby}
     *         instance does not contain the specified sense.
     *
     * @throws UbyInvalidArgumentException
     *             if the specified part of speech is not valid or one of consumed arguments is null
     *
     * @since 0.2.0
     */
	@Deprecated
	public List<Sense> wordNetSenses(String partOfSpeech, String offset) throws IllegalArgumentException {

		if(partOfSpeech == null) {
            throw new IllegalArgumentException("partOfSpeech is null");
        }

		if(offset == null) {
            throw new IllegalArgumentException("offset is null");
        }

		String refId="[POS: noun] ";
		if (partOfSpeech.equals("adjective")){
			refId=refId.replaceAll("noun", "adjective");
		}else if (partOfSpeech.equals("adverb")){
			refId=refId.replaceAll("noun", "adverb");
		}else if (partOfSpeech.equals("verb")){
			refId=refId.replaceAll("noun", "verb");
		}else if (!partOfSpeech.equals("noun")) {
            throw new IllegalArgumentException(
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
     * Consumes a sense key (in WordNet terminology) and a part-of-speech. Returns a {@link Sense}
     * instance which is derived from the WordNets sense, identified by the consumed arguments.
     *
     * @param partOfSpeech
     *            a string describing part of speech of the sense to be returned.
     *            <p>
     *            Valid values are: <list>
     *            <li>"noun"</li>
     *            <li>"verb"</li>
     *            <li>"adverb"</li>
     *            <li>"adjective"</li> </list>
     *
     * @param senseKey
     *            string representation of the WordNets identifier of a sense i.e. "enter%2:33:00::"
     *
     * @return UBY-LMF sense derived from the WordNets word, described by the consumed arguments
     *         <p>
     *         This method returns null if the database accessed by this {@link Uby} instance does
     *         not contain the specified sense.
     *
     * @throws UbyInvalidArgumentException
     *             if the specified part of speech is not valid or one of the consumed arguments is
     *             null
     *
     * @since 0.2.0
     */
	@Deprecated
	public Sense wordNetSense(String partOfSpeech, String senseKey) throws IllegalArgumentException {

		if(partOfSpeech == null) {
            throw new IllegalArgumentException("partOfSpeech is null");
        }

		if(senseKey == null) {
            throw new IllegalArgumentException("senseKey is null");
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
            throw new IllegalArgumentException(
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

	// OmegaWiki

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
	 * @return list of all senses derived from specified OmegaWikis SynTrans.
	 *         <br>
	 *         This method returns an empty list if a SynTrans with specified
	 *         identifier does not exist or the database accessed by this
	 *         {@link Uby} instance does not contain a OmegaWiki {@link Lexicon}.
	 */
	@Deprecated
	public List<Sense> getSensesByOWSynTransId(String synTransId) {
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.add(Restrictions.eq("index",
				Integer.parseInt(synTransId.trim())));
		@SuppressWarnings("unchecked")
		List<Sense> result = criteria.list();
		return result;
	}

    /**
     * Consumes a unique identifier of a {@link Sense} instance and returns a {@link List} of all
     * {@link SemanticLabel} instances associated to the specified sense.
     *
     * @param senseId
     *            a unique identifier of the sense for which semantic labels should be returned
     * @return a list of all semantic labels of the specified sense or an empty list if a sense with
     *         such identifier does not exist or the sense does not have any associated semantic
     *         labels
     */
	public List<SemanticLabel> getSemanticLabelsbySenseId(String senseId){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId='"+ senseId+"'"));
		@SuppressWarnings("unchecked")
		List<SemanticLabel> result = criteria.list();
		return result;
	}

    /**
     * Consumes a unique identifier of a {@link Sense} instance and returns a {@link List} of all
     * {@link SemanticLabel} instances associated to the specified sense. The returned semantic
     * labels are filtered by the specified type.
     *
     *
     * @param senseId
     *            a unique identifier of the sense for which semantic labels should be returned
     * @param type
     *            returned semantic labels must have this type
     * @return a list of all semantic labels of the specified sense filtered by the type or an empty
     *         list if the database accessed by this {@link Uby} instance does not contain any
     *         semantic labels matching the criteria
     */
	public List<SemanticLabel> getSemanticLabelsbySenseIdbyType(String senseId, String type){
		Criteria criteria= session.createCriteria(SemanticLabel.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId='"+ senseId +"' and type ='"+type+"'"));
		@SuppressWarnings("unchecked")
		List<SemanticLabel> result = criteria.list();
		return result;
	}

    /**
     * Return the semantic predicate with the given Id
     *
     * @param predicateId
     *            the id of the predicate
     * @return semantic predicate
     */
	public SemanticPredicate getSemanticPredicateById(String predicateId){
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		criteria=criteria.add(Restrictions.sqlRestriction("semanticPredicateId='"+predicateId+"'"));
		return (SemanticPredicate) criteria.uniqueResult();

	}

	/**
	 * Returns a {@link List} of all {@link SemanticPredicate} instances in the
	 * database with the given label and filtered by {@link Lexicon}.
	 * @param label
	 * 			semantic predicate label
	 * @param lexicon
	 * 	 		all returned semantic predicates will belong to
	 *          the specified lexicon
	 * @return list of semantic predicates which matches the criteria or an
	 *         empty list if none of the semantic predicates matches the
	 *         criteria
	 */
	public List<SemanticPredicate> getSemanticPredicatesByLabelAndLexicon(String label, Lexicon lexicon){
		System.err.println(lexicon.getId());
		Criteria criteria = session.createCriteria(SemanticPredicate.class);
		criteria = criteria.add(
				Restrictions.and(
						Restrictions.eq("label", label),
						Restrictions.eq("lexicon", lexicon)));
		
        @SuppressWarnings("unchecked")
        List<SemanticPredicate> result = criteria.list(); 

        if(result == null) {
            result = new ArrayList<SemanticPredicate>(0);
        }
        
        return result;
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
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId='"
					+ lexId + "'"));
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
		DetachedCriteria criteria = DetachedCriteria.forClass(SemanticPredicate.class);
		if (lexicon != null) {
			String lexId = lexicon.getId();
			criteria = criteria.add(Restrictions.sqlRestriction("lexiconId='"
					+ lexId + "'"));
		}
		CriteriaIterator<SemanticPredicate> predicateIterator = new CriteriaIterator<SemanticPredicate>(
				criteria, sessionFactory, 500);
		return predicateIterator;
	}

    /**
     * Returns the {@link SemanticArgument} instance with the specified unique identifier.
     *
     * @param argumentId
     *            the unique identifier of the semantic argument to be returned
     *
     * @return semantic argument with the specified unique identifier, contained in the database
     *         accessed by this {@link Uby} instance.<br>
     *         If a semantic argument with the specified identifier does not exist, this method
     *         return null.
     */
	public SemanticArgument getSemanticArgumentById(String argumentId){
		Criteria criteria = session.createCriteria(SemanticArgument.class);
		criteria=criteria.add(Restrictions.sqlRestriction("semanticArgumentId='"+argumentId+"'"));
		return (SemanticArgument) criteria.uniqueResult();
	}

	/**
	 * Returns a {@link List} of all {@link SemanticArgument} instances in the
	 * database with the given label and {@link SemanticPredicate}.
	 * @param label
	 * 			semantic argument label
	 * @param lexicon
	 * 	 		all returned semantic arguments will belong to
	 *          the specified lexicon
	 * @return list of semantic predicates which matches the criteria or an
	 *         empty list if none of the semantic predicates matches the
	 *         criteria
	 */
	public List<SemanticArgument> getSemanticArgumentsByLabelAndPredicate(String roleLabel, SemanticPredicate predicate){
		Criteria criteria = session.createCriteria(SemanticArgument.class);
		System.err.println(" predicateid " + predicate.getId());
		criteria = criteria.add(
				Restrictions.and(
						Restrictions.eq("predicate",predicate),
						Restrictions.eq("semanticRole", roleLabel)));

        @SuppressWarnings("unchecked")
        List<SemanticArgument> result = criteria.list(); 

        if(result == null) {
            result = new ArrayList<SemanticArgument>(0);
        }
        
        return result;
	}
    /**
     * Returns all {@link SynSemArgMap} instances contained in the database accessed by this
     * {@link Uby} instance.
     *
     * @return a list of all mappings between syntactic and semantic arguments.<br>
     *         If the database does not contain any mappings, this method returns an empty list.
     */
    public List<SynSemArgMap> getSynSemArgMaps()
    {

        Criteria criteriaSynSem = session.createCriteria(SynSemArgMap.class);
        @SuppressWarnings("unchecked")
		List<SynSemArgMap> result = criteriaSynSem.list();

        return result;
    }

	@Override
    protected void finalize()
		throws Throwable
	{
		//dbConfig = null; -- FindBugs: This finalizer nulls out fields. This is usually an error, as it does not aid garbage collection, and the object is going to be garbage collected anyway.
		session.close();
	}

	/**
	 * Returns a {@link List} of all {@link SemanticPredicate} instances in the
	 * database that are associated with a {@link Sense} with the given senseId
	 * @param senseId
	 * 		UBY sense Id string
	 * @return list of semantic predicates which matches the criteria or an empty list
	 */
	public List<SemanticPredicate> getSemanticPredicatesBySenseId(
			String senseId) {
		Criteria criteria= session.createCriteria(PredicativeRepresentation.class);
		criteria=criteria.add(Restrictions.sqlRestriction("senseId='"+ senseId+"'"));
		@SuppressWarnings("unchecked")
		List<PredicativeRepresentation> representations = criteria.list();
		List<SemanticPredicate> result = new ArrayList<>();
		for (PredicativeRepresentation predicative : representations){
			result.add(predicative.getPredicate());
		}
		return result;
	}

	/**
	 * Returns a {@link List} of all {@link Sense} instances in the
	 * database that are associated with a {@link SemanticPredicate} with the given semanticPredicateId
	 * @param semanticPredicateid
	 * 		UBY semanticPredicateId string
	 * @return list of senses which matches the criteria or an empty list
	 */
	public List<Sense> getSensesBySemanticPredicateId(
			String semanticPredicateId) {
		Criteria criteria= session.createCriteria(PredicativeRepresentation.class);
		criteria=criteria.add(Restrictions.sqlRestriction("predicate='"+ semanticPredicateId+"'"));
		@SuppressWarnings("unchecked")
		List<PredicativeRepresentation> representations = criteria.list();
		List<Sense> result = new ArrayList<>();
		for (PredicativeRepresentation predicative : representations){
			result.add(predicative.getSense());
		}
		return result;
	}
	
    /**
     * Returns a {@link List} of all {@link PredicateArgumentAxis} instances contained in the database accessed
     * by this {@link Uby} instance.
     *
     * @return a list of all predicate-argument axes in the accessed database or an empty list if the accessed
     *         database does not contain any sense axes
     */
	public List<PredicateArgumentAxis> getPredicateArgumentAxes(){
		Criteria criteria = session.createCriteria(PredicateArgumentAxis.class);
		@SuppressWarnings("unchecked")
		List<PredicateArgumentAxis> result = criteria.list();
		if(result == null) {
			result = new ArrayList<PredicateArgumentAxis>(0);
		}
		return result;
	}
	
    /**
     * This method finds all {@link PredicateArgumentAxis} instances whose id contains the specified
     * {@link String} in the database accessed by this {@link Uby} instance.
     *
     * @param senseAxisId
     *            string contained in the identifiers of the predicate-argument axes to be returned
     *
     * @return the {@link List} of all predicate-argument axes whose id contains the specified string.<br>
     *         This method returns an empty list if no axis contains the specified string in
     *         its id or the specified string is null.
     *
     * @see #getPredicateArgumentAxes()
     * @see #getSenseAxesBySense(Sense)
     */
	public List<PredicateArgumentAxis> getPredicateArgumentAxesByIdPattern(String axisId){
		Criteria criteria= session.createCriteria(PredicateArgumentAxis.class);
		criteria=criteria.add(Restrictions.sqlRestriction("predicateArgumentAxisId like '%"+ axisId+"%'"));

		@SuppressWarnings("unchecked")
		List<PredicateArgumentAxis> result =  criteria.list();
		if(result == null) {
			result = new ArrayList<PredicateArgumentAxis>(0);
		}
		return result;
	}

    /**
     * This method retrieves all {@link PredicateArgumentAxis} which bind the specified {@link SemanticPredicate}.
     *
     * @param predicate
     *            all returned predicate-argument axes should bind this semantic predicate
     *
     * @return all predicate-argument axes that contain the consumed semantic predicate.<br>
     *         This method returns an empty list if the accessed UBY-LMF database does not contain
     *         any alignments of the specified semantic predicate, or the specified semantic predicate is null.
     */
	public List<PredicateArgumentAxis> getPredicateArgumentAxesByPredicate(SemanticPredicate predicate) {
		if (predicate != null && predicate.getId() != null && !predicate.getId().equals("")) {
			Criteria criteria = session.createCriteria(PredicateArgumentAxis.class);
			criteria = criteria.add(Restrictions.or(
					Restrictions.eq("semanticPredicateOne", predicate),
					Restrictions.eq("semanticPredicateTwo", predicate)));
			@SuppressWarnings("unchecked")
			List<PredicateArgumentAxis> result = criteria.list();
			return result;
		}
		else {
			return new ArrayList<PredicateArgumentAxis>(0);
		}
	}
	
    /**
     * Consumes two {@link SemanticPredicate} instances and returns true if and only if the consumed instances
     * are aligned by a {@link PredicateArgumentAxis} instance.
     *
     * @param pred1
     *            the semantic predicate to be checked for alignment with pred2
     * @param pred2
     *            the semantic predicate to be checked for alignment with pred1
     * @return true if and only if pred1 has an alignment to pred2 by a predicate-argument axis instance so
     *         that pred1 is the first semantic predicate of an axis and pred2 the second.<br>
     *         This method returns false if one of the consumed semantic predicates is null.
     * @see PredicateArgumentAxis#getSemanticPredicateOne()
     * @see PredicateArgumentAxis#getSemanticPredicateTwo()
     */
	public boolean hasPredicateArgumentAxis(SemanticPredicate pred1, SemanticPredicate pred2) {
		boolean ret = false;
		if (pred1 != null && pred2 != null && pred1.getId() != null
				&& pred1.getId().length() > 0 && pred2.getId() != null
				&& pred2.getId().length() > 0) {
			String sql = "Select semanticPredicateOne, semanticPredicateTwo from PredicateArgumentAxis where "
					+ "(semanticPredicateOne='" + pred1.getId() + "' and semanticPredicateTwo='"
					+ pred2.getId() + "')" + "or(semanticPredicateOne='"
					+ pred2.getId() + "' and semanticPredicateTwo='" + pred1.getId()
					+ "')";

			List<?> query = session.createSQLQuery(sql).list();
			if (query.size() > 0) {
				ret = true;
			}

		}
		return ret;
	}
}