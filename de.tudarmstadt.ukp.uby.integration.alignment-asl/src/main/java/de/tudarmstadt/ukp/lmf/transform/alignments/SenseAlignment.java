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

package de.tudarmstadt.ukp.lmf.transform.alignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import de.tudarmstadt.ukp.lmf.transform.LMFXmlWriter;
import de.tudarmstadt.ukp.lmf.transform.XMLToDBTransformer;

abstract public class SenseAlignment
{
	protected static String LF = System.getProperty("line.separator");
	protected static String UBY_HOME=System.getenv("UBY_HOME");
	private String sourceUrl;
	private String destUrl;

	private String alignmentFileLocation;
	private File alignment;

	private final List<Sense> sourcesSenses;
	private final List<Sense> destinationsSenses;

	protected Uby ubySource, ubyDest;

	protected HashMap<String, MetaData> metaData;

	protected List<String> metaDataIds;
	protected List<Double> confidences;

	public SenseAlignment(String sourceUrl, String destUrl, String alignmentFile)
	{
		this.sourceUrl = sourceUrl;
		this.destUrl = destUrl;
		this.alignmentFileLocation = alignmentFile;
		sourcesSenses = new ArrayList<Sense>();
		destinationsSenses = new ArrayList<Sense>();
		metaData = new HashMap<String, MetaData>();
		confidences = new ArrayList<Double>();
		metaDataIds = new ArrayList<String>();
	}


