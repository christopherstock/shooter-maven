/*  $Id: FaceEllipseFloor.java 1241 2013-01-02 15:01:12Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.g3d.face;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /**************************************************************************************
    *   Represents a face.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LibFaceEllipseFloor extends LibFace
    {
        private     static      final       long    serialVersionUID    = 7078400047554092109L;

        public LibFaceEllipseFloor( LibDebug aDebug, LibGLTexture aTexture, LibColors aCol, float aX, float aY, float aZ, float aRadiusX, float aRadiusY, int aEllipseSegments )
        {
            this( aDebug, aTexture, aCol, aX, aY, aZ, aRadiusX, aRadiusY,LibMath.getRandom( 0, 360 ), aEllipseSegments );
        }

        public LibFaceEllipseFloor( LibDebug aDebug, LibGLTexture aTexture, LibColors aCol, float aX, float aY, float aZ, float aRadiusX, float aRadiusY, float textureRotation, int aEllipseSegments )
        {
            //call super-construct
            super( aDebug, new LibVertex( aX, aY, aZ, 0.0f, 0.0f ), aTexture, aCol, null );

            //calculate vertices
            LibVertex[] ret = new LibVertex[ aEllipseSegments ];
            for ( int i = 0; i < aEllipseSegments; ++i )
            {
                float u = 0.0f;
                float v = 0.0f;

                u = ( LibMath.sinDeg(  90.0f + textureRotation + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;
                v = ( LibMath.cosDeg( -90.0f + textureRotation + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;
              //v = ( LibMath.cosDeg(  90.0f + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;

                ret[ i ] = new LibVertex
                (
                    aX + aRadiusX * LibMath.cosDeg( i * 360.0f / aEllipseSegments ),
                    aY + aRadiusY * LibMath.sinDeg( i * 360.0f / aEllipseSegments ),
                    aZ,
                    u,
                    v
                );
            }

            //set original vertices
            setOriginalVertices( ret );
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
