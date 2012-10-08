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
public enum ERelNameSemantics {

	 antonym("antonym"),
	 synonym("synonym"),
	 hypernym("hypernym"),
	 hyponym("hyponym"),
	 meronym("meronym"),
	 holonym("holonym");
	
	 
	 private String name;
	 
	 private ERelNameSemantics(final String name) {
		this.name = name;
	}
	 
	 @Override
	 public String toString() {
		 return name;
	 };
	 
}
