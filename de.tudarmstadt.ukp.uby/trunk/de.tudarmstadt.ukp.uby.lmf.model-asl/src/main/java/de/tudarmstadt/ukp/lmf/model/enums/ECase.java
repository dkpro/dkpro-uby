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
package de.tudarmstadt.ukp.lmf.model.enums;

/**
 * Enumeration of grammatical cases.
 */
public enum ECase {

	/** Nominative case.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2721 */
	nominative,
	
	/** Genitive case.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2722 */
	genitive,
	
	/** Dative case.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2723 */
	dative,
	
	/** Accusative case.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2724 */
	accusative,
	
	vocative,
	instrumental,
	locative;

}
