package de.tudarmstadt.ukp.integration.alignment.xml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Scoretype {

	@XmlAttribute
	public String id;
	@XmlAttribute
	public String min;
	@XmlAttribute
	public String max;
	@XmlAttribute
	public String step;
	@XmlAttribute
	public String type;
	
	@XmlElement(name="name")
	public String name;
	
	@XmlElement(name="description")
	public String description;
}
