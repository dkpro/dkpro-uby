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
package de.tudarmstadt.ukp.test.resources;

import java.io.File;
import java.io.FileNotFoundException;

import org.dom4j.DocumentException;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.hibernate.UBYH2Dialect;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBUtils;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;

/**
 * @author Eckle-Kohler
 * Provides an in-memory UBY test DB for testing.
 *
 */
public class UbyTestDbProvider {

	private Uby uby;
	public UbyTestDbProvider() throws FileNotFoundException, DocumentException, IllegalArgumentException {
			
		this.uby = createDB();

	}

	public Uby getUby() {
		return this.uby;
	}
	
	private Uby createDB() throws DocumentException, IllegalArgumentException, FileNotFoundException {
		String uby_user = "root";
		String uby_pass = "pass";

		DBConfig dbConfig = 
//			new DBConfig("not_important","org.h2.Driver","h2",uby_user,uby_pass,true);
			new DBConfig("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1","org.h2.Driver",UBYH2Dialect.class.getName(),uby_user,uby_pass,true);
		
			LMFDBUtils.createTables(dbConfig);
			
			XMLToDBTransformer trans = new XMLToDBTransformer(dbConfig);
			
			trans.transform(new File("src/main/resources/UbyTestLexicon.xml"),"UbyTest");
			
			Uby uby = new Uby(dbConfig);	

		return uby;
	}



}
