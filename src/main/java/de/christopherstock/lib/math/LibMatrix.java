
    package de.christopherstock.lib.math;

    import  javax.vecmath.*;
    import  de.christopherstock.lib.g3d.*;

    /*******************************************************************************************************************
    *   What is the matrix?
    *   Simple math-class exclusively containg static functionality for transforming matrices.
    *******************************************************************************************************************/
    public class LibMatrix
    {
        @SuppressWarnings( "unused" )
        private     static  final   double[][]                      UNIT_MATRIX         = new double[][]
        {
            new double[] {   1,  0,  0,  0,   },
            new double[] {   0,  1,  0,  0,   },
            new double[] {   0,  0,  1,  0,   },
            new double[] {   0,  0,  0,  1,   },
        };

        private                     double[][]                       matrix              = null;

        public LibMatrix( double angleX, double angleY, double angleZ )
        {
          //double[][] transformationMatrix =                     getTranslationMatrix( 0.0f, 0.0f, 0.0f );

            matrix  =                     getAxisRotXMatrix( angleX );
            matrix  = LibMatrix.multiply( getAxisRotYMatrix( angleY ), matrix );
            matrix  = LibMatrix.multiply( getAxisRotZMatrix( angleZ ), matrix );
        }

        public final void transformVertices( LibVertex[] va, LibVertex ank )
        {
            for ( int i = 0; i < va.length; ++i )
            {
                va[ i ] = transformVertexF( va[ i ], ank );
            }
        }

        public final LibVertex transformVertexF( LibVertex vertex, LibVertex ank )
        {
            //translate by anchor
            vertex.x -= ank.x;
            vertex.y -= ank.y;
            vertex.z -= ank.z;

            //assign transformation matrix
            double oldX = vertex.x;
            double oldY = vertex.y;
            double oldZ = vertex.z;
            vertex.x = (float)( oldX * matrix[ 0 ][ 0 ] + oldY * matrix[ 1 ][ 0 ] + oldZ * matrix[  2 ][ 0 ] + matrix[  3 ][ 0 ] );
            vertex.y = (float)( oldX * matrix[ 0 ][ 1 ] + oldY * matrix[ 1 ][ 1 ] + oldZ * matrix[  2 ][ 1 ] + matrix[  3 ][ 1 ] );
            vertex.z = (float)( oldX * matrix[ 0 ][ 2 ] + oldY * matrix[ 1 ][ 2 ] + oldZ * matrix[  2 ][ 2 ] + matrix[  3 ][ 2 ] );

            //translate back by anchor
            vertex.x += ank.x;
            vertex.y += ank.y;
            vertex.z += ank.z;

            return vertex;
        }

        public final Point3d transformVertexD( Point3d vertex, Point3d ank )
        {
            //translate by anchor
            vertex.x -= ank.x;
            vertex.y -= ank.y;
            vertex.z -= ank.z;

            //assign transformation matrix
            double oldX = vertex.x;
            double oldY = vertex.y;
            double oldZ = vertex.z;
            vertex.x = (float)( oldX * matrix[ 0 ][ 0 ] + oldY * matrix[ 1 ][ 0 ] + oldZ * matrix[  2 ][ 0 ] + matrix[  3 ][ 0 ] );
            vertex.y = (float)( oldX * matrix[ 0 ][ 1 ] + oldY * matrix[ 1 ][ 1 ] + oldZ * matrix[  2 ][ 1 ] + matrix[  3 ][ 1 ] );
            vertex.z = (float)( oldX * matrix[ 0 ][ 2 ] + oldY * matrix[ 1 ][ 2 ] + oldZ * matrix[  2 ][ 2 ] + matrix[  3 ][ 2 ] );

            //translate back by anchor
            vertex.x += ank.x;
            vertex.y += ank.y;
            vertex.z += ank.z;

            return vertex;
        }

        @SuppressWarnings( "unused" )
        private static final double[][] getTranslationMatrix( double x, double y, double z )
        {
            return new double[][]
            {
                new double[] { 1, 0, 0, 0 },
                new double[] { 0, 1, 0, 0 },
                new double[] { 0, 0, 1, 0 },
                new double[] { x, y, z, 1 },
            };
        }

        @SuppressWarnings( "unused" )
        private static final double[][] getScalationMatrix( double x, double y, double z )
        {
            return new double[][]
            {
                new double[] { x, 0, 0, 0 },
                new double[] { 0, y, 0, 0 },
                new double[] { 0, 0, z, 0 },
                new double[] { 0, 0, 0, 1 },
            };
        }

        @Deprecated
        @SuppressWarnings( "unused" )
        private static final double[][] getVertexRotXMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   1,    0,     0,      0   },
                new double[] {   0,    cos,   -sin,   0   },
                new double[] {   0,    sin,   cos,    0   },
                new double[] {   0,    0,     0,      1   },
            };
            return newMatrix;
        }

        @Deprecated
        @SuppressWarnings( "unused" )
        private static final double[][] getVertexRotYMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   cos,    0,      sin,    0   },
                new double[] {   0,      1,      0,      0   },
                new double[] {   -sin,   0,      cos,    0   },
                new double[] {   0,      0,      0,      1   },
            };
            return newMatrix;
        }

        @Deprecated
        @SuppressWarnings( "unused" )
        private static final double[][] getVertexRotZMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   cos,    -sin,       0,      0   },
                new double[] {   sin,    cos,        0,      0   },
                new double[] {   0,      0,          1,      0   },
                new double[] {   0,      0,          0,      1   },
            };
            return newMatrix;
        }

        private static final double[][] getAxisRotXMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   1,    0,     0,      0   },
                new double[] {   0,    cos,   sin,    0   },
                new double[] {   0,   -sin,   cos,    0   },
                new double[] {   0,    0,     0,      1   },
            };
            return newMatrix;
        }

        private static final double[][] getAxisRotYMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   cos,    0,      -sin,   0   },
                new double[] {   0,      1,      0,      0   },
                new double[] {   sin,    0,      cos,    0   },
                new double[] {   0,      0,      0,      1   },
            };
            return newMatrix;
        }

        private static final double[][] getAxisRotZMatrix( double angle )
        {
            double sin = LibMath.sinDegD( angle );
            double cos = LibMath.cosDegD( angle );
            double[][] newMatrix = new double[][]
            {
                new double[] {   cos,    sin,        0,      0   },
                new double[] {   -sin,   cos,        0,      0   },
                new double[] {   0,      0,          1,      0   },
                new double[] {   0,      0,          0,      1   },
            };
            return newMatrix;
        }

        private static final double[][] multiply( double[][] matrix1, double[][] matrix2 )
        {
            double[][] ret = new double[][]
            {
                new double[]     {   0, 0, 0, 0, },
                new double[]     {   0, 0, 0, 0, },
                new double[]     {   0, 0, 0, 0, },
                new double[]     {   0, 0, 0, 0, },
            };

            //multiply and store in ret
            for( int j = 0; j < 4; j++ )
            {
                for( int i = 0; i < 4; i++ )
                {
                    ret[ i ][ j ] = 0;
                    for ( int n = 0; n < 4; n++ )
                    {
                        ret[ i ][ j ] += matrix1[ i ][ n ] * matrix2[ n ][ j ];
                    }
                }
            }

            return ret;
        }
    }
