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

package org.myrobotlab.opencv;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.image.BufferedImage;

import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.slf4j.Logger;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OpenCVFilterAnd extends OpenCVFilter {

	private static final long serialVersionUID = 1L;

	public final static Logger log = LoggerFactory.getLogger(OpenCVFilterAnd.class.getCanonicalName());

	transient IplImage and = null;
	
	public OpenCVFilterAnd() {
		super();
	}

	public OpenCVFilterAnd(String name) {
		super(name);
	}

	public void loadMask(String filename) {
		try {
			and = cvLoadImage(filename, CV_LOAD_IMAGE_GRAYSCALE);
			/*
			BufferedImage img = ImageIO.read(new File(filename));
			mask = IplImage.createFrom(img);
			*/
		} catch (Exception e) {
			Logging.logException(e);
		}
	}
	
	public void loadMask(BufferedImage mask){
		this.and = IplImage.createFrom(mask);
	}
	
	public void loadMask(IplImage mask){
		this.and = mask;
	}
	
	

	@Override
	public IplImage process(IplImage image, OpenCVData data) {
		
		
		//IplImage img = cvLoadImage("data/boldt.jpg", CV_LOAD_IMAGE_GRAYSCALE);
		
		if (and != null) {
			
			
			//cvConvertScale(arg0, arg1, arg2, arg3)
			//cvConvertScale(tfaceImg32, faceImg8, 255);
			//cvSetImageROI(image, cvRect(0, 0, mask.width(), mask.height()));
			//CvMat mask = CvMat.create(mask, mask.height(), CV_8U);
			//cvAnd(image, mask, buffer, null); // use mask instead?
			//cvAnd(src1, src2,  result, mask);
			
			cvSetImageROI(image, cvRect(0, 0, and.width(), and.height()));
			//cvAnd(image, and, buffer, null); // use mask instead?
			cvAnd(image, and, image, null); // use mask instead?
			cvResetImageROI(image);
			
			
			/*
			CanvasFrame canvas = new CanvasFrame("My Image", 1);
			canvas.showImage(and);
			
			CanvasFrame canvas2 = new CanvasFrame("My Image 2", 1);
			canvas2.showImage(buffer);

			*/
			// working example
			// cvAnd(image, negativeImage, buffer, null); - both were clones of image negativeImage was an cvNot inverse
		}

		return image;
	}

	@Override
	public void imageChanged(IplImage image) {
		// TODO Auto-generated method stub

	}

}
