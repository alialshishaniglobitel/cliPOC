package com.globitel.Application;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.globitel.SS7.codec.Message;
import com.globitel.common.structure.SessionManager;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class SessionManagerImp extends Thread implements SessionManager {

	private static Map<String, Session> map = new ConcurrentHashMap<String, Session>();
	LinkedList<String> timedOutKeys = new LinkedList<>();
	private int sessionManagerId;
	private int serverid;

	public static boolean isRunning = true;

	public SessionManagerImp(int sessionManagerId, int serverId) {
		this.sessionManagerId = sessionManagerId;
		this.serverid = serverId;
	}

	@Override
	public Session getSession(String keyBf) {
		if (keyBf == null) {
			return null;
		}
		Session result = null;
		result = map.get(keyBf);
		return result;
	}

	@Override
	public void addSession(String key, Object session) {
		// long key = GenerateTransaction();
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Save Session[%s] to get info about subscriber location.", key));
		((Session) session).updateSessionTime();
		map.put(key, (Session) session);
	}

	@Override
	public void removeSession(String keyBf) {
		try {
			getSession(keyBf).getCDR().ReportCDR();
			map.remove(keyBf);
			MyLoggerFactory.getInstance().getAppLogger().info(String.format("Remove Session[%s].", keyBf));
		} catch (Exception e) {
			MyLoggerFactory.getInstance().getAppLogger().error(
					String.format("Failed to remove session[%s], Exception : %s.", keyBf, e.getMessage()), e);
		}
	}

	public void run() {
		this.setName("SessionManager[" + this.sessionManagerId + "]");

		while (isRunning) {
			try {
				Collection<String> session_keys = null;
				session_keys = map.keySet();
				Iterator<String> itr = session_keys.iterator();

				while (itr.hasNext()) {
					String key = itr.next();
					Session session = null;
					session = map.get(key);
					String sessionKey = "";
					if (session != null) {
						if (shouldDeleteSession(session)) {
							timedOutKeys.add(key);
						}
					} else {
						MyLoggerFactory.getInstance().getAppLogger().debug(sessionKey + ", returned a NULL session!");
					}
				}
				Iterator<String> itr3 = timedOutKeys.iterator();
				while (itr3.hasNext()) {
					String key = itr3.next();
					removeSession(key);
				}
				timedOutKeys.clear();
			} catch (Exception e) {
				MyLoggerFactory.getInstance().getAppLogger().error("Exception : " + e.getMessage(), e);
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				MyLoggerFactory.getInstance().getAppLogger().error("SessionManager::run: InterruptedException: " + e.getMessage());
			}
		}
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("This thread has been Shutdown..."));
	}

	@Override
	public boolean shouldDeleteSession(Object session) throws Exception {
		if ((System.currentTimeMillis() - ((Session) session).time) > Application.sessionTimeOut) {
			return true;
		} else {
			return false;
		}
	}

	public int getServerID() {
		return serverid;
	}

}
