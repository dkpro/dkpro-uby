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
 * Enumeration of different core types.<p>
 * <i>Used in FrameNet to classify semantic arguments (i.e. frame elements in FrameNet)
 * in terms of how central they are to a particular predicate (i.e. frame in FrameNet).</i>
 * @author Zijad Maksuti
 *
 */
public enum ECoreType {
	core,
	peripheral,
	coreUnexpressed,
	extraThematic
}
