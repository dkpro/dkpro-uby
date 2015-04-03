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
package de.tudarmstadt.ukp.lmf.model.semantics;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.abstracts.HasTextRepresentations;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EExampleType;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * SenseExample is a class used to illustrate the particular meaning of a {@link Sense} instance.
 * A Sense can have zero to many examples.
 *
 * @author Zijad Maksuti
 */
public class SenseExample extends HasTextRepresentations
		implements IHasID, Comparable<SenseExample> {

	// Unique Id of this SenseExample
	@VarType(type = EVarType.ATTRIBUTE)
	private String id;

	// TextRepresentation of this SenseExample
	@VarType(type = EVarType.CHILDREN)
	private List<TextRepresentation> textRepresentations = new ArrayList<TextRepresentation>();

	// Example type of this Sense Example
	@VarType(type = EVarType.ATTRIBUTE)
	private EExampleType exampleType;

	/**
	 * Returns the type of this {@link SenseExample} instance.
	 * @return the type of this sense example or null the type is not set.
	 * @see EExampleType
	 */
	public EExampleType getExampleType() {
		return exampleType;
	}

	/**
	 * Sets the type of this {@link SenseExample} instance.
	 * @param exampleType the type of this sense example to set
	 * @see EExampleType
	 */
	public void setExampleType(EExampleType exampleType) {
		this.exampleType = exampleType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TextRepresentation> getTextRepresentations() {
		return textRepresentations;
	}

	public void setTextRepresentations(List<TextRepresentation> textRepresentations) {
		this.textRepresentations = textRepresentations;
	}

	@Override
	public String toString(){
		return this.id.toString();
	}

	@Override
	public int compareTo(SenseExample o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other)
	      return true;
	    if (!(other instanceof SenseExample))
	      return false;
	    SenseExample otherSenseExample = (SenseExample) other;
	    return this.id.equals(otherSenseExample.id);
	  }

	@Override
	public int hashCode() {
		return 31 + (id == null ? 0 : id.hashCode());
//	    int hash = 1;
//	    hash = hash * 31 + this.id.hashCode(); <-- ChM: ???
//	    return hash;
	}

}
