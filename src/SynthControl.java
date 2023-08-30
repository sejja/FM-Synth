//
//	SynthControl.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import javax.swing.*;
import java.awt.*;

public class SynthControl extends JPanel {
	private static final long serialVersionUID = 1793082100203474023L;
	private Point mMouseClickLocation;
	private boolean mOn;
	private Synthesizer mSynth;
	private int mTextYTrack = 172;
	
	// ------------------------------------------------------------------------
	/*! Get Mouse Click Location
	*
	*   Returns the Location where the Mouse was last clicked
	*/ // ---------------------------------------------------------------------
	public Point GetMouseClickLocation() {
		return mMouseClickLocation;
	}
	
	// ------------------------------------------------------------------------
	/*! Get Mouse Click Location
	*
	*   Sets the point where the mouse has clicked
	*/ // ---------------------------------------------------------------------
	public void SetMouseClickLocation(Point mouseClickLocation) {
		this.mMouseClickLocation = mouseClickLocation;
	}
	
	// ------------------------------------------------------------------------
	/*! Is On
	*
	*   Returns weather the SynthControl is activated or not
	*/ // ---------------------------------------------------------------------
	public boolean IsOn() {
		return mOn;
	}
	 
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Returns the Location where the Mouse was last clicked
	*/ // ---------------------------------------------------------------------
	public SynthControl(Synthesizer synth) {
		mSynth = synth;
	}
	
	// ------------------------------------------------------------------------
	/*! Set On
	*
	*   Sets the controller on and off
	*/ // ---------------------------------------------------------------------
	public void SetOn(boolean on) {
		mOn = on;
	}
	
	// ------------------------------------------------------------------------
	/*! Add Text
	*
	*   Add Texts to the controller
	*/ // ---------------------------------------------------------------------
	void AddText(String text) {
		JLabel toneText = new JLabel(text);
		toneText.setBounds(mTextYTrack, 40, 75, 25);
		add(toneText);
		
		mTextYTrack += 50;
	}
	
	// ------------------------------------------------------------------------
	/*! add
	*
	*   Adds a component to the Visual interface
	*/ // ---------------------------------------------------------------------
	@Override
	public Component add(Component component) {
		 component.addKeyListener(mSynth.getKeyAdapter());
		 return super.add(component);
	}
}
