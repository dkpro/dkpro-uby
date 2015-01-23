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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract base class for all transformations of lexical resources. 
 * Extensions of this class may model original resource to database or UBY-XML
 * as well as from database to UBY-XML or vice-versa.
 * @author Christian M. Meyer
 */
public abstract class UBYTransformer {

	protected static final int COMMIT_STEP = 1000;
	protected int commitCounter;
	protected Map<Class<?>, UBYLMFClassMetadata> metadata;

	/** Mapping of Resource unique IDs to LMF unique IDs. */
	protected Map<String, String> idMapping;

	/** Current LMF ID for each LMF Class. */
	protected Map<Class<?>, Long> currentClassId;

	public UBYTransformer() {
		commitCounter = 0;
		metadata = new HashMap<Class<?>, UBYLMFClassMetadata>();
		
		idMapping = new TreeMap<String, String>();
		currentClassId = new HashMap<Class<?>, Long>();
	}

	/** Returns LMF class metadata for the specified class type. If the
	 *  metadata information is not yet in the cache, it will be newly
	 *  created using the reflection API. */
	protected UBYLMFClassMetadata getClassMetadata(final Class<?> clazz) {
		UBYLMFClassMetadata result = metadata.get(clazz);
		if (result != null)
			return result;
		
		result = new UBYLMFClassMetadata(clazz);
		metadata.put(clazz, result);
		return result;
	}

	/** Maps unique original ID to unique LMF ID. */
	protected String getLmfId(final Class<?> clazz, final String originalId) {
		String result = idMapping.get(originalId); 
		if (result != null)
			return result;
		
		Long currentId = currentClassId.get(clazz);
		if (currentId == null)
			currentId = 1L;

		String classId = clazz.getSimpleName();
		classId = classId.substring(0,1).toLowerCase() + classId.substring(1);

		result = getResourceAlias() + "_" + classId + "_" + currentId;
		idMapping.put(originalId, result);
		currentClassId.put(clazz, currentId + 1);
		return result;
	}

	/** Returns id of lexical resource. */
	protected abstract String getResourceAlias();

}
