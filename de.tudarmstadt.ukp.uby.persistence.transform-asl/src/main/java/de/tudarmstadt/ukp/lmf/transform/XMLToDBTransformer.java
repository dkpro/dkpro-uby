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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.exceptions.UbyInvalidArgumentException;
import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
/**
 * Converts LMF resource saved in XML to Hibernate database
 * @author chebotar
 *
 */
public class XMLToDBTransformer implements ElementHandler {

	private DBConfig dbConfig;
	private Session session;
	private Transaction tx;
	private SessionFactory sessionFactory;
	private Configuration cfg;

	private int commitNumber = 1000;
	private int commitCounter = 0;

	private LexicalResource lexicalResource;	// Current lexical resource
	private Lexicon lexicon;					// Current lexicon
	private boolean externalLexicalResource;
	private long startTime;

	public XMLToDBTransformer(DBConfig dbConfig) throws FileNotFoundException {
		this.dbConfig = dbConfig;
		cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory();
	}
	

	/**
	 * Read xml File and save its contents to Database
	 * @param xmlFile
	 * @param lexicalResourceName
	 * @throws DocumentException
	 * @throws UbyInvalidArgumentException 
	 */
	public void transform(File xmlFile, String lexicalResourceName) throws DocumentException, UbyInvalidArgumentException{
		startTime = System.currentTimeMillis();

		openSession();

		if(lexicalResourceName != null)
			lexicalResource = (LexicalResource) session.get(LexicalResource.class, lexicalResourceName);
	
		SAXReader reader = new SAXReader();
		reader.setDefaultHandler(this);
		reader.read(xmlFile);
		commit();
		closeSession();
	
		System.out.println("TOTAL TIME: "+( System.currentTimeMillis() - startTime));
		System.out.println("NUM ENTRIES: "+ commitCounter);
		
	}


	@Override
	public void onStart(ElementPath epath) {
		saveElement(epath.getCurrent(), true);
	}
	

	@Override
	public void onEnd(ElementPath epath) {
		saveElement(epath.getCurrent(), false);		
	}
	
	/**
	 * Saves a single XML-Element to the database
	 * @param el
	 * @param start
	 */
	public void saveElement(Element el, boolean start){
		
		String n = el.getName();	
		if(start && !n.equals("LexicalResource") && !n.equals("Lexicon"))
			return;
		
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
		if(n.equals("LexicalResource"))
			saveLexicalResource(el, start);
		else if (n.equals("Lexicon"))
			saveLexicon(el, start);
		else if (n.equals("LexicalEntry"))
			saveLexicalEntry(el);
		else if (n.equals("SubcategorizationFrame"))
			saveSubcategorizationFrame(el);
		else if (n.equals("SubcategorizationFrameSet"))
			saveSubcategorizationFrameSet(el);
		else if (n.equals("Synset"))
			saveSynset(el);
		else if (n.equals("SynSemCorrespondence"))
			saveSynSemCorrespondence(el);
		else if (n.equals("ConstraintSet"))
			saveConstraintSet(el);
		else if (n.equals("SenseAxis"))
			saveSenseAxis(el);
		else return;
	}
	
