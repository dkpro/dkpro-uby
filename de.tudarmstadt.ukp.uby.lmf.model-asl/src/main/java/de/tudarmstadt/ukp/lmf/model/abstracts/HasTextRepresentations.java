/*******************************************************************************
 * Copyright 2017
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
package de.tudarmstadt.ukp.lmf.model.abstracts;

import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasTextRepresentations;

/**
 * Base class for all containers that aggegrate multiple
 * {@link TextRepresentation} instances in a list.
 */
public abstract class HasTextRepresentations implements IHasTextRepresentations {

	public String getText() {
		StringBuilder result = new StringBuilder();
		for (TextRepresentation textRep : getTextRepresentations()) {
			if (result.length() > 0)
				result.append("\n");
			result.append(textRep.getWrittenText());
		}
		return result.toString();
	}

}
