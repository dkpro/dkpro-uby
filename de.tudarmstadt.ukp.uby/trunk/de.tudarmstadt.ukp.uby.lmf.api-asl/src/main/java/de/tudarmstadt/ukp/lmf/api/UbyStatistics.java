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
package de.tudarmstadt.ukp.lmf.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * An extension of the Uby API to support collecting resource statistics
 * (Could be integrated to Uby.java)
 * @author sh
 *
 */
public class UbyStatistics extends Uby{

	/**
	 * @param dbConfig
	 *            Database configuration of the Uby database
	 */
	public UbyStatistics(DBConfig dbConfig){
		super(dbConfig);
	}


	/**
	 * Count the number of senses in the given lexicon
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @return the number of senses in the lexicon
	 */
	public long countSensesPerLexicon(String lexiconName){
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.createCriteria("lexicalEntry").createCriteria("lexicon");
		criteria = criteria.add(Restrictions.eq("name", lexiconName));
		long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return count;
	}

	/**
	 * Count the number of lexical entries in the given lexicon
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @return the number of lexical entries in the lexicon
	 */
	public long countLexicalEntriesPerLexicon(String lexiconName){
		Criteria criteria = session.createCriteria(LexicalEntry.class);
		criteria = criteria.createCriteria("lexicon");
		criteria = criteria.add(Restrictions.eq("name", lexiconName));
		long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return count;
	}

	/**
	 * Count the number of lemma+pos combinations per lexicon
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @return the number of lemma+pos combinations in the lexicon
	 */
	public long countLemmaPosPerLexicon(String lexiconName){
			System.out.println(lexiconName);
			Set<String> l = getLemmaPosPerLexicon(lexiconName);
			int res = 0;
			if (!l.isEmpty()){
				return l.size();
			}
			return res;
//		Criteria criteria = session.createCriteria(Lexicon.class,"l");
////		if (pos != null) {
////			criteria = criteria.add(Restrictions.eq("partOfSpeech", pos));
////		}
//		criteria = criteria.createCriteria("lexicalEntries", "e");
//		if (lexiconName != null) {
//			criteria = criteria.add(Restrictions.sqlRestriction("lexiconName = '"
//					+ lexiconName + "'"));
//		}
//		criteria = criteria.createCriteria("lemma")
//				.createCriteria("formRepresentations", "f")
//				.setProjection(Projections.projectionList()
//						.add(Property.forName("f.writtenForm"))
//					    .add(Property.forName("e.partOfSpeech")));
////		ProjectionList p = Projections.projectionList().add(Projections.countDistinct("f.writtenForm")).add(Projections.countDistinct("e.partOfSpeech"));
//		Long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
//		return count;
	}


	/**
	 * Count the number of lemma+pos combinations per lexicon, 
	 * 		part-of-speech prefix 
	 * 		and language 
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @param prefix
	 * 			The partOfSpeech prefix
	 * @param lang
	 * 			The language identifier of the lexicon
	 * @return the number of lemma+pos combinations
	 */
	public long countLemmaPosPerLexiconAndPosPrefixAndLanguage(String lexiconName, String prefix, ELanguageIdentifier lang){
			Set<String> l= getLemmaPosPerLexiconAndPosPrefixAndLanguage(lexiconName, prefix, lang);
			int res = 0;
			if (!l.isEmpty()){
				res = l.size();
			}
			return res;
	}	
	
	/**
	 * Return a list of strings consisting of lemma+"_"+part-of-speech 
	 * 		filtered by lexicon
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @return a list of strings containing lemma and pos information
	 */
	public Set<String> getLemmaPosPerLexicon(String lexiconName){
			Criteria criteria = session.createCriteria(Lexicon.class,"l");
			criteria = criteria.createCriteria("lexicalEntries", "e");
			if (lexiconName != null) {
				criteria = criteria.add(Restrictions.eq("l.name", lexiconName));
			}
			criteria = criteria.createCriteria("lemma")
					.createCriteria("formRepresentations", "f")
					.setProjection(Projections.projectionList()
							.add(Property.forName("f.writtenForm"))
						    .add(Property.forName("e.partOfSpeech")));
			ScrollableResults res = criteria.scroll();
			ArrayList<String> out = new ArrayList<String>();
			while (res.next()){
				Object[] r = res.get();
				if (r[1] != null){ // some resources do not have pOS
					out.add((String)r[0] +"_"+((EPartOfSpeech)r[1]).toString());
				} else {
					out.add((String)r[0] +"_null");
				}
				
			}
			HashSet<String> out2 = new HashSet<String>(out);
		return out2;
	}
	
