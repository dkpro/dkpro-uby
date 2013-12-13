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
