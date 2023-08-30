//
//	Wavetable.java
//	Synthesizer
//
//	Created by Diego Revilla on 26/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import java.util.concurrent.ThreadLocalRandom;

enum Wavetable {
	Sine, 
	Square, 
	Saw, 
	Triangle,
	Noise;

	private static final int mBufferSize = Short.MAX_VALUE;
	private final float[] mBuffer = new float[mBufferSize];
	
	static {
		final double FUND_FREQ = 1d /(mBufferSize / (double)Synthesizer.AudioInfo.GetSampleRate());
		
		//Calculate all samples
		for (int i = 0; i < mBufferSize; i++) {
			final double t = i / (double)Synthesizer.AudioInfo.GetSampleRate();
			final double tDivP = t / (1d/ FUND_FREQ);
			Sine.mBuffer[i] = (float)Math.sin(Utils.Math.frequencyToAngularFrequency(FUND_FREQ) * t);
			Square.mBuffer[i] = Math.signum(Sine.mBuffer[i]);
			Saw.mBuffer[i] = (float)(2d * (tDivP - Math.floor(0.5 + tDivP)));
			Triangle.mBuffer[i] = (float)(2d * Math.abs(Saw.mBuffer[i]) - 1);
			Noise.mBuffer[i] = (float)ThreadLocalRandom.current().nextDouble(-1, 1);
		}
	}
	
	// ------------------------------------------------------------------------
	/*! Get Sample Size
	*
	*   Returns the size of the sample buffer
	*/ // ---------------------------------------------------------------------
	public static int GetSampleSize() {
		return mBufferSize;
	}
	
	// ------------------------------------------------------------------------
	/*! Get Samples
	*
	*   Returns the sample pack
	*/ // ---------------------------------------------------------------------
	public float[] GetSamples() {
		return mBuffer;
	}
}
