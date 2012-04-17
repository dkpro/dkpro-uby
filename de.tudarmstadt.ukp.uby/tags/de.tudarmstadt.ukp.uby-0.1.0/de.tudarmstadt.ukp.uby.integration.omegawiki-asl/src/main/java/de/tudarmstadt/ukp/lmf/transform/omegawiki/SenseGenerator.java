/*******************************************************************************
 * Copyright 2012
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
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.Statement;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.ECase;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalGender;
import de.tudarmstadt.ukp.lmf.model.enums.EGrammaticalNumber;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.ERegisterType;
import de.tudarmstadt.ukp.lmf.model.enums.EStatementType;
import de.tudarmstadt.ukp.lmf.model.enums.ESyntacticProperty;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.syntax.LexemeProperty;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SyntacticBehaviour;
import de.tudarmstadt.ukp.omegawiki.api.Annotation;
import de.tudarmstadt.ukp.omegawiki.api.DefinedMeaning;
import de.tudarmstadt.ukp.omegawiki.api.OmegaWiki;
import de.tudarmstadt.ukp.omegawiki.api.SynTrans;
import de.tudarmstadt.ukp.omegawiki.exception.OmegaWikiException;

/**
 * This class generates senses
 * @author matuschek
 *
 */
class SenseGenerator {
	private final  ELanguageIdentifier GlobalLanguageLMF;
	private final OmegaWiki omegawiki;

	/*
	 * Synset generator is needed for recovering
	 * the LMFSynset-OWSynset mappings
	 */
	private final SynsetGenerator synsetGenerator;

	private  int LMFSenseNumber; // Running number for creating Sense-IDs
	private  int SCFNumber; // Running number for creating SubCatFrames
	private  int SBNumber;// Running number for creating SyntacticBehaviour

	// Mappings between lexemes and corresponding Senses
	private final  HashMap<SynTrans, Sense> lexemeSenseMappings = new HashMap<SynTrans, Sense>();

	protected static int exampleIdx = 1;

	/**
	 * Constructs a SenseGenerator
	 * @param omegawiki
	 * @param synsetGenerator a SynsetGenerator
	 */
	public SenseGenerator(SynsetGenerator synsetGenerator,OmegaWiki ow){
		this.omegawiki = ow;
		this.synsetGenerator = synsetGenerator;
		this.GlobalLanguageLMF=synsetGenerator.getGlobalLanguageLMF();

	}

