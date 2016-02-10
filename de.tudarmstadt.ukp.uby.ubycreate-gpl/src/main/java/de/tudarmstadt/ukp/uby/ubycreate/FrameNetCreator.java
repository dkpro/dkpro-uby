package de.tudarmstadt.ukp.uby.ubycreate;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.saar.coli.salsa.reiter.framenet.FNDatabaseReader;
import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.saar.coli.salsa.reiter.framenet.FrameNetVersion;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;
import de.tudarmstadt.ukp.lmf.transform.framenet.FNConverter;

public class FrameNetCreator
    implements Creator
{

    private static final String dtdPath = "ubyLmfDTD_1.0.dtd";
    private static final String dtdVersion = "1_0";

    /**
     * This method converts framenet into ubyXML,stores it into a temporary file and then migrates
     * the data from the temporary file into DB
     */
    @Override
    public void lexicon2DB(final DBConfig dbConfig, String source)
        throws IOException, XMLStreamException, SAXException, DocumentException
    {

        String lexicalName = "FrameNet";

        File lmfXML = File.createTempFile("tempfile", ".tmp");
        lmfXML = lexicon2XML(source, lmfXML);

        /* Persisting lmfXML into DB */
        XMLToDBTransformer xmlToDB = new XMLToDBTransformer(dbConfig);
        xmlToDB.transform(lmfXML, lexicalName);

        System.out.println("DB Operation DONE");

        lmfXML.deleteOnExit();

    }

    @Override
    public File lexicon2XML(String source, File lmfXML)
        throws IOException, XMLStreamException, SAXException, DocumentException
    {

        String lexicalResourceName = "FrameNet_1.5_eng";

        /* Dumping lexical into a file */

        LexicalResource lexicalResource = null;

        FrameNet fn = new FrameNet();
        FNDatabaseReader reader = FNDatabaseReader.createInstance(new File(source),
                FrameNetVersion.V15);
        fn.readData(reader);

        FNConverter converterFN = new FNConverter(fn, new LexicalResource(), lexicalResourceName,
                dtdVersion);
        converterFN.toLMF();
        lexicalResource = converterFN.getLexicalResource();

        LMFXmlWriter xmlWriter = new LMFXmlWriter(lmfXML.getAbsolutePath(), dtdPath);
        xmlWriter.writeElement(lexicalResource);
        xmlWriter.writeEndDocument();

        System.out.println("temp file saved: " + lmfXML.getAbsolutePath());

        return lmfXML;

    }
}
