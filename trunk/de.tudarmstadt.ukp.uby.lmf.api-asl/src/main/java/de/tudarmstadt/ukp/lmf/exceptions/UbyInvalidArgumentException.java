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

package de.tudarmstadt.ukp.lmf.exceptions;

/**
 * This Exception should be thrown whenever an UBY-API method
 * consumes an invalid argument.
 * 
 * @author Michael Matuschek
 *
 */
public class UbyInvalidArgumentException extends Exception {

	private static final long serialVersionUID = -7331798183882114866L;

	/**
	 * Constructs a new {@link UbyInvalidArgumentException} with the specified cause.
	 * This constructor can be used as a wrapper.
	 * 
	 * @param cause a throwable which can be wrapped into this exception
	 */
	public UbyInvalidArgumentException(Throwable cause) {
        super(cause);
    }
	
	/**
	 * Constructs a new {@link UbyInvalidArgumentException} with the specified detail message.
	 * 
	 * @param message the message to be attached to the exception
	 */
	public UbyInvalidArgumentException(String message){
		super(message);
	}
}
