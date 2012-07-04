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
package de.tudarmstadt.ukp.lmf.model.abstracts;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.lmf.model.interfaces.IHasMonolingualExternalRefs;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;

/**
 * A parent class for UBY-LMF classes containing one or more
 * references to an external system, represented by a {@link List} of {@link MonolingualExternalRef} instances.
 * 
 * @author Zijad Maksuti
 *
 */
public class HasMonolingualExternalRefs implements IHasMonolingualExternalRefs {
	
	// references to external resources
	@VarType(type = EVarType.CHILDREN)
	protected List<MonolingualExternalRef> monolingualExternalRefs = new ArrayList<MonolingualExternalRef>();

	@Override
	public List<MonolingualExternalRef> getMonolingualExternalRefs() {
		return this.monolingualExternalRefs;
	}

	@Override
	public void setMonolingualExternalRefs(List<MonolingualExternalRef> monolingualExternalRefs) {
		if(this.monolingualExternalRefs != null)
			this.monolingualExternalRefs =  monolingualExternalRefs;
	}
	
}
