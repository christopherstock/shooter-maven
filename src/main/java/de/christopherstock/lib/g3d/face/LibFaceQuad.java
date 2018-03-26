
    package de.christopherstock.lib.g3d.face;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Represents a triangle face.
    *******************************************************************************************************************/
    public class LibFaceQuad extends LibFace
    {
        /***************************************************************************************************************
        *   Copy constructor.
        ***************************************************************************************************************/
        public LibFaceQuad( LibDebug debug, LibVertex anchor, LibVertex a, LibVertex b, LibVertex c, LibVertex d, LibColors color )
        {
            super( debug, anchor, null, color, null );

            //set vertices
            this.setOriginalVertices( new LibVertex[] { a, b, c, d, } );
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
