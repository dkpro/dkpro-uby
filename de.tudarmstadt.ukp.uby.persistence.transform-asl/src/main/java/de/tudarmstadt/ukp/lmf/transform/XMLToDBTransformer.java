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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
/**
 * Converts LMF resource saved in XML to Hibernate database
 * @author chebotar
 *
 */
public class XMLToDBTransformer implements ElementHandler{

	private final DBConfig dbConfig;		// Database configuration
	private Session dom4jSession;	// DOM4J Hibernate session for converting XML nodes to database
	private Session session;		// Hibernate session
	private Transaction tx;
	private final SessionFactory sessionFactory;

	private Element lexiconElement; // Current Lexicon - Node
	private Element lexicalResourceElement; // LexicalResource - Node

	private int commitCounter = 0;		// Counts how many elementsToCommit were saved

	private long lastReportTime;		// Needed for reporting of running time
	private long startTime;

	/** Elements that should be periodically committed in order to not overflow the memeory */
	private static Set<String> elementsToCommit = new HashSet<String>() {	    {
	        add("LexicalEntry");
	        add("Lexicon");
	        add("LexicalResource");
	        add("SubcategorizationFrame");
	        add("SubcategorizationFrameSet");
	        add("SemanticPredicate");
	        add("Synset");
	        add("SynSemCorrespondence");
	        add("ConstraintSet");
	       // add("SenseAxis");
	    }};

	/**
	 *
	 * @param cfg Hibernate Configuration
	 * @throws FileNotFoundException
	 */
	public XMLToDBTransformer(DBConfig dbConfig) throws FileNotFoundException{
		this.dbConfig = dbConfig;
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory();
		lastReportTime = 0;
		startTime= 0;

	}

	/**
	 * Read xml File and save its contents to Database
	 * @param xmlFile
	 * @throws DocumentException
	 */
	public void transform(File xmlFile, String lexicalResourceName, boolean constraints, boolean delete) throws DocumentException{
		lastReportTime = startTime = System.currentTimeMillis();

		openSession();
		LexicalResource lexicalResource = new LexicalResource();
		lexicalResource.setName(lexicalResourceName);
		if(delete)
		{
			LMFDBUtils.deleteLexicalResourceFromDatabase(lexicalResource, dbConfig);// Delete existing Lexical resource
		}
		if(constraints)
		{
			LMFDBUtils.turnOffConstraints(dbConfig);
		}
		SAXReader reader = new SAXReader();
		reader.setDefaultHandler(this);
		reader.read(xmlFile);
		closeSession();
		if(constraints)
		{
			LMFDBUtils.turnOnConstraints(dbConfig);
		}
		System.out.println("TOTAL TIME: "+( System.currentTimeMillis() - startTime));
		System.out.println("NUM ENTRIES: "+ commitCounter);

	}

	@Override
	/**
	 * Called when element is ended
	 */
	public void onEnd(ElementPath epath) {

		Element el = epath.getCurrent();
		String elName = el.getName();

		if(elementsToCommit.contains(elName)){ // Periodically commit the model to the database
			commitCounter++;
			if(commitCounter % 100 == 0){
				if(commitCounter % 1000 == 0){
					System.out.println(commitCounter + " "+(System.currentTimeMillis()-lastReportTime)+" milliseconds");
					lastReportTime= System.currentTimeMillis();
				}
				try{
					commit();
				}catch(Exception ex){
					System.err.println("COMMIT CAN'T SAVE "+elName+" "+ex.getMessage());
				//	ex.printStackTrace();
				}
			}
		}
	}


	@Override
	/**
	 * Called when element is started
	 */
	public void onStart(ElementPath epath) {

		Element el = epath.getCurrent();	// If the attribute value is NULL, remove this attribute							// because it can cause some errors during the transformation

		List<Attribute> toRemove = new ArrayList<Attribute>();
		for(int i = 0; i<el.attributeCount(); i++){
			Attribute att = el.attribute(i);
			if(att.getStringValue().equals("NULL")){
				toRemove.add(att);
				continue;
			}
			att.setValue(StringUtils.replaceNonUtf8(att.getValue())); // Replace all characters that can not be saved in the database
		}
		for(Attribute att : toRemove){
			el.remove(att);
		}

		String elName = el.getName();

		// TextRepresentation and FormRepresentation should be disambiguated by their parent
		if(elName.equals("TextRepresentation") || elName.equals("FormRepresentation")){
			elName += "_"+el.getParent().getName();
		}
		if(elName.equals("Lexicon")) {
			lexiconElement = el;
		}

		else if(elName.equals("LexicalResource")) {
			lexicalResourceElement = el;
		}
		try{
			dom4jSession.save(elName, el);
		}catch(Exception ex){
			System.err.println("CAN'T SAVE "+elName+" "+ex.getMessage());
			//ex.printStackTrace();
		}
	}

	/**
	 * Opens DOM4J session
	 */
	private void openSession(){
		session = sessionFactory.openSession();
		dom4jSession = session.getSession(EntityMode.DOM4J);
		tx = dom4jSession.beginTransaction();
	}

	/**
	 * Closes session
	 */
	private void closeSession(){
		tx.commit();
		session.close();
	}

	/**
	 * Commits changes made to the session, reopens the session
	 * and loads the Lexicon element in order for its children elements to have correct Lexicon IDs
	 */
	private void commit(){
		closeSession();
		lexicalResourceElement.clearContent();
		lexiconElement.clearContent();
		openSession();
		dom4jSession.update(lexicalResourceElement);
		dom4jSession.update(lexiconElement);

	}



}
