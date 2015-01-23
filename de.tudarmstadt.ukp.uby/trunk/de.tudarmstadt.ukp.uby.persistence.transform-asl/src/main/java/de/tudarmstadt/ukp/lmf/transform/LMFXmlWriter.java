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
package de.tudarmstadt.ukp.lmf.transform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.xml.sax.SAXException;

/**
 * Basic writer for UBY-XML files. The class serializes the UBY-LMF model
 * objects to XML.
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class LMFXmlWriter extends UBYXMLTransformer {

	/**
	 * Constructs a LMFXmlWriter, XML will be saved to file in outputPath
	 * @param outputPath
	 * @param dtdPath Path of the dtd-File
	 * @throws FileNotFoundException if the writer can not to the specified outputPath
	 */
	public LMFXmlWriter(final String outputPath, final String dtdPath)
			throws FileNotFoundException, SAXException {
		this(new FileOutputStream(outputPath), dtdPath);
	}

	/** Constructs a LMFXmlWriter, XML will be saved to OutputStream out. */
	public LMFXmlWriter(final OutputStream outputStream, final String dtdPath)
			throws SAXException {
		super();
		writeStartDocument(outputStream, dtdPath);
	}

	@Override
	protected String getResourceAlias() {
		return null;
	}

}
