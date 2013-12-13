package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudarmstadt.ukp.jwktl.api.RelationType;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.Language;

public class OntoWiktionary {

	protected File directory;
	protected ILanguage language;
	protected Map<String, List<OntoWiktionarySemanticRelation>> semanticRelations;
	protected List<OntoWiktionaryConcept> concepts;
	
	public OntoWiktionary(final File directory, final ILanguage language) {
		this.directory = directory;
		this.language = language;
	}
		
	public List<OntoWiktionarySemanticRelation> getSemanticRelations(
			final String senseId) throws IOException {
		if (semanticRelations != null)
			return semanticRelations.get(senseId);
		
		File dataFile = new File(directory, 
				"semantic_relations_" + language.getISO639_1() + ".tsv");
		semanticRelations = new TreeMap<String, List<OntoWiktionarySemanticRelation>>();
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		try {
			int lineNo = 1;
			String line = reader.readLine(); // skip first line
			while ((line = reader.readLine()) != null) {
				int idx = line.indexOf('\t');
				if (idx < 0)
					throw new RuntimeException("Invalid file format at " 
							+ dataFile.getName() + " line " + lineNo++);
				String sourceId = line.substring(0, idx);
				line = line.substring(idx + 1);

				idx = line.indexOf('\t');
				if (idx < 0)
					throw new RuntimeException("Invalid file format at " 
							+ dataFile.getName() + " line " + lineNo++);
				line = line.substring(idx + 1);

				idx = line.indexOf('\t');
				if (idx < 0)
					throw new RuntimeException("Invalid file format at " 
							+ dataFile.getName() + " line " + lineNo++);
				String relationTypeStr	= line.substring(0, idx);
				line = line.substring(idx + 1);

				idx = line.indexOf('\t');
				if (idx < 0)
					throw new RuntimeException("Invalid file format at " 
							+ dataFile.getName() + " line " + lineNo++);
				String targetId = line.substring(0, idx);
				String target = line.substring(idx + 1);

				List<OntoWiktionarySemanticRelation> relations = semanticRelations.get(sourceId);
				if (relations == null) {
					relations = new ArrayList<OntoWiktionarySemanticRelation>();
					semanticRelations.put(sourceId, relations);
				}
				
				RelationType relationType = null;
				if ("HAS_SYN".equals(relationTypeStr))
					relationType = RelationType.SYNONYM;
				else
				if ("HAS_ANT".equals(relationTypeStr))
					relationType = RelationType.ANTONYM;
				else
				if ("HAS_HYPER".equals(relationTypeStr))
					relationType = RelationType.HYPERNYM;
				else
				if ("HAS_HYPO".equals(relationTypeStr))
					relationType = RelationType.HYPONYM;
				else
				if ("HAS_HOLO".equals(relationTypeStr))
					relationType = RelationType.HOLONYM;
				else
				if ("HAS_MERO".equals(relationTypeStr))
					relationType = RelationType.MERONYM;
				else
				if ("CHARACTERISTIC_WORD".equals(relationTypeStr))
					relationType = RelationType.CHARACTERISTIC_WORD_COMBINATION;
				else
				if ("DERIVED_TERM".equals(relationTypeStr))
					relationType = RelationType.DERIVED_TERM;
				else
				if ("ETYMOLOGICALLY_RELA".equals(relationTypeStr))
					relationType = RelationType.ETYMOLOGICALLY_RELATED_TERM;
				else
				if ("COORDINATE_TERM".equals(relationTypeStr))
					relationType = RelationType.COORDINATE_TERM;
				else
				if ("TROPONYM".equals(relationTypeStr))
					relationType = RelationType.TROPONYM;
				else
				if ("DESCENDANT".equals(relationTypeStr))
					relationType = RelationType.DESCENDANT;
				else					
				if ("SEE_ALSO".equals(relationTypeStr))
					relationType = RelationType.SEE_ALSO;
				else
					System.out.println("UNKNOWN RELATION TYPE: " + relationTypeStr);
								
				relations.add(new OntoWiktionarySemanticRelation(sourceId, 
						relationType, targetId, target));
			}
		} finally {
			reader.close();
		}
		
		return semanticRelations.get(senseId);
	}

	public void freeSemanticRelations() {
		if (semanticRelations != null) {
			semanticRelations.clear();
			semanticRelations = null;
		}
	}
	
	public List<OntoWiktionaryConcept> getConcepts() throws IOException, 
			ParserConfigurationException, SAXException {
		if (concepts != null)
			return concepts;
		
		concepts = new ArrayList<OntoWiktionaryConcept>();
		InputStream in = new FileInputStream(new File(directory, 
				"OntoWiktionary_" + language.getISO639_1().toUpperCase() + ".xml"));
		try {
			// Run the SAX parser.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(in, new DefaultHandler(){

				protected OntoWiktionaryConcept currentConcept;
				
				@Override
				public void startElement(final String uri, final String localName, 
						final String qName, final Attributes attributes) throws SAXException {
					if ("Concept".equals(qName)) {
						currentConcept = new OntoWiktionaryConcept(attributes.getValue("id"));
						concepts.add(currentConcept);
					} else					
					if ("Lexicalization".equals(qName))
						currentConcept.addLexicalization(attributes.getValue("id"));
					else
					if ("Subsumes".equals(qName))
						currentConcept.addSubsumesRelation(attributes.getValue("target_id"));
					else
					if ("SubsumedBy".equals(qName))
						currentConcept.addSubsumedByRelation(attributes.getValue("target_id"));
					else
					if ("RelatedConcept".equals(qName))
						currentConcept.addRelatedConcept(attributes.getValue("target_id"));					
				}
				
			});
		} finally {
			in.close();
		}
		return concepts;
	}

	public void freeConcepts() {
		if (concepts != null) {
			concepts.clear();
			concepts = null;
		}
	}

	public static void main(String[] args) throws Exception {
		OntoWiktionary ow = new OntoWiktionary(new File("ontowiktionary"), Language.GERMAN);
		ow.getConcepts();
		ow.getSemanticRelations("");
		
		OntoWiktionary owEN = new OntoWiktionary(new File("ontowiktionary"), Language.ENGLISH);
		owEN.getConcepts();
		owEN.getSemanticRelations("");
	}
	
}
