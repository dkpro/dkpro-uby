/*******************************************************************************
 * Copyright 2012
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
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.lmf.transform.framenet;

import de.saar.coli.salsa.reiter.framenet.CoreType;
import de.saar.coli.salsa.reiter.framenet.FrameNetRelationDirection;
import de.saar.coli.salsa.reiter.framenet.PartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ECoreType;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.EYesNo;
import de.tudarmstadt.ukp.lmf.model.semantics.PredicateRelation;

/**
 * This class offers universal methods for {@link FNConverter}
 * @author Zijad Maksuti, Silvana Hartmann
 */
public class FNUtils {

	/**
	 * This method removes the XSL-tags that are contained in FrameNet's files
	 * @param aString String containing XSL-tags
	 * @return String without XSL-tags
	 */
	public static String filterTags(String aString){

		aString = aString.replaceAll("&lt;!--", "");
		aString = aString.replaceAll("--&gt;", "");

		aString = aString.replaceAll("<!--", "");
		aString = aString.replaceAll("-->", "");
		StringBuffer sb = new StringBuffer(aString);
		int start;
		int end;
		while((start = sb.indexOf("&lt;")) > -1 && (end = sb.substring(start).indexOf("&gt;")) > -1) {
            sb.delete(start, start+end+4);
        }

		while((start = sb.indexOf("<")) > -1 && (end = sb.substring(start).indexOf(">")) > -1) {
            sb.delete(start, start+end+1);
        }

		// line feeds
		String result = sb.toString().replaceAll("\n", "\n");
		result = sb.toString().replaceAll("&#10;", "\n");


		// quotation marks
		result = result.replaceAll("&quot;", "\"");
		return result;
	}

	/**
	 * This method consumes a part of speech, defined by <a href="http://www.cl.uni-heidelberg.de/trac/FrameNetAPI/wiki/0.4.2"> API used for parsing FrameNet's files</a>,
	 * <br> and returns the associated part of speech defined in Uby-LMF
	 * @param pos part of speech defined in {@link PartOfSpeech}
	 * @return Uby's {@link EPartOfSpeech} that is associated with the consumed pos
	 */
	public static EPartOfSpeech getPOS(PartOfSpeech pos){
		switch (pos){
			case Adjective : return EPartOfSpeech.adjective;
			case Adverb : return EPartOfSpeech.adverb;
			case Conjunction : return EPartOfSpeech.conjunction;
			case Determiner : return EPartOfSpeech.determiner;
			case Interjection : return EPartOfSpeech.interjection;
			case Noun : return EPartOfSpeech.noun;
			case Preposition : return EPartOfSpeech.adpositionPreposition;
			case Pronoun : return EPartOfSpeech.pronoun;
			case Verb : return EPartOfSpeech.verb;
			case SCON : return EPartOfSpeech.conjunctionCoordinating;
			default : return null;
		}
	}

	/**
	 * Consumes a boolean value and returns associated value in {@link EYesNo}
	 * @param bool a boolean value
	 * @return {@link EYesNo#yes} if bool is true, {@link EYesNo#no} otherwise
	 */
	public static EYesNo booleanForHumans(boolean bool){
		if(bool) {
            return EYesNo.yes;
        }
        else {
            return EYesNo.no;
        }
	}

	/**
	 * Consumes a core type,
	 * defined in <a href="http://www.cl.uni-heidelberg.de/trac/FrameNetAPI/wiki/0.4.2"> API used for parsing FrameNet's files</a>
	 * and returns the associated core type defined in Uby-LMF
	 * @param coreType core type defined in {@link CoreType}
	 * @return Uby's {@link ECoreType} that is associated with the consumed coreType
	 */
	public static ECoreType getCoreType(CoreType coreType){
		switch(coreType){
			case Core : return ECoreType.core;
			case Peripheral : return ECoreType.peripheral;
			case Core_Unexpressed : return ECoreType.coreUnexpressed;
			case Extra_Thematic : return ECoreType.extraThematic;
			default : return null;
		}
	}

	/**
	 * Consumes a name of frame relation and relation's direction, as
	 * defined in <a href="http://www.cl.uni-heidelberg.de/trac/FrameNetAPI/wiki/0.4.2"> API used for parsing FrameNet's files</a>
	 * and returns the associated relation name of Uby's {@link PredicateRelation}
	 * @param frameNetRelationName FrameNet's relation name
	 * @param direction the direction of the relation
	 * @return name of Uby's PredicateRelation, that corresponds to the consumed frameNetRelationName and the direction
	 * @see {@link Frame}
	 * @see {@link FrameNetRelationDirection}
	 */
	public static String getRelName(String frameNetRelationName, FrameNetRelationDirection direction){
		if(frameNetRelationName.equals("Inheritance")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "inherits_from";
            }
            else {
                return "is_inherited_by";
            }
        }
		if(frameNetRelationName.equals("Subframe")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "subframe_of";
            }
            else {
                return "has_subframe";
            }
        }
		if(frameNetRelationName.equals("Using")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "uses";
            }
            else {
                return "used_by";
            }
        }
		if(frameNetRelationName.equals("See_also")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "referring";
            }
            else {
                return "referred by";
            }
        }
		if(frameNetRelationName.equals("Inchoative_of")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "inchoative";
            }
            else {
                return "is_inchoative_of";
            }
        }
		if(frameNetRelationName.equals("Causative_of")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "causative";
            }
            else {
                return "is_causative_of";
            }
        }
		if(frameNetRelationName.equals("Precedes")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "is_preceded_by";
            }
            else {
                return "precedes";
            }
        }
		if(frameNetRelationName.equals("Perspective_on")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "perspective_on";
            }
            else {
                return "is_perspectivized_in";
            }
        }
		if(frameNetRelationName.equals("ReFraming_Mapping")) {
            if(direction.equals(FrameNetRelationDirection.UP)) {
                return "source";
            }
            else {
                return "target";
            }
        }
		return null;
	}

}
