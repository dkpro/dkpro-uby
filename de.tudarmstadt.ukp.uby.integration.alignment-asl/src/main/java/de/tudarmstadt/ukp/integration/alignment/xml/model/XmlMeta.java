/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
	public ResourceXml sourceResource;
	
	@XmlElement(name="targetresource")
	public ResourceXml targetResource;

	@XmlElement(name="subsource")
	public ResourceXml subSource;
	
	@XmlElement(name="subtarget")
	public ResourceXml subTarget;
	
	@XmlElementWrapper(name="scoretypes")
	@XmlElement(name="scoretype")
	public List<Scoretype> scoretypes = new ArrayList<Scoretype>();
	
	@XmlElementWrapper(name="decisiontypes")
	@XmlElement(name="decisiontype")
	public List<Decisiontype> decisiontypes = new ArrayList<Decisiontype>();
	
	
	
}
