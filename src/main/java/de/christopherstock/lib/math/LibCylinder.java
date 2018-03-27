
    package de.christopherstock.lib.math;

    import  java.awt.geom.*;
    import  de.christopherstock.lib.g3d.face.*;

    /*******************************************************************************************************************
    *   Simple math-wrapper-class.
    *******************************************************************************************************************/
    public interface LibCylinder
    {
        boolean checkCollisionHorzLines( LibFaceTriangle face, boolean useBottomToleranceZ, boolean invertBottomTolerance );

        Point2D.Float getCenterHorz();

        Ellipse2D.Float getCircle();

        boolean heightsIntersect( float wallZ1, float wallZ2, boolean useBottomToleranceZ, boolean invertBottomTolerance );
    }
