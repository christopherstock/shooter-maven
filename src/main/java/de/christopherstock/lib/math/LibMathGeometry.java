
    package de.christopherstock.lib.math;

    import  java.awt.geom.*;
    import  javax.vecmath.*;
    import  de.christopherstock.lib.*;

    /*******************************************************************************************************************
    *   A collection of static geometry utility methods.
    *******************************************************************************************************************/
    public final class LibMathGeometry
    {
        /***************************************************************************************************************
        *   Find the intersection of this circle with the line defined by point p1 & p2.
        *
        *   @param      line        The line to check collision with the specified circle.
        *   @param      circle      The circle to check collision with the specified line.
        *   @return                 The NEARER point of intersection seen from Line2D.Float.getP1().
        *                           <code>null</code> if no intersection occured.
        ***************************************************************************************************************/
        public static Point2D.Float findLineCircleIntersection( Line2D.Float line, Ellipse2D.Float circle )
        {
            Point2D.Float p1 = (Point2D.Float)line.getP1();
            Point2D.Float p2 = (Point2D.Float)line.getP2();

            float radius  = (float)circle.getWidth() / 2;
            float circleX = (float)circle.getX() + radius;
            float circleY = (float)circle.getY() + radius;

            //substract line by circle values
            p1.x -= circleX;    p1.y -= circleY;
            p2.x -= circleX;    p2.y -= circleY;

            float dx = p2.x - p1.x;
            float dy = p2.y  - p1.y;
            float dr = (float)Math.sqrt( dx * dx + dy * dy );
            float D  = p1.x * p2.y - p2.x * p1.y;
            float discriminant = radius * radius * dr * dr - D * D;
            if ( discriminant < 0.0f )
            {
                //No intersection;
                return null;
            }
            else if ( discriminant == 0.0f )
            {
                //Tangant line there is only ONE intersection
            }
            else if ( discriminant > 0.0f )
            {
                //The line intersects at TWO points
            }

            //Compute intersection for ONE point (to compute intersection at OTHER point change + to a -)
            float x1 = (float)( ( D  * dy + Math.signum( dy ) * dx * Math.sqrt( discriminant ) ) / ( dr * dr ) );
            float y1 = (float)( ( -D * dx + Math.abs(    dy )      * Math.sqrt( discriminant ) ) / ( dr * dr ) );

            float x2 = (float)( ( D  * dy - Math.signum( dy ) * dx * Math.sqrt( discriminant ) ) / ( dr * dr ) );
            float y2 = (float)( ( -D * dx - Math.abs(    dy )      * Math.sqrt( discriminant ) ) / ( dr * dr ) );

            //translate back
            x1 += circleX;
            y1 += circleY;

            x2 += circleX;
            y2 += circleY;

            //return nearer point to line-point 1
            Point2D.Float ret =
            (
                        line.getP1().distance( new Point2D.Float( x1, y1 ) )
                    <   line.getP1().distance( new Point2D.Float( x2, y2 ) )
                ?   new Point2D.Float( x1, y1 )
                :   new Point2D.Float( x2, y2 )
            );

            return ret;

        }

        /***************************************************************************************************************
        *   Checks if two line-segments intersect and returns the point of intersection.
        *
        *   @param  line1   Line-segment 1.
        *   @param  line2   Line-segment 2.
        *   @return         The point of intersection.
        *                   <code>null</code> if the lines to not intersect.
        ***************************************************************************************************************/
        public static Point2D.Float findLineSegmentIntersection( Line2D.Float line1, Line2D.Float line2, LibDebug debug )
        {
            double[]    intersectionCoords  = new double[ 2 ];
            int         res                 = findLineSegmentIntersection
            (
                line1.getX1(), line1.getY1(),
                line1.getX2(), line1.getY2(),
                line2.getX1(), line2.getY1(),
                line2.getX2(), line2.getY2(),
                intersectionCoords
            );

            //may fail
            if ( res < 1 )
            {
                debug.err( "FATAL!! bug in external class Geometry! no intersection point found!" );
                return null;
            }

            return new Point2D.Float( (float)intersectionCoords[ 0 ], (float)intersectionCoords[ 1 ] );

        }

        /***************************************************************************************************************
        *   Compute the length of the line from (x0,y0) to (x1,y1).
        *
        *   @param      x0  First  line end point x.
        *   @param      y0  First  line end point y.
        *   @param      x1  Second line end point x.
        *   @param      y1  Second line end point y.
        *   @return         Length of line from (x0,y0) to (x1,y1).
        ***************************************************************************************************************/
        private static double length( double x0, double y0, double x1, double y1 )
        {
            double dx = x1 - x0;
            double dy = y1 - y0;

            return Math.sqrt ( dx*dx + dy*dy );
        }

        /***************************************************************************************************************
        *   Compute the intersection between two line segments, or two lines
        *   of infinite length.
        *
        *   @param  x0              X coordinate first end point first line segment.
        *   @param  y0              Y coordinate first end point first line segment.
        *   @param  x1              X coordinate second end point first line segment.
        *   @param  y1              Y coordinate second end point first line segment.
        *   @param  x2              X coordinate first end point second line segment.
        *   @param  y2              Y coordinate first end point second line segment.
        *   @param  x3              X coordinate second end point second line segment.
        *   @param  y3              Y coordinate second end point second line segment.
        *   @param  intersection    Preallocated by caller to double[2]
        *   @return -1              if lines are parallel (x,y unset),
        *           -2              if lines are parallel and overlapping (x, y center)
        *           0               if intesrection outside segments (x,y set)
        *          +1               if segments intersect (x,y set)
        ***************************************************************************************************************/
        private static int findLineSegmentIntersection
        (
            double x0, double y0,
            double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            double[] intersection
        )
        {
            // TO DO: Make limit depend on input domain
            final double LIMIT        = 1e-5;
            final double INFINITY = 1e10;

            double x, y;

            //
            // Convert the lines to the form y = ax + b
            //

            // Slope of the two lines
            double a0 = LibMath.equals (x0, x1, LIMIT) ?
                                    INFINITY : (y0 - y1) / (x0 - x1);
            double a1 = LibMath.equals (x2, x3, LIMIT) ?
                                    INFINITY : (y2 - y3) / (x2 - x3);

            double b0 = y0 - a0 * x0;
            double b1 = y2 - a1 * x2;

            // Check if lines are parallel
            if ( LibMath.equals ( a0, a1 ) )
            {
                if (!LibMath.equals (b0, b1))
                    return -1; // Parallell non-overlapping

                if (LibMath.equals (x0, x1)) {
                    if (Math.min (y0, y1) < Math.max (y2, y3) ||
                            Math.max (y0, y1) > Math.min (y2, y3)) {
                        double twoMiddle = y0 + y1 + y2 + y3 -
                                                             LibMath.min (y0, y1, y2, y3) -
                                                             LibMath.max (y0, y1, y2, y3);
                        y = (twoMiddle) / 2.0;
                        x = (y - b0) / a0;
                    }
                    else return -1;    // Parallell non-overlapping
                }
                else {
                    if (Math.min (x0, x1) < Math.max (x2, x3) ||
                            Math.max (x0, x1) > Math.min (x2, x3)) {
                        double twoMiddle = x0 + x1 + x2 + x3 -
                                                             LibMath.min (x0, x1, x2, x3) -
                                                             LibMath.max (x0, x1, x2, x3);
                        x = (twoMiddle) / 2.0;
                        y = a0 * x + b0;
                    }
                    else return -1;
                }

                intersection[0] = x;
                intersection[1] = y;
                return -2;
            }

            // Find correct intersection point
            if (LibMath.equals (a0, INFINITY)) {
                x = x0;
                y = a1 * x + b1;
            }
            else if (LibMath.equals (a1, INFINITY)) {
                x = x2;
                y = a0 * x + b0;
            }
            else {
                x = - (b0 - b1) / (a0 - a1);
                y = a0 * x + b0;
            }

            intersection[0] = x;
            intersection[1] = y;

            // Then check if intersection is within line segments
            double distanceFrom1;

            if (LibMath.equals (x0, x1)) {
                if (y0 < y1)
                    distanceFrom1 = y < y0 ? LibMathGeometry.length (x, y, x0, y0) :
                                                    y > y1 ? LibMathGeometry.length (x, y, x1, y1) : 0.0;
                else
                    distanceFrom1 = y < y1 ? LibMathGeometry.length (x, y, x1, y1) :
                                                    y > y0 ? LibMathGeometry.length (x, y, x0, y0) : 0.0;
            }
            else {
                if (x0 < x1)
                    distanceFrom1 = x < x0 ? LibMathGeometry.length (x, y, x0, y0) :
                                                    x > x1 ? LibMathGeometry.length (x, y, x1, y1) : 0.0;
                else
                    distanceFrom1 = x < x1 ? LibMathGeometry.length (x, y, x1, y1) :
                                                    x > x0 ? LibMathGeometry.length (x, y, x0, y0) : 0.0;
            }

            double distanceFrom2;

            if ( LibMath.equals ( x2, x3 ) )
            {
                if (y2 < y3)
                    distanceFrom2 = y < y2 ? LibMathGeometry.length (x, y, x2, y2) :
                                                    y > y3 ? LibMathGeometry.length (x, y, x3, y3) : 0.0;
                else
                    distanceFrom2 = y < y3 ? LibMathGeometry.length (x, y, x3, y3) :
                                                    y > y2 ? LibMathGeometry.length (x, y, x2, y2) : 0.0;
            }
            else
            {
                if (x2 < x3)
                    distanceFrom2 = x < x2 ? LibMathGeometry.length (x, y, x2, y2) :
                                                    x > x3 ? LibMathGeometry.length (x, y, x3, y3) : 0.0;
                else
                    distanceFrom2 = x < x3 ? LibMathGeometry.length (x, y, x3, y3) :
                                                    x > x2 ? LibMathGeometry.length (x, y, x2, y2) : 0.0;
            }

            return
            (
                    LibMath.equals (distanceFrom1, 0.0) && LibMath.equals (distanceFrom2, 0.0)
                ?   1
                :   0
            );
        }

        /***************************************************************************************************************
        *   Checks if a line-segment intersects the given circle.
        *
        *   @param  line    The line-segment to check for intersection with circle.
        *   @param  circle  The circle. CAUTION! only the circle's width is used as it's radius!
        *   @return         <code>true</code> if the line intersects the circle in one ( tangent )
        *                   or two ( secant ) points. Otherwise <code>false</code>.
        *   @deprecated     can be replaced by native api.
        ***************************************************************************************************************/
        @Deprecated
        public static boolean isLineIntersectingCircle(Line2D.Float line, Ellipse2D.Float circle )
        {
            //check if the closest distance from the circle's center is lower as it's radius
            return
            (
                line.ptSegDist( new Point2D.Double( circle.getCenterX(), circle.getCenterY() ) ) <= circle.getWidth() / 2
            );
        }

        /**
        *   Returns the point where a line crosses a plane  ..
        *   This would be a milestone ..
        */
        @Deprecated
        public static Point3f getLineFaceIntersectionF(Point3f ray1, Point3f ray2, Point3f plane1, Point3f plane2, Point3f plane3 )
        {
            Vector3f p1 = new Vector3f( plane1 );
            Vector3f p2 = new Vector3f( plane2 );
            Vector3f p3 = new Vector3f( plane3 );
            Vector3f p2minusp1 = new Vector3f( p2 );
            p2minusp1.sub( p1 );
            Vector3f p3minusp1 = new Vector3f( p3 );
            p3minusp1.sub( p1 );
            Vector3f normal = new Vector3f();
            normal.cross( p2minusp1, p3minusp1 );
            // The plane can be defined by p1, n + d = 0
            float d = -p1.dot( normal );
            Vector3f i1 = new Vector3f( ray1 );
            Vector3f direction = new Vector3f( ray1 );
            direction.sub( ray2 );
            float dot = direction.dot( normal );
            if ( dot == 0 ) return null;
            float t = ( -d - i1.dot( normal ) ) / ( dot );
            Vector3f intersection = new Vector3f( ray1 );
            Vector3f scaledDirection = new Vector3f( direction );
            scaledDirection.scale( t );
            intersection.add( scaledDirection );
            Point3f intersectionPoint = new Point3f( intersection );

            float minFaceX = (float)LibMath.min( plane1.x, plane2.x, plane3.x );
            float minFaceY = (float)LibMath.min( plane1.y, plane2.y, plane3.y );
            float minFaceZ = (float)LibMath.min( plane1.z, plane2.z, plane3.z );
            float maxFaceX = (float)LibMath.max( plane1.x, plane2.x, plane3.x );
            float maxFaceY = (float)LibMath.max( plane1.y, plane2.y, plane3.y );
            float maxFaceZ = (float)LibMath.max( plane1.z, plane2.z, plane3.z );

            //check if the point of intersection lies in between the points of the face-triangle !
            if
            (
                    intersectionPoint.x < minFaceX
                ||  intersectionPoint.x > maxFaceX
                ||  intersectionPoint.y < minFaceY
                ||  intersectionPoint.y > maxFaceY
                ||  intersectionPoint.z < minFaceZ
                ||  intersectionPoint.z > maxFaceZ
            )
            {
                return null;
            }

            float minShotX = Math.min( ray1.x, ray2.x );
            float minShotY = Math.min( ray1.y, ray2.y );
            float minShotZ = Math.min( ray1.z, ray2.z );
            float maxShotX = Math.max( ray1.x, ray2.x );
            float maxShotY = Math.max( ray1.y, ray2.y );
            float maxShotZ = Math.max( ray1.z, ray2.z );

            //check if the point of intersection lies in between the points of the line !
            if
            (
                    intersectionPoint.x < minShotX
                ||  intersectionPoint.x > maxShotX
                ||  intersectionPoint.y < minShotY
                ||  intersectionPoint.y > maxShotY
                ||  intersectionPoint.z < minShotZ
                ||  intersectionPoint.z > maxShotZ
            )
            {
                return null;
            }
            return intersectionPoint;
        }

        /**
        *   Returns the point where a line crosses a plane  ..
        *   This would be a milestone ..
        */
        public static Point3d getLineFaceIntersectionD(Point3d ray1, Point3d ray2, Point3d plane1, Point3d plane2, Point3d plane3 )
        {
            Vector3d p1 = new Vector3d( plane1 );
            Vector3d p2 = new Vector3d( plane2 );
            Vector3d p3 = new Vector3d( plane3 );
            Vector3d p2minusp1 = new Vector3d( p2 );
            p2minusp1.sub( p1 );
            Vector3d p3minusp1 = new Vector3d( p3 );
            p3minusp1.sub( p1 );
            Vector3d normal = new Vector3d();
            normal.cross( p2minusp1, p3minusp1 );
            // The plane can be defined by p1, n + d = 0
            double d = -p1.dot( normal );
            Vector3d i1 = new Vector3d( ray1 );
            Vector3d direction = new Vector3d( ray1 );
            direction.sub( ray2 );
            double dot = direction.dot( normal );
            if ( dot == 0 ) return null;
            double t = ( -d - i1.dot( normal ) ) / ( dot );
            Vector3d intersection = new Vector3d( ray1 );
            Vector3d scaledDirection = new Vector3d( direction );
            scaledDirection.scale( t );
            intersection.add( scaledDirection );
            Point3d intersectionPoint = new Point3d( intersection );

            float minFaceX = (float)LibMath.min( plane1.x, plane2.x, plane3.x );
            float minFaceY = (float)LibMath.min( plane1.y, plane2.y, plane3.y );
            float minFaceZ = (float)LibMath.min( plane1.z, plane2.z, plane3.z );
            float maxFaceX = (float)LibMath.max( plane1.x, plane2.x, plane3.x );
            float maxFaceY = (float)LibMath.max( plane1.y, plane2.y, plane3.y );
            float maxFaceZ = (float)LibMath.max( plane1.z, plane2.z, plane3.z );

            //check if the point of intersection lies in between the points of the face-triangle !
            if
            (
                    intersectionPoint.x < minFaceX
                ||  intersectionPoint.x > maxFaceX
                ||  intersectionPoint.y < minFaceY
                ||  intersectionPoint.y > maxFaceY
                ||  intersectionPoint.z < minFaceZ
                ||  intersectionPoint.z > maxFaceZ
            )
            {
                return null;
            }

            float minShotX = (float)Math.min( ray1.x, ray2.x );
            float minShotY = (float)Math.min( ray1.y, ray2.y );
            float minShotZ = (float)Math.min( ray1.z, ray2.z );
            float maxShotX = (float)Math.max( ray1.x, ray2.x );
            float maxShotY = (float)Math.max( ray1.y, ray2.y );
            float maxShotZ = (float)Math.max( ray1.z, ray2.z );

            //check if the point of intersection lies in between the points of the line !
            if
            (
                    intersectionPoint.x < minShotX
                ||  intersectionPoint.x > maxShotX
                ||  intersectionPoint.y < minShotY
                ||  intersectionPoint.y > maxShotY
                ||  intersectionPoint.z < minShotZ
                ||  intersectionPoint.z > maxShotZ
            )
            {
                return null;
            }
            return intersectionPoint;
        }

        public static double distance(Point3d p1, Point3d p2 )
        {
            return p1.distance( p2 );
        }

        public static float getDistanceXYZ(Point3f l1, Point3f l2 )
        {
            float distance = (float)Math.sqrt( Math.pow( l2.x - l1.x, 2 ) + Math.pow( l2.y - l1.y, 2 ) + Math.pow( l2.z - l1.z, 2 ) );
            return distance;
        }

        public static float getDistanceXY(Point3f l1, Point3f l2 )
        {
            float distance = (float)Math.sqrt( Math.pow( l2.x - l1.x, 2 ) + Math.pow( l2.y - l1.y, 2 ) );
            return distance;
        }

        /***************************************************************************************************************
        *   Delivers the distant 3d point from another point.
        *
        *   Gosh! That was hard work!
        ***************************************************************************************************************/
        public static Point3d getDistantPoint(Point3d src, float distance3D, float rotX, float rotZ )
        {
            Point3d iEndPoint3d = null;

            //rotate x
            iEndPoint3d = new LibMatrix( 0.0f, rotX, 0.0f                      ).transformVertexD( new Point3d( src.x + distance3D, src.y, src.z ), src );

            //rotate z
            iEndPoint3d = new LibMatrix( 0.0f, 0.0f, 360.0f - ( rotZ + 90.0f ) ).transformVertexD( new Point3d( iEndPoint3d ), src );

            return iEndPoint3d;
        }
    }
