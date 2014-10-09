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
package de.tudarmstadt.ukp.alignment.framework.uima;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;



public class StringReader extends CasCollectionReader_ImplBase {


	public static final String PARAM_CONTENT = "String for processing";
	@ConfigurationParameter(name = PARAM_CONTENT, mandatory=true)
	private String mContent;

	public static final String PARAM_LANGUAGE= "en";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory=true)
	private String language;


	private boolean done=false;
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {

		JCas jcas = null;
		try {
			jcas = aCAS.getJCas();
			aCAS.setDocumentLanguage(language);
		} catch (CASException e) {
			e.printStackTrace();
		}
		jcas.setDocumentText(mContent);
		done=true;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return !done;
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(1, 1, mContent) };
	}

	@Override
	public void close()
		throws IOException
	{
		// TODO Auto-generated method stub

	}

}