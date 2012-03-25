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
package de.tudarmstadt.ukp.lmf.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;

/**
 * Universal iterator that can be used for iteration over any LMF element. 
 * Needs a Hibernate-Criteria object with predefined selection/filtering settings
 * @author chebotar
 *
 * @param <T> Class of the object that is iterated
 */
@SuppressWarnings("unchecked")
public class CriteriaIterator<T>  implements Iterator<T> {
	
	private int bufferSize;	    // Max. number of objects to load into memory
	private Criteria criteria;  // Hibernate Criteria
	private Iterator<T> buffer;	// Buffer with loaded objects
	private int firstResult;	// Current result number
	
	/**
	 * @param criteria   Criteria which holds selection settings for the iterated element
	 * @param bufferSize Max. number of objects to load into memory
	 */
	public CriteriaIterator(Criteria criteria, int bufferSize){
		this.bufferSize = bufferSize;
		this.criteria = criteria;	
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
	 * Fills the buffer with objects from the database
	 * @return
	 */
	private boolean fillBuffer(){
		List result = criteria.setFirstResult(firstResult)
			.setMaxResults(bufferSize).list();		
		
		if(result.size() == 0)
			return false;		
		buffer = result.iterator();
		return true;
	}
	
	@Override
	public void remove() {				
	}
	
}
