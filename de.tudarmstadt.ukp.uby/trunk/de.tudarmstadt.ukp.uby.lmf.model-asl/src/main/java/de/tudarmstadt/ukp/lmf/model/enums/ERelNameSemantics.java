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
 * Frequently used relation names for {@link SenseRelation}s and 
 * {@link SynsetRelation}s. Relation names are not limited to those specified
 * by this enumeration; the enumeration focuses on the most common relations.
 * 
 * @author Christian M. Meyer
 *
 */
public final class ERelNameSemantics {
	
	public static final String ANTONYM = "antonym";
	public static final String SYNONYM = "synonym";
	public static final String HYPERNYM = "hypernym";
	public static final String HYPONYM = "hyponym";
	public static final String MERONYM = "meronym";
	public static final String HOLONYM = "holonym";
		 
	private ERelNameSemantics() {} // Avoid instanciation.

}
