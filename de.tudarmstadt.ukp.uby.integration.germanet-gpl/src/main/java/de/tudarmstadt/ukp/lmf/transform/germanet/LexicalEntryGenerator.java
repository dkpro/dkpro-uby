/**
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.lmf.transform.germanet;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ERelTypeMorphology;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.RelatedForm;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicativeRepresentation;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
import de.tuebingen.uni.sfs.germanet.api.Frame;
import de.tuebingen.uni.sfs.germanet.api.LexRel;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;

/**
 * Instance of this class offers methods for creating {@link LexicalEntry} out of GermaNet's data
 * @author Zijad Maksuti
 * @author Judith Eckle-Kohler
 *
 */
public class LexicalEntryGenerator {


	// converter associated with this LexicalEntryGenerator
	private final GNConverter converter;

	private SenseGenerator senseGenerator;

	// running number used for creating IDs of LexicalEntries
	private int lexicalEntryNumber = 0;

	// running number used for creating IDs of SyntacitBehaviours
	private int syntacticBehaviourNumber = 0;

	// Mappings between LexicalEntries and their' corresponding LexUnit-groups
	private final HashMap<LexicalEntry, Set<LexUnit>> leLUGroupMappings = new HashMap<LexicalEntry, Set<LexUnit>>();

	// Mappings between LexUnit-groups and their' corresponding LexicalEntries
	private final Map<Set<LexUnit>, LexicalEntry> luGroupLEMappings = new HashMap<Set<LexUnit>, LexicalEntry>();

	private final Logger logger = Logger.getLogger(GNConverter.class.getName());


	/**
	 * Constructs an instance of {@link LexicalEntryGenerator}, which provides methods for creating <br>
	 * LexicalEntries out of GermaNet's files
	 * @param converter an instance of {@link GNConverter} associated with this generator
	 * @param resourceVersion Version of the resource
	 */
	public LexicalEntryGenerator(GNConverter converter, String resourceVersion){
		this.converter = converter;
		if(senseGenerator == null) {
            senseGenerator = new SenseGenerator(converter.getGnet(), resourceVersion);
        }
	}

	/**
	 * This method creates a {@link LexicalEntry} based on the
	 * consumed {@link Set} of LexicalUnits
	 * @param luGroup a group of LexUnits from which a LexicalEntry should be created
	 * @return LexicalEntry based on consumed group of LexUnits
	 * @throws DocumentException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @see {@link LexUnit}
	 */
	public LexicalEntry createLexicalEntry(Set<LexUnit> luGroup){

		LexicalEntry lexicalEntry = new LexicalEntry();

		// Create ID
		lexicalEntry.setId(getLEID(luGroup)); // Implied

		// Create partOfSpeech
		lexicalEntry.setPartOfSpeech(getLEPOS(luGroup));

		//*** Creating Lemma ***//
		Lemma lemma = new Lemma();
		lexicalEntry.setLemma(lemma); // appending
		List<FormRepresentation> formRepresentations = getFormRepresentations(luGroup); // get all FormRepresentation for this LU
		lemma.setFormRepresentations(formRepresentations);

//		//*** Creating Senses***//
		List<Sense> senses = senseGenerator.generateSenses(luGroup);

		//** Creating SyntacticBehavior (one for each LexUnit in the group)**//
		SubcategorizationFrameExtractor subcatFrameExtr = converter.getSubcategorizationFrameExtractor();
		if(lexicalEntry.getPartOfSpeech().equals(EPartOfSpeech.verb)){
			List<SyntacticBehaviour> syntacticBehaviours = new LinkedList <SyntacticBehaviour>();

			// A SyntacticBehaviour can only be created for verbs

			LinkedList<Sense> newSenses = new LinkedList<Sense>();

			for(LexUnit lu : luGroup){
				List <Frame> gnFrames = lu.getFrames();
				Sense sense = senseGenerator.getSynsetGenerator().getSense(lu);
				newSenses.add(sense);
				for(Frame gnFrame : gnFrames){
					SyntacticBehaviour syntacticBehaviour = new SyntacticBehaviour();
					// Generating an ID
					StringBuffer sb = new StringBuffer(32);
					sb.append("GN_SyntacticBehaviour_").append(syntacticBehaviourNumber);
					syntacticBehaviourNumber++;
					syntacticBehaviour.setId(sb.toString());
					syntacticBehaviour.setSense(sense);
					syntacticBehaviour.setSubcategorizationFrame(subcatFrameExtr.getSubcategorizationFrame(gnFrame.toString()));
					syntacticBehaviours.add(syntacticBehaviour);

					SemanticPredicate semanticPredicate = subcatFrameExtr.getSemanticPredicate(gnFrame.toString());
					if(semanticPredicate != null){

						List<PredicativeRepresentation> predicativeRepresentations = sense.getPredicativeRepresentations();
						if(predicativeRepresentations == null) {
                            predicativeRepresentations = new LinkedList <PredicativeRepresentation>();
                        }
						PredicativeRepresentation predicativeRepresentation = new PredicativeRepresentation();
						predicativeRepresentation.setPredicate(semanticPredicate);
						predicativeRepresentations.add(predicativeRepresentation);
						sense.setPredicativeRepresentations(predicativeRepresentations);
					}
				}
			}
			lexicalEntry.setSyntacticBehaviours(syntacticBehaviours);
			lexicalEntry.setSenses(newSenses);
		}
		else {
			lexicalEntry.setSenses(senses);
		}

		// record this mappings for later usage
		leLUGroupMappings.put(lexicalEntry, luGroup);
		luGroupLEMappings.put(luGroup, lexicalEntry);

		return lexicalEntry;
	}