	/**
	 * Return a list of strings consisting of lemma+"_"+part-of-speech
	 * 		filtered by lexicon, part-of-speech prefix and language
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @param prefix
	 * 			The partOfSpeech prefix
	 * @param lang
	 * 			The language identifier of the lexicon
	 * @return
	 */
	public Set<String> getLemmaPosPerLexiconAndPosPrefixAndLanguage(String lexiconName, String prefix, ELanguageIdentifier lang){
			Criteria criteria = session.createCriteria(Lexicon.class,"l");

			criteria = criteria.createCriteria("lexicalEntries", "e");
			if (lexiconName != null) {
				criteria = criteria.add(Restrictions.eq("l.name", lexiconName));
			}
			if (lang != null) {
				criteria = criteria.add(Restrictions.eq("l.languageIdentifier", lang));
			}
			if (prefix != null) {
				criteria = criteria.add(Restrictions.sqlRestriction("partOfSpeech like '"+prefix+"'"));
			}
			criteria = criteria.createCriteria("lemma")
					.createCriteria("formRepresentations", "f")
					.setProjection(Projections.projectionList()
							.add(Property.forName("f.writtenForm"))
						    .add(Property.forName("e.partOfSpeech")));
			ScrollableResults res = criteria.scroll();
			ArrayList<String> out = new ArrayList<String>();
			while (res.next()){
				Object[] r = res.get();
				if (r[1] != null){
					out.add((String)r[0]+"_"+((EPartOfSpeech)r[1]).toString());
				} else {
					out.add((String)r[0]+"_null");
				}
			}
			HashSet<String> out2 = new HashSet<String>(out);
		return out2;

	}

	/**
	 * Count the number of sense relations within this lexicon
	 * @param lexiconName
	 * 			Name of the lexicon
	 * @return the number
	 */
	public long countSenseRelationsPerLexicon(String lexiconName) {
		Criteria criteria = session.createCriteria(SenseRelation.class);
		criteria = criteria.createCriteria("source");
		criteria = criteria.createCriteria("lexicalEntry");
		criteria = criteria.createCriteria("lexicon");
		criteria = criteria.add(Restrictions.eq("name", lexiconName));
		return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}	
	
	/**
	 * Count the senses in Uby
	 * @return the number of senses
	 */
	public long countSenses(){
		return countClassEntities(Sense.class);		
	}
	
	/**
	 * Count the lexical entries in Uby
	 * @return the number of lexical entries
	 */
	public long countLexicalEntries(){
		return countClassEntities(LexicalEntry.class);
	}
	
	/**
	 * Count the number of sense axes in uby
	 * @return a list of alignments in uby
	 */
	public long countSenseAxes() {
		return countClassEntities(SenseAxis.class);
	}	
	/**
	 * Count the class instances for the given class
	 * @param ubyClass
	 * @return the number of class instances in uby
	 */
	public long countClassEntities(Class ubyClass){
		return (Long) session.createCriteria(ubyClass).setProjection(Projections.rowCount()).uniqueResult();
	}
	


