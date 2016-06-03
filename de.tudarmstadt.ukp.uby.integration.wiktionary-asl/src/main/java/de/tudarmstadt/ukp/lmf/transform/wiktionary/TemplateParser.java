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
package de.tudarmstadt.ukp.lmf.transform.wiktionary;

import java.util.LinkedHashMap;
import java.util.Map;

import de.tudarmstadt.ukp.wiktionary.api.util.ILanguage;
import de.tudarmstadt.ukp.wiktionary.api.util.Language;
import de.tudarmstadt.ukp.wiktionary.parser.util.StringUtils;

//TODO: move to JWKTL.
public class TemplateParser {

	public static class Template {
		
		protected String name;
		protected Map<String, String> params;
		
		public Template(final String name) {
			this.name = name;
			params = new LinkedHashMap<String, String>();
		}
		
		public String getName() {
			return name;
		}

		public void addParam(int idx, final String value) {
			params.put(Integer.toString(idx), value);
		}

		public void addParam(final String key, final String value) {
			// Add duplicates with a suffix.
			if (params.containsKey(key)) {
				int suffix = 2;
				while (params.containsKey(key + suffix))
					suffix++;
				params.put(key + suffix, value);
			} else
			params.put(key, value);			
		}

		public String getParam(int idx) {
			return params.get(Integer.toString(idx));
		}
		
	}
	
	public interface ITemplateHandler {
		
		public String handle(final Template template);
		
	}
	
	public static class EtymologyTemplateHandler implements ITemplateHandler {

		public String handle(final Template template) {
			if ("etyl".equals(template.getName())) {
				String languageCode = template.getParam(0);
				ILanguage language = Language.findByCode(languageCode);
				if (language == null)
					return "{" + languageCode + "}";
				else
					return language.getName();				
			} else			
			if ("term".equals(template.getName())) {
				String term = template.getParam(0);
				String notes = template.getParam(2);
				if (notes == null)
					return term;
				else
					return term + " (“" + notes + "”)";
			} else			
			if ("recons".equals(template.getName())) {
				String term = template.getParam(0);
				String notes = template.getParam(2);
				if (notes == null)
					return "*" + term;
				else
					return "*" + term + " (“" + notes + "”)";
			}
			
			// Remove other templates.
			return "";
		}
		
	}
	
	public static String parse(final String wikiText, final ITemplateHandler handler) {
		StringBuilder result = new StringBuilder();
		String text = wikiText;
		int startIdx;
		do {
			startIdx = text.indexOf("{{");
			if (startIdx >= 0) {
				result.append(text.substring(0, startIdx));
				int endIdx = text.indexOf("}}", startIdx);
				if (endIdx >= 0) {
					String templateText = text.substring(startIdx + 2, endIdx);
					if (handler != null) {
						Template template = parseTemplate(templateText);
						if (template != null)
							result.append(handler.handle(template));
						else
							result.append("{{").append(templateText).append("}}");
					} else
						result.append("{{").append(templateText).append("}}");
					text = text.substring(endIdx + 2);
				} else
					text = text.substring(startIdx + 2);
			}
		} while (startIdx >= 0);
		result.append(text);
		return result.toString();
	}

	public static Template parseTemplate(final String templateText) {
		// Split the template text to access the parameters.
		String[] params = StringUtils.split(templateText, '|');
		if (params.length < 1)
			return null;
		
		Template result = new Template(params[0]);
		int idx = -1;
		for (String param : params) {
			// Skip the template name.
			if (idx < 0) {
				idx++;
				continue;
			}
			
			// Check for named params.
			int j = param.indexOf('=');
			if (j >= 0) {
				String key = param.substring(0, j);
				String value = param.substring(j + 1);				
				result.addParam(key, value);
			} else
				result.addParam(idx++, param);			
		}
		return result;
	}
	
	
}
