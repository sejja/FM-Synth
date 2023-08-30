//
//	ARSDViewer.java
//	Synthesizer
//
//	Created by Diego Revilla on 21/03/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Function;
import javax.swing.*;

public class ARSDViewer extends SynthControl {
	private static final long serialVersionUID = -5873320893842605461L;
	private RefWrapper<Float> attack = new RefWrapper<>(2.f);
	private RefWrapper<Float> attack_period = new RefWrapper<>(1.f);
	private RefWrapper<Float> decay = new RefWrapper<>(1.f);
	private RefWrapper<Float> decay_period = new RefWrapper<>(2.f);
	private RefWrapper<Float> sustain = new RefWrapper<>(1.f);
	private RefWrapper<Float> sustain_period = new RefWrapper<>(2.f);
	private RefWrapper<Float> release = new RefWrapper<>(0.f);
	private RefWrapper<Float> release_period = new RefWrapper<>(2.f);
	private Boolean mActivated = false;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs a Panel inside the view to visualize the ARSD values
	*/ // ---------------------------------------------------------------------
	public ARSDViewer(Synthesizer synth) {
		super(synth);
		JCheckBox toogle = new JCheckBox("Activate");
		add(toogle);
		
		toogle.addItemListener(new ItemListener() {    
			@Override
			public void itemStateChanged(ItemEvent e) {
				mActivated = !mActivated;
			}    
         });    
		
		
		AddModulator("Attack", attack);
		AddModulator("Attack Rate", attack_period);
		AddModulator("Decay", decay);
		AddModulator("Decay Rate", decay_period);
		AddModulator("Sustain", sustain);
		AddModulator("Sustain Rate", sustain_period);
		AddModulator("Release", release);
		AddModulator("Release Rate", release_period);
	}
	
	public Boolean IsActive() {
		return mActivated;
	}
	
	public float Evaluate(float time) {
		float b = (float)(1.0 - (time - attack_period.val) * (1.0 - sustain.val) / decay_period.val);
		
		if(time < attack_period.val) {
			return (time / attack_period.val) * attack.val;
		} else if(time < attack_period.val + decay_period.val) {
			return b;
		} else if(time < attack_period.val + decay_period.val + sustain_period.val) {
			return sustain.val;
		} else if(time < attack_period.val + decay_period.val + sustain_period.val + release_period.val) {
			return release.val + (sustain.val - release.val) * (1 - ((time - sustain_period.val) /release_period.val));
		} else {
			return release.val;
		}
	}
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Returns the volume value that we should consider, normalizing it to math the wave units
	*/ // ---------------------------------------------------------------------
	void AddModulator(String name, RefWrapper<Float> param) {
		final JLabel parameterLabel = new JLabel("100%");
		parameterLabel.setBounds(0, 0, 50, 25);
		parameterLabel.setBorder(Utils.WindowDesign.GetSynthesizerBorder());
		Utils.ParameterListenerHelpers.addParameterMouseListening(parameterLabel, this, 0.f, 1.f, .01f, param, () ->  {
			parameterLabel.setText(" " + param.val + "%");
			
		});
		add(parameterLabel);
		final JLabel parameterName = new JLabel(name);
		parameterName.setBounds(0, 1000, 75, 25);
		add(parameterName);
	}
	
	// ------------------------------------------------------------------------
	/*! Paint Component
	*
	*   Paints the Components
	*/ // ---------------------------------------------------------------------
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		final int PAD = 25;
		final Vector2D<Integer> mid = new Vector2D<>(getHeight() / 2, getWidth() / 2);
		final Function<Float, Integer> sampleToYCoord = sample ->(int)((mid.y + sample * (mid.y - PAD)));
		final Function<Float, Integer> sampleToXCoord = sample ->(int)(PAD + sample * (mid.x - PAD));
		Graphics2D graphics2D = (Graphics2D)graphics;
		
		graphics2D.clearRect(0, 0, mid.x * 2, mid.y * 2);
		graphics2D.drawLine(PAD, mid.x, getWidth() - PAD, mid.y);
		graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);
		graphics2D.setColor(Color.GREEN);
		graphics2D.drawLine(sampleToXCoord.apply(0.f), sampleToYCoord.apply(0.f), 
				sampleToXCoord.apply(attack_period.val * 2), sampleToYCoord.apply(-attack.val));
		graphics2D.drawLine(sampleToXCoord.apply(attack_period.val* 2), sampleToYCoord.apply(-attack.val), 
				sampleToXCoord.apply(decay_period.val * 2), sampleToYCoord.apply(-decay.val));
		//graphics2D.drawLine(sampleToXCoord.apply((float) decay_period.val * 2), sampleToYCoord.apply((float) -decay.val), 
		//		sampleToXCoord.apply((float) sustain_period.val * 2), sampleToYCoord.apply((float) -sustain.val));
		//graphics2D.drawLine(sampleToXCoord.apply((float) sustain_period.val * 2), sampleToYCoord.apply((float) -sustain.val), 
		//		sampleToXCoord.apply((float) release_period.val * 2), sampleToYCoord.apply((float) -release.val));
	}
}
