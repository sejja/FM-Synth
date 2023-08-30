//
//	Sampler.java
//	Synthesizer
//
//	Created by Diego Revilla on 16/03/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Sampler {
	private static final int mBufferSize = Short.MAX_VALUE;
	private Boolean mStop = false;
	private File mFile = null;
	private double mData[] = null;
	private ArrayList<SamplerSoundThread> mThreads = new ArrayList<>();

	private class SamplerSoundThread extends Thread {
		AudioInputStream mAudioStream;
		SourceDataLine mSourceLine;
		
		// ------------------------------------------------------------------------
		/*! Constructor
		*
		*   Constructs a thread with a data strean abd a source data line
		*/ // ---------------------------------------------------------------------
		SamplerSoundThread(AudioInputStream audiostream, SourceDataLine sourceline) {
			mAudioStream = audiostream;
			mSourceLine = sourceline;
		}
		
		// ------------------------------------------------------------------------
		/*! Play Sound
		*
		*   Plays the sound of a loaded sound
		*/ // ---------------------------------------------------------------------
		@Override
	    public void run() {
			mSourceLine.start();
			
			int nBytesRead = 0;
			byte[] abData = new byte[mBufferSize];
			
			//Read all bytes until it ends, and there is a bug because it should end if mStop is true
	        while (nBytesRead != -1 && !mStop) {
	            try {
	                nBytesRead = mAudioStream.read(abData, 0, abData.length);
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            if (nBytesRead >= 0) {
	                @SuppressWarnings("unused")
	                int nBytesWritten = mSourceLine.write(abData, 0, nBytesRead);
	            }
	        }
	        
	        mThreads.remove(this);
	    }
	}
	
	// ------------------------------------------------------------------------
	/*! Extract
	*
	*   Returns the Sample Rate of the Synthesizer
	*/ // ---------------------------------------------------------------------
	public double[] Extract(File inputFile) throws IOException {
        AudioInputStream in = null;
        mFile = inputFile;
        
        try {
            in = AudioSystem.getAudioInputStream(mFile);
        } catch (Exception e) {
            return new double[0];
        }
        
        AudioFormat format = in.getFormat();
        byte[] audioBytes = in.readAllBytes();
        
        //If the format is a 16 bit
        if (format.getSampleSizeInBits() == 16) {
        	
            final int samplesLength = audioBytes.length / 2;
            mData = new double[samplesLength];
            
            //Format with big or little endianesss
            if (format.isBigEndian()) {
                for (int i = 0; i < samplesLength; ++i)
                	mData[i] = audioBytes[i * 2] << 8 | (255 & audioBytes[i * 2 + 1]);
            } else {
                for (int i = 0; i < samplesLength; i += 2)
                	mData[i / 2] = audioBytes[i * 2 + 1] << 8 | (255 & audioBytes[i * 2]);
            }
        } else {
           final int samplesLength = audioBytes.length;
           mData = new double[samplesLength];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < samplesLength; ++i)
                	mData[i] = audioBytes[i];
            } else {
                for (int i = 0; i < samplesLength; ++i)
                	mData[i] = audioBytes[i] - 128;
            }
        }

        return mData;
    }
	
	// ------------------------------------------------------------------------
	/*! Stop Sound
	 *
	*   Stops the sound from being played
	*/ // ---------------------------------------------------------------------
	public void StopSound() {
		mStop = true;
	}
	
	// ------------------------------------------------------------------------
	/*! Play Sound
	*
	*   Plays the sound of a loaded sound
	*/ // ---------------------------------------------------------------------
	public void PlaySound(){
		mStop = false;

        AudioInputStream audioStream = null;
		try {
            audioStream = AudioSystem.getAudioInputStream(mFile);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        AudioFormat audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceLine = null;
		try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

		SamplerSoundThread th = new SamplerSoundThread(audioStream, sourceLine);
		th.start();
		mThreads.add(th);
	}
}
