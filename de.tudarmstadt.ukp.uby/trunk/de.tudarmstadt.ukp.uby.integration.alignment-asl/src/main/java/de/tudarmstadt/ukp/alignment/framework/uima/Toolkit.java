package de.tudarmstadt.ukp.alignment.framework.uima;

//import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
//import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;



public class Toolkit
{
	public static HashMap<String,String> posMapping = new HashMap<String, String>();

	public static final String LF = System.getProperty("line.separator");

	public static double[] normalizeVector(double[] vector)
	{
		double length=0;
		for(double v_i: vector)
		{
			length+=Math.pow(v_i, 2);
		}
		length = Math.sqrt(length);
		for(int i =0;i< vector.length;i++)
		{
			vector[i]=vector[i] / length;
		}

		return vector;
	}


	public static double[] calu(double[] vector)
	{
		double length=0;
		for(double v_i: vector)
		{
			length+=Math.pow(v_i, 2);
		}
		length = Math.sqrt(length);
		for(int i =0;i< vector.length;i++)
		{
			vector[i]=vector[i] / length;
		}

		return vector;
	}




	public static double[] calculateNminus1distance(HashSet<double[]> points, double point)
	{
		double[] centroid = null;
		double dem = 0;
		for (double[] element : points)
		{
			dem++;
			if(centroid==null)
			{
				centroid = element.clone();
			}else
			{
				for(int i=0;i<element.length;i++) {
					centroid[i]+=element[i];
				}
			}
		}

		for(int i =0;i< centroid.length;i++)
		{
			centroid[i]=centroid[i] / dem;
		}

		return centroid;
	}





	public static double[] calculateCentroid(HashSet<double[]> points)
	{
		double[] centroid = null;
		double dem = 0;
		for (double[] element : points)
		{
			dem++;
			if(centroid==null)
			{
				centroid = element.clone();
			}else
			{
				for(int i=0;i<element.length;i++) {
					centroid[i]+=element[i];
				}
			}
		}

		for(int i =0;i< centroid.length;i++)
		{
			centroid[i]=centroid[i] / dem;
		}

		return centroid;
	}

	public static double cosineSimilarity(double[] vector1, double[] vector2)
	{
		double sum = 0;
		double asum = 0;
		double bsum = 0;
		for(int i = 0; i<vector2.length;i++)
		{
			sum+= vector1[i]*vector2[i];
			asum+=vector1[i]*vector1[i];
			bsum+=vector2[i]*vector2[i];
		}
		return sum / (Math.sqrt(asum)* Math.sqrt(bsum));

	}

	public static double bitSimilarity(BitSet vector1, BitSet vector2, boolean normalize)
	{
		BitSet result =(BitSet) vector1.clone();
		result.and(vector2);
		double max = Math.max(vector1.cardinality(), vector2.cardinality());
		if(!normalize) {
			return result.cardinality();
		}
		else {
			// System.out.println((result.cardinality()) / max);
			if(max == 0.0) {
				return max;
			}
			else {
				return (result.cardinality()) / max;
			}
		}

	}



	@SuppressWarnings("restriction")
	public static String[] process(String input,PosGetter getter,AnalysisEngineDescription... aeds) {
		try {

			 CollectionReaderDescription cr = createReaderDescription(
			         StringReader.class  ,
			         StringReader.PARAM_CONTENT, input,
			         StringReader.PARAM_LANGUAGE, "en"
			         );

			AnalysisEngineDescription cc = createEngineDescription(StringWriter.class);
			StringWriter.getter=getter;
			AnalysisEngineDescription[] aeds2 = new AnalysisEngineDescription[aeds.length+1];
			for(int i=0;i<aeds.length;i++) {
				aeds2[i]=aeds[i];
			}
			aeds2[aeds.length]=cc;
			runPipeline(cr,aeds2);
			return (String[]) StringWriter.mContent;
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		} catch (UIMAException e) {
			e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
		}
		return null;
	}


	public static String posMatcher(String inputPos)
	{
		String ret;
		if ((ret=posMapping.get(inputPos))==null) {
			return "unknown";
		}
		else {
			return ret;
		}

	}

