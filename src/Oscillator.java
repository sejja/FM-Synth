//
//	Oscillator.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Oscillator extends SynthControl implements Serializable {
	private static final long serialVersionUID = 4638081381028293840L;
	private Wavetable mWaveTable = Wavetable.Sine;
	private RefWrapper<Integer> mToneOffset = new RefWrapper<>(0);
	private RefWrapper<Integer> mVolume = new RefWrapper<>(100);
	private double mKeyFrequency;
	private int mWaveTableStepSize;
	private int mWaveTableIndex;
	private Synthesizer mParent;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs an Oscillator, with it's GUI and it's parameters
	*/ // ---------------------------------------------------------------------
	public Oscillator(Synthesizer synth) {
		super(synth);		
		mParent = synth;
		
		{
            JButton sinuidal = new JButton();
            sinuidal.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    mWaveTable = Wavetable.Sine;
                    synth.updateWaveviewer();
                }
            });

            try {
                Image img = ImageIO.read(getClass().getResource("content/sinouidal.png")).getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );
                sinuidal.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            JButton square = new JButton();
            square.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    mWaveTable = Wavetable.Square;
                    synth.updateWaveviewer();
                }
            });

            try {
                Image img = ImageIO.read(getClass().getResource("content/square.png")).getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );
                square.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            JButton triangle = new JButton();
            triangle.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    mWaveTable = Wavetable.Triangle;
                    synth.updateWaveviewer();
                }
            });

            try {
                Image img = ImageIO.read(getClass().getResource("content/triangle.png")).getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );
                triangle.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            JButton saw = new JButton();
            saw.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    mWaveTable = Wavetable.Saw;
                    synth.updateWaveviewer();
                }
            });

            try {
                Image img = ImageIO.read(getClass().getResource("content/saw.png")).getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );
                saw.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            JButton noise = new JButton();
            noise.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    mWaveTable = Wavetable.Noise;
                    synth.updateWaveviewer();
                }
            });

            try {
                Image img = ImageIO.read(getClass().getResource("content/noise.png")).getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );
                noise.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }

            sinuidal.setBounds(10, 10, 25, 25);
            add(sinuidal);
            square.setBounds(45, 10, 25, 25);
            add(square);
            triangle.setBounds(80, 10, 25, 25);
            add(triangle);
            saw.setBounds(115, 10,25, 25);
            add(saw);
            noise.setBounds(150, 10, 25, 25);
            add(noise);
		}
		
		{
			JLabel toneParameter = new JLabel("x0.00");
			toneParameter.setBounds(165, 65, 50, 25);
			toneParameter.setBorder(Utils.WindowDesign.GetSynthesizerBorder());
			Utils.ParameterListenerHelpers.addParameterMouseListening(toneParameter, this, -2000, 2000, 1, mToneOffset, () -> {
				ApplyToneOffset();
				toneParameter.setText(" x" + String.format("%.3f", GetToneOffset()));
				synth.updateWaveviewer();
			});
			add(toneParameter);	
		}
			
		{
			AddText("Tone");
		}
		
		{
			JLabel volumeParameter = new JLabel("100%");
			volumeParameter.setBounds(222, 65, 50, 25);
			volumeParameter.setBorder(Utils.WindowDesign.GetSynthesizerBorder());
            volumeParameter.setBackground(Color.white);
			Utils.ParameterListenerHelpers.addParameterMouseListening(volumeParameter, this, 0, 100, 1, mVolume, () ->  {
				volumeParameter.setText(" " + mVolume.val + "%");
				synth.updateWaveviewer();
			});
			add(volumeParameter);	
		}
		
		{
			AddText("Volume");
		}
	
		setSize(279, 100);	
		setBorder(Utils.WindowDesign.GetSynthesizerBorder());
		setLayout(null);
	}
	
	// ------------------------------------------------------------------------
	/*! Next Sample
	*
	*   Generates the next sample of our Oscillator, depending of our WaveTable
	*/ // ---------------------------------------------------------------------
	public double NextSample() {
		double sample = mWaveTable.GetSamples()[mWaveTableIndex] * GetVolumeMultiplier();
		mWaveTableIndex = (mWaveTableIndex + mWaveTableStepSize) % Wavetable.GetSampleSize();
		return sample;
	}
	
	// ------------------------------------------------------------------------
	/*! Set Frequency
	*
	*   Sets the Frequency of our Oscillator
	*/ // ---------------------------------------------------------------------
	public void SetFrequency(double frequency) {
		mKeyFrequency = frequency;
		ApplyToneOffset();
	}
	
	// ------------------------------------------------------------------------
	/*! Get Sample WaveForm
	*
	*   Generates an array of values from t = 0, to plot the wave form
	*/ // ---------------------------------------------------------------------
	public double[] GetSampleWaveForm(int numSamples) {
		double[] samples = new double[numSamples];
		double frequency = 1.0 / (numSamples / (double)Synthesizer.AudioInfo.GetSampleRate()) * 3.0;
		int index = 0;
		int stepSize = (int)(Wavetable.GetSampleSize() * Utils.Math.offsetTone(frequency, GetToneOffset()) / Synthesizer.AudioInfo.GetSampleRate());
		
		//Number of samples
		for(int i = 0; i < numSamples; i++) {
			samples[i] = mWaveTable.GetSamples()[index] * GetVolumeMultiplier();
			index = (index + stepSize) % Wavetable.GetSampleSize();
		}
		
		return samples;
	}
	
	// ------------------------------------------------------------------------
	/*! Get Tone Offset
	*
	*   Returns the tone value that we should consider, normalizing it to math the wave units
	*/ // ---------------------------------------------------------------------
	private double GetToneOffset() {
		return mToneOffset.val / 100f;
	}
	
	// ------------------------------------------------------------------------
	/*! Get Volume Multiplier
	*
	*   Returns the volume value that we should consider, normalizing it to math the wave units
	*/ // ---------------------------------------------------------------------
	private double GetVolumeMultiplier() {
		return mVolume.val / 100.0f;
	}
	
	// ------------------------------------------------------------------------
	/*! Apply Tone Offset
	*
	*   Modifies how much of the current wave form we advance for every audio tick. Depends on the frequency and tone
	*/ // ---------------------------------------------------------------------
	private void ApplyToneOffset() {
		mWaveTableStepSize = (int)(Wavetable.GetSampleSize() * (Utils.Math.offsetTone(mKeyFrequency, GetToneOffset())) / Synthesizer.AudioInfo.GetSampleRate());
	}

	@Override
	public String Serialize() {	
		return mWaveTable + "," + mVolume.val + "," + mToneOffset.val;
	}

	@Override
	public void Fetch(String serial) {
		String[] parse = serial.split(",");
		
		mWaveTable = Wavetable.valueOf(parse[0]);
		mVolume.val = Integer.parseInt(parse[1]);
		mToneOffset.val = Integer.parseInt(parse[2]);
		mParent.updateWaveviewer();
	}
}
