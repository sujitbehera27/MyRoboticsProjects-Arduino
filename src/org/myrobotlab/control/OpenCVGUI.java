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

package org.myrobotlab.control;

import static org.myrobotlab.opencv.VideoProcessor.INPUT_KEY;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;

import org.myrobotlab.control.opencv.ComboBoxModel;
import org.myrobotlab.control.opencv.OpenCVFilterGUI;
import org.myrobotlab.framework.Service;
import org.myrobotlab.image.SerializableImage;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.opencv.FilterWrapper;
import org.myrobotlab.opencv.OpenCVData;
import org.myrobotlab.opencv.OpenCVFilter;
import org.myrobotlab.opencv.VideoProcessor;
import org.myrobotlab.opencv.VideoSources;
import org.myrobotlab.service.GUIService;
import org.myrobotlab.service.OpenCV;
import org.myrobotlab.service.Runtime;
import org.myrobotlab.service.interfaces.VideoGUISource;
import org.slf4j.Logger;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;

public class OpenCVGUI extends ServiceGUI implements ListSelectionListener, VideoGUISource, ActionListener {

	static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(OpenCVGUI.class.toString());
	public String prefixPath = "com.googlecode.javacv.";

	BasicArrowButton addFilterButton = new BasicArrowButton(BasicArrowButton.EAST);
	BasicArrowButton removeFilterButton = new BasicArrowButton(BasicArrowButton.WEST);

	OpenCVListAdapter popup = new OpenCVListAdapter(this);

	JList<String> possibleFilters;
	JList<String> currentFilters;

	VideoWidget video0 = null;

	JButton capture = new JButton("capture");
	JCheckBox undock = new JCheckBox("undock");

	CanvasFrame cframe = null;//new CanvasFrame("canvas frame");
	
	// input
	JPanel captureCfg = new JPanel();
	JRadioButton fileRadio = new JRadioButton();
	JRadioButton cameraRadio = new JRadioButton();
	JTextField inputFile = new JTextField("");
	JLabel inputFileLable = new JLabel("file");
	JLabel cameraIndexLable = new JLabel("camera");
	JLabel modeLabel = new JLabel("mode");
	JButton inputFileButton = new JButton("open file");

	JComboBox<String> IPCameraType = new JComboBox<String>(new String[] { "foscam FI8918W" });
	DefaultComboBoxModel<String> pipelineHookModel = new DefaultComboBoxModel<String>();
	JComboBox<String> pipelineHook = new JComboBox<String>(pipelineHookModel);

	ButtonGroup groupRadio = new ButtonGroup();
	DefaultListModel<String> currentFilterListModel = new DefaultListModel<String>();

	JComboBox<String> kinectImageOrDepth = new JComboBox<String>(new String[] { "image", "depth", "interleave" });
	JComboBox<String> grabberTypeSelect = null;

	JComboBox<Integer> cameraIndex = new JComboBox<Integer>(new Integer[] { 0, 1, 2, 3, 4, 5 });

	JPanel filterParameters = new JPanel(new BorderLayout());

	LinkedHashMap<String, OpenCVFilterGUI> guiFilters = new LinkedHashMap<String, OpenCVFilterGUI>();

	// output
	JButton recordButton = new JButton("record");
	JButton recordFrameButton = new JButton("record frame");

	OpenCV myOpenCV;
	final OpenCVGUI self;

	public OpenCVGUI(final String boundServiceName, final GUIService myService, final JTabbedPane tabs) {
		super(boundServiceName, myService, tabs);
		self = this;
	}

