
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
        private                     LibDebug                debug                       = null;
        public                      LibShot                 shot                        = null;
        public                      LibGameObject           carrier                     = null;
        public                      LibVertex               vertex                      = null;
        public                      LibGLTextureMetaData    bulletHoleTexture           = null;
        public                      LibGLTextureMetaData    wallTexture                 = null;
        public                      float                   horzShotAngle               = 0.0f;
        public                      float                   horzInvertedShotAngle       = 0.0f;
        public                      float                   horzFaceAngle               = 0.0f;
        public                      float                   vertFaceAngle               = 0.0f;
        public                      float                   damageMultiplier            = 0.0f;
        private                     int                     fadeOutTicks                = 0;
        private                     LibColors[]             sliverColors                = null;
        private                     float                   horzDistance                = 0.0f;
        private                     float                   horzSliverAngle             = 0.0f;
        private                     float                   vertShotAngle               = 0.0f;
        private                     float                   vertSliverAngle             = 0.0f;
        private                     int                     ellipseSegments             = 0;

        public LibHitPoint
        (
            LibShot       shot,

            LibGameObject carrier,
            LibGLTextureMetaData bulletHoleTexture,
            LibGLTextureMetaData wallTexture,
            LibColors[]   sliverColors,
            LibVertex     vertex,

            float         horzDistance,
            float         horzShotAngle,
            float         horzInvertedShotAngle,
            float         horzSliverAngle,
            float         horzFaceAngle,
            float         vertFaceAngle,
            float         damageMultiplier,

            int           fadeOutTicks,
            int           ellipseSegments
        )
        {
            this.shot                  = shot;

            this.carrier               = carrier;
            this.bulletHoleTexture     = bulletHoleTexture;
            this.wallTexture           = wallTexture;
            this.sliverColors          = sliverColors;

            this.vertex                = vertex;

            this.horzShotAngle         = horzShotAngle;
            this.horzInvertedShotAngle = horzInvertedShotAngle;
            this.horzSliverAngle       = horzSliverAngle;
            this.horzFaceAngle         = horzFaceAngle;
            this.horzDistance          = horzDistance;

            this.vertShotAngle         = LibMath.getAngleCorrect( this.shot.srcPointVert, new Point2D.Float( (float) this.shot.srcPointHorz.distance( new Point2D.Float(this.vertex.x, this.vertex.y ) ), this.vertex.z ) );
            this.vertFaceAngle         = vertFaceAngle;
            this.damageMultiplier      = damageMultiplier;

          //iVertDistance           = aVertDistance;
          //iVertInvertedShotAngle  = 360.0f - ( vertShotAngle - 180.0f  );

            this.vertSliverAngle       = LibMath.normalizeAngle( 180.0f - this.vertShotAngle);

            this.fadeOutTicks          = fadeOutTicks;
            this.ellipseSegments       = ellipseSegments;
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
                //ShooterDebug.bugfix.out( "hitpoint: " + n.horzDistance );
                //ShooterDebug.bugfix.out( " penetrable: " + n.wallTexture.getMaterial().isPenetrable() );

                //add hitpoint
                ret.add( n );

                //break if not penetrable or material is not specified
                if ( n.wallTexture == null || n.wallTexture.getMaterial() == null || !n.wallTexture.getMaterial().isPenetrable() )
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
                    ||  ( nearestDistance > hitPoints.elementAt( i ).horzDistance)
                )
                {
                    nearestIndex    = i;
                    nearestDistance = hitPoints.elementAt( i ).horzDistance;
                }
            }

            //debug.out( "exact nearest hitPoint-distance to player: [" + nearestDistance + "]" );

            //return nearest hit-point
            return hitPoints.elementAt( nearestIndex );
        }

        public final void launchWallSliver(LibParticleQuantity sliverQuantity, float angleMod, int lifetime, FXSize size, FXGravity gravity, Object exclude, LibFloorStack floorStack )
        {
            float SIZE = 0.01f;
/*
            boolean drawRedSliverLine = false;
            if ( drawRedSliverLine )
            {
                //draw sliver line with own calculations
                final   float   MAX_DRAW_SHOT_LINE_RANGE = 0.5f;
                for ( float distance = 0.0f; distance < MAX_DRAW_SHOT_LINE_RANGE; distance += 0.1f )
                {
                    LibFXManager.launchStaticPoint
                    (
                        this.debug,
                        new LibVertex
                        (
                                this.vertex.x - LibMath.sinDeg(this.horzSliverAngle) * distance,
                                this.vertex.y - LibMath.cosDeg(this.horzSliverAngle) * distance,
                                this.vertex.z - LibMath.sinDeg(this.vertSliverAngle - 90.0f ) * distance
                        ),
                        LibColors.ERed,
                        SIZE,
                        lifetime,
                        this.fadeOutTicks
                    );
                }
            }
*/
            //ignore non-climbable walls
            if ( exclude instanceof LibClimbable )
            {
                if ( ( (LibClimbable)exclude ).isClimbable() )
                {
                    exclude = null;
                }
            }

            //random translation occurs HERE!

            //get sliver vertex ( translate a bit to shot source in order to distance it from walls )
            LibVertex sliverVertex = new LibVertex
            (
                    this.vertex.x - ( LibMath.sinDeg(this.horzInvertedShotAngle) * 0.01f ),
                    this.vertex.y - ( LibMath.cosDeg(this.horzInvertedShotAngle) * 0.01f ),
                    this.vertex.z // + ( (float)LibMath.getRandom( 0, 100 ) * 0.001f )
            );

            //launch sliver fx on this hole
            float baseZ     = Float.MIN_VALUE;
            Float baseZF    = floorStack.getHighestFloor( null, sliverVertex, 0.05f, SIZE, 0, this.debug, false, SIZE, SIZE, this.ellipseSegments, exclude );
            if ( baseZF != null )
            {
                baseZ = baseZF;
            }
            baseZ += SIZE;

            //LibFXManager.launchStaticPoint( sliverVertex, LibColors.EGreenLight, 0.05f, 250 );

            LibFXManager.launchSliver
            (
                this.debug,
                sliverVertex,
                this.sliverColors,
                this.horzSliverAngle,
                sliverQuantity,
                angleMod,
                lifetime,
                size,
                gravity,
                baseZ,
                this.fadeOutTicks
            );
        }

        public final int compareTo( LibHitPoint otherHP )
        {
            return Float.compare( this.horzDistance, otherHP.horzDistance );
/*
            if (this.horzDistance == otherHP.horzDistance) return 0;
            if (this.horzDistance > otherHP.horzDistance) return 1;
            return -1;
*/
        }
    }
