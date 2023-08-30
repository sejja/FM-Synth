//
//	AudioThread.java
//	Synthesizer
//
//	Created by Diego Revilla on 21/03/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import java.util.function.Supplier;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class AudioThread extends Thread {
    static final int BUFFER_SIZE = 512;
    static final int BUFFER_COUNT = 8;

    private final Supplier<short[]> mBufferSupplier;
    private final int[] mBuffers = new int[BUFFER_COUNT];
    private final long mDevice = alcOpenDevice(alcGetString(0,ALC_DEFAULT_DEVICE_SPECIFIER));
    private final long mContext = alcCreateContext(mDevice,new int[1]);
    private final int mSource;
    private int mBufferIndex;
    private boolean mClosed;
    private boolean mRunning;

    public class AudioThreadException extends Exception {
		private static final long serialVersionUID = -807720991762229112L;

		// ------------------------------------------------------------------------
    	/*! Custom Constructor
    	*
    	*   Constructs an Audio Thread Exception with a description
    	*/ // ---------------------------------------------------------------------
    	AudioThreadException(String s) {
    		super(s);
    	}
    }
    
	// ------------------------------------------------------------------------
	/*! Custom Constructor
	*
	*   Constructs an Audio Thread Class with a buffer supplier
	*/ // ---------------------------------------------------------------------
    AudioThread(Supplier<short[]> bufferSupplier) {
        mBufferSupplier = bufferSupplier;
        alcMakeContextCurrent(mContext);
        AL.createCapabilities(ALC.createCapabilities(mDevice));
        mSource = alGenSources();
        
        //Create samples on the Sound Card's Buffer
        for (int i = 0; i < BUFFER_COUNT; i++) BufferSamples(new short[0]);
        alSourcePlay(mSource);
        CatchInternalException();
        start();
    }

    // ------------------------------------------------------------------------
 	/*! Get Is Running
 	*
 	*   Returns whether the Audio thread is Running or not
 	*/ // ---------------------------------------------------------------------
    boolean GetIsRunning() {
        return mRunning;
    }

    // ------------------------------------------------------------------------
  	/*! Run
  	*
  	*   Returns whether the Audio thread is Running or not
  	*/ // ---------------------------------------------------------------------
    @Override
    public synchronized void run() {
        while (!mClosed) {
            while (!mRunning) {
                Utils.handleProcedure(this::wait);
            }
            
            //For each buffer, refresh the samples and send them into the audio card
            for (int i = 0, p = alGetSourcei(mSource, AL_BUFFERS_PROCESSED); i < p; i++) {
            	final short[] Run = mBufferSupplier.get();
                if (Run != null) {
                	alDeleteBuffers(alSourceUnqueueBuffers(mSource));
                    mBuffers[mBufferIndex] = alGenBuffers();
                    BufferSamples(Run);
                } else {
                	mRunning = false;
                	break;
                }
            }
            
            //If we have correct buffers, play them
            if (alGetSourcei(mSource, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(mSource);
            CatchInternalException();
       }
       
       alDeleteSources(mSource);
       alDeleteBuffers(mBuffers);
       alcDestroyContext(mContext);
       alcCloseDevice(mDevice);
    }

    // ------------------------------------------------------------------------
    /*! Run
   	*
   	*   Returns whether the Audio thread is Running or not
   	*/ // ---------------------------------------------------------------------
    synchronized void TriggerPlayback() {
        if(!mClosed) {
            mRunning = true;
            notify();
        }
    }

    // ------------------------------------------------------------------------
    /*! Close
   	*
   	*   Closes the thread and stops audio buffers from refreshing
   	*/ // ---------------------------------------------------------------------
    void close() {
        mClosed = true;
        mRunning = true;
        System.out.println("Closed");
    }

    // ------------------------------------------------------------------------
    /*! Buffer Samples
   	*
   	*   From an array of Samples, create Sound Card Buffers and Queue them to the output
   	*/ // ---------------------------------------------------------------------
    private void BufferSamples(short[] samples) {
        int buf = mBuffers[mBufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, Synthesizer.AudioInfo.GetSampleRate());
        alSourceQueueBuffers(mSource, buf);
        mBufferIndex %= BUFFER_COUNT;
    }

    // ------------------------------------------------------------------------
    /*! Catch Internal Exception
   	*
   	*   In case something went wrong with OpenAL
   	*/ // ---------------------------------------------------------------------
    private void CatchInternalException() {
    	int err = alcGetError(mDevice);
        if (err != ALC_NO_ERROR) {
            throw new OpenALException(err);
        }
    }
}