	 /**
	  * Consumes an instance of a {@link LexicalEntry} and appends the associated
	  * RelatedForms to it. <br>
	  * This method should only be invoked after all LexicalEntries of a {@link Lexicon}
	  * have been created (without RelatedForms)
	  * @param lexicalEntry an instance of LexicalEntry to which RelatedForms should be appended
	  * @see RelatedForm
	  */
	 public void setRelatedForms(LexicalEntry lexicalEntry) {
		List<RelatedForm> relatedForms = new LinkedList<RelatedForm>();
		Set<LexUnit> luGroup = leLUGroupMappings.get(lexicalEntry);

		if(luGroup.isEmpty()){
			StringBuffer sb = new StringBuffer(128);
			sb.append("LexicalEntryGenerator: found a LexicalEntry without corresponding group of LexUnits");
			sb.append("\n");
			sb.append("is setRelatedForms() invoked to soon?");
			sb.append("\n");
			sb.append("Aborting all operations!");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}

		for(LexUnit lu : luGroup){
			// Extracting derivationBaseVerb
			List<LexUnit> participles = lu.getRelatedLexUnits(LexRel.has_participle);
			if(!participles.isEmpty()) {
                for(LexUnit participle : participles){
					RelatedForm relatedForm = getRelatedForm(participle);
					relatedForm.setRelType(ERelTypeMorphology.derivationBaseVerb);
					relatedForms.add(relatedForm);
				}
            }

			// Extracting derivationBaseVerbAdj
			List<LexUnit> pertonyms = lu.getRelatedLexUnits(LexRel.has_pertainym);
			if(!pertonyms.isEmpty()) {
                for(LexUnit pertonym : pertonyms){
					RelatedForm relatedForm = getRelatedForm(pertonym);
					relatedForm.setRelType(ERelTypeMorphology.derivationBaseVerbAdj);
					relatedForms.add(relatedForm);
				}
            }
		}

		if(relatedForms.size() > 1) {
            removeDuplicateRelatedForms(relatedForms);
        }

		lexicalEntry.setRelatedForms(relatedForms);

	}


