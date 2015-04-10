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
package de.tudarmstadt.ukp.alignment.framework.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.alignment.framework.uima.Toolkit.PosGetter;



public class StringWriter extends CasConsumer_ImplBase {

	public static Object mContent;
	public static PosGetter getter;

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
			System.out.println(aCAS);
			mContent = getter.retrieveData(aCAS);

		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
