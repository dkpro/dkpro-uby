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

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;

/**
 * Universal {@link Iterator} that can be used for iteration over any UBY-LMF element.
 *  
 * Needs a Hibernate-Criteria object with predefined selection/filtering settings
 * 
 * @author Yevgen Chebotar
 * 
 * @param <T> Class of the object that is iterated
 */
@SuppressWarnings("unchecked")
public class CriteriaIterator<T>  implements Iterator<T> {
	
	private int bufferSize;	    // Max. number of objects to load into memory
	private DetachedCriteria criteria;  // Hibernate Criteria
	private Iterator<T> buffer;	// Buffer with loaded objects
	private int firstResult;	// Current result number
	private Session session;	// Current hibernate session
	private SessionFactory sessionFactory; // Hibernate session factory
	
	
	/**
	 * @param criteria   Criteria which holds selection settings for the iterated element
	 * @param bufferSize Max. number of objects to load into memory
	 */
	public CriteriaIterator(DetachedCriteria criteria, SessionFactory sessionFactory, int bufferSize){
		this.bufferSize = bufferSize;
		this.criteria = criteria;	
		this.sessionFactory = sessionFactory;
		this.firstResult = 0;		
	}
	
	@Override
	public boolean hasNext() {
		boolean hasNext = buffer==null ? false : buffer.hasNext();		
		if(!hasNext)
			hasNext = fillBuffer();
		return hasNext;
	}

	@Override
	public T next() {		
		firstResult++;
		return buffer.next();		
	}
	
	/**
	 * Fills the buffer with objects from the database.
	 */
	private boolean fillBuffer(){
		if(session != null && session.isOpen())
			session.close();
		session = sessionFactory.openSession();		
		Criteria execCriteria = criteria.getExecutableCriteria(session);
		
		@SuppressWarnings("rawtypes")
		List result = execCriteria.setFirstResult(firstResult)
			.setMaxResults(bufferSize).list();		
		
		if(result.size() == 0){
			if(session != null)
				session.close();
			return false;		
		}
		buffer = result.iterator();
		return true;
	}
	
	@Override
	public void remove() {				
	}
	
}
