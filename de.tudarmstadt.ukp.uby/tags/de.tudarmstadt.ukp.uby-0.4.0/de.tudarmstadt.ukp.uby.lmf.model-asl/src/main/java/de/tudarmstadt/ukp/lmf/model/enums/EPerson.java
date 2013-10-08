/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.lmf.model.enums;

/**
 * Enumeration of different person properties.<br>
 * Person property is the class of properties with a deictic dimension,
 * interpreted relative to the speaker, encoding the participants in a speech situation.
 * Usually a three-way contrast is found: first person (speaker), second person (addressee), and third person (neither speaker nor addressee).
 * @author Zijad Maksuti
 *
 */
public enum EPerson {
	first,
	second,
	third
}
