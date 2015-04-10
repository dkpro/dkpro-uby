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
package de.tudarmstadt.ukp.lmf.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.dialect.H2Dialect;

/**
 * Variant of the {@link H2Dialect} that treats LONGVARCHAR fields as 
 * VARCHAR(maxint). This is necessary since Hibernate's schema validator
 * fails for fields typed "text" otherwise.
 */
public class UBYH2Dialect extends H2Dialect {
	
	@Override
	public String getTypeName(int code, long length, int precision, int scale)
			throws HibernateException {
		String result = super.getTypeName(code, length, precision, scale);
		if ("longvarchar".equals(result))
			return "varchar(" + Integer.MAX_VALUE + ")";
		return result;
	}

}
