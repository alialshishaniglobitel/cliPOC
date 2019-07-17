package com.globitel.diameterCodec.Diameter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementations from this interface shall be used in branching evaluation process.
 */
public interface Evaluatable {
	
	Map<String, Object> valuesMap = new ConcurrentHashMap<String, Object>();

	public Object getProperty(String fieldName);

	public void setProperty(String fieldName, Object value);

}
