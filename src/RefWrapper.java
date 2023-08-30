//
//	RefWrapper.java
//	Synthesizer
//
//	Created by Diego Revilla on 18/02/23
//	Copyright © 2023 Deusto. All Rights reserved
//

public class RefWrapper<T> {
	public T val;
	
	// ------------------------------------------------------------------------
	/*! Constructor
	*
	*   Constructs a Reference Wrapper (dirty)
	*/ // ---------------------------------------------------------------------
	public RefWrapper(T val) {
		this.val = val;
	}
}
