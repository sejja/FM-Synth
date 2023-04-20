import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class WaveViewer extends JPanel {
	private Oscillator[] oscillators;
	
	public WaveViewer(Oscillator[] oscillator) {
		System.out.println("WaveViewer Cons");
		this.oscillators = oscillator;
		setBorder(Utils.WindowDesign.LINE_BORDER);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		final int PAD = 25;
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D)graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int numSamplse = getWidth() - PAD *2;
		double[] mizedSamples = new double[numSamplse];
		
		for(Oscillator oscillator : this.oscillators) {
			double[] samples = oscillator.GetSampleWaveForm(numSamplse);
		
			for(int i = 0; i < samples.length; i++) {
				mizedSamples[i] += samples[i] / oscillators.length;
			}
		}
		
		int midY = getHeight()/2;
		Function<Double, Integer> sampleToYCoord = sample ->(int)(midY + sample * (midY - PAD));
		graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
		graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);
		graphics2D.setColor(Color.GREEN);
		
		for(int i = 0; i < numSamplse; i++) {
			int nextY = i == numSamplse - 1 ? sampleToYCoord.apply(mizedSamples[i]) : sampleToYCoord.apply(mizedSamples[i + 1]);
			graphics2D.drawLine(PAD + i, sampleToYCoord.apply(mizedSamples[i]), PAD + i + 1, nextY);
		}
	}
}
