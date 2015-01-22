package de.tudarmstadt.ukp.integration.alignment.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Source {

	@XmlAttribute
	public String ref;
	
	@XmlElement(name = "target")
	public List<Target> targets = new ArrayList<Target>();

	
}
