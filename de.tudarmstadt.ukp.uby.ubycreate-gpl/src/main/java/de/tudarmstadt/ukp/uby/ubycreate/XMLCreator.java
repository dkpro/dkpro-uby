/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.uby.ubycreate;

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
