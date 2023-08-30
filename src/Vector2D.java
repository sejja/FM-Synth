//
//	Vector2D.java
//	Synthesizer
//
//	Created by Diego Revilla on 18/05/23
//	Copyright © 2023 Deusto. All Rights reserved
//

public class Vector2D <T> {
    public T x;
    public T y;

    // ------------------------------------------------------------------------
 	/*! Constructor
 	*
 	*   From two datas of the same type, construct a Vector2D
 	*/ // ---------------------------------------------------------------------
    public Vector2D(T x, T y) {
        this.x = x;
        this.y = y;
    }

    // ------------------------------------------------------------------------
 	/*! Constructor
 	*
 	*   Copy another vector's values
 	*/ // ---------------------------------------------------------------------
    public Vector2D(Vector2D<T> v) {
    	this.x = v.x;
        this.y = v.y;
    }

    // ------------------------------------------------------------------------
 	/*! To String
 	*
 	*   Prints the Vector values within a string format
 	*/ // ---------------------------------------------------------------------
    @Override
    public String toString() {
        return "Vector2d[" + x + ", " + y + "]";
    }
}