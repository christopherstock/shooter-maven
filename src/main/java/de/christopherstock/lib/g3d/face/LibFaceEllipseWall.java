
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
    public class LibFaceEllipseWall extends LibFace
    {
        public                      float               iTextureRotation        = 0.0f;

        public LibFaceEllipseWall(LibDebug debug, LibGLTextureMetaData textureID, float horzFaceAngle, float vertFaceAngle, float x, float y, float z, float radiusX, float radiusZ, int ellipseSegments )
        {
            this( debug, textureID, horzFaceAngle, vertFaceAngle, x, y, z, radiusX, radiusZ, LibMath.getRandom( 0, 360 ), ellipseSegments );
        }

        public LibFaceEllipseWall(LibDebug debug, LibGLTextureMetaData textureID, float horzFaceAngle, float vertFaceAngle, float x, float y, float z, float radiusX, float radiusZ, float textureRotation, int ellipseSegments )
        {
            //call super-construct
            super( debug, new LibVertex( x, y, z, 0.0f, 0.0f ), textureID, LibColors.EWhite, null );

            this.iTextureRotation = textureRotation;

            //substract 90° from the horz faceAngle ! ( not from vert ! )

            //assign face angle
            this.setFaceAngleHorz( horzFaceAngle - 90.0f );
            this.setFaceAngleVert( vertFaceAngle );

            //calculate vertices
            LibVertex[] ret = new LibVertex[ ellipseSegments ];
            for ( int i = 0; i < ellipseSegments; ++i )
            {
                float u = ( LibMath.sinDeg(  90.0f + textureRotation + i * 360.0f / ellipseSegments ) + 1.0f ) / 2.0f;
                float v = ( LibMath.cosDeg( -90.0f + textureRotation + i * 360.0f / ellipseSegments ) + 1.0f ) / 2.0f;

                //turn ?
                float a = 0.0f; //LibMath.getRandom( 0, 360 ); //0.0f; //aHorzFaceAngle;

                LibVertex ve = new LibVertex
                (
                    x + radiusX * LibMath.cosDeg( a ) * LibMath.cosDeg( i * 360.0f / ellipseSegments ),
                    y + radiusX * LibMath.sinDeg( a ) * LibMath.cosDeg( i * 360.0f / ellipseSegments ),
                    z + radiusZ * LibMath.sinDeg( i * 360.0f / ellipseSegments ),
                    u,
                    v
                );

                //ve.rotateXYZ( 0.0f, 0.0f, 0.0f, new LibVertex( aX, aY, aZ ) );
                //ve.rotateXYZ( aVertFaceAngle * LibMath.sinDeg( aHorzFaceAngle ), aVertFaceAngle * LibMath.cosDeg( aHorzFaceAngle ), aHorzFaceAngle, new LibVertex( aX, aY, aZ ) );
                //ve.rotateXYZ( aVertFaceAngle * LibMath.sinDeg( aHorzFaceAngle ), aVertFaceAngle * LibMath.cosDeg( aHorzFaceAngle ), aHorzFaceAngle, new LibVertex( aX, aY, aZ ) );

                //rotate x ( vert face angle )
                ve.rotateXYZ(this.faceAngleVert, 0.0f, 0.0f, new LibVertex( x, y, z ) );

                //rotate z ( horz face angle )
                ve.rotateXYZ( 0.0f, 0.0f, this.faceAngleHorz, new LibVertex( x, y, z ) );

                ret[ i ] = ve;
            }

            //set original vertices
            this.setOriginalVertices( ret );
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
