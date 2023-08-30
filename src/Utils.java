//
//	Utils.java
//	Synthesizer
//
//	Created by Diego Revilla on 16/03/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.border.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import static java.lang.Math.*;

public class Utils {
	protected static Object ParameterHandling;

	// ------------------------------------------------------------------------
	 /*! Handle Procedure
	 *
	 *   Executes a function
	 */ // ---------------------------------------------------------------------
	public static void handleProcedure(Procedure procedure) {
		try {
			procedure.invoke();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class ParameterListenerHelpers {
		public static final Robot mParamenterRobot;
		
		static {
			try {
				mParamenterRobot = new Robot();
			} catch (AWTException e) {
				throw new ExceptionInInitializerError("Cannot construct robot instance");
			}
		}
		
		private ParameterListenerHelpers() {}
		
		// ------------------------------------------------------------------------
		/*! Cursor Mouse Adapter Cursor
		 *
		*   From two datas of the same type, construct a Vector2D
		*/ // ---------------------------------------------------------------------
		private static MouseAdapter CustomMouseAdapterCursor(Component component, SynthControl container) {
			return new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					final Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank_cursor");
					component.setCursor(cursor);
					container.SetMouseClickLocation(e.getLocationOnScreen());
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
					component.setCursor(Cursor.getDefaultCursor());
				}
			};
		}
		
		// ------------------------------------------------------------------------
	 	/*! Add Parameter Mouse Listening
	 	*
	 	*   This parameter now listens to weather the mouse changes it's value
	 	*/ // ---------------------------------------------------------------------
		public static void addParameterMouseListening(Component component, SynthControl container,
				int minVal, int maxVal, int valStep, RefWrapper<Integer> parameter,
				Procedure onchangedprocedure) {
			component.addMouseListener(CustomMouseAdapterCursor(component, container));
			component.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					
					//If we dragged the mouse on the Y axis, hence the value has changed:
					if(container.GetMouseClickLocation().y != e.getYOnScreen()) {
						boolean mouseMovingUp = container.GetMouseClickLocation().y - e.getYOnScreen() > 0;
						
						parameter.val += (mouseMovingUp && parameter.val < maxVal ? 1 : -1) * valStep;
						
						//If we have a valid procedure
						if (onchangedprocedure != null) handleProcedure(onchangedprocedure);
						
						//We should throw something
						mParamenterRobot.mouseMove(container.GetMouseClickLocation().x, container.GetMouseClickLocation().y);
					}
				}
			});
		}

		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static void addParameterMouseListening(Component component, ARSDViewer container, float minVal,
				float maxVal, float valStep, RefWrapper<Float> parameter, Procedure onchangedprocedure) {
			component.addMouseListener(CustomMouseAdapterCursor(component, container));
			component.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					
					//If we dragged the mouse on the Y axis, hence the value has changed:
					if(container.GetMouseClickLocation().y != e.getYOnScreen()) {
						boolean mouseMovingUp = container.GetMouseClickLocation().y - e.getYOnScreen() > 0;
						
						parameter.val += (mouseMovingUp && parameter.val < maxVal ? 1 : -1) * valStep;
						
						//If we have a valid procedure
						if (onchangedprocedure != null) handleProcedure(onchangedprocedure);
						
						//We should throw something	
						mParamenterRobot.mouseMove(container.GetMouseClickLocation().x, container.GetMouseClickLocation().y);
					}
				}
			});
		}
	}
	
	public static class WindowDesign {
		
		// ------------------------------------------------------------------------
		/*! Get Synthesizer Border
		*
		*   Returns the Border used by the synhtesizer components
		*/ // ---------------------------------------------------------------------
		public static Border GetSynthesizerBorder() {
			return BorderFactory.createLineBorder(Color.BLACK);
		}
		
		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static int SampleToYCoordinates(double sample, JPanel panel, int PAD) {
			final int midY = panel.getHeight() / 2;
			return (int)(midY + sample * (midY - PAD));
		}
	}
	
	public static class Math {
		
		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static double offsetTone(double baseFrequency, double frequecyMultiplier) {
			return baseFrequency * pow(2.0, frequecyMultiplier);
		}
		
		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static double frequencyToAngularFrequency(double freq) {
			return 2 * PI * freq;
		}
		
		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static double getKeyFrequency(int keyNum) {
			return pow(root(2, 12), keyNum - 49) * 440 ;
		}
		
		// ------------------------------------------------------------------------
	 	/*! Constructor
	 	*
	 	*   From two datas of the same type, construct a Vector2D
	 	*/ // ---------------------------------------------------------------------
		public static double root(double num, double root) {
			return pow(E, log(num) / root);
		}
	}
}