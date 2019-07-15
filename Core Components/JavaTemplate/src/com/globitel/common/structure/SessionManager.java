package com.globitel.common.structure;


public interface SessionManager
{
	public Object getSession(String keyBf);

	public void addSession(String key, Object session);
	
	public void removeSession(String keyBf);
	
	public boolean shouldDeleteSession(Object session) throws Exception;
}
