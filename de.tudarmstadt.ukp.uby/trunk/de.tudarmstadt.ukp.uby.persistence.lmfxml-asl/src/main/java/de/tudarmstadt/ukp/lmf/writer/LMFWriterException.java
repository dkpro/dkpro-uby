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

package de.tudarmstadt.ukp.lmf.writer;

/**
 * This exception should be thrown by all {@link LMFWriter} instances and can be used
 * as a wrapper.
 * 
 * @author Yevgen Chebotar
 * @since UBY 0.1.0
 */
public class LMFWriterException extends Exception {

	private static final long serialVersionUID = 3656613135105045894L;

	public LMFWriterException(Throwable cause) {
        super(cause);
    }
	
	public LMFWriterException(){
		super();
	}
}
