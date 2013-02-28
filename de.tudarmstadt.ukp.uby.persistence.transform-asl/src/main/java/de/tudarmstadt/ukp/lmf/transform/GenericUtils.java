/*******************************************************************************
 * Copyright 2012
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class GenericUtils {

	/**
	 * Returns all fields of a class along with their types
	 * Generic types are resolved
	 * @param clazz
	 * @return
	 */
	public static HashMap<Field, Class> getFields(Class clazz){
		HashMap<Field, Class> fields = new HashMap<Field, Class>();
		
		for(Field field : clazz.getDeclaredFields()){			
			fields.put(field, field.getType());				
		}
		Class superClass = clazz.getSuperclass();
		if(superClass != null){
			HashMap<Field,Class> superFields = getFields(superClass);
			Type superClassType = clazz.getGenericSuperclass();

			if(superClassType instanceof ParameterizedType){
				ParameterizedType aType = (ParameterizedType) superClassType;
				Class actualClass = (Class)aType.getActualTypeArguments()[0];
			    for(Field field : superFields.keySet()){
			    	if(superFields.get(field).equals(Object.class))
			    		superFields.put(field, actualClass);				    	
			    }				    
			}
			fields.putAll(superFields);
		}
		return fields;
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public static Class getListElementClass(Field field) {
		Type genericFieldType = field.getGenericType();

		if(genericFieldType instanceof ParameterizedType){
		    ParameterizedType aType = (ParameterizedType) genericFieldType;
		    Type[] fieldArgTypes = aType.getActualTypeArguments();
		    for(Type fieldArgType : fieldArgTypes){
		        Class fieldArgClass = (Class) fieldArgType;
		        return fieldArgClass;
		    }
		}
		return null;
	}
	
	/**
	 * Return boolean value of a string
	 * @param value
	 * @return
	 */
	public static boolean getBoolean(String value){
		if(value != null && (value.equals("yes") || value.equals("true")))
			return true;
		else return false;
	}

	/**
	 * Return string value of a string
	 * @param value
	 * @return
	 */
	public static int getInteger(String value){
		if(value != null)
			return Integer.parseInt(value);
		else return 0;
	}
	
	/**
	 * Return Enum-value of a string
	 * @param fieldClass
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getEnum(Class fieldClass, String value) {
		if(value != null)
			return Enum.valueOf(fieldClass, value);
		else return null;
	}

}
