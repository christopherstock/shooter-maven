
    package de.christopherstock.lib.g3d.face;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Represents a face.
    *******************************************************************************************************************/
    public class LibFaceEllipseFloor extends LibFace
    {
        public LibFaceEllipseFloor(LibDebug debug, LibGLTextureMetaData texture, LibColors col, float x, float y, float z, float radiusX, float radiusY, int ellipseSegments )
        {
            this( debug, texture, col, x, y, z, radiusX, radiusY,LibMath.getRandom( 0, 360 ), ellipseSegments );
        }

        private LibFaceEllipseFloor(LibDebug debug, LibGLTextureMetaData texture, LibColors col, float x, float y, float z, float radiusX, float radiusY, float textureRotation, int ellipseSegments)
        {
            //call super-construct
            super( debug, new LibVertex( x, y, z, 0.0f, 0.0f ), texture, col, null );

            //calculate vertices
            LibVertex[] ret = new LibVertex[ ellipseSegments ];
            for ( int i = 0; i < ellipseSegments; ++i )
            {
                float u = 0.0f;
                float v = 0.0f;

                u = ( LibMath.sinDeg(  90.0f + textureRotation + i * 360.0f / ellipseSegments ) + 1.0f ) / 2.0f;
                v = ( LibMath.cosDeg( -90.0f + textureRotation + i * 360.0f / ellipseSegments ) + 1.0f ) / 2.0f;
              //v = ( LibMath.cosDeg(  90.0f + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;

                ret[ i ] = new LibVertex
                (
                    x + radiusX * LibMath.cosDeg( i * 360.0f / ellipseSegments ),
                    y + radiusY * LibMath.sinDeg( i * 360.0f / ellipseSegments ),
                    z,
                    u,
                    v
                );
            }

            //set original vertices
            this.setOriginalVertices( ret );
        }

        @Override
        public void setCollisionValues()
        {
            //bullet holes do not have collision values
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
    }
