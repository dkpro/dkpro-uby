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
package de.tudarmstadt.ukp.lmf.transform.wiktionary.labels;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.meta.SemanticLabel;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;
import de.tudarmstadt.ukp.lmf.transform.StringUtils;

/**
 * Converts different Wiktionar labels to the LMF elements
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WiktionaryLabelLMFMap {
	
	/**
	 * Converts FORM_OF labels to the LMF WordForm element
	 * @param formOfLabel	FORM_OF label
	 * @param word	Word of the entry that contains the formOfLabel
	 */
	public static WordForm formOfToWordForm(WiktionaryLabel formOfLabel, String word){
		WordForm wordForm = new WordForm();
		List<FormRepresentation> formRepresentations = new ArrayList<FormRepresentation>();
		FormRepresentation formRepresentation = new FormRepresentation();
		formRepresentation.setWrittenForm(StringUtils.replaceNonUtf8(word, 1000));						
		formRepresentations.add(formRepresentation);	
		wordForm.setFormRepresentations(formRepresentations);
		
		return wordForm;
	}
	
	/**
	 * Converts Wiktionary label to different types of SubjectField
	 * @param label
	 */
	public static SemanticLabel labelToSemanticLabel(WiktionaryLabel label){
		SemanticLabel semanticLabel = new SemanticLabel();
		semanticLabel.setLabel(StringUtils.replaceNonUtf8(label.getName()));
		WiktionaryLabelType type = label.getLabelType();
		
		if(type.equals(WiktionaryLabelType.PERIOD)){
			semanticLabel.setType("Time Period of Usage"); // style label
			//subjectField.setRegisterType(ERegisterType.time);
		}else if(type.equals(WiktionaryLabelType.QUALIFIER)){
			semanticLabel.setType("Qualifier of Usage");
			//subjectField.setRegisterType(ERegisterType.usage);
		}else if(type.equals(WiktionaryLabelType.REGIONAL)){
			semanticLabel.setType("Region of Usage");
			//subjectField.setRegisterType(ERegisterType.region);
		}else if(type.equals(WiktionaryLabelType.USAGE)){
			semanticLabel.setType("Register");
			//subjectField.setRegisterType(ERegisterType.usage);
		}else if(type.equals(WiktionaryLabelType.TOPIC)){
			semanticLabel.setType("Domain");
			//subjectField.setRegisterType(ERegisterType.usage);
		}else if(type.equals(WiktionaryLabelType.GRAMMATICAL)){
			semanticLabel.setType("Syntax");
			//subjectField.setRegisterType(ERegisterType.usage);
		}
		
		return semanticLabel;
	}
	
}
