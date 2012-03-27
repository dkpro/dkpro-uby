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
package de.tudarmstadt.ukp.lmf.transform.wikipedia;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

/**
 * Maps Wikipedia constants to LMF constants
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class WikipediaLMFMap {
	
	/**
	 * Maps Wikipedia Language to LMF LanguageIdentifier
	 * @param lang
	 * @return
	 */
	public static ELanguageIdentifier mapLanguage(Language lang){
		if(lang.equals(Language.english)) {
			return ELanguageIdentifier.en;
		}
		else if(lang.equals(Language.german)) {
			return ELanguageIdentifier.de;
		}
		else {
			return null;
		}
	}

}
