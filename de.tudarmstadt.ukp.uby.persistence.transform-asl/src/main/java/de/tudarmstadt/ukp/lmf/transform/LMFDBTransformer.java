/*******************************************************************************
 * Copyright 2017
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

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;

/**
 * Basic transformation of a lexical resource into a UBY database. Extend this
 * class to convert the resource-specific information.
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public abstract class LMFDBTransformer extends UBYHibernateTransformer {

	/** Initialize a new transformer for writing to the database with the
	 *  specified configuration. */
	public LMFDBTransformer(DBConfig dbConfig) throws FileNotFoundException{
		super(dbConfig);
	}
	
	/** Start the transformation. That is, the transformer will sequentially
	 *  invoke the createNext* methods which are to convert the 
	 *  resource-specific information types into UBY-LMF model objects. */
	public void transform() {
		System.out.println("START DB TRANSFORM");
		openSession();

		LexicalResource lexicalResource = createLexicalResource();
		saveCascade(lexicalResource, null);

		Lexicon lexicon;
		while ((lexicon = createNextLexicon()) != null) {
			lexicalResource.getLexicons().add(lexicon);
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
	}

	/** Creates LexicalResource object. */
	protected abstract LexicalResource createLexicalResource();
	
	/** Creates next Lexicon object. */
	protected abstract Lexicon createNextLexicon();
	
	/** Returns next LexicalEntry that should be stored in LMF. */
	protected abstract LexicalEntry getNextLexicalEntry();

	/** Returns next SubcategorizationFrame that should be stored in LMF. */
	protected abstract SubcategorizationFrame getNextSubcategorizationFrame();

	/** Returns next SubcategorizationFrameSet that should be stored in LMF. */
	protected abstract SubcategorizationFrameSet getNextSubcategorizationFrameSet();
	
	/** Returns next SemanticPredicate that should be stored in LMF. */
	protected abstract SemanticPredicate getNextSemanticPredicate();
	
	/** Returns next Synset that should be stored in LMF. */
	protected abstract Synset getNextSynset();
	
	/** Returns next SynSemCorrespondence that should be stored in LMF. */
	protected abstract SynSemCorrespondence getNextSynSemCorrespondence();
	
	/** Returns next ConstraintSet that should be stored in LMF. */
	protected abstract ConstraintSet getNextConstraintSet();

	/** Returns next SenseAxis that should be stored in LMF. */
	protected abstract SenseAxis getNextSenseAxis();

	/** Finalize the transformation. */
	protected abstract void finish();

}
