/*******************************************************************************
 * Copyright 2016
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
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.morphology.FormRepresentation;
import de.tudarmstadt.ukp.lmf.model.morphology.Lemma;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * This class represents an extension of the {@link Uby} class to support collecting resource statistics.
 * 
 * @author Silvana Hartmann
 *
 */
public class UbyStatistics extends Uby{

	/**
	 * Creates a {@link UbyStatistics} instance based on the consumed parameter.
	 * 
	 * @param dbConfig
	 *            Configuration holder of the UBY database used for generating the statistics
	 *            
	 * @throws UbyInvalidArgumentException if the provided dbConfig is null 
	 *            
	 * @see DBConfig
	 */
	public UbyStatistics(DBConfig dbConfig) throws IllegalArgumentException {
		super(dbConfig);
	}


	/**
	 * Counts the number of {@link Sense} instances in the {@link Lexicon}
	 * specified by the given name.
	 * 
	 * @param lexiconName
	 * 			name of the lexicon which senses should be counted
	 * 
	 * @return the number of senses in the lexicon or zero, if the
	 * lexicon with the specified name does not exist
	 */
	public long countSensesPerLexicon(String lexiconName){
		Criteria criteria = session.createCriteria(Sense.class);
		criteria = criteria.createCriteria("lexicalEntry").createCriteria("lexicon");
		criteria = criteria.add(Restrictions.eq("name", lexiconName));
		long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return count;
	}

