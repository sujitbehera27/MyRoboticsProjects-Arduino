/**
 *                    
 * @author greg (at) myrobotlab.org
 *  
 * This file is part of MyRobotLab (http://myrobotlab.org).
 *
 * MyRobotLab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * MyRobotLab is distributed in the hope that it will be useful or fun,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * All libraries in thirdParty bundle are subject to their own license
 * requirements - please refer to http://myrobotlab.org/libraries for 
 * details.
 * 
 * Enjoy !
 * 
 * */

package org.myrobotlab.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.myrobotlab.framework.Encoder;
import org.myrobotlab.framework.Message;
import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.data.ClockEvent;
import org.slf4j.Logger;

public class Clock extends Service {

	private static final long serialVersionUID = 1L;

	public final static Logger log = LoggerFactory.getLogger(Clock.class.getCanonicalName());

	public boolean isClockRunning;
	public int interval = 1000;

	public transient ClockThread myClock = null;

	// FIXME
	ArrayList<ClockEvent> events = new ArrayList<ClockEvent>();

	public class ClockThread implements Runnable {
		public Thread thread = null;

		ClockThread() {
			thread = new Thread(this, getName() + "_ticking_thread");
			thread.start();
		}

		public void run() {
			try {
				while (isClockRunning == true) {
					Date now = new Date();
					Iterator<ClockEvent> i = events.iterator();
					while (i.hasNext()) {
						ClockEvent event = i.next();
						if (now.after(event.time)) {
							// TODO repeat - don't delete set time forward
							// interval
							send(event.name, event.method, event.data);
							i.remove();
						}
					}
					invoke("pulse", new Date());
					Thread.sleep(interval);
				}
			} catch (InterruptedException e) {
				log.info("ClockThread interrupt");
				isClockRunning = false;
			}
		}
	}

	public Clock(String n) {
		super(n);
	}

	public void startClock() {
		if (myClock == null) {
			isClockRunning = true;
			myClock = new ClockThread();
			invoke("clockStarted");
		} else {
			log.warn("clock already started");
		}
	}
	
	// clock started  event
	public void clockStarted(){
	}

	public void stopClock() {

		if (myClock != null) {
			log.info("stopping " + getName() + " myClock");
			isClockRunning = false;
			myClock.thread.interrupt();
			myClock.thread = null;
			myClock = null;
			// have requestors broadcast state !
			// broadcastState();
			invoke("clockStopped");
		} else {
			log.warn("clock already stopped");
		}

		isClockRunning = false;
	}

	public void clockStopped(){
	}

	public Date pulse(Date time) {
		return time;
	}

	public void setInterval(Integer milliseconds) {
		interval = milliseconds;
	}

	@Override
	public void stopService() {
		stopClock();
		super.stopService();
	}

	@Override
	public String getDescription() {
		return "used to generate pulses";
	}

	public void addClockEvent(Date time, String name, String method, Object... data) {
		ClockEvent event = new ClockEvent(time, name, method, data);
		events.add(event);
	}

	public static void main(String[] args) throws ClassNotFoundException, CloneNotSupportedException {
		LoggingFactory.getInstance().configure();
		LoggingFactory.getInstance().setLevel(Level.INFO);

		String test = "tcp";

		if ("tcp".equals(test)) {
			// TCP CONNECT WORKS BEGIN ---------------------------------
			try {

				int i = 5;
				Runtime.main(new String[] { "-runtimeName", String.format("r%d", i) });
				// RemoteAdapter remote = (RemoteAdapter)
				// Runtime.createAndStart(String.format("remote%d", i),
				// "RemoteAdapter");
				RemoteAdapter remote = new RemoteAdapter(String.format("remote%d", i));
				remote.setUDPPort(7776);
				remote.setTCPPort(7776);
				remote.startService();
				// remote.startListening(7776, 7776); FIXME - problem starting
				// server threads which rely on isRunning() - which is false -
				// cuz you have not started it yet
				Clock clock = (Clock) Runtime.createAndStart(String.format("clock%d", i), "Clock");
				Runtime.createAndStart(String.format("gui%d", i), "GUIService");
				
				//myservice.subscribe("clock", "pulse", "pulseMe");

				Message msg = remote.createMessage("", "register", clock);
				URI uri = new URI("tcp://127.0.0.1:6767");
				remote.sendRemote(uri, msg);

				// FIXME - sholdn't this be sendRemote ??? or at least
				// in an interface
				// remote.sendRemote(uri, msg);
				// xmpp1.sendMessage("xmpp 2", "robot02 02");
			} catch (Exception e) {
				Logging.logException(e);
			}
			// TCP CONNECT WORKS END ---------------------------------

		} else if ("xmpp".equals(test)) {

			// XMPP CONNECT WORKS BEGIN ---------------------------------
			int i = 2;

			Runtime.main(new String[] { "-runtimeName", String.format("r%d", i) });
			Security security = (Security) Runtime.createAndStart("security", "Security");
			security.addUser("incubator incubator");
			security.setGroup("incubator incubator", "authenticated");
			security.allowExportByType("XMPP", false);
			security.allowExportByType("Security", false);
			security.allowExportByType("Runtime", false);
			XMPP xmpp1 = (XMPP) Runtime.createAndStart(String.format("xmpp%d", i), "XMPP");
			Clock clock = (Clock) Runtime.createAndStart(String.format("clock%d", i), "Clock");
			Runtime.createAndStart(String.format("gui%d", i), "GUIService");

			xmpp1.connect("talk.google.com", 5222, "robot02@myrobotlab.org", "mrlRocks!");

			Message msg = null;

			msg = xmpp1.createMessage("", "register", clock);
			String base64 = Encoder.msgToBase64(msg);
			xmpp1.sendMessage(base64, "incubator incubator");

			// XMPP CONNECT WORKS END ---------------------------------
		}
	}

}