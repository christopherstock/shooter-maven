/*  $Id: LibMath.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.math;

    import  java.awt.geom.*;
    import  de.christopherstock.lib.g3d.face.*;

    /**************************************************************************************
    *   Simple math-wrapper-class.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface LibCylinder
    {
        public abstract boolean checkCollisionHorzLines( LibFaceTriangle face, boolean useBottomToleranceZ, boolean invertBottomTolerance  );

        public abstract Point2D.Float getCenterHorz();

        public abstract Ellipse2D.Float getCircle();

        public abstract boolean heightsIntersect( float wallZ1, float wallZ2, boolean useBottomToleranceZ, boolean invertBottomTolerance );
    }
