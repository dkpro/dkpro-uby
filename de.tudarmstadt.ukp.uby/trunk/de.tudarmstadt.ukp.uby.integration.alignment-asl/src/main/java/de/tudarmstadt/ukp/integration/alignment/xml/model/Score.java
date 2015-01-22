package de.tudarmstadt.ukp.integration.alignment.xml.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Score {

	@XmlAttribute
	public String src;
	
	@XmlAttribute
	public double value;
	
}