	public void init() {

		video0 = new VideoWidget(boundServiceName, myService, tabs, false);
		video0.init();
		
		undock.addActionListener(this);

		capture.addActionListener(captureListener);

		ArrayList<String> frameGrabberList = new ArrayList<String>();
		for (int i = 0; i < FrameGrabber.list.size(); ++i) {
			String ss = FrameGrabber.list.get(i);
			String fg = ss.substring(ss.lastIndexOf(".") + 1);
			frameGrabberList.add(fg);
		}

		frameGrabberList.add("IPCamera");
		frameGrabberList.add("Pipeline"); // service which implements
											// ImageStreamSource

		// CanvasFrame cf = new CanvasFrame("hello");

		grabberTypeSelect = new JComboBox(frameGrabberList.toArray());

		kinectImageOrDepth.addActionListener(this);

		possibleFilters = new JList<String>(OpenCV.VALID_FILTERS);
		possibleFilters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		possibleFilters.setSelectedIndex(0);
		possibleFilters.setVisibleRowCount(10);
		possibleFilters.addMouseListener(popup);

		currentFilters = new JList<String>(currentFilterListModel);
		currentFilters.setFixedCellWidth(100);
		currentFilters.addListSelectionListener(this);
		currentFilters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentFilters.setSize(140, 160);
		currentFilters.setVisibleRowCount(10);

		JScrollPane currentFiltersScrollPane = new JScrollPane(currentFilters);
		JScrollPane possibleFiltersScrollPane = new JScrollPane(possibleFilters);

		gc.gridx = 0;
		gc.gridy = 0;
		JPanel videoPanel = new JPanel();
		videoPanel.add(video0.display);
		gc.gridheight = 2;
		display.add(videoPanel, gc);
		// display.add(video0.display, gc);
		gc.gridheight = 1;

		// build input begin ------------------
		JPanel input = new JPanel(new GridBagLayout());

		TitledBorder title;
		title = BorderFactory.createTitledBorder("input");
		input.setBorder(title);

		groupRadio.add(cameraRadio);
		groupRadio.add(fileRadio);

		gc.gridx = 0;
		gc.gridy = 0;

		grabberTypeSelect.addActionListener(grabberTypeListener);

		// capture panel
		JPanel cpanel = new JPanel();
		cpanel.setBorder(BorderFactory.createEtchedBorder());
		cpanel.add(capture);
		cpanel.add(grabberTypeSelect);
		//cpanel.add(new JLabel(" canvas "));
		cpanel.add(undock);
		// build configuration for the various captures
		// non visible - when not applicable
		// disable when capturing
		captureCfg.setBorder(BorderFactory.createEtchedBorder());

		captureCfg.add(cameraRadio);
		captureCfg.add(cameraIndexLable);
		captureCfg.add(cameraIndex);
		captureCfg.add(modeLabel);
		captureCfg.add(kinectImageOrDepth);
		captureCfg.add(fileRadio);
		captureCfg.add(inputFileLable);
		captureCfg.add(inputFile);

		captureCfg.add(IPCameraType);
		captureCfg.add(pipelineHook);

		input.add(cpanel, gc);
		++gc.gridy;
		input.add(captureCfg, gc);

		gc.gridx = 0;
		++gc.gridy;

		gc.gridx = 0;
		gc.gridy = 2;
		display.add(input, gc);

		gc.gridy = 3;

		JPanel output = new JPanel();

		title = BorderFactory.createTitledBorder("output");
		output.setBorder(title);

		display.add(output, gc);
		output.add(recordButton);
		output.add(recordFrameButton);

		recordButton.addActionListener(this);
		recordFrameButton.addActionListener(this);

		// build input end ------------------

		// build filters begin ------------------
		addFilterButton.addActionListener(this);
		removeFilterButton.addActionListener(this);

		JPanel filterPanel = new JPanel();
		title = BorderFactory.createTitledBorder("filters: available - current");
		filterPanel.setBorder(title);
		filterPanel.add(possibleFiltersScrollPane);
		filterPanel.add(removeFilterButton);
		filterPanel.add(addFilterButton);
		filterPanel.add(currentFiltersScrollPane);

		gc.gridx = 1;
		gc.gridy = 0;
		display.add(filterPanel, gc);

		title = BorderFactory.createTitledBorder("filter parameters");
		filterParameters.setBorder(title);
		// filterParameters.setPreferredSize(new Dimension(340, 360));
		filterParameters.setPreferredSize(new Dimension(340, 400));
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridheight = 3;
		display.add(filterParameters, gc);

		setCurrentFilterMouseListener();
		// build filters end ------------------

		// TODO - bury in framework?
		myOpenCV = (OpenCV) Runtime.getService(boundServiceName);

		// TODO - remove action listener?
		grabberTypeSelect.setSelectedItem("OpenCV");

	}

