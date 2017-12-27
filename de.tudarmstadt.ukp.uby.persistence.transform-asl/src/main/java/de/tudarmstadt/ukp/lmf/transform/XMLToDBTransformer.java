/*******************************************************************************
 * Copyright 2017
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

package de.tudarmstadt.ukp.lmf.transform;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.lmf.model.core.GlobalInformation;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.LexicalResource;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.interfaces.IHasID;
import de.tudarmstadt.ukp.lmf.model.meta.MetaData;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.ConstraintSet;
import de.tudarmstadt.ukp.lmf.model.multilingual.PredicateArgumentAxis;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.SemanticPredicate;
import de.tudarmstadt.ukp.lmf.model.semantics.SynSemCorrespondence;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrame;
import de.tudarmstadt.ukp.lmf.model.syntax.SubcategorizationFrameSet;
import de.tudarmstadt.ukp.lmf.transform.UBYLMFClassMetadata.UBYLMFFieldMetadata;

/**
 * Converts a given lexical resource from a UBY-XML file to a UBY database
 * using Hibernate.
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class XMLToDBTransformer extends UBYHibernateTransformer
		implements ElementHandler {

	protected LexicalResource lexicalResource; // Current lexical resource
	protected Lexicon lexicon; // Current lexicon
	protected boolean externalLexicalResource;

	public XMLToDBTransformer(final DBConfig dbConfig) {
		super(dbConfig);
	}

	/**
	 * Read xml File and save its contents to Database
	 * @param xmlFile
	 * @param lexicalResourceName
	 * @throws DocumentException
	 * @throws UbyInvalidArgumentException
	 */
	public void transform(File xmlFile, String lexicalResourceName) throws DocumentException, IllegalArgumentException{
		long startTime = System.currentTimeMillis();

		openSession();

		if (lexicalResourceName != null) {
            lexicalResource = (LexicalResource) session.get(LexicalResource.class, lexicalResourceName);
        }

		SAXReader reader = new SAXReader(false);
		reader.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				if (systemId.endsWith(".dtd")) {
                    return new InputSource(new StringReader(""));
                }
				return null;
			}
		});
		reader.setDefaultHandler(this);
		reader.read(xmlFile);

		commit();
		closeSession();

		System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - startTime));
		System.out.println("NUM ENTRIES: " + commitCounter);
	}

	@Override
	public void onStart(ElementPath epath) {
		Element el = epath.getCurrent();
		String n = el.getName();

		// Remove empty attributes and invalid characters.
		Iterator<?> attrIter = el.attributeIterator();
		while (attrIter.hasNext()) {
			Attribute attr = (Attribute) attrIter.next();
			if ("NULL".equals(attr.getStringValue())) {
                attrIter.remove();
            }
            else {
                attr.setValue(StringUtils.replaceNonUtf8(attr.getValue()));
            }
		}

		if ("LexicalResource".equals(n)) {
			// If no lexical resource exists yet, create a new one.
			if (lexicalResource == null){
				lexicalResource = new LexicalResource();
				lexicalResource.setName(el.attributeValue("name"));
				lexicalResource.setDtdVersion(el.attributeValue("dtdVersion"));
				session.save(lexicalResource);
			}
            else {
                externalLexicalResource = true;
            }
		} else
		if ("Lexicon".equals(n)) {
			// Create a new, empty lexicon.
			lexicon = new Lexicon();
			lexicon.setId(el.attributeValue("id"));
			lexicon.setName(el.attributeValue("name"));
			lexicon.setLanguageIdentifier(el.attributeValue("languageIdentifier"));
			lexicalResource.addLexicon(lexicon);
			saveCascade(lexicon, lexicalResource);
        }
        // Save some global information if we're using a new lexical resource.
        else if ("GlobalInformation".equals(n) && !externalLexicalResource) {
            GlobalInformation glInformation = new GlobalInformation();
            glInformation.setLabel(el.attributeValue("label"));
            lexicalResource.setGlobalInformation(glInformation);
            saveCascade(glInformation, lexicalResource);
            commit();
            lexicalResource.setGlobalInformation(null);
        }
	}

	@Override
	public void onEnd(ElementPath epath) {
		Element el = epath.getCurrent();
		String n = el.getName();
		Object listElement = null;

		// Create instances for all direct children of Lexicon.
		if ("LexicalEntry".equals(n)) {
			listElement = fromXmlToObject(el, LexicalEntry.class);
			saveListElement(lexicon, lexicon.getLexicalEntries(), listElement);
		} else
		if ("SemanticPredicate".equals(n)) {
			listElement = fromXmlToObject(el, SemanticPredicate.class);
			saveListElement(lexicon, lexicon.getSemanticPredicates(), listElement);
		} else
		if ("SubcategorizationFrame".equals(n)) {
			listElement = fromXmlToObject(el, SubcategorizationFrame.class);
			saveListElement(lexicon, lexicon.getSubcategorizationFrames(), listElement);
		} else
		if ("SubcategorizationFrameSet".equals(n)) {
			listElement = fromXmlToObject(el, SubcategorizationFrameSet.class);
			saveListElement(lexicon, lexicon.getSubcategorizationFrameSets(), listElement);
		} else
		if ("SynSemCorrespondence".equals(n)) {
			listElement = fromXmlToObject(el, SynSemCorrespondence.class);
			saveListElement(lexicon, lexicon.getSynSemCorrespondences(), listElement);
		} else
		if ("Synset".equals(n)) {
			listElement = fromXmlToObject(el, Synset.class);
			saveListElement(lexicon, lexicon.getSynsets(), listElement);
		} else
		if ("ConstraintSet".equals(n)) {
			listElement = fromXmlToObject(el, ConstraintSet.class);
			saveListElement(lexicon, lexicon.getConstraintSets(), listElement);
		} else

		// Create instances for all direct children of LexicalResource.
		if ("SenseAxis".equals(n)) {
			listElement = fromXmlToObject(el, SenseAxis.class);
			saveListElement(lexicalResource, lexicalResource.getSenseAxes(), listElement);
		} else
		if ("PredicateArgumentAxis".equals(n)) {
				listElement = fromXmlToObject(el, PredicateArgumentAxis.class);
				saveListElement(lexicalResource, lexicalResource.getPredicateArgumentAxes(), listElement);
			} else
		if ("MetaData".equals(n)) {
			listElement = fromXmlToObject(el, MetaData.class);
			saveListElement(lexicalResource, lexicalResource.getMetaData(), listElement);
		}

		// Forget the corresponding XML elements of the saved instances.
		if (listElement != null) {
            el.detach();
        }
	}

	/**
	 * Transforms XML-Element and all its children to Java object
	 * @param el XML-Element
	 * @param clazz Java-Class of the Element
	 * @return
	 */
	protected Object fromXmlToObject(Element el, Class<?> clazz)  {
		try {
			Object lmfObject = clazz.newInstance();
			UBYLMFClassMetadata classMeta = getClassMetadata(clazz);
			for (UBYLMFFieldMetadata fieldMeta : classMeta.getFields()) {
				String xmlFieldName = fieldMeta.getName().replace("_", "");
				Class<?> fieldType = fieldMeta.getType();

				// Determine the field's value from the current XML element.
				Object newValue = null;
				switch (fieldMeta.getVarType()) {
					case ATTRIBUTE:
					case ATTRIBUTE_OPTIONAL:
						String attrValue = el.attributeValue(xmlFieldName);
						if (attrValue == null) {
                            continue;
                        }

						newValue = attrValue;
						if (fieldMeta.isBoolean()) {
                            newValue = GenericUtils.getBoolean(attrValue);
                        }
                        else
						if (fieldMeta.isInteger()) {
                            newValue = GenericUtils.getInteger(attrValue);
                        }
                        else
						if (fieldMeta.isDouble()) {
                            newValue = GenericUtils.getDouble(attrValue);
                        }
                        else
						if (fieldMeta.isEnum()) {
                            newValue = GenericUtils.getEnum(fieldType, attrValue);
                        }
						else
						if (fieldMeta.isDate()){
							newValue = GenericUtils.getDate(attrValue);
						}
						break;

					case CHILD:
						Element childEl = el.element(fieldType.getSimpleName());
						if (childEl == null) {
                            continue;
                        }

						newValue = fromXmlToObject(childEl, fieldType);
						break;

					case CHILDREN:
						Class<?> elementClass = fieldMeta.getGenericElementType();
						if (elementClass == null) {
                            throw new RuntimeException("Unable to obtain list element class for field " + fieldMeta.getName());
                        }

						List<Object> childList = new ArrayList<Object>();
						for (Object child : el.elements(elementClass.getSimpleName())) {
                            childList.add(fromXmlToObject((Element) child, elementClass));
                        }
						newValue = childList;
						break;

					case IDREF:
						String idref = el.attributeValue(xmlFieldName);
						if (idref == null || idref.isEmpty()) {
                            continue;
                        }

						IHasID obj = (IHasID) fieldType.newInstance();
						obj.setId(idref);
						newValue = obj;
						break;

					case IDREFS:
						String idStr = el.attributeValue(xmlFieldName);
						if (idStr == null || idStr.isEmpty()) {
                            continue;
                        }

						elementClass = fieldMeta.getGenericElementType();
						if (elementClass == null) {
                            throw new RuntimeException("Unable to obtain list element class for field " + fieldMeta.getName());
                        }

						List<Object> idrefList = new ArrayList<Object>();
						String ids[] = idStr.split(" ");
						for (String id : ids) {
							obj = (IHasID) elementClass.newInstance();
							obj.setId(id);
							idrefList.add(obj);
						}
						newValue = idrefList;
						break;

					case NONE:
						continue;
				}

				// Save the new value using the setter method.
				Method setter = fieldMeta.getSetter();
				if (setter == null) {
                    throw new RuntimeException("Missing setter for : " + lmfObject.getClass() + "." + xmlFieldName);
                }
				setter.invoke(lmfObject, newValue);
			}
			return lmfObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String getResourceAlias() {
		return lexicalResource.getName();
	}

}
