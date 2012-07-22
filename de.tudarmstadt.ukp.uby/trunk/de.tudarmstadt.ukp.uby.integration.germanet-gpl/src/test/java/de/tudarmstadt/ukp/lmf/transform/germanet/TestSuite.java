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
package de.tudarmstadt.ukp.lmf.transform.germanet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tuebingen.uni.sfs.germanet.api.GermaNet;

/**
 * Runs all tests for germanet-gpl module:
 * {@link GNConverterTest} and {@link SenseGeneratorTest}.
 * 
 * @author Zijad Maksuti
 * 
 * @since 0.2.0
 *
 */
@RunWith(Suite.class)
@SuiteClasses({SenseGeneratorTest.class, GNConverterTest.class})
public class TestSuite {
	
	/*
	 * GermaNet object
	 */
	static GermaNet gnet;
	
	/**
	 * Creates a {@link GermaNet} instance and initializes it.<br>
	 * 
	 * @since 0.2.0
	 */
	@BeforeClass
	public static void setUpClass() {
		String UBY_HOME = System.getenv("UBY_HOME");
		File gnetDir = new File(UBY_HOME+"/GermaNet/GN_V70/GN_V70/GN_V70_XML");
		
		try {
			gnet = new GermaNet(gnetDir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
