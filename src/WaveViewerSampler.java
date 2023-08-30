//
//	WaveViewerSampler.java
//	Synthesizer
//
//	Created by Diego Revilla on 17/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class WaveViewerSampler extends JPanel {
	private static final long serialVersionUID = 9168685175067733826L;
	private double[] mAudioData;
	private Sampler mSampler;
	private Boolean mEnabled = false;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs a Wave Viewer from a file source
	*/ // ---------------------------------------------------------------------
	@SuppressWarnings("serial")
	public WaveViewerSampler() {
		final JCheckBox toogle = new JCheckBox("Activate");
		add(toogle);
		
		toogle.addItemListener(new ItemListener() {    
			@Override
			public void itemStateChanged(ItemEvent e) {
				mEnabled = !mEnabled;
			}    
         });
		
		setBorder(Utils.WindowDesign.GetSynthesizerBorder());
		setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            @SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>)evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            SetFile(droppedFiles.get(0));
		            repaint();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
	
	}
	
	// ------------------------------------------------------------------------
	/*! Play Sound
	*
	*   Plays the sound, if there is one
	*/ // ---------------------------------------------------------------------
	public void PlaySound() {
		if(mEnabled) mSampler.PlaySound();
	}
	
	// ------------------------------------------------------------------------
	/*! Set File
	*
	*   Extracts the Audio Data from the file
	*/ // ---------------------------------------------------------------------
	public void SetFile(File inputFile) {
		mSampler = new Sampler();
		try {
			this.mAudioData = mSampler.Extract(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.repaint();
	}
	
	// ------------------------------------------------------------------------
	/*! Get Sampler
	*
	*   Returns a reference to the internal sampler
	*/ // ---------------------------------------------------------------------
	public Sampler GetSampler() {
		return mSampler;
	}
	
	// ------------------------------------------------------------------------
	/*! Paint Component
	*
	*   Paints the component, showing the wave form of the loaded sound
	*/ // ---------------------------------------------------------------------
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		final int PAD = 25;
		final Graphics2D graphics2D = (Graphics2D)graphics;
		final int sampleBufferSize = getWidth() - PAD *2;
		final int midY = getHeight()/2;
		
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
		graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);
		graphics2D.setColor(Color.GREEN);
		
		//If we have loaded audio before
		if(mAudioData != null) {
			ArrayList<Integer> plotdata = new ArrayList<>();
			Arrays.stream(mAudioData).forEach((dobl) -> plotdata.add(Utils.WindowDesign.SampleToYCoordinates(dobl * 2 / Short.MAX_VALUE, this, PAD)));
			
			//Draw each of the components
			for(int i = sampleBufferSize - 1, x = 0; i >= 0; i--, x++) {
				graphics2D.drawLine(PAD + x, plotdata.get(i), PAD + x + 1, (i == sampleBufferSize - 1 ? plotdata.get(i) : plotdata.get(i + 1)));
			}	
		}
	}
}
