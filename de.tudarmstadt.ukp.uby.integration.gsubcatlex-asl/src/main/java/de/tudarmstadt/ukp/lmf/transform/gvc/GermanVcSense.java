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

package de.tudarmstadt.ukp.lmf.transform.gvc;

import java.io.IOException;

/**
 * This class defines a German verb class (GVC) sense in the LMF-Model
 * @author Eckle-Kohler
 *
 */
public class GermanVcSense {
	public String lemma, synArgs, classInformation;
	
	public GermanVcSense(String lemma,String synArgs,String classInformation) throws IOException {
		this.lemma = lemma;		
		this.synArgs = synArgs; // syntactic arguments: role information might be present in few cases
		this.classInformation = classInformation;
	}

}