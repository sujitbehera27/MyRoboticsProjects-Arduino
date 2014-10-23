package org.myrobotlab.opencv;

import static com.googlecode.javacv.cpp.opencv_highgui.cvEncodeImage;
import static org.myrobotlab.opencv.VideoProcessor.INPUT_KEY;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.service.data.Point2Df;
import org.myrobotlab.service.data.Rectangle;
import org.simpleframework.xml.Root;
import org.slf4j.Logger;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This is the data returned from a single pass of an OpenCV pipeline of
 * filters. The amount of data can be changed depending on individual
 * configuration of the filters. The filters had some limited ability to add a
 * copy of the image and add other data structures such as arrays of point,
 * bounding boxes, masks and other information.
 * 
 * The default behavior is to return the data from the LAST FILTER ON THE
 * PIPELINE
 * 
 * Some optimizations are done by saving the results of type conversions. For
 * example if a JPG is asked for it is saved back into the data map, so that if
 * its asked again, the cached copy will be returned
 * 
 * All data is put in with keys with the following format
 * [ServiceName].[FilterName].[Format].[Data Type] - e.g.
 * opencv.PyramidDown.jpg.Bytes -- lame re-work ByteArray
 * 
 * choices of images are "by filter name", the "input", the display, and the
 * "last filter" == "output" choices of return types are IplImage, CVMat,
 * BufferedImage, ByteBuffer, ByteArrayOutputStream, byte[]
 * 
 * method naming conventions (get|set) (display | input | filtername) (format -
 * IplImage=image CVMat | BufferedImage | ByteBuffer | Bytes
 * 
 * internal keys - there are several expected keys - they are
 * 
 * input    IplImage image = [[servicename].input]
 * input    IplImage image = [[servicename].input].display
 * 
 * filter   IplImage image = [[servicename].[filtername]]
 * display  IplImage image = [[servicename].[filtername]].display
 *
 * optional type keys
 * filter   IplImage image = [[servicename].[filtername]].bufferedImage
 * display  IplImage image = [[servicename].[filtername]].display.bufferedImage
 * filter   IplImage image = [[servicename].[filtername]].jpg.bytes
 * display  IplImage image = [[servicename].[filtername]].display.jpg.bytes
 * 
 * @author GroG
 * 
 */
@Root
public class OpenCVData implements Serializable {

	private static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(OpenCVData.class);

	//@ElementMap(entry = "data", key = "key", value = "data", attribute = true, inline = true, required = false)
	
	// THIS IS A COPY OF SOURCES !!! - which means this is a copy of the references of
	// SOURCES !!!
	private HashMap<String, Object> data = new HashMap<String, Object>();

	// TODO add KEY_INPUT .. take away from OpenCV
	public static final String KEY_DEPTH = "depth";
	public static final String KEY_JPG = "jpg";
	public static final String KEY_BYTES = "bytes";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_BUFFERED_IMAGE = "bufferedImage";
	
	/**
	 * return type - an ArrayList<Rectangles>
	 */
	public static final String KEY_BOUNDING_BOXES = "boundingBoxes";

	/**
	 * return type - IplImage - either references original filtername IplImage or a 
	 * filter.display() processes IplImage
	 */
	public static final String KEY_DISPLAY = "display"; 
	// public final static String DEPTH_KEY = "depth";
	// Bytes
	
	private String serviceName;

	/**
	 * the filter's name - used as a key to get or put data associated with a
	 * specific filter
	 */
	private String inputFilterName = INPUT_KEY;
	private String selectedFilter = INPUT_KEY;
	private String displayFilterName = INPUT_KEY;
 	private long timestamp;
	private int frameIndex;

	/**
	 * constructed by the 'name'd service
	 * 
	 * @param serviceName
	 */
	public OpenCVData() {
		this(null, 0);
	}

	public OpenCVData(String serviceName, int frameIndex) {
		this.serviceName = serviceName;
		this.timestamp = System.currentTimeMillis();
		this.frameIndex = frameIndex;
	}

	/**
	 * sets the key - used to access the various data of a particular filter -
	 * first set the filter name the access images, points, etc
	 * 
	 * @param name
	 */
	public void setSelectedFilterName(String name) {
		this.selectedFilter = name;
	}

	/**
	 * sets the selected filter name in the OpenCVData structure provisioned
	 * later to save entire filter? or parts ?
	 * 
	 * @param inFilter
	 */
	public void setFilter(OpenCVFilter inFilter) {
		this.selectedFilter = inFilter.name;
	}

	public String getSelectedFilterName() {
		return selectedFilter;
	}

	public int getWidth() {
		return getImage().width();
	}

	public int getHeight() {
		return getImage().height();
	}

	// -------- IplImage begin ----------------

	/**
	 * OpenCV VideoProcessor will set this data collection to the last
	 * filtername - when asked for an "image" it will give the last filter's
	 * 
	 * @return the filter's IplImage
	 */

	public IplImage getImage(String filtername) {
		String key = String.format("%s.%s", serviceName, filtername);
		return ((IplImage) data.get(key));
	}

	/**
	 * parameterless tries to retrieve image based on current filtername
	 * 
	 * @return
	 */
	public IplImage getImage() {
		return getImage(selectedFilter);
	}

	/**
	 * get the original "camera" image - or the image which started the pipeline
	 * 
	 * @return
	 */
	public IplImage getInputImage() {
		return getImage(inputFilterName);
	}

	public IplImage getDepthImage() {
		return getImage(String.format("%s.%s.%s", serviceName, selectedFilter, KEY_DEPTH));
	}

	/**
	 * the main and typically first image data put into the OpenCVData object
	 * 
	 * @param key
	 * @param image
	 */
	public void put(String fullkey, IplImage image) {
		data.put(fullkey, image);
	}

	// -------- IplImage end ----------------

	// -------- BufferedImage begin ----------------
	
	// ---------- BufferedImage begin ------------
	public BufferedImage getDisplayBufferedImage() {
		return getBufferedImage(displayFilterName, KEY_DISPLAY);
	}
	
	public BufferedImage getInputBufferedImage() {
		return getBufferedImage(inputFilterName, null);
	}
	
	public BufferedImage getBufferedImage() {
		return getBufferedImage(selectedFilter, null);
	}

	/**
	 * FIXME (FIX OTHERS) NEEDS TO BE ONE AND ONLY ONE TYPE PROCESSOR LIKE THIS ONE !!!! 
	 * WITH SAME SUBKEY SIGNATURE
	 * lowest level - full key path always required 
	 * @param filterName
	 * @return
	 */
	public BufferedImage getBufferedImage(String filterName, String subkey) {
		String bufferedImageKey;
		if (subkey != null){
			bufferedImageKey = String.format("%s.%s.%s.%s", serviceName, filterName, subkey, KEY_BUFFERED_IMAGE);
		} else {
			bufferedImageKey = String.format("%s.%s.%s", serviceName, filterName, KEY_BUFFERED_IMAGE);
		}
		
		if (data.containsKey(bufferedImageKey)) {
			return (BufferedImage) data.get(bufferedImageKey);
		} else {
			String imgKey;
			
			if (subkey != null){
				imgKey = String.format("%s.%s.%s", serviceName, filterName, subkey);
			} else {
				imgKey = String.format("%s.%s", serviceName, filterName);
			}
			
			IplImage img = (IplImage) data.get(imgKey);

			BufferedImage image = img.getBufferedImage();
			data.put(bufferedImageKey, image);
			return image;
		}
	}
	
	// ---------- BufferedImage end ------------

	// -------- ByteBuffer begin ----------------
	public ByteBuffer getByteBufferImage(String filtername) {
		IplImage img = getImage(filtername);
		return img.getByteBuffer();
	}

	// -------- ByteBuffer end ----------------

	// -------- JPG to file begin ----------------
	public String writeDisplay() {
		return writeImage(selectedFilter, KEY_DISPLAY, null);
	}

	public String writeInput() {
		return writeImage(INPUT_KEY, null, null);
	}

	public String writeImage() {
		return writeImage(selectedFilter, null, null);
	}

	public String writeImage(String filter, String subkey, String format) {
		String filename = null;
		if (format == null) {
			format = "jpg";
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			BufferedImage bi = getBufferedImage(filter, subkey);
			if (bi == null)
				return null;
			// FIXME OPTIMIZE - USE CONVERT & OPENCV !!!
			ImageIO.write(bi, format, baos);
			filename = String.format("%s.%s.%d.%s", serviceName, filter, frameIndex, format);
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(baos.toByteArray());
			fos.close();

		} catch (IOException e) {
			Logging.logException(e);
		}

		return filename;
	}

	// FIXME FIXME FIXME - always push result back into data structure
	public byte[] getJPGBytes(String filterName) {
		String key = String.format("%s.%s.jpg.Bytes", serviceName, filterName);
		if (data.containsKey(key)) {
			return (byte[]) data.get(key);
		}

		CvMat mat = getJPG(filterName);

		ByteBuffer byteBuffer = mat.getByteBuffer();
		byte[] barray = new byte[byteBuffer.remaining()];
		byteBuffer.get(barray);
		data.put(key, barray);
		return barray;
	}

	public ByteBuffer getJPGByteBuffer(String filterName) {
		CvMat mat = getJPG(filterName);
		ByteBuffer byteBuffer = mat.getByteBuffer();
		return byteBuffer;
	}

	// WTF ??
	public CvMat getJPG(String filterName) {
		// FIXME FIXME FIXME - before doing ANY CONVERSION EVER - ALWAYS CHECK
		// CACHE !!
		CvMat mat = getEncoded(filterName, ".jpg");
		return mat;
	}

	public CvMat getEncoded(String filterName, String encoding) {

		// should you go to CvMat ?? - or ByteBuffer ???
		String key = String.format("%s.%s.%s", serviceName, filterName, encoding);
		if (data.containsKey(key)) {
			return (CvMat) data.get(key);
		} else {
			IplImage img = getImage(filterName);
			if (img == null)
				return null;

			try {
				String e = encoding.toLowerCase();
				CvMat encodedImg = cvEncodeImage(e, img);
				return encodedImg;
				/*
				 * 
				 * ByteBuffer byteBuffer = encodedImg.getByteBuffer(); byte[]
				 * barray = new byte[byteBuffer.remaining()];
				 * byteBuffer.get(barray); log.info(String.format("%d size",
				 * barray.length));
				 * 
				 * FileOutputStream fos = new
				 * FileOutputStream("memoryEncoded.jpg"); fos.write(barray);
				 * fos.close();
				 * 
				 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 * bos.write(encodedImg.data_ptr().getStringBytes()); byte[] b =
				 * bos.toByteArray(); log.info("%d size", barray.length);
				 */

			} catch (Exception e) {
				Logging.logException(e);
			}

			/*
			 * cvSaveImage("direct.jpg", img); cvSaveImage("direct.png", img);
			 */

			/*
			 * ByteBuffer bb = encodedImg.asByteBuffer();
			 * 
			 * byte[] b = new byte[bb.remaining()]; bb.get(b);
			 * 
			 * data.put(String.format("%s.JPG", filterName), b);
			 */
			return null;
		}

	}

	// -------- JPG to file end ----------------
	// -------- HashMap begin ----------------

	public boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	// -------- HashMap end ----------------

	// // -----------continue------------------
	@SuppressWarnings("unchecked")
	public void put(Rectangle boundingBox) {

		// String key = String.format("%s.%s", filterName,
		// KEY_BOUNDING_BOX_ARRAY);
		String key = String.format("%s.%s.%s", serviceName, selectedFilter, KEY_BOUNDING_BOXES);
		ArrayList<Rectangle> list;
		if (!data.containsKey(key)) {
			list = new ArrayList<Rectangle>();
			data.put(key, list);
		} else {
			list = (ArrayList<Rectangle>) data.get(key);
		}

		list.add(boundingBox);
	}

	public ArrayList<Rectangle> getBoundingBoxArray() {
		String key = String.format("%s.%s.boundingboxes", serviceName, selectedFilter);
		if (data.containsKey(key)) {
			return (ArrayList<Rectangle>) data.get(key);
		} else {
			return null;
		}
	}

	public void set(ArrayList<Point2Df> pointsToPublish) {
		data.put(String.format("%s.points", selectedFilter), pointsToPublish);
	}

	public ArrayList<Point2Df> getPoints() {
		return (ArrayList<Point2Df>) data.get(String.format("%s.points", selectedFilter));
	}

	public Point2Df getFirstPoint() {
		ArrayList<Point2Df> points = (ArrayList<Point2Df>) data.get(String.format("%s.points", selectedFilter));
		if (points != null && points.size() > 0)
			return points.get(0);
		return null;
	}

	public boolean containsAttribute(String name) {
		return data.containsKey(String.format("%s.attribute.%s", selectedFilter, name));
	}

	public void put(ArrayList<Rectangle> bb) {
		data.put(String.format("%s.%s.boundingboxes", serviceName, selectedFilter), bb);
	}

	public Integer getX() {
		return (Integer) data.get(String.format("%s.x", selectedFilter));
	}

	public void setX(int x) {
		data.put(String.format("%s.x", selectedFilter), x);
	}

	public Integer getY() {
		return (Integer) data.get(String.format("%s.y", selectedFilter));
	}

	public void setY(int y) {
		data.put(String.format("%s.y", selectedFilter), y);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * public ArrayList<SerializableImage> crop() { return
	 * cropBoundingBoxArray(String.format(filtername)); }
	 */

	/*
	 * public ArrayList<SerializableImage> cropBoundingBoxArray() { return
	 * cropBoundingBoxArray(filtername); }
	 */

	/*
	 * public ArrayList<IplImage> cropBoundingBoxArray(String key) { IplImage
	 * img = getImage(key); ArrayList<Rectangle> bbxs = getBoundingBoxArray();
	 * ArrayList<SerializableImage> ret = new ArrayList<SerializableImage>(); if
	 * (bbxs != null) { for (int i = 0; i < bbxs.size(); ++i) { Rectangle r =
	 * bbxs.get(i); //ret.add(new
	 * SerializableImage(img.getImage().getSubimage(r.x, r.y, r.width,
	 * r.height), filtername)); // expand to use pixel values - int width =
	 * img.width(); int height = img.height(); int sx = (int)(r.x * width); int
	 * sy = (int)(r.y * height); int swidth = (int)(r.width * width); int
	 * sheight = (int)(r.height * height); ret.add(new
	 * SerializableImage(deepCopy(img.getImage()).getSubimage(sx, sy, swidth,
	 * sheight), filtername)); } } return ret; }
	 */

	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/*
	 * public ArrayList<SerializableImage> cropPoints() { return cropPoints(); }
	 * 
	 * public ArrayList<SerializableImage> cropPoints(String key) {
	 * SerializableImage img = getImage(key); ArrayList<Point2Df> pts =
	 * getPoints(); ArrayList<SerializableImage> ret = new
	 * ArrayList<SerializableImage>(); int x = 0; int y = 0; if (pts != null) {
	 * for (int i = 0; i < pts.size(); ++i) { Point2Df p = pts.get(i); x =
	 * (int)(p.x * (float)img.getWidth() - 55); y = (int)(p.y *
	 * (float)img.getWidth() - 55); ret.add(new
	 * SerializableImage(img.getImage().getSubimage(x,y,x+(2*55),y+(2*55)),
	 * filtername)); } } return ret; }
	 */

	/*
	 * public void saveToDirectory(String folderName) { File f = new
	 * File(folderName); f.mkdirs(); for (Map.Entry<String, Object> d :
	 * data.entrySet()) { // Map.Entry<String,SerializableImage> pairs = o;
	 * String key = d.getKey(); Object o = d.getValue();
	 * log.error(String.format("saving %s of type %s", key,
	 * o.getClass().getSimpleName())); try { if (o.getClass() ==
	 * SerializableImage.class) { SerializableImage img = (SerializableImage)o;
	 * String imageFile = String.format("%s%s%d.%s.png", folderName,
	 * File.separator, timestamp, img.getSource());
	 * ImageIO.write(img.getImage(), "png",new File(imageFile)); } else if
	 * (o.getClass() == ArrayList.class){
	 * 
	 * // FIXME - not exact ArrayList<SerializableImage> dump = crop(); for (int
	 * i = 0; i < dump.size(); ++i) { SerializableImage img = dump.get(i);
	 * String imageFile = String.format("%s%s%d.%s.%d.png", folderName,
	 * File.separator, timestamp, img.getSource(), i);
	 * ImageIO.write(img.getImage(), "png",new File(imageFile)); } } } catch
	 * (Exception e) { Logging.logException(e); } }
	 * 
	 * }
	 */

	public String makeKey(String attributeName) {
		return String.format("%s.%s", selectedFilter, attributeName);
	}

	public void setAttribute(String key, Object value) {
		data.put(makeKey(key), value);
	}

	public Object getAttribute(String name) {
		String key = makeKey(name);
		if (data.containsKey(key)) {
			return data.get(key);
		}
		return null;
	}

	public void setDisplayFilterName(String displayFilterName) {
		this.displayFilterName = displayFilterName;
	}

	public IplImage getDisplay() {
		String key = String.format("%s.%s", serviceName, displayFilterName);
		if (data.containsKey(key)) {
			return (IplImage) data.get(key);
		}
		return null;
	}

	public String getDisplayFilterName() {
		return displayFilterName;
	}

	public void putAll(HashMap<String,Object> sources){
		data.putAll(sources);
	}

	public void setInputFilterName(String inputFilterName) {
		this.inputFilterName = inputFilterName;
	}
	
	/**
	 * FIXME implement
	 * @param name
	 * @return
	 */
	public OpenCVFilter getFilter(String name){
		return null;
	}
	
	public void logKeySet(){
		for (Map.Entry<String, Object> o : data.entrySet()) {
			log.info(o.getKey());
		}
	}
}
