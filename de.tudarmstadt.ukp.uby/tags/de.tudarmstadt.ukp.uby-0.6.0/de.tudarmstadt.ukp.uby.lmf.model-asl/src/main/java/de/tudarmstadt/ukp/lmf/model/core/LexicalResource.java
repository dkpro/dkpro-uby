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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;

/**
 * LexicalResource is a class representing the entire resource. 
 * The Lexical Resource instance is a container for at least one or more lexicons.
 * @see Lexicon
 * @author Zijad Maksuti  
 *
 */
public class LexicalResource {
	
	// .dtd-Version
	@VarType(type = EVarType.ATTRIBUTE)
	private String dtdVersion;
	
	// name of the LexicalResource
	@VarType(type = EVarType.ATTRIBUTE)
	private String name;
	
	// GlobalInformation Object
	@VarType(type = EVarType.CHILD)
	private GlobalInformation globalInformation;
	
	// Lexicons of this LexicalResource
	@VarType(type = EVarType.CHILDREN)
	private List<Lexicon> lexicons = new ArrayList<Lexicon>();
	
	// SenseAxes of this LexicalResource
	@VarType(type = EVarType.CHILDREN)
	private List<SenseAxis> senseAxes = new ArrayList<SenseAxis>();
	
	// PredicateArgumentAxes of this LexicalResource
	@VarType(type = EVarType.CHILDREN)
	private List<PredicateArgumentAxis> predicateArgumentAxes = new ArrayList<PredicateArgumentAxis>();

	// MetaData of this LexcalResource
	@VarType(type = EVarType.CHILDREN)
	private List<MetaData> metaData = new ArrayList<MetaData>();

	/**
	 * Returns the name of the {@link LexicalResource}.
	 * @return the name of the lexical resource or null, if the name is not set
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the {@link LexicalResource}.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns global (administrative) information of this {@link LexicalResource}.
	 * @return the global information of this lexical resource or null, if the global information is not set
	 * @see GlobalInformation
	 */
	public GlobalInformation getGlobalInformation() {
		return globalInformation;
	}

	/**
	 * Sets global (administrative) information of this {@link LexicalResource}.
	 * @param globalInformation the global information to set
	 * @see GlobalInformation
	 */
	public void setGlobalInformation(GlobalInformation globalInformation) {
		this.globalInformation = globalInformation;
	}

	/**
	 * Returns all lexicons of this {@link LexicalResource}.
	 * @return the {@link List} of all lexicons contained in this lexical resource or an empty list, if this lexical
	 * resource does not contain any lexicons.
	 * @see Lexicon
	 */
	public List<Lexicon> getLexicons() {
		return lexicons;
	}

	/**
	 * Sets the {@link List} of {@link Lexicon} instances to this {@link LexicalResource}.
	 * @param lexicons the lexicons to set
	 */
	public void setLexicons(List<Lexicon> lexicons) {
		this.lexicons = lexicons;
	}

	/**
	 * Returns a {@link List} of all {@link PredicateArgumentAxis} instances contained in this {@link LexicalResource}. 
	 * @return the list of all predicate argument axes contained in this lexical resource or an empty list, if the lexical resource
	 * does not contain any predicate argument axes. 
	 */
	public List<PredicateArgumentAxis> getPredicateArgumentAxes() {
		return predicateArgumentAxes;
	}

	/**
	 * Sets a {@link List} of {@link PredicateArgumentAxis} instances to this {@link LexicalResource}.
	 * @param predicateArgumentAxes the predicate argument axes to set
	 */
	public void setPredicateArgumentAxes(List<PredicateArgumentAxis> predicateArgumentAxes) {
		this.predicateArgumentAxes = predicateArgumentAxes;
	}
	
	/**
	 * Returns a {@link List} of all {@link SenseAxis} instances contained in this {@link LexicalResource}. 
	 * @return the list of all sense axes contained in this lexical resource or an empty list, if the lexical resource
	 * does not contain any sense axes. 
	 */
	public List<SenseAxis> getSenseAxes() {
		return senseAxes;
	}

	/**
	 * Sets a {@link List} of {@link SenseAxis} instances to this {@link LexicalResource}.
	 * @param senseAxes the sense axes to set
	 */
	public void setSenseAxes(List<SenseAxis> senseAxes) {
		this.senseAxes = senseAxes;
	}

	/**
	 * Returns the DTD (Document Type Definition) version of Uby-LMF, against which this {@link LexicalResource} is valid.
	 * @return the DTD version against which this lexical resource is valid or null, if the DTD version is not set
	 */
	public String getDtdVersion() {
		return dtdVersion;
	}

	/**
	 * Sets the DTD (Document Type Definition) version of Uby-LMF, against which this {@link LexicalResource} is valid.
	 * @param dtdVersion the DTD version against which this lexical resource is valid
	 */
	public void setDtdVersion(String dtdVersion) {
		this.dtdVersion = dtdVersion;
	}
	
	
	/**
	 * @return the metaData
	 */
	public List<MetaData> getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(List<MetaData> metaData) {
		this.metaData = metaData;
	}

	/**
	 * Adds a {@link Lexicon} instance to this {@link LexicalResource} instance.
	 * 
	 * @param lexicon the lexicon to add
	 * 
	 * @return <code>true</code> only when this lexical resource did not already
	 * contain an equal lexicon.
	 * 
	 * @since 0.2.0
	 * 
	 */
	public boolean addLexicon(Lexicon lexicon){
		if(!this.lexicons.contains(lexicon)){
			this.lexicons.add(lexicon);
			return true;
		}
		return false;
	}
}
