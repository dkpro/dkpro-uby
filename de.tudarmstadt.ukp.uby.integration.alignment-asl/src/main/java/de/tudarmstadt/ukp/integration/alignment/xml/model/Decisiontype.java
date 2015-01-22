package de.tudarmstadt.ukp.integration.alignment.xml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Decisiontype {

	@XmlAttribute
	public String id;
	public String type;
	
	@XmlElement(name="name")
	public String name;
	
	@XmlElement(name="description")
	public String description;
}
