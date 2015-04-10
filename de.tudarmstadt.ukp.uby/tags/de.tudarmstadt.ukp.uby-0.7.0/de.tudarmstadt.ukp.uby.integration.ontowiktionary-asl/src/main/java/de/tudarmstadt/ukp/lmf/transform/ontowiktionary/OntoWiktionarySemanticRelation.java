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

import de.tudarmstadt.ukp.jwktl.api.RelationType;

public class OntoWiktionarySemanticRelation {

//	protected String sourceSenseId;
	protected RelationType relationType;
	protected String targetSenseId;
	protected String targetWordForm;
	
	public OntoWiktionarySemanticRelation(final String sourceSenseId,
			final RelationType relationType, final String targetSenseId,
			final String targetWordForm) {
//		this.sourceSenseId = sourceSenseId;
		this.relationType = relationType;
		this.targetSenseId = targetSenseId;
		this.targetWordForm = targetWordForm;
	}
	
//	public String getSourceSenseId() {
//		return sourceSenseId;
//	}
	
	public RelationType getRelationType() {
		return relationType;
	}
	
	public String getTargetSenseId() {
		return targetSenseId;
	}
	
	public String getTargetWordForm() {
		return targetWordForm;
	}
	
}
