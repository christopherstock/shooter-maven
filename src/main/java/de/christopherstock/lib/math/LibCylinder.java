
    package de.christopherstock.lib.math;

    import  java.awt.geom.*;
    import  de.christopherstock.lib.g3d.face.*;

    /*******************************************************************************************************************
    *   Simple math-wrapper-class.
    *******************************************************************************************************************/
    public abstract interface LibCylinder
    {
        public abstract boolean checkCollisionHorzLines( LibFaceTriangle face, boolean useBottomToleranceZ, boolean invertBottomTolerance  );

        public abstract Point2D.Float getCenterHorz();

        public abstract Ellipse2D.Float getCircle();

        public abstract boolean heightsIntersect( float wallZ1, float wallZ2, boolean useBottomToleranceZ, boolean invertBottomTolerance );
    }
