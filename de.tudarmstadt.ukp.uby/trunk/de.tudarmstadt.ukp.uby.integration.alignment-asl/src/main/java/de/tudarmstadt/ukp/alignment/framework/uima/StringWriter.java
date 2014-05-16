package de.tudarmstadt.ukp.alignment.framework.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.CasConsumer_ImplBase;

import de.tudarmstadt.ukp.alignment.framework.uima.Toolkit.PosGetter;



public class StringWriter extends CasConsumer_ImplBase {

	public static Object mContent;
	public static PosGetter getter;

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
			mContent = getter.retrieveData(jcas);
		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
