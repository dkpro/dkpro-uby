/**
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package de.tudarmstadt.ukp.lmf.transform.wordnet.util;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.util.JCasUtil.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.uimafit.factory.JCasFactory;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

/**
 * This class offers methods for lemmatizing lexemes in a sentence
 * @author Zijad Maksuti
 *
 */
public class WNConvUtil {

	private static JCas jcas;
	private static AnalysisEngine ae;

	/**
	 * Consumes a sentence and returns the list of all lemmas in the Sentence
	 * @param sentence a sentence for which a list of lemmas should be returned
	 * @return the list of lemmas contained in the consumed sentence
	 */
	public static List<String> lemmatize(String sentence) {
		try {
			
			if (jcas == null) {
				jcas = JCasFactory.createJCas();
			}
			else {
				jcas.reset();
			}
			sentence = sentence.replace("-", " ");
			jcas.setDocumentLanguage("en");
			jcas.setDocumentText(sentence);

			if (ae == null) {
				ae = createAggregate(createAggregateDescription(
						createPrimitiveDescription(BreakIteratorSegmenter.class),
						
						
						createPrimitiveDescription(StanfordLemmatizer.class)
						
						
						));
			}
			
			ae.process(jcas);
			List<String> lemmas = new ArrayList<String>();
			for (Lemma l : select(jcas, Lemma.class)) {
				lemmas.add(l.getValue());
			}
			
			return lemmas;
			
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer(512);
			sb.append("##########################");
			sb.append(sentence);
			sb.append("##########################");
			Logger.getLogger(WNConvUtil.class.getName()).log(Level.SEVERE, sb.toString());
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Consumes a sentence and returns the list of all tokens of the Sentence
	 * @param sentence a sentence for which the list of tokens should be returned
	 * @return the list of tokens of the consumed sentence
	 */
	public static List<String> tokens(String sentence) {
		String temp = sentence.replaceAll("\\.", "");
		temp = temp.replaceAll("\\,", "");
		temp = temp.replaceAll("\\:", "");
		temp = temp.toLowerCase();
		return Arrays.asList(temp.split(" "));
		
	}
	
}
