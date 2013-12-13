package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.util.ArrayList;
import java.util.List;

class OntoWiktionaryConcept {
	
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