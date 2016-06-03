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
package de.tudarmstadt.ukp.lmf.transform;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import de.tudarmstadt.ukp.lmf.hibernate.HibernateConnect;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasParentSpecificTable;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.transform.UBYLMFClassMetadata.UBYLMFFieldMetadata;

/**
 * Abstract base class for all resource transformations requiring a 
 * Hibernate session (i.e., transformations requring database access).
 * @author Christian M. Meyer
 */
public abstract class UBYHibernateTransformer extends UBYXMLTransformer {

	protected Session session;
	protected Transaction tx;
	protected SessionFactory sessionFactory;	

	public UBYHibernateTransformer(final DBConfig dbConfig) {
		super();
		Configuration cfg = HibernateConnect.getConfiguration(dbConfig);
		sessionFactory = cfg.buildSessionFactory(
				new ServiceRegistryBuilder().applySettings(
				cfg.getProperties()).buildServiceRegistry());
	}

	/** Adds the given child element to the list (which should be a member of
	 *  the specified parent), saves the child, and updates the parent. To 
	 *  reduce the memory footprint, the changes are periodically committed, 
	 *  and the parent object is cleared. */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void saveListElement(final Object parent, final List list, 
			final Object child) {
		list.add(child);
		saveCascade(child, parent);
		commitCounter++;
		if(commitCounter % COMMIT_STEP == 0){
			commit();
			session.clear();
			session.update(parent);
			list.clear();
		}
	}

	/** Saves all elements of the list and updates their parent (in contrast 
	 *  to {@link #saveCascade(Object, Object)}, which would also save the 
	 *  parent and lead to "element already exists" Hibernate exception). */
	@SuppressWarnings("rawtypes")
	protected void saveList(final Object parent, final List list) {
		for (Object obj : list)
			saveCascade(obj, parent);
		session.update(parent);
	}

	/** Saves the specified object and all its children to the Hibernate session.
	 *  For objects implementing the {@link IHasParentSpecificTable} interface,
	 *  a parent should be specified, which is used to derive the table name. */
	protected void saveCascade(final Object obj, final Object parent){
		Class<?> objClass = obj.getClass();
		obj.toString();	// It can happen that a Hibernate object is not initialized properly
						// --> force initialization of object by calling its toString() method

		Class<?> parentClass = objClass;
		if(parent != null){
			parentClass = parent.getClass();
			parent.toString();	// Force initialization of parent by calling its toString() method
		}
		
		// Identify dependent child objects and save them.
		UBYLMFClassMetadata classMeta = getClassMetadata(objClass);			
		for (UBYLMFFieldMetadata fieldMeta : classMeta.getFields()) {
			EVarType varType = fieldMeta.getVarType();
			if (varType != EVarType.CHILDREN && varType != EVarType.CHILD)
				continue;
			
			Method getter = fieldMeta.getGetter();
			Object retObj = null;
			try {
				retObj = getter.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (retObj == null)
				continue;
			
			if (varType == EVarType.CHILD)
				saveCascade(retObj, obj);
			else 
			if (varType == EVarType.CHILDREN)
				for (Object el : (Iterable<?>) retObj)
					saveCascade(el, obj);
		}
		
		try {
			if (obj instanceof IHasParentSpecificTable)
				session.save(objClass.getSimpleName() + "_" + parentClass.getSimpleName(), obj);
			else
				session.save(obj);
		} catch(Exception ex) {
			System.err.println("CAN'T SAVE "+objClass.getSimpleName()+" PARENT: "+parentClass.getSimpleName() +": "+ex.getMessage());
		}
	}

	/** Shorthand for {@link #saveCascade(Object, Object)} with an empty 
	 *  parent (i.e., the second parameter is null). */
	protected void saveCascade(final Object obj){
		saveCascade(obj, null);
	}

	/** Returns the object of the given class with the specified ID from the 
	 *  Hibernate session. */
	protected Object getLmfObjectById(final Class<?> clazz, final String id) {
		return session.get(clazz, id);
	}

	/** Opens a Hibernate session. */
	protected void openSession() {
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
	}

	/** Closes the Hibernate session. */
	protected void closeSession() {
		tx.commit();
		session.close();
	}

	/** Commits changes made to the Hibernate session. */
	protected void commit() {
		System.out.println(new Date(System.currentTimeMillis()) + ": COMMIT " + commitCounter);
		tx.commit();
		tx = session.beginTransaction();
	}
		
}
