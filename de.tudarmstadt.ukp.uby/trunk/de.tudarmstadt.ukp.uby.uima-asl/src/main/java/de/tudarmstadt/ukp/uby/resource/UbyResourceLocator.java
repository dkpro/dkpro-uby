/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.uby.resource;

import java.util.Map;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResourceLocator;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.exceptions.UbyInvalidArgumentException;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Locate an UBY object.
 *
 * @author Judith Eckle-Kohler
 */
public class UbyResourceLocator extends Resource_ImplBase implements ExternalResourceLocator {
	public static final String PARAM_URL = "url";
	@ConfigurationParameter(name = PARAM_URL, mandatory = true)
	private String url;

	public static final String PARAM_DRIVER = "driver";
	@ConfigurationParameter(name = PARAM_DRIVER, mandatory = true)
	private String driver;
	
	/**
     * Hibernate dialect name. For convenience and backwards-compatibility with previous Uby
     * versions the short names {@code mysql} and {@code h2} are supported. Otherwise, this
     * must be a full Hibernate dialect class name.
     * 
     * @see DBConfig#setDb_vendor(String)
     * @see DBConfig#getDb_vendor()
     */
	public static final String PARAM_DIALECT = "dialect";
	@ConfigurationParameter(name = PARAM_DIALECT, mandatory = true)
	private String dialect;
	
	public static final String PARAM_USERNAME = "username";
	@ConfigurationParameter(name = PARAM_USERNAME, mandatory = true)
	private String username;
	
	public static final String PARAM_PASSWORD = "password";
	@ConfigurationParameter(name = PARAM_PASSWORD, mandatory = true)
	private String password;


	private Uby resource;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
			DBConfig dbConfig = new DBConfig(url,driver,dialect,username,password,false);

			resource = new Uby(dbConfig);

		} catch (UbyInvalidArgumentException e) {
			throw new ResourceInitializationException(e);
		}
		return true;
	}

	@Override
    public Uby getResource() {
		return resource;
	}
}
