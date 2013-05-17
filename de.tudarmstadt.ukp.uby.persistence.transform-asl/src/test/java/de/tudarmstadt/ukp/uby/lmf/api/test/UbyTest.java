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
package de.tudarmstadt.ukp.uby.lmf.api.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;

import org.dom4j.DocumentException;
import org.junit.Test;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.exceptions.UbyInvalidArgumentException;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBUtils;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;


/**
 * @author Eckle-Kohler
 * Unit tests for UBY-API methods on in-memory test database
 *
 */
public class UbyTest
{
	


	private Uby connectToTestDB() throws FileNotFoundException, DocumentException, UbyInvalidArgumentException {
		String uby_user = "root";
		String uby_pass = "pass";

		DBConfig dbConfig = 
			new DBConfig("not_important","org.h2.Driver","h2",uby_user,uby_pass,true);
		LMFDBUtils.createTables(dbConfig);
		XMLToDBTransformer trans = new XMLToDBTransformer(dbConfig);
		File ubyTestXmlFile = new File("src/test/resources/UbyTestLexicon.xml");
		trans.transform(ubyTestXmlFile, "UbyTest");
		Uby uby = new Uby(dbConfig);
		return uby;
		
	}
	
	@Test
	public void testUbyApi() throws FileNotFoundException, DocumentException, UbyInvalidArgumentException {
		
		Uby uby = connectToTestDB();
		assertEquals(uby.getLexiconByName("FrameNet").getName(),"FrameNet");
		
	}
	
	
}
