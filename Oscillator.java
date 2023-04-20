//
//	Oscillator.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.event.ItemEvent;
import javax.swing.*;

public class Oscillator extends SynthControl {
	private static final long serialVersionUID = 4638081381028293840L;
	private static final int TONE_OFFSET_LIMIT = 2000;
	private Wavetable mWaveTable = Wavetable.Sine;
	private RefWrapper<Integer> mToneOffset = new RefWrapper<>(0);
	private RefWrapper<Integer> mVolume = new RefWrapper<>(100);
	private double mKeyFrequency;
	private int mWaveTableStepSize;
	private int mWaveTableIndex;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs an Oscillator, with it's GUI and it's parameters
	*/ // ---------------------------------------------------------------------
	public Oscillator(Synthesizer synth) {
		super(synth);		
		
		{
			JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
			comboBox.setSelectedItem(mWaveTable);
			comboBox.setBounds(10, 10, 75, 25);
			comboBox.addItemListener(l -> {
				if(l.getStateChange() == ItemEvent.SELECTED) {
					mWaveTable = (Wavetable)l.getItem();
				}
				synth.updateWaveviewer();
			});
			add(comboBox);
		}
		
		{
			JLabel toneParameter = new JLabel("x0.00");
			toneParameter.setBounds(165, 65, 50, 25);
			toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
			Utils.phP.addParameterMouseListening(toneParameter, this, -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, mToneOffset, () -> {
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
			volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
			Utils.phP.addParameterMouseListening(volumeParameter, this, 0, 100, 1, mVolume, () ->  {
				volumeParameter.setText(" " + mVolume.val + "%");
				synth.updateWaveviewer();
			});
			add(volumeParameter);	
		}
		
		{
			AddText("Volume");
		}
		
		setSize(279, 100);	
		setBorder(Utils.WindowDesign.LINE_BORDER);
		setLayout(null);
	}
	
	// ------------------------------------------------------------------------
	/*! Next Sample
	*
	*   Generates the next sample of our Oscillator, depending of our WaveTable
	*/ // ---------------------------------------------------------------------
	public double NextSample() {
		double sample = mWaveTable.getSamples()[mWaveTableIndex] * GetVolumeMultiplier();
		mWaveTableIndex = (mWaveTableIndex + mWaveTableStepSize) % Wavetable.SIZE;
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
		double frequency = 1.0 / (numSamples / (double)Synthesizer.AudioInfo.SAMPLE_RATE) * 3.0;
		int index = 0;
		int stepSize = (int)(Wavetable.SIZE * Utils.Math.offsetTone(frequency, GetToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
		
		for(int i = 0; i < numSamples; i++) {
			samples[i] = mWaveTable.getSamples()[index] * GetVolumeMultiplier();
			index = (index + stepSize) % Wavetable.SIZE;
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
		mWaveTableStepSize = (int)(Wavetable.SIZE * (Utils.Math.offsetTone(mKeyFrequency, GetToneOffset())) / Synthesizer.AudioInfo.SAMPLE_RATE);
	}
}