	public void setFilterState(FilterWrapper filterData) {
		if (guiFilters.containsKey(filterData.name)) {
			OpenCVFilterGUI gui = guiFilters.get(filterData.name);
			gui.getFilterState(filterData);
		} else {
			log.error(filterData.name + " does not contain a gui");
		}
	}

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void addFilter() {
		JFrame frame = new JFrame();
		frame.setTitle("add new filter");
		String name = JOptionPane.showInputDialog(frame, "new filter name");

		String type = (String) possibleFilters.getSelectedValue();
		myService.send(boundServiceName, "addFilter", name, type);
		// TODO - block on response - if (myService.send...)

		// addFilterToGUI(name, type);
	}

	public OpenCVFilterGUI addFilterToGUI(String name, OpenCVFilter f) {

		String type = f.getClass().getSimpleName();
		type = type.substring(prefix.length());

		currentFilterListModel.addElement(name);

		// get a gui filter
		String guiType = "org.myrobotlab.control.opencv.OpenCVFilter" + type + "GUI";

		OpenCVFilterGUI filtergui = null;

		// try creating one based on type
		filtergui = (OpenCVFilterGUI) Service.getNewInstance(guiType, name, boundServiceName, myService);
		if (filtergui == null) {
			log.info(String.format("filter %s does not have a gui defined", type));
			filtergui = (OpenCVFilterGUI) Service.getNewInstance("org.myrobotlab.control.opencv.OpenCVFilterDefaultGUI", name, boundServiceName, myService);
			if (filtergui == null) {
				log.error("could not create default filter gui");
				return null;
			}
		}

		// add new input to sources
		ArrayList<String> newSources = f.getPossibleSources();
		// DefaultComboBoxModel model = ComboBoxModel.getModel();
		for (int i = 0; i < newSources.size(); ++i) {
			ComboBoxModel.addElement(name, String.format("%s.%s", boundServiceName, newSources.get(i)));
		}

		// set source of gui's input to

		filtergui.initFilterState(f); // set the bound filter
		guiFilters.put(name, filtergui);
		currentFilters.setSelectedIndex(currentFilterListModel.size() - 1);
		return filtergui;
	}

	public void removeFilterFromGUI(String name) {
		currentFilterListModel.removeElement(name);
		ComboBoxModel.removeSource(name);
	}

	private ActionListener captureListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			// TODO - setState only done in Capture !!!!!
			// TODO - setting all of OpenCV's actual variables should ONLY be
			// done here
			// otherwise invalid states may occur while a capture is running
			// the model is to set all the data of the gui just before the
			// capture
			// request is sent
			VideoProcessor vp = myOpenCV.videoProcessor;

			String selected = (String) grabberTypeSelect.getSelectedItem();

			if ("IPCamera".equals(selected) || "Pipeline".equals(selected)) {

				prefixPath = "org.myrobotlab.opencv.";
			} else {
				prefixPath = "com.googlecode.javacv.";
			}

			vp.grabberType = prefixPath + (String) grabberTypeSelect.getSelectedItem() + "FrameGrabber";

			if (fileRadio.isSelected()) {
				String fileName = inputFile.getText();
				vp.inputFile = fileName;
				String extension = "";

				int i = fileName.lastIndexOf('.');
				if (i > 0) {
					extension = fileName.substring(i + 1);
				}

				if (("jpg").equals(extension) || ("png").equals(extension)) {
					vp.inputSource = OpenCV.INPUT_SOURCE_IMAGE_FILE;
					vp.grabberType = "org.myrobotlab.opencv.ImageFileFrameGrabber";

				} else {
					vp.inputSource = OpenCV.INPUT_SOURCE_MOVIE_FILE;
				}

			} else if (cameraRadio.isSelected()) {
				vp.inputSource = OpenCV.INPUT_SOURCE_CAMERA;
				vp.cameraIndex = (Integer) cameraIndex.getSelectedItem();
			} else {
				log.error("input source is " + vp.inputSource);
			}

