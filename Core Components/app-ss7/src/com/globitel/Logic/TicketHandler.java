package com.globitel.Logic;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.globitel.Application.Application;
import com.globitel.utilities.commons.logger.MyLoggerFactory;

public class TicketHandler extends Thread {
	public static ConcurrentHashMap<String, Date> tickets = new ConcurrentHashMap<>();

	public TicketHandler() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		this.setName("TicketHandler");
		MyLoggerFactory.getInstance().getAppLogger().info(String.format("Ticket Handler Starting..."));
		do {
			try {
				MyLoggerFactory.getInstance().getAppLogger().debug(String.format("Looping over rejected UL tickets.."));
				Date tempDate = null;
				for (String ticket : tickets.keySet()) {
					tempDate = tickets.get(ticket);
					if (new Date().getTime() - tempDate.getTime() > Application.ULTicketTimeout) {
						MyLoggerFactory.getInstance().getAppLogger().info(String.format("Timed-out UL Ticket TID: %s.", ticket));
						tickets.remove(ticket);
					}
				}

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				MyLoggerFactory.getInstance().getAppLogger().error(String.format("Exception : %s.", e.getMessage()), e);
			}
		} while (this.isInterrupted() == false);
		MyLoggerFactory.getInstance().getAppLogger().error("'CycleTicketsTimeoutThread' Thread Ended...");
	}

}
