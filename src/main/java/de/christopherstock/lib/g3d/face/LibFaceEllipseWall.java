/*  $Id: FaceEllipseWall.java 1241 2013-01-02 15:01:12Z jenetic.bytemare@googlemail.com $
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
    public class LibFaceEllipseWall extends LibFace
    {
        private     static  final   long                serialVersionUID        = 7597942973047560428L;

        public                      float               iTextureRotation        = 0.0f;

        public LibFaceEllipseWall( LibDebug aDebug, LibGLTexture aTextureID, float aHorzFaceAngle, float aVertFaceAngle, float aX, float aY, float aZ, float aRadiusX, float aRadiusZ, int aEllipseSegments )
        {
            this( aDebug, aTextureID, aHorzFaceAngle, aVertFaceAngle, aX, aY, aZ, aRadiusX, aRadiusZ, LibMath.getRandom( 0, 360 ), aEllipseSegments );
        }

        public LibFaceEllipseWall( LibDebug aDebug, LibGLTexture aTextureID, float aHorzFaceAngle, float aVertFaceAngle, float aX, float aY, float aZ, float aRadiusX, float aRadiusZ, float textureRotation, int aEllipseSegments )
        {
            //call super-construct
            super( aDebug, new LibVertex( aX, aY, aZ, 0.0f, 0.0f ), aTextureID, LibColors.EWhite, null );

            iTextureRotation = textureRotation;

            //substract 90° from the horz faceAngle ! ( not from vert ! )

            //assign face angle
            setFaceAngleHorz( aHorzFaceAngle - 90.0f );
            setFaceAngleVert( aVertFaceAngle );

            //calculate vertices
            LibVertex[] ret = new LibVertex[ aEllipseSegments ];
            for ( int i = 0; i < aEllipseSegments; ++i )
            {
                float u = ( LibMath.sinDeg(  90.0f + textureRotation + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;
                float v = ( LibMath.cosDeg( -90.0f + textureRotation + i * 360.0f / aEllipseSegments ) + 1.0f ) / 2.0f;

                //turn ?
                float a = 0.0f; //LibMath.getRandom( 0, 360 ); //0.0f; //aHorzFaceAngle;

                LibVertex ve = new LibVertex
                (
                    aX + aRadiusX * LibMath.cosDeg( a ) * LibMath.cosDeg( i * 360.0f / aEllipseSegments ),
                    aY + aRadiusX * LibMath.sinDeg( a ) * LibMath.cosDeg( i * 360.0f / aEllipseSegments ),
                    aZ + aRadiusZ * LibMath.sinDeg( i * 360.0f / aEllipseSegments ),
                    u,
                    v
                );

                //ve.rotateXYZ( 0.0f, 0.0f, 0.0f, new LibVertex( aX, aY, aZ ) );
                //ve.rotateXYZ( aVertFaceAngle * LibMath.sinDeg( aHorzFaceAngle ), aVertFaceAngle * LibMath.cosDeg( aHorzFaceAngle ), aHorzFaceAngle, new LibVertex( aX, aY, aZ ) );
                //ve.rotateXYZ( aVertFaceAngle * LibMath.sinDeg( aHorzFaceAngle ), aVertFaceAngle * LibMath.cosDeg( aHorzFaceAngle ), aHorzFaceAngle, new LibVertex( aX, aY, aZ ) );

                //rotate x ( vert face angle )
                ve.rotateXYZ( iFaceAngleVert, 0.0f, 0.0f, new LibVertex( aX, aY, aZ ) );

                //rotate z ( horz face angle )
                ve.rotateXYZ( 0.0f, 0.0f, iFaceAngleHorz, new LibVertex( aX, aY, aZ ) );

                ret[ i ] = ve;
            }

            //set original vertices
            setOriginalVertices( ret );
        }

        @Override
        protected void setCollisionValues()
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
