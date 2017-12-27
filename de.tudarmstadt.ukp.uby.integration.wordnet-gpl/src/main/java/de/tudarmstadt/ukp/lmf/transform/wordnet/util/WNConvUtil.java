/**
 * Copyright 2017
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


import java.util.LinkedList;
import java.util.List;

import net.sf.extjwnl.data.POS;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;

/**
 * This class offers methods offers some helper methods used when converting
 * WordNet UBY-LMF.
 * @author Zijad Maksuti
 *
 */
public class WNConvUtil {

	/**
	 * This method consumes a {@link POS}
	 * and returns corresponding {@link EPartOfSpeech}
	 * @param pos part of speech encoded in extJWNL-API
	 * @return associated part of speech defined in UBY-LMF
	 * @since 0.2.0
	 */
	public static EPartOfSpeech getPOS(POS pos) {
		switch (pos) {
			case NOUN:
				return EPartOfSpeech.noun;
			case VERB:
				return EPartOfSpeech.verb;
			case ADJECTIVE:
				return EPartOfSpeech.adjective;
			case ADVERB:
				return EPartOfSpeech.adverb;
		}
		return null;
	}

	public static List<TextRepresentation> makeTextRepresentationList(
			final String text, final String language) {
		List<TextRepresentation> result = new LinkedList<TextRepresentation>();
		TextRepresentation textRepresentation = new TextRepresentation();
		textRepresentation.setLanguageIdentifier(language);
		textRepresentation.setWrittenText(text);
		result.add(textRepresentation);
		return result;
	}

}
