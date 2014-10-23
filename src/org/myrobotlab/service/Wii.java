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

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.util.ArrayList;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.serial.gnu.BinaryCommPort.LineDriver;
import org.slf4j.Logger;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;

// http://diy.sickmods.net/Tutorials/Wii/Disassemble_Wiimote/
// http://procrastineering.blogspot.com/2008/09/working-with-pixart-camera-directly.html
// http://www.bot-thoughts.com/2010/12/connecting-mbed-to-wiimote-ir-camera.html

public class Wii extends Service implements WiimoteListener, SerialPortEventListener, LineDriver {

	private static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(Wii.class.getCanonicalName());

	Wiimote[] wiimotes = null;
	Wiimote wiimote = null;
	int cnt = 0;
	boolean serialInitialized = false;

	public static class IRData {
		public long time;
		public IREvent event;

		public IRData(long t, int s) {
			this.time = t;
		}

		public IRData(long t, IREvent ir) {
			this.time = t;
			this.event = ir;
		}
	}

	public Wii(String n) {
		super(n);
	}



	// TODO - support shutdown()
	public void onButtonsEvent(WiimoteButtonsEvent arg0) {
		log.debug("{}",arg0);
		// if (arg0.isButtonAPressed()){
		// WiiUseApiManager.shutdown();
		// }

		if (arg0.isButtonDownJustPressed()) {
			invoke("onButtonDownJustPressed");
		}

		if (arg0.isButtonUpJustPressed()) {
			invoke("onButtonUpJustPressed");
		}

		if (arg0.isButtonLeftJustPressed()) {
			invoke("onButtonLeftJustPressed");
		}

		if (arg0.isButtonRightJustPressed()) {
			invoke("onButtonRightJustPressed");
		}
	}

	// button publishing/event hooks begin -------
	public void onButtonDownJustPressed() {
		log.info("buttonDownJustPressed");
	}

	public void onButtonUpJustPressed() {
		log.info("onButtonUpJustPressed");
	}

	public void onButtonLeftJustPressed() {
		log.info("onButtonLeftJustPressed");
	}

	public void onButtonRightJustPressed() {
		log.info("onButtonRightJustPressed");
	}

	// button publishing/event hooks end -------

	public void onIrEvent(IREvent arg0) {
		// log.debug(arg0);
		++cnt;
		if (cnt % 100 == 0) {
			log.error("cnt {}",cnt);
		}
		IRData ir = new IRData(System.currentTimeMillis(), arg0);
		// t.add(ir);
		invoke("publishIR", ir);
	}

	public IRData publishIR(IRData ir) {
		return ir;
	}

	public void onMotionSensingEvent(MotionSensingEvent arg0) {
		log.debug("motion sensing event {}",arg0);
	}

	public void onExpansionEvent(ExpansionEvent arg0) {
		log.debug("expansion event {}",arg0);
	}

	public void onStatusEvent(StatusEvent arg0) {
		log.debug("status event {}",arg0);
	}

	public void onDisconnectionEvent(DisconnectionEvent arg0) {
		log.debug("disconnect event {}",arg0);
	}

	public void onNunchukInsertedEvent(NunchukInsertedEvent arg0) {
		log.debug("NunchukInsertedEvent {}",arg0);
	}

	public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {
		log.debug("NunchukRemovedEvent {}",arg0);
	}

	public Wiimote[] getWiimotes() {
		wiimotes = WiiUseApiManager.getWiimotes(1, true);
		wiimote = wiimotes[0];
		return wiimotes;
	}

	public Wiimote[] getWiimotes(int n, boolean rumble) {
		return WiiUseApiManager.getWiimotes(n, rumble);
	}

	public void activateListening() {
		wiimote.addWiiMoteEventListeners(this);
	}

	public void activateIRTRacking() {
		wiimote.activateIRTRacking();
	}

	public void activateMotionSensing() {
		wiimote.activateMotionSensing();
	}

	public void setIrSensitivity(int level) {
		wiimote.setIrSensitivity(level);
	}

