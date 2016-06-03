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

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * This class represents an extension of the {@link Uby} class and offers additional methods for
 * quick searching of different UBY-LMF elements in a database containing a {@link LexicalResource}.
 * <p>
 * For performance reasons, the methods offered by this class do not return fully initialized
 * Uby-LMF class instances.
 * 
 * @since 0.1.0
 * 
 * @author Silvana Hartmann
 * @author Zijad Maksuti
 */
public class UbyQuickAPI extends Uby
{
    /**
     * Using this constructor, you have to call setDbConfig before using any method.
     * 
     * @deprecated use {@link #UbyQuickAPI(DBConfig)} instead
     */
	@Deprecated
	public UbyQuickAPI()
	{
		// TO-DO nothing
	}

    /**
     * Constructor for a {@link UbyQuickAPI} instance used for searching of different elements in a
     * database containing UBY-LMF {@link LexicalResource}.
     *
     * The connection to the database is specified using a {@link DBConfig} instance.
     *
     * @param dbConfig
     *            configuration of the database containing UBY-LMF lexical resource.
     * @throws UbyInvalidArgumentException
     *             if the specified dbConfig is null
     * 
     * @since 0.1.0
     */
	public UbyQuickAPI(DBConfig dbConfig) throws IllegalArgumentException
	{
		super(dbConfig);
	}

    /**
     *
     * @param config
     *            database's configuration
     * @param useHibernate
     *            false for direct access; true connect via Hibernate
     * @deprecated use {@link #UbyQuickAPI(DBConfig)} instead
     */
	@Deprecated
	public UbyQuickAPI(DBConfig config, boolean useHibernate,boolean useTempTables) throws ClassNotFoundException, SQLException, FileNotFoundException{
		setDBConfig(config,useHibernate,useTempTables);
	}

	/**
	 *
	 * @param session
	 *            Session of hibernate connection. In case you don't run query
	 *            directly
	 * @deprecated use {@link #UbyQuickAPI(DBConfig)} instead
	 */
	@Deprecated
	public UbyQuickAPI(Session session)
	{
		this.session = session;
	}

	/**
	 * @deprecated use {@link #UbyQuickAPI(DBConfig)} instead
	 */
	@Deprecated
	public UbyQuickAPI(SessionFactory sf){
		sessionFactory=sf;
		session=sessionFactory.openSession();
	}

    /**
     * @param config
     *            Database's Configuration
     * @param useHibernate
     *            true if you want to connect to database via Hibernate; false for direct access
     * @deprecated use {@link #UbyQuickAPI(DBConfig)} instead
     */
	@Deprecated
    public void setDBConfig(DBConfig config, boolean useHibernate, boolean useTemporaryTables)
        throws ClassNotFoundException, SQLException, FileNotFoundException
    {
		this.dbConfig = config;
		if(useHibernate){
			setDbConfig(config);
		}/*else{
			this.useTemporaryTables=useTemporaryTables;
			mysql=new MySQLConnect(config,this.useTemporaryTables);
		}*/
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
	 * @since 0.2.0
	 *
	 * @see Lexicon#getName()
	 * @see Lexicon#getId()
	 * 
	 */
	public List<Lexicon> lightLexicons(){
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
	 * This method fetches a {@link List} of all identifiers of {@link Sense}
	 * instances which are aligned by a {@link SenseAxis} with the specified
	 * sense.
	 * <p>
	 *
	 * The method is meant for fast fetching of alignments. For retrieving of
	 * complete alignments use {@link #getSenseAxesBySense(Sense)} instead.
	 *
	 * @param sense
	 *            all returned identifiers must belong to senses which are
	 *            aligned to it
	 *
	 * @return a list of identifiers of all senses which are aligned with the
	 *         specified sense by a sense axis.<br>
	 *         If the specified sense is not contained in any alignment or the
	 *         specified sense is null, this method returns an empty list.
	 *         
	 * @since 0.2.0
	 *         
	 */
	public List<String> alignedSenseIDs(Sense sense) {
		List<String> list = new ArrayList<String>();

		String id = sense.getId();
		if (id != null && !id.equals("")) {
			// Select senseOneId, senseTwoId from SenseAxis where
			// senseOneId='WN_Sense_100' or senseTwoId='WN_Sense_100'
			String sql = "Select senseOneId, senseTwoId from SenseAxis where senseOneId='"
					+ id + "' or senseTwoId='" + id + "'";
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
				} else {
					list.add(sense1);
				}
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
	 * complete alignments use {@link #getSenseAxesBySense(Sense)} instead.
	 *
	 * @param senseId
	 *            all returned identifiers must belong to senses which are
	 *            aligned to the sense represented by the id
	 *
	 * @return a list of identifiers of all senses which are aligned with the
	 *         specified sense by a sense axis.<br>
	 *         If the sense specified by its identifier is not contained in any
	 *         alignment or the specified id is null, this method returns an
	 *         empty list.
	 *         
	 * @since 0.2.0
	 *
	 */
	public List<String> alignedSenseIDs(String senseId) {
		List<String> list = new ArrayList<String>();
		if (senseId != null && !senseId.equals("")) {
			// Select senseOneId, senseTwoId from SenseAxis where
			// senseOneId='WN_Sense_100' or senseTwoId='WN_Sense_100'
			String sql = "Select senseOneId, senseTwoId from SenseAxis where senseOneId='"
					+ senseId + "' or senseTwoId='" + senseId + "'";
			@SuppressWarnings("rawtypes")
			List query = session.createSQLQuery(sql).list();
			@SuppressWarnings("rawtypes")
			Iterator iter = query.iterator();
			while (iter.hasNext()) {
				Object[] row = (Object[]) iter.next();
				String sense1 = (String) row[0];
				String sense2 = (String) row[1];
				if (sense1.matches(senseId)) {
					list.add(sense2);
				} else {
					list.add(sense1);
				}
			}
		}
		return list;
	}

	/**
	 * Consumes a {@link List} of {@link Sense} instances and returns a List of
	 * all {@link SenseAxis} instances aligning senses from the consumed list.
	 * In particular, every returned sense axis aligns two senses from the
	 * consumed list.<br>
	 * The returned sense axes are not fully initialized and contain only references
	 * to senses which they bind.
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
	 * @since 0.2.0
	 * 
	 * @see #lightSenseAxesBySenseIDs(List)
	 * 
	 */
	public List<SenseAxis> lightSenseAxes(List<Sense> listSense) {
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
	 * <br>
	 * The returned sense axes are not fully initialized and contain only references
	 * to senses which they bind.
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
	 * @since 0.2.0
	 *
	 * @see #getSenseAxes()
	 * @see #lightSenseAxes(List)
	 */
	public List<SenseAxis> lightSenseAxesBySenseIDs(List<String> listSenseId) {
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
	// TODO not neccessary?
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
}
