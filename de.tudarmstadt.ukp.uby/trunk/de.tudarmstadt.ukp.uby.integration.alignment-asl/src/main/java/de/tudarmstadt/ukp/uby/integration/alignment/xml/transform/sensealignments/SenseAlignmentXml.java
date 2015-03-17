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

package de.tudarmstadt.ukp.uby.integration.alignment.xml.transform.sensealignments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import de.tudarmstadt.ukp.integration.alignment.xml.AlignmentXmlWriter;
import de.tudarmstadt.ukp.integration.alignment.xml.model.XmlMeta;

/**
 * Conversion of alignment files to generic alignment xml
 */
public abstract class SenseAlignmentXml {

	public static double DEFAULTCONFIDENCE = 0.0;

	protected static String LF = System.getProperty("line.separator");
	protected String alignmentFile;
	protected AlignmentXmlWriter writer;
	protected Logger logger;
	protected StringBuilder logString;

	public SenseAlignmentXml(String alignmentFile, String outFile) {
		this.alignmentFile = alignmentFile;
		logString = new StringBuilder();
		try {
			FileOutputStream stream = new FileOutputStream(outFile);
			writer = new AlignmentXmlWriter(stream);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public abstract XmlMeta getDefaultXmlMeta();

	public abstract void toAlignmentXml(XmlMeta metadata) throws IOException;
}
