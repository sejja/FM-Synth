//
//	Phaser.java
//	Synthesizer
//
//	Created by Diego Revilla on 24/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class Phaser extends SynthControl implements Serializable {
	private static final long serialVersionUID = 415490975575098955L;
	private Boolean mEnabled = false;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs a Phaser Filter, with it's parent synthesizer
	*/ // ---------------------------------------------------------------------
	public Phaser(Synthesizer synth) {
		super(synth);		
			
		JCheckBox toogle = new JCheckBox("Activate");
		JLabel label = new JLabel("Phaser: ");
		add(label);
		add(toogle);
			
		toogle.addItemListener(new ItemListener() {    
			@Override
			public void itemStateChanged(ItemEvent e) {
				mEnabled = !mEnabled;
			}    
	    });

		setSize(279, 100);	
		setBorder(Utils.WindowDesign.GetSynthesizerBorder());
	}
		
	// ------------------------------------------------------------------------
	/*! Phaser Effect
	 *
	 *   Returns the Phaser Effect value, to modify the soundwave
	*/ // ---------------------------------------------------------------------
	public double PhaserEffect(float time) {
		return mEnabled ? -time : 1.0;
	}

	// ------------------------------------------------------------------------
	/*! Serialize
	*
	*   Writes the Component value onto a string
	*/ // ---------------------------------------------------------------------
	@Override
	public String Serialize() {
		return mEnabled.toString();
	}

	// ------------------------------------------------------------------------
	/*! Fetch
	*
	*   Reconstructs the settings of the phaser from a string
	*/ // ---------------------------------------------------------------------
	@Override
	public void Fetch(String serial) {
		String[] parse = serial.split(",");
		mEnabled = Boolean.valueOf(parse[0]);
	}
}