	/**
	 * Save LexicalResource element
	 * @param el XML Element
	 * @param start - True if it the element tag is a start tag
	 * @return
	 */
	protected void saveLexicalResource(Element el, boolean start){
		if(start){
			if(lexicalResource == null){
				lexicalResource = new LexicalResource();
				lexicalResource.setName(el.attributeValue("name"));				
				lexicalResource.setDtdVersion(el.attributeValue("dtdVersion"));
				lexicalResource.setLexicons(new ArrayList<Lexicon>());
				saveCascade(lexicalResource, null);
			}else externalLexicalResource = true;
		}else if (!externalLexicalResource){
			Element glElement = el.element("GlobalInformation");			
			GlobalInformation glInformation = new GlobalInformation();
			glInformation.setLabel(glElement.attributeValue("label"));
			session.save(glInformation);
			lexicalResource.setGlobalInformation(glInformation);
			session.update(lexicalResource);
		}		
	}
	/**
	 * Saves Lexicon object
	 * @param el XML Element
	 * @param start - True if it the element tag is a start tag
	 * @return
	 */
	protected void saveLexicon(Element el, boolean start){
		if(start){
			lexicon = new Lexicon();	
			lexicon.setId(el.attributeValue("id"));
			lexicon.setName(el.attributeValue("name"));
			lexicon.setLanguageIdentifier(el.attributeValue("languageIdentifier"));
			
			lexicon.setLexicalEntries(new ArrayList<LexicalEntry>(commitNumber));
			lexicon.setSubcategorizationFrames(new ArrayList<SubcategorizationFrame>(commitNumber));
			lexicon.setSemanticPredicates(new ArrayList<SemanticPredicate>(commitNumber));
			lexicon.setSynsets(new ArrayList<Synset>(commitNumber));
			lexicon.setSynSemCorrespondences(new ArrayList<SynSemCorrespondence>(commitNumber));
			lexicon.setConstraintSets(new ArrayList<ConstraintSet>(commitNumber));
			lexicalResource.getLexicons().add(lexicon);
			
			saveCascade(lexicon,lexicalResource);
			
		}		
	}
	
	
	/**
	 * Save LexicalEntry element
	 * @return
	 */
	protected void saveLexicalEntry(Element el){
		LexicalEntry lexEntry = (LexicalEntry)fromXmlToObject(el, LexicalEntry.class);
		saveListElement(lexicon, lexicon.getLexicalEntries(), lexEntry);	
	}
	
	
	/**
	 * Returns next SubcategorizationFrame that should be stored in LMF
	 * @return
	 */
	protected void saveSubcategorizationFrame(Element el){

		SubcategorizationFrame obj = (SubcategorizationFrame)fromXmlToObject(el, SubcategorizationFrame.class);
		saveListElement(lexicon, lexicon.getSubcategorizationFrames(), obj);
	}

	/**
	 * Returns next SubcategorizationFrameSet that should be stored in LMF
	 * @return
	 */
	protected void saveSubcategorizationFrameSet(Element el){

		SubcategorizationFrameSet obj = (SubcategorizationFrameSet)fromXmlToObject(el, SubcategorizationFrameSet.class);
		saveListElement(lexicon, lexicon.getSubcategorizationFrameSets(), obj);
	}
	/**
	 * Returns next SemanticPredicate that should be stored in LMF
	 * @return
	 */
	protected void saveSemanticPredicate(Element el){
		SemanticPredicate obj = (SemanticPredicate)fromXmlToObject(el, SemanticPredicate.class);
		saveListElement(lexicon, lexicon.getSemanticPredicates(), obj);
	}
	/**
	 * Returns next Synset that should be stored in LMF
	 * @return
	 */
	protected void saveSynset(Element el){
		Synset obj = (Synset)fromXmlToObject(el, Synset.class);
		saveListElement(lexicon, lexicon.getSynsets(), obj);
	}
	/**
	 * Returns next SynSemCorrespondence that should be stored in LMF
	 * @return
	 */
	protected void saveSynSemCorrespondence(Element el){
		SynSemCorrespondence obj = (SynSemCorrespondence)fromXmlToObject(el, SynSemCorrespondence.class);
		saveListElement(lexicon, lexicon.getSynSemCorrespondences(), obj);
	}
	/**
	 * Returns next ConstraintSet that should be stored in LMF
	 * @return
	 */
	protected void saveConstraintSet(Element el){
		ConstraintSet obj = (ConstraintSet)fromXmlToObject(el, ConstraintSet.class);
		saveListElement(lexicon, lexicon.getConstraintSets(), obj);
	}

	/**
	 * Returns next SesnseAxis that should be stored in LMF
	 * @return
	 */
	protected void saveSenseAxis(Element el){
		SenseAxis obj = (SenseAxis)fromXmlToObject(el, SenseAxis.class);
		saveListElement(lexicalResource, lexicalResource.getSenseAxes(), obj);
	}	
	