	public static void initializePOS()
	{
		posMapping.put("CC", "coordinatingConjunction");
		posMapping.put("CD", "numeral");
		posMapping.put("DT", "determiner");
		posMapping.put("IN", "conjunction");
		posMapping.put("JJ", "adjective");
		posMapping.put("JJR", "adjective");
		posMapping.put("JJS", "adjective");
		posMapping.put("NN", "noun");
		posMapping.put("NNS", "noun");
		posMapping.put("NP", "noun");
		posMapping.put("NPS", "noun");
		posMapping.put("PDT", "adverb");
		posMapping.put("PP", "personalPronoun");
		posMapping.put("PP$", "possessivePronoun");
		posMapping.put("RB", "adverb");
		posMapping.put("RBR", "adverb");
		posMapping.put("RBR", "adverb");
		posMapping.put("RP", "particle");
		posMapping.put("UH", "interjection");
		posMapping.put("VB", "verb");
		posMapping.put("VBD", "verb");
		posMapping.put("VBG", "verb");
		posMapping.put("VBN", "verb");
		posMapping.put("VBP", "verb");
		posMapping.put("VBZ", "verb");
		posMapping.put("VV", "verb");
		posMapping.put("VVD", "verb");
		posMapping.put("VVG", "verb");
		posMapping.put("VVN", "verb");
		posMapping.put("VVP", "verb");
		posMapping.put("VVZ", "verb");
		posMapping.put("WDT", "relativePronoun");
		posMapping.put("WP", "interrogativePronoun");
		posMapping.put("WP$", "interrogativePronoun");
		posMapping.put("WRB", "interrogativePronoun");

	}



	public static void initializePOSGerman()
	{
		posMapping.put("CC", "coordinatingConjunction");
		posMapping.put("CARD", "numeral");
		posMapping.put("DT", "determiner");
		posMapping.put("IN", "conjunction");

		posMapping.put("ADJA", "adjective");
		posMapping.put("ADJD", "adjective");
		posMapping.put("JJS", "adjective");
		posMapping.put("NN", "noun");
		posMapping.put("NE", "noun");
		posMapping.put("NP", "noun");
		posMapping.put("NPS", "noun");

		posMapping.put("ADV", "adverb");

		posMapping.put("PP", "personalPronoun");
		posMapping.put("PP$", "possessivePronoun");
		posMapping.put("RB", "adverb");
		posMapping.put("RBR", "adverb");
		posMapping.put("RBR", "adverb");
		posMapping.put("RP", "particle");
		posMapping.put("UH", "interjection");
		posMapping.put("VVFIN", "verb");
		posMapping.put("VVIMP", "verb");
		posMapping.put("VVINF", "verb");
		posMapping.put("VVIZU", "verb");
		posMapping.put("VVPP", "verb");
		posMapping.put("VAFIN", "verb");
		posMapping.put("VAIMP", "verb");
		posMapping.put("VAINF", "verb");
		posMapping.put("VAPP", "verb");
		posMapping.put("VMFIN", "verb");
		posMapping.put("VMINF", "verb");
		posMapping.put("VMPP", "verb");
		posMapping.put("WDT", "relativePronoun");
		posMapping.put("WP", "interrogativePronoun");
		posMapping.put("WP$", "interrogativePronoun");
		posMapping.put("WRB", "interrogativePronoun");

	}

