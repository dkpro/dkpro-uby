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
package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.util.ArrayList;
import java.util.List;

public class OntoWiktionaryConcept {
	
	protected String conceptId;
	protected List<String> lexicalizations;
	protected List<String> subsumesRelations;
	protected List<String> subsumedByRelations;
	protected List<String> relatedConcepts;
	
	public OntoWiktionaryConcept(final String conceptId) {
		this.conceptId = conceptId;
		this.lexicalizations = new ArrayList<String>();
		this.subsumesRelations = new ArrayList<String>();
		this.subsumedByRelations = new ArrayList<String>();
		this.relatedConcepts = new ArrayList<String>();
	}
	
	public String getConceptId() {
		return conceptId;
	}
	
	public void addLexicalization(final String lexicalizationID) {
		lexicalizations.add(lexicalizationID);
	}

	public Iterable<String> getLexicalizations() {
		return lexicalizations;
	}

	public void addSubsumesRelation(final String targetConcept) {
		subsumesRelations.add(targetConcept);
	}
	
	public void addSubsumedByRelation(final String targetConcept) {
		subsumedByRelations.add(targetConcept);
	}
	
	public void addRelatedConcept(final String targetConcept) {
		relatedConcepts.add(targetConcept);
	}
	
	public List<String> getSubsumesRelations() {
		return subsumesRelations;
	}
	
	public List<String> getSubsumedByRelations() {
		return subsumedByRelations;
	}
	
	public List<String> getRelatedConcepts() {
		return relatedConcepts;
	}
	
}