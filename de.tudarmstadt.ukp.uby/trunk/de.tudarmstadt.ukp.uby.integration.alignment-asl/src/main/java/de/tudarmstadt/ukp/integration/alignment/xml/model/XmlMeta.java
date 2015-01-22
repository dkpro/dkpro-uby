package de.tudarmstadt.ukp.integration.alignment.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class XmlMeta {
//	@XmlElement(name="dc:title", namespace="http://purl.org/dc/elements/1.1/title")
//	public String title;
	
//	@XmlElement(name="dc:description",namespace="http://purl.org/dc/elements/1.1/description")
//	public String description;

	@XmlElement(name="title")
	public String title;
//	
	@XmlElement(name="description")
	public String description;
	
	@XmlElement(name="identifier")
	public String identifier;
	
	@XmlElement(name="date")
	public String date;
	
	@XmlElement(name="version")
	public String version;	
	
	@XmlElement(name="creator")
	public String creator;
	
	@XmlElement(name="publisher")
	public String publisher;
	
	@XmlElement(name="rights")
	public String rights;
	
	@XmlElement(name="sourceresource")
	public Resource sourceResource;
	
	@XmlElement(name="targetresource")
	public Resource targetResource;
	
	@XmlElementWrapper(name="scoretypes")
	@XmlElement(name="scoretype")
	public List<Scoretype> scoretypes = new ArrayList<Scoretype>();
	
	@XmlElementWrapper(name="decisiontypes")
	@XmlElement(name="decisiontype")
	public List<Decisiontype> decisiontypes = new ArrayList<Decisiontype>();
	
	
	
}
