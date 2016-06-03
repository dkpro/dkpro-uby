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
package de.tudarmstadt.ukp.lmf.model.meta;

import java.util.Date;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * Meta data information for semantic axis
 * @author Yevgen Chebotar
 *
 */
public class MetaData implements IHasID{
	
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;
	
	@VarType(type = EVarType.ATTRIBUTE)
	private Date creationDate;

	@VarType(type = EVarType.ATTRIBUTE)
	private String creationTool;

	@VarType(type = EVarType.ATTRIBUTE)
	private String version;	
	
	@VarType(type = EVarType.ATTRIBUTE)
	private String creationProcess;

	@VarType(type = EVarType.ATTRIBUTE)
	private Boolean automatic;

	// Backlink to LexicalResource added for convenience
	@VarType(type = EVarType.NONE)
	private LexicalResource lexicalResource;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the creationTool
	 */
	public String getCreationTool() {
		return creationTool;
	}
	/**
	 * @param creationTool the creationTool to set
	 */
	public void setCreationTool(String creationTool) {
		this.creationTool = creationTool;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the creationProcess
	 */
	public String getCreationProcess() {
		return creationProcess;
	}
	/**
	 * @param creationProcess the creationProcess to set
	 */
	public void setCreationProcess(String creationProcess) {
		this.creationProcess = creationProcess;
	}
	/**
	 * @return the automatic
	 */
	public Boolean isAutomatic() {
		return automatic;
	}
	/**
	 * @param automatic the automatic to set
	 */
	public void setAutomatic(Boolean automatic) {
		this.automatic = automatic;
	}

	/**
	 * Returns the {@link LexicalResource} containing this {@link MetaData} instance. <p>
	 * <i>This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @return the lexical resource containing this meta data or null if the backlink is not set
	 */
	public LexicalResource getLexicalResource() {
		return lexicalResource;
	}

	/**
	 * Sets the {@link LexicalResource} containing this {@link MetaData} instance.<p>
	 * <i> This backlink is not a part of Uby-LMF model and exists for convenience.</i>
	 * @param lexicon the lexicon to set
	 */
	public void setLexicalResource(LexicalResource lexicalResource) {
		this.lexicalResource = lexicalResource;
	}

}