			if ("IPCamera".equals(selected)) {
				vp.inputSource = OpenCV.INPUT_SOURCE_NETWORK;
			}

			if ("Pipeline".equals(selected)) {
				vp.inputSource = OpenCV.INPUT_SOURCE_PIPELINE;
				vp.pipelineSelected = (String) pipelineHook.getSelectedItem();
			}

			myService.send(boundServiceName, "setState", myOpenCV);

			// set new button state
			if (("capture".equals(capture.getText()))) {
				myService.send(boundServiceName, "capture");
				capture.setText("stop");
				// captureCfg.disable();
				setChildrenEnabled(captureCfg, false);
			} else {
				myService.send(boundServiceName, "stopCapture");
				capture.setText("capture");
				setChildrenEnabled(captureCfg, true);
			}

		}
	};

	/**
	 * GUIService defaults for grabber types
	 */
	private ActionListener grabberTypeListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			String type = (String) grabberTypeSelect.getSelectedItem();
			if ("OpenKinect".equals(type)) {
				cameraRadio.setSelected(true);
				cameraIndexLable.setVisible(true);
				cameraIndex.setVisible(true);
				modeLabel.setVisible(true);
				kinectImageOrDepth.setVisible(true);
				inputFileLable.setVisible(false);
				inputFile.setVisible(false);
				fileRadio.setVisible(false);

				IPCameraType.setVisible(false);
				pipelineHook.setVisible(false);
			}

			if ("OpenCV".equals(type) || "VideoInput".equals(type) || "FFmpeg".equals(type)) {
				// cameraRadio.setSelected(true);
				kinectImageOrDepth.setSelectedItem("image");
				// myOpenCV.format = "image";
				cameraIndexLable.setVisible(true);
				cameraIndex.setVisible(true);
				modeLabel.setVisible(false);
				kinectImageOrDepth.setVisible(false);
				inputFileLable.setVisible(true);
				inputFile.setVisible(true);

				fileRadio.setVisible(true);
				cameraRadio.setVisible(true);

				IPCameraType.setVisible(false);
				pipelineHook.setVisible(false);
			}

			if ("IPCamera".equals(type)) {
				// cameraRadio.setSelected(true);
				// kinectImageOrDepth.setSelectedItem("image");
				// myOpenCV.format = "image";
				cameraIndexLable.setVisible(false);
				cameraIndex.setVisible(false);
				modeLabel.setVisible(false);
				kinectImageOrDepth.setVisible(false);
				inputFileLable.setVisible(true);
				inputFile.setVisible(true);
				fileRadio.setSelected(true);

				fileRadio.setVisible(false);
				cameraRadio.setVisible(false);

				IPCameraType.setVisible(true);
				pipelineHook.setVisible(false);
			}

			if ("Pipeline".equals(type)) {
				// cameraRadio.setSelected(true);
				// kinectImageOrDepth.setSelectedItem("image");
				// myOpenCV.format = "image";
				cameraIndexLable.setVisible(false);
				cameraIndex.setVisible(false);
				modeLabel.setVisible(false);
				kinectImageOrDepth.setVisible(false);
				inputFileLable.setVisible(false);
				inputFile.setVisible(false);
				fileRadio.setSelected(true);

				fileRadio.setVisible(false);
				cameraRadio.setVisible(false);

				IPCameraType.setVisible(false);
				// this has static / global internals
				VideoSources vs = new VideoSources();
				Set<String> p = vs.getKeySet();
				pipelineHookModel.removeAllElements();
				for (String i : p) {
					pipelineHookModel.insertElementAt(i, 0);
				}
				pipelineHook.setVisible(true);
			}

		}
	};

	// TODO - put in util class
	private void setChildrenEnabled(Container container, boolean enabled) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component comp = container.getComponent(i);
			comp.setEnabled(enabled);
			if (comp instanceof Container)
				setChildrenEnabled((Container) comp, enabled);
		}
	}

	public void displayFrame(SerializableImage frame) {
		video0.displayFrame(frame);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// log.debug(e);
		if (!e.getValueIsAdjusting()) {
			String filterName = (String) currentFilters.getSelectedValue();
			log.info("gui valuechange setting to {}", filterName);
			if (filterName != null) {
				myService.send(boundServiceName, "setDisplayFilter", filterName);
				OpenCVFilterGUI f = guiFilters.get(filterName);
				if (f != null) {
					filterParameters.removeAll();
					filterParameters.add(f.getDisplay());
					filterParameters.repaint();
					filterParameters.validate();
				} else {
					filterParameters.removeAll();
					filterParameters.add(new JLabel("no parameters available"));
					filterParameters.repaint();
					filterParameters.validate();
				}
			} else {
				myService.send(boundServiceName, "setDisplayFilter", INPUT_KEY);
				// TODO - send message to OpenCV - that no filter should be sent
				// to publish
				filterParameters.removeAll();
				filterParameters.add(new JLabel("no filter selected"));
				filterParameters.repaint();
				filterParameters.validate();
			}

			// TODO - if filterName = null - it has been "un"selected ctrl-click

		}
	}

	// MouseListener mouseListener = new MouseAdapter() {
	public void setCurrentFilterMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
						System.out.println("Double-clicked on: " + o.toString());
					}
				}
			}
		};

		currentFilters.addMouseListener(mouseListener);
	}

	// jlist.addMouseListener(mouseListener);

	@Override
	public VideoWidget getLocalDisplay() {
		// TODO Auto-generated method stub
		return video0; // else return video1
	}

	/*
	 * getState is an interface function which allow the interface of the
	 * GUIService Bound service to update graphical portions of the GUIService
	 * based on data changes.
	 * 
	 * The entire service is sent and it is this functions responsibility to
	 * update all of the gui components based on data elements and/or method of
	 * the service.
	 * 
	 * getState get's its Service directly if the gui is operating "in process".
	 * If the gui is operating "out of process" a serialized (zombie) process is
	 * sent to provide the updated state information. Typically "publishState"
	 * is the function which provides the event for getState.
	 */
	final static String prefix = "OpenCVFilter";

	public void getState(final OpenCV opencv) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				if (opencv != null) {
					VideoProcessor vp = opencv.videoProcessor;

					// add new filters from service into gui
					Iterator<OpenCVFilter> itr = opencv.getFiltersCopy().iterator();
					HashMap<String, String> allSvcFilterNames = new HashMap<String, String>();
					while (itr.hasNext()) {
						String name;
						try {
							OpenCVFilter f = itr.next();
							name = f.name;

							// adding filters in gui - which are in the service
							if (!guiFilters.containsKey(name)) {
								OpenCVFilterGUI guifilter = addFilterToGUI(name, f);
								// set the state of the filter gui - first one
								// is
								// free :)
								if (guifilter != null) {
									guifilter.getFilterState(new FilterWrapper(name, f));
								}
							}

							allSvcFilterNames.put(name, name);

						} catch (Exception e) {
							Logging.logException(e);
							break;
						}

					}

					// remove filters not found in service from gui
					Iterator<String> guifltr = guiFilters.keySet().iterator();
					while (guifltr.hasNext()) {
						String guifltrname = guifltr.next();
						if (!allSvcFilterNames.containsKey(guifltrname)) {
							removeFilterFromGUI(guifltrname);
							// guiFilters.remove(name);
							guifltr.remove();
						}
					}

					currentFilters.repaint();

					for (int i = 0; i < grabberTypeSelect.getItemCount(); ++i) {
						String currentObject = prefixPath + (String) grabberTypeSelect.getItemAt(i);
						if (currentObject.equals(vp.grabberType)) {
							grabberTypeSelect.setSelectedIndex(i);
							break;
						}
					}

					if (vp.capturing) {
						capture.setText("stop");
					} else {
						capture.setText("capture");
					}

					inputFile.setText(vp.inputFile);
					cameraIndex.setSelectedIndex(vp.cameraIndex);
					String inputSource = opencv.videoProcessor.inputSource;
					if (OpenCV.INPUT_SOURCE_CAMERA.equals(inputSource)) {
						cameraRadio.setSelected(true);
					} else if (OpenCV.INPUT_SOURCE_CAMERA.equals(inputSource)) {
						fileRadio.setSelected(true);
					} else if (OpenCV.INPUT_SOURCE_PIPELINE.equals(inputSource)) {
						// grabberTypeSelect.removeActionListener(grabberTypeListener);
						grabberTypeSelect.setSelectedItem("Pipeline");
						// grabberTypeSelect.addActionListener(grabberTypeListener);
						pipelineHook.setSelectedItem(vp.pipelineSelected);
					}

					currentFilters.removeListSelectionListener(self);
					currentFilters.setSelectedValue(vp.displayFilterName, true);// .setSelectedIndex(index);
					currentFilters.addListSelectionListener(self);
					
					if (opencv.undockDisplay == true){
						cframe = new CanvasFrame("canvas frame");
					} else {
						if (cframe != null){
							cframe.dispose();
							cframe = null;
						}
					}

				} else {
					log.error("getState for " + myService.getName() + " was called on " + boundServiceName + " with null reference to state info");
				}

			}
		});

	}
	
	public void onOpenCVData(OpenCVData data){
		// GRRR BufferedImage - should have been a Serialized Image;		
		
		if (cframe != null){
			cframe.showImage(data.getImage());
		} else {
			video0.displayFrame(new SerializableImage(data.getDisplayBufferedImage(), data.getDisplayFilterName()));
		}
	}

	@Override
	public void attachGUI() {
		// TODO - bury in GUIService Framework?
		subscribe("publishState", "getState", OpenCV.class);
		subscribe("publishOpenCVData", "onOpenCVData", OpenCVData.class);
		myService.send(boundServiceName, "publishState");

		//video0.attachGUI(); // default attachment
		// templateDisplay.attachGUI(); // default attachment
	}

	@Override
	public void detachGUI() {
		unsubscribe("publishState", "getState", OpenCV.class);
		unsubscribe("publishOpenCVData", "onOpenCVData", OpenCVData.class);

		// video0.detachGUI();
		// stemplateDisplay.detachGUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// VideoProcessor vp = myOpenCV.videoProcessor; FIXME !!! YOU CANT USE
		// VP - its transient !!!
		if (o == addFilterButton) {
			addFilter();
		} else if (o == removeFilterButton) {
			String name = (String) currentFilters.getSelectedValue();
			myService.send(boundServiceName, "removeFilter", name);
			// TODO - block on response
			currentFilterListModel.removeElement(name);
		} else if (o == kinectImageOrDepth) {
			String mode = (String) kinectImageOrDepth.getSelectedItem();
			/*
			 * if ("depth".equals(mode)) { vp.format = "depth"; } else {
			 * vp.format = "image"; } // FIXME - broadcastState ???
			 */
		} else if (o == recordButton) {
			if (recordButton.getText().equals("record")) {
				// start recording
				myService.send(boundServiceName, "recordOutput", true);
				recordButton.setText("stop recording");
			} else {
				// stop recording
				myService.send(boundServiceName, "recordOutput", false);
				recordButton.setText("record");
			}
		} else if (o == recordFrameButton) {
			myService.send(boundServiceName, "recordSingleFrame");
		} else if (o == undock){
			if (undock.isSelected()){
			if (cframe != null){
				cframe.dispose();				
			}
			cframe = new CanvasFrame("canvas");
			} else {
				if (cframe != null){
					cframe.dispose();				
					cframe = null;
				}
			}
		}
	}

}
