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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.writer.LMFWriter;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * Transforms resource to LMF database
 * @author chebotar
 */
@SuppressWarnings("unchecked")
public abstract class LMFDBTransformer extends LMFTransformer{

	private DBConfig dbConfig;
	private Session session;
	private Transaction tx;
	private SessionFactory sessionFactory;
	private Configuration cfg;

	private int commitNumber = 100;
	private int commitCounter = 0;

	private LexicalResource lexicalResource;
	/**
	 * Creates LMFTransformer, which writes to LMFXmlWriter
	 * @param writer
	 * @throws FileNotFoundException
	 */
	public LMFDBTransformer(DBConfig dbConfig) throws FileNotFoundException{
		super();
		this.dbConfig = dbConfig;
		cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory();
	}

	/**
	 * Transforms Resource to LMF sequentially
	 */
	@Override
	public void transform(/*boolean constraints, boolean delete*/){ //TODO YC Remove constraints and delete
		System.out.println("START DB TRANSFORM");
		openSession();
		resourceAlias = getResourceAlias();
		lexicalResource = createLexicalResource();
		/*if(delete) {
			LMFDBUtils.deleteLexicalResourceFromDatabase(lexicalResource, dbConfig); // Delete LexicalResource before importing new one
		}
		if(constraints) {
			LMFDBUtils.turnOffConstraints(dbConfig); // We need to turn off some constraints in order not to get
		}*/
												 // constraint errors during the import
		lexicalResource.setLexicons(new ArrayList<Lexicon>());
		saveCascade(lexicalResource, null);

		Lexicon lexicon;
		while((lexicon = createNextLexicon()) != null){
			lexicalResource.getLexicons().add(lexicon);
			lexicon.setLexicalEntries(new ArrayList<LexicalEntry>(commitNumber));
			lexicon.setSubcategorizationFrames(new ArrayList<SubcategorizationFrame>(commitNumber));
			lexicon.setSemanticPredicates(new ArrayList<SemanticPredicate>(commitNumber));
			lexicon.setSynsets(new ArrayList<Synset>(commitNumber));
			lexicon.setSynSemCorrespondences(new ArrayList<SynSemCorrespondence>(commitNumber));
			lexicon.setConstraintSets(new ArrayList<ConstraintSet>(commitNumber));
			saveCascade(lexicon,lexicalResource);

			LexicalEntry lexEntry;
			while((lexEntry = getNextLexicalEntry()) != null) {
				saveListElement(lexicon, lexicon.getLexicalEntries(), lexEntry);
			}
			commit();
			session.update(lexicon);

			SubcategorizationFrame subCatFrame;
			while((subCatFrame = getNextSubcategorizationFrame()) != null) {
				saveListElement(lexicon, lexicon.getSubcategorizationFrames(), subCatFrame);
			}
			commit();
			session.update(lexicon);

			SubcategorizationFrameSet subCatFrameSet;
			while((subCatFrameSet = getNextSubcategorizationFrameSet()) != null) {
				saveListElement(lexicon, lexicon.getSubcategorizationFrameSets(), subCatFrameSet);
			}
			commit();
			session.update(lexicon);


			SemanticPredicate semPredicate;
			while((semPredicate = getNextSemanticPredicate()) != null) {
				saveListElement(lexicon, lexicon.getSemanticPredicates(), semPredicate);
			}
			commit();
			session.update(lexicon);

			Synset synset;
			while((synset = getNextSynset()) != null) {
				saveListElement(lexicon, lexicon.getSynsets(), synset);
			}
			commit();
			session.update(lexicon);

			SynSemCorrespondence synSemCorrespondence;
			while((synSemCorrespondence = getNextSynSemCorrespondence()) != null) {
				saveListElement(lexicon, lexicon.getSynSemCorrespondences(), synSemCorrespondence);
			}
			commit();
			session.update(lexicon);


			ConstraintSet constraintSet;
			while((constraintSet = getNextConstraintSet()) != null) {
				saveListElement(lexicon, lexicon.getConstraintSets(), constraintSet);
			}
			commit();
			session.update(lexicon);
		}

		SenseAxis senseAxis;
		while((senseAxis = getNextSenseAxis()) != null) {
			saveListElement(lexicalResource, lexicalResource.getSenseAxes(), senseAxis);
		}
		commit();

		finish();
		closeSession();
		// Turn on the constraints in order to have consistent connections
		// in the database (e.g. to delete cascade in the future)
		/*if(constraints) {
			LMFDBUtils.turnOnConstraints(dbConfig);
		}*/
	}

