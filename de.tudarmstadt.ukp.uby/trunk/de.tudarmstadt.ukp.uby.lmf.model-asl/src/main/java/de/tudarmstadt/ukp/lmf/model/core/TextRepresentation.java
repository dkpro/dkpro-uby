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
package de.tudarmstadt.ukp.lmf.model.core;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasLanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasParentSpecificTable;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;

/**
 * TextRepresentation is a class representing one textual content of
 * diverse Uby-LMF classes, such as {@link Definition}, {@link SenseExample} or {@link Statement}.
 * 
 * @author Zijad Maksuti
 * 
 */
public class TextRepresentation implements IHasLanguageIdentifier, 
		IHasParentSpecificTable, Comparable<TextRepresentation>{

	// Language identifier of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String languageIdentifier;

	// Orthography name of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String orthographyName;

	// geographical variant of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String geographicalVariant;

	// written Text of this TextRepresentation
	@VarType(type = EVarType.ATTRIBUTE)
	private String writtenText;

	
	public String getLanguageIdentifier() {
		return languageIdentifier;
	}

	public void setLanguageIdentifier(final String languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	/**
	 * Returns the name of the orthography used in this {@link TextRepresentation}.<p>
	 * For instance, an orthographe name can be <i>"arabic"</i> or <i>"arabic unpointed"</i>.
	 * @return the name of the orthography used in the written text of this text representation
	 * or null, if the name of orthography is not set
	 */
	public String getOrthographyName() {
		return orthographyName;
	}

	/**
	 * Sets the name of the orthography used in this {@link TextRepresentation}.<p>
	 * For instance, an orthographe name can be <i>"arabic"</i> or <i>"arabic unpointed"</i>.
	 * @param orthographyName the name to set
	 */
	public void setOrthographyName(String orthographyName) {
		this.orthographyName = orthographyName;
	}

	/**
	 * Returns another variant of the written text in this {@link TextRepresentation} instance
	 * that is specific in a certain geographical region. 
	 * @return another variant of written text of this text representation or null, if
	 * the text representation does not have another geographical variant set 
	 * @see #getWrittenText()
	 */
	public String getGeographicalVariant() {
		return geographicalVariant;
	}

	/**
	 * Sets another variant of the written text used in this {@link TextRepresentation} instance
	 * that is specific in a certain geographical region.
	 * @param geographicalVariant the geographical variant to set
	 * @see #setWrittenText(String)
	 */
	public void setGeographicalVariant(String geographicalVariant) {
		this.geographicalVariant = geographicalVariant;
	}

	/**
	 * Returns the series of sentences expressed in natural language contained in this
	 * {@link TextRepresentation}. 
	 * @return the series of sentences expressed in natural language contained in this
	 * text representation or null, if this text representation does not have any written text set <p>
	 * <i> Note that written text is the essential part of a text representation and should
	 * always be set under normal circumstances.</i>
	 */
	public String getWrittenText() {
		return writtenText;
	}

	/**
	 * Sets the series of sentences expressed in natural language contained in this
	 * {@link TextRepresentation}.
	 * @param writtenText sentences to set
	 */
	public void setWrittenText(String writtenText) {
		this.writtenText = writtenText;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(512);
		sb.append("TextRepresentation ");
		sb.append("languageIdentifier:").append(languageIdentifier);
		sb.append(" ortographyName:").append(orthographyName);
		sb.append(" geographicalVariant:").append(geographicalVariant);
		sb.append(" writtenText:").append(writtenText);
		return sb.toString();
	}

	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}

	@Override
	public int compareTo(TextRepresentation o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
			return true;
		}
	    if (!(other instanceof TextRepresentation)) {
			return false;
		}
	    TextRepresentation otherTextRepresentation = (TextRepresentation) other;
	    return this.toString().equals(otherTextRepresentation.toString());
	 }

}
