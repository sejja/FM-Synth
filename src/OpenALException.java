//
//	OpenALException.java
//	Synthesizer
//
//	Created by Diego Revilla on 12/04/23
//	Copyright © 2023 Deusto. All Rights reserved
//

import static org.lwjgl.openal.AL10.*;

class OpenALException extends RuntimeException {
	private static final long serialVersionUID = 5714729918844782848L;

	// ------------------------------------------------------------------------
	/*! Custom Constructor
	*
	*   Constructs an Audio Exception Class with an integer error code
	*/ // ---------------------------------------------------------------------
	OpenALException(final int errorCode) {
		super("Internal" + (errorCode == AL_INVALID_NAME ? "invalid name" : errorCode == AL_INVALID_ENUM ? "invalid enum"
				: errorCode == AL_INVALID_VALUE ? "invalid value" : errorCode == AL_INVALID_OPERATION ? "invalid operation" : "unknown") 
				+ "OpenAL exception.");
	}
}