package com.globitel.Application;

import java.util.ArrayList;

import com.globitel.SS7.codec.Message;

public class SessionPool {

	private static ArrayList<SessionManagerImp> sessionManagers;

	public static void initialize(int serverId, int numberOfThreads) {
		try {
			sessionManagers = new ArrayList<SessionManagerImp>();
			for (int sessionManagerIndex = 0; sessionManagerIndex < numberOfThreads; ++sessionManagerIndex) {
				SessionManagerImp sessionManager = new SessionManagerImp(sessionManagerIndex, serverId);
				sessionManager.start();
				sessionManagers.add(sessionManager);
			}
			
		} catch (Exception e) {
			Message.logError("Exception, initializing ProcessorUtils: " + e.getMessage());
		}
	}

	public static SessionManagerImp getSessionManager(int processorID) {
		return sessionManagers.get(processorID);
	}
	
	public static void endSessions() {
		SessionManagerImp.isRunning = false;
	}

}
