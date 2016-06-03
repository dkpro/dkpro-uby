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

import java.util.HashMap;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;

/**
 * Transforms resource to LMF
 * @author chebotar
 * @deprecated THIS CLASS WILL BE REMOVED SOON. USE {@link UBYTransformer} INSTEAD.
 */
@Deprecated
abstract class LMFTransformer {
	// Mapping of Resource unique IDs to LMF unique IDs
	protected HashMap<String, String> idMapping;

	// Current LMF ID for each LMF Class
	@SuppressWarnings("rawtypes")
	protected HashMap<Class, Long> currentClassId;

	// ID of the resource
	protected String resourceAlias;

	// Name of the resource
	protected String resourceName;


	/**
	 * Creates LMFTransformer, which writes to LMFXmlWriter
	 * @param writer
	 */
	@SuppressWarnings("rawtypes")
	public LMFTransformer(){
		idMapping = new HashMap<String, String>();
		currentClassId = new HashMap<Class, Long>();
	}
	/**
	 * Transforms Resource to LMF
	 * @throws LMFWriterException
	 */
	public abstract void transform() throws Exception;
	// public abstract void transform(/*boolean constraints, boolean delete*/)
	
	/**
	 * Maps unique original ID to unique LMF ID
	 * @param resourceAlias
	 * @return
	 */
	protected String getLmfId(@SuppressWarnings("rawtypes") Class clazz, String originalId){
		if(idMapping.containsKey(originalId)) {
			return idMapping.get(originalId);
		}
		else{
			long currentId = 1;
			if(currentClassId.containsKey(clazz)){
				currentId = currentClassId.get(clazz);
			}
			String classId = clazz.getSimpleName();
			classId = classId.substring(0,1).toLowerCase() + classId.substring(1);

			String newLmfId = resourceAlias+"_"+classId+"_"+currentId;
			idMapping.put(originalId, newLmfId);
			currentClassId.put(clazz, currentId+1);
			return newLmfId;
		}
	}

	/**
	 * Creates LexicalResource object
	 * @return
	 */
	protected abstract LexicalResource createLexicalResource();
	/**
	 * Creates next Lexicon object
	 * @return
	 */
	protected abstract Lexicon createNextLexicon();
	/**
	 * Returns next LexicalEntry that should be stored in LMF
	 * @return
	 */
	protected abstract LexicalEntry getNextLexicalEntry();

	/**
	 * Returns next SubcategorizationFrame that should be stored in LMF
	 * @return
	 */
	protected abstract SubcategorizationFrame getNextSubcategorizationFrame();

	/**
	 * Returns next SubcategorizationFrameSet that should be stored in LMF
	 * @return
	 */
	protected abstract SubcategorizationFrameSet getNextSubcategorizationFrameSet();
	/**
	 * Returns next SemanticPredicate that should be stored in LMF
	 * @return
	 */
	protected abstract SemanticPredicate getNextSemanticPredicate();
	/**
	 * Returns next Synset that should be stored in LMF
	 * @return
	 */
	protected abstract Synset getNextSynset();
	/**
	 * Returns next SynSemCorrespondence that should be stored in LMF
	 * @return
	 */
	protected abstract SynSemCorrespondence getNextSynSemCorrespondence();
	/**
	 * Returns next ConstraintSet that should be stored in LMF
	 * @return
	 */
	protected abstract ConstraintSet getNextConstraintSet();

	/**
	 * Returns next SenseAxis that should be stored in LMF
	 * @return
	 */
	protected abstract SenseAxis getNextSenseAxis();

	/**
	 * Returns next PredicateArgumentAxis that should be stored in LMF
	 * @return
	 */
	protected abstract PredicateArgumentAxis getNextPredicateArgumentAxis();
	
	/**
	 * Returns id of lexical resource
	 * @return
	 */
	protected abstract String getResourceAlias();


	/**
	 * Finalize the transformation
	 */
	protected abstract void finish();
}
