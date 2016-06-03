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
package de.tudarmstadt.ukp.lmf.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
 
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

/**
 * Custom {@link EnumUserType} for all enumerators in the UBY-LMF model.
 * 
 * @author Yevgen Chebotar
 *
 */
@SuppressWarnings("rawtypes")
public class EnumUserType implements EnhancedUserType, ParameterizedType {
    
	private Class<Enum> enumClass;
 
    @SuppressWarnings("unchecked")
	public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            enumClass = (Class<Enum>) Class.forName(enumClassName);
        }
        catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }
 
    public Object assemble(Serializable cached, Object owner) 
    throws HibernateException {
        return cached;
    }
 
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }
 
    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }
 
    public boolean equals(Object x, Object y) throws HibernateException {
        return x==y;
    }
 
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }
 
    public boolean isMutable() {
        return false;
    }
 
    /*@SuppressWarnings("unchecked")
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
    throws HibernateException, SQLException {
        String name = rs.getString( names[0] );
        return rs.wasNull() ? null : Enum.valueOf(enumClass, name);
    }
 
    public void nullSafeSet(PreparedStatement st, Object value, int index) 
    throws HibernateException, SQLException {
        if (value==null) {
            st.setNull(index, Types.VARCHAR);
        }
        else {
            st.setString( index, ( (Enum) value ).name() ); 
        }
    }
    */
    
    @Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor si, Object owner) 
			throws HibernateException,SQLException {
    	 String name = rs.getString( names[0] );
         return rs.wasNull() ? null : Enum.valueOf(enumClass, name);
   	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor si) 
			throws HibernateException, SQLException {
		 if (value==null) {
	        st.setNull(index, Types.VARCHAR);
	     }else{
	        st.setString( index, ( (Enum) value ).name() ); 
	     }
		
	} 
 
    public Object replace(Object original, Object target, Object owner) 
    throws HibernateException {
        return original;
    }
 
    public Class returnedClass() {
        return enumClass;
    }
 
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }
 
    @SuppressWarnings("unchecked")
	public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(enumClass, xmlValue);
    }
 
    public String objectToSQLString(Object value) {
        return '\'' + ( (Enum) value ).name() + '\'';
    }
 
    public String toXMLString(Object value) {
        return ( (Enum) value ).name();
    }

	
}