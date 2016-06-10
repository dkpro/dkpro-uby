/**
 * Copyright 2016
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
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;
import de.tudarmstadt.ukp.lmf.transform.germanet.GNConverter;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;

public class GermaNetCreator
    implements Creator
{

    private static final String dtdPath = "ubyLmfDTD_1.0.dtd";
    private static final String dtdVersion = "1_0";

    /**
     * This method converts germanet into ubyXML,stores it into a temporary file and then migrates
     * the data from the temporary file into DB
     *
     */
    @Override
    public void lexicon2DB(DBConfig dbConfig, String source)
        throws IOException, XMLStreamException, SAXException, DocumentException
    {

        String lexicalName = "GermaNet";

        File lmfXML = File.createTempFile("tempfile", ".tmp");
        lmfXML = lexicon2XML(source, lmfXML);

        /* Persisting lmfXML into DB */

        XMLToDBTransformer xmlToDB = new XMLToDBTransformer(dbConfig);
        xmlToDB.transform(lmfXML, lexicalName);
        System.out.println("DB Operation Done");
        lmfXML.deleteOnExit();

    }

    @Override
    public File lexicon2XML(String source, File lmfXML)
        throws IOException, XMLStreamException, SAXException, DocumentException
    {
        String lexicalResourceName = "GermaNet_8.0_deu";

        /* Dumping lexical into a file */

        LexicalResource lexicalResource = null;

        GermaNet gnet = new GermaNet(new File(source));
        GNConverter converterGN = new GNConverter(gnet, new LexicalResource(), null, lexicalResourceName,
                dtdVersion);
        converterGN.toLMF();
        lexicalResource = converterGN.getLexicalResource();

        LMFXmlWriter xmlWriter = new LMFXmlWriter(lmfXML.getAbsolutePath(), dtdPath);
        xmlWriter.writeElement(lexicalResource);
        xmlWriter.writeEndDocument();

        return lmfXML;
    }
}
