/**
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.lmf.transform.wordnet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Intstance of this class parses index.sense file of WordNet.
 */
public class IndexSenseReader {

	private File indexSense; // index.sense - file
	private Map<String, String> senseKeySenseNumberMpg; // mappings between sense keys and sense numbers

	/**
	 * Initializes this instance of {@link IndexSenseReader} by parsing the index.sense file
	 */
	public void initialize(final File indexSenseFile) {
		indexSense = indexSenseFile;
		senseKeySenseNumberMpg = new HashMap<String, String>();
		try {
			read();
		} catch (IOException e) {
			throw new RuntimeException("Could not read index.sense file at "
					+ indexSenseFile, e);
		}
	}

	/** This method parses index.sense file */
	protected void read() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(indexSense));
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				senseKeySenseNumberMpg.put(parts[0], parts[2]);
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * Consumes a sense key of a lexeme and returns it's sense number
	 * @param senseKey the sense key of a lexeme, for which sense number should be returned
	 * @return sense number of the lexeme associated to the consumed senseKey or null, <br> if index.sense file parsed by this instance of {@link IndexSenseReader} contains no entry for the consumed senseKey
	 */
	public String getSenseNumber(String senseKey) {
		return senseKeySenseNumberMpg.get(senseKey);
	}

}
