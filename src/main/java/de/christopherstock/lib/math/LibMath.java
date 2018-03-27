
    package de.christopherstock.lib.math;

    import  java.awt.geom.*;
    import  java.io.*;
    import  javax.vecmath.*;

    /*******************************************************************************************************************
    *   Simple math-wrapper-class.
    *******************************************************************************************************************/
    @SuppressWarnings("PointlessBitwiseExpression")
    public abstract class LibMath
    {
        /***************************************************************************************************************
        *   Delivers the sin-value from a degree-value.
        *
        *   @param  degrees     The degrees to get the sin-value for.
        *   @return             The sin-value from -1.0f to 1.0f of the given degree value.
        ***************************************************************************************************************/
        public static float sinDeg( float degrees )
        {
            return (float)Math.sin( degrees * Math.PI / 180.0f );
        }

        /***************************************************************************************************************
        *   Delivers the cos-value from a degree-value.
        *
        *   @param  degrees     The degrees to get the cos-value for.
        *   @return     The cos-value from -1.0f to 1.0f of the given degree value.
        ***************************************************************************************************************/
        public static float cosDeg( float degrees )
        {
            return (float)Math.cos( degrees * Math.PI / 180.0f );
        }

        /***************************************************************************************************************
        *   Delivers the sin-value from a degree-value as a DOUBLE value.
        *
        *   @param  degrees     The degrees to get the sin-value for.
        *   @return             The sin-value from -1.0f to 1.0f of the given degree value.
        ***************************************************************************************************************/
        public static double sinDegD( double degrees )
        {
            return Math.sin( degrees * Math.PI / 180.0f );
        }

        /***************************************************************************************************************
        *   Delivers the cos-value from a degree-value as a DOUBLE value.
        *
        *   @param  degrees     The degrees to get the cos-value for.
        *   @return     The cos-value from -1.0f to 1.0f of the given degree value.
        ***************************************************************************************************************/
        public static double cosDegD( double degrees )
        {
            return Math.cos( degrees * Math.PI / 180.0f );
        }

        /***************************************************************************************************************
        *   Creates a random integer of the specified range.
        *   There is no need to set a random seed.
        *   {@link Math#random()} does this on the first invocation.
        ***************************************************************************************************************/
        public static int getRandom( int from, int to )
        {
            double  rand    = Math.random() * 1000;
            int     randI   = ( ( (int)rand % ( to + 1 - from ) ) + from );

            return randI;
        }

        public static float[] col2f3(int col )
        {
            float r = ( col & 0xff0000 ) >> 16;
            float g = ( col & 0x00ff00 ) >> 8;
            float b = ( col & 0x0000ff ) >> 0;

            return new float[] { r / 255, g / 255, b / 255 };
        }

        public static float[] col2f4(int col )
        {
            float a = ( col & 0xff000000 ) >> 24;
            float r = ( col & 0x00ff0000 ) >> 16;
            float g = ( col & 0x0000ff00 ) >> 8;
            float b = ( col & 0x000000ff ) >> 0;

            return new float[] { r / 255, g / 255, b / 255, a / 255 };
        }

        public static float getAngleCorrect(Point2D.Float a, Point2D.Float b ) {

            double distX = b.getX() - a.getX();
            double distY = b.getY() - a.getY();
            double angle = 0.0;

            if ( distX == 0.0 )
            {
                if ( distY == 0.0 )     angle = 0.0;
                else if( distY > 0.0 )  angle = Math.PI / 2.0;
                else                    angle = ( Math.PI * 3.0 ) / 2.0;
            }
            else if ( distY == 0.0 )
            {
                if ( distX > 0.0 )      angle = 0.0;
                else                    angle = Math.PI;
            }
            else
            {
                if ( distX < 0.0 )      angle = Math.atan( distY / distX ) + Math.PI;
                else if ( distY < 0.0 ) angle = Math.atan( distY / distX ) + ( 2 * Math.PI );
                else                    angle = Math.atan( distY / distX );
            }

            //to degree
            float ret = (float)( ( angle * 180 ) / Math.PI );
                  ret += 90.0f;

            return normalizeAngle( ret );
        }

        /***************************************************************************************************************
        *   Returns the (nearest) angle distance from angle1 to angle2.
        *
        *   @return     The nearest distance as an absolute value.
        ***************************************************************************************************************/
        public static float getAngleDistanceAbsolute( float angleSrc, float angleDest )
        {
            return Math.abs( getAngleDistanceRelative( angleSrc, angleDest ) );
        }

        /***************************************************************************************************************
        *   Returns the (nearest) angle distance from angle1 to angle2.
        *
        *   @return     The nearest distance as a relative value.
        ***************************************************************************************************************/
        public static float getAngleDistanceRelative( float angleSrc, float angleDest )
        {
            float distance = 0.0f;

            distance = angleDest - angleSrc;
            while ( distance < -180.0f ) distance += 360.0f;
            while ( distance >  180.0f ) distance -= 360.0f;

            return distance;
        }

        public static float normalizeAngle( int aAngle )
        {
            float angle = aAngle;
            while ( angle <    0 ) angle += 360;
            while ( angle >= 360 ) angle -= 360;

            return angle;
        }

        public static float normalizeAngle(float aAngle )
        {
            float angle = aAngle;
            while ( angle <    0.0f ) angle += 360.0f;
            while ( angle >= 360.0f ) angle -= 360.0f;

            return angle;
        }

        public static byte[] intArrayToByteArray(int[] ints )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            for ( int anInt : ints )
            {
                baos.write( ( anInt >> 0  ) & 0xff);
                baos.write( ( anInt >> 8  ) & 0xff);
                baos.write( ( anInt >> 16 ) & 0xff);
                baos.write( ( anInt >> 24 ) & 0xff);
            }

            return baos.toByteArray();
        }

        public static float reachToAbsolute(float aSrc, float aDest, float absoluteDistance )
        {
            float src  = aSrc;
            float dest = aDest;

            //raise src if lower destination
            if ( src < dest )
            {
                src += absoluteDistance;
                if ( src > dest ) src = dest;
            }
            //or decrease if higher
            else if ( src > dest)
            {
                src -= absoluteDistance;
                if ( src < dest ) src = dest;
            }

            return src;
        }

        /***************************************************************************************************************
        *   Check if two double precision numbers are "equal", i.e. close enough
        *   to a given limit.
        *
        *   @param  a           First number to check
        *   @param  b           Second number to check
        *   @param  limit       The definition of "equal"
        *   @return             True if the twho numbers are "equal", false otherwise
        ***************************************************************************************************************/
        public static boolean equals (double a, double b, double limit)
        {
            return Math.abs (a - b) < limit;
        }

        /***************************************************************************************************************
        *   Check if two double precision numbers are "equal", i.e. close enough
        *   to a prespecified limit.
        *
        *   @param  a   First number to check
        *   @param  b   Second number to check
        *   @return     True if the twho numbers are "equal", false otherwise
        ***************************************************************************************************************/
        public static boolean equals (double a, double b)
        {
            return equals (a, b, 1.0e-5);
        }

        /***************************************************************************************************************
        *   Return smallest of four numbers.
        *
        *   @param  a   First number to find smallest among.
        *   @param  b   Second number to find smallest among.
        *   @param  c   Third number to find smallest among.
        *   @param  d   Fourth number to find smallest among.
        *   @return     Smallest of a, b, c and d.
        ***************************************************************************************************************/
        public static double min ( double a, double b, double c, double d )
        {
            return Math.min( Math.min( a, b ), Math.min( c, d ) );
        }

        /***************************************************************************************************************
        *   Return smallest of four numbers.
        *
        *   @param  a   First number to find smallest among.
        *   @param  b   Second number to find smallest among.
        *   @param  c   Third number to find smallest among.
        *   @return     Smallest of a, b and c
        ***************************************************************************************************************/
        public static double min ( double a, double b, double c )
        {
            return Math.min( Math.min ( a, b ), c );
        }

        /***************************************************************************************************************
        *   Return largest of four numbers.
        *
        *   @param  a   First number to find largest among.
        *   @param  b   Second number to find largest among.
        *   @param  c   Third number to find largest among.
        *   @param  d   Fourth number to find largest among.
        *   @return     Largest of a, b, c and d.
        ***************************************************************************************************************/
        public static double max ( double a, double b, double c, double d )
        {
            return Math.max ( Math.max( a, b ), Math.max( c, d ) );
        }

        /***************************************************************************************************************
        *   Return largest of four numbers.
        *
        *   @param  a   First number to find largest among.
        *   @param  b   Second number to find largest among.
        *   @param  c   Third number to find largest among.
        *   @return     Largest of a, b and c.
        ***************************************************************************************************************/
        public static double max ( double a, double b, double c )
        {
            return Math.max ( Math.max( a, b ), c );
        }

        public static Point2D.Float getDistantPoint( Point3d src, float angle, float distance )
        {
            return new Point2D.Float
            (
                (float)src.x - ( distance * cosDeg( angle ) ),
                (float)src.y - ( distance * sinDeg( angle ) )
            );
        }

        @Deprecated
        public static float reachToRelative(float src, float dest, float relativeDistance )
        {
            float angleDistance = getAngleDistanceAbsolute( src, dest );

            return reachToAbsolute( src, dest, angleDistance * relativeDistance );
        }
    }
