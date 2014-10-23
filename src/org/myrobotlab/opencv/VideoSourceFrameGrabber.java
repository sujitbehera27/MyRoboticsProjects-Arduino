package org.myrobotlab.opencv;

import java.util.LinkedList;

import org.myrobotlab.image.SerializableImage;
import org.myrobotlab.logging.LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class VideoSourceFrameGrabber extends FrameGrabber {

	/*
	 * excellent reference - http://www.jpegcameras.com/ foscam url
	 * http://host/videostream.cgi?user=username&pwd=password
	 * http://192.168.0.59:60/videostream.cgi?user=admin&pwd=password android ip
	 * cam http://192.168.0.57:8080/videofeed
	 */

	LinkedList<SerializableImage> imgq = new LinkedList<SerializableImage>();

	public final static Logger log = LoggerFactory.getLogger(VideoSourceFrameGrabber.class.getCanonicalName());

	public VideoSourceFrameGrabber(String name) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void trigger() throws Exception {
	}

	@Override
	public IplImage grab() {

		IplImage image = null;
		synchronized (imgq) {

			while (image == null) { // while no messages && no messages that are
									// blocking
				if (imgq.size() == 0) {
					try {
						imgq.wait();
					} catch (InterruptedException e) {
					} // must own the lock
				} else {
					image = IplImage.createFrom(imgq.removeLast().getImage());
				}
			}
			imgq.notifyAll();
		}

		return image;
	}

	int maxQueue = 100;

	public void add(SerializableImage image) {

		synchronized (imgq) {
			if (imgq.size() > maxQueue) {
				log.warn(String.format("Image Source BUFFER OVERRUN size %d dropping frames", imgq.size()));
				try {
					// FIXME ??? it's not nice to keep the inbound thread
					// waiting No ???
					imgq.wait();
				} catch (InterruptedException e) {
				}
			} else {
				imgq.addFirst(image);
				imgq.notifyAll(); // must own the lock
			}
		}

	}

	@Override
	public void release() throws Exception {
	}

}
