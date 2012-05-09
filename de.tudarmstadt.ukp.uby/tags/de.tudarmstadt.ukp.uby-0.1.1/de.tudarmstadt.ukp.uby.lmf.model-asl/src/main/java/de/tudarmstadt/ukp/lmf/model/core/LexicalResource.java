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
package de.tudarmstadt.ukp.lmf.model.core;

import java.util.List;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;

/**
 * This Class represents a LexicalResource
 * @author zijad
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
	private List<Lexicon> lexicons;
	
	// SenseAxes of this LexicalResource
	@VarType(type = EVarType.CHILDREN)
	private List<SenseAxis> senseAxes;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the globalInformation
	 */
	public GlobalInformation getGlobalInformation() {
		return globalInformation;
	}

	/**
	 * @param globalInformation the globalInformation to set
	 */
	public void setGlobalInformation(GlobalInformation globalInformation) {
		this.globalInformation = globalInformation;
	}

	/**
	 * @return the lexicons
	 */
	public List<Lexicon> getLexicons() {
		return lexicons;
	}

	/**
	 * @param lexicons the lexicons to set
	 */
	public void setLexicons(List<Lexicon> lexicons) {
		this.lexicons = lexicons;
	}

	/**
	 * @return the senseAxes
	 */
	public List<SenseAxis> getSenseAxes() {
		return senseAxes;
	}

	/**
	 * @param senseAxes the senseAxes to set
	 */
	public void setSenseAxes(List<SenseAxis> senseAxes) {
		this.senseAxes = senseAxes;
	}

	/**
	 * @return the dtdVersion
	 */
	public String getDtdVersion() {
		return dtdVersion;
	}

	/**
	 * @param dtdVersion the dtdVersion to set
	 */
	public void setDtdVersion(String dtdVersion) {
		this.dtdVersion = dtdVersion;
	}
}