	/**
	 * Count the sense axes of a particular sense axis type 
	 * 		between two lexicons identified by their name.
	 * 		! Note that lexicons are identified by senseId-prefixes 
	 * 		! and that sources of the alignments are not distinguished.
	 * 		! and that only alignments between senses are considered;
	 * @param type
	 * 			The type of the sense axis
	 * @param lex1Name
	 * 			Name of lexicon 1
	 * @param lex2Name
	 * 			Name of lexicon 2
	 * @return the number of sense axes between the lexicons
	 */
	public long countSenseAxesPerLexiconPair(ESenseAxisType type, String lex1Name, String lex2Name){
		// get prefix for res1Name
		Criteria c1 = session.createCriteria(Sense.class,"s");
		c1 = c1.createCriteria("lexicalEntry");
		c1 = c1.createCriteria("lexicon");
		c1 = c1.add(Restrictions.eq("name", lex1Name));
		c1 = c1.setProjection(Projections.property("s.id"));
		c1 = c1.setMaxResults(1);
		String res1 = (String)c1.uniqueResult();
		//get prefix for res2Name
		Criteria c2 = session.createCriteria(Sense.class,"s");
		c2 = c2.createCriteria("lexicalEntry");
		c2 = c2.createCriteria("lexicon");
		c2 = c2.add(Restrictions.eq("name", lex2Name));
		c2 = c2.setProjection(Projections.property("s.id"));
		c2 = c2.setMaxResults(1);
		String res2 = (String)c2.uniqueResult();
		String pref1 = "";
		String pref2 = "";
		if (res1!=null && res2!=null){
			pref1 = res1.split("_")[0]; 
			if (res1.split("_")[1].equals("en")  ||res1.split("_")[1].equals("de") ){
				pref1 +=  "_" + res1.split("_")[1];
			}
			pref2 = res2.split("_")[0];
			if (res2.split("_")[1].equals("en")  || res2.split("_")[1].equals("de") ){
				pref2 +=  "_" + res2.split("_")[1];
			}
		// get alignments with these prefixes
		Criteria criteria = session.createCriteria(SenseAxis.class);
		criteria = criteria.add(Restrictions.eq("senseAxisType", type));
		criteria = criteria.add(Restrictions.like("senseOne.id", pref1, MatchMode.START));
		criteria = criteria.add(Restrictions.like("senseTwo.id", pref2, MatchMode.START));
		criteria = criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		} else {
			return 0L;
		}
	}
	
	/**
	 * Count the sense axes of a particular sense axis type 
	 * 		between two lexicons identified by their name.
	 * 		! Note that lexicons are identified by senseId-prefixes 
	 * 		! and that sources of the alignments are not distinguished.
	 * 		! and that only alignments between senses are considered;
	 * @param type
	 * 			The type of the sense axis
	 * @param lex1Name
	 * 			Name of lexicon 1
	 * @param lex2Name
	 * 			Name of lexicon 2
	 * @return the number of sense axes between the lexicons
	 */
	public List<SenseAxis> getSenseAxesPerLexiconPair(ESenseAxisType type, String lex1Name, String lex2Name){
		// get prefix for res1Name
		Criteria c1 = session.createCriteria(Sense.class,"s");
		c1 = c1.createCriteria("lexicalEntry");
		c1 = c1.createCriteria("lexicon");
		c1 = c1.add(Restrictions.eq("name", lex1Name));
		c1 = c1.setProjection(Projections.property("s.id"));
		c1 = c1.setMaxResults(1);
		String res1 = (String)c1.uniqueResult();
		//get prefix for res2Name
		Criteria c2 = session.createCriteria(Sense.class,"s");
		c2 = c2.createCriteria("lexicalEntry");
		c2 = c2.createCriteria("lexicon");
		c2 = c2.add(Restrictions.eq("name", lex2Name));
		c2 = c2.setProjection(Projections.property("s.id"));
		c2 = c2.setMaxResults(1);
		String res2 = (String)c2.uniqueResult();
		String pref1 = "";
		String pref2 = "";
		if (res1!=null && res2!=null){
			pref1 = res1.split("_")[0]; 
			if (res1.split("_")[1].equals("en")  ||res1.split("_")[1].equals("de") ){
				pref1 +=  "_" + res1.split("_")[1];
			}
			pref2 = res2.split("_")[0];
			if (res2.split("_")[1].equals("en")  || res2.split("_")[1].equals("de") ){
				pref2 +=  "_" + res2.split("_")[1];
			}
			// get alignments with these prefixes
			Criteria criteria = session.createCriteria(SenseAxis.class);
			criteria = criteria.add(Restrictions.eq("senseAxisType", type));
			criteria = criteria.add(Restrictions.like("senseOne.id", pref1, MatchMode.START));
			criteria = criteria.add(Restrictions.like("senseTwo.id", pref2, MatchMode.START));
			return criteria.list();
		} else {
			return null;
		}
	}
}