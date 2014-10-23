package org.myrobotlab.opencv;

import static com.googlecode.javacv.cpp.opencv_core.CV_FONT_HERSHEY_PLAIN;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPutText;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SimpleTimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.myrobotlab.framework.Service;
import org.myrobotlab.image.SerializableImage;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.service.OpenCV;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.slf4j.Logger;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.OpenKinectFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

@Root
public class VideoProcessor implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;

	public final static Logger log = LoggerFactory.getLogger(VideoProcessor.class);

	int frameIndex = 0;
	public boolean capturing = false;

	// GRABBER BEGIN --------------------------
	@Element
	public String inputSource = OpenCV.INPUT_SOURCE_CAMERA;
	@Element
	public String grabberType = "com.googlecode.javacv.OpenCVFrameGrabber";

	// OpenCVFilter displayFilter = null;

	// grabber cfg
	@Element(required = false)
	public String format = null;
	@Element
	public boolean getDepth = false;
	@Element
	public int cameraIndex = 0;
	@Element
	public String inputFile = "http://localhost/videostream.cgi";
	@Element(required = false)
	public String pipelineSelected = "";
	@Element
	public boolean publishOpenCVData = true;
	// GRABBER END --------------------------
	// DEPRECATED - always use blocking queue
	// public boolean useBlockingData = false;
	transient CvFont font = new CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);

	// DEPRECATED deemed a bad idea - non blocking
	// use getOpenCVData
	// OpenCVData lastData = null;

	StringBuffer frameTitle = new StringBuffer();

	OpenCVData data = null;

	// FIXME - more than 1 type is being used on this in more than one context
	// BEWARE !!!!
	// FIXME - use for RECORDING & another one for Blocking for data !!!
	public BlockingQueue<Object> blockingData = new LinkedBlockingQueue<Object>();

	/**
	 * map of video sources - allows filters to process any named source
	 */
	transient VideoSources sources = new VideoSources();

	private transient OpenCV opencv;
	private transient FrameGrabber grabber = null;
	transient Thread videoThread = null;

	private ArrayList<OpenCVFilter> filters = new ArrayList<OpenCVFilter>();

	transient SimpleDateFormat sdf = new SimpleDateFormat();

	transient HashMap<String, FrameRecorder> outputFileStreams = new HashMap<String, FrameRecorder>();

	public static final String INPUT_KEY = "input";

	public String boundServiceName;

	/**
	 * selected display filter unselected defaults to input
	 */
	public String displayFilterName = INPUT_KEY;

	transient IplImage frame;

	private int minDelay = 0;

	/**
	 * creates a copy of the frame data leaving the original data unmarked
	 */
	public boolean forkDisplay = false;

	private boolean recordOutput = false;
	private boolean closeOutputs = false;
	public String recordingSource = INPUT_KEY;

	private boolean showFrameNumbers = true;

	private boolean showTimestamp = true;

	/**
	 * Although OpenCVData might be publishing, this determines if a display is
	 * to be published. In addition to this a specific filter name is needed, if
	 * the filter name does not exist - input will be displayed
	 */
	public boolean publishDisplay = true;

	public VideoProcessor() {
		// parameterless constructor for simple xml
	}

	public OpenCV getOpencv() {
		return opencv;
	}

	/*
	 * DEPRECATED - use getOpenCVData() public OpenCVData getLastData() { return
	 * lastData; }
	 */

	// FIXME - cheesy initialization - put it all in the constructor or before
	// I assume this was done because the load() is difficult to manage !!
	public void setOpencv(OpenCV opencv) {
		this.opencv = opencv;
		this.boundServiceName = opencv.getName();
	}

	public void start() {
		log.info("starting capture");
		sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
		sdf.applyPattern("dd MMM yyyy HH:mm:ss z");

		if (videoThread != null) {
			log.info("video processor already started");
			return;
		}
		videoThread = new Thread(this, String.format("%s_videoProcessor", opencv.getName()));
		videoThread.start();
	}

	public void stop() {
		log.debug("stopping capture");
		capturing = false;
		videoThread = null;
	}

	/**
	 * main video processing loop sources is a globally accessible VideoSources
	 * - but is not threadsafe data is thread safe - at least the references to
	 * the data are threadsafe even if the data might not be (although it
	 * "probably" is :)
	 * 
	 * more importantly the references of data are synced with itself - so that
	 * all references are from the same processing loop
	 */
	public void run() {

		capturing = true;

		/*
		 * TODO - check out opengl stuff if (useCanvasFrame) { cf = new
		 * CanvasFrame("CanvasFrame"); }
		 */

		try {

			// inputSource = INPUT_SOURCE_IMAGE_FILE;
			log.info(String.format("video source is %s", inputSource));

			Class<?>[] paramTypes = new Class[1];
			Object[] params = new Object[1];

			// TODO - determine by file type - what input it is

			if (OpenCV.INPUT_SOURCE_CAMERA.equals(inputSource)) {
				paramTypes[0] = Integer.TYPE;
				params[0] = cameraIndex;
			} else if (OpenCV.INPUT_SOURCE_MOVIE_FILE.equals(inputSource)) {
				paramTypes[0] = String.class;
				params[0] = inputFile;
			} else if (OpenCV.INPUT_SOURCE_IMAGE_FILE.equals(inputSource)) {
				paramTypes[0] = String.class;
				params[0] = inputFile;
			} else if (OpenCV.INPUT_SOURCE_PIPELINE.equals(inputSource)) {
				paramTypes[0] = String.class;
				params[0] = pipelineSelected;
			} else if (OpenCV.INPUT_SOURCE_NETWORK.equals(inputSource)) {
				paramTypes[0] = String.class;
				params[0] = inputFile;
			}

			log.info(String.format("attempting to get frame grabber %s format %s", grabberType, format));
			Class<?> nfg = Class.forName(grabberType);
			// TODO - get correct constructor for Capture Configuration..
			Constructor<?> c = nfg.getConstructor(paramTypes);

			grabber = (FrameGrabber) c.newInstance(params);

			if (format != null) {
				grabber.setFormat(format);
			}

			log.info(String.format("using %s", grabber.getClass().getCanonicalName()));

			if (grabber == null) {
				log.error(String.format("no viable capture or frame grabber with input %s", grabberType));
				stop();
			}

			if (grabber != null) {
				grabber.start();
			}

			log.info("wating 300 ms for camera to warm up");
			Service.sleep(300);

		} catch (Exception e) {
			Logging.logException(e);
			stop();
		}
		// TODO - utilize the size changing capabilites of the different
		// grabbers
		// grabbler.setImageWidth()
		// grabber.setImageHeight(320);
		// grabber.setImageHeight(240);

		log.info("beginning capture");

		// keys
		// String inputKey = String.format("%s.%s", boundServiceName,
		// INPUT_KEY);
		// String displayKey = String.format("%s.%s.%s", boundServiceName,
		// INPUT_KEY, OpenCVData.KEY_DISPLAY);

		// String inputFilterName = INPUT_KEY;

		while (capturing) {
			try {

				++frameIndex;
				if (Logging.performanceTiming)
					Logging.logTime("start");

				frame = grabber.grab();

				if (Logging.performanceTiming)
					Logging.logTime(String.format("post-grab %d", frameIndex));

				// log.info(String.format("frame %d", frameIndex));

				if (minDelay > 0) {
					Service.sleep(minDelay);
				}

				if (frame == null) {
					log.warn("frame is null");
					Service.sleep(300); // prevent thrashing
					continue;
				}

				if (getDepth && grabber.getClass() == OpenKinectFrameGrabber.class) {
					sources.put(boundServiceName, OpenCV.SOURCE_KINECT_DEPTH, ((OpenKinectFrameGrabber) grabber).grabDepth());
				}

				// TODO - option to accumulate? - e.g. don't new
				data = new OpenCVData(boundServiceName, frameIndex);

				if (Logging.performanceTiming)
					Logging.logTime("pre-synchronized-filter");
				synchronized (filters) {
					if (Logging.performanceTiming)
						Logging.logTime("post-synchronized-filter");
					Iterator<OpenCVFilter> itr = filters.iterator();

					// setting up INPUT filter
					sources.put(boundServiceName, INPUT_KEY, frame);
					sources.put(boundServiceName, INPUT_KEY, OpenCVData.KEY_DISPLAY, frame);

					while (capturing && itr.hasNext()) {

						OpenCVFilter filter = itr.next();
						if (Logging.performanceTiming)
							Logging.logTime(String.format("pre set-filter %s", filter.name));
						// set the selected filter
						data.setFilter(filter);
						if (Logging.performanceTiming)
							Logging.logTime(String.format("set-filter %s", filter.name));

						// get the source image this filter is chained to
						// should be safe and correct if operating in this
						// service
						// pipeline to another service needs to use data not
						// sources
						IplImage image = sources.get(filter.sourceKey);
						if (image == null) {
							log.warn(String.format("%s has no image - waiting", filter.sourceKey));
							Service.sleep(300);
							continue;
						}

						// pre process for image size & channel changes
						filter.preProcess(frameIndex, image, data);
						if (Logging.performanceTiming)
							Logging.logTime(String.format("preProcess-filter %s", filter.name));
						image = filter.process(image, data); // <- image =
																// filter.process(image,
																// data) <--
																// this means if
																// the
																// filter.process
																// copies and
																// returns a new
																// buffer - it's
																// FORKED
						if (Logging.performanceTiming)
							Logging.logTime(String.format("process-filter %s", filter.name));

						// process the image - push into source as new output
						// other pipelines will pull it off the from the sources
						sources.put(boundServiceName, filter.name, image);
						sources.put(boundServiceName, filter.name, OpenCVData.KEY_DISPLAY, image);

						// no display || merge display || fork display
						// currently there is no "display" in sources
						// i've got a user selection to display a particular
						// filter
						// TODO - future make displayFilterName a set -
						// displayFilters !
						if (publishDisplay && displayFilterName != null && displayFilterName.equals(filter.name)) {
							data.setDisplayFilterName(displayFilterName);

							// The fact that I'm in a filter loop
							// and there is a display to publish means
							// i've got to process a filter's display
							// TODO - would be to have a set of displays if it's
							// needed
							// if displayFilter == null but we are told to
							// display - then display INPUT

							IplImage display;

							if (forkDisplay) {
								IplImage forked = cvCreateImage(cvGetSize(image), image.depth(), image.nChannels());
								cvCopy(image, forked, null);
								display = forked;
								// push reference to fork back in
								sources.put(boundServiceName, filter.name, OpenCVData.KEY_DISPLAY, display);
							} else {
								display = image;
							}

							filter.display(display, data);

							// if display frame
							if (showFrameNumbers || showTimestamp) {

								frameTitle.setLength(0);

								if (showFrameNumbers) {
									frameTitle.append("frame ");
									frameTitle.append(frameIndex);
									frameTitle.append(" ");
								}

								if (showTimestamp) {
									frameTitle.append(System.currentTimeMillis());
								}

								cvPutText(display, frameTitle.toString(), cvPoint(10, 20), font, CvScalar.BLACK);
							}

						} // end of display processing

					} // capturing && itr.hasNext()
					if (Logging.performanceTiming)
						Logging.logTime("filters done");
				} // synchronized (filters)
				if (Logging.performanceTiming)
					Logging.logTime("sync done");

				// copy key references from sources to data
				// the references will presist and so will the data
				// for as long as the OpenCVData structure exists
				// Sources will contain new references to new data
				// next iteration
				data.putAll(sources.getData());

				// has to be 2 tests for publishDisplay
				// one inside the filter loop - to set the display to a new
				// filter
				// and this one to publish - if it is left "unset" then the
				// input becomes the
				// display filter
				if (publishDisplay) {
					SerializableImage display = new SerializableImage(data.getDisplayBufferedImage(), data.getDisplayFilterName(), frameIndex);
					opencv.invoke("publishDisplay", display);
				}

				// publish accumulated data
				if (publishOpenCVData) {
					opencv.invoke("publishOpenCVData", data);
				}

				// this has to be before record as
				// record uses the queue - this has the "issue" if
				// the consumer does not pickup-it will get stale
				if (blockingData.size() == 0) {
					blockingData.add(data);
				}

				if (recordOutput) {
					// TODO - add input, filter, & display
					record(data);
				}

			} catch (Exception e) {
				Logging.logException(e);
				log.error("stopping capture");
				stop();
			}

			if (Logging.performanceTiming)
				Logging.logTime("finished pass");
		} // while capturing

		try {
			grabber.release();
			grabber = null;
		} catch (Exception e) {
			Logging.logException(e);
		}
	}

	// ------- filter methods begin ------------------
	public OpenCVFilter addFilter(String name, String newFilter) {
		String type = String.format("org.myrobotlab.opencv.OpenCVFilter%s", newFilter);
		/*
		 * Object[] params = new Object[1]; params[0] = name;
		 */

		OpenCVFilter filter = (OpenCVFilter) Service.getNewInstance(type, name);
		// returns filter if added - or if dupe returns actual
		return addFilter(filter);
	}

	public OpenCVFilter addFilter(OpenCVFilter filter) {
		// important for filter to access parent data
		// and call-backs
		filter.setVideoProcessor(this);
		synchronized (filters) {

			for (int i = 0; i < filters.size(); ++i) {
				if (filter.name.equals(filters.get(i).name)) {
					log.warn("duplicate filter name {}", filter.name);
					return filters.get(i);
				}
			}

			if (filter.sourceKey == null) {
				filter.sourceKey = String.format("%s.%s", boundServiceName, INPUT_KEY);
				if (filters.size() > 0) {
					OpenCVFilter f = filters.get(filters.size() - 1);
					filter.sourceKey = String.format("%s.%s", boundServiceName, f.name);
				}
			}

			filters.add(filter);
			log.info(String.format("added new filter %s.%s, %s", boundServiceName, filter.name, filter.getClass().getCanonicalName()));
		}

		return filter;
	}

	public void removeFilters() {
		synchronized (filters) {
			filters.clear();
		}
	}

	public void removeFilter(OpenCVFilter inFilter) {
		synchronized (filters) {
			Iterator<OpenCVFilter> itr = filters.iterator();
			while (itr.hasNext()) {
				OpenCVFilter filter = itr.next();
				if (filter == inFilter) {
					itr.remove();
					if (filters.size() - 1 > 0) {
						displayFilterName = filters.get(filters.size() - 1).name;
						log.info("remove and switch displayFilter to {}", displayFilterName);
					}
					return;
				}
			}
		}

		log.error(String.format("removeFilter could not find %s filter", inFilter.name));
	}

	public ArrayList<OpenCVFilter> getFiltersCopy() {
		synchronized (filters) {
			return new ArrayList<OpenCVFilter>(filters);
		}
	}

	public OpenCVFilter getFilter(String name) {

		synchronized (filters) {
			Iterator<OpenCVFilter> itr = filters.iterator();
			while (itr.hasNext()) {
				OpenCVFilter filter = itr.next();
				if (filter.name.equals(name)) {
					return filter;
				}
			}
		}
		log.error(String.format("removeFilter could not find %s filter", name));
		return null;
	}

	// ------- filter methods end ------------------

	/**
	 * thread safe recording of avi
	 * 
	 * @param key
	 *            - input, filter, or display
	 * @param data
	 */
	public void record(OpenCVData data) {
		try {

			if (!outputFileStreams.containsKey(recordingSource)) {
				// FFmpegFrameRecorder recorder = new FFmpegFrameRecorder
				// (String.format("%s.avi",filename), frame.width(),
				// frame.height());

				FrameRecorder recorder = new OpenCVFrameRecorder(String.format("%s.avi", recordingSource), frame.width(), frame.height());
				// recorder.setCodecID(CV_FOURCC('M','J','P','G'));
				// TODO - set frame rate to framerate
				recorder.setFrameRate(15);
				recorder.setPixelFormat(1);
				recorder.start();
				outputFileStreams.put(recordingSource, recorder);
			}

			// TODO - add input, filter & display
			outputFileStreams.get(recordingSource).record(data.getImage(recordingSource));

			if (closeOutputs) {
				OpenCVFrameRecorder output = (OpenCVFrameRecorder) outputFileStreams.get(recordingSource);
				outputFileStreams.remove(output);
				output.stop();
				output.release();
				recordOutput = false;
				closeOutputs = false;
			}

		} catch (Exception e) {
			Logging.logException(e);
		}
	}

	public void recordOutput(Boolean b) {

		if (b) {
			recordOutput = b;
		} else {
			closeOutputs = true;
		}
	}

	public FrameGrabber getGrabber() {
		return grabber;
	}

	public LinkedBlockingQueue<IplImage> requestFork(String filterName, String myName) {
		return null;
	}

	public void setMinDelay(int minDelay) {
		this.minDelay = minDelay;
	}

	public void showFrameNumbers(boolean b) {
		showFrameNumbers = b;
	}

	public void showTimestamp(boolean b) {
		showTimestamp = b;
	}
}
