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
package de.tudarmstadt.ukp.lmf.model.enums;

/**
 * Degree is a grammatical category appropriate for grammatical classes of adjectives
 * and adverbs expressing the gradation in the level of intensity of the named feature.<p>
 * For example, an adjective "big" has following degrees:
 * <list>
 * <li> POSITIVE: "big" </li>
 * <li> COMPARATIVE: "bigger" </li>
 * <li> SUPERLATIVE: "biggest" </li>
 * <list>
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
public enum EDegree {
	
	POSITIVE{
		public String toString(){
			return "positive";
		}
	},
	
	COMPARATIVE{
		public String toString(){
			return "comparative";
		}
	},
	
	SUPERLATIVE{
		public String toString(){
			return "superlative";
		}
	}
	
	
	
	
}
