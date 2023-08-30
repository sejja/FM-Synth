//
//	PianoView.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

class PianoLayout extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private Synthesizer mSynth;
	private int mHeight = 280;
	private int mNoteWidth = 40;
	
	enum KeyColor {
		White,
		Black
	}
	
	// ------------------------------------------------------------------------
	/*! Custom Constructor
	*
	*   Constructs a piano keyboard, taking as a listener the parent synthesizer
	*/ // ---------------------------------------------------------------------
	public PianoLayout(Synthesizer synth) {
		mSynth = synth;
		
		final JLayeredPane layer = new JLayeredPane();
		final JButton[] keys = new JButton[48];
		layer.setSize(synth.GetWidth(), mHeight);
		
		//Generate 28 keys
		for(int i = 0, keyIndex = i; keyIndex < keys.length; i++) {
			keys[keyIndex] = CreateKey(KeyColor.White, i, keyIndex);
			layer.add(keys[keyIndex], 0, -1);
			keyIndex++;
			
			//And exclude the D and A keys with #s
			if(i%7!=2 && i%7!=6) {
				keys[keyIndex] = CreateKey(KeyColor.Black, i, keyIndex);
				layer.add(keys[keyIndex], 1, -1);
				keyIndex++;
			}
		}
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setViewportView(layer);
		setSize(synth.GetWidth(), mHeight);     
	}

	// ------------------------------------------------------------------------
	/*! Create White Key
	*
	*   Creates a White Key
	*/ // ---------------------------------------------------------------------
	private JButton CreateKey(KeyColor col, int i, int keyIndex) {
		JButton keybutton = new JButton();
		keybutton.addMouseListener(new MouseAdapter() {
			@Override
		    public void mousePressed(MouseEvent e) {
				mSynth.PlayNote(mSynth.GetKeyID(keyIndex));
		    }
			
			@Override
		    public void mouseReleased(MouseEvent e) {
				mSynth.StopSound();
		    }
		});
		
		keybutton.setBackground(col == KeyColor.White ? Color.white : Color.black);
		keybutton.setLocation(i*mNoteWidth + (col == KeyColor.White ? 0 : 25),0);
		
		//Change the size of the color depending on the key
		if(col == KeyColor.White) 
			keybutton.setSize(mNoteWidth, 150);
		else
			keybutton.setSize(30, 90);
		return keybutton;
	}
}