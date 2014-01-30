/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universit��t Darmstadt
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.ukp.lmf.api.CriteriaIterator;
import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
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
import de.tudarmstadt.ukp.lmf.writer.xml.LMFXmlWriter;

/**
 * Converts UBY-database to  a XML file.
 * 
 * @author Yevgen Chebotar
 * @author Zijad Maksuti
 * 
 * @since UBY 0.1.0
 */
public class DBToXMLTransformer {

	private final SessionFactory sessionFactory;
	private final LMFWriter writer;
	private static final Logger logger = Logger.getLogger(DBToXMLTransformer.class.getSimpleName());
	
	// true only if only selected lexicons should be converted to XML
	private boolean lexiconsOnly = false;
	// the list of lexicons which should be to XML; used only when selectiveConversion set to true
	private Set<String> selectedLexicons;
	
	// true only if only sense axes should be converted to XML
	private boolean senseAxesOnly = false;

	/**
	 * Constructs a new {@link DBToXMLTransformer} instance which is used to convert UBY
	 * from a database to an XML file.
	 * 
	 * @param dbConfig {@link DBConfig} instance used to access the database
	 * @param writer  {@link LMFWriter} used to write to a XML file
	 * 
	 * @since UBY 0.1.0
	 */
	@SuppressWarnings("deprecation")
	public DBToXMLTransformer(DBConfig dbConfig, LMFWriter writer) {
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);