	 /**
	  * This method consumes an instance of {@link LexUnit} and returns
	  * the corresponding instance of {@link RelatedForm}
	  * @param lexUnit lexical unit for which an instance of RelatedForm class should be returned
	  * @return RelatedForm which is associated with consumed lexUnit
	  */
	 private RelatedForm getRelatedForm(LexUnit lexUnit){
		 RelatedForm relatedForm = new RelatedForm();

		 Set<LexUnit> targetGroup = converter.getLUGroup(lexUnit);

		if(targetGroup == null){
			StringBuffer sb = new StringBuffer(128);
			sb.append("LexicalEntryGenerator, no group for LexUnit with id: ").append(lexUnit.getId());
			sb.append(" found").append('\n');
			sb.append("Aborting all operations!");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}

		relatedForm.setTargetLexicalEntry(luGroupLEMappings.get(targetGroup));
		relatedForm.setTargetSense(senseGenerator.getSense(lexUnit));
		return relatedForm;
	 }



	/**
	  * This method consumes a group of LexUnits and returns a {@link List} of instances of {@link FormRepresentation}, <br>
	  * generated from consumed group of LexUnits
	  * @param luGroup a group of LexUnits, for which the list of FormRepresentations should be generated
	  * @return A list of instances of FormRepresentations from the consumed luGroup
	  *
	  * @since UBY 0.1.0
	  */
	 private List<FormRepresentation> getFormRepresentations(Set<LexUnit> luGroup){
		 HashMap<String, String> mappings = new HashMap<String, String>(); // <orthographyName,orthographyForm>
		 String orthForm = null;
		 String orthVar = null;
		 String oldOrthForm = null;
		 String oldOrthVar = null;
		 for(LexUnit lu : luGroup){

			 // Extracting orthForm
			 String orthForm2 = lu.getOrthForm();
			 if(orthForm == null) {
                orthForm = orthForm2;
            }
            else
				 if(orthForm2 != null && !orthForm.equals(orthForm2)){
					 logger.log(Level.WARNING, "conflict, diffrent orthForm in same luGroup!");
				 }

			 // Extracting orthVar
			 String orthVar2 = lu.getOrthVar();
			 if(orthVar == null) {
                orthVar = orthVar2;
            }
            else
				 if(orthVar2 != null && !orthVar.equals(orthVar2)){
					 logger.log(Level.WARNING, "conflict, diffrent orthVar in same luGroup!");
				 }

			 // Extracting oldOrthForm
			 String oldOrthForm2 = lu.getOldOrthForm();
			 if(oldOrthForm == null) {
                oldOrthForm = oldOrthForm2;
            }
            else
				 if(oldOrthForm2 != null && !oldOrthForm.equals(oldOrthForm2)){
					 logger.log(Level.WARNING, "LexicalEntryGenerator: conflict, diffrent oldOrthForm in same luGroup!");
				 }

			 // Extracting oldOrthVar
			 String oldOrthVar2 = lu.getOldOrthVar();
			 if(oldOrthVar == null) {
                oldOrthVar = oldOrthVar2;
            }
            else
				 if(oldOrthVar2 != null && !oldOrthVar.equals(oldOrthVar2)){
					 logger.log(Level.WARNING, "LexicalEntryGenerator: conflict, diffrent oldOrthVar in same luGroup!");
				 }
		 }

		 // Add the mappings
		 if(orthForm != null) {
            mappings.put("orthForm", orthForm);
        }
		 if(orthVar != null) {
            mappings.put("orthVar", orthVar);
        }
		 if(oldOrthForm != null) {
            mappings.put("oldOrthForm", oldOrthForm);
        }
		 if(oldOrthVar != null) {
            mappings.put("oldOrthVar", oldOrthVar);
        }

		 List<FormRepresentation> formRepresentations = new LinkedList<FormRepresentation>();
		 for(String orthName : mappings.keySet()){
			 FormRepresentation formRepresentation = new FormRepresentation();
			 formRepresentation.setLanguageIdentifier(ELanguageIdentifier.GERMAN);
			 formRepresentation.setWrittenForm(mappings.get(orthName));
				String orthographyName = null;
				if(orthName.equals("orthForm")) {
                    orthographyName = "new German orthography";
                }
                else
					if(orthName.equals("orthVar")) {
                        orthographyName = "new German orthographical variant";
                    }
                    else
						if(orthName.equals("oldOrthForm")) {
                            orthographyName = "old German orthography";
                        }
                        else
							if(orthName.equals("oldOrthVar")) {
                                orthographyName = "old German orthographical variant";
                            }
                            else{
								/*
								 *  if this segment is reached, the orthographic form is not recognized
								 *
								 */
								StringBuffer sb = new StringBuffer(128);
								sb.append("LexicalEntryGeneraError: orthographic form of LexUnit is not recognized!");
								sb.append('\n').append("Aborting all operations!");
								logger.log(Level.SEVERE, sb.toString());
								System.exit(1);
							}

				formRepresentation.setOrthographyName(orthographyName);
				formRepresentations.add(formRepresentation);
				}
		 return formRepresentations;
	 }

