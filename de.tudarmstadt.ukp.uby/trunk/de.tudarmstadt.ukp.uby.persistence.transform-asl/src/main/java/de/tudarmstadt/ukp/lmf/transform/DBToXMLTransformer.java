/*******************************************************************************
 * Copyright 2015
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.api.CriteriaIterator;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;

/**
 * Converts a given lexical resource from a UBY database to a UBY-XML file.
 * @author Yevgen Chebotar
 * @author Zijad Maksuti
 * @author Christian M. Meyer
 * @since UBY 0.1.0
 */
public class DBToXMLTransformer extends UBYHibernateTransformer {

	private static final Log logger = LogFactory.getLog(DBToXMLTransformer.class);

	protected LexicalResource lexicalResource;
	protected DBConfig dbConfig;

	/** Constructs a new {@link DBToXMLTransformer} instance which is used to
	 *  convert UBY from a database to an XML file.
	 *  @param dbConfig {@link DBConfig} instance used to access the database.
	 *  @param outputPath the file path of the resulting XML file.
	 *  @param dtdPath the file path of the DTD file. */
	public DBToXMLTransformer(final DBConfig dbConfig, final String outputPath,
			String dtdPath) throws FileNotFoundException, SAXException {
		this(dbConfig, new FileOutputStream(outputPath), dtdPath);
		this.dbConfig = dbConfig;
	}

	/** Constructs a new {@link DBToXMLTransformer} instance which is used to
	 *  convert UBY from a database to an XML file.
	 *  @param dbConfig {@link DBConfig} instance used to access the database.
	 *  @param outputStream the (file) stream of the resulting XML data.
	 *  @param dtdPath the file path of the DTD file. */
	public DBToXMLTransformer(final DBConfig dbConfig,
			final OutputStream outputStream, final String dtdPath)
			throws SAXException {
		super(dbConfig);
		writeStartDocument(outputStream, dtdPath);
	}

	/**
	 * Transforms a {@link LexicalResource} instance retrieved from a database
	 * to a XML file.
	 *
	 * @param lexicalResource the lexical resource retrived from the database
	 *
	 * @throws SAXException if a severe error occurs when writing to a file
	 *
	 * @since UBY 0.1.0
	 */
	public void transform(final LexicalResource lexicalResource) throws SAXException {
		openSession();
		try {
			String lexicalResourceName = lexicalResource.getName();
			this.lexicalResource = (LexicalResource)session.get(LexicalResource.class, lexicalResourceName);
			logger.info("Started writing lexicalResource " +  lexicalResourceName);

			doTransform(true, (Lexicon[]) null);
		} finally {
			closeSession();
		}
	}

	public void transform(final LexicalResource lexicalResource,
			final Lexicon lexicon) throws SAXException {
		this.lexicalResource = lexicalResource;
		openSession();
		try {
			doTransform(false, lexicon);
		} finally {
			closeSession();
		}
	}

	/**
	 * Transforms a {@link LexicalResource} instance retrieved from a database
	 * to a XML file. The created XML only contains {@link Lexicon} instances which
	 * names are specified in the consumed {@link Set}. {@link SenseAxis} instances are omitted.
	 *
	 * @param lexicalResource the lexical resource retrieved from the database
	 *
	 * @param lexicons the set of names of lexicons which should be written to XML file
	 *
	 * @throws SAXException if a severe error occurs when writing to a file
	 *
	 * @since UBY 0.2.0
	 *
	 * @see #transform(LexicalResource)
	 * @see #transformSenseAxes(LexicalResource)
	 */
	public void transformLexicons(final LexicalResource lexicalResource,
			final Set<String> lexicons) throws SAXException {
		this.lexicalResource = lexicalResource;
		openSession();
		try {
			doTransform(false, lexicons.toArray(new Lexicon[0]));
		} finally {
			closeSession();
		}
	}

	/**
	 * Transforms a {@link LexicalResource} instance retrieved from a database
	 * to a XML file. The created XML only contains {@link SenseAxis} contained in the
	 * consumed lexical resource.
	 *
	 * @param lexicalResource the lexical resource retrieved from the database
	 *
	 * @throws SAXException if a severe error occurs when writing to a file
	 *
	 * @since UBY 0.2.0
	 *
	 * @see #transform(LexicalResource)
	 * @see #transformLexicons(LexicalResource, List)
	 */
	public void transformSenseAxes(final LexicalResource lexicalResource)
			throws SAXException {
		this.lexicalResource = lexicalResource;
		openSession();
		try {
			doTransform(true, new Lexicon[0]);
		} finally {
			closeSession();
		}
	}

