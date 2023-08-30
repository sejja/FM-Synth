//
//	Synthesizer.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import javax.swing.*;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Synthesizer implements Serializable {
	private class AudioCreator {
		private boolean mShouldGenerate;
		final private AudioThread mAudioThread = new AudioThread(() -> {
			//If we should not generate any sound, return null
			if(!mShouldGenerate) return null; 
			
			short[] soundbuffer = new short[AudioThread.BUFFER_SIZE];
			
			//For each sample on the buffer, compute it's value
			for(int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
				double sample = 0;
				
				for(Oscillator o : mOscillators)
					sample += o.NextSample() / mOscillators.length;
				
				soundbuffer[i] = (short)(Short.MAX_VALUE * sample * mPhaserFilter.PhaserEffect(mTime) / ARSDCurveVal());
			}
			
			return soundbuffer;
		});
	}
	
	public static class AudioInfo {
		// ------------------------------------------------------------------------
		/*! Get Sample Rate
		*
		*   Returns the Sample Rate of the Synthesizer
		*/ // ---------------------------------------------------------------------
		public static int GetSampleRate() {
			return 44100;
		}
	}
	
	final AudioCreator mSoundWaveGenerator = new AudioCreator();
	final private char[] mKeys = "º1234567890'¡qwertyuiop`+çasdfghjklñ´<zxcvbnm,.-ª!·$%&/()=?¿".toCharArray();
	private static final HashMap<Character, Double> mKeyFrequencies = new HashMap<>();
	final private JFrame mFrame = new JFrame("Basic Synth");
	private Oscillator[] mOscillators = new Oscillator[3];
	final private WaveViewer mWaveViewer = new WaveViewer(mOscillators);
	final private ARSDViewer mARDSViewer = new ARSDViewer(this);
	final private WindowOptions mWindowOptions = new WindowOptions(mFrame, new Vector2D<>(450, 130));
	final private PianoLayout mPianoView = new PianoLayout(this);
	final private WaveViewerSampler mSampler = new WaveViewerSampler();
	private float mTime = 1;
	final private Phaser mPhaserFilter = new Phaser(this);
	
	// ------------------------------------------------------------------------
	/*! ADSRCurve Val
	*
	*   Evaluates the ADSR Curve value
	*/ // ---------------------------------------------------------------------
	public float ARSDCurveVal() {
		//If the ARSD Curve is activated, let's hear it play
		if(mARDSViewer.IsActive()) {
			mTime += 0.001;
			return mARDSViewer.Evaluate(mTime);
		}
		return 1;
	}
	
	// ------------------------------------------------------------------------
	/*! Play Note
	*
	*   Plays a note given the keyboard key
	*/ // ---------------------------------------------------------------------
	public void PlayNote(char key) {
		if(!mKeyFrequencies.containsKey(key)) {
			return;
		}
		
		//If the audio thread is running, set the frequency of every oscillator to the given key
		if(!mSoundWaveGenerator.mAudioThread.GetIsRunning()) {
			for(Oscillator o : mOscillators) o.SetFrequency(mKeyFrequencies.get(key));	
			mSoundWaveGenerator.mShouldGenerate = true;
			mSoundWaveGenerator.mAudioThread.TriggerPlayback();
		}
		
		mSampler.PlaySound();
	}
	
	// ------------------------------------------------------------------------
	/*! Get Key ID
	*
	*   Returns the identifier of a Key by Index
	*/ // ---------------------------------------------------------------------
	public char GetKeyID(int idx) {
		return mKeys[idx];
	}
	
	// ------------------------------------------------------------------------
	/*! Stop Sound
	*
	*   Stops the synthesizer from producing a particular sound
	*/ // ---------------------------------------------------------------------
	public void StopSound() {
		mSoundWaveGenerator.mShouldGenerate = false;
		
		//If we got a valid sampller
		if(mSampler.GetSampler() != null)
			mSampler.GetSampler().StopSound();
		mTime = 1;
	}
	
	private final KeyAdapter keyAdapter = new KeyAdapter() {		
		@Override
		public void keyPressed(KeyEvent e) {
			PlayNote(e.getKeyChar());
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			StopSound();
		}
	};
	
	// ------------------------------------------------------------------------
	/*! Get Width
	*
	*   Returns the width of the whole synthesizer
	*/ // ---------------------------------------------------------------------
	public int GetWidth() {
		return mFrame.getWidth();
	}
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs the Synthesizer, creating every class
	*/ // ---------------------------------------------------------------------
	Synthesizer() {
		int y = 0;
		for(int i = 0; i < mOscillators.length; i++) {
			mOscillators[i] = new Oscillator(this);
			mOscillators[i].setLocation(5, y);
			mFrame.add(mOscillators[i]);
			y += 105;
		}
		
		final int startingKey = 16;
		
		for(int i = startingKey, key = 0; i < mKeys.length + startingKey; i++, key++) {
			mKeyFrequencies.put(mKeys[key], Utils.Math.getKeyFrequency(i));
		}
		
		mFrame.addKeyListener(keyAdapter);
		mFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mSoundWaveGenerator.mAudioThread.close();
			}
		});
		mWaveViewer.setBounds(290, 0, 310, 310);
		mARDSViewer.setBounds(600, 0, 360, 310);
		mWindowOptions.setBounds(0, 310, 620, 490);
		mFrame.add(mWaveViewer);
		mFrame.add(mARDSViewer);
		mFrame.add(mWindowOptions);
		
		mSampler.setBounds(600, 310, 310, 310);
		mFrame.add(mSampler);
		
		mFrame.add(mPianoView);
		mPianoView.setBounds(0, 800, 2000, 500);
		mFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mFrame.setSize(1000, 1000);
		mFrame.setResizable(false);
		mFrame.setLayout(null);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);
		mPhaserFilter.setBounds(600, 620, 310, 310);
		mFrame.add(mPhaserFilter);
		
		try {
			mWindowOptions.AddSerializable((Serializable)this);
		} catch (WindowOptions.WinOptException e1) {
			e1.printStackTrace();
		}
		
		try {
			/*MidiPlayer play =*/ new MidiPlayer();
			new MidiReceiver(this);
			//play.PlayMidi();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------------
	/*! Get Key Adapter
	*
	*   Returns the handler of keys
	*/ // ---------------------------------------------------------------------
	public KeyAdapter getKeyAdapter() {
		return keyAdapter;
	}
	
	// ------------------------------------------------------------------------
	/*! Update Wave Viewer
	*
	*   Repaints the wave form
	*/ // ---------------------------------------------------------------------
	public void updateWaveviewer() {
		mWaveViewer.repaint();
	}

	// ------------------------------------------------------------------------
	/*! Serialize
	*
	*   Serializes every single registered component, into a preset
	*/ // ---------------------------------------------------------------------
	@Override
	public String Serialize() {
		String s = new String();
		
		for(int i = 0; i < mOscillators.length; i++)
			s += mOscillators[i].Serialize() + ";";
		
		return s;
	}

	// ------------------------------------------------------------------------
	/*! Fetch
	*
	*   Creates the settings from a present file
	*/ // ---------------------------------------------------------------------
	@Override
	public void Fetch(String serial) {
		String[] parse = serial.split(";");
		
		for(int i = 0; i < 3; i++)
			mOscillators[i].Fetch(parse[i]);
	}
}