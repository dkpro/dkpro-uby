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
package de.tudarmstadt.ukp.uby.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResourceLocator;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CompressionUtils;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;

/**
 * Locate an UBY object.
 *
 * @author Judith Eckle-Kohler
 * @author Richard Eckart de Castilho
 */
public class UbyResource extends Resource_ImplBase implements ExternalResourceLocator {
    private static final String DATABASE = "database";
    private static final String DATABASE_FILE = DATABASE+".h2.db";
    private static final String UBY_PASSWORD = "uby.password";
    private static final String UBY_USERNAME = "uby.username";
    private static final String UBY_DIALECT = "uby.dialect";
    private static final String UBY_DRIVER = "uby.driver";
    private static final String UBY_URL = "uby.url";
    
    /**
     * When using an embedded database, do not set this parameter.
     */
	public static final String PARAM_URL = "url";
	@ConfigurationParameter(name = PARAM_URL, mandatory = false)
	private String url;

	public static final String PARAM_DRIVER = "driver";
	@ConfigurationParameter(name = PARAM_DRIVER, mandatory = false)
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
    @ConfigurationParameter(name = PARAM_DIALECT, mandatory = false)
    private String dialect;
    
	public static final String PARAM_USERNAME = "username";
	@ConfigurationParameter(name = PARAM_USERNAME, mandatory = false)
	private String username;
	
	public static final String PARAM_PASSWORD = "password";
	@ConfigurationParameter(name = PARAM_PASSWORD, mandatory = false)
	private String password;

    /**
     * Load the model from this location instead of locating the model automatically. If
     *  you are NOT using an embedded database, you need to set this parameter to
     * {@link ResourceObjectProviderBase#NOT_REQUIRED} 
     * like this: UbyResource.PARAM_MODEL_LOCATION, ResourceObjectProviderBase.NOT_REQUIRED
     * (i.e. in that case it is mandatory to set this parameter)
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;
	
    /**
     * Use this language instead of the document language to resolve the model.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    /**
     * Override the default variant used to locate the model.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;
    
    private CasConfigurableProviderBase<Uby> modelProvider;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		
        modelProvider = new ModelProviderBase<Uby>() {
            {
                setContextObject(UbyResource.this);
                setDefault(GROUP_ID, "de.tudarmstadt.ukp.uby");

                setDefault(ARTIFACT_ID, "${groupId}-model-data-${language}-${variant}");
                setDefault(LOCATION, "classpath:/de/tudarmstadt/ukp/uby/data/lib/data-${language}-${variant}.properties");
                setDefault(VARIANT, "light");

                setOverride(LOCATION, modelLocation);
                setOverride(LANGUAGE, language);
                setOverride(VARIANT, variant);
            }

            @Override
            protected Uby produceResource(URL aUrl)
                throws IOException
            {
                Properties props = getAggregatedProperties();
                
                Properties meta = new Properties(getResourceMetaData());
                addOverride(meta, UBY_URL, url);
                addOverride(meta, UBY_DRIVER, driver);
                addOverride(meta, UBY_DIALECT, dialect);
                addOverride(meta, UBY_USERNAME, username);
                addOverride(meta, UBY_PASSWORD, password);

                // If an embedded database is to be used, extract database to disk
                if (aUrl != null) {
                    UbyResource.this.getLogger().info("Using embedded database");

                    File tmpFolder = File.createTempFile("uby", ".db");
                    FileUtils.forceDelete(tmpFolder);
                    FileUtils.forceMkdir(tmpFolder);
                    tmpFolder.deleteOnExit();

                    File tmpDbFile = new File(tmpFolder, DATABASE_FILE);
                    tmpDbFile.deleteOnExit();

                    UbyResource.this.getLogger().info(
                            "Extracting embedded database to [" + tmpDbFile + "]");
                    
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        // FIXME should probably just do nothing if database file is not compressed
                        // and if the URL already points to the file system.
                        is = CompressionUtils.getInputStream(aUrl.toString(), aUrl.openStream());
                        
                        os = new FileOutputStream(tmpDbFile);
                        IOUtils.copyLarge(is, os);
                    }
                    finally {
                        IOUtils.closeQuietly(os);
                        IOUtils.closeQuietly(is);
                    }
                    
                    // Well... we currently only support H2 as embedded DB. If somebody wants to
                    // use a different embedded DB, we'll have to implement something more
                    // generic here.
                    meta.setProperty(UBY_URL, "jdbc:h2:" + tmpFolder.toURI().toURL().toString()
                            + "/" + DATABASE + ";TRACE_LEVEL_FILE=0");
                }
                else {
                    getLogger().info("Connecting to server...");
                }
                
                getLogger().info("uby.url: " + meta.getProperty(UBY_URL));
                getLogger().info("uby.driver: " + meta.getProperty(UBY_DRIVER));
                getLogger().info("uby.dialect: " + meta.getProperty(UBY_DIALECT));
                getLogger().info("uby.username: " + meta.getProperty(UBY_USERNAME));
                getLogger().info("uby.password: " + (StringUtils.isNotEmpty(
                        meta.getProperty(UBY_PASSWORD)) ? "<set>" : "<unset>"));

                DBConfig dbConfig = new DBConfig(meta.getProperty(UBY_URL),
                        meta.getProperty(UBY_DRIVER), meta.getProperty(UBY_DIALECT),
                        meta.getProperty(UBY_USERNAME), meta.getProperty(UBY_PASSWORD), false);

                try {
                    return new Uby(dbConfig);
                }
                catch (IllegalArgumentException e) {
                    throw new IOException(e);
                }                
            }
        };		
        
		return true;
	}

	@Override
    public Uby getResource() {
	    try {
    	    modelProvider.configure();
    		return modelProvider.getResource();
	    }
	    catch (IOException e) {
	        throw new IllegalStateException(e);
	    }
	}
	
	private static void addOverride(Properties aProps, String aKey, String aValue)
	{
	    if (aValue != null) {
	        aProps.setProperty(aKey, aValue);
	    }
	}
}
