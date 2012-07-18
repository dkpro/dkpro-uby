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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.ukp.lmf.api.CriteriaIterator;
import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.writer.LMFWriter;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * Converts Uby saved in the Database (Hibernate) to XML
 * @author chebotar
 *
 */
public class DBToXMLTransformer {

	private final SessionFactory sessionFactory;
	private final LMFWriter writer;

	/**
	 * @param dbConfig Configuration of the database with Uby
	 * @param writer  Writer where the XML output should be written
	 * @throws FileNotFoundException
	 */
	public DBToXMLTransformer(DBConfig dbConfig, LMFWriter writer) throws FileNotFoundException{
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);

		this.sessionFactory = cfg.buildSessionFactory();
		this.writer = writer;
	}

	/**
	 * Transforms lexicalResource to XML
	 * @throws LMFWriterException
	 */
	public void transform(LexicalResource lexicalResource) throws LMFWriterException{
		Session session = sessionFactory.openSession();
		lexicalResource = (LexicalResource)session.get(LexicalResource.class, lexicalResource.getName());
		writer.writeStartElement(lexicalResource);

		int bufferSize = 100;
		int counter = 0;
		// Iterate over all lexicons
		for(Lexicon lexicon : lexicalResource.getLexicons()){
			writer.writeStartElement(lexicon);

			// Iterate over all possible sub-elements of this Lexicon and write them to the XML
			@SuppressWarnings("rawtypes")
			Class[] lexiconClassesToSave = {LexicalEntry.class, SubcategorizationFrame.class,
				SubcategorizationFrameSet.class, SemanticPredicate.class,Synset.class,
				SynSemCorrespondence.class,ConstraintSet.class
			};

			for(@SuppressWarnings("rawtypes") Class clazz : lexiconClassesToSave){
				Criteria criteria = session.createCriteria(clazz)
					.add(Restrictions.sqlRestriction("lexiconId = '"+lexicon.getId()+"'"));
				@SuppressWarnings("rawtypes")
				CriteriaIterator iter = new CriteriaIterator(criteria, bufferSize);
				while(iter.hasNext()){
					if(counter % 1000 == 0) {
						System.out.println("PROGRESS: "+counter);
					}
					Object obj = iter.next();
					writer.writeElement(obj);
					session.evict(obj);
					counter++;
				}
			}
			writer.writeEndElement(lexicon);
		}

		// Iterate over SenseAxes and write them to XMLX
		Criteria criteria = session.createCriteria(SenseAxis.class)
				.add(Restrictions.sqlRestriction("lexicalResourceId = '"+lexicalResource.getName()+"'"));
		@SuppressWarnings("rawtypes")
		CriteriaIterator iter = new CriteriaIterator(criteria, bufferSize);
		while(iter.hasNext()){
			if(counter % 1000 == 0) {
				System.out.println("PROGRESS: "+counter);
			}
			Object obj = iter.next();
			writer.writeElement(obj);
			session.evict(obj);
			counter++;
		}
		writer.writeEndElement(lexicalResource);
		writer.writeEndDocument();
	}

}
