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
package de.tudarmstadt.ukp.uby.uima.annotator.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.dom4j.DocumentException;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceObjectProviderBase;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBUtils;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;
import de.tudarmstadt.ukp.uby.resource.UbyResource;


/**
 * @author Judith Eckle-Kohler
 *
 */
public class UbyResourceTest
{

	@Test
	public void testUbyResourceOnInMemDb()
		throws Exception
	{
	    
        runAnnotatorTestOnInMemDb("en");
         
        // botnet: Wiktionary domain=question (just for the test case) -> question: WordNet semantic field = communication
         runAnnotatorTestOnInMemDb("en");
               
	}

	@Ignore
	@Test
	public void testUbyResourceOnMySqlDb()
		throws Exception
	{
                
        runAnnotatorTestOnMySqlDb("en");

	}

	/**
	 * This is the test case that uses an embedded DB
	 * use of in-memory DB is commented out
	 *
	 */	 
    private void runAnnotatorTestOnInMemDb(String language)
        throws UIMAException, FileNotFoundException, DocumentException, IllegalArgumentException
    {
	 	DBConfig dbConfig = new DBConfig("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1","org.h2.Driver","h2","root","pass",false);
		
		LMFDBUtils.createTables(dbConfig);
		
		XMLToDBTransformer transformer;
		transformer = new XMLToDBTransformer(dbConfig);
		transformer.transform(new File("src/test/resources/UbyTestLexicon.xml"),"UbyTest");
		
		 
		AnalysisEngineDescription processor = createEngineDescription(

				createEngineDescription(
		                TestAnnotator.class,
		                TestAnnotator.RES_UBY,
						createExternalResourceDescription(UbyResource.class,
		                	UbyResource.PARAM_MODEL_LOCATION, ResourceObjectProviderBase.NOT_REQUIRED,								
							UbyResource.PARAM_URL, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
							UbyResource.PARAM_DRIVER, "org.h2.Driver",
							UbyResource.PARAM_DIALECT, "h2",
							UbyResource.PARAM_USERNAME, "root",
							UbyResource.PARAM_PASSWORD, "pass"
									))
		);

		AnalysisEngine engine = createEngine(processor);
		JCas aJCas = engine.newJCas();
		aJCas.setDocumentLanguage(language);

		engine.process(aJCas);

	
	}	

    private void runAnnotatorTestOnMySqlDb(String language)
        throws UIMAException
    {
        AnalysisEngineDescription processor = createEngineDescription(

         createEngineDescription(
                TestAnnotator.class,
                TestAnnotator.RES_UBY,
                createExternalResourceDescription(UbyResource.class,
                		UbyResource.PARAM_MODEL_LOCATION, ResourceObjectProviderBase.NOT_REQUIRED,
                		UbyResource.PARAM_URL, "localhost/uby_open_0_3_0",
                		UbyResource.PARAM_DRIVER, "com.mysql.jdbc.Driver",
                        UbyResource.PARAM_DIALECT, "mysql",
                        UbyResource.PARAM_USERNAME, "root",
                        UbyResource.PARAM_PASSWORD, "pass")));

		AnalysisEngine engine = createEngine(processor);
		JCas aJCas = engine.newJCas();
		aJCas.setDocumentLanguage(language);

		engine.process(aJCas);

	}
    
    public static class TestAnnotator
        extends JCasAnnotator_ImplBase
{

    public static final String RES_UBY = "uby";
    @ExternalResource(key = RES_UBY)
    private Uby uby;
    

    @Override
    public void process(JCas arg0) throws AnalysisEngineProcessException {
    	List<String> lexiconNames = uby.getLexiconNames();
		List<Lexicon> lexicons = uby.getLexicons();
		assertEquals(lexiconNames.size(), lexicons.size());
    }
                
}

     
    
}
