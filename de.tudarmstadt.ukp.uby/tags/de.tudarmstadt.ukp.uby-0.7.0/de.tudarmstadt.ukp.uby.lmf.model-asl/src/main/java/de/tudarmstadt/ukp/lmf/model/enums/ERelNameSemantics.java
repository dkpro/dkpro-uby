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
 * Frequently used relation names for {@link SenseRelation}s and 
 * {@link SynsetRelation}s. Relation names are not limited to those specified
 * by this class; the constants focus only on the most common relations
 * and on those relations used in WordNet and GermaNet.
 */
public final class ERelNameSemantics {
	
	public static final String ANTONYM = "antonym";
	public static final String SYNONYM = "synonym";
	public static final String SYNONYMNEAR = "synonymNear"; // used in WordNet
	
	public static final String HYPERNYM = "hypernym";
	public static final String HYPERNYMINSTANCE = "hypernymInstance"; // used in WordNet
	
	public static final String HYPONYM = "hyponym";
	public static final String HYPONYMINSTANCE = "hyponymInstance"; // used in WordNet
	
	public static final String MERONYM = "meronym";
	public static final String HOLONYM = "holonym";
	public static final String ENTAILS = "entails";
	public static final String ENTAILEDBY = "entailedBy";
	public static final String SEEALSO = "seeAlso";
	public static final String RELATED = "related";
	public static final String CAUSES = "causes";
	public static final String CAUSEDBY = "causedBy";
	public static final String MERONYMCOMPONENT = "meronymComponent";
	public static final String HOLONYMCOMPONENT = "holonymComponent";
	
	public static final String HOLONYMSUBSTANCE = "holonymSubstance";
	public static final String MERONYMSUBSTANCE = "meronymSubstance";
	
	public static final String MERONYMMEMBER = "meronymMember";
	public static final String HOLONYMMEMBER = "holonymMember";
	
	public static final String HOLONYMPORTION = "holonymPortion";
	public static final String MERONYMPORTION = "meronymPortion";
	
	public static final String MERONYMPART = "meronymPart";
	public static final String HOLONYMPART = "holonymPart";
	
	private ERelNameSemantics() {} // Avoid instanciation.

}