		this.sessionFactory = cfg.buildSessionFactory();
		this.writer = writer;
	}

	/**
	 * Transforms a {@link LexicalResource} instance retrieved from a database
	 * to a XML file.
	 * 
	 * @param lexicalResource the lexical resource retrived from the database 
	 * 
	 * @throws LMFWriterException if a severe error occurs when writing to a file
	 * 
	 * @since UBY 0.1.0
	 */
	public void transform(LexicalResource lexicalResource) throws LMFWriterException{
		Session session = sessionFactory.openSession();
		String lexicalResourceName = lexicalResource.getName();
		lexicalResource = (LexicalResource)session.get(LexicalResource.class, lexicalResourceName);
		
		
		if(writer instanceof LMFXmlWriter)
			logger.log(Level.INFO, "Started writing lexicalResource "+ lexicalResourceName
					+ " to " + ((LMFXmlWriter) writer).getOutputPath());
		else
			logger.log(Level.INFO, "Started writing lexicalResource " +  lexicalResourceName);
			
		
		writer.writeStartElement(lexicalResource);
		
		// write GlobalInformation
		GlobalInformation globalInformation = lexicalResource.getGlobalInformation();
		writer.writeElement(globalInformation);
		int counter = 1;
		
		int bufferSize = 100;
		// Iterate over all lexicons
		for(Lexicon lexicon : lexicalResource.getLexicons()){
			
			String lexiconName = lexicon.getName();
			// write lexicons
			if((lexiconsOnly && !selectedLexicons.contains(lexiconName)) || senseAxesOnly){
				// on selective conversion, omit the lexicons not in the list
				// when only sense axes should be converter, omit all
				continue;
			}
			
			logger.info("processing lexicon: " + lexiconName);
			writer.writeStartElement(lexicon);

			// Iterate over all possible sub-elements of this Lexicon and write them to the XML
			@SuppressWarnings("rawtypes")
			Class[] lexiconClassesToSave = {LexicalEntry.class, SubcategorizationFrame.class,
				SubcategorizationFrameSet.class, SemanticPredicate.class,Synset.class,
				SynSemCorrespondence.class,ConstraintSet.class
			};

			for(@SuppressWarnings("rawtypes") Class clazz : lexiconClassesToSave){
				DetachedCriteria criteria = DetachedCriteria.forClass(clazz)
					.add(Restrictions.sqlRestriction("lexiconId = '"+lexicon.getId()+"'"));
				@SuppressWarnings("rawtypes")
				CriteriaIterator iter = new CriteriaIterator(criteria, sessionFactory, bufferSize);
				while(iter.hasNext()){
					if(counter % 1000 == 0) {
						logProcessedInstances(counter);
					}
					Object obj = iter.next();
					writer.writeElement(obj);
					session.evict(obj);
					counter++;
				}
			}
			writer.writeEndElement(lexicon);
		}
		
		if(!lexiconsOnly){
			
			// Iterate over SenseAxes and write them to XMLX when not only lexicons should be converted
			DetachedCriteria criteria = DetachedCriteria.forClass(SenseAxis.class)
					.add(Restrictions.sqlRestriction("lexicalResourceId = '"+lexicalResource.getName()+"'"));
			@SuppressWarnings("rawtypes")
			CriteriaIterator iter = new CriteriaIterator(criteria, sessionFactory, bufferSize);
			logger.info("started processing sense axes");
			while(iter.hasNext()){
				if(counter % 1000 == 0) {
					logProcessedInstances(counter);
				}
				Object obj = iter.next();
				writer.writeElement(obj);
				session.evict(obj);
				counter++;
			}
		}
		writer.writeEndElement(lexicalResource);
		writer.writeEndDocument();
		
		// clear the previous parameter values
		this.lexiconsOnly = false;
		this.senseAxesOnly = false;
		this.selectedLexicons = new HashSet<String>();
	}
	
	
	public void transform(LexicalResource lexicalResource, Lexicon lexicon) throws LMFWriterException{
		Session session = sessionFactory.openSession();
		String lexicalResourceName = lexicalResource.getName();
		lexicalResource = (LexicalResource)session.get(LexicalResource.class, lexicalResourceName);
		
		
		if(writer instanceof LMFXmlWriter)
			logger.log(Level.INFO, "Started writing lexicalResource "+ lexicalResourceName
					+ " to " + ((LMFXmlWriter) writer).getOutputPath());
		else
			logger.log(Level.INFO, "Started writing lexicalResource " +  lexicalResourceName);
			
		
		writer.writeStartElement(lexicalResource);
		
		// write GlobalInformation
		GlobalInformation globalInformation = lexicalResource.getGlobalInformation();
		writer.writeElement(globalInformation);
		int counter = 1;
		
		int bufferSize = 50;
		// Iterate over all lexicons
		//for(Lexicon lexicon : lexicalResource.getLexicons()){
			
			String lexiconName = lexicon.getName();
			
			logger.info("processing lexicon: " + lexiconName);
			writer.writeStartElement(lexicon);

			// Iterate over all possible sub-elements of this Lexicon and write them to the XML
			@SuppressWarnings("rawtypes")
			Class[] lexiconClassesToSave = {LexicalEntry.class, SubcategorizationFrame.class,
				SubcategorizationFrameSet.class, SemanticPredicate.class,Synset.class,
				SynSemCorrespondence.class,ConstraintSet.class
			};

			for(@SuppressWarnings("rawtypes") Class clazz : lexiconClassesToSave){
				DetachedCriteria criteria = DetachedCriteria.forClass(clazz)
					.add(Restrictions.sqlRestriction("lexiconId = '"+lexicon.getId()+"'"));
				@SuppressWarnings("rawtypes")
				CriteriaIterator iter = new CriteriaIterator(criteria, sessionFactory, bufferSize);
				while(iter.hasNext()){
					if(counter % 1000 == 0) {
						logProcessedInstances(counter);
					}
					Object obj = iter.next();
					writer.writeElement(obj);
					session.evict(obj);
					counter++;
				}
			}
			writer.writeEndElement(lexicon);
		//}
		
		writer.writeEndElement(lexicalResource);
		writer.writeEndDocument();
		
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
	 * @throws LMFWriterException if a severe error occurs when writing to a file
	 * 
	 * @since UBY 0.2.0
	 * 
	 * @see #transform(LexicalResource)
	 * @see #transformSenseAxes(LexicalResource)
	 */
	public void transformLexicons(LexicalResource lexialResource, Set<String> lexicons) throws LMFWriterException{
		this.lexiconsOnly = true;
		this.selectedLexicons = lexicons;
		transform(lexialResource);
	}
	
	/**
	 * Transforms a {@link LexicalResource} instance retrieved from a database
	 * to a XML file. The created XML only contains {@link SenseAxis} contained in the
	 * consumed lexical resource.
	 * 
	 * @param lexicalResource the lexical resource retrieved from the database
	 * 
	 * @throws LMFWriterException if a severe error occurs when writing to a file
	 * 
	 * @since UBY 0.2.0
	 * 
	 * @see #transform(LexicalResource)
	 * @see #transformLexicons(LexicalResource, List)
	 */
	public void transformSenseAxes(LexicalResource lexialResource) throws LMFWriterException{
		this.senseAxesOnly = true;
		transform(lexialResource);
	}
	
	/**
	 * Logs the number of UBY-LMF class instances written to a file.
	 * 
	 * @param counter number of processed class instances so far.
	 * 
	 * @since UBY 0.2.0
	 */
	private void logProcessedInstances(int counter){
		logger.info("progress: " + counter  + " class instances written to file");
	}

}