	// lexicons = null (all lexicons), lexicons.length = 0 (no lexicons).
	protected void doTransform(boolean includeAxes, 
			final Lexicon... includeLexicons) throws SAXException {
		final int bufferSize = 100;
		commitCounter = 1;

		writeStartElement(lexicalResource);

		// Iterate over all lexicons
		if (includeLexicons == null || includeLexicons.length > 0) {
			for (Lexicon lexicon : lexicalResource.getLexicons()) {
				String lexiconName = lexicon.getName();

				// Check if we want to include this lexicon.
				if (includeLexicons != null) {
					boolean found = false;
					for (Lexicon l : includeLexicons) {
                        if (lexiconName.equals(l.getName())) {
							found = true;
							break;
						}
                    }
					if (!found) {
                        continue;
                    }
				}

				logger.info("Processing lexicon: " + lexiconName);
				writeStartElement(lexicon);

				// Iterate over all possible sub-elements of this Lexicon and
				// write them to the XML
				Class<?>[] lexiconClassesToSave = {
						LexicalEntry.class,
						SubcategorizationFrame.class,
						SubcategorizationFrameSet.class,
						SemanticPredicate.class,
						Synset.class,
						SynSemCorrespondence.class,
						//ConstraintSet.class
				};

				//  "Unfortunately, MySQL does not treat large offset values efficiently by default and will still read all the rows prior to an offset value. It is common to see a query with an offset above 100,000 take over 20 times longer than an offset of zero!"
				// http://www.numerati.com/2012/06/26/reading-large-result-sets-with-hibernate-and-mysql/
				for(Class<?> clazz : lexiconClassesToSave) {
					/*DetachedCriteria criteria = DetachedCriteria.forClass(clazz)
							.add(Restrictions.sqlRestriction("lexiconId = '" + lexicon.getId() + "'"));
					CriteriaIterator<Object> iter = new CriteriaIterator<Object>(criteria, sessionFactory, bufferSize);
					while (iter.hasNext()) {
						Object obj = iter.next();
						writeElement(obj);
						session.evict(obj);
						commitCounter++;
						if (commitCounter % 1000 == 0)
							logger.info("progress: " + commitCounter  + " class instances written to file");
					}*/
					Session lookupSession = sessionFactory.openSession();
					Query query = lookupSession.createQuery("FROM " + clazz.getSimpleName()
							+ " WHERE lexiconId = '" + lexicon.getId() + "' ORDER BY id");
					query.setReadOnly(true);
					if (DBConfig.MYSQL.equals(dbConfig.getDBType())) {
                        query.setFetchSize(Integer.MIN_VALUE); // MIN_VALUE gives hint to JDBC driver to stream results
                    }
                    else {
                        query.setFetchSize(1000);
                    }
					ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
					while (results.next()) {
						// For streamed query results, no further queries are allowed (incl. lazy proxy queries!)
						// Detach the object from the lookup session and reload it using the "official" session.
						Object[] rows = results.get();
						Object row = rows[0];
						lookupSession.evict(row);
						lookupSession.evict(rows);
						rows = null;
						row = session.get(row.getClass(), ((IHasID) row).getId());
						writeElement(row);
						session.evict(row);
						row = null;
						commitCounter++;
						if (commitCounter % 1000 == 0) {
                            logger.info("progress: " + commitCounter  + " class instances written to file");
                        }
						if (commitCounter % 10000 == 0) {
							closeSession();
							openSession();
						}
				  }
				  results.close();
				  lookupSession.close();
				}
				writeEndElement(lexicon);
			}
		}

		// Iterate over SenseAxes and write them to XMLX when not only
		// lexicons should be converted
		if (includeAxes) {
			logger.info("Processing sense axes");
			DetachedCriteria criteria = DetachedCriteria.forClass(SenseAxis.class)
					.add(Restrictions.sqlRestriction("lexicalResourceId = '" + lexicalResource.getName() + "'"));
			CriteriaIterator<Object> iter = new CriteriaIterator<Object>(criteria, sessionFactory, bufferSize);
			while (iter.hasNext()) {
				Object obj = iter.next();
				writeElement(obj);
				session.evict(obj);
				commitCounter++;
				if (commitCounter % 1000 == 0) {
                    logger.info("progress: " + commitCounter  + " class instances written to file");
                }
			}
			
			logger.info("Processing predicateargument axes");
			DetachedCriteria criteria2 = DetachedCriteria.forClass(PredicateArgumentAxis.class)
					.add(Restrictions.sqlRestriction("lexicalResourceId = '" + lexicalResource.getName() + "'"));
			CriteriaIterator<Object> iter2 = new CriteriaIterator<Object>(criteria2, sessionFactory, bufferSize);
			while (iter2.hasNext()) {
				Object obj = iter2.next();
				writeElement(obj);
				session.evict(obj);
				commitCounter++;
				if (commitCounter % 1000 == 0) {
                    logger.info("progress: " + commitCounter  + " class instances written to file");
                }
		}

		}
		writeEndElement(lexicalResource);

		writeEndDocument();
	}

	@Override
	protected String getResourceAlias() {
		return lexicalResource.getName();
	}

}