	/**
	 * Counts the number of {@link LexicalEntry} instances in the {@link Lexicon}
	 * specified by the given name.
	 * 
	 * @param lexiconName
	 * 			name of the lexicon which lexical entries should be counted
	 * 
	 * @return the number of lexical entries in the lexicon or zero, if the
	 * lexicon with the specified name does not exist
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
	public long countLemmaPosPerLexiconAndPosPrefixAndLanguage(String lexiconName, String prefix, String lang){
			Set<String> l= getLemmaPosPerLexiconAndPosPrefixAndLanguage(lexiconName, prefix, lang);
			int res = 0;
			if (!l.isEmpty()){
				res = l.size();
			}
			return res;
	}

	/**
	 * Return a {@link Set} of {@link String} instances consisting of <code>lemma+"_"+part-of-speech</code>,
	 * 		filtered by given {@link Lexicon} name.<br>
	 * The lemma is obtained from the written form of the first {@link FormRepresentation} of the {@link Lemma}
	 * instance.
	 * @param lexiconName
	 * 			name of the lexicon which lemmas should be used
	 * 
	 * @return a set of strings containing lemma and part-of-speech of the specified lexicon.<br>
	 * This method returns an empty set if the lexicon with the specified name does no exist.
	 * 
	 * @see Lemma#getFormRepresentations()
	 * @see FormRepresentation#getWrittenForm()
	 * @see EPartOfSpeech
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
				if (r[1] != null){ // some resources do not have POS
					out.add((String)r[0] +"_"+((EPartOfSpeech)r[1]).toString());
				} else {
					out.add((String)r[0] +"_null");
				}

			}
			HashSet<String> out2 = new HashSet<String>(out);
		return out2;
	}

	/**
	 * Return a {@link Set} of {@link String} instances consisting of <code>lemma+"_"+part-of-speech</code>,
	 * 		filtered by given {@link Lexicon} name, part-of-speech prefix and a language identifier.<br>
	 * The lemma is obtained from the written form of the first {@link FormRepresentation} of the {@link Lemma}
	 * instance.
	 * 
	 * @param lexiconName
	 * 			name of the lexicon which lemmas should be used
	 * 
	 * @param prefix the part-of-speech prefix used when filtering {@link LexicalEntry} instances
	 * 
	 * @param lang the language identifier used when filtering lexical entries
	 * 
	 * @return a set of strings containing lemma and part-of-speech of the specified lexicon.<br>
	 * 
	 * This method returns an empty set if the lexicon with the specified name does no exist or
	 * the lexicon does not contain any lexical entries with specified part-of-speech prefix and language
	 * identifier.
	 * 
	 * @see Lemma#getFormRepresentations()
	 * @see FormRepresentation#getWrittenForm()
	 * @see EPartOfSpeech
	 * @see ELanguageIdentifier
	 */
	public Set<String> getLemmaPosPerLexiconAndPosPrefixAndLanguage(String lexiconName, String prefix, String lang){
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
	 * Counts the number of {@link SenseRelation} instances within the {@link Lexicon}
	 * specified by the given name.
	 * 
	 * @param lexiconName
	 * 			name of the lexicon which senses should be counted
	 * 
	 * @return the number of sense relations in the lexicon or zero if the
	 * lexicon with the specified name does not exist
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
	 * Counts the number of {@link Sense} instances within the UBY-LMF {@link LexicalResource}
	 * contained in the database accessed by this {@link UbyStatistics} instance.
	 * 
	 * @return the number of senses in the lexical resource or zero if the
	 * lexical resource does not contain any senses
	 */
	public long countSenses(){
		return countClassEntities(Sense.class);
	}

	/**
	 * Counts the number of {@link LexicalEntry} instances within the UBY-LMF {@link LexicalResource}
	 * contained in the database accessed by this {@link UbyStatistics} instance.
	 * 
	 * @return the number of lexical entries in the lexical resource or zero if the
	 * lexical resource does not contain any lexical entries
	 */
	public long countLexicalEntries(){
		return countClassEntities(LexicalEntry.class);
	}

	/**
	 * Counts the number of {@link SenseAxis} instances within the UBY-LMF {@link LexicalResource}
	 * contained in the database accessed by this {@link UbyStatistics} instance.
	 * 
	 * @return the number of sense axes in the lexical resource or zero if the
	 * lexical resource does not contain any sense axes
	 */
	public long countSenseAxes() {
		return countClassEntities(SenseAxis.class);
	}
	
	/**
	 * Counts the number of instances of a specified UBY-LMF class.
	 * The instances are counted within the UBY-LMF {@link LexicalResource}
	 * contained in the database accessed by this {@link UbyStatistics} instance.
	 * 
	 * @param ubyClass specifies the UBY-LMF class which instances should be counted
	 * 
	 * @return the number of specified UBY-LMF class instances contained in the
	 * lexical resource or zero if the lexical resource does not contain any instances
	 * of the specified class 
	 */
	public long countClassEntities(@SuppressWarnings("rawtypes") Class ubyClass){
		return (Long) session.createCriteria(ubyClass).setProjection(Projections.rowCount()).uniqueResult();
	}



	/**
	 * Counts the number of {@link SenseAxis} instances between two {@link Lexicon} instances
	 * identified by their name. The counted sense axes are filtered by the
	 * specified type.<p>
	 * <b>Important properties of this method:</b>
	 * <ul>
	 * 		<li>Only alignments between {@link Sense} instances are considered.</li>
	 * 		<li>The sources of the alignments are not distinguished.</li>
	 * 		<li>The lexicons are identified by identifier prefixes of the aligned senses.</li>
	 * </ul>
	 * 		
	 * @param type
	 * 			Type of sense axes to be considered when counting
	 * 
	 * @param lex1Name
	 * 			The name of the first of two lexicons between which sense axes should be counted
	 * 
	 * @param lex2Name
	 * 			The name of the second of two lexicons between which sense axes should be counted
	 * 
	 * @return the number of sense axes between the lexicons filtered by the specified sense axes type.
	 * This method returns zero if a lexicon with the specified name does not exist or one of the
	 * consumed arguments is null.
	 * 
	 * @see ESenseAxisType
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
	 * Returns a {@link List} containing all {@link SenseAxis} instances between two {@link Lexicon} instances
	 * identified by their name. The counted sense axes are filtered by the
	 * specified type.<p>
	 * <b>Important properties of this method:</b>
	 * <ul>
	 * 		<li>Only alignments between {@link Sense} instances are considered.</li>
	 * 		<li>The sources of the alignments are not distinguished.</li>
	 * 		<li>The lexicons are identified by identifier prefixes of the aligned senses.</li>
	 * </ul>
	 * 		
	 * @param type
	 * 			Type of sense axes to be returned
	 * 
	 * @param lex1Name
	 * 			The name of the first of two lexicons between which sense axes should be found
	 * @param lex2Name
	 * 			The name of the second of two lexicons between which sense axes should be found
	 * 
	 * @return the list of sense axes between the lexicons filtered by the specified sense axes type.
	 * This method returns an empty list if a lexicon with the specified name does not exist or one of
	 * the consumed arguments is null.
	 * 
	 * @see ESenseAxisType
	 */
	@SuppressWarnings("unchecked")
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
		}
		else return new ArrayList<SenseAxis>();
	}
}