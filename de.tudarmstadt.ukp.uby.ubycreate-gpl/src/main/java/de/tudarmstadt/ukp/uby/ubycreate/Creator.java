package org.dkpro.uby.creation.tools;

import java.io.File;

import de.tudarmstadt.ukp.lmf.transform.DBConfig;

public interface Creator
{
    public File lexicon2XML(String source, File lmfXML)
        throws Exception;

    public void lexicon2DB(DBConfig dbConfig, String source)
        throws Exception;
}
