package de.tudarmstadt.ukp.integration.alignment.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Target {

//	@XmlID
	@XmlAttribute
	public String ref;
	
	@XmlElement
	public Decision decision;
	
	@XmlElement(name = "score")
	public List<Score> scores = new ArrayList<>();

}
