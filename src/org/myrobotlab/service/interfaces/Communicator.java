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

package org.myrobotlab.service.interfaces;

import java.net.URI;
import java.util.HashMap;

import org.myrobotlab.framework.Message;
import org.myrobotlab.net.CommData;

public interface Communicator {

	
	/**
	 * will send a message to the mrl key'ed uri
	 * the expectation is the uri is directly from the hosts registry in runtime
	 * therefore it has the following format
	 * 
	 * mrl://[hostname]/proto://protohost:protoport/otherkeyinfo
	 * 
	 * e.g. a tcp connection throughh a RemoteAdapter instance named "remote" would be
	 * 		
	 * 		mrl://remote/tcp://somehost:6767
	 * 
	 * @param uri
	 * @param msg
	 */
	public void sendRemote(final URI uri, final Message msg); 

	// FIXME - remove - not needed now that all Communictors are Services ?
	// should be shutdown - pauseCommunication - stopCommunication ?

	/**
	 * DEPRICATE
	 * 
	 * adds remote client data - the uri key will be used for
	 * messages which need to be sent to the remote client
	 * the commData is all the data necessary to communicate to that client
	 * there might be enough info in just the uri - but depending on the
	 * protocol - more info might be needed
	 * 
	 * @param uri
	 * @param commData
	 */
	public void addClient(URI uri, Object commData);

	public HashMap<URI, CommData> getClients();
	
}