	/**
	 * Transforms XML-Element and all its children to Java object
	 * @param el XML-Element
	 * @param clazz Java-Class of the Element
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Object fromXmlToObject(Element el, Class clazz)  {			
		try{
			Object lmfObject = clazz.newInstance();
					
			// Iterating over all fields
			HashMap<Field, Class> fields = GenericUtils.getFields(clazz);
			for(Field field : fields.keySet()){
				String fieldName = field.getName().replace("_", "");
				Class fieldClass = fields.get(field);
				VarType varType = field.getAnnotation(VarType.class);
				// No VarType-Annotation found for the field, then don't save to DB 
				if(varType == null)
					continue;
			
				EVarType type = varType.type();
				// VarType is NONE, don't save to XML
				if(type.equals(EVarType.NONE))
					continue;
				
				// Set-Method for the field
				String setFuncName = "set"+fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				try{
					Method setMethod = null;
					try{
						setMethod = clazz.getMethod(setFuncName, fieldClass);
					}catch(NoSuchMethodException e){
						setMethod = clazz.getMethod(setFuncName, Object.class);
					}
					
					if(type.equals(EVarType.ATTRIBUTE)){ // Save attribute
						String value = el.attributeValue(fieldName);
						if(value != null){
							Object valueToSet = value;
							if(fieldClass.equals(boolean.class) || fieldClass.equals(Boolean.class))
								valueToSet = GenericUtils.getBoolean(value);
							else if (fieldClass.equals(int.class) || fieldClass.equals(Integer.class))
								valueToSet = GenericUtils.getInteger(value);
							else if (fieldClass.isEnum()){
								valueToSet = GenericUtils.getEnum(fieldClass, value);
							}
							setMethod.invoke(lmfObject, valueToSet);
						}
					}else if(type.equals(EVarType.CHILD)){ // Save child

						Element childEl = el.element(fieldClass.getSimpleName());			
						if(childEl != null){
							Object childObj = fromXmlToObject(childEl, fieldClass);
							setMethod.invoke(lmfObject, childObj);	
						}
					}else if(type.equals(EVarType.CHILDREN)){ //Save list

						List list = new ArrayList();
						Class elementClass = GenericUtils.getListElementClass(field);
						if(elementClass != null){
							for(Object childEl : el.elements(elementClass.getSimpleName())){
								Object childObj = fromXmlToObject((Element)childEl, elementClass);
								list.add(childObj);
							}
							setMethod.invoke(lmfObject, list);
						}
					}else if(type.equals(EVarType.IDREF)){ // Save IDREF as object

						String id = el.attributeValue(fieldName);
						if(id != null && !id.isEmpty()){
							IHasID obj = (IHasID)fieldClass.newInstance();						
							obj.setId(id);
							setMethod.invoke(lmfObject, obj);
						}
					}else if(type.equals(EVarType.IDREFS)){ // Save IDREFS as list

						List list = new ArrayList();
						String idStr = el.attributeValue(fieldName);							
						if(idStr != null && !idStr.isEmpty()){
							String ids[] = idStr.split(" ");
							Class elementClass = GenericUtils.getListElementClass(field);
							if(elementClass == null)
								return null;
							
							for(String id : ids){
								IHasID obj = (IHasID)elementClass.newInstance();
								obj.setId(id);
								list.add(obj);
							}
							setMethod.invoke(lmfObject, list);
						}
					}				
				}catch(Exception e){
					e.printStackTrace();
				}			
			}
			return lmfObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
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
				System.err.println("CAN'T SAVE "+objClass.getSimpleName()+": "+ex.getMessage());
				ex.printStackTrace();
			}
		}

		try{
			session.save(obj);
		}catch(Exception ex){
			try{ // If no correct mapping was found, try to save according to
				 // the disambiguated entity-name like "TextRepresentation_Definition" or "FormRepresentation_Lemma"
				session.save(objClass.getSimpleName()+"_"+parentClass.getSimpleName(), obj);
			}catch(Exception ex2){
				System.err.println("CAN'T SAVE "+objClass.getSimpleName()+" PARENT: "+parentClass.getSimpleName() +": "+ex.getMessage());
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
		System.out.println(new Date(System.currentTimeMillis())+": COMMIT "+commitCounter);
		closeSession();
		openSession();
	}
		
	public static void main(String[] args){
		try{
			
			DBConfig dbConfig = new DBConfig("localhost/uby2","com.mysql.jdbc.Driver","mysql","root", "", true, false);
			LMFDBUtils.createTables(dbConfig);
			//LMFDBUtils.updateTables(dbConfig);
			XMLToDBTransformer trans = new XMLToDBTransformer(dbConfig);
			
			//File xmlFile = new File("e:/Dokumente/HiWi/uby/GN7_0UbyLMF.xml");
			File xmlFile = new File("C:/uby/ubyTestLexicon.xml");
			trans.transform(xmlFile, "UBY");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
