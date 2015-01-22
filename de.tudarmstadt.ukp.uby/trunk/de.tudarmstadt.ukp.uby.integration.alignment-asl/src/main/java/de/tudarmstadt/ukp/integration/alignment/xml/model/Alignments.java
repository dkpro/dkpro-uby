package de.tudarmstadt.ukp.integration.alignment.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Alignments {

	@XmlElement(name = "source")
	public List<Source> source = new ArrayList<Source>();

}