	/**
	 * Saves transformed lexcialResource to XML
	 * @param writer XML writer
	 * @throws LMFWriterException
	 * @throws FileNotFoundException
	 */
	public void saveToXML(LMFWriter writer) throws LMFWriterException, FileNotFoundException{
		DBToXMLTransformer dbToXml = new DBToXMLTransformer(dbConfig, writer);
		dbToXml.transform(lexicalResource);
	}

	/**
	 * Saves child element of the parent to the list and updates the parent
	 * Periodically commits to the database
	 * @param parent
	 * @param list
	 * @param child
	 */
	private void saveListElement(Object parent, @SuppressWarnings("rawtypes") List list, Object child){
		commitCounter ++;
		list.add(child);
		saveCascade(child, parent);
		if(commitCounter % commitNumber == 0){
			commit();
			session.update(parent);
			list.clear();
		}
	}

	/**
	 * Opens Hibernate session
	 */
	private void openSession(){
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
	}

	/**
	 * Closes Hibernate session
	 */
	private void closeSession(){
		tx.commit();
		session.close();
	}

	/**
	 * Commits changes made to the Hibernate session
	 */
	private void commit(){
		closeSession();
		openSession();
	}

	/**
	 * Returns object from the Hibernate database by its class and id
	 * @param clazz
	 * @param id
	 * @return
	 */
	protected Object getLmfObjectById(@SuppressWarnings("rawtypes") Class clazz, String id){
		Object obj = session.get(clazz, id);
		return obj;
	}


	/**
	 * Saves all elements of the list and updates their parent
	 * (in contrast to saveCascade(), which would also save the parent and lead to
	 * "element already exists" Hibernate exception)
	 * @param parent
	 * @param list
	 */
	protected void saveList(Object parent, @SuppressWarnings("rawtypes") List list){
		for(Object obj : list){
			saveCascade(obj, parent);
		}
		session.update(parent);
	}

	/**
	 * Updates the object in the Hibernate session
	 * @param obj
	 */
	protected void update(Object obj){
		session.update(obj);
	}

	/**
	 * Saves element and all its children to the Hibernate session
	 * @param obj
	 * @param parent
	 */
	@SuppressWarnings("rawtypes")
	protected void saveCascade(Object obj, Object parent){
		Class objClass = obj.getClass();
		obj.toString();	// It can happen that a Hibernate object is not initialized properly
						// --> force initialization of object by calling its toString() method

		Class parentClass = objClass;
		if(parent != null){
			parentClass = parent.getClass();
			parent.toString();	// Force initialization of parent by calling its toString() method
		}

		try{
			session.save(obj);
		}catch(Exception ex){
			try{ // If no correct mapping was found, try to save according to
				 // the disambiguated entity-name like "TextRepresentation_Definition" or "FormRepresentation_Lemma"
				session.save(objClass.getSimpleName()+"_"+parentClass.getSimpleName(), obj);
			}catch(Exception ex2){
				System.out.println("CAN'T SAVE "+objClass.getSimpleName()+" PARENT: "+parentClass.getSimpleName() +": "+ex.getMessage());
			}
		}
		
		// Find all fields including inherited fields. 
		ArrayList<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(objClass.getDeclaredFields()));
		Class<?> superClass = objClass.getSuperclass();
		while (superClass != null){
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
				
		// Iterating over all fields
		for(Field field : fields){
			String fieldName = field.getName().replace("_", "");
			VarType varType = field.getAnnotation(VarType.class);
			// No VarType-Annotation found for the field
			if(varType == null) {
				continue;
			}

			EVarType type = varType.type();
			if(!(type.equals(EVarType.CHILD) || type.equals(EVarType.CHILDREN))) {
				continue;
			}
			// Get-Method for the field
			String getFuncName = "get"+fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);

			try{
				Method getMethod = objClass.getMethod(getFuncName);
				Object retObj = getMethod.invoke(obj); // Run the Get-Method

				if(retObj != null){
					if(type.equals(EVarType.CHILD)) {
						saveCascade(retObj, obj);
					}
					else if(type.equals(EVarType.CHILDREN)){
						for(Object el : (Iterable)retObj){
							saveCascade(el, obj);
						}
					}
				}
			}catch(Exception ex){
				System.out.println("CAN'T SAVE "+objClass.getSimpleName()+": "+ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Saves element and all its children without given parent
	 * @param obj
	 */
	protected void saveCascade(Object obj){
		saveCascade(obj, null);
	}
}
