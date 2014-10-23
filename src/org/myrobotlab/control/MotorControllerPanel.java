package org.myrobotlab.control;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author GroG interface to update a MotorGUI's Controller Panel
 * 
 */
abstract class MotorControllerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	MotorControllerPanel() {
		setBorder(BorderFactory.createTitledBorder("type"));
	}

	abstract void setAttached(boolean state);

	abstract public void setData(Object[] data);

}
