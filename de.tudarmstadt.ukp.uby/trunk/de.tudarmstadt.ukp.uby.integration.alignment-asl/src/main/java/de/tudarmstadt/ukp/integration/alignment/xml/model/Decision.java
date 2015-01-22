package de.tudarmstadt.ukp.integration.alignment.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Decision {

	
	@XmlAttribute
	public String src;
	
	@XmlAttribute
	public String value;
	
	@XmlAttribute
	public double confidence;
	
}

