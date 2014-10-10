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
