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
import de.tudarmstadt.ukp.lmf.writer.LMFWriter;
import de.tudarmstadt.ukp.lmf.writer.LMFWriterException;

/**
 * Transforms resource to LMF XML
 * @author chebotar
 * @deprecated THIS CLASS WILL BE REMOVED SOON. USE {@link UBYXMLTransformer} INSTEAD.
 */
@Deprecated
abstract class LMFXMLTransformer extends LMFTransformer{	
	// LMF Writer
	protected LMFWriter writer;
	
	/**
	 * Creates LMFTransformer, which writes to LMFXmlWriter
	 * @param writer
	 */
	public LMFXMLTransformer(LMFWriter writer){
		super();
		this.writer = writer;	
	}
	/**
	 * Transforms Resource to LMF
	 * @throws LMFWriterException
	 */
	public void transform() throws LMFWriterException{
		resourceAlias = getResourceAlias();
		LexicalResource resource = createLexicalResource();		
		writer.writeStartElement(resource);
		
		Lexicon lexicon;
		while((lexicon = createNextLexicon()) != null){
			writer.writeStartElement(lexicon);
			
			LexicalEntry lexEntry;
			while((lexEntry = getNextLexicalEntry()) != null)
				writer.writeElement(lexEntry);			
						
			SubcategorizationFrame subCatFrame;
			while((subCatFrame = getNextSubcategorizationFrame()) != null)
				writer.writeElement(subCatFrame);
			
			SubcategorizationFrameSet subCatFrameSet;
			while((subCatFrameSet = getNextSubcategorizationFrameSet()) != null)
				writer.writeElement(subCatFrameSet);
			
			SemanticPredicate semPredicate;
			while((semPredicate = getNextSemanticPredicate()) != null)
				writer.writeElement(semPredicate);
			
			Synset synset;
			while((synset = getNextSynset()) != null)
				writer.writeElement(synset);
			
			SynSemCorrespondence synSemCorrespondence;
			while((synSemCorrespondence = getNextSynSemCorrespondence()) != null)
				writer.writeElement(synSemCorrespondence);
			
			ConstraintSet constraintSet;
			while((constraintSet = getNextConstraintSet()) != null)
				writer.writeElement(constraintSet);
			
			writer.writeEndElement(lexicon);
		}
		
		SenseAxis senseAxis;
		while((senseAxis = getNextSenseAxis()) != null)
			writer.writeElement(senseAxis);

		PredicateArgumentAxis predAxis;
		while((predAxis = getNextPredicateArgumentAxis()) != null)
			writer.writeElement(predAxis);
		
		writer.writeEndElement(resource);
		writer.writeEndDocument();
		finish();
	}
}