	/**
	 * This method consumes a Set of lexemes (SynTrans in OW)
	 * and returns a List of Senses for this group of lexemes
	 * @param lexemeGroup
	 * @param lexicalEntry
	 * @throws OmegaWikiException
	 * @throws UnsupportedEncodingException
	 */
	public List<Sense> generateSenses(Set<SynTrans> lexemeGroup, LexicalEntry lexicalEntry) throws UnsupportedEncodingException, OmegaWikiException{
		List<Sense> result = new LinkedList<Sense>();

		// Every lexeme has a sense of its own
		for(SynTrans lexeme : lexemeGroup){
			Set<Annotation> annos = lexeme.getAnnotations(); //all annotations of this lexeme
			String hyphenation = "";
			String phonetic = "";
			String example = null;
			String etymology=null;
			String otherStatement=null;
			SemanticLabel sl = null;
			SyntacticBehaviour sb = null;
			LexemeProperty lp= null;
			boolean urlStat = false;
			for(Annotation anno : annos)
			{
				String name = anno.getName();
				String value = anno.getValue();
				if (name.equals("hyphenation"))
				{
					hyphenation = value;
					Lemma lemma = lexicalEntry.getLemma();
					List<FormRepresentation> formRepresentations = lemma.getFormRepresentations();
					FormRepresentation formRepresentation = formRepresentations.get(0);
					formRepresentation.setHyphenation(hyphenation);
				}
				else if (name.equals("alfabeto fonético internacional"))//phonetic form
				{
					phonetic = value;
					Lemma lemma = lexicalEntry.getLemma();
					List<FormRepresentation> formRepresentations = lemma.getFormRepresentations();
					FormRepresentation formRepresentation = formRepresentations.get(0);
					formRepresentation.setPhoneticForm(phonetic);
				}
				else if (name.equals("Genus"))
				{
					Lemma lemma = lexicalEntry.getLemma();
					WordForm wf;
					if(lexicalEntry.getWordForms()!=null) {
						wf = lexicalEntry.getWordForms().get(0);
					}
					else {
						wf = new WordForm();
					}
					List<FormRepresentation> formRepresentations = lemma.getFormRepresentations();
					wf.setFormRepresentations(formRepresentations);
					if(value.equals("maskulinum")) {
						wf.setGrammaticalGender(EGrammaticalGender.masculine);
					}
					else if (value.equals("weiblich")) {
						wf.setGrammaticalGender(EGrammaticalGender.feminine);
					}
					else if (value.equals("neutrum")) {
						wf.setGrammaticalGender(EGrammaticalGender.neuter);
					}
					if(lexicalEntry.getWordForms()==null) {
						lexicalEntry.setWordForms(new LinkedList<WordForm>());
						lexicalEntry.getWordForms().add(wf);
					}

				}
				else if (name.equals("Kasus")) //Grammatical case
				{
					Lemma lemma = lexicalEntry.getLemma();
					WordForm wf;
					if(lexicalEntry.getWordForms()!=null) {
						wf = lexicalEntry.getWordForms().get(0);
					}
					else {
						wf = new WordForm();
					}
					List<FormRepresentation> formRepresentations = lemma.getFormRepresentations();
					wf.setFormRepresentations(formRepresentations);
					if(value.equals("Akkusativ")) {
						wf.setCase(ECase.accusative);
					}
					else if(value.equals("Dativ")) {
						wf.setCase(ECase.dative);
					}
					else if(value.equals("Nominativ")) {
						wf.setCase(ECase.nominative);
					}
					else if(value.equals("Genitiv")) {
						wf.setCase(ECase.genitive);
					}
					if(lexicalEntry.getWordForms()==null) {
						lexicalEntry.setWordForms(new LinkedList<WordForm>());
						lexicalEntry.getWordForms().add(wf);
					}

				}
				else if (name.equals("Numerus"))//Grammatical number
				{
					Lemma lemma = lexicalEntry.getLemma();
					WordForm wf;
					if(lexicalEntry.getWordForms()!=null) {
						wf = lexicalEntry.getWordForms().get(0);
					}
					else {
						wf = new WordForm();
					}
					List<FormRepresentation> formRepresentations = lemma.getFormRepresentations();
					wf.setFormRepresentations(formRepresentations);
					if(value.equals("Singular")) {
						wf.setGrammaticalNumber(EGrammaticalNumber.singular);
					}
					else if (value.equals("Plural")) {
						wf.setGrammaticalNumber(EGrammaticalNumber.plural);
					}
					else if (value.equals("Dual")) {
						wf.setGrammaticalNumber(EGrammaticalNumber.plural);
					}
					if(lexicalEntry.getWordForms()==null) {
						lexicalEntry.setWordForms(new LinkedList<WordForm>());
						lexicalEntry.getWordForms().add(wf);
					}

				}
				else if (name.equals("grammatical property")||name.equals("property")) //Other properties
				{
					if (value.equals("Singularetantum")) {
						sl = new SemanticLabel();
						sl.setType("semanticNounClass");
						sl.setLabel(value);

						//lexicalEntry.setSingularetantum(EYesNo.yes);
					}
					else if (value.equals("Pluraletantum")) {
						sl = new SemanticLabel();
						sl.setType("semanticNounClass");
						sl.setLabel(value);

						//lexicalEntry.setPluraletantum(EYesNo.yes);
					}
					else if (value.equals("intransitive")|| value.equals("transitive")|| value.equals("impersonal")|| value.equals("reflexive")) {
						SubcategorizationFrame scf= new SubcategorizationFrame();
						scf.setSubcatLabel(value);
						scf.setId(getNewSCFID());
						if(lexicalEntry.getLexicon().getSubcategorizationFrames()==null)
						{
							lexicalEntry.getLexicon().setSubcategorizationFrames(new LinkedList<SubcategorizationFrame>());
						}
						lexicalEntry.getLexicon().getSubcategorizationFrames().add(scf);
						sb = new SyntacticBehaviour();
						sb.setSubcategorizationFrame(scf);
						sb.setId(getNewSBID());
						if(lexicalEntry.getSyntacticBehaviours()==null) {
							lexicalEntry.setSyntacticBehaviours(new LinkedList<SyntacticBehaviour>());
						}
						lexicalEntry.getSyntacticBehaviours().add(sb);
					}
					else if (value.equals("attributive"))
					{
						lp = new LexemeProperty();
						lp.setSyntacticProperty(ESyntacticProperty.nonPredicativeAdjective);
					}
					else if (value.equals("predicative"))
					{
						lp = new LexemeProperty();
						lp.setSyntacticProperty(ESyntacticProperty.predicativeAdjective);
					}
					//else if (value.equals("impersonal"))
					//{
					//	lp = new LexemeProperty();
					//	lp.setSyntacticProperty(ESyntacticProperty.impersonal);
					//}
					//else if (value.equals("reflexive"))
					//{
					//	lp = new LexemeProperty();
					//	lp.setSyntacticProperty(ESyntacticProperty.reflexive);
					//}
					else if (value.equals("separable"))
					{
						lexicalEntry.setSeparableParticle(EYesNo.yes.toString());
					}
					else if (value.equals("inseparable"))
					{
						lexicalEntry.setSeparableParticle(EYesNo.no.toString());
					}
				}
				else if (name.equals("example sentence"))
				{
					example=value;
				}
				else if (name.equals("etymology"))
				{
					etymology=value;
				}
				else if (name.equals("usage"))
				{

					sl = new SemanticLabel();
					if(value.equals("vulgar")||value.equals("technical")||value.equals("poetic")||value.equals("pejorative")||value.equals("offensive")||value.equals("colloquial")||value.equals("medical")||value.equals("juvenile")||value.equals("informal")||value.equals("humorous")||value.equals("euphemistic")||value.equals("kindersprache")) {
						sl.setType(ERegisterType.usage.toString());
					}
					else if(value.equals("archaic")||value.equals("alte deutsche Schreibweise")||value.equals("dated")||value.equals("neologism")||value.equals("obsolete")) {
						sl.setType(ERegisterType.time.toString());
					}
					else  {
						sl.setType(ERegisterType.region.toString());
					}
					sl.setType(ERegisterType.time.toString());
					sl.setLabel(value);

				}
				else if(!name.equals("part of speech")) {
					otherStatement=value;
					if (value.startsWith("http:")) {
						urlStat= true;
					}
				}

			}
			Sense sense = new Sense();
			lexemeSenseMappings.put(lexeme, sense);
			//set ID
			sense.setId(getNewID());

			if(sl!=null)
			{
				sl.setParentId(sense.getId());
				if(sense.getSemanticLabels()==null) {
					sense.setSemanticLabels(new LinkedList<SemanticLabel>());
				}
				sense.getSemanticLabels().add(sl);
			}
			if(sb!=null)
			{
				sb.setSense(sense);
				if(lp!=null)
				{
					sb.getSubcategorizationFrame().setLexemeProperty(lp);
				}
			}
			else if(lp!=null)
			{
				SubcategorizationFrame scf= new SubcategorizationFrame();
				scf.setSubcatLabel("");
				scf.setId(getNewSCFID());
				if(lexicalEntry.getLexicon().getSubcategorizationFrames()==null)
				{
					lexicalEntry.getLexicon().setSubcategorizationFrames(new LinkedList<SubcategorizationFrame>());
				}
				lexicalEntry.getLexicon().getSubcategorizationFrames().add(scf);
				sb = new SyntacticBehaviour();
				sb.setSubcategorizationFrame(scf);
				sb.setId(getNewSBID());
				sb.setSense(sense);
				if(lexicalEntry.getSyntacticBehaviours()==null) {
					lexicalEntry.setSyntacticBehaviours(new LinkedList<SyntacticBehaviour>());
				}
				lexicalEntry.getSyntacticBehaviours().add(sb);
				scf.setLexemeProperty(lp);
			}

			// setting index of the Sense (lexeme's SynTrans ID)
			sense.setIndex(lexeme.getSyntransid());

			DefinedMeaning lexemesSynset = lexeme.getDefinedMeaning(); // Lexeme's DM

			//set Synset
			de.tudarmstadt.ukp.lmf.model.semantics.Synset lmfSynset = synsetGenerator.getLMFSynset(lexemesSynset);
			if(lmfSynset == null){
				System.err.println("Error, SenseGenerator: Could not find lmfSynset for Synset: "+ lexemesSynset);
				System.exit(1);
			}
			sense.setSynset(lmfSynset);
			sense.setLexicalEntry(lexicalEntry);
			if(lmfSynset.getSenses() == null) {
				lmfSynset.setSenses(new LinkedList<Sense>());
			}
			lmfSynset.getSenses().add(sense);
			sense.setDefinitions(sense.getSynset().getDefinitions());


			if(example!=null)
			{
			if(sense.getSenseExamples()==null) {
				sense.setSenseExamples(new LinkedList<SenseExample>());
			}
			SenseExample se = new SenseExample();
			se.setId("OW" + this.GlobalLanguageLMF + "_SenseExample_" + (exampleIdx++));
			se.setExampleType(EExampleType.senseInstance);
			se.setTextRepresentations(new LinkedList<TextRepresentation>());
			TextRepresentation tr = new TextRepresentation();
			tr.setLanguageIdentifier(GlobalLanguageLMF);
			tr.setWrittenText(example);
			se.getTextRepresentations().add(tr);
			sense.getSenseExamples().add(se);
			}




			if(etymology !=null && sense.getDefinitions()!=null)
			{
			if(sense.getDefinitions().get(0).getStatements()==null) {
				sense.getDefinitions().get(0).setStatements(new LinkedList<Statement>());
			}
			Statement stat = new Statement();
			stat.setStatementType(EStatementType.etymology);
			stat.setTextRepresentations(new LinkedList<TextRepresentation>());
			TextRepresentation tr = new TextRepresentation();
			tr.setLanguageIdentifier(GlobalLanguageLMF);
			tr.setWrittenText(etymology);
			stat.getTextRepresentations().add(tr);
			sense.getDefinitions().get(0).getStatements().add(stat);
			}
			if(otherStatement !=null && sense.getDefinitions()!=null)
			{
			if(sense.getDefinitions().get(0).getStatements()==null) {
				sense.getDefinitions().get(0).setStatements(new LinkedList<Statement>());
			}
			Statement stat = new Statement();

			stat.setStatementType(EStatementType.encyclopedicInformation);
			if(urlStat) {
				stat.setStatementType(EStatementType.externalReference);
			}
			stat.setTextRepresentations(new LinkedList<TextRepresentation>());
			TextRepresentation tr = new TextRepresentation();
			tr.setLanguageIdentifier(GlobalLanguageLMF);
			tr.setWrittenText(otherStatement);
			stat.getTextRepresentations().add(tr);
			sense.getDefinitions().get(0).getStatements().add(stat);
			}

			// Creating MonolingualExternalRef for a Sense
			MonolingualExternalRef monolingualExternalRef = new MonolingualExternalRef();
			String senseKey;
			senseKey = lexeme.getSyntransid()+"";
			// create an external reference
			StringBuffer stb = new StringBuffer(32);
			stb.append(senseKey);

			monolingualExternalRef.setExternalSystem("OW SynTrans ID");
			monolingualExternalRef.setExternalReference(stb.toString());
			List<MonolingualExternalRef> monolingualExternalRefs = new LinkedList<MonolingualExternalRef>();
			monolingualExternalRefs.add(monolingualExternalRef);
			sense.setMonolingualExternalRefs(monolingualExternalRefs);

			// Add the created Sense to the result
			result.add(sense);
		}

		return result;
	}

	/**
	 * This method generates a Sense-ID
	 */
	private String getNewID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("OW_"+GlobalLanguageLMF.toString()+"_Sense_").append(Integer.toString(LMFSenseNumber));
		LMFSenseNumber++;
		return sb.toString();
	}
	/**
	 * This method generates a SubCatFrame-ID
	 */
	private String getNewSCFID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("OW_"+GlobalLanguageLMF.toString()+"_SubcatFrame_").append(Integer.toString(SCFNumber));
		SCFNumber++;
		return sb.toString();
	}
	/**
	 * This method generates a SyntacticBehaviour-ID
	 */
	private String getNewSBID() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("OW_"+GlobalLanguageLMF.toString()+"_SyntacticBehaviour_").append(Integer.toString(SBNumber));
		SBNumber++;
		return sb.toString();
	}
	/**
	 * This method returns the corresponding sense of a Lexeme
	 */
	public Sense getSense(SynTrans lexeme){
		return lexemeSenseMappings.get(lexeme);
	}

	/**
	 * Returns all Lexemes processed by SenseGenerator
	 */
	public Set<SynTrans> getProcessedLexemes(){
		return lexemeSenseMappings.keySet();
	}
}

