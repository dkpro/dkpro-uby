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


import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;

/**
 * This class represents an {@link Iterable} for {@link CriteriaIterator}.
 * 
 * @author Yevgen Chebotar
 *
 * @param <T> Class of the object that is iterated
 */
public class CriteriaIterable<T> implements Iterable<T> {
	
	private int bufferSize;
	private DetachedCriteria criteria;
	private SessionFactory sessionFactory;

	public CriteriaIterable(DetachedCriteria criteria, SessionFactory sessionFactory, int bufferSize){
		this.criteria = criteria;
		this.sessionFactory = sessionFactory;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public Iterator<T> iterator() {		
		return new CriteriaIterator<T>(criteria, sessionFactory, bufferSize);
	}
	
}
