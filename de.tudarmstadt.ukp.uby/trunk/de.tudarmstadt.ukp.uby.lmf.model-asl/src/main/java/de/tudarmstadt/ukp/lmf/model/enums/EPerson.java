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

import de.tudarmstadt.ukp.lmf.model.morphology.WordForm;

/**
 * Enumeration of the grammatical person of a {@link WordForm}.
 */
public enum EPerson {
	
	/** First person (e.g., I, we).
	 *  ISOcat: http://www.isocat.org/datcat/DC-3198 */
	first,
	
	/** Second person (e.g., you).
	 *  ISOcat: http://www.isocat.org/datcat/DC-3464 */
	second,
	
	/** Third person (e.g., she, they).
	 *  ISOcat: http://www.isocat.org/datcat/DC-3526 */
	third;
	
}
