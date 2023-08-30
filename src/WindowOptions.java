//
//	WindowOptions.java
//	Synthesizer
//
//	Created by Diego Revilla on 25/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;

public class WindowOptions extends JPanel implements ActionListener {
	private static final long serialVersionUID = 4507820470528041488L;
	final HashSet<Serializable> serials = new HashSet<>();
	final JFrame mParent;
	private Image mBackground;
	
	public class WinOptException extends Exception {
		private static final long serialVersionUID = -847294260803039379L;
		private final String id;
		
		// ------------------------------------------------------------------------
		/*! Constructor
		*
		*   constructs a Windows Options Exception from a string
		*/ // ---------------------------------------------------------------------
		public WinOptException(String what) {
			id = what;
		}
		
		// ------------------------------------------------------------------------
		/*! To String
		*
		*   Outputs the Exception as a String
		*/ // ---------------------------------------------------------------------
		public String toString() {
			return "Error: " + id;
		}
	}
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs the Options Panel
	*/ // ---------------------------------------------------------------------
	public WindowOptions(JFrame parent, Vector2D<Integer> dim) {
		super(new BorderLayout());
		try {
			mBackground = ImageIO.read(getClass().getResource("content/DeustoSynth.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mParent = parent;
		
		JToolBar toolBar = new JToolBar("Still draggable");	
		final ActionListener saveActionListener = SaveActionListener();
		final ActionListener loadActionListener = LoadActionListener();
		
		try {
			JButton save = MakeButton("C:/Users/Usuario/git/Java/Ejercicios/src/todoo/Save.png", "Saved", "Save", saveActionListener);
			toolBar.add(save);
	        JButton load = MakeButton("C:/Users/Usuario/git/Java/Ejercicios/src/todoo/Save.png", "Loaded", "Load", loadActionListener);	
	        toolBar.add(load);
		} catch(WinOptException e) {
			e.printStackTrace();
		}
		
        setPreferredSize(new Dimension(dim.x, dim.y));
        add(toolBar, BorderLayout.PAGE_START);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = this.getSize().width;
		int height = this.getSize().height;

		if (this.mBackground != null) {
			g.drawImage(this.mBackground, 0, 0, width, height, null);
		}
	}

	// ------------------------------------------------------------------------
	/*! Add Serializable
	*
	*   Adds a Component to be serializable
	*/ // ---------------------------------------------------------------------
	public void AddSerializable(Serializable serial) throws WindowOptions.WinOptException {
		//If we have already added it
		if(!serials.add(serial)) throw new WinOptException("Object already added");
	}
	
	// ------------------------------------------------------------------------
	/*! Remove Serializable
	*
	*   Removes a Serializable from being serializable
	*/ // ---------------------------------------------------------------------
	public void RemoveSerializable(Serializable serial) throws WindowOptions.WinOptException {
		//If there is no such object
		if(!serials.remove(serial)) throw new WinOptException("Object was not previously serialized");
	}
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs the Options Panel
	*/ // ---------------------------------------------------------------------
    private JButton MakeButton(String imageName, String actionCommand, String altText, ActionListener action) throws WinOptException {
        final URL imageURL = SynthControl.class.getResource(imageName);
       
        final JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        button.addActionListener(action);
        
        //image found
        if (imageURL != null)
            button.setIcon(new ImageIcon(imageURL, altText));
       else
            button.setText(altText);
        return button;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// ------------------------------------------------------------------------
	/*! Save Action Listener
	*
	*   Constructs the Options Panel
	*/ // ---------------------------------------------------------------------
	private ActionListener SaveActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final FileDialog fd = new FileDialog(mParent, "Choose a file", FileDialog.SAVE);
				fd.setDirectory("C:\\");
				fd.setFile("*.dsynth");
				fd.setVisible(true);
				final String filename = fd.getDirectory() + fd.getFile() + ".dsynth";
		
				//If there is indeed a filename
				if (filename != null) {
					String total = "";
					for(var x : serials)total += x.Serialize();
					
					BufferedWriter bufwriter = null;
					
					try {
						bufwriter = new BufferedWriter(new FileWriter(filename));
				        bufwriter.write(total);
					} catch (Exception e2) {
				        e2.printStackTrace();
				    } finally {
				    	try {
							bufwriter.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				    }
				}	
			}
		};
	}
	
	// ------------------------------------------------------------------------
	/*! Load Action Listener
	*
	*   Creates an action listener to load files with a file dialog
	*/ // ---------------------------------------------------------------------
	private ActionListener LoadActionListener() {
		return  new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final FileDialog fd = new FileDialog(mParent, "Choose a file", FileDialog.LOAD);
				fd.setDirectory("C:\\");
				fd.setFile("*.dsynth");
				fd.setVisible(true);
				final String filename = fd.getDirectory() + fd.getFile();
				
				//If we selected a file
				if (filename != null) {
			        Scanner fileToRead = null;
			        try {
			            fileToRead = new Scanner(new File(filename));
			            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null; ) {
			            	
			            	for(var x : serials)
			                	x.Fetch(line);
			            }
			        } catch (FileNotFoundException ex) {
			        	ex.printStackTrace();
			        } finally {
			            fileToRead.close();
			        }
				}
			}
		};
	}
}
