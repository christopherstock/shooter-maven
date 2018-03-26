
    package de.christopherstock.lib.game;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.FXGravity;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Represents a point of shot collision.
    *******************************************************************************************************************/
    public class LibHitPoint implements Comparable<LibHitPoint>
    {
        private LibDebug                iDebug                      = null;
        public                      LibShot                 iShot                       = null;
        public                      LibGameObject           iCarrier                    = null;
        public                      LibVertex               iVertex                     = null;
        public                      LibGLTexture            iBulletHoleTexture          = null;
        public                      LibGLTexture            iWallTexture                = null;
        public                      float                   iHorzShotAngle              = 0.0f;
        public                      float                   iHorzInvertedShotAngle      = 0.0f;
        public                      float                   iHorzFaceAngle              = 0.0f;
        public                      float                   iVertFaceAngle              = 0.0f;
        public                      float                   iDamageMultiplier           = 0.0f;
        private                     int                     iFadeOutTicks               = 0;
        private                     LibColors[]             iSliverColors               = null;
        private                     float                   iHorzDistance               = 0.0f;
        private                     float                   iHorzSliverAngle            = 0.0f;
        private float                   iVertShotAngle              = 0.0f;
        private                     float                   iVertSliverAngle            = 0.0f;
        private                     int                     iEllipseSegments            = 0;

        public LibHitPoint
        (
            LibShot                 aShot,

            LibGameObject           aCarrier,
            LibGLTexture            aBulletHoleTexture,
            LibGLTexture            aWallTexture,
            LibColors[]             aSliverColors,
            LibVertex               aVertex,

            float                   aHorzDistance,
            float                   aHorzShotAngle,
            float                   aHorzInvertedShotAngle,
            float                   aHorzSliverAngle,
            float                   aHorzFaceAngle,
            float                   aVertFaceAngle,
            float                   aDamageMultiplier,

            int                     aFadeOutTicks,
            int                     aEllipseSegments
        )
        {
            this.iShot = aShot;

            this.iCarrier = aCarrier;
            this.iBulletHoleTexture = aBulletHoleTexture;
            this.iWallTexture = aWallTexture;
            this.iSliverColors = aSliverColors;

            this.iVertex = aVertex;

            this.iHorzShotAngle = aHorzShotAngle;
            this.iHorzInvertedShotAngle = aHorzInvertedShotAngle;
            this.iHorzSliverAngle = aHorzSliverAngle;
            this.iHorzFaceAngle = aHorzFaceAngle;
            this.iHorzDistance = aHorzDistance;

            this.iVertShotAngle = LibMath.getAngleCorrect(this.iShot.iSrcPointVert, new Point2D.Float( (float) this.iShot.iSrcPointHorz.distance( new Point2D.Float(this.iVertex.x, this.iVertex.y ) ), this.iVertex.z ) );
            this.iVertFaceAngle = aVertFaceAngle;
            this.iDamageMultiplier = aDamageMultiplier;

          //iVertDistance           = aVertDistance;
          //iVertInvertedShotAngle  = 360.0f - ( iVertShotAngle - 180.0f  );

            this.iVertSliverAngle = LibMath.normalizeAngle( 180.0f - this.iVertShotAngle);

            this.iFadeOutTicks = aFadeOutTicks;
            this.iEllipseSegments = aEllipseSegments;
        }

        public static LibHitPoint[] getAffectedHitPoints(Vector<LibHitPoint> hitPoints )
        {
            LibHitPoint[] nearToFar = hitPoints.toArray( new LibHitPoint[] {} );
            Arrays.sort( nearToFar );

            //ShooterDebug.bugfix.out( "---------------------------------" );

            //browse all hitpoints - from the nearest to the farest
            Vector<LibHitPoint> ret = new Vector<LibHitPoint>();
            for ( LibHitPoint n : nearToFar )
            {
                //ShooterDebug.bugfix.out( "hitpoint: " + n.iHorzDistance );
                //ShooterDebug.bugfix.out( " penetrable: " + n.iWallTexture.getMaterial().isPenetrable() );

                //add hitpoint
                ret.add( n );

                //break if not penetrable or material is not specified
                if ( n.iWallTexture == null || n.iWallTexture.getMaterial() == null || !n.iWallTexture.getMaterial().isPenetrable() )
                {
                    break;
                }
            }

            return ret.toArray( new LibHitPoint[] {} );
        }

        @Deprecated
        public static LibHitPoint getNearestHitPoint(Vector<LibHitPoint> hitPoints )
        {
            //return null if no hit point was found
            if ( hitPoints.size() == 0 )
            {
                //debug.out( "no hit points found!" );
                return null;
            }

            //get the nearest hit-point
            float nearestDistance = 0.0f;
            int   nearestIndex    = -1;
            for ( int i = 0; i < hitPoints.size(); ++i )
            {
                //assign 1st point
                if
                (
                        ( nearestIndex == -1 )
                    ||  ( nearestDistance > hitPoints.elementAt( i ).iHorzDistance )
                )
                {
                    nearestIndex    = i;
                    nearestDistance = hitPoints.elementAt( i ).iHorzDistance;
                }
            }

            //debug.out( "exact nearest hitPoint-distance to player: [" + nearestDistance + "]" );

            //return nearest hit-point
            return hitPoints.elementAt( nearestIndex );
        }

        public final void launchWallSliver(LibParticleQuantity sliverQuantity, float angleMod, int lifetime, FXSize size, FXGravity gravity, Object exclude, LibFloorStack floorStack )
        {
            float SIZE = 0.01f;
            boolean drawRedSliverLine = false;
            if ( drawRedSliverLine )
            {
                //draw sliver line with own calculations
                final   float   MAX_DRAW_SHOT_LINE_RANGE = 0.5f;
                for ( float distance = 0.0f; distance < MAX_DRAW_SHOT_LINE_RANGE; distance += 0.1f )
                {
                    LibFXManager.launchStaticPoint
                    (
                            this.iDebug,
                        new LibVertex
                        (
                                this.iVertex.x - LibMath.sinDeg(this.iHorzSliverAngle) * distance,
                                this.iVertex.y - LibMath.cosDeg(this.iHorzSliverAngle) * distance,
                                this.iVertex.z - LibMath.sinDeg(this.iVertSliverAngle - 90.0f ) * distance
                        ),
                        LibColors.ERed,
                        SIZE,
                        lifetime,
                            this.iFadeOutTicks
                    );
                }
            }

            //ignore non-climbable walls
            if ( exclude instanceof LibClimbable )
            {
                if ( ( (LibClimbable)exclude ).isClimbable() )
                {
                    exclude = null;
                }
            }

            //get sliver vertex ( translate a bit to shot source in order to distance it from walls )
            LibVertex sliverVertex = new LibVertex
            (
                    this.iVertex.x - ( LibMath.sinDeg(this.iHorzInvertedShotAngle) * 0.1f ),
                    this.iVertex.y - ( LibMath.cosDeg(this.iHorzInvertedShotAngle) * 0.1f ),
                    this.iVertex.z
            );

            //launch sliver fx on this hole
            float baseZ     = Float.MIN_VALUE;
            Float baseZF    = floorStack.getHighestFloor( null, sliverVertex, 0.05f, SIZE, 0, this.iDebug, false, SIZE, SIZE, this.iEllipseSegments, exclude );
            if ( baseZF != null )
            {
                baseZ = baseZF;
            }
            baseZ += SIZE;

            //LibFXManager.launchStaticPoint( sliverVertex, LibColors.EGreenLight, 0.05f, 250 );

            LibFXManager.launchSliver
            (
                    this.iDebug,
                sliverVertex,
                    this.iSliverColors,
                    this.iHorzSliverAngle,
                sliverQuantity,
                angleMod,
                lifetime,
                size,
                gravity,
                baseZ,
                    this.iFadeOutTicks
            );
        }

        public final int compareTo( LibHitPoint otherHP )
        {
            if (this.iHorzDistance == otherHP.iHorzDistance ) return 0;
            if (this.iHorzDistance > otherHP.iHorzDistance  ) return 1;
            return -1;
        }
    }
