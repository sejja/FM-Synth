import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.function.Function;

import javax.swing.*;

public class ARSDViewer extends SynthControl {
	public RefWrapper<Float> attack = new RefWrapper<>(0.2f);
	public RefWrapper<Float> attack_period = new RefWrapper<>(0.2f);
	public RefWrapper<Float> decay = new RefWrapper<>(0.1f);
	public RefWrapper<Float> decay_period = new RefWrapper<>(0.4f);
	public RefWrapper<Float> sustain = new RefWrapper<>(0.1f);
	public RefWrapper<Float> sustain_period = new RefWrapper<>(.6f);
	public RefWrapper<Float> release = new RefWrapper<>(0.f);
	public RefWrapper<Float> release_period = new RefWrapper<>(0.8f);
	
	public ARSDViewer(Synthesizer synth) {
		super(synth);
		AddModulator("Attack", attack);
		AddModulator("Attack Rate", attack_period);
		AddModulator("Decay", decay);
		AddModulator("Decay Rate", decay_period);
		AddModulator("Sustain", sustain);
		AddModulator("Sustain Rate", sustain_period);
		AddModulator("Release", release);
		AddModulator("Release Rate", release_period);
	}
	
	void AddModulator(String name, RefWrapper<Float> param) {
		JLabel volumeParameter = new JLabel("100%");
		volumeParameter.setBounds(0, 0, 50, 25);
		volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
		Utils.phP.addParameterMouseListening(volumeParameter, this, 0.f, 1.f, .01f, param, () ->  {
			volumeParameter.setText(" " + param.val + "%");
			
		});
		add(volumeParameter);
		JLabel volumeText = new JLabel(name);
		volumeText.setBounds(0, 1000, 75, 25);
		add(volumeText);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		final int PAD = 25;
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D)graphics;
		//graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int numSamplse = getWidth() - PAD *2;
		double[] mizedSamples = new double[numSamplse];
		final float total_parts = attack_period.val + decay_period.val + sustain_period.val + release_period.val;
		
		int midY = getHeight()/2;
		int midX = getWidth() / 2;
		graphics2D.clearRect(0, 0, midX * 2, midY * 2);
		Function<Float, Integer> sampleToYCoord = sample ->(int)((midY + sample * (midY - PAD)));
		Function<Float, Integer> sampleToXCoord = sample ->(int)(PAD + sample * (midX - PAD));
		graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
		graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);
		graphics2D.setColor(Color.GREEN);
		
		
		graphics2D.drawLine(sampleToXCoord.apply((float) 0), sampleToYCoord.apply((float) 0), 
				sampleToXCoord.apply((float) attack_period.val * 2), sampleToYCoord.apply((float) -attack.val));
		graphics2D.drawLine(sampleToXCoord.apply((float) attack_period.val* 2), sampleToYCoord.apply((float) -attack.val), 
				sampleToXCoord.apply((float) decay_period.val * 2), sampleToYCoord.apply((float) -decay.val));
		
		System.out.println(attack_period.val);
		System.out.println(sampleToXCoord.apply((float) attack_period.val * 2));
		//graphics2D.drawLine(sampleToXCoord.apply((float) decay_period.val * 2), sampleToYCoord.apply((float) -decay.val), 
		//		sampleToXCoord.apply((float) sustain_period.val * 2), sampleToYCoord.apply((float) -sustain.val));
		//graphics2D.drawLine(sampleToXCoord.apply((float) sustain_period.val * 2), sampleToYCoord.apply((float) -sustain.val), 
		//		sampleToXCoord.apply((float) release_period.val * 2), sampleToYCoord.apply((float) -release.val));
	}
}
