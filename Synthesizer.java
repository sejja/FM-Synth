import javax.swing.*;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Synthesizer {
	private boolean shouldGenerate;
	private static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
	private final JFrame frame = new JFrame("Basic Synth");
	private final Oscillator[] oscillators = new Oscillator[3];
	private final WaveViewer waveViewer = new WaveViewer(oscillators);
	private final ARSDViewer arsdViewer;
	private final AudioThread audiothread = new AudioThread(() -> {
		if(!shouldGenerate) {
			return null; 
		}
		
		short[] a = new short[AudioThread.BUFFER_SIZE];
		
		for(int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
			double d = 0;
			
			for(Oscillator o : oscillators) {
				d += o.NextSample() / oscillators.length;
			}
			
			a[i] = (short)(Short.MAX_VALUE * d);
		}
		
		return a;
	});
	
	private final KeyAdapter keyAdapter = new KeyAdapter() {		
		@Override
		public void keyPressed(KeyEvent e) {
			if(!KEY_FREQUENCIES.containsKey(e.getKeyChar())) {
				return;
			}
			
			if(!audiothread.GetIsRunning()) {
				for(Oscillator o : oscillators) {
					o.SetFrequency(KEY_FREQUENCIES.get(e.getKeyChar()));
				}
				
				shouldGenerate = true;
				audiothread.TriggerPlayback();
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			shouldGenerate = false;
		}
	};
	
	static  {
		final int STARTING_KEY = 16;
		final int KEY_FREQUENCY_INCREMENT = 2;
		final char[] KEYS = "qwertyuiop`+çasdfghjklñ´zxcvbnm,.-".toCharArray();
		
		for(int i = STARTING_KEY, key = 0; i < KEYS.length * KEY_FREQUENCY_INCREMENT + STARTING_KEY; i += KEY_FREQUENCY_INCREMENT, key++) {
			KEY_FREQUENCIES.put(KEYS[key], Utils.Math.getKeyFrequency(i));
		}	
	}
	
	Synthesizer() {
		arsdViewer = new ARSDViewer(this);
		int y = 0;
		for(int i = 0; i < oscillators.length; i++) {
			oscillators[i] = new Oscillator(this);
			oscillators[i].setLocation(5, y);
			frame.add(oscillators[i]);
			y += 105;
		}
		
		frame.addKeyListener(keyAdapter);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				audiothread.close();
			}
		});
		waveViewer.setBounds(290, 0, 310, 310);
		arsdViewer.setBounds(500, 500, 310, 310);
		frame.add(waveViewer);
		frame.add(arsdViewer);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setResizable(false);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public KeyAdapter getKeyAdapter() {
		return keyAdapter;
	}
	
	public void updateWaveviewer() {
		waveViewer.repaint();
	}
	
	public static class AudioInfo {
		public static final int SAMPLE_RATE = 44100;
	}
}