package org.dkpro.uby.creation.tools;

import java.io.File;

import org.dom4j.DocumentException;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;

public class XMLCreator
    implements Creator
{

    /**
     * This method store the data from the XML file in UBY format into DB
     *
     */
    @Override
    public void lexicon2DB(DBConfig dbConfig, String source)
        throws DocumentException
    {
        String lexicalName = "xml";
        System.out.println("xml2db: " + source + " -> " + dbConfig.getJdbc_url());

        File lmfXML = new File(source);

        /* Persisting lmfXML into DB */

        XMLToDBTransformer xmlToDB = new XMLToDBTransformer(dbConfig);
        xmlToDB.transform(lmfXML, lexicalName);
    }

    @Override
    public File lexicon2XML(String source, File lmfXML)
        throws Exception
    {
        return lmfXML;
    }
}
