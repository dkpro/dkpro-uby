/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.transform.verbnet;

import java.io.IOException;
import java.util.List;

/**
 * This class defines a VerbNet sense in the LMF-Model
 * @author Eckle-Kohler
 *
 */
public class VerbNetSense {
	public String lemma, wnSense, example, arguments, predicate, classInformation, roleSet, synArgs;
	public List<String> synSemArgs; // syntactic arguments enriched with thematic roles and selectional restrictions
	
	public VerbNetSense(String lemma,String wnSense,String example,String arguments,String predicate,String classInformation,String roleSet,String synArgs) throws IOException {
		this.lemma = lemma;		
		this.wnSense = wnSense;
		this.example = example;
		this.arguments = arguments; // syntactic arguments enriched with thematic roles
		this.predicate = predicate;
		this.classInformation = classInformation;
		this.roleSet = roleSet;	// thematic roles enriched with selectional restrictions
		this.synArgs = synArgs; // purely syntactic arguments
	}


}
