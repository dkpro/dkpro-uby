package de.tudarmstadt.ukp.uby.ubycreate;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;
import de.tudarmstadt.ukp.lmf.transform.wordnet.WNConverter;

public class WordNetCreator
    implements Creator
{
    private static final String dtdPath = "ubyLmfDTD_1.0.dtd";
    private static final String dtdVersion = "1_0";

    /**
     * This method converts wordNet into ubyXML,stores it into a temporary file
     * and then migrates the data from the temporary file into DB
     *
     */
    @Override
    public void lexicon2DB(final DBConfig dbConfig, String source)
        throws IOException, XMLStreamException, SAXException,
        DocumentException, JWNLException
    {

        String lexicalName = "WordNet";

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
        throws IOException, XMLStreamException, SAXException,
        DocumentException, JWNLException
    {

        String lexicalResourceName = "WordNet_3.0_eng";

        /* Dumping lexical into a file */

        LexicalResource lexicalResource = null;

        File wnPath = new File(source);
        Dictionary extWordnet;
        extWordnet = Dictionary
                .getInstance(IOUtils
                        .toInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                + "<jwnl_properties language=\"en\">"
                                + "    <version publisher=\"Princeton\" number=\"3.0\" language=\"en\"/>"
                                + "    <dictionary class=\"net.sf.extjwnl.dictionary.FileBackedDictionary\">"
                                + "        <param name=\"morphological_processor\" value=\"net.sf.extjwnl.dictionary.morph.DefaultMorphologicalProcessor\">"
                                + "            <param name=\"operations\">"
                                + "                <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>"
                                + "                <param value=\"net.sf.extjwnl.dictionary.morph.DetachSuffixesOperation\">"
                                + "                    <param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"
                                + "                    <param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"
                                + "                    <param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"
                                + "                    <param name=\"operations\">"
                                + "                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>"
                                + "                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>"
                                + "                    </param>"
                                + "                </param>"
                                + "                <param value=\"net.sf.extjwnl.dictionary.morph.TokenizerOperation\">"
                                + "                    <param name=\"delimiters\">"
                                + "                        <param value=\" \"/>"
                                + "                        <param value=\"-\"/>"
                                + "                    </param>"
                                + "                    <param name=\"token_operations\">"
                                + "                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>"
                                + "                        <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>"
                                + "                        <param value=\"net.sf.extjwnl.dictionary.morph.DetachSuffixesOperation\">"
                                + "                            <param name=\"noun\" value=\"|s=|ses=s|xes=x|zes=z|ches=ch|shes=sh|men=man|ies=y|\"/>"
                                + "                            <param name=\"verb\" value=\"|s=|ies=y|es=e|es=|ed=e|ed=|ing=e|ing=|\"/>"
                                + "                            <param name=\"adjective\" value=\"|er=|est=|er=e|est=e|\"/>"
                                + "                            <param name=\"operations\">"
                                + "                                <param value=\"net.sf.extjwnl.dictionary.morph.LookupIndexWordOperation\"/>"
                                + "                                <param value=\"net.sf.extjwnl.dictionary.morph.LookupExceptionsOperation\"/>"
                                + "                            </param>"
                                + "                        </param>"
                                + "                    </param>"
                                + "                </param>"
                                + "            </param>"
                                + "        </param>"
                                + "        <param name=\"dictionary_element_factory\""
                                + "               value=\"net.sf.extjwnl.princeton.data.PrincetonWN17FileDictionaryElementFactory\"/>"
                                + "        <param name=\"file_manager\" value=\"net.sf.extjwnl.dictionary.file_manager.FileManagerImpl\">"
                                + "            <param name=\"file_type\" value=\"net.sf.extjwnl.princeton.file.PrincetonRandomAccessDictionaryFile\">"
                                + "                <!--<param name=\"write_princeton_header\" value=\"true\"/>-->"
                                + "                <!--<param name=\"encoding\" value=\"UTF-8\"/>-->"
                                + "            </param>"
                                + "            <!--<param name=\"cache_use_count\" value=\"true\"/>-->"
                                + "            <param name=\"dictionary_path\" value=\""
                                + wnPath.getAbsolutePath()
                                + "\"/>"
                                + "        </param>"
                                + "    </dictionary>"
                                + "    <resource class=\"net.sf.extjwnl.princeton.PrincetonResource\"/>"
                                + "</jwnl_properties>"));

        WNConverter converterWN = new WNConverter(wnPath, extWordnet,
                new LexicalResource(), lexicalResourceName, dtdVersion);
        converterWN.toLMF();
        lexicalResource = converterWN.getLexicalResource();

        LMFXmlWriter xmlWriter = new LMFXmlWriter(lmfXML.getAbsolutePath(),
                dtdPath);
        xmlWriter.writeElement(lexicalResource);
        xmlWriter.writeEndDocument();

        System.out.println("temp file saved: " + lmfXML.getAbsolutePath());

        return lmfXML;

    }
}
