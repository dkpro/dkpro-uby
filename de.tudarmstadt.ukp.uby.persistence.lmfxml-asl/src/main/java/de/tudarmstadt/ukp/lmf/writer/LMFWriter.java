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

package de.tudarmstadt.ukp.lmf.writer;

/** @deprecated THIS CLASS WILL BE REMOVED SOON. USE MODULE persistence.transform INSTEAD! */
@Deprecated
public abstract class LMFWriter {
	public abstract void writeElement(Object lmfObject) throws LMFWriterException;
	public abstract void writeStartElement(Object lmfObject) throws LMFWriterException;	
	public abstract void writeEndElement(Object lmfObject) throws LMFWriterException;
	public abstract void writeEndDocument() throws LMFWriterException;
}