	public SenseAlignment(String sourceUrl, String destUrl, String dbDriver, String dbVendor, String user, String pass, String UBY_HOME)
	{

		this.sourceUrl = sourceUrl;
		this.destUrl = destUrl;
		this.alignmentFileLocation = null;
		this.alignment = null;

		sourcesSenses = new ArrayList<Sense>();
		destinationsSenses = new ArrayList<Sense>();
		metaData = new HashMap<String, MetaData>();
		confidences = new ArrayList<Double>();
		metaDataIds = new ArrayList<String>();

		DBConfig dbConfigSource = new DBConfig(
				sourceUrl,dbDriver,dbVendor, user,
				pass, false);
		try {
			ubySource = new Uby(dbConfigSource);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if (!sourceUrl.equals(destUrl)){
			DBConfig dbConfigDest = new DBConfig(destUrl,dbDriver,dbVendor, user, pass, false);
			try {
				ubyDest=new Uby(dbConfigDest);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}else{
			ubyDest = ubySource;
		}
	}

	 public SenseAlignment(String sourceUrl, String destUrl,
			 String dbDriver, String dbVendor, String alignmentFile, String user, String pass, String UBY_HOME)
	 {

		this.sourceUrl = sourceUrl;
		this.destUrl = destUrl;
		this.alignmentFileLocation = alignmentFile;
		this.alignment = new File(alignmentFile);

		sourcesSenses = new ArrayList<Sense>();
		destinationsSenses = new ArrayList<Sense>();
		metaData = new HashMap<String, MetaData>();
		confidences = new ArrayList<Double>();
		metaDataIds = new ArrayList<String>();

		if (!alignment.exists() && !alignment.isFile()) {
			System.out.println("Alignment file: " + alignmentFile + " doesn't exist! ");
			System.exit(1);
		}

		DBConfig dbConfigSource = new DBConfig(sourceUrl,dbDriver,dbVendor, user, pass, false);
		try {
			ubySource = new Uby(dbConfigSource);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if (!sourceUrl.equals(destUrl)){
			DBConfig dbConfigDest = new DBConfig(destUrl,dbDriver,dbVendor, user,pass, false);
			try {
				ubyDest = new Uby(dbConfigDest);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}else{
			ubyDest = ubySource;
		}
	}

	public void setSourceUrl(String sourceUrl)
	{
		this.sourceUrl = sourceUrl;
	}

	public void setDestinationUrl(String destUrl)
	{
		this.destUrl = destUrl;
	}

	public void setAlignmentFileLocation(String alignmentFileLocation)
	{
		this.alignmentFileLocation = alignmentFileLocation;
		this.alignment = new File(alignmentFileLocation);
		if (!alignment.exists() && !alignment.isFile()) {
			System.out.println("Alignment file: " + alignmentFileLocation
					+ " doesn't exist! ");
			System.exit(1);
		}
	}

	public String getSourceUrl()
	{
		return sourceUrl;
	}

	public String getDestinationUrl()
	{
		return destUrl;
	}

	public String getAlignmentFileLocation()
	{
		return alignmentFileLocation;
	}

	public File getAlignmentFile(){
		return alignment;
	}

	public List<Sense> getSourceSenses(){
		return sourcesSenses;
	}

	public List<Sense> getDestSenses(){
		return destinationsSenses;
	}

	public void addSourceSense(Sense sense){
		sourcesSenses.add(sense);
	}

	public void addDestSense(Sense sense){
		destinationsSenses.add(sense);
	}

	/**
	 * depending on each alignment file format.
	 * @throws IllegalArgumentException
	 */
	abstract public void getAlignment() throws IllegalArgumentException;


	/**
	 * Adds MetaData id and confidence to the current alignment
	 * If one of attributes is not available set it to null.
	 * @param metaDataId
	 * @param confidence
	 */
	protected void addMetaData(String metaDataId, double confidence){
		metaDataIds.add(metaDataId);
		confidences.add(confidence);
	}

	/**
	 * Parses MetaData at the beginning of an alignment file.
	 * Requires blocks with '::MetaData'- and '::Alignments'-headers in the alignment file.
	 * If '::MetaData'-block is not found, returns BufferedReader pointing at the beginning of the file
	 * @return BufferedReader pointing at the start of '::Alignments'-block
	 * @throws IOException
	 */
	protected BufferedReader parseMetaData() throws IOException{

		BufferedReader reader = new BufferedReader(
				new FileReader(getAlignmentFileLocation()));

		String line = reader.readLine();
		if(line == null || !line.equals("::MetaData")){
			reader.close();
			return new BufferedReader(
					new FileReader(getAlignmentFileLocation()));
		}

		while ((line = reader.readLine()) != null) {
			if(line.equals("::Alignments")) {
                break;
            }

			String[] lineParts = line.split("#");
			MetaData meta = new MetaData();
			if(lineParts.length == 6){
				String id = lineParts[0];
				meta.setId(id);
				meta.setCreationDate(lineParts[1].equals("null")?null:new Date(Long.parseLong(lineParts[1])));
				meta.setCreationTool(lineParts[2].equals("null")?null:lineParts[2]);
				meta.setVersion(lineParts[3].equals("null")?null:lineParts[3]);
				meta.setAutomatic(lineParts[4].equals("null")?null:Boolean.parseBoolean(lineParts[4]));
				meta.setCreationProcess(lineParts[5].equals("null")?null:lineParts[5]);
				metaData.put(id, meta);
			}else{
				System.err.println("Can't parse MetaData: "+line);
			}
		}
		return reader;
	}
	/**
	 * convert the alignment to LMF format
	 * @param idPrefix
	 * @param crosslingual
	 * @param dtdVersion
	 * @param UBY_HOME
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SAXException
	 */
	public void toLMF(String idPrefix, boolean crosslingual, String dtdVersion,String UBY_HOME) throws IOException, TransformerException, SAXException
	{
		toLMF(idPrefix,crosslingual,true,dtdVersion,UBY_HOME);
	}

	/**
	 *
	 * @param idPrefix
	 * @param crosslingual
	 * @param usingSynsetAttribute
	 * @param dtdVersion
	 * @param UBY_HOME
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	public void toLMF(String idPrefix, boolean crosslingual, boolean usingSynsetAttribute,String dtdVersion,String UBY_HOME) throws IOException, TransformerException, SAXException
	{
		LMFXmlWriter xmlWriter = new LMFXmlWriter(UBY_HOME+"/target/"+idPrefix+".xml", UBY_HOME+"/resources/dtd/DTD_unifiedModel_"+dtdVersion+".dtd");

		LexicalResource lexicalResource = new LexicalResource();
		List<SenseAxis> senseAxes=new ArrayList<SenseAxis>();

		boolean metaDataNotEmpty = metaData.size() > 0;
		if(metaDataNotEmpty){
			List<MetaData> metaDataList = new ArrayList<MetaData>(metaData.size());
			int i = 0;
			for(String metaDataId : metaData.keySet()){
				MetaData meta = metaData.get(metaDataId);
				meta.setId(idPrefix+"_Meta_"+i);
				metaDataList.add(meta);
				i++;
			}
			lexicalResource.setMetaData(metaDataList);
		}
		for (int i = 0; i < sourcesSenses.size(); i++) {
			SenseAxis senseAxis = new SenseAxis();
			//Set type of alignment
			if(crosslingual) {
				senseAxis.setSenseAxisType(ESenseAxisType.crosslingualSenseAlignment);
			} else {
				senseAxis.setSenseAxisType(ESenseAxisType.monolingualSenseAlignment);
			}
			senseAxis.setSenseOne(sourcesSenses.get(i));
			senseAxis.setSenseTwo(destinationsSenses.get(i));
			if (usingSynsetAttribute){
				senseAxis.setSynsetOne(sourcesSenses.get(i).getSynset());
				senseAxis.setSynsetTwo(destinationsSenses.get(i).getSynset());
			}
			senseAxis.setId(idPrefix+"_"+i);


			if(metaDataNotEmpty){
				String metaDataId = metaDataIds.get(i);
				if(metaDataId != null){
					MetaData meta = metaData.get(metaDataId);
					senseAxis.setMetaData(meta);
				}
				senseAxis.setConfidence(confidences.get(i));
			}

			senseAxes.add(senseAxis);
			System.out.println(senseAxes.size());
			//save them to database
			//xmlWriter.writeElement(senseAxis);
//			uby.getSession().save(senseAxis);
//			Transaction   transaction = uby.getSession().beginTransaction();
//			transaction.commit();
		}
		lexicalResource.setSenseAxes(senseAxes);
		lexicalResource.setDtdVersion(dtdVersion);
		lexicalResource.setName("Uby_Alignments_"+idPrefix);
		GlobalInformation globalInformation=new GlobalInformation();
		globalInformation.setLabel("Alignments_"+idPrefix);
		lexicalResource.setGlobalInformation(globalInformation);
		xmlWriter.writeElement(lexicalResource);
		xmlWriter.writeEndDocument();

	}

	public static void toDB(DBConfig dbConfig, File xmlSource, String idPrefix) throws DocumentException, FileNotFoundException, IllegalArgumentException{
		XMLToDBTransformer xml2DB = new XMLToDBTransformer(dbConfig);
		xml2DB.transform(xmlSource,"Uby_Alignments_"+idPrefix);
	}
}
