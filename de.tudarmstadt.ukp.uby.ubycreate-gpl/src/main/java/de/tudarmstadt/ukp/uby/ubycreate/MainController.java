package org.dkpro.uby.creation.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;

import net.sf.extjwnl.JWNLException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.DocumentException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFDBUtils;

public class MainController
{

    @Option(name = "--target", metaVar = "<target>", usage = "Target: <database type>:<username>:<password>@<host>:<port>/<database name> or xml:<UBYFileName>")
    private String target;


    @Option(name = "--source", metaVar = "<source>", usage = "Source: <lexicon type>:<source folder or file>")
    private String source;


    @Option(name = "-v", metaVar = "<ExceptionTrace>", usage = "Exception Trace: true(for printing the root cause message of the exception)")
    private String exceptionTrace;


    public static void main(String[] args)
        throws XMLStreamException, SAXException, DocumentException
    {
        new MainController().run(args);
    }


    /**
     * This method dumps Germanet, Framenet, WordNet and XML into DB by instantiating the
     * appropriate classes and calling their methods
     */
    private void run(String args[])
        throws XMLStreamException, SAXException, DocumentException
    {

        CmdLineParser parser = new CmdLineParser(this);

        try {

            Map<String, Class<? extends Creator>> creatorMap = createMap();

            parser.parseArgument(args);

            /* Breaking the parameter value into lexicon and source path */
            StringTokenizer tokSource = new StringTokenizer(source, ":");

            String lexicon = tokSource.nextToken();

            Creator creatorObj = creatorMap.get(lexicon).newInstance();

            source = "";

            while (tokSource.hasMoreTokens()) {
                source = source + tokSource.nextToken();
                if (tokSource.hasMoreElements()) {
                    source = source + ":";
                }
            }

            StringTokenizer tokTarget = new StringTokenizer(target, ":");
            String targetType = tokTarget.nextToken();

            /* If block is run when target is uby xml */
            if (targetType.equals("xml")) {

                String ubyXMLName = "";

                while (tokTarget.hasMoreTokens()) {
                    ubyXMLName = ubyXMLName + tokTarget.nextToken();
                    if (tokTarget.hasMoreElements()) {
                        ubyXMLName = ubyXMLName + ":";
                    }
                }

                File lmfXML = new File(ubyXMLName);

                creatorObj.lexicon2XML(source, lmfXML);

            }
            else {

                DBConfig dbConfig = makeDBConfig(target);

                if (dbConfig == null) {
                    return;
                }

                /*
                 * the following has been commented as it throws exception when there is no table in
                 * DB schema to drop
                 */
                // LMFDBUtils.dropTables(dbConfig);

                LMFDBUtils.createTables(dbConfig);

                creatorObj.lexicon2DB(dbConfig, source);

            }
        }
        catch (JWNLException e) {

            printHelpMessage(parser, e);

        }
        catch (IOException e) {

            printHelpMessage(parser, e);

        }
        catch (CmdLineException e) {

            printHelpMessage(parser, e);

        }
        catch (NullPointerException e) {

            printHelpMessage(parser, e);

        }
        catch (ClassNotFoundException e) {

            printHelpMessage(parser, e);

        }
        catch (NoSuchElementException e) {

            printHelpMessage(parser, e);

        }
        catch (IllegalAccessException e) {

            printHelpMessage(parser, e);

        }
        catch (InstantiationException e) {

            printHelpMessage(parser, e);

        }
        catch (Exception e) {

            printHelpMessage(parser, e);

        }

    }

    /**
     * This method prints the help message for command line arguments
     */
    private void printHelpMessage(CmdLineParser parser, Exception e)
    {

        if (exceptionTrace != null && exceptionTrace.equals("true")) {
            System.out.println("\n" + ExceptionUtils.getFullStackTrace(e) + "\n");

        }
        else {
            System.out.println("\n" + ExceptionUtils.getRootCauseMessage(e) + "\n");

        }

        parser.printUsage(System.out);

        System.out
                .println("\n"
                        + "See the following examples "
                        + "\n"
                        + "1. java -jar ubycreate.jar --target mysql:root:pass@localhost:3306/uby_gn --source germanet:GN_V70/GN_V70_XML"
                        + "\n\n"
                        + "2. java -jar ubycreate.jar --target h2:root:pass@file:ubyGermanet --source germanet:GN_V70/GN_V70_XML "
                        + "\n\n"
                        + "3. java -jar ubycreate.jar --target h2:root:pass@file:ubyGermanet --source xml:GN_V70/GN_V70_XML/uby_gn.xml"
                        + "\n" + "\n\n" + "Note: " + "\n"
                        + "1. Only MySQL and H2 database are supported " + "\n"
                        + "2. Lexicon type: germanet, xml, framenet, wordnet");
    }

    /**
     * This method parses the DB URL provided by the User,breaks it into tokens and creates DBConfig
     * object out of it
     */
    private DBConfig makeDBConfig(final String databaseURL)
        throws ClassNotFoundException, NoSuchElementException
    {

        StringTokenizer tok = new StringTokenizer(databaseURL, ":@ ");

        String dbType = tok.nextToken();
        String dbUser = tok.nextToken();
        String dbPassword = tok.nextToken();
        String dbURL = tok.nextToken() + ":" + tok.nextToken();

        String dbDriver = "";
        String dbVendor = "";
        boolean showSQL = false;

        /* Values of database driver and database vendor are set based on the type of DB */

        if (dbType.equals("mysql")) {
            dbDriver = "com.mysql.jdbc.Driver";
            dbVendor = "mysql";
        }
        else if (dbType.equals("h2")) {
            dbDriver = "org.h2.Driver";
            dbVendor = "h2";
        }

        // Prepare the database for all resources.
        Class.forName(dbDriver);

        return new DBConfig(dbURL, dbDriver, dbVendor, dbUser, dbPassword, showSQL);
    }

    private Map<String, Class<? extends Creator>> createMap()
    {
        Map<String, Class<? extends Creator>> creatorMap = new HashMap<String, Class<? extends Creator>>();
        creatorMap.put("germanet", GermaNetCreator.class);
        creatorMap.put("framenet", FrameNetCreator.class);
        creatorMap.put("wordnet", WordNetCreator.class);
        creatorMap.put("xml", XMLCreator.class);

        return creatorMap;
    }
}
