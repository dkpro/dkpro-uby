package de.tudarmstadt.ukp.integration.alignment.xml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Resource {

	@XmlAttribute
	public String id;
	
	@XmlElement(name="description")
	public String description;
	@XmlElement(name="identifiertype")
	public String type;
	@XmlElement(name="language")
	public String language;
}
