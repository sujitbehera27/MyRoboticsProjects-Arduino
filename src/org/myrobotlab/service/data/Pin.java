/**
 *                    
 * @author grog (at) myrobotlab.org
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

package org.myrobotlab.service.data;

import java.io.Serializable;

import org.myrobotlab.logging.LoggerFactory;
import org.slf4j.Logger;


public class Pin implements Serializable {
	private static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(Pin.class);
	public static final int DIGITAL_VALUE = 1; // normalized with data coming
												// from
												// Arduino.DIGITAL_READ_POLLING_START
	public static final int PWM_VALUE = 2;
	public static final int ANALOG_VALUE = 3; // normalized with data coming
												// from
												// Adruino.ANALOG_READ_POLLING_START

	public int pin;
	public int type;
	public int value;
	public String source;

	public Pin() {
	}

	public Pin(int pin, int type, int value, String source) {
		this.pin = pin;
		this.type = type;
		this.value = value;
		this.source = source;
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("{");
		ret.append("\"pin\":" + "\"" + pin + "\"");
		ret.append("\"function\":" + "\"" + type + "\"");
		ret.append("\"value\":" + "\"" + value + "\"");
		ret.append("}");
		return ret.toString();
	}

}