	public static String[] lemmatizeGerman(String text) throws ResourceInitializationException {

		if (text=="" || text==null) {
			return null;
		}
		AnalysisEngineDescription seg;
		try {
			seg = createEngineDescription(StanfordSegmenter.class);
				AnalysisEngineDescription sw = createEngineDescription(StopWordRemover.class,
					StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES, new String[]{"/home/matuschek/UBY_HOME/resources/snowball_german_stopwords.txt"}

				);
			AnalysisEngineDescription pos = createEngineDescription(TreeTaggerPosLemmaTT4J.class,
					TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE,"de");
			String[] result;

			result = process(text, new PosGetter(), seg,sw,pos);

			return result;
		}
		catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String[] lemmatizeEnglish(String text) throws ResourceInitializationException {

		if (text=="" || text==null) {
			return null;
		}

		try {

			 CollectionReaderDescription cr = createReaderDescription(
			         StringReader.class  ,
			         StringReader.PARAM_CONTENT, text,
			         StringReader.PARAM_LANGUAGE, "en"
			         );

			      AnalysisEngineDescription     seg = createEngineDescription(StanfordSegmenter.class, StanfordSegmenter.PARAM_LANGUAGE,"en");
			      AnalysisEngineDescription     lemma = createEngineDescription(StanfordLemmatizer.class);
					AnalysisEngineDescription pos = createEngineDescription(StanfordPosTagger.class	);
					HashSet<String> swords = new HashSet<String>();
					 swords.add("/home/matuschek/UBY_HOME/resources/snowball_english_stopwords.txt");
			AnalysisEngineDescription sw = createEngineDescription(StopWordRemover.class,
				StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES,  swords
					//StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES, new String[]{"/home/matuschek/UBY_HOME/resources/snowball_english_stopwords.txt"}
						);
//					AnalysisEngineDescription pos = createEngineDescription(TreeTaggerPosLemmaTT4J.class,
//							TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE,"en");


			      StringWriter.getter=new PosGetter();
			      AnalysisEngineDescription cc = createEngineDescription(StringWriter.class);
//			    AnalysisEngineDescription tagger = createEngineDescription(OpenNlpPosTagger.class);
			      runPipeline(cr, seg,sw, lemma,  pos,  cc);
			     String[] result = (String[]) StringWriter.mContent;
			     return result;

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}



	public static class PosGetter {


/*		public Object retrieveData_old2(JCas cas) {
			String ret = "";
			FSIterator<org.apache.uima.jcas.tcas.Annotation> ai = cas.getAnnotationIndex(StopWord.type).iterator();
			org.apache.uima.jcas.tcas.Annotation nextStop = ai.next();
			for(org.apache.uima.jcas.tcas.Annotation annot : cas.getAnnotationIndex(Token.type)) {
				if(annot.getBegin() == nextStop.getBegin() && ai.hasNext()) {
					nextStop = ai.next();
				} else {
					//ret+=((Token)annot).getLemma().getValue()+"#"+((Token)annot).getPos().getPosValue().toLowerCase().charAt(0)+" ";
					ret+=annot.getCoveredText()+"#"+((Token)annot).getPos().getPosValue().replace("J","A").toLowerCase().charAt(0)+" ";
				}
			}
			return ret;
		}*/
		public Object retrieveData(CAS cas) {
			Vector<String> rets= new Vector<String>();
		//	System.out.println(cas.getAnnotationIndex(Token.type).size());

			for(AnnotationFS annot : cas.getAnnotationIndex()) {
				if(!Token.class.toString().contains(annot.getType().getName())) {
//					System.out.println(annot.getType().getName());
//					System.out.println(Token.class);
//					System.out.println("No Token");
					continue;
				}

				String tok=annot.getCoveredText();

				tok=tok.replaceAll("@[.:;,\"'´`]","" );
				if (tok.length()>0 && ((Token)annot).getPos() != null ) {
					//ret+=annot.getCoveredText()+" ";
				//	rets.add(((Token)annot).getLemma().getValue());//+"#"+((Token)annot).getPos().getPosValue());
//					System.out.println("CT: "+annot.getCoveredText());
//					System.out.println("Lemma: "+((Token)annot).getLemma().getValue());
//					System.out.println("POS "+((Token)annot).getPos().getPosValue());

					rets.add(((Token)annot).getLemma().getValue()+"#"+posMatcher(((Token)annot).getPos().getPosValue()));
					//TODO POS-Mapping richtig umsetzen!!!!
				}
			}
			String[] a = new String[rets.size()];
			return rets.toArray(a);
			//ret;
		}
	}
	public static String[] createMultiwords(String[] string1)
	{
		String[] result = new String[string1.length-1];
		for(int i  = 0; i< string1.length-1;i++)
		{
			String temp = string1[i]+"_"+string1[i+1];
			result[i]= temp;
		}
		return result;

	}

}
