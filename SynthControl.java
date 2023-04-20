import javax.swing.*;
import java.awt.*;

public class SynthControl extends JPanel {
	private Point mouseClickLocation;
	protected boolean on;
	private Synthesizer synth;
	private int mTextYTrack = 172;
	
	public Point getMouseClickLocation() {
		return mouseClickLocation;
	}
	
	public void setMouseClickLocation(Point mouseClickLocation) {
		this.mouseClickLocation = mouseClickLocation;
	}
	
	public boolean isOn() {
		return on;
	}
	 
	public SynthControl(Synthesizer synth) {
		this.synth = synth;
	}
	
	public void setOn(boolean on) {
		this.on = on;
	}
	
	void AddText(String text) {
		JLabel toneText = new JLabel(text);
		toneText.setBounds(mTextYTrack, 40, 75, 25);
		add(toneText);
		
		mTextYTrack += 50;
	}
	
	@Override
	public Component add(Component component) {
		 component.addKeyListener(synth.getKeyAdapter());
		 return super.add(component);
	}
}