	public void setSensorBarBelowScreen() {
		wiimote.setSensorBarBelowScreen();
	}

	public void setSensorBarAboveScreen() {
		wiimote.setSensorBarAboveScreen();
	}

	public void setLeds(boolean l1, boolean l2, boolean l3, boolean l4) {
		wiimote.setLeds(l1, l2, l3, l4);
	}

	int pos = 0;

	static ArrayList<IRData> t = new ArrayList<IRData>();

	@Override
	public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {
		// TODO Auto-generated method stub

	}

	private long bitCount = 0;
	private boolean strobeState = true;

	public boolean initSerial() {

		// magic 3 bytes - this is to synch the protocol
		// when the wii connects the leds are flashing
		// the arduino will disregard this garbage data
		// the wii should stop with the first led high
		// these 3 bytes should stop the arduino from
		// continuing to disregard
		// synced

		if (wiimote == null) {
			log.error("wii is not connected - can not initialize");
			log.error("please press the (1) & (2) buttons of the wii - and re-run program while lights are flashing");
			return false;
		}

		if (!serialInitialized) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logException(e);
			} // must slow down to initialize

			// force to correct state strobe state
			wiimote.setLeds(true, false, false, false);

			// break out of init
			wiimote.setLeds(false, false, true, false);
			wiimote.setLeds(true, false, false, false);
			wiimote.setLeds(false, false, false, false); // last 2 bits
															// disregarded !

			wiimote.setLeds(true, false, false, false);
			wiimote.setLeds(false, false, false, false);
			wiimote.setLeds(true, false, false, false); // last 2 bits
														// disregarded

			wiimote.setLeds(false, false, false, false);
			wiimote.setLeds(true, true, true, true);
			wiimote.setLeds(false, true, true, true);

			serialInitialized = true;
		} else {
			log.warn("wii serial already initialized");
		}

		return true;
	}

	// NOT THREAD SAFE
	// true = 0 | false = 1 - thats how the hardware works :P
	public void sendSerial(int data) {
		if (!serialInitialized) {
			initSerial();
		}
		strobeState = (bitCount % 2 == 0);
		// 1st 3 MSB bits
		wiimote.setLeds(strobeState, (data & 128) == 0, (data & 64) == 0, (data & 32) == 0);
		++bitCount;
		// 2nd 3 bits
		wiimote.setLeds(!strobeState, (data & 16) == 0, (data & 8) == 0, (data & 4) == 0);
		++bitCount;
		// last 2 bits
		wiimote.setLeds(strobeState, (data & 2) == 0, (data & 1) == 0, false);

		++bitCount;

	}

	public static void main(String[] args) {

		LoggingFactory.getInstance().configure();
		LoggingFactory.getInstance().setLevel(Level.DEBUG);

		Wii wii = new Wii("wii");

		// add the port as a possible option for the Arduino
		/*
		 * Arduino.addPortName("wiicom", CommPortIdentifier.PORT_SERIAL,
		 * (CommDriver) new WiiDriver(wii));
		 */
		/*
		 * try { portId = CommPortIdentifier.getPortIdentifier("wiicom"); }
		 * catch (NoSuchPortException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		Arduino arduino = new Arduino("arduino");
		arduino.startService();

		Servo servo = new Servo("servo");
		servo.startService();

		//GUIService gui = new GUIService("gui");
		//gui.startService();
		

		wii.getWiimotes();
		wii.initSerial();
		wii.setSensorBarAboveScreen();
		wii.activateIRTRacking();
		wii.setIrSensitivity(5); // 1-5 (highest)
		wii.activateListening();

	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub
		log.info("serialEvent - YAY!");
	}

	@Override
	public void pulseDown() {
		wiimote.setLeds(true, true, true, true);
	}

	@Override
	public void pulseUp() {
		wiimote.setLeds(true, true, true, false);
	}

	@Override
	public String getDescription() {
		return "<html>wrapper service for wiiusej</html>";
	}

}
