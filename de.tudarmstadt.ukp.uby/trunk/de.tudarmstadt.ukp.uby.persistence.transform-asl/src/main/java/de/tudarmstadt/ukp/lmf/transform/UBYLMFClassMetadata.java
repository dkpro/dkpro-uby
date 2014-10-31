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
package de.tudarmstadt.ukp.lmf.transform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.lmf.model.miscellaneous.EVarType;
import de.tudarmstadt.ukp.lmf.model.miscellaneous.VarType;

/**
 * Helper class for caching the fields of an LMF class along with their
 * types, getters, and setters. The implementation basically determines all
 * field, type, and method information using the Java reflection API. Rather
 * than obtaining this kind of information over and over again (which would
 * slow down the application's performance), it is recommended to obtain
 * this meta information just once and store it in a cache (e.g., a simple
 * {@link Map}) using this class as a data model.  
 * @author Christian M. Meyer
 */
public class UBYLMFClassMetadata {

	/**
	 * Metadata information of a single field within an LMF class. The
	 * class should not be instanciated individually. Use 
	 * {@link UBYLMFClassMetadata} as a container for the field 
	 * metadata instances.
	 * @author Christian M. Meyer
	 */
	public static class UBYLMFFieldMetadata {
		
		protected Field field;
		protected Class<?> type;
		protected Class<?> genericElementType;
		protected EVarType varType;
		protected Method getter;
		protected Method setter;
		
		/** Instanciates a new field metadata cache for the given
		 *  field. This involves determining the field's type, getter,
		 *  and setter. For parameterized types, the given
		 *  actual type will be used which should match the generic
		 *  parameter of the enclosing subclass. */
		protected UBYLMFFieldMetadata(final Field field, final Class<?> actualType) {
			this.field = field;

			// Raw type.
			type = field.getType();
			if (actualType != null && type == Object.class 
					&& !type.equals(field.getGenericType()))
				type = actualType;

			// Generic type parameter.
			genericElementType = null;
			Type genericFieldType = field.getGenericType();
			if (genericFieldType instanceof ParameterizedType) {
			    ParameterizedType aType = (ParameterizedType) genericFieldType;
			    genericElementType = (Class<?>) aType.getActualTypeArguments()[0];
			}

			// VarType.
			VarType varTypeAnnot = field.getAnnotation(VarType.class);
			if (varTypeAnnot == null) {
				varType = EVarType.NONE;
				return; // no getter/setter
			} else
				varType = varTypeAnnot.type();
			
			// Determine getter and setter methods.
			String methodName = field.getName();
			methodName = methodName.replace("_", "");
			if (methodName.startsWith("is"))
				methodName = methodName.substring(2);
			methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1);
			
			String getterName = "get" + methodName;
			if (type == boolean.class || type == Boolean.class)
				getterName = "is" + methodName;
			String setterName = "set" + methodName;
			
			for (Method method : field.getDeclaringClass().getMethods()) {
				if (method.getName().equals(getterName))
					getter = method;
				else
				if (method.getName().equals(setterName))
					setter = method;
			}
		}

		/** Returns the name of the field. Shorthand for 
		 *  <code>getField().getName()</code>.*/
		public String getName() {
			return field.getName();
		}
		
		/** Returns the data type of this field. For generic types, the raw
		 *  type will be returned (e.g., List for List&lt;Number&gt;). Use
		 *  {@link #getGenericElementType()} for obtaining the generic
		 *  type parameter. For parameterized types, the actual type is
		 *  returned, if available (e.g., <code>Double</code> for an 
		 *  inherited field defined as <code>T number</code> in a generic class 
		 *  <code>TestClass extends BaseClass&lt;Double</code> that is 
		 *  a subclass of <code>BaseClass%lt;T extends Number&gt;</code>). */
		public Class<?> getType() {
			return type;
		}
		
		/** Returns true if, and only if, the field represents a truth
		 *  value using a primitive or wrapped boolean type. */
		public boolean isBoolean() {
			return (type == boolean.class || type == Boolean.class);
		}

		/** Returns true if, and only if, the field represents an integer
		 *  using a primitive or wrapped int type. */
		public boolean isInteger() {
			return (type == int.class || type == Integer.class);
		}

		/** Returns true if, and only if, the field represents a floating
		 *  point number using a primitive or wrapped double type. */
		public boolean isDouble() {
			return (type == double.class || type == Double.class);
		}
		
		/** Returns true if, and only if, the field is an enumeration type. */
		public boolean isEnum() {
			return type.isEnum();
		}

		/** Returns the generic parameter of the field. That is, if the 
		 *  field is not a raw type, the actual type parameter will be 
		 *  returned (e.g., Number for List&lt;Number&gt;). */
		public Class<?> getGenericElementType() {
			return genericElementType;
		}

		/** Returns the variable type of this field. */
		public EVarType getVarType() {
			return varType;
		}
		
		/** Returns the getter method of this field. */
		public Method getGetter() {
			return getter;
		}

		/** Returns the setter method of this field. */
		public Method getSetter() {
			return setter;
		}
		
	}
	
	
	protected Class<?> clazz;
	protected List<UBYLMFFieldMetadata> fields;

	/** Create a new metadata representation for the given LMF class. 
	 *  That is, the implementation determines information on all declared
	 *  fields of the given class (using the Java reflection API) and
	 *  creates a {@link UBYLMFFieldMetadata} instance for each field. */
	public UBYLMFClassMetadata(final Class<?> clazz) {
		this.clazz = clazz;
		fields = new ArrayList<UBYLMFFieldMetadata>();			
		for (Field field : clazz.getDeclaredFields())
			fields.add(new UBYLMFFieldMetadata(field, null));

		Type superClassType = clazz.getGenericSuperclass();
		while (superClassType != null) {
			//TODO: This is hacky, since only the first type parameter
			//  is used and there's no matching to type parameter names.
			//  However, there seems to be no clear mapping between
			//  type parameter index and name. 
			Class<?> superClass;
			Class<?> genericType = null;
			if (superClassType instanceof ParameterizedType) {
				ParameterizedType aType = (ParameterizedType) superClassType;
				genericType = (Class<?>) aType.getActualTypeArguments()[0];
				superClass = (Class<?>) aType.getRawType();
			} else
				superClass = (Class<?>) superClassType;

			for (Field field : superClass.getDeclaredFields())
				fields.add(new UBYLMFFieldMetadata(field, genericType));

			superClassType = superClass.getGenericSuperclass();
		}
	}

	/** Return the meta information of all fields the current LMF class
	 *  contains. */
	public Iterable<UBYLMFFieldMetadata> getFields() {
		return fields;
	}

}
