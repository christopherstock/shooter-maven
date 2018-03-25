
    package de.christopherstock.shooter.g3d;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import de.christopherstock.shooter.ShooterSetting;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.base.ShooterTexture.BotTex;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   Represents a cylinder.
    *******************************************************************************************************************/
    public class Cylinder implements LibGeomObject, LibCylinder
    {
        private     static  final   int                 VERTICAL_DEBUG_SLICES           = 4;

        private                     LibGameObject       iParentGameObject               = null;
        private                     LibVertex           iAnchor                         = null;
        private                     LibDebug            iDebug                          = null;

        /** Current center bottom point. */
        private                     float               iRadius                         = 0.0f;

        private                     int                 iEllipseSegments                = 0;

        /** Cylinder's height. */
        public                      float               iHeight                         = 0.0f;
        private                     boolean             iDebugDrawBotCircles            = false;
        private                     int                 iCollisionCheckingSteps         = 0;
        private                     float               iBottomCollisionToleranceZ      = 0.0f;
        private                     float               iMinBottomCollisionToleranceZ   = 0.0f;

        /** The target position for the cylinder to move to. Will be the next pos if no collisions appear. */
        private                     LibVertex           iTarget                         = null;

        private                     LibMaterial         iMaterial                       = null;

        public Cylinder( LibGameObject aParentGameObject, LibVertex aAnchor, float aRadius, float aHeight, int aCollisionCheckingSteps, LibDebug aDebug, boolean aDebugDrawBotCircles, float aBottomCollisionToleranceZ, float aMinBottomCollisionToleranceZ, int aEllipseSegments, LibMaterial aMaterial )
        {
            this.iParentGameObject = aParentGameObject;
            this.iAnchor = aAnchor;
            this.iRadius = aRadius;
            this.iHeight = aHeight;
            this.iCollisionCheckingSteps = aCollisionCheckingSteps;
            this.iDebug = aDebug;
            this.iDebugDrawBotCircles = aDebugDrawBotCircles;
            this.iBottomCollisionToleranceZ = aBottomCollisionToleranceZ;
            this.iMinBottomCollisionToleranceZ = aMinBottomCollisionToleranceZ;
            this.iEllipseSegments = aEllipseSegments;
            this.iMaterial = aMaterial;
        }

        public final void update( float pX, float pY, float pZ, float newHeight )
        {
            this.iAnchor = new LibVertex( pX, pY, pZ );

            //unchanged
            //radius = RADIUS;
            this.iHeight = newHeight;

        }

        public final void drawDebug()
        {
            if (this.iDebugDrawBotCircles)
            {
                //Debug.bot.out( "drawing bot .." );
                for ( int i = 0; i <= VERTICAL_DEBUG_SLICES; ++i )
                {
                    new LibFaceEllipseFloor(this.iDebug, null, LibColors.ERed, this.iAnchor.x, this.iAnchor.y, this.iAnchor.z + ( i * this.iHeight / VERTICAL_DEBUG_SLICES ), this.iRadius, this.iRadius, this.iEllipseSegments).draw();
                }
            }
        }

        public final Point2D.Float getCenterHorz()
        {
            return new Point2D.Float(this.iAnchor.x, this.iAnchor.y );
        }

        public final void setNewAnchor( LibVertex newAnk, boolean b, LibTransformationMode tm )
        {
            this.iAnchor.x = newAnk.x;
            this.iAnchor.y = newAnk.y;
            this.iAnchor.z = newAnk.z;
        }

        public final void translate( float x, float y, float z, LibTransformationMode tm )
        {
            this.iAnchor.translate( x, y, z );
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> ret = new Vector<LibHitPoint>();
            LibHitPoint hp = this.launchAShot( shot );
            if ( hp != null ) ret.addElement( hp );
            return ret;
        }

        /**
        *   Workaround - returning a Vector of HitPoints here will result in:
        *
        *   Exception in thread "Thread-3" java.lang.NullPointerException
        *   at java.util.Vector.addAll(Unknown Source)
        */
        private LibHitPoint launchAShot(LibShot shot )
        {
            //if ( shot.iOrigin == ShotOrigin.EEnemies ) ShooterDebug.bugfix.out( "launch shot: [" + "on cylinder" + "]" );

            //check horizontal intersection
            Point2D.Float   centerHorz              = new Point2D.Float(this.iAnchor.x, this.iAnchor.y );
            boolean         horizontalIntersection  = shot.iLineShotHorz.ptSegDist( centerHorz ) <= this.iRadius;
            if ( horizontalIntersection )
            {
                //if ( shot.iOrigin == ShotOrigin.EEnemies ) ShooterDebug.bugfix.out( " player hit" );

                this.iDebug.out( "bot horizontal hit !!" );

                //get horizontal intersecttion
                Point2D.Float intersectionPointHorz = LibMathGeometry.findLineCircleIntersection( shot.iLineShotHorz, this.getCircle() );
                this.iDebug.out( "interP: ["+intersectionPointHorz+",]" );

                //get angle from horz hitPoint to shot-src-point
                float           angleCenterToHitPointHorz = LibMath.getAngleCorrect( centerHorz, intersectionPointHorz );
                float           angleHitPointHorzToCenter = angleCenterToHitPointHorz - 180.0f;

                float           exactDistanceHorz       = (float)shot.iSrcPointHorz.distance( intersectionPointHorz );
                float           shotAngleHorz           = LibMath.getAngleCorrect( shot.iSrcPointHorz, new Point2D.Float( (float)centerHorz.getX(), (float)centerHorz.getY() ) );        //get angle between player and hit-point
                float           invertedShotAngleHorz   = 360.0f - ( shotAngleHorz - 180.0f  );              //get opposite direction of shot
                float           sliverAngleHorz         = shotAngleHorz /* - faceAngleHorz */ * 2;              //get Sliver angle

                this.iDebug.out( "exactDistanceHorz:     [" + exactDistanceHorz       + "]" );
                this.iDebug.out( "shotAngleHorz:         [" + shotAngleHorz           + "]" );
                this.iDebug.out( "invertedShotAngleHorz: [" + invertedShotAngleHorz   + "]" );
                this.iDebug.out( "SliverAngleHorz:       [" + sliverAngleHorz         + "]" );

                //calculate face's vertical collision line
                Line2D.Float collisionLineVert = new Line2D.Float( new Point2D.Float( exactDistanceHorz, this.iAnchor.z ), new Point2D.Float( exactDistanceHorz, this.iAnchor.z + this.iHeight) );
                this.iDebug.out( "bot's collision line vert is: [" + collisionLineVert + "]" );

                if ( !shot.iLineShotVert.intersectsLine( collisionLineVert ) )
                {
                    this.iDebug.out( "VERTICAL FACE MISSED!" );
                    return null;
                }

                //get intersection point for the vertical axis
                this.iDebug.out( "VERTICAL FACE HIT!" );
                Point2D.Float   intersectionPointVert   = LibMathGeometry.findLineSegmentIntersection( shot.iLineShotVert, collisionLineVert, this.iDebug);
                float           z                       = intersectionPointVert.y;
                this.iDebug.out( ">> INTERSECTION POINT VERT: " + intersectionPointVert );

                //get vertical values
                float exactDistanceVert = (float)shot.iSrcPointVert.distance( intersectionPointVert );       //get exact distance

                this.iDebug.out( ">> EXACT DISTANCE VERT: " + exactDistanceVert );
//                iDebug.out( ">> SHOT-ANGLE-VERT: " + shotAngleVert );
//                iDebug.out( ">> SLIVER-ANGLE-VERT: " + SliverAngleVert );

                //return hit point
                return new LibHitPoint
                (
                    shot,
                        this.iParentGameObject,
                        this.iMaterial.getBulletHoleTexture().getTexture(),
                    BotTex.ESkinBrown.getTexture(),
                        this.iMaterial.getSliverColors(),
                    new LibVertex( intersectionPointHorz.x, intersectionPointHorz.y, z ),
                    exactDistanceHorz,
                    shotAngleHorz,
                    invertedShotAngleHorz,
                    sliverAngleHorz,
                    angleHitPointHorzToCenter - 90.0f,
                    0.0f,                    //faceAngleVert
                    1.0f,
                    General.FADE_OUT_FACES_TOTAL_TICKS,
                    ShooterSetting.Performance.ELLIPSE_SEGMENTS
                );
            }

            //return null
            return null;
        }

        public final Ellipse2D.Float getCircle()
        {
            return new Ellipse2D.Float(this.iAnchor.x - this.iRadius, this.iAnchor.y - this.iRadius, this.iRadius * 2, this.iRadius * 2 );
        }

        public final float getRadius()
        {
            return this.iRadius;
        }

        public final float setRadius( float newRadius )
        {
            return this.iRadius = newRadius;
        }

        public final LibVertex getTarget()
        {
            return this.iTarget;
        }

        public final float getHeight()
        {
            return this.iHeight;
        }

        /************************************************************************************
        *   Checks vertical intersection.
        *
        *   @param  wallZ1  wall's first  z-point. May be lower or higher than the 2nd.
        *   @param  wallZ2  wall's second z-point. May be lower or higher than the 1st.
        *   @return         <code>true</code> if the player's vertical collision line
        *                   intersects the given wall points. Otherwise <code>false</code>.
        ************************************************************************************/
        public final boolean heightsIntersect( float wallZ1, float wallZ2, boolean useBottomToleranceZ, boolean invertBottomTolerance )
        {
            float lowestWallZ   = Math.min( wallZ1, wallZ2 );
            float highestWallZ  = Math.max( wallZ1, wallZ2 );

            //ShooterDebug.bugfix.out( "heights intersect " + wallZ1 + " " + wallZ2 + " " + iAnchor.z + "  "   );

            float mod = ( invertBottomTolerance ? -1.0f : 1.0f );
            boolean miss =
            (
                    this.iAnchor.z + ( useBottomToleranceZ ? mod * this.iBottomCollisionToleranceZ : mod * this.iMinBottomCollisionToleranceZ) > highestWallZ
                || this.iAnchor.z + this.iHeight < lowestWallZ
            );
            //ShooterDebug.bugfix.out( "intersect miss " + miss );

            //ShooterDebug.bugfix.out( " check: ["+anchor.z+"]["+lowestWallZ+"]["+highestWallZ+"]["+height+"] miss: ["+miss+"]" );

            //check if the player's head or toe point lies in between the wall line
            return !miss;
        }

        public void drawStandingCircle()
        {
            if (this.iDebugDrawBotCircles)
            {
                new LibFaceEllipseFloor(this.iDebug, null, LibColors.ERed, this.iAnchor.x, this.iAnchor.y, this.iAnchor.z + 0.001f, this.iRadius, this.iRadius, this.iEllipseSegments).draw();
            }
        }

        /***************************************************************************************************************
        *   Return the nearest available position z for the given point.
        *
        *   @param      floorsToCheck   All floors to check.
        *   @param      maxClimbDistZ   The maximum climbing distance z from the anchor's z-position.
        *   @return                     The nearest floor's z-position or <code>null</code> if non-existent.
        *   @deprecated                 May be useful one day.
        ***************************************************************************************************************/
        @Deprecated
        public final Float getNearestFloor( Vector<Float> floorsToCheck, float maxClimbDistZ )
        {
            if ( floorsToCheck.size() == 0 )
            {
                return null;
            }

            //get the nearest face-hit-point
            float nearestDistance = 0.0f;
            int   nearestIndex    = -1;
            for ( int i = 0; i < floorsToCheck.size(); ++i )
            {
                //assign 1st point - check if the distance is nearer
                float thisDistance = Math.abs(this.iAnchor.z - floorsToCheck.elementAt(i));
                if
                (
                        ( nearestIndex == -1 )
                    ||  ( thisDistance < nearestDistance )
                )
                {
                    //check if the point is under the player or, if lower, not too high to climb up to!
                    if
                    (
                            floorsToCheck.elementAt(i) < this.iAnchor.z
                        ||  thisDistance <= maxClimbDistZ
                    )
                    {
                        nearestIndex    = i;
                        nearestDistance = floorsToCheck.elementAt(i);

                    }
                }
            }

            //return the nearest distance
            return ( nearestIndex == -1 ? null : floorsToCheck.elementAt( nearestIndex ) );
        }

        public final Float getHighestOfCheckedFloor( Vector<Float> floorsToCheck )
        {
            if ( floorsToCheck.size() == 0 )
            {
                return null;
            }

            //get the highest face-hit-point
            Float highestZ = null;
            for ( int i = 0; i < floorsToCheck.size(); ++i )
            {
                //assign 1st point - check if the distance is nearer
                float thisDistance = Math.abs(this.iAnchor.z - floorsToCheck.elementAt(i));

                //only pick faces that..
                if
                (
                        //..are lower than the player
                        floorsToCheck.elementAt(i) < this.iAnchor.z
                        //..are not too high
                    ||  thisDistance <= this.iBottomCollisionToleranceZ
                )
                {
                    //assign if higher
                    if ( highestZ == null || floorsToCheck.elementAt(i) > highestZ)
                    {
                        highestZ = floorsToCheck.elementAt( i );
                    }
                }
            }

            //return the nearest distance
            return highestZ;
        }

        public final void setAnchorAsTargetPosition()
        {
            this.iTarget = new LibVertex(this.iAnchor.x, this.iAnchor.y, this.iAnchor.z );
        }

        public final void moveToTargetPosition( float newHeight, boolean disableWallCollisions, boolean disableBotCollisions )
        {
            //get distances from old to the new position
            float distX     = this.iTarget.x - this.iAnchor.x;
            float distY     = this.iTarget.y - this.iAnchor.y;
            float distZ     = this.iTarget.z - this.iAnchor.z;

            //save original anchor cause it will change in the loop!
            float startX   = this.iAnchor.x;
            float startY   = this.iAnchor.y;
            float startZ   = this.iAnchor.z;

            //check target-point collision stepwise from far to near ( to increase walking speed )
            for (float j = this.iCollisionCheckingSteps; j > 0; --j )
            {
                //try next step point..
                float distanceToCheckX = distX * j / this.iCollisionCheckingSteps;
                float distanceToCheckY = distY * j / this.iCollisionCheckingSteps;
                float distanceToCheckZ = distZ * j / this.iCollisionCheckingSteps;

                //try point on the line between current and target point
                this.iTarget.x = startX + distanceToCheckX;
                this.iTarget.y = startY + distanceToCheckY;
                this.iTarget.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;

                //try point on x-clipped axis
                this.iTarget.x = startX + distanceToCheckX;
                this.iTarget.y = startY;
                this.iTarget.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;

                //try point on y-clipped axis
                this.iTarget.x = startX;
                this.iTarget.y = startY + distanceToCheckY;
                this.iTarget.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;
            }

            //update player's collision circle to initial position
            this.update( startX, startY, startZ, newHeight );
        }

        /***************************************************************************************************************
         *   Check collision with the given cylinder.
         *
         *    @param circle  Circle to check horizontal collision with.
         *    @return        <code>true</code> if collision appears. Otherwise <code>false</code>.
         ***************************************************************************************************************/
         public final boolean checkCollision( float z )
         {
             //check collision of 2 circles ( easy  task.. )
             return
             (
                     z >= this.iAnchor.z
                 &&  z < (this.iAnchor.z + this.iHeight)
             );
         }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param circle  Circle to check horizontal collision with.
        *    @return        <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final boolean checkCollision( Ellipse2D.Float circle )
        {
            //check collision of 2 circles ( easy  task.. )
            Area circleHorz      = new Area( circle );
            Area ownCircleHorz   = new Area(this.getCircle() );
            circleHorz.intersect( ownCircleHorz );

            //check horizontal intersection
            return ( !circleHorz.isEmpty() );
        }

        public final boolean checkCollisionHorzLines( LibFaceTriangle face, boolean useBottomToleranceZ, boolean invertBottomTolerance  )
        {
            //check horizontal intersection
            Point2D.Double  cylinderPosHorz         = new Point2D.Double(this.iAnchor.x, this.iAnchor.y );
            boolean         horizontalIntersection1 = ( face.iCollisionLineHorz1.ptSegDist( cylinderPosHorz ) <= this.iRadius);
            boolean         horizontalIntersection2 = ( face.iCollisionLineHorz2.ptSegDist( cylinderPosHorz ) <= this.iRadius);
            boolean         horizontalIntersection3 = ( face.iCollisionLineHorz3.ptSegDist( cylinderPosHorz ) <= this.iRadius);

            if ( horizontalIntersection1 || horizontalIntersection2 || horizontalIntersection3 )
            {
                //check vertical intersection
                boolean verticalIntersection = this.heightsIntersect( face.iLowestZ, face.iHighestZ, useBottomToleranceZ, invertBottomTolerance   );
                if ( verticalIntersection )
                {
                    //ShooterDebug.bugfix.out("INTERSECT - return float with z [" + face.iHighestZ + "]");
                    return true;
                }
            }

            return false;
        }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param c   Cylinder to check collision with.
        *    @return    <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final boolean checkCollisionHorz( LibCylinder c )
        {
            //check horizontal and vertical intersection
            if (this.checkCollision( c.getCircle() ) && c.heightsIntersect(this.iAnchor.z, this.iAnchor.z + this.iHeight, false, false ) )
            {
                //no height information is required here ..
                return true;
            }

            return false;
        }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param c   Cylinder to check collision with.
        *    @return    <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final Vector<Float> checkCollisionVert( LibCylinder c, Object exclude )
        {
            Vector<Float> v = new Vector<Float>();

            //check horizontal and vertical intersection
            if (this.checkCollision( c.getCircle() ) && c.heightsIntersect(this.iAnchor.z, this.iAnchor.z + this.iHeight, false, false ) )
            {
                //no height information is required here ..
                v.add( new Float( 42 ) );
            }

            return v;
        }

        public final LibVertex getAnchor()
        {
            return this.iAnchor;
        }

        public final boolean tryTargetPoint( float newHeight, boolean disableWallCollisions, boolean disableBotCollisions )
        {
            //update player's collision circle with target location
            this.update(this.iTarget.x, this.iTarget.y, this.iTarget.z, newHeight );

            //check if target-point is collision free
            boolean collisionFree =
            (
                    ( disableWallCollisions || !Level.currentSection().checkCollisionOnWalls( this ) )
                &&  ( disableBotCollisions  || !Level.currentSection().checkCollisionOnBots(  this ) )
            );

            if ( collisionFree )
            {
                //assign this point as the target-point and leave this method!
                this.update(this.iTarget.x, this.iTarget.y, this.iTarget.z, newHeight );
            }

            return collisionFree;
        }

        public final Cylinder copy()
        {
            return new Cylinder
            (
                    this.iParentGameObject,
                    this.iAnchor.copy(),
                    this.iRadius,
                    this.iHeight,
                    this.iCollisionCheckingSteps,
                    this.iDebug,
                    this.iDebugDrawBotCircles,
                    this.iBottomCollisionToleranceZ,
                    this.iMinBottomCollisionToleranceZ,
                ShooterSetting.Performance.ELLIPSE_SEGMENTS,
                    this.iMaterial
            );
        }

        /***************************************************************************************************************
        *   Check horizontal collision of the face and the player's center point.
        *
        *   @param  x1      rect x1.
        *   @param  y1      rect y1.
        *   @param  x2      rect x2.
        *   @param  y2      rect y2.
        *   @return         <code>true</code> if the cylinder's center point is in the rect.
        *   @deprecated     replaced by j2se api.
        ***************************************************************************************************************/
/*
        @Deprecated
        public final boolean checkHorzCenterCollision( float x1, float y1, float x2, float y2 )
        {
            return
            (
                    ( x1 > x2 ? anchor.x >= x2 && anchor.x <= x1 : anchor.x >= x1 && anchor.x <= x2 )
                &&  ( y1 > y2 ? anchor.y >= y2 && anchor.y <= y1 : anchor.y >= y1 && anchor.y <= y2 )
            );
        }
*/
    }
