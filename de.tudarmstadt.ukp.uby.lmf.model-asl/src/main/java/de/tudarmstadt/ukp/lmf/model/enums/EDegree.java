/*******************************************************************************
 * Copyright 2016
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
 * Enumeration of grammatical degrees of adjectives and adverbs (e.g., big, 
 * bigger, biggest).
 */
public enum EDegree {

	/** Positive; basic level of intensity.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2780 */
	positive,

	/** Comparative; higher level of intensity than the basic level.
	 *  http://www.isocat.org/datcat/DC-2781 */
	comparative,
	
	/** Superlative; highest level of intensity.
	 *  ISOcat: http://www.isocat.org/datcat/DC-2782 */
	superlative;
	
}
