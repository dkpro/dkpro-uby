package de.tudarmstadt.ukp.integration.alignment.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SubSource {

	@XmlAttribute
	public String ref;
	
	@XmlElement(name = "subtarget")
	public List<SubTarget> subtargets = new ArrayList<SubTarget>();
}
