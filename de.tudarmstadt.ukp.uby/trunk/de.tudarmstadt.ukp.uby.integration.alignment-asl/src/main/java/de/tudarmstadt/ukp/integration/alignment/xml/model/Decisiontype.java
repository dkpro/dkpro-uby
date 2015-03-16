/*******************************************************************************
 * Copyright 2015
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

public class Decisiontype {

	@XmlType(name="")
	@XmlEnum
	public enum Decision {
		AUTOMATIC, 
		MANUAL;
		
		public String value() {
			return name();
		}
		
		public static Decisiontype.Decision fromValue(String v){
			return valueOf(v);
		}
	};
	
	@XmlAttribute
	public String id;
	public Decision type; //automatic or manual
	
	@XmlElement(name="name")
	public String name;
	
	@XmlElement(name="description")
	public String description;
}
