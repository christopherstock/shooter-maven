
    package de.christopherstock.lib.g3d.face;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /**************************************************************************************
    *   Represents a triangle face.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LibFaceQuad extends LibFace
    {
        private     static  final   long    serialVersionUID        = -7222146262558021828L;

        /**************************************************************************************
        *   Copy constructor.
        **************************************************************************************/
        public LibFaceQuad( LibDebug aDebug, LibVertex aAnchor, LibVertex aA, LibVertex aB, LibVertex aC, LibVertex aD, LibColors aColor )
        {
            super( aDebug, aAnchor, null, aColor, null );

            //set vertices
            setOriginalVertices( new LibVertex[] { aA, aB, aC, aD, } );
        }

        public boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //empty implementation - debug circles can not be shot :)
            return false;
        }

        public Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            //empty implementation - debug circles can not be shot :)
            return new Vector<Float>();
        }

        @Override
        protected void setCollisionValues()
        {
            //no collisions for this kind of face
        }
    }
