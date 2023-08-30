//
//	MidiReceiver.java
//	Synthesizer
//
//	Created by Diego Revilla on 19/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import javax.sound.midi.*;

public class MidiReceiver {
	// ------------------------------------------------------------------------
	/*! Custom Constructor
	*
	*   Constructs a Midi Receiver from a parent synthesizer
	*/ // ---------------------------------------------------------------------
    public MidiReceiver(Synthesizer listener) { 
        for(MidiDevice.Info in : MidiSystem.getMidiDeviceInfo()) {
        	try {
        		final MidiDevice device = MidiSystem.getMidiDevice(in);
                final MidiInputReceiver mir =  new MidiInputReceiver(listener);
                device.getTransmitter().setReceiver(mir);
                device.getTransmitters().stream().forEach((x) -> x.setReceiver(mir));
                device.open();
        	} catch (MidiUnavailableException e) {
        		System.err.println("Error encountered at Midi Device: " + in.getName());
            	e.printStackTrace();
        	}
        }
    }
    
    public class MidiInputReceiver implements Receiver {
    	private Synthesizer mSynth;
   
    	// ------------------------------------------------------------------------
    	/*! Custom Constructor
    	*
    	*   Receives a synthesizer as a listener, which will play the notes later
    	*/ // ---------------------------------------------------------------------
    	public MidiInputReceiver(Synthesizer synth) {
    		mSynth = synth;
    	}
    
    	// ------------------------------------------------------------------------
    	/*! Send
    	*
    	*   Sends a note to play on the synthesizer (listener)
    	*/ // ---------------------------------------------------------------------
    	public void send(MidiMessage msg, long timeStamp) {  
    		if(msg instanceof ShortMessage) {
    	        final ShortMessage sm = (ShortMessage) msg;

    	        if (sm.getCommand() == javax.sound.midi.ShortMessage.NOTE_ON) {
    	            if(sm.getData2() > 0)
    	            	mSynth.PlayNote(mSynth.GetKeyID(sm.getData1() - 36));
    	            else
    	            	mSynth.StopSound();
    	        }
    	    }
    	}
    	
    	// ------------------------------------------------------------------------
    	/*! Stop
    	*
    	*   Unused Function
    	*/ // ---------------------------------------------------------------------
    	public void close() {}
    }
}