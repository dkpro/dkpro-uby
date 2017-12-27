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
package de.tudarmstadt.ukp.lmf.transform.ontowiktionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudarmstadt.ukp.jwktl.api.RelationType;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;

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
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataFile), StandardCharsets.UTF_8));
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

	public Iterable<OntoWiktionaryConcept> getStreamedConcepts() throws IOException,
			ParserConfigurationException, SAXException {
		return new Iterable<OntoWiktionaryConcept>() {

			@Override
			public Iterator<OntoWiktionaryConcept> iterator() {
				try {
					final InputStream in = new FileInputStream(new File(directory,
							"OntoWiktionary_" + language.getISO639_1().toUpperCase() + ".xml"));
					final XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(in);
					return new Iterator<OntoWiktionaryConcept>() {

						protected boolean atStartPosition = true;

						@Override
						public boolean hasNext() {
							try {
								return parser.hasNext();
							} catch (XMLStreamException e) {
								throw new RuntimeException(e);
							}
						}

						@Override
						public OntoWiktionaryConcept next() {
							try {
								// Find the first concept node.
								if (atStartPosition) {
									while (parser.hasNext()
											&& (parser.getEventType() != XMLStreamConstants.START_ELEMENT
											|| !"Concept".equals(parser.getLocalName())))
										parser.next();
									atStartPosition = false;
								}

								if (!parser.hasNext()) {
									in.close();
									return null;
								}

								OntoWiktionaryConcept currentConcept =
										new OntoWiktionaryConcept(parser.getAttributeValue(null, "id"));

								// Iterate over all elements before the concept node's end element.
								while (parser.hasNext()
										&& (parser.getEventType() != XMLStreamConstants.END_ELEMENT
										|| !"Concept".equals(parser.getLocalName()))) {
									if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
										String qName = parser.getLocalName();
										if ("Lexicalization".equals(qName))
											currentConcept.addLexicalization(parser.getAttributeValue(null, "id"));
										else
										if ("Subsumes".equals(qName))
											currentConcept.addSubsumesRelation(parser.getAttributeValue(null, "target_id"));
										else
										if ("SubsumedBy".equals(qName))
											currentConcept.addSubsumedByRelation(parser.getAttributeValue(null, "target_id"));
										else
										if ("RelatedConcept".equals(qName))
											currentConcept.addRelatedConcept(parser.getAttributeValue(null, "target_id"));
									}
									parser.next();
								}

								// Find the next concept node.
								while (parser.hasNext()
										&& (parser.getEventType() != XMLStreamConstants.START_ELEMENT
										|| !"Concept".equals(parser.getLocalName())))
									parser.next();

								return currentConcept;
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException("Iterator<OntoWiktionaryConcept>.remove()");
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	public void freeConcepts() {
		if (concepts != null) {
			concepts.clear();
			concepts = null;
		}
	}

}