	/**
	 * Consumes a group LexicalUnits and returns {@link EPartOfSpeech} of
	 * corresponding {@link LexicalEntry}
	 * @param luGroup s group LexicalUnits
	 * @return part of speech of the LexicalEntry
	 * @see LexUnit
	 */
	private EPartOfSpeech getLEPOS(Set<LexUnit> luGroup) {
		EPartOfSpeech result = null;
		for(LexUnit lu : luGroup){
			String wordForm = lu.getWordCategory().name();
			if(wordForm.equals("adj")) {
                result = EPartOfSpeech.adjective;
            }
            else
				if(wordForm.equals("nomen")) {
                    if(lu.isNamedEntity()) {
                        result = EPartOfSpeech.nounProper;
                    }
                    else {
                        result = EPartOfSpeech.nounCommon;
                    }
                }
                else
					if(wordForm.equals("verben")) {
                        result = EPartOfSpeech.verb;
                    }
                    else {

				/*
				 * If this code segment is reached, part of speech could
				 * not be resolved...
				 */
				StringBuffer sb = new StringBuffer(128);
				sb.append("LexicalEntryGenerator: part of speech ").append(wordForm).append(" could not be recognized");
				sb.append('\n');
				sb.append("Aborting all operations!");
				System.exit(1);
				return null;
			}
			break;
		}
		return result;
		}

	/**
	 * This method consumes a group of LexicalUnits and generates a unique
	 * ID for corresponding {@link LexicalEntry}
	 * @param luGroup a group of LexicalUnits for which LexicalEntry an id will be generated
	 * @return generated id
	 * @see LexUnit
	 */
	private String getLEID(Set<LexUnit> luGroup) {
		LexicalEntry generatedLexicalEntry = luGroupLEMappings.get(luGroup);
		if(generatedLexicalEntry != null) {
            return generatedLexicalEntry.getId();
        }
        else{
			StringBuffer sb = new StringBuffer(32);
			sb.append("GN_LexicalEntry_").append(lexicalEntryNumber++);
			return sb.toString();
		}
	}

	/**
	 * This method consumes a list of RelatedForms and removes
	 * all duplicates from the consumed list
	 * @param relatedForms {@link List} of RelatedForm objects from which duplicates should be removed
	 * @see {@link RelatedForm}
	 * @since UBY 0.1.0
	 */
	private void removeDuplicateRelatedForms(List<RelatedForm> relatedForms){
		HashSet<RelatedForm> temp = new HashSet<RelatedForm>();
		temp.addAll(relatedForms);
		relatedForms.clear();
		relatedForms.addAll(temp);
		if(relatedForms.isEmpty()){
			StringBuffer sb = new StringBuffer(128);
			sb.append("filter removed all related forms!").append('\n');
			sb.append("Aborting all operations!");
			logger.log(Level.SEVERE, sb.toString());
			System.exit(1);
		}
	}

}

