package org.myrobotlab.service;

import java.util.HashMap;
import java.util.HashSet;

import org.java_websocket.WebSocket;
import org.myrobotlab.fileLib.Zip;
import org.myrobotlab.framework.Encoder;
import org.myrobotlab.framework.Message;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.Status;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.net.BareBonesBrowserLaunch;
import org.myrobotlab.security.BasicSecurity;
import org.myrobotlab.service.interfaces.AuthorizationProvider;
import org.myrobotlab.webgui.WSServer;
import org.myrobotlab.webgui.WSServer.WSMsg;
import org.simpleframework.xml.Element;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebGUI extends Service implements AuthorizationProvider {

	// import javax.xml.transform.Transformer;

	// TODO - important !!!
	// api's XML, Text, HTML or other formatting of return type needs to be
	// encoded as part of the URI "BEFORE" the method request & paramters !!!!
	// e.g. http://127.0.0.1:7777/api/xml/services/arduino01/digitalWrite/13/1
	// Consistency is important to maintain the REST API !!!!
	// Jenkins did it right -
	// https://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API
	// http://server/jenkins/crumbIssuer/api/xml (or /api/json) !
	// HA !! realize that there are 2 encodings !!! inbound and return
	// VERY SIMPLE BUT IT MUST BE CONSISTENT !!!
	// http://mrl:7777/api/<inbound format>/<outbound
	// format>/<method>/<parameters>/
	// http://mrl:7777/api/<rest>/<xml>/<method>/<params> !!! inbound is rest -
	// return format is xml !!!
	// http://mrl:7777/api/<soap>/<xml>/<method>/<params> !!! inbound is soap -
	// return format is xml !!!
	// http://mrl:7777/api/<soap>/<soap>/<method>/<params> !!! inbound is soap -
	// return format is soap !!!
	// http://mrl:7777/api/<resource>/<json>/<method>/<params> !!! inbound is
	// resource request - return format is json !!!
	// default is /<rest>/<gson>/<method>/<params>

	// dropped - NanoHTTPD in favor of websockets with user call-back
	// http://nanohttpd.com/

	// FIXME !!! SINGLE WebServer/Socket server - capable of long polling
	// fallback
	private static final long serialVersionUID = 1L;

	public final static Logger log = LoggerFactory.getLogger(WebGUI.class);

	@Element
	public Integer port = 7777;
	@Element
	boolean autoStartBrowser = true;
	@Element
	boolean useLocalResources = true;
	@Element
	public String startURL = "http://127.0.0.1:%d/index.html";
	@Element
	public String root = "resource";

	public int messages = 0;

	transient WSServer wss;

	public void autoStartBrowser(boolean autoStartBrowser) {
		this.autoStartBrowser = autoStartBrowser;
	}

	public HashMap<String, String> clients = new HashMap<String, String>();

	public WebGUI(String n) {
		super(n);
		// first message web browser client is getRegistry
		// so we want it routed back here to deliver to client
		subscribe(Runtime.getInstance().getIntanceName(), "getRegistry");
		load();
	}

	public Integer getPort() {
		return port;
	}

	public Integer setPort(Integer port) {
		this.port = port;
		return port;
	}

	public void restart() {
		stop();
		startWebSocketServer(port);
	}

	/**
	 * determines if references to JQuery JavaScript library are local or if the
	 * library is linked to using content delivery network. Default (false) is
	 * to use the CDN
	 * 
	 * @param b
	 */
	public void useLocalResources(boolean b) {
		useLocalResources = b;
	}

	/**
	 * @return whether instance is using CDN for delivery of JavaScript
	 *         libraries to browser
	 */
	public boolean useLocalResources() {
		return useLocalResources;
	}

	/**
	 * @param port
	 *            - port to start server on default is 7778
	 * @return - true if successfully started
	 */
	public boolean startWebSocketServer(Integer port) {
		try {

			this.port = port;

			if (wss != null) {
				wss.stop();
			}

			wss = new WSServer(this, port);
			wss.start();
			return true;
		} catch (Exception e) {
			Logging.logException(e);
		}

		return false;
	}

	/**
	 * starts and web socket server, auto launches browser if
	 * autoStartBrowser=true
	 * 
	 * @return true if both servers started
	 */
	public boolean start() {
		boolean result = startWebSocketServer(port);
		log.info("using local resources is {}", useLocalResources);
		log.info("starting web socket server on port {} result is {}", port, result);
		if (autoStartBrowser) {
			log.info("auto starting default browser");
			BareBonesBrowserLaunch.openURL(String.format(startURL, port));
		}
		if (!result) {
			warn("could not start properly");
		}
		return result;
	}

	public void stop() {
		try {
			if (wss != null) {
				wss.stop();
				wss = null;
			}
		} catch (Exception e) {
			Logging.logException(e);
		}
	}

	@Override
	public String getDescription() {
		return "The new web enabled GUIService 2.0 !";
	}

	public void startService() {
		if (!isRunning()) {
			super.startService();
			start();
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		stop();
	}

	/**
	 * called by the framework pre process of messages so that data can be
	 * routed back to the correct subcomponent and control of the WebGUI can
	 * still be maintained like a "regular" service
	 * 
	 */
	public boolean preProcessHook(Message m) {
		// FIXME - problem with collisions of this service's methods
		// and dialog methods ?!?!?

		// if the method name is == to a method in the GUIService
		if (methodSet.contains(m.method)) {
			// process the message like a regular service
			return true;
		}

		// otherwise send the message to the dialog with the senders name
		sendToAll(m);
		return false;
	}

	/**
	 * expanding of all resource data from WebGUI onto the file system so that
	 * it may be customized by the user
	 */
	public void customize() {
		try {
			Zip.extractFromFile("./myrobotlab.jar", "./resource", "resource");
		} catch (Exception e) {
			Logging.logException(e);
		}
	}

	// FIXME - take out of RESTProcessor - normalize
	/**
	 * Encodes MyRobotLab message into JSON so that it can be sent over
	 * websockets to listening clients
	 * 
	 * @param msg
	 *            message to be encoded
	 * @return message encoded as JSON (gson) string
	 */
	public String toJson(Message msg) {
		try {
			// GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();

			/*
			 * Gson gson = new GsonBuilder() .registerTypeAdapter(Id.class, new
			 * IdTypeAdapter()) .serializeNulls()
			 * .setDateFormat(DateFormat.LONG)
			 * .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			 * .setPrettyPrinting() .setVersion(1.0) .create();
			 */
			// http://google-gson.googlecode.com/svn/tags/1.2.3/docs/javadocs/com/google/gson/GsonBuilder.html#setDateFormat(int)
			// PRETTY PRINTING IS AWESOME ! MAKE CONFIGURABLE - PRETTY PRINT
			// ONLY WORKS IN TEXTMODE .setPrettyPrinting()
			// .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
			// gson.setDateFormat(DateFormat.FULL);
			/*
			 * REMOVED RECENTLY out = new ByteArrayOutputStream(); // FIXME -
			 * threadsafe? singleton? JsonWriter writer = new JsonWriter(new
			 * OutputStreamWriter(out, "UTF-8")); // FIXME - threadsafe?
			 * singleton? gson.toJson(msg, Message.class, writer);
			 */
			// writer.setIndent("  "); // TODO config driven - very cool !
			// writer.beginArray();

			String ret = Encoder.gson.toJson(msg, Message.class);
			// log.info(ret);
			// for (Message message : messages) {
			// gson.toJson(message, Message.class, writer);
			// }
			// writer.endArray();

			// writer.close();
			return ret;
		} catch (Exception e) {
			Logging.logException(e);
		}
		return null;
	}

	/**
	 * sends JSON encoded MyRobotLab Message to all clients currently connected
	 * through web sockets
	 * 
	 * @param msg
	 *            message to broadcast
	 */
	public void sendToAll(Message msg) {
		++messages;
		String json = toJson(msg);
		log.debug(String.format("webgui ---to---> all clients [%s]", json));
		if (messages % 500 == 0) {
			info(String.format("sent %d messages to %d clients", messages, wss.connections().size())); // TODO
																										// modulus
		}

		if (json != null) {
			wss.sendToAll(json);
		} else {
			log.error(String.format("toJson %s.%s is null", msg.name, msg.method));
		}
	}

	public boolean addUser(String username, String password) {
		return BasicSecurity.addUser(username, password);
	}

	public void addConnectListener(Service service) {
		addListener("publishConnect", service.getName(), "onConnect", WebSocket.class);
	}

	public WebSocket publishConnect(WebSocket conn) {
		return conn;
	}

	public void addWSMsgListener(Service service) {
		addListener("publishWSMsg", service.getName(), "onWSMsg", WSMsg.class);
	}

	public WSMsg publishWSMsg(WSMsg wsmsg) {
		return wsmsg;
	}

	public void addDisconnectListener(Service service) {
		addListener("publishDisconnect", service.getName(), "onDisconnect", WebSocket.class);
	}

	public WebSocket publishDisconnect(WebSocket conn) {
		return conn;
	}

	public boolean allowDirectMessaging(boolean b) {
		wss.allowDirectMessaging(b);
		return b;
	}

	// ============== security begin =========================
	// FIXME - this will have to be keyed by the service name
	// if the global datastructures are to be in Security

	// specifically for a gateway
	// this interface should be incorporated into Security Service

	// interesting - regular expresion matching .. its a combined key !
	// Service.method or perhaps sender.Service.method ?

	private HashSet<String> allowMethods = new HashSet<String>();
	private HashSet<String> excludeMethods = new HashSet<String>();

	private HashSet<String> allowServices = new HashSet<String>();
	private HashSet<String> excludeServices = new HashSet<String>();

	@Override
	public boolean isAuthorized(Message msg) {
		String method = msg.method;
		String service = msg.name;

		if (allowMethods.size() > 0 && !allowMethods.contains(method)) {
			return false;
		}

		if (excludeMethods.size() > 0 && excludeMethods.contains(method)) {
			return false;
		}

		return true;
	}

	public void allowREST(Boolean b) {
		if (wss != null) {
			wss.allowREST(b);
		}
	}

	public void allowMethod(String method) {
		allowMethods.add(method);
	}

	@Override
	public boolean isAuthorized(HashMap<String, String> security, String serviceName, String method) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowExport(String serviceName) {
		// TODO Auto-generated method stub
		return false;
	}
	// ============== security end =========================
	
	public Status test(){
		Status status = super.test();
		
		try {
			
			// test re-entrant starting
			WebGUI webgui = (WebGUI)Runtime.start(getName(),"WebGUI");
			
		} catch(Exception e){
			status.addError(e);
		}
		
		return status;
	}

	public static void main(String[] args) {
		LoggingFactory.getInstance().configure();
		LoggingFactory.getInstance().setLevel(Level.DEBUG);
	
		WebGUI webgui = (WebGUI)Runtime.start("webgui","WebGUI");
		
		webgui.test();
		// webgui.useLocalResources(true);
		// webgui.autoStartBrowser(false);
		// Runtime.createAndStart("webgui", "WebGUI");
		// webgui.useLocalResources(true);

	}
}
