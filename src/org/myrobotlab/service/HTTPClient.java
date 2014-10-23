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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

/**
 * @author greg (at) myrobotlab.org wrapper service for Apache HTTPClient
 */
public class HTTPClient extends Service {

	public final static Logger log = LoggerFactory.getLogger(HTTPClient.class.getCanonicalName());
	private static final long serialVersionUID = 1L;

	transient private DefaultHttpClient client = new DefaultHttpClient();

	public HTTPClient(String n) {
		super(n);
	}

	public static String parse(String in, String beginTag, String endTag) {
		if (in == null) {
			return null;
		}
		int pos0 = in.indexOf(beginTag);
		int pos1 = in.indexOf(endTag, pos0);

		String ret = in.substring(pos0 + beginTag.length(), pos1);
		ret = ret.replaceAll("<br />", "");
		return ret;

	}

	public byte[] post(String uri) {
		return post(uri, null);
	}

	public byte[] post(String uri, HashMap<String, String> fields) {
		HttpPost post = new HttpPost(uri);
		try {

			if (fields != null){
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(fields.size());
				for (String name: fields.keySet())
				{
					nameValuePairs.add(new BasicNameValuePair(name, fields.get(name)));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
			}
			
			
			HttpResponse response = client.execute(post);

			return getResponse(response);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getResponse(HttpResponse response) {
		try {
			InputStream is = response.getEntity().getContent();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			return buffer.toByteArray();
		} catch (Exception e) {
			Logging.logException(e);
		}

		return null;

	}

	public byte[] get(String uri) {

		try {
			HttpGet request = new HttpGet(uri);
			HttpResponse response = client.execute(request);
			return getResponse(response);

		} catch (Exception e) {
			Logging.logException(e);
		}

		return null;
	}

	@Override
	public String getDescription() {
		return "an HTTP client, used to fetch information on the web";
	}

	public static void main(String[] args) {
		LoggingFactory.getInstance().configure();
		LoggingFactory.getInstance().setLevel(Level.INFO);

		HTTPClient client = (HTTPClient) Runtime.createAndStart("client", "HTTPClient");

		String google = new String(client.get("http://www.google.com/"));
		log.info(google);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("p", "apple");
		google = new String(client.post("http://www.google.com", params));
		log.info(google);
		
		
	}

}
