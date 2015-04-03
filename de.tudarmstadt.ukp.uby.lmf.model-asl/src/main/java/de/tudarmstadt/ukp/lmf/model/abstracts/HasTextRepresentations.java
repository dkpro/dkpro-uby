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
